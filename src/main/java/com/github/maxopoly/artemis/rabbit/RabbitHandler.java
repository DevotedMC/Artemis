package com.github.maxopoly.artemis.rabbit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.bukkit.scheduler.BukkitRunnable;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.servers.ZeusServer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitHandler {

	private ConnectionFactory connectionFactory;
	private String incomingQueue;
	private String outgoingQueue;
	private Logger logger;
	private Connection conn;
	private Channel incomingChannel;
	private Channel outgoingChannel;
	private ArtemisRabbitInputHandler inputProcessor;
	private ZeusServer zeus;

	public RabbitHandler(ConnectionFactory connFac, String incomingQueue, String outgoingQueue, TransactionIdManager transactionIdManager, Logger logger, ZeusServer zeus, ArtemisRabbitInputHandler rabbitProcessor) {
		this.connectionFactory = connFac;
		this.incomingQueue = incomingQueue;
		this.outgoingQueue = outgoingQueue;
		this.logger = logger;
		inputProcessor = rabbitProcessor;
		this.zeus = zeus;
	}

	public boolean setup() {
		InteractiveRabbitCommand.setSendingLambda((s,p) -> sendMessage(p));	
		try {
			conn = connectionFactory.newConnection();
			incomingChannel = conn.createChannel();
			outgoingChannel = conn.createChannel();
			incomingChannel.queueDeclare(incomingQueue, false, false, false, null);
			outgoingChannel.queueDeclare(outgoingQueue, false, false, false, null);
			return true;
		} catch (IOException | TimeoutException e) {
			logger.severe("Failed to setup rabbit connection: " + e.toString());
			return false;
		}
	}

	public void beginAsyncListen() {
		new BukkitRunnable() {

			@Override
			public void run() {
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					try {
						String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
						if (ArtemisPlugin.getInstance().getConfigManager().debugRabbit()) {
							logger.info("[X] R_IN: " + message);
						}
						inputProcessor.handle(zeus, message);
					} catch (Exception e) {
						logger.severe("Exception in rabbit handling: " + e.toString());
						e.printStackTrace();
					}
				};
				try {
					incomingChannel.basicConsume(incomingQueue, true, deliverCallback, consumerTag -> {
					});
				} catch (IOException e) {
					logger.severe("Error in rabbit listener: " + e.toString());
				}
			}
		}.runTask(ArtemisPlugin.getInstance());
	}

	public void shutdown() {
		try {
			incomingChannel.close();
			outgoingChannel.close();
			conn.close();
		} catch (IOException | TimeoutException e) {
			logger.severe("Failed to close rabbit connection: " + e);
		}
	}

	public boolean sendMessage(RabbitMessage message) {
		try {
			String strMsg = message.getJSON().toString();
			if (ArtemisPlugin.getInstance().getConfigManager().debugRabbit()) {
				logger.info("[X] R_OUT: " + strMsg);
			}
			outgoingChannel.basicPublish("", outgoingQueue, null, strMsg.getBytes(StandardCharsets.UTF_8));			
			return true;
		} catch (IOException e) {
			logger.severe("Failed to send rabbit message: " + e);
			return false;
		}
	}
}

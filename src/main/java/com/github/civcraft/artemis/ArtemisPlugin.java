package com.github.civcraft.artemis;

import org.bukkit.Bukkit;

import com.github.civcraft.artemis.rabbit.RabbitHandler;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.servers.ZeusServer;

import vg.civcraft.mc.civmodcore.ACivMod;

public final class ArtemisPlugin extends ACivMod {

	private static ArtemisPlugin instance;

	public static ArtemisPlugin getInstance() {
		return instance;
	}

	private RabbitHandler rabbitHandler;
	private ArtemisConfigManager configManager;
	private TransactionIdManager transactionIdManager;
	private GlobalPlayerManager globalPlayerManager;

	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		this.configManager = new ArtemisConfigManager(this);
		if (!configManager.parse()) {
			Bukkit.shutdown();
			return;
		}
		this.globalPlayerManager = new GlobalPlayerManager();
		this.transactionIdManager = new TransactionIdManager(configManager.getOwnIdentifier());
		this.rabbitHandler = new RabbitHandler(configManager.getConnectionFactory(),
				configManager.getIncomingRabbitQueue(), configManager.getOutgoingRabbitQueue(), transactionIdManager,
				getLogger(), new ZeusServer());
		if (!rabbitHandler.setup()) {
			Bukkit.shutdown();
			return;
		}

		rabbitHandler.beginAsyncListen();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		rabbitHandler.shutdown();
	}
	
	public GlobalPlayerManager getGlobalPlayerManager() {
		return globalPlayerManager;
	}

	public RabbitHandler getRabbitHandler() {
		return rabbitHandler;
	}

	public TransactionIdManager getTransactionIdManager() {
		return transactionIdManager;
	}

}

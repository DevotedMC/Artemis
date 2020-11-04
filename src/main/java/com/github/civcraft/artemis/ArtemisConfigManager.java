package com.github.civcraft.artemis;

import org.bukkit.configuration.ConfigurationSection;

import com.github.civcraft.zeus.model.ConnectedMapState;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.ZeusRabbitGateway;
import com.rabbitmq.client.ConnectionFactory;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class ArtemisConfigManager extends CoreConfigManager {
	
	private ConfigurationSection config;
	private ConnectionFactory connectionFactory;
	private String ownIdentifier;
	private ConnectedMapState connectedMapState;
	private boolean debugRabbit;

	public ArtemisConfigManager(ACivMod plugin) {
		super(plugin);
	}
	
	private ConnectionFactory parseRabbitConfig() {
		ConnectionFactory connFac = new ConnectionFactory();
		String user = config.getString("rabbitmq.user", null);
		if (user != null) {
			connFac.setUsername(user);
		}
		String password = config.getString("rabbitmq.password", null);
		if (password != null) {
			connFac.setPassword(password);
		}
		String host = config.getString("rabbitmq.host", null);
		if (host != null) {
			connFac.setHost(host);
		}
		int port = config.getInt("rabbitmq.port", -1);
		if (port != -1) {
			connFac.setPort(port);
		}
		debugRabbit = config.getBoolean("rabbitmq.debug", true);
		return connFac;
	}
	
	private boolean parseMapPosition(ConfigurationSection config) {
		if (config == null) {
			return false;
		}
		int xSize = config.getInt("x_size");
		int zSize = config.getInt("z_size");
		String world = config.getString("world");
		int lowerX = config.getInt("lower_x_bound");
		int lowerZ = config.getInt("lower_z_bound");
		boolean randomSpawnTarget = config.getBoolean("random_spawn", true);
		ZeusLocation corner = new ZeusLocation(world, lowerX, 0, lowerZ);
		connectedMapState = new ConnectedMapState(null, corner, xSize, zSize, randomSpawnTarget);
		return true;
	}
	
	protected boolean parseInternal(ConfigurationSection config) {
		this.config = config;
		if (!parseMapPosition(config.getConfigurationSection("position"))) {
			logger.severe("No position configured in config");
			return false;
		}
		ownIdentifier = config.getString("own_identifier");
		connectionFactory = parseRabbitConfig();
		return true;
	}
	
	public String getWorldName() {
		return connectedMapState.getWorld();
	}
	
	public ConnectedMapState getConnectedMapState() {
		return connectedMapState;
	}
	
	public String getOwnIdentifier() {
		return ownIdentifier;
	}
	
	public boolean debugRabbit() {
		return debugRabbit;
	}
	
	public String getOutgoingRabbitQueue() {
		return ZeusRabbitGateway.getChannelToZeus(ownIdentifier);
	}
	
	public String getIncomingRabbitQueue() {
		return ZeusRabbitGateway.getChannelFromZeus(ownIdentifier);
	}
	
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

}

package com.github.maxopoly.artemis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import com.github.maxopoly.zeus.model.ConnectedMapState;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.ZeusRabbitGateway;
import com.rabbitmq.client.ConnectionFactory;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class ArtemisConfigManager extends CoreConfigManager {

	private ConfigurationSection config;
	private ConnectionFactory connectionFactory;
	private String ownIdentifier;
	private ConnectedMapState connectedMapState;
	private boolean debugRabbit;
	private List<Material> randomSpawnBlacklist;
	private int minRandomSpawnY;
	private int maxRandomSpawnY;
	private int randomSpawnAirNeeded;
	private int randomSpawnsToCache;
	private boolean firstSpawnTarget;

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
		int xSize = Integer.parseInt(config.getString("x_size")); // intentionally to allow quoted values, because
																	// getInt() is broken
		int zSize = Integer.parseInt(config.getString("z_size"));
		String world = config.getString("world", "world");
		if (Bukkit.getWorld(world) == null) {
			logger.severe("No world with the name " + world + " exists");
			return false;
		}
		int lowerX = Integer.parseInt(config.getString("lower_x_bound"));
		int lowerZ = Integer.parseInt(config.getString("lower_z_bound"));
		
		ZeusLocation corner = new ZeusLocation(world, lowerX, 0, lowerZ);
		connectedMapState = new ConnectedMapState(null, corner, xSize, zSize, firstSpawnTarget);
		return true;
	}

	public List<Material> getBlacklistedRandomspawnMaterials() {
		return randomSpawnBlacklist;
	}

	public int getMaxRandomSpawnY() {
		return maxRandomSpawnY;
	}
	
	public int getRandomSpawnAirNeeded() {
		return randomSpawnAirNeeded;
	}

	public int getMinRandomSpawnY() {
		return minRandomSpawnY;
	}
	
	public int getRandomSpawnsToCache() {
		return randomSpawnsToCache;
	}

	protected boolean parseInternal(ConfigurationSection config) {
		this.config = config;
		firstSpawnTarget = config.getBoolean("random_spawn.first_spawn", true);
		randomSpawnBlacklist = parseMaterialList(config, "random_spawn.block_blacklist");
		if (randomSpawnBlacklist == null) {
			randomSpawnBlacklist = new ArrayList<>();
		}
		minRandomSpawnY = config.getInt("random_spawn.min_y", 1);
		maxRandomSpawnY = config.getInt("random_spawn.max_y", 255);
		randomSpawnAirNeeded = config.getInt("random_spawn.air_needed", 6);
		randomSpawnAirNeeded = config.getInt("random_spawn.spawns_cached", 10);
		if (maxRandomSpawnY < minRandomSpawnY) {
			logger.severe("Maximum random spawn y is below minimum");
			return false;
		}
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

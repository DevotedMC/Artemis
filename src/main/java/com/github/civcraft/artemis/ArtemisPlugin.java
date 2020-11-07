package com.github.civcraft.artemis;

import org.bukkit.Bukkit;

import com.github.civcraft.artemis.listeners.PlayerDataListener;
import com.github.civcraft.artemis.listeners.ShardBorderListener;
import com.github.civcraft.artemis.nbt.CustomWorldNBTStorage;
import com.github.civcraft.artemis.rabbit.RabbitHandler;
import com.github.civcraft.artemis.rabbit.outgoing.ArtemisStartup;
import com.github.civcraft.zeus.model.PlayerData;
import com.github.civcraft.zeus.model.PlayerManager;
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
	private PlayerManager<PlayerData> globalPlayerTracker;
	private TransitManager transitManager;
	private ArtemisPlayerDataCache playerDataCache;
	private ShardBorderManager borderManager;
	private ZeusServer zeus;
	
	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		this.configManager = new ArtemisConfigManager(this);
		if (!configManager.parse()) {
			Bukkit.shutdown();
			return;
		}
		this.zeus = new ZeusServer();
		this.borderManager = new ShardBorderManager(configManager.getConnectedMapState());
		this.playerDataCache = new ArtemisPlayerDataCache();
		this.transitManager = new TransitManager();
		this.globalPlayerTracker = new PlayerManager<>();
		this.transactionIdManager = new TransactionIdManager(configManager.getOwnIdentifier(), getLogger()::info);
		this.rabbitHandler = new RabbitHandler(configManager.getConnectionFactory(),
				configManager.getIncomingRabbitQueue(), configManager.getOutgoingRabbitQueue(), transactionIdManager,
				getLogger(), zeus);
		if (!rabbitHandler.setup()) {
			Bukkit.shutdown();
			return;
		}
		CustomWorldNBTStorage.insertCustomNBTHandler();
		Bukkit.getPluginManager().registerEvents(new PlayerDataListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShardBorderListener(borderManager), this);
		rabbitHandler.beginAsyncListen();
		rabbitHandler.sendMessage(new ArtemisStartup(transactionIdManager.pullNewTicket()));
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, transactionIdManager::updateTimeouts, 1, 1);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		rabbitHandler.shutdown();
	}
	
	public PlayerManager<PlayerData> getPlayerDataManager() {
		return globalPlayerTracker;
	}
	
	public ArtemisConfigManager getConfigManager() {
		return configManager;
	}
	
	public ArtemisPlayerDataCache getPlayerDataCache() {
		return playerDataCache;
	}

	public ZeusServer getZeus() {
		return zeus;
	}
	
	public TransitManager getTransitManager() {
		return transitManager;
	}
	
	public RabbitHandler getRabbitHandler() {
		return rabbitHandler;
	}

	public TransactionIdManager getTransactionIdManager() {
		return transactionIdManager;
	}

}

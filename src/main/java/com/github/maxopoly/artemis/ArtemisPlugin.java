package com.github.maxopoly.artemis;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.maxopoly.artemis.listeners.PlayerDataListener;
import com.github.maxopoly.artemis.listeners.ShardBorderListener;
import com.github.maxopoly.artemis.nbt.CustomWorldNBTStorage;
import com.github.maxopoly.artemis.rabbit.ArtemisRabbitInputHandler;
import com.github.maxopoly.artemis.rabbit.RabbitHandler;
import com.github.maxopoly.artemis.rabbit.outgoing.ArtemisStartup;
import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.servers.ZeusServer;

import vg.civcraft.mc.civmodcore.ACivMod;

public final class ArtemisPlugin extends ACivMod {

	private static ArtemisPlugin instance;

	public static ArtemisPlugin getInstance() {
		return instance;
	}

	private RabbitHandler rabbitHandler;
	private ArtemisRabbitInputHandler rabbitInputHandler;
	private ArtemisConfigManager configManager;
	private TransactionIdManager transactionIdManager;
	private ArtemisPlayerManager globalPlayerTracker;
	private TransitManager transitManager;
	private ArtemisPlayerDataCache playerDataCache;
	private ShardBorderManager borderManager;
	private ZeusServer zeus;
	private CustomWorldNBTStorage customNBTHandler;
	private ScheduledExecutorService transactionIdCleanup; // can't be a bukkit thread, because those are disable before
															// onDisable and we
	// still need it there

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
		this.transactionIdManager = new TransactionIdManager(configManager.getOwnIdentifier(), getLogger()::info);

		this.transitManager = new TransitManager(transactionIdManager);
		this.globalPlayerTracker = new ArtemisPlayerManager();
		this.rabbitInputHandler = new ArtemisRabbitInputHandler(getLogger(), transactionIdManager);
		this.rabbitHandler = new RabbitHandler(configManager.getConnectionFactory(),
				configManager.getIncomingRabbitQueue(), configManager.getOutgoingRabbitQueue(), transactionIdManager,
				getLogger(), zeus, rabbitInputHandler);
		if (!rabbitHandler.setup()) {
			Bukkit.shutdown();
			return;
		}
		customNBTHandler = CustomWorldNBTStorage.insertCustomNBTHandler();
		Bukkit.getPluginManager().registerEvents(new PlayerDataListener(), this);
		Bukkit.getPluginManager().registerEvents(new ShardBorderListener(borderManager, transitManager), this);
		rabbitHandler.beginAsyncListen();
		rabbitHandler.sendMessage(new ArtemisStartup(transactionIdManager.pullNewTicket()));
		transactionIdCleanup = Executors.newSingleThreadScheduledExecutor();
		transactionIdCleanup.scheduleAtFixedRate(transactionIdManager::updateTimeouts, 0, 50, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onDisable() {
		customNBTHandler.shutdown();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer("Server is shutting down");
		}
		while (transactionIdManager.hasActiveSessions()) {
			getLogger().info("Waiting for closure of open rabbit sessions");
			transactionIdManager.printActiveSessions(getLogger()::info);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		rabbitHandler.shutdown();
		try {
			transactionIdCleanup.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDisable();
	}

	public ArtemisPlayerManager getPlayerDataManager() {
		return globalPlayerTracker;
	}

	public ArtemisRabbitInputHandler getRabbitInputHandler() {
		return rabbitInputHandler;
	}

	public ArtemisConfigManager getConfigManager() {
		return configManager;
	}

	public ArtemisPlayerDataCache getPlayerDataCache() {
		return playerDataCache;
	}

	public CustomWorldNBTStorage getCustomNBTStorage() {
		return customNBTHandler;
	}

	public ZeusServer getZeus() {
		return zeus;
	}

	public ShardBorderManager getBorderManager() {
		return borderManager;
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

package com.github.maxopoly.artemis.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.nbt.CustomWorldNBTStorage;
import com.github.maxopoly.artemis.rabbit.RabbitHandler;
import com.github.maxopoly.artemis.rabbit.outgoing.RequestPlayerData;
import com.github.maxopoly.artemis.rabbit.session.ArtemisPlayerDataTransferSession;

public class PlayerDataListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preLoginDataFetch(AsyncPlayerPreLoginEvent event) {
		RabbitHandler rabbit = ArtemisPlugin.getInstance().getRabbitHandler();
		String ticket = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
		ArtemisPlayerDataTransferSession session = new ArtemisPlayerDataTransferSession(
				ArtemisPlugin.getInstance().getZeus(), ticket, event.getUniqueId());
		ArtemisPlugin.getInstance().getTransactionIdManager().putSession(session);
		rabbit.sendMessage(new RequestPlayerData(ticket, event.getUniqueId()));
		event.kickMessage(Component.empty());
		ArtemisPlugin.getInstance().getPlayerDataCache().putWaiting(event.getUniqueId(), event);
		synchronized (event) {
			while (event.kickMessage().equals(Component.empty())) {
				try {
					event.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (event.kickMessage().equals(Component.text("D"))) {
			event.disallow(Result.KICK_OTHER, "Internal data error, try waiting a few seconds and then login again. "
					+ "If that does not help, consult an admin");
			return;
		}
		if (!event.kickMessage().equals(Component.text("A"))) {
			event.disallow(Result.KICK_OTHER, "Special internal error, tell an admin about this");
			return;
		}
		CustomWorldNBTStorage.addActivePlayer(event.getUniqueId());
		event.allow();
		// if login doesn't complete, remove them again, give them full 10 seconds to
		// time out
		Bukkit.getScheduler().runTaskLater(ArtemisPlugin.getInstance(), () -> {
			if (Bukkit.getPlayer(event.getUniqueId()) == null) {
				CustomWorldNBTStorage.removeActivePlayer(event.getUniqueId());
			}
		}, 20 * 10L);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void playerQuit(PlayerQuitEvent event) {
		CustomWorldNBTStorage.removeActivePlayer(event.getPlayer().getUniqueId());
		Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), () -> ArtemisPlugin.getInstance().getTransitManager()
				.removeFromTransit(event.getPlayer().getUniqueId()));
	}

}

package com.github.civcraft.artemis.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.rabbit.RabbitHandler;
import com.github.civcraft.artemis.rabbit.outgoing.RequestPlayerData;

public class PlayerDataListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void preLoginDataFetch(AsyncPlayerPreLoginEvent event) {
		RabbitHandler rabbit = ArtemisPlugin.getInstance().getRabbitHandler();
		String ticket = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
		rabbit.sendMessage(new RequestPlayerData(ticket, event.getUniqueId()));
		event.setKickMessage(null);
		synchronized (event) {
			while (event.getKickMessage() == null) {
				try {
					event.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (event.getKickMessage().equals("D")) {
			event.disallow(Result.KICK_OTHER, "Internal data error, tell an admin about this");
			return;
		}
		if (!event.getKickMessage().equals("A")) {
			event.disallow(Result.KICK_OTHER, "Special internal error, tell an admin about this");
			return;
		}
		event.allow();
	}

}

package com.github.maxopoly.artemis.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.maxopoly.artemis.ArtemisPlugin;

public class RespawnListener implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(ArtemisPlugin.getInstance().getRandomSpawnHandler()
				.getRandomSpawnLocation(event.getPlayer().getUniqueId()));
	}

}

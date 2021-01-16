package com.github.maxopoly.artemis.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.maxopoly.artemis.ArtemisPlugin;

public class RespawnListener implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!ArtemisPlugin.getInstance().getConfigManager().getOverrideAnchorSpawn() && event.isAnchorSpawn()) {
			return;
		}
		if (!ArtemisPlugin.getInstance().getConfigManager().getOverrideBedSpawn() && event.isBedSpawn()) {
			return;
		}
		event.setRespawnLocation(ArtemisPlugin.getInstance().getRandomSpawnHandler()
				.getRandomSpawnLocation(event.getPlayer().getUniqueId()));
	}
	
	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			return;
		}
		player.teleport(ArtemisPlugin.getInstance().getRandomSpawnHandler().getRandomSpawnLocation(player.getUniqueId()));
	}

}

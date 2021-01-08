package com.github.maxopoly.artemis;

import java.util.UUID;
import java.util.logging.Level;

import com.github.maxopoly.zeus.model.PlayerData;
import com.github.maxopoly.zeus.model.PlayerManager;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerName;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerUUID;
import com.github.maxopoly.zeus.util.SimpleFuture;

public class ArtemisPlayerManager extends PlayerManager<PlayerData> {

	/**
	 * Gets the UUID of the player with the given name, case-insensitive. Initially
	 * checks the local cache of known players for the name and returns any result
	 * found there. All online player are guaranteed to be in this cache.
	 * 
	 * If the player is not found in the cache, Zeus is queried for the players UUID
	 * (blocking)
	 * 
	 * @param name Name of the player to look up
	 * @return UUID of the given player name or null if no such player is known
	 */
	public UUID getUUID(String name) {
		name = name.toLowerCase();
		if (cacheNameToUUID.containsKey(name)) {
			// double lookup because cache may contain null values intentionally
			return cacheNameToUUID.get(name);
		}
		// get from Zeus
		SimpleFuture<UUID> fetched = new SimpleFuture<>();
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RequestPlayerUUID(ArtemisPlugin.getInstance().getTransactionIdManager(),
						ArtemisPlugin.getInstance().getZeus(), name, fetched::put));
		UUID uuid;
		try {
			uuid = fetched.get();
		} catch (InterruptedException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to fetch uuid", e);
			return null;
		}
		// not the other way around because no guarantee regarding upper/lower case is
		// made from this
		cacheNameToUUID.put(name, uuid);
		return uuid;
	}

	/**
	 * Gets the name of the player with the given uuid. Initially checks the local
	 * cache of known players for the UUID and returns any result found there. All
	 * online player are guaranteed to be in this cache.
	 * 
	 * If the player is not found in the cache, Zeus is queried for the players name
	 * (blocking)
	 * 
	 * @param uuid UUID of the player to look up
	 * @return Name of the given player or null if no such player is known
	 */
	public String getName(UUID playerUUID) {
		if (cacheUUIDToName.containsKey(playerUUID)) {
			// double lookup because cache may contain null values intentionally
			return cacheUUIDToName.get(playerUUID);
		}
		// get from Zeus
		SimpleFuture<String> fetched = new SimpleFuture<>();
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new RequestPlayerName(ArtemisPlugin.getInstance().getTransactionIdManager(),
						ArtemisPlugin.getInstance().getZeus(), playerUUID, fetched::put));
		String name;
		try {
			name = fetched.get();
		} catch (InterruptedException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to fetch name", e);
			return null;
		}
		cacheNameToUUID.put(name.toLowerCase(), playerUUID);
		cacheUUIDToName.put(playerUUID, name);
		return name;
	}

}

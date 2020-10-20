package com.github.civcraft.artemis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalPlayerManager {
	
	private Map<String, GlobalPlayerData> playersByName;
	private Map<UUID, GlobalPlayerData> playersByUUID;
	
	public GlobalPlayerManager() {
		this.playersByName = new ConcurrentHashMap<>();
		this.playersByUUID = new ConcurrentHashMap<>();
	}
	
	public GlobalPlayerData getLoggedInPlayerByName(String name) {
		return playersByName.get(name.toLowerCase());
	}
	
	public GlobalPlayerData getLoggedInPlayerByUUID(UUID uuid) {
		return playersByUUID.get(uuid);
	}
	
	public void addPlayer(GlobalPlayerData data) {
		playersByName.put(data.getName().toLowerCase(), data);
		playersByUUID.put(data.getUUID(), data);
	}
	
	public void removePlayer(GlobalPlayerData data) {
		playersByName.remove(data.getName().toLowerCase());
		playersByUUID.remove(data.getUUID());
	}

}

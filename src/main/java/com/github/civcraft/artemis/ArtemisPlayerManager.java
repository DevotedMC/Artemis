package com.github.civcraft.artemis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.civcraft.zeus.model.PlayerData;
import com.github.civcraft.zeus.model.PlayerManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ArtemisPlayerManager extends PlayerManager<PlayerData> {
	
	private BiMap<String, UUID> offlineLookasideCache;
	
	public ArtemisPlayerManager() {
		super();
		this.offlineLookasideCache = HashBiMap.create(); 
	}
	
	public void addPlayer(PlayerData data) {
		super.addPlayer(data);
		offlineLookasideCache.put(data.getName().toLowerCase(), data.getUUID());
	}
	
	public UUID asyncLookupUUID(String name) {
		name = name.toLowerCase();
		if (offlineLookasideCache.containsKey(name)) {
			//double lookup because cache may contain null values
			return offlineLookasideCache.get(name);
		}
		
	}

}

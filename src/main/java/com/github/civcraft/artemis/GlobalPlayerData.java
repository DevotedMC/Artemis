package com.github.civcraft.artemis;

import java.util.UUID;

import com.google.common.base.Preconditions;

public class GlobalPlayerData {
	
	private UUID uuid;
	private String name;
	
	public GlobalPlayerData(UUID uuid, String name) {
		Preconditions.checkNotNull(uuid);
		Preconditions.checkNotNull(name);
		this.uuid = uuid;
		this.name = name;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}

}

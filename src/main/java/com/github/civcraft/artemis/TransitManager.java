package com.github.civcraft.artemis;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TransitManager {
	
	private Set<UUID> transitPlayers;
	
	public TransitManager() {
		this.transitPlayers = new HashSet<>();
	}
	
	public synchronized void putInTransit(UUID player) {
		transitPlayers.add(player);
	}
	
	public synchronized void removeFromTransit(UUID player) {
		transitPlayers.remove(player);
	}
	
	public synchronized boolean isInTransit(UUID player) {
		return transitPlayers.contains(player);
	}

}

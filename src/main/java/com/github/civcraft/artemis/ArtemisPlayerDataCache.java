package com.github.civcraft.artemis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;

public class ArtemisPlayerDataCache {
	private Map<UUID, AsyncPlayerPreLoginEvent> eventLocks;
	private Map<UUID, PlayerDataTransferSession> sessions;

	public ArtemisPlayerDataCache() {
		this.eventLocks = new HashMap<>();
		this.sessions = new HashMap<>();

	}

	public PlayerDataTransferSession consumeSession(UUID player) {
		return sessions.get(player);
	}

	public synchronized void putWaiting(UUID uuid, AsyncPlayerPreLoginEvent event) {
		eventLocks.put(uuid, event);
	}

	public synchronized void completeSession(PlayerDataTransferSession session) {
		UUID uuid = session.getPlayer();
		AsyncPlayerPreLoginEvent event = eventLocks.get(uuid);
		if (event == null) {
			return; // player left while we were waiting for his data
			// TODO tell Zeus that we dont want the data?S
		}
		// we abuse the kick message to deliver the result
		if (session.getData() == null) {
			event.setKickMessage("D"); //deny
		} else {
			sessions.put(uuid, session);
			event.setKickMessage("A"); //accept
		}
		// resume asyncloginevent
		event.notifyAll();
	}

}

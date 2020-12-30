package com.github.maxopoly.artemis;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.github.maxopoly.artemis.nbt.CustomWorldNBTStorage;
import com.github.maxopoly.artemis.rabbit.outgoing.PlayerInitTransfer;
import com.github.maxopoly.artemis.rabbit.session.OutgoingPlayerTransferSession;
import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.model.ZeusLocation;

public class TransitManager {
	
	private Set<UUID> transitPlayers;
	private TransactionIdManager transIdManager;
	
	public TransitManager(TransactionIdManager transIdManager) {
		this.transIdManager = transIdManager;
		this.transitPlayers = new HashSet<>();
	}
	
	public synchronized boolean putInTransit(UUID player) {
		if (isInTransit(player)) {
			return false;
		}
		transitPlayers.add(player);
		return true;
	}
	
	public synchronized void removeFromTransit(UUID player) {
		transitPlayers.remove(player);
	}
	
	public synchronized boolean isInTransit(UUID player) {
		return transitPlayers.contains(player);
	}
	
	public void sendTo(UUID player, ZeusLocation location) {
		String transId = transIdManager.pullNewTicket();
		OutgoingPlayerTransferSession session = new OutgoingPlayerTransferSession(ArtemisPlugin.getInstance().getZeus(),
				transId, player);
		transIdManager.putSession(session);
		CustomWorldNBTStorage.removeActivePlayer(player);
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new PlayerInitTransfer(transId, player, location));
	}

}

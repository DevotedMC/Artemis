package com.github.civcraft.artemis.rabbit.session;

import java.util.UUID;

import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class OutgoingPlayerTransferSession extends PlayerSpecificPacketSession {

	public OutgoingPlayerTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}

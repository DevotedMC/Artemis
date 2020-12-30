package com.github.maxopoly.artemis.rabbit.session;

import java.util.UUID;

import com.github.maxopoly.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class OutgoingPlayerTransferSession extends PlayerSpecificPacketSession {

	public OutgoingPlayerTransferSession(ConnectedServer source, String transactionID, UUID player) {
		super(source, transactionID, player);
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}

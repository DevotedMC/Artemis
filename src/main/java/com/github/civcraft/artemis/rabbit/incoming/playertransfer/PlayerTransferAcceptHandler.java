package com.github.civcraft.artemis.rabbit.incoming.playertransfer;

import org.json.JSONObject;

import com.github.civcraft.artemis.rabbit.session.OutgoingPlayerTransferSession;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerTransferAcceptHandler extends InteractiveRabbitCommand<OutgoingPlayerTransferSession> {

	@Override
	public boolean handleRequest(OutgoingPlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		// TODO Is there anything we want to do with this?
		return false;
	}

	@Override
	public String getIdentifier() {
		return "accept_transfer";
	}

	@Override
	public boolean createSession() {
		return false;
	}
	
}


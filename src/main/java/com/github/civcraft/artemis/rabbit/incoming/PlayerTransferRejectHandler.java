package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.PacketSession;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.incoming.RabbitRequest;
import com.github.civcraft.zeus.rabbit.sessions.PlayerTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerTransferRejectHandler extends InteractiveRabbitCommand<PlayerTransferSession> {

	@Override
	public boolean handleRequest(PlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		// TODO Possibly queue handling to send a message to the player regarding what went wrong?
		return false;
	}

	@Override
	public String getIdentifier() {
		return "reject_transfer";
	}

	@Override
	public boolean createSession() {
		return false;
	}
	
}

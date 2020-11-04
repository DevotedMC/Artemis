package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerDataRequest;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class HandleRejectPlayerDataRequest extends InteractiveRabbitCommand<PlayerDataTransferSession> {

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		ArtemisPlugin.getInstance().getPlayerDataCache().completeSession(connState);
		return false;
	}

	@Override
	public String getIdentifier() {
		return RejectPlayerDataRequest.ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}
	

}

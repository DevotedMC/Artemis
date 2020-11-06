package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.artemis.rabbit.session.ALocationRequestSession;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerLocation;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ReceivePlayerLocation extends InteractiveRabbitCommand<ALocationRequestSession> {

	@Override
	public boolean handleRequest(ALocationRequestSession connState, ConnectedServer sendingServer, JSONObject data) {
		ZeusLocation location = ZeusLocation.parseLocation(data);
		connState.handleReply(location);
		return false;
	}

	@Override
	public String getIdentifier() {
		return SendPlayerLocation.ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}

}

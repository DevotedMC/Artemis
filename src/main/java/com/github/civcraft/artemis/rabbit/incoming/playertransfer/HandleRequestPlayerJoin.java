package com.github.civcraft.artemis.rabbit.incoming.playertransfer;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.artemis.rabbit.outgoing.AcceptPlayerJoinRequest;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerRequest;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class HandleRequestPlayerJoin extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		ZeusLocation futureLocation = ZeusLocation.parseLocation(data);
		//TODO event or something? always accept for now
		sendReply(sendingServer, new AcceptPlayerJoinRequest(ticket));
	}

	@Override
	public String getIdentifier() {
		return SendPlayerRequest.ID;
	}

}

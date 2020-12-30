package com.github.maxopoly.artemis.rabbit.incoming.playertransfer;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.outgoing.AcceptPlayerJoinRequest;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerRequest;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class HandleRequestPlayerJoin extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		ZeusLocation futureLocation = ZeusLocation.parseLocation(data);
		ArtemisPlugin.getInstance().getPlayerDataCache().putTargetLocation(player, futureLocation);
		//TODO event or something? always accept for now
		sendReply(sendingServer, new AcceptPlayerJoinRequest(ticket));
	}

	@Override
	public String getIdentifier() {
		return SendPlayerRequest.ID;
	}

}

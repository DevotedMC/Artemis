package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.rabbit.outgoing.ArtemisStartup;
import com.github.civcraft.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.ResetConnectionPacket;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class ZeusResetConnection extends GenericInteractiveRabbitCommand {

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		// new ticket, because Zeus will flood this and having all servers reply with
		// the same ticket makes no sense
		sendReply(sendingServer,
				new ArtemisStartup(ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket()));
	}

	@Override
	public String getIdentifier() {
		return ResetConnectionPacket.ID;
	}

}

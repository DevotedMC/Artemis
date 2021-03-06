package com.github.maxopoly.artemis.rabbit.incoming.playertransfer;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.session.ArtemisPlayerDataTransferSession;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class HandleReplyReceivePlayerData extends InteractiveRabbitCommand<ArtemisPlayerDataTransferSession> {

	@Override
	public String getIdentifier() {
		return SendPlayerData.REPLY_ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}

	@Override
	public boolean handleRequest(ArtemisPlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		boolean accepted = data.getBoolean("accepted");
		if (!accepted) {
			//fallback for recovery
			ArtemisPlugin.getInstance().getCustomNBTStorage().vanillaSave(connState.getEntityHuman());
		}
		return false;
	}

}

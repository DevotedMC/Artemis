package com.github.civcraft.artemis.rabbit.incoming.playertransfer;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.rabbit.outgoing.RequestPlayerData;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerDataRequest;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class HandleRejectPlayerDataRequest extends InteractiveRabbitCommand<PlayerDataTransferSession> {

	public static final int MAXIMUM_RETRIES = 10;
	
	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		int requestsSoFar = connState.getRequestAttempts();
		if (requestsSoFar > MAXIMUM_RETRIES) {
			ArtemisPlugin.getInstance().getPlayerDataCache().completeSession(connState);
			return false;
		}
		else {
			requestsSoFar++;
			connState.incrementRequestAttempts();
			try {
				Thread.sleep(requestsSoFar * 50L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sendReply(sendingServer, new RequestPlayerData(connState.getTransactionID(), connState.getPlayer()));
			return true;
		}
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

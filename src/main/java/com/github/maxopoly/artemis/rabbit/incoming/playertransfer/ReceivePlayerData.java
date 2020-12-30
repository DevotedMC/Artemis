package com.github.maxopoly.artemis.rabbit.incoming.playertransfer;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.outgoing.PlayerDataConfirm;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import com.github.maxopoly.zeus.util.Base64Encoder;

public class ReceivePlayerData extends InteractiveRabbitCommand<PlayerDataTransferSession> {

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		byte[] playerData;
		if (!data.optBoolean("new_player", false)) {
			String serializedData = data.getString("data");
			playerData = Base64Encoder.decode(serializedData);
		} else {
			playerData = new byte[0];
		}
		ZeusLocation location;
		if (data.has("loc")) {
			location = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		} else {
			location = null;
		}
		connState.setData(playerData);
		connState.setLocation(location);
		System.out.println("Post initial " + location.toString());
		ArtemisPlugin.getInstance().getPlayerDataCache().completeSession(connState);
		sendReply(sendingServer, new PlayerDataConfirm(connState.getTransactionID()));
		return false;
	}

	@Override
	public String getIdentifier() {
		return SendPlayerData.ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}

}

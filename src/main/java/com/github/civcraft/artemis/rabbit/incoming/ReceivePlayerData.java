package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.github.civcraft.zeus.util.Base64Encoder;

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
		connState.setData(playerData, location);
		ArtemisPlugin.getInstance().getPlayerDataCache().completeSession(connState);
		return true;
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

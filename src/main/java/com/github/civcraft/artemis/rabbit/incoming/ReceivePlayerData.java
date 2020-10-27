package com.github.civcraft.artemis.rabbit.incoming;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.github.civcraft.zeus.util.Base64Encoder;

public class ReceivePlayerData extends InteractiveRabbitCommand<PlayerDataTransferSession> {

	@Override
	public boolean handleRequest(PlayerDataTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		String serializedData = data.getString("data");
		byte [] raw = Base64Encoder.decode(serializedData);
		ZeusLocation location = ZeusLocation.parseLocation(data.getJSONObject("loc"));
		connState.setData(raw, location);
		ArtemisPlugin.getInstance().getPlayerDataCache().completeSession(connState);
		return true;
	}

	@Override
	public String getIdentifier() {
		return "receive_player_data";
	}

	@Override
	public boolean createSession() {
		return false;
	}
	
}

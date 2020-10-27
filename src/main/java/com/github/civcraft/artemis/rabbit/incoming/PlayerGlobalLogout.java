package com.github.civcraft.artemis.rabbit.incoming;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.model.PlayerData;
import com.github.civcraft.zeus.model.PlayerManager;
import com.github.civcraft.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerGlobalLogout extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		JSONArray players = data.getJSONArray("players");
		PlayerManager<PlayerData> playerMan = ArtemisPlugin.getInstance().getPlayerDataManager();
		for(Object obj : players) {
			JSONObject json = (JSONObject) obj;
			String name = json.getString("name");
			UUID uuid = UUID.fromString(json.getString("uuid"));
			playerMan.removePlayer(new PlayerData(uuid, name));
		}}

	@Override
	public String getIdentifier() {
		return "player_network_leave";
	}
	

}

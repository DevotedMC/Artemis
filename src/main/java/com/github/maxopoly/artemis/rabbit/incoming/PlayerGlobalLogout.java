package com.github.maxopoly.artemis.rabbit.incoming;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.model.PlayerData;
import com.github.maxopoly.zeus.model.PlayerManager;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerGlobalLogout extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		JSONArray players = data.getJSONArray("players");
		PlayerManager<PlayerData> playerMan = ArtemisPlugin.getInstance().getPlayerDataManager();
		for(Object obj : players) {
			JSONObject json = (JSONObject) obj;
			String name = json.getString("name");
			UUID uuid = UUID.fromString(json.getString("uuid"));
			playerMan.removeOnlinePlayerData(new PlayerData(uuid, name));
		}}

	@Override
	public String getIdentifier() {
		return "player_network_leave";
	}
	

}

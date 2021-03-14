package com.github.maxopoly.artemis.rabbit.incoming;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.model.PlayerData;
import com.github.maxopoly.zeus.model.PlayerManager;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.servers.ConnectedServer;
import java.util.UUID;
import org.json.JSONObject;

public class PlayerGlobalLogin extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		JSONObject player = data.getJSONObject("player");
		PlayerManager<PlayerData> playerMan = ArtemisPlugin.getInstance().getPlayerDataManager();
		if (player != null) {
			ArtemisPlugin.getInstance().getLogger().info("Got a player's data: " + player.toString());
			String name = player.getString("name");
			UUID uuid = UUID.fromString(player.getString("uuid"));
			playerMan.addPlayer(new PlayerData(uuid, name));
		} else {
			ArtemisPlugin.getInstance().getLogger().severe("Should have gotten a player's data but did not: " + data.toString());
		}
	}

	@Override
	public String getIdentifier() {
		return "player_network_join";
	}

}

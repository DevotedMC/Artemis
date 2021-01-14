package com.github.maxopoly.artemis.rabbit.incoming.statetracking;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.rabbit.incoming.StaticRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.CachePlayerName;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class CachePlayerNameHandler extends StaticRabbitCommand {

	@Override
	public void handleRequest(ConnectedServer sendingServer, JSONObject data) {
		UUID uuid = UUID.fromString(data.getString("uuid"));
		String name = data.getString("name");
		ArtemisPlugin.getInstance().getPlayerDataManager().addToNameUUIDCache(uuid, name);
	}

	@Override
	public String getIdentifier() {
		return CachePlayerName.ID;
	}

}

package com.github.maxopoly.artemis.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisConfigManager;
import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.ArtemisStartupHandler;

public class ArtemisStartup extends RabbitMessage {

	public ArtemisStartup(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		ArtemisConfigManager config = ArtemisPlugin.getInstance().getConfigManager();
		json.put("server", config.getOwnIdentifier());
		JSONObject pos = new JSONObject();
		config.getConnectedMapState().getUpperLeftCorner().writeToJson(pos);
		pos.put("x_size", config.getConnectedMapState().getXSize());
		pos.put("z_size", config.getConnectedMapState().getZSize());
		pos.put("random_spawn", config.getConnectedMapState().isFirstSpawnTarget());
		json.put("pos", pos);
	}

	@Override
	public String getIdentifier() {
		return ArtemisStartupHandler.ID;
	}

}

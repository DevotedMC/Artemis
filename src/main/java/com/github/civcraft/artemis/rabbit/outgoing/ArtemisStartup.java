package com.github.civcraft.artemis.rabbit.outgoing;

import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisConfigManager;
import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.google.gson.JsonObject;

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
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

}

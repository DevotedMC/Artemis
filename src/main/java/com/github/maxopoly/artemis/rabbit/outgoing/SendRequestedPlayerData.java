package com.github.maxopoly.artemis.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerDataFallbackReceive;
import com.github.maxopoly.zeus.util.Base64Encoder;

/**
 * Sent when Zeus explicitly requested player data from the local file cache
 *
 */
public class SendRequestedPlayerData extends RabbitMessage {

	private byte [] data;
	private ZeusLocation location;
	
	public SendRequestedPlayerData(String transactionID, byte [] data, ZeusLocation loc) {
		super(transactionID);
		
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("available", data != null);
		if (data != null) {
			json.put("data", Base64Encoder.encode(data));
			JSONObject obj = new JSONObject();
			location.writeToJson(obj);
			json.put("loc", obj);
		}
	}

	@Override
	public String getIdentifier() {
		return PlayerDataFallbackReceive.ID;
	}

}

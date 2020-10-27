package com.github.civcraft.artemis.rabbit.outgoing;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class RequestPlayerData extends RabbitMessage {

	private UUID player;
	
	public RequestPlayerData(String transactionID, UUID player) {
		super(transactionID);
		this.player = player;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player);
	}

	@Override
	public String getIdentifier() {
		return "get_player_data";
	}

}

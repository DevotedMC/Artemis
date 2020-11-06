package com.github.civcraft.artemis.rabbit.outgoing;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;
import com.github.civcraft.zeus.rabbit.incoming.artemis.PlayerLocationRequest;

public class RequestPlayerLocation extends RabbitMessage {
	
	private UUID player;

	public RequestPlayerLocation(String transactionID, UUID player) {
		super(transactionID);
		this.player = player;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player);
	}

	@Override
	public String getIdentifier() {
		return PlayerLocationRequest.ID;
	}

}

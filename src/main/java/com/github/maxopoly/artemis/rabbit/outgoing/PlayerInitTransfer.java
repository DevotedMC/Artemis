package com.github.maxopoly.artemis.rabbit.outgoing;

import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.google.common.base.Preconditions;

public class PlayerInitTransfer extends RabbitMessage {
	
	private UUID player;
	private ZeusLocation location;

	public PlayerInitTransfer(String transactionID, UUID player, ZeusLocation location) {
		super(transactionID);
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(location);
		this.player = player;
		this.location = location;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player);
        JSONObject obj = new JSONObject();
		location.writeToJson(obj);
		json.put("loc", obj);
	}

	@Override
	public String getIdentifier() {
		return "init_transfer";
	}

}

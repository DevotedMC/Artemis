package com.github.civcraft.artemis.rabbit.outgoing;

import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.rabbit.RabbitMessage;

public class PlayerInitTransfer extends RabbitMessage {
	
	private UUID player;
	private int x,y,z;

	public PlayerInitTransfer(String transactionID, UUID player, int x, int y, int z) {
		super(transactionID);
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	protected void enrichJson(JSONObject json) {
		json.put("player", player);
        JSONObject obj = new JSONObject();
		obj.put("x", x);
		obj.put("y", y);
		obj.put("z", z);
		json.put("loc", obj);
	}

	@Override
	public String getIdentifier() {
		return "init_transfer";
	}

}

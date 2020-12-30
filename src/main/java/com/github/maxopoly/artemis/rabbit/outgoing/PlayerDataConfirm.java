package com.github.maxopoly.artemis.rabbit.outgoing;

import org.json.JSONObject;

import com.github.maxopoly.zeus.rabbit.RabbitMessage;
import com.github.maxopoly.zeus.rabbit.incoming.artemis.PlayerDataTargetConfirm;

public class PlayerDataConfirm extends RabbitMessage {

	public PlayerDataConfirm(String transactionID) {
		super(transactionID);
	}

	@Override
	protected void enrichJson(JSONObject json) {
		//empty
	}

	@Override
	public String getIdentifier() {
		return PlayerDataTargetConfirm.ID;
	}

}

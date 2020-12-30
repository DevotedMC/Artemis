package com.github.maxopoly.artemis.rabbit.session;

import java.util.UUID;

import com.github.maxopoly.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import net.minecraft.server.v1_16_R1.EntityHuman;

public class ArtemisPlayerDataTransferSession extends PlayerDataTransferSession {

	private EntityHuman entityHuman;
	private int requestAttempt;
	
	public ArtemisPlayerDataTransferSession(ConnectedServer source, String transactionID, EntityHuman entityHuman) {
		super(source, transactionID, entityHuman.getUniqueID());
		this.entityHuman = entityHuman;
		this.requestAttempt = 0;
	}
	
	public ArtemisPlayerDataTransferSession(ConnectedServer source, String transactionID, UUID uuid) {
		super(source, transactionID, uuid);
		this.entityHuman = null; //is offline
	}
	
	public EntityHuman getEntityHuman() {
		return entityHuman;
	}
	
	public int getRequestAttempts() {
		return requestAttempt;
	}

	public void incrementRequestAttempts() {
		requestAttempt++;
	}

}

package com.github.civcraft.artemis.rabbit.session;

import java.util.UUID;
import java.util.function.Consumer;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.PlayerSpecificPacketSession;
import com.github.civcraft.zeus.servers.ConnectedServer;
import com.google.common.base.Preconditions;

public class ALocationRequestSession extends PlayerSpecificPacketSession {
	
	private Consumer<ZeusLocation> callback;
	
	public ALocationRequestSession(ConnectedServer source, String transactionID, UUID player, Consumer<ZeusLocation> callback) {
		super(source, transactionID, player);
		Preconditions.checkNotNull(callback);
		this.callback = callback;
	}
	
	public void handleReply(ZeusLocation location) {
		callback.accept(location);
	}

	@Override
	public void handleTimeout() {
		// TODO Auto-generated method stub
		
	}

}

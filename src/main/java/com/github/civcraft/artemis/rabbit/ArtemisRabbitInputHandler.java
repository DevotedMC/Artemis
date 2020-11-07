package com.github.civcraft.artemis.rabbit;

import java.util.logging.Logger;

import com.github.civcraft.artemis.rabbit.incoming.HandleRejectPlayerDataRequest;
import com.github.civcraft.artemis.rabbit.incoming.HandleRequestPlayerJoin;
import com.github.civcraft.artemis.rabbit.incoming.PlayerGlobalLogin;
import com.github.civcraft.artemis.rabbit.incoming.PlayerGlobalLogout;
import com.github.civcraft.artemis.rabbit.incoming.PlayerTransferAcceptHandler;
import com.github.civcraft.artemis.rabbit.incoming.PlayerTransferRejectHandler;
import com.github.civcraft.artemis.rabbit.incoming.ReceivePlayerData;
import com.github.civcraft.artemis.rabbit.incoming.ReceivePlayerLocation;
import com.github.civcraft.artemis.rabbit.incoming.ZeusResetConnection;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.abstr.AbstractRabbitInputHandler;

public class ArtemisRabbitInputHandler extends AbstractRabbitInputHandler {
	
	private Logger logger;
	
	public ArtemisRabbitInputHandler(Logger logger, TransactionIdManager transactionIdManager) {
		super(transactionIdManager);
		this.logger = logger;
	}

	@Override
	protected void registerCommands() {
		registerCommand(new HandleRejectPlayerDataRequest());
		registerCommand(new HandleRequestPlayerJoin());
		registerCommand(new PlayerGlobalLogin());
		registerCommand(new PlayerGlobalLogout());
		registerCommand(new PlayerTransferRejectHandler());
		registerCommand(new PlayerTransferAcceptHandler());
		registerCommand(new ReceivePlayerData());
		registerCommand(new ReceivePlayerLocation());
		registerCommand(new ZeusResetConnection());
	}

	@Override
	protected void logError(String msg) {
		logger.severe(msg);
	}

}

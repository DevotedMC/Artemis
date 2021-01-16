package com.github.maxopoly.artemis.rabbit;

import java.util.logging.Logger;

import com.github.maxopoly.artemis.rabbit.incoming.PlayerGlobalLogin;
import com.github.maxopoly.artemis.rabbit.incoming.PlayerGlobalLogout;
import com.github.maxopoly.artemis.rabbit.incoming.ReceivePlayerLocation;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.HandleRejectPlayerDataRequest;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.HandleReplyReceivePlayerData;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.HandleRequestPlayerJoin;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.HandleZeusRequestPlayerData;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.PlayerTransferAcceptHandler;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.PlayerTransferRejectHandler;
import com.github.maxopoly.artemis.rabbit.incoming.playertransfer.ReceivePlayerData;
import com.github.maxopoly.artemis.rabbit.incoming.statetracking.CachePlayerNameHandler;
import com.github.maxopoly.artemis.rabbit.incoming.statetracking.ZeusResetConnection;
import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.rabbit.abstr.AbstractRabbitInputHandler;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerName;
import com.github.maxopoly.zeus.rabbit.common.RequestPlayerUUID;

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
		registerCommand(new HandleReplyReceivePlayerData());
		registerCommand(new CachePlayerNameHandler());
		registerCommand(new HandleZeusRequestPlayerData());
		deferCommandToStandardRequest(RequestPlayerName.REPLY_ID);
		deferCommandToStandardRequest(RequestPlayerUUID.REPLY_ID);
	}

	@Override
	protected void logError(String msg) {
		logger.severe(msg);
	}

}

package com.github.maxopoly.artemis.rabbit.incoming.playertransfer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.session.OutgoingPlayerTransferSession;
import com.github.maxopoly.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.RejectPlayerTransfer;
import com.github.maxopoly.zeus.servers.ConnectedServer;

public class PlayerTransferRejectHandler extends InteractiveRabbitCommand<OutgoingPlayerTransferSession> {

	@Override
	public boolean handleRequest(OutgoingPlayerTransferSession connState, ConnectedServer sendingServer, JSONObject data) {
		ArtemisPlugin.getInstance().getTransitManager().removeFromTransit(connState.getPlayer());
		Player player = Bukkit.getPlayer(connState.getPlayer());
		if (player != null) {
			String reason = data.optString("reason", "UNKNOWN");
			player.sendMessage(ChatColor.RED + "Failed to send you to target server because of : " + reason);
		}
		return false;
	}

	@Override
	public String getIdentifier() {
		return RejectPlayerTransfer.ID;
	}

	@Override
	public boolean createSession() {
		return false;
	}
	
}

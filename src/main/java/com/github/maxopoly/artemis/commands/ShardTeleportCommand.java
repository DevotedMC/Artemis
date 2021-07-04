package com.github.maxopoly.artemis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.outgoing.RequestPlayerLocation;
import com.github.maxopoly.artemis.rabbit.session.ALocationRequestSession;
import com.github.maxopoly.zeus.model.PlayerData;
import com.github.maxopoly.zeus.model.TransactionIdManager;
import com.github.maxopoly.zeus.model.ZeusLocation;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShardTeleportCommand extends BaseCommand {

	@CommandAlias("stp")
	@Syntax("[player] [optional_player]")
	@Description("Teleport to a player across any shard")
	@CommandPermission("artemis.tp")
	public void execute(Player sender, @Optional String targetPlayer, @Optional String optionalPlayer) {// TP Sender to player
		if (targetPlayer != null && optionalPlayer == null) {
			UUID playerToTpTo = getPlayerUUID(sender, targetPlayer);
			if (playerToTpTo == null) {
				return;
			}
			setupLocationRequestForPlayer(playerToTpTo, l -> teleportToLocation(((Player) sender).getUniqueId(), l));
		}
		if (targetPlayer != null && optionalPlayer != null) {
			// Tp First player to second one
			UUID playerToTp = getPlayerUUID(sender, targetPlayer);
			UUID playerToTpTo = getPlayerUUID(sender, optionalPlayer);
			if (playerToTp == null || playerToTpTo == null) {
				return;
			}
			setupLocationRequestForPlayer(playerToTpTo, l -> teleportToLocation(playerToTp, l));
		}
	}

	private void setupLocationRequestForPlayer(UUID player, Consumer<ZeusLocation> callback) {
		TransactionIdManager transIdMan = ArtemisPlugin.getInstance().getTransactionIdManager();
		String ticket = transIdMan.pullNewTicket();
		ALocationRequestSession session = new ALocationRequestSession(ArtemisPlugin.getInstance().getZeus(), ticket,
				player, callback);
		transIdMan.putSession(session);
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(new RequestPlayerLocation(ticket, player));
	}

	private void teleportToLocation(UUID who, ZeusLocation loc) {
		ArtemisPlugin.getInstance().getTransitManager().sendTo(who, loc);
	}

	private UUID getPlayerUUID(CommandSender requester, String name) {
		PlayerData data = ArtemisPlugin.getInstance().getPlayerDataManager().getOnlinePlayerData(name);
		if (data == null) {
			requester.sendMessage(ChatColor.RED + "No player with the name " + name + " is online");
			return null;
		}
		return data.getUUID();
	}

	private Integer parseInt(CommandSender sender, String toParse) {
		try {
			return Integer.parseInt(toParse);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + toParse + " is not a valid number");
			return null;
		}
	}

}

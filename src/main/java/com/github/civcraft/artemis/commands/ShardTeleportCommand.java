package com.github.civcraft.artemis.commands;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.rabbit.outgoing.RequestPlayerLocation;
import com.github.civcraft.artemis.rabbit.session.ALocationRequestSession;
import com.github.civcraft.zeus.model.PlayerData;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.model.ZeusLocation;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "stp")
public class ShardTeleportCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		TransactionIdManager transIdMan = ArtemisPlugin.getInstance().getTransactionIdManager();
		if (args.length <= 4 && args.length != 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Fuck off");
				return true;
			}
		}
		switch (args.length) {
		case 1: { // TP Sender to player
			UUID playerToTpTo = getPlayerUUID(sender, args[0]);
			if (playerToTpTo == null) {
				return true;
			}
			setupLocationRequestForPlayer(playerToTpTo, l -> teleportToLocation(((Player) sender).getUniqueId(), l));
			break;
		}
		case 2: // Tp First player to second one
			UUID playerToTp = getPlayerUUID(sender, args[0]);
			UUID playerToTpTo = getPlayerUUID(sender, args[1]);
			if (playerToTp == null || playerToTpTo == null) {
				return true;
			}
			setupLocationRequestForPlayer(playerToTpTo, l -> teleportToLocation(playerToTp, l));
			break;
		case 3: // Tp sender to coords
		case 4: {
			String world;
			if (args.length == 3) {
				world = ArtemisPlugin.getInstance().getConfigManager().getWorldName();
			} else {
				world = args[0];
			}
			Integer x = parseInt(sender, args[args.length - 3]);
			Integer y = parseInt(sender, args[args.length - 2]);
			Integer z = parseInt(sender, args[args.length - 1]);
			if (x == null || y == null || z == null) {
				return true;
			}
			ZeusLocation location = new ZeusLocation(world, x, y, z);
			teleportToLocation(((Player) sender).getUniqueId(), location);
			break;}
		case 5:
			UUID targetPlayer = getPlayerUUID(sender, args[0]);
			if (targetPlayer == null) {
				return true;
			}
			String world = args[1];
			Integer x = parseInt(sender, args[2]);
			Integer y = parseInt(sender, args[3]);
			Integer z = parseInt(sender, args[4]);
			if (x == null || y == null || z == null) {
				return true;
			}
			ZeusLocation location = new ZeusLocation(world, x, y, z);
			teleportToLocation(targetPlayer, location);
			break;
		default:
			return false;
		}
		return true;
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
		// TODO
	}

	private UUID getPlayerUUID(CommandSender requester, String name) {
		PlayerData data = ArtemisPlugin.getInstance().getPlayerDataManager().getLoggedInPlayerByName(name);
		if (data == null) {
			requester.sendMessage(ChatColor.RED + "No player with the name " + name + " is online");
			return null;
		}
		return data.getUUID();
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
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

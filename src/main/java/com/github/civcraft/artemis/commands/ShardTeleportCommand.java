package com.github.civcraft.artemis.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.rabbit.outgoing.PlayerInitTransfer;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;

@CivCommand(id = "stp")
public class ShardTeleportCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Integer x = parseInt(args[0]);
		Integer y = parseInt(args[1]);
		Integer z = parseInt(args[2]);
		if (x == null || y == null || z == null) {
			player.sendMessage(ChatColor.RED + "One of the numbers you entered was malformed.");
			return false;
		}
		String ticket = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
		PlayerInitTransfer initTransfer = new PlayerInitTransfer(ticket, player.getUniqueId(), x, y, z);
		ArtemisPlugin.getInstance().getRabbitHandler().sendMessage(initTransfer);
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer parseInt(String toParse) {
		try {
			return Integer.parseInt(toParse);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}

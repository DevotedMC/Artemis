package com.github.civcraft.artemis.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.artemis.ShardBorderManager;
import com.github.civcraft.artemis.TransitManager;
import com.github.civcraft.artemis.rabbit.outgoing.PlayerInitTransfer;
import com.github.civcraft.zeus.model.ZeusLocation;

public class ShardBorderListener implements Listener {

	private ShardBorderManager manager;

	public ShardBorderListener(ShardBorderManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void playerMove(PlayerMoveEvent event) {
		if (event.getTo() == null) {
			return;
		}
		Location to = event.getTo();
		Location from = event.getFrom();
		if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
			return;
		}
		if (!isOutside(to)) {
			return;
		}
		UUID uuid = event.getPlayer().getUniqueId();
		event.setCancelled(true);
		TransitManager transit = ArtemisPlugin.getInstance().getTransitManager();
		synchronized (transit) {
			if (transit.isInTransit(uuid)) {
				return;
			}
			transit.putInTransit(uuid);
		}
		String world = ArtemisPlugin.getInstance().getConfigManager().getWorldName();
		double x = event.getPlayer().getLocation().getX();
		double y = event.getPlayer().getLocation().getY();
		double z = event.getPlayer().getLocation().getZ();
		event.getPlayer().kickPlayer("TRANSIT");
		Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), () -> {
			String transId = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
			ArtemisPlugin.getInstance().getRabbitHandler()
					.sendMessage(new PlayerInitTransfer(transId, uuid, new ZeusLocation(world, x, y, z)));
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockBurn(BlockBurnEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockDispense(BlockDispenseEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockDropItem(BlockDropItemEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void blockExp(BlockExpEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setExpToDrop(0);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockExplode(BlockExplodeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockFade(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockFertilize(BlockFertilizeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockGrow(BlockGrowEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockFromTo(BlockFromToEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation()) || isOutside(event.getToBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockIgnite(BlockIgniteEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockMultiPlace(BlockMultiPlaceEvent event) {
		for (BlockState state : event.getReplacedBlockStates()) {
			if (isOutside(state.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			if (isOutside(block.getLocation())) {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPistonRetract(BlockPistonRetractEvent event) {
		for (Block block : event.getBlocks()) {
			if (isOutside(block.getLocation())) {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPhysics(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setNewCurrent(0);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockShearEntity(BlockShearEntityEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockCauldronChange(CauldronLevelChangeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockFluidLevelChange(FluidLevelChangeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockLeavesDecay(LeavesDecayEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockMoistureChange(MoistureChangeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockNotePlay(NotePlayEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPhysics(SpongeAbsorbEvent event) {
		Block block = event.getBlock();
		if (isOutside(block.getLocation())) {
			event.setCancelled(true);
			return;
		}

		for (BlockState state : event.getBlocks()) {
			if (isOutside(state.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	private boolean isOutside(Location loc) {
		return !manager.getMapState().isInside(loc.getX(), loc.getZ());
	}

}

package com.github.maxopoly.artemis.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.ShardBorderManager;
import com.github.maxopoly.artemis.TransitManager;
import com.github.maxopoly.artemis.events.PlayerAttemptLeaveShard;
import com.github.maxopoly.zeus.model.ZeusLocation;

public class ShardBorderListener implements Listener {

	private ShardBorderManager manager;
	private TransitManager transitManager;

	public ShardBorderListener(ShardBorderManager manager, TransitManager transitManager) {
		this.manager = manager;
		this.transitManager = transitManager;
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
		if (!transit.putInTransit(uuid)) {
			return; //already in transit
		}
		PlayerAttemptLeaveShard leaveEvent = new PlayerAttemptLeaveShard(event.getPlayer());
		Bukkit.getPluginManager().callEvent(leaveEvent);
		if (leaveEvent.isCancelled()) {
			return;
		}
		String world = ArtemisPlugin.getInstance().getConfigManager().getWorldName();
		double x = to.getX();
		double y = to.getY();
		double z = to.getZ();
		ZeusLocation targetLocation = new ZeusLocation(world, x, y, z);
		transitManager.sendTo(uuid, targetLocation);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void vehicleMove(VehicleMoveEvent event) {
		for(Entity entity : event.getVehicle().getPassengers()) {
			if (entity instanceof Player) {
				playerMove(new PlayerMoveEvent((Player) entity, event.getFrom(), event.getTo()));
			}
		}
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
	public void interactBlock(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			if (isOutside(event.getClickedBlock().getLocation())) {
				event.setCancelled(true);
			}
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void enderPearl(PlayerTeleportEvent event) {
		Block block = event.getTo().getBlock();
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
				|| event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
			if (isOutside(block.getLocation())) {
				event.setCancelled(true);
			}
		}
	}

	private boolean isOutside(Location loc) {
		return !manager.getMapState().isInside(loc.getX(), loc.getZ());
	}

}

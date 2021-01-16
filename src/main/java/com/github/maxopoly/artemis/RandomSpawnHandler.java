package com.github.maxopoly.artemis;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.destroystokyo.paper.MaterialTags;
import com.github.maxopoly.zeus.model.ConnectedMapState;

import vg.civcraft.mc.civmodcore.api.MaterialAPI;

public class RandomSpawnHandler {

	private ConnectedMapState mapState;
	private Random rng;
	private Set<Material> blacklistedGround;
	private int minY;
	private int maxY;
	private int maxTries = 100;
	private int airNeeded;
	private int spawnsToCache;
	private final List <Location> validSpawns;

	public RandomSpawnHandler(ArtemisConfigManager configManager) {
		this.validSpawns = new LinkedList<>();
		this.mapState = configManager.getConnectedMapState();
		this.minY = configManager.getMinRandomSpawnY();
		this.maxY = configManager.getMaxRandomSpawnY();
		this.airNeeded = configManager.getRandomSpawnAirNeeded();
		this.spawnsToCache = configManager.getRandomSpawnsToCache();
		this.blacklistedGround = new HashSet(configManager.getBlacklistedRandomspawnMaterials());
		this.rng = new Random();
		Bukkit.getScheduler().runTaskTimer(ArtemisPlugin.getInstance(), this::refillSpots, 20 * 5L, 20 * 5L);
	}

	public Location getRandomSpawnLocation(UUID uuid) {
		synchronized (validSpawns) {
			if (!validSpawns.isEmpty()) {
				return validSpawns.remove(0);
			}
		}
		return calcSpawnLocation(true);
	}

	private Location getInitialXZ() {
		World world = Bukkit.getWorld(mapState.getWorld());
		double x = mapState.getUpperLeftCorner().getX() + rng.nextInt(mapState.getXSize());
		double z = mapState.getUpperLeftCorner().getZ() + rng.nextInt(mapState.getZSize());
		return new Location(world, x, 0, z);
	}
	
	private void refillSpots() {
		if (validSpawns.size() >= spawnsToCache) {
			//always evict oldest to ensure they are fresh, it's async so who cares. Advantages is not having to do a lookup on spawn
			synchronized (validSpawns) {
				if (!validSpawns.isEmpty()) {
					validSpawns.remove(0);	
				}
			}
		}
		while (validSpawns.size() < spawnsToCache) {
			Location spawn = calcSpawnLocation(false);
			if (spawn != null) {
				synchronized (validSpawns) {
					validSpawns.add(spawn);
				}
			}
		}
	}

	private Location calcSpawnLocation(boolean sync) {
		int tries = 0;
		while (tries++ < maxTries) {
			Location pos = getInitialXZ();
			Chunk chunk = null;
			if (sync) {
				chunk = pos.getChunk();
			} else {
				CompletableFuture<Chunk> chunkFuture = pos.getWorld().getChunkAtAsync(pos);
				try {
					chunk = chunkFuture.get();
				} catch (InterruptedException | ExecutionException e) {
					ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to async load chunk", e);
					continue;
				}
			}
			int airCount = 0;
			int x = pos.getBlockX() & 0xF;
			int z = pos.getBlockZ() & 0xF;
			for (int y = maxY; y >= minY; y--) {
				Block block = chunk.getBlock(x, y, z);
				if (MaterialAPI.isAir(block.getType())) {
					airCount++;
					continue;
				}
				if (!block.getType().isSolid() || blacklistedGround.contains(block.getType())) {
					airCount = 0;
					continue;
				}
				if (airCount >= airNeeded) {
					return block.getLocation().clone().add(0.5, 0.01, 0.5);
				}
				airCount = 0;
			}
		}
		return null;
	}

}

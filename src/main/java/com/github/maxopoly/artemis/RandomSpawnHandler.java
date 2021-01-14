package com.github.maxopoly.artemis;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
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
	private int maxTries = 30;
	private int airNeeded;

	public RandomSpawnHandler(ArtemisConfigManager configManager) {
		this.mapState = configManager.getConnectedMapState();
		this.minY = configManager.getMinRandomSpawnY();
		this.maxY = configManager.getMaxRandomSpawnY();
		this.airNeeded = configManager.getRandomSpawnAirNeeded();
		this.blacklistedGround = new HashSet(configManager.getBlacklistedRandomspawnMaterials());
		this.rng = new Random();
	}

	public Location getRandomSpawnLocation(UUID uuid) {
		int tries = 0;
		while (tries++ < maxTries) {
			Location pos = getInitialXZ();
			int airCount = 0;
			for(int i = maxY; i >= minY; i--) {
				Block block = pos.getWorld().getBlockAt(pos.getBlockX(), i, pos.getBlockZ());
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

	private Location getInitialXZ() {
		World world = Bukkit.getWorld(mapState.getWorld());
		double x = mapState.getUpperLeftCorner().getX() + rng.nextInt(mapState.getXSize());
		double z = mapState.getUpperLeftCorner().getZ() + rng.nextInt(mapState.getZSize());
		return new Location(world, x, 0, z);
	}

}

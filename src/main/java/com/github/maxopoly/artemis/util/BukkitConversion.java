package com.github.maxopoly.artemis.util;

import org.bukkit.Location;

import com.github.maxopoly.zeus.model.ZeusLocation;

public final class BukkitConversion {
	
	private BukkitConversion() {}
	
	public static ZeusLocation convertLocation(Location location) {
		return new ZeusLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

}

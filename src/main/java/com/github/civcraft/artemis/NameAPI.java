package com.github.civcraft.artemis;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import com.google.common.base.Preconditions;

public final class NameAPI {
	
	private NameAPI() {}
	

	/**
	 * UUID used when the server console issues a NameLayer command. This UUID can
	 * never be used by a normal minecraft account due to them always using UUIDv4
	 * and the version being set to 1 for this one
	 */
	public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-1000-0000-000000000001");

	/**
	 * Looks up a player name for the given UUID. If the player has not logged on at
	 * all since this server was started, their UUID may not be cached locally in
	 * which case a lookup for the UUID is sent to Zeus. Any UUID <--> name mapping
	 * looked up since this servers last restart is cached and mappings for all
	 * players online on any Artemis server are guaranteed to be globally available.
	 * 
	 * For Group.CONSOLE_UUID, the special UUID used for console command, the string
	 * "Server console" will be returned
	 * 
	 * @param uuid UUID to look up name for, may not be null
	 * @return Name of the player with the given UUID or null if no such player
	 *         exists
	 */
	public static String getName(UUID uuid) {
		Preconditions.checkNotNull(uuid);
		if (uuid.equals(CONSOLE_UUID)) {
			return "Server console";
		}
	}

	public static UUID getUUID(String name) {
		return null; // TODO
	}

	public static void consumeUUIDSync(String name, Consumer<UUID> handler) {
		consumeUUIDAsync(name,
				u -> Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), () -> handler.accept(u)));
	}

	public static void consumeNameSync(UUID uuid, Consumer<String> handler) {
		consumeNameAsync(uuid,
				n -> Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), () -> handler.accept(n)));
	}

	public static void consumeUUIDAsync(String name, Consumer<UUID> handler) {

	}

	public static void consumeNameAsync(UUID uuid, Consumer<String> handler) {

	}

}

package com.github.maxopoly.artemis.nbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.rabbit.session.ArtemisPlayerDataTransferSession;
import com.github.maxopoly.artemis.util.BukkitConversion;
import com.github.maxopoly.zeus.ZeusMain;
import com.github.maxopoly.zeus.model.ConnectedMapState;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.maxopoly.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.mojang.datafixers.DataFixer;

import net.minecraft.server.v1_16_R3.Convertable;
import net.minecraft.server.v1_16_R3.Convertable.ConversionSession;
import net.minecraft.server.v1_16_R3.DataFixTypes;
import net.minecraft.server.v1_16_R3.DedicatedPlayerList;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.GameProfileSerializer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.PlayerList;
import net.minecraft.server.v1_16_R3.SavedFile;
import net.minecraft.server.v1_16_R3.SystemUtils;
import net.minecraft.server.v1_16_R3.WorldNBTStorage;
import vg.civcraft.mc.civmodcore.playersettings.PlayerSetting;
import vg.civcraft.mc.civmodcore.playersettings.PlayerSettingAPI;
import vg.civcraft.mc.civmodcore.serialization.NBTCompound;

public class CustomWorldNBTStorage extends WorldNBTStorage {
	
	private static final String CUSTOM_DATA_ID = "artemis_data";

	private static final Set<UUID> activePlayers = new HashSet<>();
	private Map<UUID, Map<String, String>> customDataOriginallyLoaded;

	public static synchronized void addActivePlayer(UUID uuid) {
		activePlayers.add(uuid);
	}

	public static synchronized void removeActivePlayer(UUID uuid) {
		activePlayers.remove(uuid);
	}

	public static synchronized boolean isActive(UUID uuid) {
		return activePlayers.contains(uuid);
	}

	private final File playerDir;

	private CustomWorldNBTStorage(ConversionSession conversionsession, DataFixer datafixer) {
		super(conversionsession, datafixer);
		this.playerDir = conversionsession.getWorldFolder(SavedFile.PLAYERDATA).toFile();
		this.playerDir.mkdirs();
		this.customDataOriginallyLoaded = new ConcurrentHashMap<>();
	}
	
	public void shutdown() {
		activePlayers.clear();
	}
	
	public static ZeusLocation readZeusLocation(byte [] playerData) {
		try {
			NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(new ByteArrayInputStream(playerData));
			NBTCompound comp = new NBTCompound(nbttagcompound);
			double [] pos = comp.getDoubleArray("Pos");
			ConnectedMapState mapState = ArtemisPlugin.getInstance().getConfigManager().getConnectedMapState();
			return new ZeusLocation(mapState.getWorld(), pos [0], pos [1], pos [2]);
		} catch (IOException e) {
			ZeusMain.getInstance().getLogger().error("Failed to deserialize nbt", playerData);
			return null;
		}
	}
	
	public void vanillaSave(EntityHuman entityhuman) {
		try {
			NBTTagCompound nbttagcompound = entityhuman.save(new NBTTagCompound());
			insertCustomPlayerData(entityhuman.getUniqueID(), nbttagcompound);
			File file = File.createTempFile(entityhuman.getUniqueIDString() + "-", ".dat",
					this.playerDir);
			NBTCompressedStreamTools.a(nbttagcompound, new FileOutputStream(file));
			File file1 = new File(this.playerDir, entityhuman.getUniqueIDString() + ".dat");
			File file2 = new File(this.playerDir, entityhuman.getUniqueIDString() + ".dat_old");
			SystemUtils.a(file1, file, file2);
		} catch (Exception exception) {
			ZeusMain.getInstance().getLogger().warn("Failed to save player data for {}",
					entityhuman.getDisplayName().getString());
		}
	}
	
	public void vanillaSaveFullData(byte )
	
	public NBTTagCompound vanillaLoad(UUID uuid) {
		NBTTagCompound nbttagcompound = null;
		try {
			File file = new File(this.playerDir, String.valueOf(uuid.toString()) + ".dat");
			if (file.exists() && file.isFile()) {
				nbttagcompound = NBTCompressedStreamTools.a(new FileInputStream(file));
			}
		} catch (Exception exception) {
			ZeusMain.getInstance().getLogger().warn("Failed to vanilla load player data for " + uuid);
		}
		return nbttagcompound;
	}

	public void save(EntityHuman entityhuman) {
		if (isActive(entityhuman.getUniqueID())) {
			vanillaSave(entityhuman);
			return;
		}
		ArtemisPlugin artemis = ArtemisPlugin.getInstance();
		NBTTagCompound nbttagcompound = entityhuman.save(new NBTTagCompound());
		insertCustomPlayerData(entityhuman.getUniqueID(), nbttagcompound);
		if (ArtemisPlugin.getInstance().getConfigManager().isDebugEnabled()) {
			ArtemisPlugin.getInstance().getLogger().info("Saved NBT : " + nbttagcompound.toString());
		}
		String transactionId = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
		// create session which will be used to save data locally if Zeus is unavailable
		ArtemisPlayerDataTransferSession session = new ArtemisPlayerDataTransferSession(ArtemisPlugin.getInstance().getZeus(),
				transactionId, entityhuman);
		ArtemisPlugin.getInstance().getTransactionIdManager().putSession(session);
		// save both location and data in that session
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			NBTCompressedStreamTools.a(nbttagcompound, output);
		} catch (IOException e) {
			artemis.getLogger().severe("Failed to serialize player data: " + e.toString());
			return;
		}
		byte[] data = output.toByteArray();
		ZeusLocation location = new ZeusLocation(artemis.getConfigManager().getWorldName(), entityhuman.locX(),
				entityhuman.locY(), entityhuman.locZ());
		session.setData(data);
		session.setLocation(location);
		//always vanilla save
		vanillaSave(entityhuman);
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new SendPlayerData(transactionId, entityhuman.getUniqueID(), data, location));
	}

	public NBTTagCompound load(EntityHuman entityhuman) {
		NBTTagCompound comp = loadCompound(entityhuman.getUniqueID());
		if (comp != null) {
			int i = comp.hasKeyOfType("DataVersion", 3) ? comp.getInt("DataVersion") : -1;
			entityhuman.load(
					GameProfileSerializer.a(this.a, DataFixTypes.PLAYER, comp, i));
		}
		return comp;
	}

	public NBTTagCompound getPlayerData(String s) {
		UUID uuid = UUID.fromString(s);
		return loadCompound(uuid);
	}

	private NBTTagCompound loadCompound(UUID uuid) {
		PlayerDataTransferSession session = ArtemisPlugin.getInstance().getPlayerDataCache().consumeSession(uuid);
		if (session == null) {
			return null;
		}
		if (session.getData().length == 0) {
			// new player, data will be generated
			return null;
		}
		ByteArrayInputStream input = new ByteArrayInputStream(session.getData());
		try {
			NBTCompound comp = new NBTCompound(NBTCompressedStreamTools.a(input));
			ZeusLocation loc = session.getLocation();
			if (loc == null) {
				loc = BukkitConversion.convertLocation(
						ArtemisPlugin.getInstance().getRandomSpawnHandler().getRandomSpawnLocation(uuid));
			}
			if (loc != null) {
				comp.setDoubleArray("Pos",new double [] {loc.getX(), loc.getY(), loc.getZ()});
			}
			insertWorldUUID(comp);
			if (comp.hasKeyOfType(CUSTOM_DATA_ID, 10)) {
				NBTCompound customData = comp.getCompound(CUSTOM_DATA_ID);
				extractCustomPlayerData(uuid, customData);
			}
			if (ArtemisPlugin.getInstance().getConfigManager().isDebugEnabled()) {
				ArtemisPlugin.getInstance().getLogger().info("Loaded NBT : " + comp.toString());
			}
			return comp.getRAW();
		} catch (IOException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data", e);
			return null;
		}
	}
	
	private static void insertWorldUUID(NBTCompound compound) {
		String worldName = ArtemisPlugin.getInstance().getConfigManager().getConnectedMapState().getWorld();
		UUID worldUUID = Bukkit.getWorld(worldName).getUID();
		compound.setLong("WorldUUIDLeast", worldUUID.getLeastSignificantBits());
		compound.setLong("WorldUUIDMost", worldUUID.getMostSignificantBits());
	}

	public static CustomWorldNBTStorage insertCustomNBTHandler() {
		Server server = Bukkit.getServer();
		try {
			Field trueServerField = CraftServer.class.getDeclaredField("console");
			trueServerField.setAccessible(true);
			MinecraftServer trueServer = (MinecraftServer) trueServerField.get(server);
			Field nbtField = MinecraftServer.class.getDeclaredField("worldNBTStorage");
			Convertable.ConversionSession session = trueServer.convertable;
			DataFixer dataFixer = trueServer.dataConverterManager;
			CustomWorldNBTStorage customNBT = new CustomWorldNBTStorage(session, dataFixer);
			overwriteFinalField(nbtField, customNBT, trueServer);
			Field playerListField = CraftServer.class.getDeclaredField("playerList");
			playerListField.setAccessible(true);
			DedicatedPlayerList playerList = (DedicatedPlayerList) playerListField.get(server);
			Field nbtPlayerListField = PlayerList.class.getField("playerFileData");
			overwriteFinalField(nbtPlayerListField, customNBT, playerList);
			return customNBT;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to set custom nbt handler", e);
			return null;
		}
	}
	
	private void extractCustomPlayerData(UUID player, NBTCompound specialDataCompound) {
		//we keep data in this map so settings not loaded on the server currently are not reset
		Map<String, String> extractedData = new HashMap<>();
		for(PlayerSetting setting: PlayerSettingAPI.getAllSettings()) {
			if (!specialDataCompound.hasKey(setting.getIdentifier())) {
				continue;
			}
			String serial = specialDataCompound.getString(setting.getIdentifier());
			extractedData.put(setting.getIdentifier(), serial);
			Object deserialized = setting.deserialize(serial);
			setting.setValue(player, deserialized);
		}
		this.customDataOriginallyLoaded.put(player, extractedData);
	}
	
	private void insertCustomPlayerData(UUID player, NBTTagCompound generalPlayerDataCompound) {
		Map<String, String> dataToInsert = customDataOriginallyLoaded.computeIfAbsent(player, p -> new HashMap<>());
		for(PlayerSetting setting: PlayerSettingAPI.getAllSettings()) {
			if (!setting.hasValue(player)) {
				continue;
			}
			String serial = setting.serialize(setting.getValue(player));
			dataToInsert.put(setting.getIdentifier(), serial);
		}
		NBTCompound comp = new NBTCompound(generalPlayerDataCompound);
		NBTCompound customDataComp = new NBTCompound();
		for(Entry<String, String> entry : dataToInsert.entrySet()) {
			customDataComp.setString(entry.getKey(), entry.getValue());
		}
		comp.setCompound(CUSTOM_DATA_ID, customDataComp);
	}

	private static void overwriteFinalField(Field field, Object newValue, Object obj) {
		try {
			field.setAccessible(true);
			// remove final modifier from field
			Field modifiersField;
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.PROTECTED);
			field.set(obj, newValue);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to set final field", e);
		}
	}

}

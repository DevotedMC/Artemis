package com.github.civcraft.artemis.nbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.SendPlayerData;
import com.github.civcraft.zeus.rabbit.sessions.PlayerDataTransferSession;
import com.mojang.datafixers.DataFixer;

import net.minecraft.server.v1_16_R1.Convertable;
import net.minecraft.server.v1_16_R1.Convertable.ConversionSession;
import net.minecraft.server.v1_16_R1.DataFixTypes;
import net.minecraft.server.v1_16_R1.DedicatedPlayerList;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.GameProfileSerializer;
import net.minecraft.server.v1_16_R1.MinecraftServer;
import net.minecraft.server.v1_16_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import net.minecraft.server.v1_16_R1.PlayerList;
import net.minecraft.server.v1_16_R1.WorldNBTStorage;

public class CustomWorldNBTStorage extends WorldNBTStorage {

	public CustomWorldNBTStorage(ConversionSession convertable_conversionsession, DataFixer datafixer) {
		super(convertable_conversionsession, datafixer);
	}

	public void save(EntityHuman entityhuman) {
		System.out.println("Called save for " + entityhuman.getName());
		ArtemisPlugin artemis = ArtemisPlugin.getInstance();
		NBTTagCompound nbttagcompound = entityhuman.save(new NBTTagCompound());
		String transactionId = ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket();
		// create session which will be used to save data locally if Zeus is unavailable
		PlayerDataTransferSession session = new PlayerDataTransferSession(ArtemisPlugin.getInstance().getZeus(),
				transactionId, entityhuman.getUniqueID());
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
		session.setData(data, location);
		ArtemisPlugin.getInstance().getRabbitHandler()
				.sendMessage(new SendPlayerData(transactionId, entityhuman.getUniqueID(), data, location));
	}

	public NBTTagCompound load(EntityHuman entityhuman) {
		System.out.println("Called load for " + entityhuman.getName());
		NBTTagCompound comp = loadCompound(entityhuman.getUniqueID());
		int i = comp.hasKeyOfType("DataVersion", 3) ? comp.getInt("DataVersion") : -1;
		entityhuman.load(GameProfileSerializer.a((DataFixer) this.a, (DataFixTypes) DataFixTypes.PLAYER,
				comp, (int) i));
		return comp;
	}

	public NBTTagCompound getPlayerData(String s) {
		System.out.println("Called get for " + s);
		UUID uuid = UUID.fromString(s);
		return loadCompound(uuid);
	}

	private NBTTagCompound loadCompound(UUID uuid) {
		PlayerDataTransferSession session = ArtemisPlugin.getInstance().getPlayerDataCache().consumeSession(uuid);
		if (session == null) {
			return null;
		}
		if (session.getData().length == 0) {
			//new player, data will be generated
			return null;
		}
		ByteArrayInputStream input = new ByteArrayInputStream(session.getData());
		try {
			return NBTCompressedStreamTools.a(input);
		} catch (IOException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load player data", e);
			return null;
		}
	}
	
	public static void insertCustomNBTHandler() {
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
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			ArtemisPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to set custom nbt handler", e);
		}
		
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

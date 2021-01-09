package com.github.maxopoly.artemis.rabbit.incoming.playertransfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.artemis.nbt.CustomWorldNBTStorage;
import com.github.maxopoly.artemis.rabbit.outgoing.SendRequestedPlayerData;
import com.github.maxopoly.zeus.model.ZeusLocation;
import com.github.maxopoly.zeus.rabbit.incoming.GenericInteractiveRabbitCommand;
import com.github.maxopoly.zeus.rabbit.outgoing.artemis.ZeusRequestPlayerData;
import com.github.maxopoly.zeus.servers.ConnectedServer;

import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;

public class HandleZeusRequestPlayerData extends GenericInteractiveRabbitCommand {

	@Override
	public String getIdentifier() {
		return ZeusRequestPlayerData.ID;
	}

	@Override
	public void handleRequest(String ticket, ConnectedServer sendingServer, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		NBTTagCompound playerData = ArtemisPlugin.getInstance().getCustomNBTStorage().vanillaLoad(player);
		if (playerData == null) {
			sendReply(sendingServer, new SendRequestedPlayerData(ticket, null, null));
			return;
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			NBTCompressedStreamTools.a(playerData, output);
		} catch (IOException e) {
			ArtemisPlugin.getInstance().getLogger().severe("Failed to serialize player data: " + e.toString());
			return;
		}
		byte[] rawPlayerData = output.toByteArray();
		ZeusLocation loc = CustomWorldNBTStorage.readZeusLocation(rawPlayerData);
		if (loc == null) {
			sendReply(sendingServer, new SendRequestedPlayerData(ticket, null, null));
			return;
		}
		sendReply(sendingServer, new SendRequestedPlayerData(ticket, rawPlayerData, loc));
	}

}

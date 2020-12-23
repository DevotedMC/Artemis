package com.github.civcraft.artemis.rabbit;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import com.github.civcraft.artemis.ArtemisPlugin;
import com.github.civcraft.zeus.model.TransactionIdManager;
import com.github.civcraft.zeus.rabbit.StandardRequest;
import com.github.civcraft.zeus.servers.ConnectedServer;

public abstract class MCStandardRequest extends StandardRequest {

	public MCStandardRequest() {
		super(ArtemisPlugin.getInstance().getTransactionIdManager(), ArtemisPlugin.getInstance().getZeus());
	}
	
	public void doSync(Runnable run) {
		Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), run);
	}

}

package com.github.maxopoly.artemis.rabbit;

import org.bukkit.Bukkit;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.rabbit.StandardRequest;

public abstract class MCStandardRequest extends StandardRequest {

	public MCStandardRequest() {
		super(ArtemisPlugin.getInstance().getTransactionIdManager(), ArtemisPlugin.getInstance().getZeus());
	}
	
	public void doSync(Runnable run) {
		Bukkit.getScheduler().runTask(ArtemisPlugin.getInstance(), run);
	}

}

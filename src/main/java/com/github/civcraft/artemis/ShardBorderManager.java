package com.github.civcraft.artemis;

import com.github.civcraft.zeus.model.ConnectedMapState;

public class ShardBorderManager {
	
	private ConnectedMapState shardBorder;
	
	public ShardBorderManager(ConnectedMapState map) {
		this.shardBorder = map;
	}
	
	public ConnectedMapState getMapState() {
		return shardBorder;
	}

}

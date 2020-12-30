package com.github.maxopoly.artemis;

import com.github.maxopoly.zeus.model.ConnectedMapState;

public class ShardBorderManager {
	
	private ConnectedMapState shardBorder;
	
	public ShardBorderManager(ConnectedMapState map) {
		this.shardBorder = map;
	}
	
	public ConnectedMapState getMapState() {
		return shardBorder;
	}

}

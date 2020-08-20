package com.elikill58.negativity.api;

import com.elikill58.negativity.universal.adapter.Adapter;

public enum GameMode {
	
	SURVIVAL("Survival"),
	ADVENTURE("Adventure"),
	CREATIVE("Creative"),
	SPECTATOR("Spectator"),
	CUSTOM("Custom");
	
	private final String name;
	
	private GameMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static GameMode get(String name) {
		for(GameMode gm : GameMode.values())
			if(gm.getName().equalsIgnoreCase(name) || gm.name().equalsIgnoreCase(name))
				return gm;
		Adapter.getAdapter().getLogger().info("[GameMode] Unknow gamemode " + name);
		return CUSTOM;
	}
}
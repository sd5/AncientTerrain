package me.sd5.ancientterrain;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class ATMain extends JavaPlugin {

	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
		
		return new ATGenerator();
		
	}
	
}

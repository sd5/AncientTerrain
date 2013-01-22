package me.sd5.ancientterrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class ATGenerator extends ChunkGenerator {
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		
		ArrayList<BlockPopulator> populators = new ArrayList<BlockPopulator>();
		
		
		
		return populators;
		
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
		
		byte[][] result = new byte[world.getMaxHeight() / 16][4096];
		
		
		
		return result;
		
	}
	
	private void setBlock(byte[][] result, int x, int y, int z, byte material) {
		
		if(result[y >> 4] == null) {
			result[y >> 4] = new byte[4096];
		}
		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = material;
		
	}
	
}

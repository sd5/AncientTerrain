package me.sd5.ancientterrain;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class ATBlockPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				for(int y = 0; y < 128; y++) {
					chunk.getBlock(x, y, z).setType(Material.STONE);
				}
				for(int y = 128; y < 134; y++) {
					chunk.getBlock(x, y, z).setType(Material.DIRT);
				}
				chunk.getBlock(x, 134, z).setType(Material.GRASS);
			}
		}
		
	}

}

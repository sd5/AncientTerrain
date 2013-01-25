package me.sd5.ancientterrain;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class ATDataPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random random, Chunk chunk) {

		//Get the coordinates of the region this chunk is in.
		int regionX = chunk.getX() >> 5; //Or: floor(chunkX / 32.0)
		int regionZ = chunk.getZ() >> 5; //Or: floor(chunkZ / 32.0)
		
		Region region = null;
		
		try {
			region = new Region(world.getName(), regionX, regionZ);
			
			//Calculate region relative chunk coordinates.
			 int[] coordinates = Region.getRelativeChunkCoordinates(chunk.getX(), chunk.getZ());
			 byte[] data = region.getData(coordinates[0], coordinates[1]);
			 
			 for(int x = 0; x < 16; x++) {
				 for(int z = 0; z < 16; z++) {
					 for(int y = 0; y < 128; y++) {
						 byte dataByte = data[(y + (z * 128) + (x * 128 * 16))];
						 
						 if(dataByte != 0) {
							 chunk.getBlock(x, y, z).setData(dataByte);
						 }
					 }
				 }
			 }
		} catch (RegionNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "The region " + e.getRegionX() + "|" + e.getRegionZ() + " could not be found.");
			return;
		} catch (ChunkNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "The chunk " + e.getChunkX() + "|" + e.getChunkZ() + " could not be found.");
			return;
		}
		
	}

}

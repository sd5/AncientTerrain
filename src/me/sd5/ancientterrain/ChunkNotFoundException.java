package me.sd5.ancientterrain;

public class ChunkNotFoundException extends Exception {

	private static final long serialVersionUID = 5373092684601362363L;

	private String world;
	private int chunkX;
	private int chunkZ;
	private int regionX;
	private int regionZ;
	
	public ChunkNotFoundException(String world, int chunkX, int chunkZ) {
		
		this.world = world;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.regionX = regionX >> 5;
		this.regionZ = regionZ >> 5;
		
	}
	
	/**
	 * 
	 * @return: The world this region is in.
	 */
	public String getWorld() {
		
		return world;
		
	}
	
	/**
	 * 
	 * @return: The x-coordinate this chunk is at.
	 */
	public int getChunkX() {
		
		return chunkX;
		
	}
	
	/**
	 * 
	 * @return: The z-coordinate this chunk is at.
	 */
	public int getChunkZ() {
		
		return chunkZ;
		
	}
	
	/**
	 * 
	 * @return: The x-coordinate this region is at.
	 */
	public int getRegionX() {
		
		return regionX;
		
	}
	
	/**
	 * 
	 * @return: The z-coordinate this region is at.
	 */
	public int getRegionZ() {
		
		return regionZ;
		
	}
	
}

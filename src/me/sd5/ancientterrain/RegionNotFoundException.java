package me.sd5.ancientterrain;

import java.io.FileNotFoundException;

public class RegionNotFoundException extends FileNotFoundException {

	private static final long serialVersionUID = -4351494741329988405L;
	
	private String world;
	private int regionX;
	private int regionZ;
	
	public RegionNotFoundException(String world, int regionX, int regionZ) {
		
		this.world = world;
		this.regionX = regionX;
		this.regionZ = regionZ;
		
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

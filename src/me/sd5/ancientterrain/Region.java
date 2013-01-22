package me.sd5.ancientterrain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Region {

	private String world;
	private int regionX;
	private int regionZ;
	
	private RandomAccessFile file;
	
	private int[] offsets = new int[1024];
	private int[] timestamps = new int[1024];
	
	public Region(String world, int regionX, int regionZ) throws RegionNotFoundException {
			
		this.world = world;
		this.regionX = regionX;
		this.regionZ = regionZ;
		
		try {
			this.file = new RandomAccessFile(world + File.separator, "rw");
		} catch (FileNotFoundException e) {
			throw new RegionNotFoundException();
		}
			
		try {
			file.seek(0L);						//Read the chunk offsets.
			for(int n = 0; n < 1024; n++) {		//The offsets specify where the data of a chunk is stored in the region file.
				int offset = file.readInt();	//
				offsets[n] = offset;			//
			}									//
			
			file.seek(4096L);					//Read the chunk timestamps.
			for(int n = 0; n < 1024; n++) {		//The offsets specify when a chunk was edited the last time.
				int timestamp = file.readInt();	//
				timestamps[n] = timestamp;		//
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
			
	}

}

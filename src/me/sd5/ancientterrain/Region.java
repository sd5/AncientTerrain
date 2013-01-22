package me.sd5.ancientterrain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

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
	
	/**
	 * Reads the data of the chunk from the region.
	 * @param chunkX Relative chunk x-coordinate of the chunk.
	 * @param chunkZ Relative chunk z-coordinate of the chunk.
	 * @return A stream with the data of the chunk.
	 */
	public DataInputStream getChunkData(int chunkX, int chunkZ) throws ChunkNotFoundException {
		
		int offset = offsets[chunkX + (chunkZ * 32)]; //Where to find the chunk data.
		if(offset == 0) {
			throw new ChunkNotFoundException();
		}
		
		try {
			this.file.seek((offset >> 8) * 4096);
			
			int length = this.file.readInt();
			byte compression = this.file.readByte();
			
			if(compression == 1) { //If compression byte is 1, the compression is GZip.
				byte[] compressedData = new byte[length - 1];
				this.file.read(compressedData);
				return new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(compressedData)));
			}
			if(compression == 2) { //If compression byte is 2, the compression is Deflate. Standard.
				byte[] compressedData = new byte[length - 1];
				this.file.read(compressedData);
				return new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(compressedData)));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

}

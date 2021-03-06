package me.sd5.ancientterrain;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

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
			this.file = new RandomAccessFile(world + "_mcr" + File.separator + "region" + File.separator + "r." + regionX + "." + regionZ + ".mcr", "r");
		} catch (FileNotFoundException e) {
			throw new RegionNotFoundException(world, regionX, regionZ);
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
	private DataInputStream getChunkAsStream(int chunkX, int chunkZ) throws ChunkNotFoundException {
		
		int offset = offsets[chunkX + (chunkZ * 32)]; //Where to find the chunk data.
		if(offset == 0) {
			throw new ChunkNotFoundException(world, chunkX, chunkZ);
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
	
	/**
	 * Reads a tag from the chunk data.
	 * @param chunkX: Relative chunk x-coordinate of the chunk.
	 * @param chunkZ: Relative chunk x-coordinate of the chunk.
	 * @param tagName
	 * @return: The tag of the chunk with the given name.
	 * @throws ChunkNotFoundException: If the chunk at the given coordinates was not found.
	 * @throws TagNotFoundException: If the tag with the given name was not found.
	 */
	private Tag getTag(int chunkX, int chunkZ, String tagName) throws ChunkNotFoundException, TagNotFoundException {
		try {
			
			DataInputStream chunkData = getChunkAsStream(chunkX, chunkZ);
			NBTInputStream nbtIs = new NBTInputStream(chunkData);
			
			CompoundTag rootTag = (CompoundTag) nbtIs.readTag();
			Map<String, Tag> rootMap = rootTag.getValue();
			CompoundTag levelTag = (CompoundTag) rootMap.get("Level");
			Map<String, Tag> levelMap = levelTag.getValue();
			
			Tag tag = levelMap.get(tagName);
			
			if(tag == null) {
				throw new TagNotFoundException();
			}
			
			return levelMap.get(tagName);
			
		} catch(IOException e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Returns the blocks of this chunk.
	 * @param chunkX: Relative chunk x-coordinate of the chunk.
	 * @param chunkZ: Relative chunk x-coordinate of the chunk.
	 * @return An array of bytes containing the material ID of each block.
	 * @throws ChunkNotFoundException 
	 */
	public byte[] getBlocks(int chunkX, int chunkZ) throws ChunkNotFoundException {
		
		byte[] blocks = new byte[32768]; //16 * 16 * 128 = 32768
		
		int offset = offsets[chunkX + (chunkZ * 32)]; //Where to find the chunk data.
		if(offset == 0) {
			throw new ChunkNotFoundException(world, chunkX, chunkZ);
		}
		
		ByteArrayTag blocksTag = null;
		try {
			blocksTag = (ByteArrayTag) getTag(chunkX, chunkZ, "Blocks");
		} catch (TagNotFoundException e) {
			e.printStackTrace();
		}
		blocks = blocksTag.getValue();
		
		return blocks;
		
	}
	
	/**
	 * Returns the data values of the blocks in this chunk.
	 * @param chunkX: Relative chunk x-coordinate of the chunk.
	 * @param chunkZ: Relative chunk x-coordinate of the chunk.
	 * @return An array of bytes containing the data values of each block.
	 * @throws ChunkNotFoundException
	 */
	public byte[] getData(int chunkX, int chunkZ) throws ChunkNotFoundException {
		
		//1 byte for 1 nibble.
		byte[] data = new byte[32768]; //16 * 16 * 128 = 32768
		
		//1 byte for 2 nibble.
		byte[] compressedData = new byte[16384]; //16 * 16 * 128 / 2 = 16384
		try {
			compressedData = ((ByteArrayTag) getTag(chunkX, chunkZ, "Data")).getValue();
		} catch (TagNotFoundException e) {
			e.printStackTrace();
		}
		
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				for(int y = 0; y < 128; y++) {
					int offset = (y + z * 128 + x * 128 * 16) / 2; //Get the offset of the byte where the nibble is in.
					
					int part = y % 2; //Check whether we need the first or the second part of the byte.
					
					byte nibble;
					if(part == 1) {
						nibble = (byte) (compressedData[offset] >> 4 & 0xF); //Get the first part of the byte ---> first nibble.
					} else {
						nibble = (byte) (compressedData[offset] & 0xF); //Get the second part of the byte ---> second nibble.
					}
					
					data[(y + (z * 128) + (x * 128 * 16))] = nibble;
				}
			}
		}
		
		return data;
		
	}
	
	/**
	 * Calculates the region relative coordinates of a chunk out of the absolute chunk coodinates.
	 * @param chunkX: Absolute chunk x-coordinate.
	 * @param chunkZ: Absolute chunk z-coordinate.
	 * @return: An array with two integers. First is x-coordinate, second is z-coordinate.
	 */
	public static int[] getRelativeChunkCoordinates(int chunkX, int chunkZ) {
		
		int[] coordinates = new int[2];
		
		int x = (chunkX >= 0) ? (chunkX % 32) : 32 - (-chunkX % 32);
			x = (x == 32) ? 0 : x;
		int z = (chunkZ >= 0) ? (chunkZ % 32) : 32 - (-chunkZ % 32);
			z = (z == 32) ? 0: z;
			
		coordinates[0] = x;
		coordinates[1] = z;
		
		return coordinates;
		
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
	public int getX() {
		
		return regionX;
		
	}
	
	/**
	 * 
	 * @return: The z-coordinate this region is at.
	 */
	public int getZ() {
		
		return regionZ;
		
	}

}

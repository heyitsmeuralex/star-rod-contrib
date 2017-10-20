package data.sprites;

import data.yay0.Yay0Helper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeMap;
import main.Directories;
import main.InputFileException;
import org.apache.commons.io.FileUtils;
import patcher.Patcher;
import util.IOUtils;
import util.Logger;
import util.Priority;










public class SpritePatcher
{
  private static final int SPRITE_TABLE_BASE = 27158456;
  private static final int SPRITE_DATA_LIMIT = 28844552;
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private File[] files;
  private int highestID;
  
  public SpritePatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void readSpriteList()
    throws IOException
  {
    TreeMap<Integer, File> sprites = new TreeMap();
    highestID = Integer.MIN_VALUE;
    
    File in = new File(Directories.MOD_SPRITE + "Sprites.txt");
    for (String line : IOUtils.readTextFile(in, false))
    {
      String[] tokens = line.split(":");
      if (tokens.length != 2) {
        throw new InputFileException(in, "Invalid line in Sprites.txt:\r\n" + line);
      }
      try
      {
        int id = (int)Long.parseLong(tokens[0].trim(), 16);
        if ((id < 1) || (id > 32767))
          throw new InputFileException(in, "Invalid sprite ID in Sprites.txt:\r\n" + line);
      } catch (NumberFormatException e) {
        throw new InputFileException(in, "Invalid sprite ID in Sprites.txt:\r\n" + line); }
      int id;
      if (id > highestID) {
        highestID = id;
      }
      String xmlName = tokens[1].trim();
      File xml = new File(Directories.MOD_SPRITE_SRC + xmlName + "/SpriteSheet.xml");
      if (!xml.exists()) {
        throw new InputFileException(in, "XML file Sprites.txt does not exist:\r\n" + line);
      }
      sprites.put(Integer.valueOf(id), xml);
    }
    
    files = new File[highestID + 1];
    for (int i = 1; i <= highestID; i++) {
      files[i] = ((File)sprites.get(Integer.valueOf(i)));
    }
  }
  
  public void patchSpriteSheets() throws IOException {
    FileUtils.forceMkdir(Directories.MOD_SPRITE_TEMP.toFile());
    
    raf.seek(27158456 + (highestID + 1) * 4);
    int roomLeft = 28844552 - (int)raf.getFilePointer();
    boolean reusingRoom = true;
    
    int[] offsets = new int[highestID + 2];
    int lastOffset = (int)raf.getFilePointer();
    

    for (int i = 1; i <= highestID; i++)
    {
      if (files[i] == null)
      {
        offsets[i] = lastOffset;
      }
      else
      {
        File out = new File(Directories.MOD_SPRITE_TEMP + String.format("%02X", new Object[] { Integer.valueOf(i) }));
        Logger.log(String.format("Writing sprite sheet %02X of %02X...", new Object[] { Integer.valueOf(i), Integer.valueOf(highestID) }), Priority.MILESTONE);
        
        Sprite spr = Sprite.readXML(files[i]);
        spr.writeBinary(out);
        
        byte[] decoded = FileUtils.readFileToByteArray(out);
        byte[] encoded = Yay0Helper.encode(decoded);
        
        if ((reusingRoom) && (roomLeft < encoded.length))
        {
          patcher.clearRegion((int)raf.getFilePointer(), 28844552);
          raf.seek(Patcher.nextAlignedOffset(raf));
          reusingRoom = false;
        }
        
        offsets[i] = ((int)raf.getFilePointer());
        raf.write(encoded);
        roomLeft -= encoded.length;
        lastOffset = offsets[i];
        
        Logger.log(String.format("Wrote spritesheet %02X to %X", new Object[] { Integer.valueOf(i), Integer.valueOf(offsets[i]) }));
      }
    }
    if (reusingRoom) {
      patcher.clearRegion((int)raf.getFilePointer(), 28844552);
    }
    offsets[(highestID + 1)] = ((int)raf.getFilePointer());
    
    raf.seek(27158456L);
    for (int i = 1; i < offsets.length; i++) {
      raf.writeInt(offsets[i] - 27158456);
    }
    FileUtils.deleteDirectory(Directories.MOD_SPRITE_TEMP.toFile());
  }
  









  public void writeTables()
    throws IOException
  {
    raf.seek(1054488L);
    raf.writeInt(0x2A220000 | highestID + 1 & 0xFFFF);
    









    raf.seek(1051380L);
    raf.writeInt(0x2A020000 | highestID + 1 & 0xFFFF);
    
    raf.seek(Patcher.nextAlignedOffset(raf));
    int ptrTableAddr = Patcher.toAddress(raf.getFilePointer());
    for (int i = 0; i <= highestID; i++)
      raf.writeInt(0);
    int countTableAddr = Patcher.toAddress(raf.getFilePointer());
    for (int i = 0; i < (highestID + 4 & 0xFFFFFFFC); i++) {
      raf.write(0);
    }
    int upperPtr = ptrTableAddr >> 16 & 0xFFFF;
    int lowerPtr = ptrTableAddr & 0xFFFF;
    
    int upperCount = countTableAddr >> 16 & 0xFFFF;
    int lowerCount = countTableAddr & 0xFFFF;
    

    raf.seek(1051348L);
    raf.writeInt(0x3C040000 | upperCount);
    raf.writeInt(0x24840000 | lowerCount);
    raf.writeInt(0x3C030000 | upperPtr);
    raf.writeInt(0x24630000 | lowerPtr);
    

    raf.seek(1053284L);
    raf.writeInt(0x3C020000 | upperPtr);
    raf.writeInt(0x24420000 | lowerPtr);
    

    raf.seek(1053308L);
    raf.writeInt(0x3C030000 | upperCount);
    raf.writeInt(0x24630000 | lowerCount);
    

    raf.seek(1053368L);
    raf.writeInt(0x3C010000 | upperCount);
    raf.skipBytes(4);
    raf.writeInt(0xA0220000 | lowerCount);
    

    raf.seek(1054508L);
    raf.writeInt(0x3C030000 | upperCount);
    raf.writeInt(0x24630000 | lowerCount);
    

    raf.seek(1054596L);
    raf.writeInt(0x3C030000 | upperCount);
    raf.skipBytes(4);
    raf.writeInt(0x90630000 | lowerCount);
    

    raf.seek(1054628L);
    raf.writeInt(0x3C010000 | upperPtr);
    raf.skipBytes(4);
    raf.writeInt(0xAC200000 | lowerPtr);
    

    raf.seek(1055440L);
    raf.writeInt(0x3C040000 | upperPtr);
    raf.skipBytes(4);
    raf.writeInt(0x8C840000 | lowerPtr);
    

    raf.seek(1055540L);
    raf.writeInt(0x3C020000 | upperPtr);
    raf.skipBytes(4);
    raf.writeInt(0x8C420000 | lowerPtr);
    

    raf.seek(1055584L);
    raf.writeInt(0x3C020000 | upperPtr);
    raf.skipBytes(4);
    raf.writeInt(0x8C420000 | lowerPtr);
  }
}

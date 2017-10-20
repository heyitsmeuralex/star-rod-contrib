package data.sound;

import data.shared.DataConstants.ConstEnum;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import main.DevContext;
import util.IOUtils;

public class SoundTesting
{
  private static final int TABLE_BASE = 15728640;
  
  public static void main(String[] args) throws java.io.IOException
  {
    DevContext.initialize();
    new SoundTesting();
    DevContext.exit();
  }
  






























  private SoundTesting()
    throws java.io.IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    raf.seek(15728640L);
    System.out.println(IOUtils.readString(raf, 4));
    System.out.printf("End: %X\r\n", new Object[] { Integer.valueOf(15728640 + raf.readInt()) });
    raf.skipBytes(8);
    
    int tableStart = raf.readInt();
    int numEntries = raf.readInt();
    raf.skipBytes(12);
    int initOffset = raf.readInt();
    
    for (int i = 0; i < numEntries; i++)
    {
      raf.seek(15728640 + tableStart + 8 * i);
      int offset = raf.readInt();
      int word2 = raf.readInt();
      int fmt = word2 >>> 28;
      int len = word2 & 0xFFFFFFF;
      
      raf.seek(15728640 + offset);
      String type = IOUtils.readString(raf, 4);
      System.out.printf("(%2X) %7X %4X %-3s", new Object[] { Integer.valueOf(i), Integer.valueOf(15728640 + offset), Integer.valueOf(len), type.trim() });
      
      int len2 = raf.readInt();
      String name = IOUtils.readString(raf, 4);
      
      if ((!type.equals("BK  ")) && 
        (!$assertionsDisabled) && (len2 != len)) throw new AssertionError();
      System.out.print(" -- '" + name + "'");
      
      switch (type)
      {
      case "BGM ": 
        if ((!$assertionsDisabled) && (fmt != 1)) { throw new AssertionError();
        }
        break;
      case "SEF ": 
        if ((!$assertionsDisabled) && (fmt != 2)) { throw new AssertionError();
        }
        break;
      case "BK  ": 
        if ((!$assertionsDisabled) && (fmt != 3)) { throw new AssertionError();
        }
        break;
      case "PER ": 
        if ((!$assertionsDisabled) && (fmt != 4)) { throw new AssertionError();
        }
        break;
      case "PRG ": 
        if ((!$assertionsDisabled) && (fmt != 4)) { throw new AssertionError();
        }
        break;
      case "MSEQ": 
        if ((!$assertionsDisabled) && (fmt != 4)) { throw new AssertionError();
        }
        break;
      default: 
        throw new RuntimeException("Unknown file type " + type);
      }
      
      
      System.out.println();
    }
    
    int initBase = 15728640 + initOffset;
    raf.seek(initBase);
    String type = IOUtils.readString(raf, 4);
    
    DataConstants.ConstEnum songEnum = data.shared.DataConstants.getFromLibraryName("songID");
    System.out.printf("     %7X %s", new Object[] { Integer.valueOf(initBase), type.trim() });
    System.out.println();
    

    for (int i = 0; i < 64; i++)
    {
      raf.seek(initBase + 32 + 4 * i);
      int bank = raf.readShort();
      int a = raf.readByte();
      int b = raf.readByte();
      
      assert ((bank >= 144) && (bank <= 216));
      System.out.printf("(%02X) %2X %2X %2X\r\n", new Object[] { Integer.valueOf(i), Integer.valueOf(bank), Integer.valueOf(a), Integer.valueOf(b) });
    }
    
    int bgm;
    for (int i = 0; i < 160; i++)
    {
      raf.seek(initBase + 304 + 8 * i);
      bgm = raf.readShort();
      int bank1 = raf.readShort();
      int bank2 = raf.readShort();
      int bank3 = raf.readShort();
      System.out.printf("(%02X) %2X %2X %2X %2X  %s\r\n", new Object[] { Integer.valueOf(i), Integer.valueOf(bgm), Integer.valueOf(bank1), Integer.valueOf(bank2), Integer.valueOf(bank3), songEnum.getName(i) });
      System.out.flush();
      assert ((bgm >= 0) && (bgm <= 142));
      assert ((bank1 == 0) || ((bank1 >= 144) && (bank1 <= 216)));
      assert ((bank2 == 0) || ((bank2 >= 144) && (bank2 <= 216)));
      assert ((bank3 == 0) || ((bank3 >= 144) && (bank3 <= 216)));
    }
    

    for (int i = 0; i < 25; i++)
    {
      raf.seek(initBase + 1600 + 8 * i);
      bgm = raf.readShort();
    }
    
    raf.close();
  }
}

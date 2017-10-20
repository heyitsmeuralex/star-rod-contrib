package patcher;

import java.io.IOException;
import java.io.RandomAccessFile;
import util.Logger;
import util.Priority;

public abstract class VariablePatcher
{
  public VariablePatcher() {}
  
  public static void setPoisonDamage(RandomAccessFile raf, int val)
    throws IOException
  {
    val &= 0xFF;
    raf.seek(1510880L);
    raf.writeInt(0x24040000 | val);
    Logger.log("Poison damage changed to " + val + ".", Priority.IMPORTANT);
  }
  

  public static void setSpikeDamage(RandomAccessFile raf, int val)
    throws IOException
  {
    val &= 0xFF;
    raf.seek(1705384L);
    raf.writeInt(0x24040000 | val);
    Logger.log("Spike damage changed to " + val + ".", Priority.IMPORTANT);
  }
  

  public static void setFireDamage(RandomAccessFile raf, int val)
    throws IOException
  {
    val &= 0xFF;
    raf.seek(1705168L);
    raf.writeInt(0x24040000 | val);
    Logger.log("Fire damage changed to " + val + ".", Priority.IMPORTANT);
  }
}

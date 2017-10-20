package editor.map;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class MapIO
{
  private static final float LOAD_FACTOR = 1.5F;
  private final RandomAccessFile raf;
  private final ReadMode mode;
  
  public static enum ReadMode
  {
    READ,  WRITE;
    
    private ReadMode() {}
  }
  
  private long capacity = 32768L;
  
  public MapIO(File f, ReadMode mode) throws IOException
  {
    this.mode = mode;
    
    if (mode == ReadMode.WRITE)
    {
      raf = new RandomAccessFile(f, "w");
      raf.setLength(capacity);
    }
    else {
      raf = new RandomAccessFile(f, "r");
    }
  }
  
  public void close() throws IOException {
    if (mode == ReadMode.WRITE)
      raf.setLength(raf.getFilePointer());
    raf.close();
  }
  
  private void checkWriteCapacity() throws IOException
  {
    while (raf.getFilePointer() > capacity)
    {
      capacity = (((float)capacity * 1.5F));
      raf.setLength(capacity);
    }
  }
  
  public void writeBoolean(boolean val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeBoolean in READ mode.");
    }
    raf.writeBoolean(val);
    checkWriteCapacity();
  }
  
  public boolean readBoolean() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readBoolean in WRITE mode.");
    }
    return raf.readBoolean();
  }
  
  public void writeLong(long val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeLong in READ mode.");
    }
    raf.writeLong(val);
    checkWriteCapacity();
  }
  
  public long readLong() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readLong in WRITE mode.");
    }
    return raf.readLong();
  }
  
  public void writeInt(int val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeInt in READ mode.");
    }
    raf.writeInt(val);
    checkWriteCapacity();
  }
  
  public int readInt() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readInt in WRITE mode.");
    }
    return raf.readInt();
  }
  
  public void writeShort(short val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeShort in READ mode.");
    }
    raf.writeShort(val);
    checkWriteCapacity();
  }
  
  public short readShort() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readShort in WRITE mode.");
    }
    return raf.readShort();
  }
  
  public void writeByte(byte val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeByte in READ mode.");
    }
    raf.writeByte(val);
    checkWriteCapacity();
  }
  
  public byte readByte() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readByte in WRITE mode.");
    }
    return raf.readByte();
  }
  
  public double readDouble() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readDouble in WRITE mode.");
    }
    return raf.readDouble();
  }
  
  public void writeDouble(double val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeDouble in READ mode.");
    }
    raf.writeDouble(val);
    checkWriteCapacity();
  }
  
  public float readFloat() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readFloat in WRITE mode.");
    }
    return raf.readFloat();
  }
  
  public void writeFloat(float val) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeFloat in READ mode.");
    }
    raf.writeFloat(val);
    checkWriteCapacity();
  }
  
  public void writeString(String s) throws IOException
  {
    if (mode == ReadMode.READ) {
      throw new IOException("Cannot writeString in READ mode.");
    }
    raf.writeUTF(s);
    checkWriteCapacity();
  }
  
  public String readString() throws IOException
  {
    if (mode == ReadMode.WRITE) {
      throw new IOException("Cannot readString in WRITE mode.");
    }
    return raf.readUTF();
  }
  
  public static abstract interface BinarySerializable
  {
    public abstract void writeToBinary(MapIO paramMapIO)
      throws IOException;
    
    public abstract void readFromBinary(MapIO paramMapIO)
      throws IOException;
  }
}

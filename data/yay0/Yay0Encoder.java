package data.yay0;

public class Yay0Encoder {
  private final int decompressedSize;
  private short[] linkBuffer;
  private byte[] maskBuffer;
  private byte[] chunkBuffer;
  private int maskCount;
  private int linkCount;
  private int chunkCount;
  private int mask;
  private int maskBit;
  
  public Yay0Encoder(int length) {
    decompressedSize = length;
    
    maskBuffer = new byte[length];
    maskCount = 0;
    
    linkBuffer = new short[length];
    linkCount = 0;
    
    chunkBuffer = new byte[length];
    chunkCount = 0;
    
    mask = 0;
    maskBit = 128;
  }
  


  public void addCopy(byte b)
  {
    chunkBuffer[chunkCount] = b;
    chunkCount += 1;
    
    addMaskBit(false);
  }
  
  public void addLink(int linkedLength, int distance)
  {
    assert (linkedLength > 2);
    assert (linkedLength < 274);
    assert (distance > 0);
    assert (distance <= 4096);
    


    short link = 0;
    
    linkedLength -= 2;
    distance--;
    
    if (linkedLength > 15)
    {
      linkedLength -= 16;
      
      chunkBuffer[chunkCount] = ((byte)linkedLength);
      chunkCount += 1;
    }
    else
    {
      link = (short)(link + (linkedLength << 12));
    }
    
    link = (short)(link + distance);
    
    linkBuffer[linkCount] = link;
    linkCount += 1;
    
    addMaskBit(true);
  }
  
  private void addMaskBit(boolean linked)
  {
    if (!linked) {
      mask += maskBit;
    }
    maskBit >>>= 1;
    if (maskBit == 0)
    {
      maskBuffer[maskCount] = ((byte)mask);
      maskCount += 1;
      
      mask = 0;
      maskBit = 128;
    }
  }
  
  public void flush()
  {
    if (maskBit == 128) {
      return;
    }
    maskBuffer[maskCount] = ((byte)mask);
    maskCount += 1;
  }
  
  public byte[] getFile()
  {
    int maskSize = maskCount + 3 & 0xFFFFFFFC;
    int linkSize = 2 * linkCount;
    int chunkSize = chunkCount;
    int size = 16 + maskSize + linkSize + chunkSize;
    size = size + 1 & 0xFFFFFFFE;
    
    byte[] buffer = new byte[size];
    putInteger(buffer, 0, 1499560240);
    putInteger(buffer, 4, decompressedSize);
    

    int pos = 16;
    for (int i = 0; i < maskCount; i++)
      buffer[(pos++)] = maskBuffer[i];
    pos = pos + 3 & 0xFFFFFFFC;
    
    putInteger(buffer, 8, pos);
    for (int i = 0; i < linkCount; i++)
    {
      buffer[(pos++)] = ((byte)(linkBuffer[i] >> 8));
      buffer[(pos++)] = ((byte)linkBuffer[i]);
    }
    
    putInteger(buffer, 12, pos);
    for (int i = 0; i < chunkCount; i++) {
      buffer[(pos++)] = chunkBuffer[i];
    }
    return buffer;
  }
  
  private void putInteger(byte[] buffer, int position, int val)
  {
    buffer[(position++)] = ((byte)(val >> 24));
    buffer[(position++)] = ((byte)(val >> 16));
    buffer[(position++)] = ((byte)(val >> 8));
    buffer[position] = ((byte)val);
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append(" Mask Buffer: ");
    for (int i = 0; i < maskCount;)
    {
      sb.append(String.format("%02X", new Object[] { Byte.valueOf(maskBuffer[i]) }));
      i++; if (i % 4 == 0)
        sb.append(" ");
    }
    sb.append("\n");
    
    sb.append(" Link Buffer: ");
    for (int i = 0; i < linkCount;)
    {
      sb.append(String.format("%04X", new Object[] { Short.valueOf(linkBuffer[i]) }));
      i++; if (i % 2 == 0)
        sb.append(" ");
    }
    sb.append("\n");
    
    sb.append("Chunk Buffer: ");
    for (int i = 0; i < chunkCount;)
    {
      sb.append(String.format("%02X", new Object[] { Byte.valueOf(chunkBuffer[i]) }));
      i++; if (i % 4 == 0)
        sb.append(" ");
    }
    sb.append("\n");
    
    return sb.toString();
  }
}

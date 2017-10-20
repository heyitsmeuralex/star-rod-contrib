package data.yay0;





















public final class Yay0Helper
{
  public Yay0Helper() {}
  



















  public static byte[] encode(byte[] source)
  {
    return encode(source, false);
  }
  
  public static byte[] encode(byte[] source, boolean logUpdates)
  {
    if (source.length < 64) {
      throw new IllegalArgumentException("Source is too small to compress!");
    }
    Yay0EncodeHelper helper = new Yay0EncodeHelper(source, logUpdates);
    return helper.getFile();
  }
  





  public static byte[] decode(byte[] source)
  {
    assert (getInteger(source, 0) == 1499560240);
    int decompressedSize = getInteger(source, 4);
    int linkOffset = getInteger(source, 8);
    int sourceOffset = getInteger(source, 12);
    
    byte currentCommand = 0;
    int commandOffset = 16;
    int remainingBits = 0;
    
    byte[] decoded = new byte[decompressedSize];
    int decodedBytes = 0;
    

    do
    {
      if (remainingBits == 0)
      {
        currentCommand = source[commandOffset];
        commandOffset++;
        remainingBits = 8;
      }
      

      if ((currentCommand & 0x80) != 0)
      {


        decoded[decodedBytes] = source[sourceOffset];
        sourceOffset++;
        decodedBytes++;

      }
      else
      {
        short link = getShort(source, linkOffset);
        linkOffset += 2;
        
        int dist = link & 0xFFF;
        int copySrc = decodedBytes - (dist + 1);
        int length = link >> 12 & 0xF;
        

        if (length == 0)
        {
          length = source[sourceOffset] & 0xFF;
          length += 16;
          sourceOffset++;
        }
        
        length += 2;
        



        for (int i = 0; i < length; i++)
        {
          decoded[decodedBytes] = decoded[(copySrc + i)];
          decodedBytes++;
        }
      }
      


      currentCommand = (byte)(currentCommand << 1);
      remainingBits--;
    } while (decodedBytes < decompressedSize);
    
    return decoded;
  }
  
  private static short getShort(byte[] buffer, int start)
  {
    return (short)(buffer[(start + 1)] & 0xFF | (buffer[start] & 0xFF) << 8);
  }
  
  private static int getInteger(byte[] buffer, int start)
  {
    return buffer[(start + 3)] & 0xFF | (buffer[(start + 2)] & 0xFF) << 8 | (buffer[(start + 1)] & 0xFF) << 16 | (buffer[(start + 0)] & 0xFF) << 24;
  }
}

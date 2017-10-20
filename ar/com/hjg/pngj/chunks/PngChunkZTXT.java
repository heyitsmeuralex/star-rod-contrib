package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;





public class PngChunkZTXT
  extends PngChunkTextVar
{
  public static final String ID = "zTXt";
  
  public PngChunkZTXT(ImageInfo info)
  {
    super("zTXt", info);
  }
  
  public ChunkRaw createRawChunk()
  {
    if ((key == null) || (key.trim().length() == 0))
      throw new PngjException("Text chunk key must be non empty");
    try {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(ChunkHelper.toBytes(key));
      ba.write(0);
      ba.write(0);
      byte[] textbytes = ChunkHelper.compressBytes(ChunkHelper.toBytes(val), true);
      ba.write(textbytes);
      byte[] b = ba.toByteArray();
      ChunkRaw chunk = createEmptyChunk(b.length, false);
      data = b;
      return chunk;
    } catch (IOException e) {
      throw new PngjException(e);
    }
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    int nullsep = -1;
    for (int i = 0; i < data.length; i++)
      if (data[i] == 0)
      {
        nullsep = i;
        break;
      }
    if ((nullsep < 0) || (nullsep > data.length - 2))
      throw new PngjException("bad zTXt chunk: no separator found");
    key = ChunkHelper.toString(data, 0, nullsep);
    int compmet = data[(nullsep + 1)];
    if (compmet != 0)
      throw new PngjException("bad zTXt chunk: unknown compression method");
    byte[] uncomp = ChunkHelper.compressBytes(data, nullsep + 2, data.length - nullsep - 2, false);
    
    val = ChunkHelper.toString(uncomp);
  }
}

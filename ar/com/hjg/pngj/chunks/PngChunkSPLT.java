package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;







public class PngChunkSPLT
  extends PngChunkMultiple
{
  public static final String ID = "sPLT";
  private String palName;
  private int sampledepth;
  private int[] palette;
  
  public PngChunkSPLT(ImageInfo info)
  {
    super("sPLT", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    try {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(ChunkHelper.toBytes(palName));
      ba.write(0);
      ba.write((byte)sampledepth);
      int nentries = getNentries();
      for (int n = 0; n < nentries; n++) {
        for (int i = 0; i < 4; i++) {
          if (sampledepth == 8) {
            PngHelperInternal.writeByte(ba, (byte)palette[(n * 5 + i)]);
          } else
            PngHelperInternal.writeInt2(ba, palette[(n * 5 + i)]);
        }
        PngHelperInternal.writeInt2(ba, palette[(n * 5 + 4)]);
      }
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
    int t = -1;
    for (int i = 0; i < data.length; i++) {
      if (data[i] == 0) {
        t = i;
        break;
      }
    }
    if ((t <= 0) || (t > data.length - 2))
      throw new PngjException("bad sPLT chunk: no separator found");
    palName = ChunkHelper.toString(data, 0, t);
    sampledepth = PngHelperInternal.readInt1fromByte(data, t + 1);
    t += 2;
    int nentries = (data.length - t) / (sampledepth == 8 ? 6 : 10);
    palette = new int[nentries * 5];
    
    int ne = 0;
    for (int i = 0; i < nentries; i++) { int a;
      int r; int g; int b; int a; if (sampledepth == 8) {
        int r = PngHelperInternal.readInt1fromByte(data, t++);
        int g = PngHelperInternal.readInt1fromByte(data, t++);
        int b = PngHelperInternal.readInt1fromByte(data, t++);
        a = PngHelperInternal.readInt1fromByte(data, t++);
      } else {
        r = PngHelperInternal.readInt2fromBytes(data, t);
        t += 2;
        g = PngHelperInternal.readInt2fromBytes(data, t);
        t += 2;
        b = PngHelperInternal.readInt2fromBytes(data, t);
        t += 2;
        a = PngHelperInternal.readInt2fromBytes(data, t);
        t += 2;
      }
      int f = PngHelperInternal.readInt2fromBytes(data, t);
      t += 2;
      palette[(ne++)] = r;
      palette[(ne++)] = g;
      palette[(ne++)] = b;
      palette[(ne++)] = a;
      palette[(ne++)] = f;
    }
  }
  
  public int getNentries() {
    return palette.length / 5;
  }
  
  public String getPalName() {
    return palName;
  }
  
  public void setPalName(String palName) {
    this.palName = palName;
  }
  
  public int getSampledepth() {
    return sampledepth;
  }
  
  public void setSampledepth(int sampledepth) {
    this.sampledepth = sampledepth;
  }
  
  public int[] getPalette() {
    return palette;
  }
  
  public void setPalette(int[] palette) {
    this.palette = palette;
  }
}

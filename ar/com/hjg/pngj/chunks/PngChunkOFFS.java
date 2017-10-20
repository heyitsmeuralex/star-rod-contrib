package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;





public class PngChunkOFFS
  extends PngChunkSingle
{
  public static final String ID = "oFFs";
  private long posX;
  private long posY;
  private int units;
  
  public PngChunkOFFS(ImageInfo info)
  {
    super("oFFs", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(9, true);
    PngHelperInternal.writeInt4tobytes((int)posX, data, 0);
    PngHelperInternal.writeInt4tobytes((int)posY, data, 4);
    data[8] = ((byte)units);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    if (len != 9)
      throw new PngjException("bad chunk length " + chunk);
    posX = PngHelperInternal.readInt4fromBytes(data, 0);
    if (posX < 0L)
      posX += 4294967296L;
    posY = PngHelperInternal.readInt4fromBytes(data, 4);
    if (posY < 0L)
      posY += 4294967296L;
    units = PngHelperInternal.readInt1fromByte(data, 8);
  }
  


  public int getUnits()
  {
    return units;
  }
  


  public void setUnits(int units)
  {
    this.units = units;
  }
  
  public long getPosX() {
    return posX;
  }
  
  public void setPosX(long posX) {
    this.posX = posX;
  }
  
  public long getPosY() {
    return posY;
  }
  
  public void setPosY(long posY) {
    this.posY = posY;
  }
}

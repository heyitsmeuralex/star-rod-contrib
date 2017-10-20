package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;
import java.util.Calendar;

public class PngChunkTIME
  extends PngChunkSingle
{
  public static final String ID = "tIME";
  private int year;
  private int mon;
  private int day;
  private int hour;
  private int min;
  private int sec;
  
  public PngChunkTIME(ImageInfo info)
  {
    super("tIME", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NONE;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(7, true);
    PngHelperInternal.writeInt2tobytes(year, data, 0);
    data[2] = ((byte)mon);
    data[3] = ((byte)day);
    data[4] = ((byte)hour);
    data[5] = ((byte)min);
    data[6] = ((byte)sec);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    if (len != 7)
      throw new PngjException("bad chunk " + chunk);
    year = PngHelperInternal.readInt2fromBytes(data, 0);
    mon = PngHelperInternal.readInt1fromByte(data, 2);
    day = PngHelperInternal.readInt1fromByte(data, 3);
    hour = PngHelperInternal.readInt1fromByte(data, 4);
    min = PngHelperInternal.readInt1fromByte(data, 5);
    sec = PngHelperInternal.readInt1fromByte(data, 6);
  }
  
  public void setNow(int secsAgo) {
    Calendar d = Calendar.getInstance();
    d.setTimeInMillis(System.currentTimeMillis() - 1000L * secsAgo);
    year = d.get(1);
    mon = (d.get(2) + 1);
    day = d.get(5);
    hour = d.get(11);
    min = d.get(12);
    sec = d.get(13);
  }
  
  public void setYMDHMS(int yearx, int monx, int dayx, int hourx, int minx, int secx) {
    year = yearx;
    mon = monx;
    day = dayx;
    hour = hourx;
    min = minx;
    sec = secx;
  }
  
  public int[] getYMDHMS() {
    return new int[] { year, mon, day, hour, min, sec };
  }
  
  public String getAsString()
  {
    return String.format("%04d/%02d/%02d %02d:%02d:%02d", new Object[] { Integer.valueOf(year), Integer.valueOf(mon), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(min), Integer.valueOf(sec) });
  }
}

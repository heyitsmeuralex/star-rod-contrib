package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;
import ar.com.hjg.pngj.PngjInputException;
import java.io.ByteArrayInputStream;









public class PngChunkIHDR
  extends PngChunkSingle
{
  public static final String ID = "IHDR";
  private int cols;
  private int rows;
  private int bitspc;
  private int colormodel;
  private int compmeth;
  private int filmeth;
  private int interlaced;
  
  public PngChunkIHDR(ImageInfo info)
  {
    super("IHDR", info);
    if (info != null) {
      fillFromInfo(info);
    }
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NA;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = new ChunkRaw(13, ChunkHelper.b_IHDR, true);
    int offset = 0;
    PngHelperInternal.writeInt4tobytes(cols, data, offset);
    offset += 4;
    PngHelperInternal.writeInt4tobytes(rows, data, offset);
    offset += 4;
    data[(offset++)] = ((byte)bitspc);
    data[(offset++)] = ((byte)colormodel);
    data[(offset++)] = ((byte)compmeth);
    data[(offset++)] = ((byte)filmeth);
    data[(offset++)] = ((byte)interlaced);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (len != 13)
      throw new PngjException("Bad IDHR len " + len);
    ByteArrayInputStream st = c.getAsByteStream();
    cols = PngHelperInternal.readInt4(st);
    rows = PngHelperInternal.readInt4(st);
    
    bitspc = PngHelperInternal.readByte(st);
    colormodel = PngHelperInternal.readByte(st);
    compmeth = PngHelperInternal.readByte(st);
    filmeth = PngHelperInternal.readByte(st);
    interlaced = PngHelperInternal.readByte(st);
  }
  
  public int getCols() {
    return cols;
  }
  
  public void setCols(int cols) {
    this.cols = cols;
  }
  
  public int getRows() {
    return rows;
  }
  
  public void setRows(int rows) {
    this.rows = rows;
  }
  
  public int getBitspc() {
    return bitspc;
  }
  
  public void setBitspc(int bitspc) {
    this.bitspc = bitspc;
  }
  
  public int getColormodel() {
    return colormodel;
  }
  
  public void setColormodel(int colormodel) {
    this.colormodel = colormodel;
  }
  
  public int getCompmeth() {
    return compmeth;
  }
  
  public void setCompmeth(int compmeth) {
    this.compmeth = compmeth;
  }
  
  public int getFilmeth() {
    return filmeth;
  }
  
  public void setFilmeth(int filmeth) {
    this.filmeth = filmeth;
  }
  
  public int getInterlaced() {
    return interlaced;
  }
  
  public void setInterlaced(int interlaced) {
    this.interlaced = interlaced;
  }
  
  public boolean isInterlaced() {
    return getInterlaced() == 1;
  }
  
  public void fillFromInfo(ImageInfo info) {
    setCols(imgInfo.cols);
    setRows(imgInfo.rows);
    setBitspc(imgInfo.bitDepth);
    int colormodel = 0;
    if (imgInfo.alpha)
      colormodel += 4;
    if (imgInfo.indexed)
      colormodel++;
    if (!imgInfo.greyscale)
      colormodel += 2;
    setColormodel(colormodel);
    setCompmeth(0);
    setFilmeth(0);
    setInterlaced(0);
  }
  
  public ImageInfo createImageInfo()
  {
    check();
    boolean alpha = (getColormodel() & 0x4) != 0;
    boolean palette = (getColormodel() & 0x1) != 0;
    boolean grayscale = (getColormodel() == 0) || (getColormodel() == 4);
    
    return new ImageInfo(getCols(), getRows(), getBitspc(), alpha, grayscale, palette);
  }
  
  public void check() {
    if ((cols < 1) || (rows < 1) || (compmeth != 0) || (filmeth != 0))
      throw new PngjInputException("bad IHDR: col/row/compmethod/filmethod invalid");
    if ((bitspc != 1) && (bitspc != 2) && (bitspc != 4) && (bitspc != 8) && (bitspc != 16))
      throw new PngjInputException("bad IHDR: bitdepth invalid");
    if ((interlaced < 0) || (interlaced > 1))
      throw new PngjInputException("bad IHDR: interlace invalid");
    switch (colormodel) {
    case 0: 
      break;
    case 3: 
      if (bitspc == 16) {
        throw new PngjInputException("bad IHDR: bitdepth invalid");
      }
      break;
    case 2: case 4: 
    case 6: 
      if ((bitspc != 8) && (bitspc != 16))
        throw new PngjInputException("bad IHDR: bitdepth invalid");
      break;
    case 1: case 5: default: 
      throw new PngjInputException("bad IHDR: invalid colormodel");
    }
  }
}

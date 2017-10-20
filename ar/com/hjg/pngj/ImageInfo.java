package ar.com.hjg.pngj;

import java.util.zip.Checksum;








































































public class ImageInfo
{
  public static final int MAX_COLS_ROW = 16777216;
  public final int cols;
  public final int rows;
  public final int bitDepth;
  public final int channels;
  public final boolean alpha;
  public final boolean greyscale;
  public final boolean indexed;
  public final boolean packed;
  public final int bitspPixel;
  public final int bytesPixel;
  public final int bytesPerRow;
  public final int samplesPerRow;
  public final int samplesPerRowPacked;
  private long totalPixels = -1L;
  
  private long totalRawBytes = -1L;
  


  public ImageInfo(int cols, int rows, int bitdepth, boolean alpha)
  {
    this(cols, rows, bitdepth, alpha, false, false);
  }
  










  public ImageInfo(int cols, int rows, int bitdepth, boolean alpha, boolean grayscale, boolean indexed)
  {
    this.cols = cols;
    this.rows = rows;
    this.alpha = alpha;
    this.indexed = indexed;
    greyscale = grayscale;
    if ((greyscale) && (indexed))
      throw new PngjException("palette and greyscale are mutually exclusive");
    channels = (alpha ? 4 : (grayscale) || (indexed) ? 1 : alpha ? 2 : 3);
    
    bitDepth = bitdepth;
    packed = (bitdepth < 8);
    bitspPixel = (channels * bitDepth);
    bytesPixel = ((bitspPixel + 7) / 8);
    bytesPerRow = ((bitspPixel * cols + 7) / 8);
    samplesPerRow = (channels * this.cols);
    samplesPerRowPacked = (packed ? bytesPerRow : samplesPerRow);
    
    switch (bitDepth) {
    case 1: 
    case 2: 
    case 4: 
      if ((!this.indexed) && (!greyscale))
        throw new PngjException("only indexed or grayscale can have bitdepth=" + bitDepth);
      break;
    case 8: 
      break;
    case 16: 
      if (this.indexed)
        throw new PngjException("indexed can't have bitdepth=" + bitDepth);
      break;
    default: 
      throw new PngjException("invalid bitdepth=" + bitDepth);
    }
    if ((cols < 1) || (cols > 16777216))
      throw new PngjException("invalid cols=" + cols + " ???");
    if ((rows < 1) || (rows > 16777216))
      throw new PngjException("invalid rows=" + rows + " ???");
    if (samplesPerRow < 1) {
      throw new PngjException("invalid image parameters (overflow?)");
    }
  }
  





  public ImageInfo withSize(int cols, int rows)
  {
    return new ImageInfo(cols > 0 ? cols : this.cols, rows > 0 ? rows : this.rows, bitDepth, alpha, greyscale, indexed);
  }
  
  public long getTotalPixels()
  {
    if (totalPixels < 0L)
      totalPixels = (cols * rows);
    return totalPixels;
  }
  


  public long getTotalRawBytes()
  {
    if (totalRawBytes < 0L)
      totalRawBytes = ((bytesPerRow + 1) * rows);
    return totalRawBytes;
  }
  
  public String toString()
  {
    return "ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels=" + channels + ", alpha=" + alpha + ", greyscale=" + greyscale + ", indexed=" + indexed + "]";
  }
  



  public String toStringBrief()
  {
    return String.valueOf(cols) + "x" + rows + (bitDepth != 8 ? "d" + bitDepth : "") + (alpha ? "a" : "") + (indexed ? "p" : "") + (greyscale ? "g" : "");
  }
  
  public String toStringDetail()
  {
    return "ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels=" + channels + ", bitspPixel=" + bitspPixel + ", bytesPixel=" + bytesPixel + ", bytesPerRow=" + bytesPerRow + ", samplesPerRow=" + samplesPerRow + ", samplesPerRowP=" + samplesPerRowPacked + ", alpha=" + alpha + ", greyscale=" + greyscale + ", indexed=" + indexed + ", packed=" + packed + "]";
  }
  




  void updateCrc(Checksum crc)
  {
    crc.update((byte)rows);
    crc.update((byte)(rows >> 8));
    crc.update((byte)(rows >> 16));
    crc.update((byte)cols);
    crc.update((byte)(cols >> 8));
    crc.update((byte)(cols >> 16));
    crc.update((byte)bitDepth);
    crc.update((byte)(indexed ? 1 : 2));
    crc.update((byte)(greyscale ? 3 : 4));
    crc.update((byte)(alpha ? 3 : 4));
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (alpha ? 1231 : 1237);
    result = 31 * result + bitDepth;
    result = 31 * result + cols;
    result = 31 * result + (greyscale ? 1231 : 1237);
    result = 31 * result + (indexed ? 1231 : 1237);
    result = 31 * result + rows;
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ImageInfo other = (ImageInfo)obj;
    if (alpha != alpha)
      return false;
    if (bitDepth != bitDepth)
      return false;
    if (cols != cols)
      return false;
    if (greyscale != greyscale)
      return false;
    if (indexed != indexed)
      return false;
    if (rows != rows)
      return false;
    return true;
  }
}

package ar.com.hjg.pngj;

import ar.com.hjg.pngj.pixels.PixelsWriter;
import ar.com.hjg.pngj.pixels.PixelsWriterMultiple;
import java.io.File;
import java.io.OutputStream;

public class PngWriterHc
  extends PngWriter
{
  public PngWriterHc(File file, ImageInfo imgInfo, boolean allowoverwrite)
  {
    super(file, imgInfo, allowoverwrite);
    setFilterType(FilterType.FILTER_SUPER_ADAPTIVE);
  }
  
  public PngWriterHc(File file, ImageInfo imgInfo) {
    super(file, imgInfo);
  }
  
  public PngWriterHc(OutputStream outputStream, ImageInfo imgInfo) {
    super(outputStream, imgInfo);
  }
  
  protected PixelsWriter createPixelsWriter(ImageInfo imginfo)
  {
    PixelsWriterMultiple pw = new PixelsWriterMultiple(imginfo);
    return pw;
  }
  
  public PixelsWriterMultiple getPixelWriterMultiple() {
    return (PixelsWriterMultiple)pixelsWriter;
  }
}

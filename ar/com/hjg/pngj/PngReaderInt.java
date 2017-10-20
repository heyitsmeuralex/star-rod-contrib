package ar.com.hjg.pngj;

import java.io.File;
import java.io.InputStream;







public class PngReaderInt
  extends PngReader
{
  public PngReaderInt(File file)
  {
    super(file);
  }
  
  public PngReaderInt(InputStream inputStream) {
    super(inputStream);
  }
  





  public ImageLineInt readRowInt()
  {
    IImageLine line = readRow();
    if ((line instanceof ImageLineInt)) {
      return (ImageLineInt)line;
    }
    throw new PngjException("This is not a ImageLineInt : " + line.getClass());
  }
}

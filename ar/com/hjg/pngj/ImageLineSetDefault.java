package ar.com.hjg.pngj;

import java.util.ArrayList;
import java.util.List;



public abstract class ImageLineSetDefault<T extends IImageLine>
  implements IImageLineSet<T>
{
  protected final ImageInfo imgInfo;
  private final boolean singleCursor;
  private final int nlines;
  private final int offset;
  private final int step;
  protected List<T> imageLines;
  protected T imageLine;
  protected int currentRow = -1;
  
  public ImageLineSetDefault(ImageInfo imgInfo, boolean singleCursor, int nlinesx, int noffsetx, int stepx)
  {
    this.imgInfo = imgInfo;
    this.singleCursor = singleCursor;
    if (singleCursor) {
      nlines = 1;
      offset = 0;
      step = 1;
    } else {
      nlines = nlinesx;
      offset = noffsetx;
      step = stepx;
    }
    createImageLines();
  }
  
  private void createImageLines() {
    if (singleCursor) {
      imageLine = createImageLine();
    } else {
      imageLines = new ArrayList();
      for (int i = 0; i < nlines; i++) {
        imageLines.add(createImageLine());
      }
    }
  }
  



  protected abstract T createImageLine();
  


  public T getImageLine(int n)
  {
    currentRow = n;
    if (singleCursor) {
      return imageLine;
    }
    int r = imageRowToMatrixRowStrict(n);
    if (r < 0)
      throw new PngjException("Invalid row number");
    return (IImageLine)imageLines.get(r);
  }
  



  public T getImageLineRawNum(int r)
  {
    if (singleCursor) {
      return imageLine;
    }
    return (IImageLine)imageLines.get(r);
  }
  






  public boolean hasImageLine(int n)
  {
    return currentRow == n;
  }
  


  public int size()
  {
    return nlines;
  }
  


  public int imageRowToMatrixRowStrict(int imrow)
  {
    imrow -= offset;
    int mrow = (imrow >= 0) && ((step == 1) || (imrow % step == 0)) ? imrow / step : -1;
    return mrow < nlines ? mrow : -1;
  }
  





  public int matrixRowToImageRow(int mrow)
  {
    return mrow * step + offset;
  }
  






  public int imageRowToMatrixRow(int imrow)
  {
    int r = (imrow - offset) / step;
    return r < nlines ? r : r < 0 ? 0 : nlines - 1;
  }
  

  public static <T extends IImageLine> IImageLineSetFactory<T> createImageLineSetFactoryFromImageLineFactory(IImageLineFactory<T> ifactory)
  {
    new IImageLineSetFactory()
    {
      public IImageLineSet<T> create(final ImageInfo iminfo, boolean singleCursor, int nlines, int noffset, int step) {
        new ImageLineSetDefault(iminfo, singleCursor, nlines, noffset, step)
        {
          protected T createImageLine() {
            return val$ifactory.createImageLine(iminfo);
          }
        };
      }
    };
  }
  
  public static IImageLineSetFactory<ImageLineInt> getFactoryInt()
  {
    return createImageLineSetFactoryFromImageLineFactory(ImageLineInt.getFactory());
  }
  
  public static IImageLineSetFactory<ImageLineByte> getFactoryByte()
  {
    return createImageLineSetFactoryFromImageLineFactory(ImageLineByte.getFactory());
  }
}

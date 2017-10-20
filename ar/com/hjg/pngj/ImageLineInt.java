package ar.com.hjg.pngj;







public class ImageLineInt
  implements IImageLine, IImageLineArray
{
  public final ImageInfo imgInfo;
  





  protected final int[] scanline;
  





  protected final int size;
  




  protected FilterType filterType = FilterType.FILTER_UNKNOWN;
  


  public ImageLineInt(ImageInfo imgInfo)
  {
    this(imgInfo, null);
  }
  



  public ImageLineInt(ImageInfo imgInfo, int[] sci)
  {
    this.imgInfo = imgInfo;
    filterType = FilterType.FILTER_UNKNOWN;
    size = samplesPerRow;
    scanline = ((sci != null) && (sci.length >= size) ? sci : new int[size]);
  }
  



  public static IImageLineFactory<ImageLineInt> getFactory()
  {
    new IImageLineFactory() {
      public ImageLineInt createImageLine(ImageInfo iminfo) {
        return new ImageLineInt(iminfo);
      }
    };
  }
  
  public FilterType getFilterType() {
    return filterType;
  }
  


  public void setFilterType(FilterType ft)
  {
    filterType = ft;
  }
  


  public String toString()
  {
    return " cols=" + imgInfo.cols + " bpc=" + imgInfo.bitDepth + " size=" + scanline.length;
  }
  
  public void readFromPngRaw(byte[] raw, int len, int offset, int step) {
    setFilterType(FilterType.getByVal(raw[0]));
    int len1 = len - 1;
    int step1 = (step - 1) * imgInfo.channels;
    if (imgInfo.bitDepth == 8) {
      if (step == 1) {
        for (int i = 0; i < size; i++) {
          scanline[i] = (raw[(i + 1)] & 0xFF);
        }
      } else {
        int s = 1;int c = 0; for (int i = offset * imgInfo.channels; s <= len1; i++) {
          scanline[i] = (raw[s] & 0xFF);
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
          s++;

        }
        

      }
      

    }
    else if (imgInfo.bitDepth == 16) {
      if (step == 1) {
        int i = 0; for (int s = 1; i < size; i++) {
          scanline[i] = ((raw[(s++)] & 0xFF) << 8 | raw[(s++)] & 0xFF);
        }
      } else {
        int s = 1;int c = 0; for (int i = offset != 0 ? offset * imgInfo.channels : 0; s <= len1; i++) {
          scanline[i] = ((raw[(s++)] & 0xFF) << 8 | raw[s] & 0xFF);
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
          s++;

        }
        
      }
      

    }
    else
    {

      int bd = imgInfo.bitDepth;
      int mask0 = ImageLineHelper.getMaskForPackedFormats(bd);
      int i = offset * imgInfo.channels;int r = 1; for (int c = 0; r < len; r++) {
        int mask = mask0;
        int shi = 8 - bd;
        do {
          scanline[(i++)] = ((raw[r] & mask) >> shi);
          mask >>= bd;
          shi -= bd;
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
        } while ((mask != 0) && (i < size));
      }
    }
  }
  
  public void writeToPngRaw(byte[] raw) {
    raw[0] = ((byte)filterType.val);
    if (imgInfo.bitDepth == 8) {
      for (int i = 0; i < size; i++) {
        raw[(i + 1)] = ((byte)scanline[i]);
      }
    } else if (imgInfo.bitDepth == 16) {
      int i = 0; for (int s = 1; i < size; i++) {
        raw[(s++)] = ((byte)(scanline[i] >> 8));
        raw[(s++)] = ((byte)(scanline[i] & 0xFF));
      }
    }
    else {
      int bd = imgInfo.bitDepth;
      int shi = 8 - bd;
      int v = 0;
      int i = 0; for (int r = 1; i < size; i++) {
        v |= scanline[i] << shi;
        shi -= bd;
        if ((shi < 0) || (i == size - 1)) {
          raw[(r++)] = ((byte)v);
          shi = 8 - bd;
          v = 0;
        }
      }
    }
  }
  




  public void endReadFromPngRaw() {}
  



  public int getSize()
  {
    return size;
  }
  
  public int getElem(int i) {
    return scanline[i];
  }
  


  public int[] getScanline()
  {
    return scanline;
  }
  
  public ImageInfo getImageInfo() {
    return imgInfo;
  }
}

package ar.com.hjg.pngj;



public class ImageLineByte
  implements IImageLine, IImageLineArray
{
  public final ImageInfo imgInfo;
  

  final byte[] scanline;
  

  final byte[] scanline2;
  

  protected FilterType filterType;
  
  final int size;
  

  public ImageLineByte(ImageInfo imgInfo)
  {
    this(imgInfo, null);
  }
  
  public ImageLineByte(ImageInfo imgInfo, byte[] sci) {
    this.imgInfo = imgInfo;
    filterType = FilterType.FILTER_UNKNOWN;
    size = samplesPerRow;
    scanline = ((sci != null) && (sci.length >= size) ? sci : new byte[size]);
    scanline2 = (bitDepth == 16 ? new byte[size] : null);
  }
  


  public static IImageLineFactory<ImageLineByte> getFactory()
  {
    new IImageLineFactory() {
      public ImageLineByte createImageLine(ImageInfo iminfo) {
        return new ImageLineByte(iminfo);
      }
    };
  }
  
  public FilterType getFilterUsed() {
    return filterType;
  }
  



  public byte[] getScanlineByte()
  {
    return scanline;
  }
  




  public byte[] getScanlineByte2()
  {
    return scanline2;
  }
  


  public String toString()
  {
    return " cols=" + imgInfo.cols + " bpc=" + imgInfo.bitDepth + " size=" + scanline.length;
  }
  
  public void readFromPngRaw(byte[] raw, int len, int offset, int step) {
    filterType = FilterType.getByVal(raw[0]);
    int len1 = len - 1;
    int step1 = (step - 1) * imgInfo.channels;
    if (imgInfo.bitDepth == 8) {
      if (step == 1) {
        System.arraycopy(raw, 1, scanline, 0, len1);
      } else {
        int s = 1;int c = 0; for (int i = offset * imgInfo.channels; s <= len1; i++) {
          scanline[i] = raw[s];
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
        int i = 0; for (int s = 1; i < imgInfo.samplesPerRow; i++) {
          scanline[i] = raw[(s++)];
          scanline2[i] = raw[(s++)];
        }
      } else {
        int s = 1;int c = 0; for (int i = offset != 0 ? offset * imgInfo.channels : 0; s <= len1; i++) {
          scanline[i] = raw[(s++)];
          scanline2[i] = raw[(s++)];
          c++;
          if (c == imgInfo.channels) {
            c = 0;
            i += step1;
          }
        }
      }
    }
    else {
      int bd = imgInfo.bitDepth;
      int mask0 = ImageLineHelper.getMaskForPackedFormats(bd);
      int i = offset * imgInfo.channels;int r = 1; for (int c = 0; r < len; r++) {
        int mask = mask0;
        int shi = 8 - bd;
        do {
          scanline[i] = ((byte)((raw[r] & mask) >> shi));
          mask >>= bd;
          shi -= bd;
          i++;
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
      System.arraycopy(scanline, 0, raw, 1, size);
    } else if (imgInfo.bitDepth == 16) {
      int i = 0; for (int s = 1; i < size; i++) {
        raw[(s++)] = scanline[i];
        raw[(s++)] = scanline2[i];
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
  
  public int getSize() {
    return size;
  }
  
  public int getElem(int i) {
    return scanline2 == null ? scanline[i] & 0xFF : (scanline[i] & 0xFF) << 8 | scanline2[i] & 0xFF;
  }
  
  public byte[] getScanline()
  {
    return scanline;
  }
  
  public ImageInfo getImageInfo() {
    return imgInfo;
  }
  
  public FilterType getFilterType() {
    return filterType;
  }
  


  public void setFilterType(FilterType ft)
  {
    filterType = ft;
  }
}

package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.PngChunkPLTE;
import ar.com.hjg.pngj.chunks.PngChunkTRNS;
import java.util.Arrays;











public class ImageLineHelper
{
  static int[] DEPTH_UNPACK_1;
  static int[] DEPTH_UNPACK_2;
  static int[] DEPTH_UNPACK_4;
  static int[][] DEPTH_UNPACK;
  
  public ImageLineHelper() {}
  
  private static void initDepthScale()
  {
    DEPTH_UNPACK_1 = new int[2];
    for (int i = 0; i < 2; i++)
      DEPTH_UNPACK_1[i] = (i * 255);
    DEPTH_UNPACK_2 = new int[4];
    for (int i = 0; i < 4; i++)
      DEPTH_UNPACK_2[i] = (i * 255 / 3);
    DEPTH_UNPACK_4 = new int[16];
    for (int i = 0; i < 16; i++)
      DEPTH_UNPACK_4[i] = (i * 255 / 15);
    DEPTH_UNPACK = new int[][] { null, DEPTH_UNPACK_1, DEPTH_UNPACK_2, null, DEPTH_UNPACK_4 };
  }
  



  public static void scaleUp(IImageLineArray line)
  {
    if ((getImageInfoindexed) || (getImageInfobitDepth >= 8))
      return;
    int[] scaleArray = DEPTH_UNPACK[getImageInfobitDepth];
    if ((line instanceof ImageLineInt)) {
      ImageLineInt iline = (ImageLineInt)line;
      for (int i = 0; i < iline.getSize(); i++)
        scanline[i] = scaleArray[scanline[i]];
    } else if ((line instanceof ImageLineByte)) {
      ImageLineByte iline = (ImageLineByte)line;
      for (int i = 0; i < iline.getSize(); i++)
        scanline[i] = ((byte)scaleArray[scanline[i]]);
    } else {
      throw new PngjException("not implemented");
    }
  }
  

  public static void scaleDown(IImageLineArray line)
  {
    if ((getImageInfoindexed) || (getImageInfobitDepth >= 8))
      return;
    if ((line instanceof ImageLineInt)) {
      int scalefactor = 8 - getImageInfobitDepth;
      if ((line instanceof ImageLineInt)) {
        ImageLineInt iline = (ImageLineInt)line;
        for (int i = 0; i < line.getSize(); i++)
          scanline[i] >>= scalefactor;
      } else if ((line instanceof ImageLineByte)) {
        ImageLineByte iline = (ImageLineByte)line;
        for (int i = 0; i < line.getSize(); i++)
          scanline[i] = ((byte)((scanline[i] & 0xFF) >> scalefactor));
      }
    } else {
      throw new PngjException("not implemented");
    }
  }
  
  public static byte scaleUp(int bitdepth, byte v) { return bitdepth < 8 ? (byte)DEPTH_UNPACK[bitdepth][v] : v; }
  
  public static byte scaleDown(int bitdepth, byte v)
  {
    return bitdepth < 8 ? (byte)(v >> 8 - bitdepth) : v;
  }
  








  public static int[] palette2rgb(ImageLineInt line, PngChunkPLTE pal, PngChunkTRNS trns, int[] buf)
  {
    return palette2rgb(line, pal, trns, buf, false);
  }
  


  static int[] lineToARGB32(ImageLineByte line, PngChunkPLTE pal, PngChunkTRNS trns, int[] buf)
  {
    boolean alphachannel = imgInfo.alpha;
    int cols = getImageInfocols;
    if ((buf == null) || (buf.length < cols)) {
      buf = new int[cols];
    }
    if (getImageInfoindexed) {
      int nindexesWithAlpha = trns != null ? trns.getPalletteAlpha().length : 0;
      for (int c = 0; c < cols; c++) {
        int index = scanline[c] & 0xFF;
        int rgb = pal.getEntry(index);
        int alpha = index < nindexesWithAlpha ? trns.getPalletteAlpha()[index] : 255;
        buf[c] = (alpha << 24 | rgb);
      }
    } else if (imgInfo.greyscale) {
      int ga = trns != null ? trns.getGray() : -1;
      int c = 0; for (int c2 = 0; c < cols; c++) {
        int g = scanline[(c2++)] & 0xFF;
        int alpha = g != ga ? 255 : alphachannel ? scanline[(c2++)] & 0xFF : 0;
        buf[c] = (alpha << 24 | g | g << 8 | g << 16);
      }
    } else {
      int ga = trns != null ? trns.getRGB888() : -1;
      int c = 0; for (int c2 = 0; c < cols; c++) {
        int rgb = (scanline[(c2++)] & 0xFF) << 16 | (scanline[(c2++)] & 0xFF) << 8 | scanline[(c2++)] & 0xFF;
        

        int alpha = rgb != ga ? 255 : alphachannel ? scanline[(c2++)] & 0xFF : 0;
        buf[c] = (alpha << 24 | rgb);
      }
    }
    return buf;
  }
  


  static byte[] lineToRGBA8888(ImageLineByte line, PngChunkPLTE pal, PngChunkTRNS trns, byte[] buf)
  {
    boolean alphachannel = imgInfo.alpha;
    int cols = imgInfo.cols;
    int bytes = cols * 4;
    if ((buf == null) || (buf.length < bytes))
      buf = new byte[bytes];
    int c;
    int b;
    if (imgInfo.indexed) {
      int nindexesWithAlpha = trns != null ? trns.getPalletteAlpha().length : 0;
      int c = 0; for (int b = 0; c < cols; c++) {
        int index = scanline[c] & 0xFF;
        int rgb = pal.getEntry(index);
        buf[(b++)] = ((byte)(rgb >> 16 & 0xFF));
        buf[(b++)] = ((byte)(rgb >> 8 & 0xFF));
        buf[(b++)] = ((byte)(rgb & 0xFF));
        buf[(b++)] = ((byte)(index < nindexesWithAlpha ? trns.getPalletteAlpha()[index] : 'ÿ')); } } else { int ga;
      int c;
      int b; if (imgInfo.greyscale) {
        ga = trns != null ? trns.getGray() : -1;
        c = 0; for (b = 0; b < bytes;) {
          byte val = scanline[(c++)];
          buf[(b++)] = val;
          buf[(b++)] = val;
          buf[(b++)] = val;
          buf[(b++)] = ((val & 0xFF) == ga ? 0 : alphachannel ? scanline[(c++)] : -1);
        }
        
      }
      else if (alphachannel) {
        System.arraycopy(scanline, 0, buf, 0, bytes);
      } else {
        c = 0; for (b = 0; b < bytes;) {
          buf[(b++)] = scanline[(c++)];
          buf[(b++)] = scanline[(c++)];
          buf[(b++)] = scanline[(c++)];
          buf[(b++)] = -1;
          if ((trns != null) && (buf[(b - 3)] == (byte)trns.getRGB()[0]) && (buf[(b - 2)] == (byte)trns.getRGB()[1]) && (buf[(b - 1)] == (byte)trns.getRGB()[2]))
          {





            buf[(b - 1)] = 0; }
        }
      }
    }
    return buf;
  }
  
  static byte[] lineToRGB888(ImageLineByte line, PngChunkPLTE pal, byte[] buf) {
    boolean alphachannel = imgInfo.alpha;
    int cols = imgInfo.cols;
    int bytes = cols * 3;
    if ((buf == null) || (buf.length < bytes)) {
      buf = new byte[bytes];
    }
    int[] rgb = new int[3];
    int c; int b; if (imgInfo.indexed) {
      int c = 0; for (int b = 0; c < cols; c++) {
        pal.getEntryRgb(scanline[c] & 0xFF, rgb);
        buf[(b++)] = ((byte)rgb[0]);
        buf[(b++)] = ((byte)rgb[1]);
        buf[(b++)] = ((byte)rgb[2]); } } else { int c;
      int b;
      if (imgInfo.greyscale) {
        c = 0; for (b = 0; b < bytes;) {
          byte val = scanline[(c++)];
          buf[(b++)] = val;
          buf[(b++)] = val;
          buf[(b++)] = val;
          if (alphachannel) {
            c++;
          }
        }
      } else if (!alphachannel) {
        System.arraycopy(scanline, 0, buf, 0, bytes);
      } else {
        c = 0; for (b = 0; b < bytes;) {
          buf[(b++)] = scanline[(c++)];
          buf[(b++)] = scanline[(c++)];
          buf[(b++)] = scanline[(c++)];
          c++;
        }
      }
    }
    return buf;
  }
  








  public static int[] palette2rgba(ImageLineInt line, PngChunkPLTE pal, PngChunkTRNS trns, int[] buf)
  {
    return palette2rgb(line, pal, trns, buf, true);
  }
  
  public static int[] palette2rgb(ImageLineInt line, PngChunkPLTE pal, int[] buf) {
    return palette2rgb(line, pal, null, buf, false);
  }
  

  public static int[] convert2rgba(IImageLineArray line, PngChunkPLTE pal, PngChunkTRNS trns, int[] buf)
  {
    ImageInfo imi = line.getImageInfo();
    int nsamples = cols * 4;
    if ((buf == null) || (buf.length < nsamples))
      buf = new int[nsamples];
    int maxval = bitDepth == 16 ? 65535 : 255;
    Arrays.fill(buf, maxval);
    
    if (indexed) {
      int tlen = trns != null ? trns.getPalletteAlpha().length : 0;
      for (int s = 0; s < cols; s++) {
        int index = line.getElem(s);
        pal.getEntryRgb(index, buf, s * 4);
        if (index < tlen) {
          buf[(s * 4 + 3)] = trns.getPalletteAlpha()[index];
        }
      }
    } else if (greyscale) {
      int[] unpack = null;
      if (bitDepth < 8)
        unpack = DEPTH_UNPACK[bitDepth];
      int s = 0;int i = 0; for (int p = 0; p < cols; p++) {
        buf[(s++)] = (unpack != null ? unpack[line.getElem(i++)] : line.getElem(i++));
        buf[s] = buf[(s - 1)];
        s++;
        buf[s] = buf[(s - 1)];
        s++;
        if (channels == 2) {
          buf[(s++)] = (unpack != null ? unpack[line.getElem(i++)] : line.getElem(i++));
        } else
          buf[(s++)] = maxval;
      }
    } else {
      int s = 0;int i = 0; for (int p = 0; p < cols; p++) {
        buf[(s++)] = line.getElem(i++);
        buf[(s++)] = line.getElem(i++);
        buf[(s++)] = line.getElem(i++);
        buf[(s++)] = (alpha ? line.getElem(i++) : maxval);
      }
    }
    return buf;
  }
  


  private static int[] palette2rgb(IImageLine line, PngChunkPLTE pal, PngChunkTRNS trns, int[] buf, boolean alphaForced)
  {
    boolean isalpha = trns != null;
    int channels = isalpha ? 4 : 3;
    ImageLineInt linei = (ImageLineInt)((line instanceof ImageLineInt) ? line : null);
    ImageLineByte lineb = (ImageLineByte)((line instanceof ImageLineByte) ? line : null);
    boolean isbyte = lineb != null;
    int cols = linei != null ? imgInfo.cols : imgInfo.cols;
    int nsamples = cols * channels;
    if ((buf == null) || (buf.length < nsamples))
      buf = new int[nsamples];
    int nindexesWithAlpha = trns != null ? trns.getPalletteAlpha().length : 0;
    for (int c = 0; c < cols; c++) {
      int index = isbyte ? scanline[c] & 0xFF : scanline[c];
      pal.getEntryRgb(index, buf, c * channels);
      if (isalpha) {
        int alpha = index < nindexesWithAlpha ? trns.getPalletteAlpha()[index] : 255;
        buf[(c * channels + 3)] = alpha;
      }
    }
    return buf;
  }
  





  public static String infoFirstLastPixels(ImageLineInt line)
  {
    return imgInfo.channels == 1 ? String.format("first=(%d) last=(%d)", new Object[] { Integer.valueOf(scanline[0]), Integer.valueOf(scanline[(scanline.length - 1)]) }) : String.format("first=(%d %d %d) last=(%d %d %d)", new Object[] { Integer.valueOf(scanline[0]), Integer.valueOf(scanline[1]), Integer.valueOf(scanline[2]), Integer.valueOf(scanline[(scanline.length - imgInfo.channels)]), Integer.valueOf(scanline[(scanline.length - imgInfo.channels + 1)]), Integer.valueOf(scanline[(scanline.length - imgInfo.channels + 2)]) });
  }
  








  public static int getPixelRGB8(IImageLine line, int column)
  {
    if ((line instanceof ImageLineInt)) {
      int offset = column * imgInfo.channels;
      int[] scanline = ((ImageLineInt)line).getScanline();
      return scanline[offset] << 16 | scanline[(offset + 1)] << 8 | scanline[(offset + 2)]; }
    if ((line instanceof ImageLineByte)) {
      int offset = column * imgInfo.channels;
      byte[] scanline = ((ImageLineByte)line).getScanline();
      return (scanline[offset] & 0xFF) << 16 | (scanline[(offset + 1)] & 0xFF) << 8 | scanline[(offset + 2)] & 0xFF;
    }
    
    throw new PngjException("Not supported " + line.getClass());
  }
  
  public static int getPixelARGB8(IImageLine line, int column) {
    if ((line instanceof ImageLineInt)) {
      int offset = column * imgInfo.channels;
      int[] scanline = ((ImageLineInt)line).getScanline();
      return scanline[(offset + 3)] << 24 | scanline[offset] << 16 | scanline[(offset + 1)] << 8 | scanline[(offset + 2)];
    }
    if ((line instanceof ImageLineByte)) {
      int offset = column * imgInfo.channels;
      byte[] scanline = ((ImageLineByte)line).getScanline();
      return (scanline[(offset + 3)] & 0xFF) << 24 | (scanline[offset] & 0xFF) << 16 | (scanline[(offset + 1)] & 0xFF) << 8 | scanline[(offset + 2)] & 0xFF;
    }
    
    throw new PngjException("Not supported " + line.getClass());
  }
  
  public static void setPixelsRGB8(ImageLineInt line, int[] rgb) {
    int i = 0; for (int j = 0; i < imgInfo.cols; i++) {
      scanline[(j++)] = (rgb[i] >> 16 & 0xFF);
      scanline[(j++)] = (rgb[i] >> 8 & 0xFF);
      scanline[(j++)] = (rgb[i] & 0xFF);
    }
  }
  
  public static void setPixelRGB8(ImageLineInt line, int col, int r, int g, int b) {
    col *= imgInfo.channels;
    scanline[(col++)] = r;
    scanline[(col++)] = g;
    scanline[col] = b;
  }
  
  public static void setPixelRGB8(ImageLineInt line, int col, int rgb) {
    setPixelRGB8(line, col, rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
  }
  
  public static void setPixelsRGBA8(ImageLineInt line, int[] rgb) {
    int i = 0; for (int j = 0; i < imgInfo.cols; i++) {
      scanline[(j++)] = (rgb[i] >> 16 & 0xFF);
      scanline[(j++)] = (rgb[i] >> 8 & 0xFF);
      scanline[(j++)] = (rgb[i] & 0xFF);
      scanline[(j++)] = (rgb[i] >> 24 & 0xFF);
    }
  }
  
  public static void setPixelRGBA8(ImageLineInt line, int col, int r, int g, int b, int a) {
    col *= imgInfo.channels;
    scanline[(col++)] = r;
    scanline[(col++)] = g;
    scanline[(col++)] = b;
    scanline[col] = a;
  }
  
  public static void setPixelRGBA8(ImageLineInt line, int col, int rgb) {
    setPixelRGBA8(line, col, rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, rgb >> 24 & 0xFF);
  }
  
  public static void setValD(ImageLineInt line, int i, double d) {
    scanline[i] = double2int(line, d);
  }
  

  public static int interpol(int a, int b, int c, int d, double dx, double dy)
  {
    double e = a * (1.0D - dx) + b * dx;
    double f = c * (1.0D - dx) + d * dx;
    return (int)(e * (1.0D - dy) + f * dy + 0.5D);
  }
  
  public static double int2double(ImageLineInt line, int p) {
    return imgInfo.bitDepth == 16 ? p / 65535.0D : p / 255.0D;
  }
  

  public static double int2doubleClamped(ImageLineInt line, int p)
  {
    double d = imgInfo.bitDepth == 16 ? p / 65535.0D : p / 255.0D;
    return d >= 1.0D ? 1.0D : d <= 0.0D ? 0.0D : d;
  }
  
  public static int double2int(ImageLineInt line, double d) {
    d = d >= 1.0D ? 1.0D : d <= 0.0D ? 0.0D : d;
    return imgInfo.bitDepth == 16 ? (int)(d * 65535.0D + 0.5D) : (int)(d * 255.0D + 0.5D);
  }
  
  public static int double2intClamped(ImageLineInt line, double d) {
    d = d >= 1.0D ? 1.0D : d <= 0.0D ? 0.0D : d;
    return imgInfo.bitDepth == 16 ? (int)(d * 65535.0D + 0.5D) : (int)(d * 255.0D + 0.5D);
  }
  
  public static int clampTo_0_255(int i) {
    return i < 0 ? 0 : i > 255 ? 255 : i;
  }
  
  public static int clampTo_0_65535(int i) {
    return i < 0 ? 0 : i > 65535 ? 65535 : i;
  }
  
  public static int clampTo_128_127(int x) {
    return x < -128 ? -128 : x > 127 ? 127 : x;
  }
  
  public static int getMaskForPackedFormats(int bitDepth) {
    if (bitDepth == 4)
      return 240;
    if (bitDepth == 2) {
      return 192;
    }
    return 128;
  }
  
  public static int getMaskForPackedFormatsLs(int bitDepth) {
    if (bitDepth == 4)
      return 15;
    if (bitDepth == 2) {
      return 3;
    }
    return 1;
  }
  
  static {}
}

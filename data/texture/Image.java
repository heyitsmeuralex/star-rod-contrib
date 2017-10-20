package data.texture;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.PngChunkPLTE;
import ar.com.hjg.pngj.chunks.PngChunkTRNS;
import ar.com.hjg.pngj.chunks.PngMetadata;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;


public class Image
{
  public final ImageFormat format;
  public final int height;
  public final int width;
  public final ByteBuffer raster;
  public Palette palette;
  
  public Image(ImageFormat fmt, int h, int w)
  {
    format = fmt;
    height = h;
    width = w;
    
    raster = ByteBuffer.allocateDirect(w * h * format.bpp >> 3);
    palette = null;
  }
  
  public void readImage(RandomAccessFile raf, int offset, boolean flip) throws IOException
  {
    raf.seek(offset);
    readImage(raf, flip);
  }
  
  public void readImage(RandomAccessFile raf, boolean flip) throws IOException
  {
    byte[] bytes = new byte[raster.limit()];
    raf.read(bytes);
    
    if (!flip)
    {
      raster.put(bytes);
      return;
    }
    
    int rowsize = width * format.bpp >> 3;
    for (int row = height - 1; row >= 0; row--) {
      raster.put(bytes, rowsize * row, rowsize);
    }
  }
  
  public void readImage(ByteBuffer bb, int offset, boolean flip) {
    bb.position(offset);
    readImage(bb, flip);
  }
  
  public void readImage(ByteBuffer bb, boolean flip)
  {
    byte[] bytes = new byte[raster.limit()];
    bb.get(bytes);
    
    if (!flip)
    {
      raster.put(bytes);
      return;
    }
    
    int rowsize = width * format.bpp >> 3;
    for (int row = height - 1; row >= 0; row--) {
      raster.put(bytes, rowsize * row, rowsize);
    }
  }
  
  public void readPalette(RandomAccessFile raf, int offset) throws IOException {
    raf.seek(offset);
    readPalette(raf);
  }
  
  public void readPalette(RandomAccessFile raf) throws IOException
  {
    palette = Palette.read(raf, format);
  }
  
  public void readPalette(ByteBuffer bb, int offset)
  {
    bb.position(offset);
    readPalette(bb);
  }
  
  public void readPalette(ByteBuffer bb)
  {
    palette = Palette.read(bb, format);
  }
  
  public void putRaster(ByteBuffer bb, boolean flip)
  {
    raster.rewind();
    byte[] bytes = new byte[raster.limit()];
    raster.get(bytes);
    
    if (!flip)
    {
      bb.put(bytes);
      return;
    }
    
    int rowsize = width * format.bpp >> 3;
    for (int row = height - 1; row >= 0; row--) {
      bb.put(bytes, rowsize * row, rowsize);
    }
  }
  
  public void writeRaster(RandomAccessFile raf, int offset, boolean flip) throws IOException {
    raf.seek(offset);
    writeRaster(raf, flip);
  }
  
  public void writeRaster(RandomAccessFile raf, boolean flip) throws IOException
  {
    raster.rewind();
    byte[] bytes = new byte[raster.limit()];
    raster.get(bytes);
    
    if (!flip)
    {
      raf.write(bytes);
      return;
    }
    
    int rowsize = width * format.bpp >> 3;
    for (int row = height - 1; row >= 0; row--) {
      raf.write(bytes, rowsize * row, rowsize);
    }
  }
  
  public static Image load(String filename, ImageFormat format) throws IOException {
    return load(new File(filename), format, false);
  }
  
  public static Image load(File in, ImageFormat format) throws IOException
  {
    return load(in, format, false);
  }
  
  public static Image load(String filename, ImageFormat format, boolean convert) throws IOException
  {
    return load(new File(filename), format, convert);
  }
  
  public static Image load(File in, ImageFormat format, boolean convert) throws IOException
  {
    if (type == 2) {
      return loadColorIndexed(in, format, convert);
    }
    BufferedImage bimg = ImageIO.read(in);
    return ImageConverter.convert(bimg, format);
  }
  
  private static Image loadColorIndexed(File in, ImageFormat format, boolean convert) throws IOException
  {
    BufferedImage bimg = ImageIO.read(in);
    


    if (convert) {
      throw new UnsupportedOperationException("Automatic conversion from RGBA to CI is not yet supported.");
    }
    
    if ((type == 2) && (bimg.getType() != 13)) {
      throw new RuntimeException("Image is not color index format: " + in.getPath());
    }
    Image img = new Image(format, bimg.getHeight(), bimg.getWidth());
    
    DataBufferByte dataBuffer = (DataBufferByte)bimg.getRaster().getDataBuffer();
    byte[] data = dataBuffer.getData();
    
    raster.rewind();
    int k = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        if (format == ImageFormat.CI_4)
        {
          byte i = data[(k++)];
          byte j = data[(k++)];
          raster.put((byte)(i << 4 | j));
          x++;
        }
        else
        {
          raster.put(data[(k++)]);
        }
      }
    }
    int paletteSize = 1 << bpp;
    
    IndexColorModel colorModel = (IndexColorModel)bimg.getColorModel();
    palette = new Palette(colorModel, paletteSize);
    
    return img;
  }
  
  public void savePNG(String filename) throws IOException
  {
    if (!filename.endsWith(".png"))
      filename = filename + ".png";
    File out = new File(filename);
    
    if (format.type == 2)
    {
      saveColorIndexedPNG(out);
      return;
    }
    
    BufferedImage bimg = ImageConverter.convert(this);
    ImageIO.write(bimg, "png", out);
  }
  























  private void saveColorIndexedPNG(File out)
  {
    raster.rewind();
    
    int paletteSize = 1 << format.bpp;
    ImageInfo info = new ImageInfo(width, height, 8, false, false, true);
    
    PngWriter writer = new PngWriter(out, info);
    writer.setCompLevel(9);
    
    PngChunkPLTE paletteChunk = new PngChunkPLTE(info);
    paletteChunk.setNentries(256);
    for (int i = 0; i < paletteSize; i++) {
      paletteChunk.setEntry(i, palette.r[i] & 0xFF, palette.g[i] & 0xFF, palette.b[i] & 0xFF);
    }
    PngChunkTRNS alphaChunk = new PngChunkTRNS(info);
    alphaChunk.setNentriesPalAlpha(256);
    for (int i = 0; i < paletteSize; i++) {
      alphaChunk.setEntryPalAlpha(i, palette.a[i] & 0xFF);
    }
    writer.getMetadata().queueChunk(paletteChunk);
    writer.getMetadata().queueChunk(alphaChunk);
    
    for (int i = 0; i < height; i++)
    {
      ImageLineByte line = new ImageLineByte(info);
      
      byte[] scanline = line.getScanlineByte();
      for (int j = 0; j < width;)
      {
        byte b = raster.get();
        
        if (format == ImageFormat.CI_4)
        {
          scanline[(j++)] = ((byte)(b >>> 4 & 0xF));
          scanline[(j++)] = ((byte)(b & 0xF));
        }
        else
        {
          scanline[(j++)] = b;
        }
      }
      writer.writeRow(line, i);
    }
    writer.end();
  }
  
  public static BufferedImage readTGA(File file) throws IOException
  {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getAbsolutePath());
    }
    byte[] header = new byte[18];
    int len = (int)file.length() - header.length;
    if (len < 0)
      throw new IllegalStateException("file not big enough to contain header: " + file.getAbsolutePath());
    byte[] data = new byte[len];
    
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    raf.read(header);
    raf.read(data);
    raf.close();
    
    if ((header[0] | header[1]) != 0)
      throw new IllegalStateException(file.getAbsolutePath());
    if (header[2] != 2)
      throw new IllegalStateException(file.getAbsolutePath());
    int w = 0;int h = 0;
    w |= (header[12] & 0xFF) << 0;
    w |= (header[13] & 0xFF) << 8;
    h |= (header[14] & 0xFF) << 0;
    h |= (header[15] & 0xFF) << 8;
    
    System.out.println(w);
    System.out.println(h);
    System.out.println(data.length);
    
    boolean alpha;
    if (w * h * 3 == data.length) {
      alpha = false; } else { boolean alpha;
      if (w * h * 4 == data.length) {
        alpha = true;
      } else
        throw new IllegalStateException(file.getAbsolutePath()); }
    boolean alpha; if ((!alpha) && (header[16] != 24))
      throw new IllegalStateException(file.getAbsolutePath());
    if ((alpha) && (header[16] != 32))
      throw new IllegalStateException(file.getAbsolutePath());
    if ((header[17] & 0xF) != (alpha ? 8 : 0)) {
      throw new IllegalStateException(file.getAbsolutePath());
    }
    BufferedImage dst = new BufferedImage(w, h, alpha ? 2 : 1);
    int[] pixels = ((DataBufferInt)dst.getRaster().getDataBuffer()).getData();
    if (pixels.length != w * h)
      throw new IllegalStateException(file.getAbsolutePath());
    if (data.length != pixels.length * (alpha ? 4 : 3)) {
      throw new IllegalStateException(file.getAbsolutePath());
    }
    if (alpha)
    {
      int i = 0; for (int p = (pixels.length - 1) * 4; i < pixels.length; p -= 4)
      {
        pixels[i] |= (data[(p + 0)] & 0xFF) << 0;
        pixels[i] |= (data[(p + 1)] & 0xFF) << 8;
        pixels[i] |= (data[(p + 2)] & 0xFF) << 16;
        pixels[i] |= (data[(p + 3)] & 0xFF) << 24;i++;
      }
      

    }
    else
    {

      int i = 0; for (int p = (pixels.length - 1) * 3; i < pixels.length; p -= 3)
      {
        pixels[i] |= (data[(p + 0)] & 0xFF) << 0;
        pixels[i] |= (data[(p + 1)] & 0xFF) << 8;
        pixels[i] |= (data[(p + 2)] & 0xFF) << 16;i++;
      }
    }
    


    if (header[17] >> 4 != 1)
    {


      if (header[17] >> 4 == 0)
      {


        for (int y = 0; y < h; y++)
        {
          int w2 = w / 2;
          for (int x = 0; x < w2; x++)
          {
            int a = y * w + x;
            int b = y * w + (w - 1 - x);
            int t = pixels[a];
            pixels[a] = pixels[b];
            pixels[b] = t;
          }
          
        }
        
      } else {
        throw new UnsupportedOperationException(file.getAbsolutePath());
      }
    }
    return dst;
  }
}

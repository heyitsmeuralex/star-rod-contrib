package data.texture;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;





public class Palette
{
  public final byte[] r;
  public final byte[] g;
  public final byte[] b;
  public final byte[] a;
  public final int size;
  private final int[] cmap;
  
  public Palette(short[] pal)
  {
    size = pal.length;
    
    r = new byte[size];
    g = new byte[size];
    b = new byte[size];
    a = new byte[size];
    
    cmap = new int[size];
    
    for (int i = 0; i < size; i++)
    {
      Color c = unpackColor(pal[i]);
      r[i] = ((byte)c.getRed());
      g[i] = ((byte)c.getGreen());
      b[i] = ((byte)c.getBlue());
      a[i] = ((byte)c.getAlpha());
      

      cmap[i] = (a[i] << 24 & 0xFF000000 | r[i] << 16 & 0xFF0000 | g[i] << 8 & 0xFF00 | b[i] & 0xFF);
    }
  }
  
  public Palette(IndexColorModel colorModel, int size)
  {
    this.size = size;
    
    r = new byte[size];
    g = new byte[size];
    b = new byte[size];
    a = new byte[size];
    
    cmap = new int[size];
    
    int[] colors = new int[colorModel.getMapSize()];
    colorModel.getRGBs(colors);
    
    for (int i = 0; i < size; i++)
    {
      cmap[i] = colors[i];
      a[i] = ((byte)(colors[i] >>> 24));
      r[i] = ((byte)(colors[i] >>> 16));
      g[i] = ((byte)(colors[i] >>> 8));
      b[i] = ((byte)colors[i]);
    }
  }
  
  public static Palette read(RandomAccessFile raf, ImageFormat fmt) throws IOException
  {
    if (type != 2) {
      throw new IllegalArgumentException("Can only read palette for color index image.");
    }
    int paletteSize = 1 << bpp;
    
    short[] packed = new short[paletteSize];
    for (int i = 0; i < packed.length; i++) {
      packed[i] = raf.readShort();
    }
    return new Palette(packed);
  }
  
  public static Palette read(ByteBuffer bb, ImageFormat fmt)
  {
    if (type != 2) {
      throw new IllegalArgumentException("Can only read palette for color index image.");
    }
    int paletteSize = 1 << bpp;
    
    short[] packed = new short[paletteSize];
    for (int i = 0; i < packed.length; i++) {
      packed[i] = bb.getShort();
    }
    return new Palette(packed);
  }
  
  public void put(ByteBuffer bb)
  {
    for (int i = 0; i < size; i++) {
      bb.putShort(packIndex(i));
    }
  }
  
  public void write(RandomAccessFile raf, int offset) throws IOException {
    raf.seek(offset);
    write(raf);
  }
  
  public void write(RandomAccessFile raf) throws IOException
  {
    for (int i = 0; i < size; i++) {
      raf.writeShort(packIndex(i));
    }
  }
  
  public Color[] getColors() {
    Color[] colors = new Color[size];
    for (int i = 0; i < colors.length; i++)
      colors[i] = new Color(r[i] & 0xFF, g[i] & 0xFF, b[i] & 0xFF, a[i] & 0xFF);
    return colors;
  }
  





  private static Color unpackColor(short s)
  {
    int R = s >>> 11 & 0x1F;
    int G = s >>> 6 & 0x1F;
    int B = s >>> 1 & 0x1F;
    int A = (s & 0x1) == 1 ? 255 : 0;
    R = (int)Math.ceil(255.0D * (R / 31.0D));
    G = (int)Math.ceil(255.0D * (G / 31.0D));
    B = (int)Math.ceil(255.0D * (B / 31.0D));
    return new Color(R, G, B, A);
  }
  
  private short packIndex(int i)
  {
    int R = (int)Math.floor(31.0D * (r[i] / 255.0D));
    int G = (int)Math.floor(31.0D * (g[i] / 255.0D));
    int B = (int)Math.floor(31.0D * (b[i] / 255.0D));
    
    int color = (a[i] & 0x80) == 0 ? 0 : 1;
    color |= (R & 0x1F) << 11;
    color |= (G & 0x1F) << 6;
    color |= (B & 0x1F) << 1;
    
    return (short)color;
  }
}

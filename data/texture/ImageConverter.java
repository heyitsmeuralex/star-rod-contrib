package data.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ImageConverter
{
  public ImageConverter() {}
  
  public static BufferedImage convert(Image img)
  {
    switch (1.$SwitchMap$data$texture$ImageFormat[format.ordinal()]) {
    case 1: 
      return convert_RGBA_16bpp(img);
    case 2:  return convert_RGBA_32bpp(img);
    case 3:  return convert_IA_4bpp(img);
    case 4:  return convert_IA_8bpp(img);
    case 5:  return convert_IA_16bpp(img);
    case 6:  return convert_I_4bpp(img);
    case 7:  return convert_I_8bpp(img);
    case 8:  return convert_CI_4bpp(img);
    case 9:  return convert_CI_8bpp(img);
    }
    throw new UnsupportedOperationException("Unsupported format " + format);
  }
  

  public static Image convert(BufferedImage bimg, ImageFormat fmt)
  {
    switch (1.$SwitchMap$data$texture$ImageFormat[fmt.ordinal()]) {
    case 1: 
      return convert_RGBA_16bpp(bimg);
    case 2:  return convert_RGBA_32bpp(bimg);
    case 3:  return convert_IA_4bpp(bimg);
    case 4:  return convert_IA_8bpp(bimg);
    case 5:  return convert_IA_16bpp(bimg);
    case 6:  return convert_I_4bpp(bimg);
    case 7:  return convert_I_8bpp(bimg);
    }
    
    
    throw new UnsupportedOperationException("Unsupported format " + fmt);
  }
  

  private static BufferedImage convert_RGBA_16bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = unpackColor(raster.getShort());
        tex.setRGB(x, y, c.getRGB());
      }
    }
    


    return tex;
  }
  
  private static Image convert_RGBA_16bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.RGBA_16, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = new Color(bimg.getRGB(x, y), true);
        
        int R = (int)Math.floor(31.0D * (c.getRed() / 255.0D));
        int G = (int)Math.floor(31.0D * (c.getGreen() / 255.0D));
        int B = (int)Math.floor(31.0D * (c.getBlue() / 255.0D));
        
        int color = (c.getAlpha() & 0x80) == 0 ? 0 : 1;
        color |= (R & 0x1F) << 11;
        color |= (G & 0x1F) << 6;
        color |= (B & 0x1F) << 1;
        
        raster.putShort((short)color);
      }
    }
    return img;
  }
  
  private static BufferedImage convert_RGBA_32bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        int v = raster.getInt();
        int R = v >>> 24 & 0xFF;
        int G = v >>> 16 & 0xFF;
        int B = v >>> 8 & 0xFF;
        int A = v & 0xFF;
        Color c = new Color(R, G, B, A);
        



        tex.setRGB(x, y, c.getRGB());
      }
    }
    return tex;
  }
  
  private static Image convert_RGBA_32bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.RGBA_32, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = new Color(bimg.getRGB(x, y), true);
        
        int v = c.getAlpha();
        v = c.getBlue() << 8 | v;
        v = c.getGreen() << 16 | v;
        v = c.getRed() << 24 | v;
        
        raster.putInt(v);
      }
    }
    return img;
  }
  
  private static BufferedImage convert_IA_4bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        byte b = raster.get();
        byte h = (byte)(b >>> 4 & 0xF);
        byte l = (byte)(b & 0xF);
        
        int hI = h >>> 1 & 0xF;
        int hA = (h & 0x1) == 1 ? 255 : 0;
        hI = (int)Math.ceil(255.0D * (hI / 7.0D));
        
        int lI = l >>> 1 & 0xF;
        int lA = (l & 0x1) == 1 ? 255 : 0;
        lI = (int)Math.ceil(255.0D * (lI / 7.0D));
        



        Color c1 = new Color(hI, hI, hI, hA);
        Color c2 = new Color(lI, lI, lI, lA);
        
        tex.setRGB(x++, y, c1.getRGB());
        tex.setRGB(x++, y, c2.getRGB());
      }
    }
    return tex;
  }
  





  private static Image convert_IA_4bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.IA_4, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        Color c1 = new Color(bimg.getRGB(x++, y), true);
        Color c2 = new Color(bimg.getRGB(x++, y), true);
        
        int I1 = (c1.getRed() + c1.getBlue() + c1.getGreen()) / 3;
        int A1 = c1.getAlpha();
        
        int I2 = (c2.getRed() + c2.getBlue() + c2.getGreen()) / 3;
        int A2 = c2.getAlpha();
        
        I1 = (int)Math.floor(7.0D * (I1 / 255.0D));
        I2 = (int)Math.floor(7.0D * (I2 / 255.0D));
        
        A1 = A1 > 128 ? 1 : 0;
        A2 = A2 > 128 ? 1 : 0;
        
        byte h = (byte)(I1 << 1 | A1);
        byte l = (byte)(I2 << 1 | A2);
        
        raster.put((byte)(h << 4 | l));
      }
    }
    return img;
  }
  
  private static BufferedImage convert_IA_8bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        byte b = raster.get();
        int I = b >>> 4 & 0xF;
        int A = b & 0xF;
        I = (int)Math.ceil(255.0D * (I / 15.0D));
        A = (int)Math.ceil(255.0D * (A / 15.0D));
        



        Color c = new Color(I, I, I, A);
        tex.setRGB(x, y, c.getRGB());
      }
    }
    return tex;
  }
  




  private static Image convert_IA_8bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.IA_8, bimg.getHeight(), bimg.getWidth());
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = new Color(bimg.getRGB(x, y), true);
        
        int I = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
        int A = c.getAlpha();
        
        I = (int)Math.floor(15.0D * (I / 255.0D));
        A = (int)Math.floor(15.0D * (A / 255.0D));
        
        byte b = (byte)(I << 4 | A);
        raster.put(b);
      }
    }
    return img;
  }
  
  private static BufferedImage convert_IA_16bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        int I = raster.get() & 0xFF;
        int A = raster.get() & 0xFF;
        



        Color c = new Color(I, I, I, A);
        tex.setRGB(x, y, c.getRGB());
      }
    }
    return tex;
  }
  




  private static Image convert_IA_16bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.IA_16, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = new Color(bimg.getRGB(x, y), true);
        
        int I = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
        int A = c.getAlpha();
        
        raster.put((byte)I);
        raster.put((byte)A);
      }
    }
    return img;
  }
  
  private static BufferedImage convert_I_4bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        byte b = raster.get();
        int hI = b >>> 4 & 0xF;
        int lI = b & 0xF;
        hI = (int)Math.ceil(255.0D * (hI / 15.0D));
        lI = (int)Math.ceil(255.0D * (lI / 15.0D));
        
        Color c1 = new Color(hI, hI, hI, 255);
        Color c2 = new Color(lI, lI, lI, 255);
        
        tex.setRGB(x++, y, c1.getRGB());
        tex.setRGB(x++, y, c2.getRGB());
      }
    }
    return tex;
  }
  




  private static Image convert_I_4bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.I_4, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        Color c1 = new Color(bimg.getRGB(x++, y), true);
        Color c2 = new Color(bimg.getRGB(x++, y), true);
        
        int I1 = (c1.getRed() + c1.getBlue() + c1.getGreen()) / 3;
        int I2 = (c2.getRed() + c2.getBlue() + c2.getGreen()) / 3;
        
        I1 = (int)Math.floor(15.0D * (I1 / 255.0D));
        I2 = (int)Math.floor(15.0D * (I2 / 255.0D));
        
        raster.put((byte)(I1 << 4 | I2));
      }
    }
    return img;
  }
  
  private static BufferedImage convert_I_8bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        int I = raster.get() & 0xFF;
        Color c = new Color(I, I, I, 255);
        tex.setRGB(x, y, c.getRGB());
      }
    }
    return tex;
  }
  




  private static Image convert_I_8bpp(BufferedImage bimg)
  {
    Image img = new Image(ImageFormat.I_8, bimg.getHeight(), bimg.getWidth());
    raster.rewind();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++)
      {
        Color c = new Color(bimg.getRGB(x, y), true);
        int I = (c.getRed() + c.getBlue() + c.getGreen()) / 3;
        raster.put((byte)I);
      }
    }
    return img;
  }
  
  private static BufferedImage convert_CI_4bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    Color[] colors = palette.getColors();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        byte b = raster.get();
        int i = b >>> 4 & 0xF;
        int j = b & 0xF;
        
        Color c1 = colors[i];
        Color c2 = colors[j];
        



        tex.setRGB(x++, y, c1.getRGB());
        tex.setRGB(x++, y, c2.getRGB());
      }
    }
    return tex;
  }
  
  private static BufferedImage convert_CI_8bpp(Image img)
  {
    BufferedImage tex = new BufferedImage(width, height, 2);
    raster.rewind();
    
    Color[] colors = palette.getColors();
    int x;
    for (int y = 0; y < height; y++) {
      for (x = 0; x < width;)
      {
        int i = raster.get() & 0xFF;
        Color c = colors[i];
        



        tex.setRGB(x++, y, c.getRGB());
      }
    }
    return tex;
  }
  





  public static Color unpackColor(short s)
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
  
  public short packColor(int r, int g, int b, int a)
  {
    int R = (int)Math.floor(31.0D * (r / 255.0D));
    int G = (int)Math.floor(31.0D * (g / 255.0D));
    int B = (int)Math.floor(31.0D * (b / 255.0D));
    
    int color = (a & 0x80) == 0 ? 0 : 1;
    color |= (R & 0x1F) << 11;
    color |= (G & 0x1F) << 6;
    color |= (B & 0x1F) << 1;
    
    return (short)color;
  }
}

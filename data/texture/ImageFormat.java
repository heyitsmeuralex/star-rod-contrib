package data.texture;

import java.util.HashMap;

public enum ImageFormat
{
  I_4("I-4", 4, 0), 
  I_8("I-8", 4, 1), 
  IA_4("IA-4", 3, 0), 
  IA_8("IA-8", 3, 1), 
  IA_16("IA-16", 3, 2), 
  CI_4("CI-4", 2, 0), 
  CI_8("CI-8", 2, 1), 
  YUV_16("YUV-16", 1, 2), 
  RGBA_16("RGBA-16", 0, 2), 
  RGBA_32("RGBA-32", 0, 3);
  

  public static final int TYPE_RGBA = 0;
  public static final int TYPE_YUV = 1;
  public static final int TYPE_CI = 2;
  public static final int TYPE_IA = 3;
  public static final int TYPE_I = 4;
  public static final int DEPTH_4BPP = 0;
  public static final int DEPTH_8BPP = 1;
  public static final int DEPTH_16BPP = 2;
  public static final int DEPTH_32BPP = 3;
  public final String name;
  public final int type;
  public final int depth;
  public final int bpp;
  private static HashMap<String, ImageFormat> nameMap;
  
  private ImageFormat(String name, int type, int depth)
  {
    this.name = name;
    this.type = type;
    this.depth = depth;
    bpp = (4 << depth);
  }
  
  public String toString()
  {
    return name;
  }
  
  public static ImageFormat get(int type, int depth)
  {
    switch (type)
    {
    case 0: 
      switch (depth) {
      case 2: 
        return RGBA_16;
      case 3:  return RGBA_32;
      }
    case 1: 
      throw new UnsupportedOperationException("DEPTH format " + YUV_16 + " is not supported.");
    case 2: 
      switch (depth) {
      case 0: 
        return CI_4;
      case 1:  return CI_8;
      }
    case 3: 
      switch (depth) {
      case 0: 
        return IA_4;
      case 1:  return IA_8;
      case 2:  return IA_16;
      }
    case 4: 
      switch (depth) {
      case 0: 
        return I_4;
      case 1:  return I_8;
      }
      break;
    }
    String msg = String.format("Invalid image format: fmt = %s and depth = %s", new Object[] { Integer.valueOf(type), Integer.valueOf(depth) });
    throw new IllegalArgumentException(msg);
  }
  
  static { nameMap = new HashMap();
    

    for (ImageFormat fmt : values()) {
      nameMap.put(name, fmt);
    }
  }
  
  public static ImageFormat getFormat(String name) {
    return (ImageFormat)nameMap.get(name);
  }
}

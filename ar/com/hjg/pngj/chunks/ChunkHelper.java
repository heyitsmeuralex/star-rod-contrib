package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;












public class ChunkHelper
{
  public static final String IHDR = "IHDR";
  public static final String PLTE = "PLTE";
  public static final String IDAT = "IDAT";
  public static final String IEND = "IEND";
  public static final String cHRM = "cHRM";
  public static final String gAMA = "gAMA";
  public static final String iCCP = "iCCP";
  public static final String sBIT = "sBIT";
  public static final String sRGB = "sRGB";
  public static final String bKGD = "bKGD";
  public static final String hIST = "hIST";
  public static final String tRNS = "tRNS";
  public static final String pHYs = "pHYs";
  public static final String sPLT = "sPLT";
  public static final String tIME = "tIME";
  public static final String iTXt = "iTXt";
  public static final String tEXt = "tEXt";
  public static final String zTXt = "zTXt";
  public static final byte[] b_IHDR = toBytes("IHDR");
  public static final byte[] b_PLTE = toBytes("PLTE");
  public static final byte[] b_IDAT = toBytes("IDAT");
  public static final byte[] b_IEND = toBytes("IEND");
  



  private static byte[] tmpbuffer = new byte['က'];
  
  ChunkHelper() {}
  
  public static byte[] toBytes(String x)
  {
    try {
      return x.getBytes(PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  

  public static String toString(byte[] x)
  {
    try
    {
      return new String(x, PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  

  public static String toString(byte[] x, int offset, int len)
  {
    try
    {
      return new String(x, offset, len, PngHelperInternal.charsetLatin1name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  

  public static byte[] toBytesUTF8(String x)
  {
    try
    {
      return x.getBytes(PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  

  public static String toStringUTF8(byte[] x)
  {
    try
    {
      return new String(x, PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  

  public static String toStringUTF8(byte[] x, int offset, int len)
  {
    try
    {
      return new String(x, offset, len, PngHelperInternal.charsetUTF8name);
    } catch (UnsupportedEncodingException e) {
      throw new PngBadCharsetException(e);
    }
  }
  


  public static boolean isCritical(String id)
  {
    return Character.isUpperCase(id.charAt(0));
  }
  


  public static boolean isPublic(String id)
  {
    return Character.isUpperCase(id.charAt(1));
  }
  


  public static boolean isSafeToCopy(String id)
  {
    return !Character.isUpperCase(id.charAt(3));
  }
  



  public static boolean isUnknown(PngChunk c)
  {
    return c instanceof PngChunkUNKNOWN;
  }
  





  public static int posNullByte(byte[] b)
  {
    for (int i = 0; i < b.length; i++)
      if (b[i] == 0)
        return i;
    return -1;
  }
  






  public static boolean shouldLoad(String id, ChunkLoadBehaviour behav)
  {
    if (isCritical(id))
      return true;
    switch (1.$SwitchMap$ar$com$hjg$pngj$chunks$ChunkLoadBehaviour[behav.ordinal()]) {
    case 1: 
      return true;
    case 2: 
      return isSafeToCopy(id);
    case 3: 
      return false;
    }
    return false;
  }
  
  public static final byte[] compressBytes(byte[] ori, boolean compress) {
    return compressBytes(ori, 0, ori.length, compress);
  }
  
  public static byte[] compressBytes(byte[] ori, int offset, int len, boolean compress) {
    try {
      ByteArrayInputStream inb = new ByteArrayInputStream(ori, offset, len);
      InputStream in = compress ? inb : new InflaterInputStream(inb);
      ByteArrayOutputStream outb = new ByteArrayOutputStream();
      OutputStream out = compress ? new DeflaterOutputStream(outb) : outb;
      shovelInToOut(in, out);
      in.close();
      out.close();
      return outb.toByteArray();
    } catch (Exception e) {
      throw new PngjException(e);
    }
  }
  

  private static void shovelInToOut(InputStream in, OutputStream out)
    throws IOException
  {
    synchronized (tmpbuffer) {
      int len;
      while ((len = in.read(tmpbuffer)) > 0) {
        out.write(tmpbuffer, 0, len);
      }
    }
  }
  




  public static List<PngChunk> filterList(List<PngChunk> target, ChunkPredicate predicateKeep)
  {
    List<PngChunk> result = new ArrayList();
    for (PngChunk element : target) {
      if (predicateKeep.match(element)) {
        result.add(element);
      }
    }
    return result;
  }
  




  public static int trimList(List<PngChunk> target, ChunkPredicate predicateRemove)
  {
    Iterator<PngChunk> it = target.iterator();
    int cont = 0;
    while (it.hasNext()) {
      PngChunk c = (PngChunk)it.next();
      if (predicateRemove.match(c)) {
        it.remove();
        cont++;
      }
    }
    return cont;
  }
  










  public static final boolean equivalent(PngChunk c1, PngChunk c2)
  {
    if (c1 == c2)
      return true;
    if ((c1 == null) || (c2 == null) || (!id.equals(id)))
      return false;
    if (crit) {
      return false;
    }
    if (c1.getClass() != c2.getClass())
      return false;
    if (!c2.allowsMultiple())
      return true;
    if ((c1 instanceof PngChunkTextVar)) {
      return ((PngChunkTextVar)c1).getKey().equals(((PngChunkTextVar)c2).getKey());
    }
    if ((c1 instanceof PngChunkSPLT)) {
      return ((PngChunkSPLT)c1).getPalName().equals(((PngChunkSPLT)c2).getPalName());
    }
    
    return false;
  }
  
  public static boolean isText(PngChunk c) {
    return c instanceof PngChunkTextVar;
  }
}

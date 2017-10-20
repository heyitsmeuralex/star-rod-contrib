package util.japanese;

import java.nio.ByteBuffer;

public class JapaneseHelper
{
  public JapaneseHelper() {}
  
  public static String convertSJIStoUTF16(byte[] sjisBytes)
  {
    return convertSJIStoUTF16(ByteBuffer.wrap(sjisBytes));
  }
  
  public static String convertSJIStoUTF16(ByteBuffer sjisBuffer)
  {
    java.nio.charset.Charset sjis = java.nio.charset.Charset.forName("Shift-JIS");
    java.nio.charset.Charset utf8 = java.nio.charset.Charset.forName("UTF-16");
    
    java.nio.CharBuffer decodedCB = sjis.decode(sjisBuffer);
    ByteBuffer outBB = utf8.encode(decodedCB);
    byte[] outArray = new byte[outBB.limit()];
    outBB.get(outArray);
    
    return new String(outArray, utf8);
  }
  
  public static String convertSJIStoRomaji(byte[] sjisBytes)
  {
    return convertSJIStoRomaji(ByteBuffer.wrap(sjisBytes));
  }
  
  public static String convertSJIStoRomaji(ByteBuffer sjisBuffer)
  {
    String utfKanaString = convertSJIStoUTF16(sjisBuffer);
    WanaKanaJava wk = new WanaKanaJava();
    return wk.toRomaji(utfKanaString);
  }
}

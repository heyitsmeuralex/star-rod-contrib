package org.apache.commons.io.input;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;



























public class ReversedLinesFileReader
  implements Closeable
{
  private final int blockSize;
  private final String encoding;
  private final RandomAccessFile randomAccessFile;
  private final long totalByteLength;
  private final long totalBlockCount;
  private final byte[][] newLineSequences;
  private final int avoidNewlineSplitBufferSize;
  private final int byteDecrement;
  private FilePart currentFilePart;
  private boolean trailingNewlineOfFileSkipped = false;
  






  public ReversedLinesFileReader(File file)
    throws IOException
  {
    this(file, 4096, Charset.defaultCharset().toString());
  }
  










  public ReversedLinesFileReader(File file, int blockSize, String encoding)
    throws IOException
  {
    this.blockSize = blockSize;
    this.encoding = encoding;
    
    randomAccessFile = new RandomAccessFile(file, "r");
    totalByteLength = randomAccessFile.length();
    int lastBlockLength = (int)(totalByteLength % blockSize);
    if (lastBlockLength > 0) {
      totalBlockCount = (totalByteLength / blockSize + 1L);
    } else {
      totalBlockCount = (totalByteLength / blockSize);
      if (totalByteLength > 0L) {
        lastBlockLength = blockSize;
      }
    }
    currentFilePart = new FilePart(totalBlockCount, lastBlockLength, null, null);
    

    Charset charset = Charset.forName(encoding);
    CharsetEncoder charsetEncoder = charset.newEncoder();
    float maxBytesPerChar = charsetEncoder.maxBytesPerChar();
    if (maxBytesPerChar == 1.0F)
    {
      byteDecrement = 1;
    } else if (charset == Charset.forName("UTF-8"))
    {

      byteDecrement = 1;
    } else if (charset == Charset.forName("Shift_JIS"))
    {

      byteDecrement = 1;
    } else if ((charset == Charset.forName("UTF-16BE")) || (charset == Charset.forName("UTF-16LE")))
    {

      byteDecrement = 2;
    } else { if (charset == Charset.forName("UTF-16")) {
        throw new UnsupportedEncodingException("For UTF-16, you need to specify the byte order (use UTF-16BE or UTF-16LE)");
      }
      
      throw new UnsupportedEncodingException("Encoding " + encoding + " is not supported yet (feel free to submit a patch)");
    }
    

    newLineSequences = new byte[][] { "\r\n".getBytes(encoding), "\n".getBytes(encoding), "\r".getBytes(encoding) };
    
    avoidNewlineSplitBufferSize = newLineSequences[0].length;
  }
  






  public String readLine()
    throws IOException
  {
    String line = currentFilePart.readLine();
    while (line == null) {
      currentFilePart = currentFilePart.rollOver();
      if (currentFilePart == null) break;
      line = currentFilePart.readLine();
    }
    





    if (("".equals(line)) && (!trailingNewlineOfFileSkipped)) {
      trailingNewlineOfFileSkipped = true;
      line = readLine();
    }
    
    return line;
  }
  



  public void close()
    throws IOException
  {
    randomAccessFile.close();
  }
  


  private class FilePart
  {
    private final long no;
    

    private final byte[] data;
    
    private byte[] leftOver;
    
    private int currentLastBytePos;
    

    private FilePart(long no, int length, byte[] leftOverOfLastFilePart)
      throws IOException
    {
      this.no = no;
      int dataLength = length + (leftOverOfLastFilePart != null ? leftOverOfLastFilePart.length : 0);
      data = new byte[dataLength];
      long off = (no - 1L) * blockSize;
      

      if (no > 0L) {
        randomAccessFile.seek(off);
        int countRead = randomAccessFile.read(data, 0, length);
        if (countRead != length) {
          throw new IllegalStateException("Count of requested bytes and actually read bytes don't match");
        }
      }
      
      if (leftOverOfLastFilePart != null) {
        System.arraycopy(leftOverOfLastFilePart, 0, data, length, leftOverOfLastFilePart.length);
      }
      currentLastBytePos = (data.length - 1);
      leftOver = null;
    }
    





    private FilePart rollOver()
      throws IOException
    {
      if (currentLastBytePos > -1) {
        throw new IllegalStateException("Current currentLastCharPos unexpectedly positive... last readLine() should have returned something! currentLastCharPos=" + currentLastBytePos);
      }
      

      if (no > 1L) {
        return new FilePart(ReversedLinesFileReader.this, no - 1L, blockSize, leftOver);
      }
      
      if (leftOver != null) {
        throw new IllegalStateException("Unexpected leftover of the last block: leftOverOfThisFilePart=" + new String(leftOver, encoding));
      }
      
      return null;
    }
    






    private String readLine()
      throws IOException
    {
      String line = null;
      

      boolean isLastFilePart = no == 1L;
      
      int i = currentLastBytePos;
      while (i > -1)
      {
        if ((!isLastFilePart) && (i < avoidNewlineSplitBufferSize))
        {

          createLeftOver();
        }
        else
        {
          int newLineMatchByteCount;
          if ((newLineMatchByteCount = getNewLineMatchByteCount(data, i)) > 0) {
            int lineStart = i + 1;
            int lineLengthBytes = currentLastBytePos - lineStart + 1;
            
            if (lineLengthBytes < 0) {
              throw new IllegalStateException("Unexpected negative line length=" + lineLengthBytes);
            }
            byte[] lineData = new byte[lineLengthBytes];
            System.arraycopy(data, lineStart, lineData, 0, lineLengthBytes);
            
            line = new String(lineData, encoding);
            
            currentLastBytePos = (i - newLineMatchByteCount);

          }
          else
          {
            i -= byteDecrement;
            

            if (i < 0) {
              createLeftOver();
            }
          }
        }
      }
      
      if ((isLastFilePart) && (leftOver != null))
      {
        line = new String(leftOver, encoding);
        leftOver = null;
      }
      
      return line;
    }
    


    private void createLeftOver()
    {
      int lineLengthBytes = currentLastBytePos + 1;
      if (lineLengthBytes > 0)
      {
        leftOver = new byte[lineLengthBytes];
        System.arraycopy(data, 0, leftOver, 0, lineLengthBytes);
      } else {
        leftOver = null;
      }
      currentLastBytePos = -1;
    }
    






    private int getNewLineMatchByteCount(byte[] data, int i)
    {
      for (byte[] newLineSequence : newLineSequences) {
        boolean match = true;
        for (int j = newLineSequence.length - 1; j >= 0; j--) {
          int k = i + j - (newLineSequence.length - 1);
          match &= ((k >= 0) && (data[k] == newLineSequence[j]));
        }
        if (match) {
          return newLineSequence.length;
        }
      }
      return 0;
    }
  }
}

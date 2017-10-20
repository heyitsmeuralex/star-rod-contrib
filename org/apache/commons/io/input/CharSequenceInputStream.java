package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

































public class CharSequenceInputStream
  extends InputStream
{
  private final CharsetEncoder encoder;
  private final CharBuffer cbuf;
  private final ByteBuffer bbuf;
  private int mark;
  
  public CharSequenceInputStream(CharSequence s, Charset charset, int bufferSize)
  {
    encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    

    bbuf = ByteBuffer.allocate(bufferSize);
    bbuf.flip();
    cbuf = CharBuffer.wrap(s);
    mark = -1;
  }
  






  public CharSequenceInputStream(CharSequence s, String charset, int bufferSize)
  {
    this(s, Charset.forName(charset), bufferSize);
  }
  






  public CharSequenceInputStream(CharSequence s, Charset charset)
  {
    this(s, charset, 2048);
  }
  






  public CharSequenceInputStream(CharSequence s, String charset)
  {
    this(s, charset, 2048);
  }
  




  private void fillBuffer()
    throws CharacterCodingException
  {
    bbuf.compact();
    CoderResult result = encoder.encode(cbuf, bbuf, true);
    if (result.isError()) {
      result.throwException();
    }
    bbuf.flip();
  }
  
  public int read(byte[] b, int off, int len) throws IOException
  {
    if (b == null) {
      throw new NullPointerException("Byte array is null");
    }
    if ((len < 0) || (off + len > b.length)) {
      throw new IndexOutOfBoundsException("Array Size=" + b.length + ", offset=" + off + ", length=" + len);
    }
    
    if (len == 0) {
      return 0;
    }
    if ((!bbuf.hasRemaining()) && (!cbuf.hasRemaining())) {
      return -1;
    }
    int bytesRead = 0;
    while (len > 0) {
      if (bbuf.hasRemaining()) {
        int chunk = Math.min(bbuf.remaining(), len);
        bbuf.get(b, off, chunk);
        off += chunk;
        len -= chunk;
        bytesRead += chunk;
      } else {
        fillBuffer();
        if ((!bbuf.hasRemaining()) && (!cbuf.hasRemaining())) {
          break;
        }
      }
    }
    return (bytesRead == 0) && (!cbuf.hasRemaining()) ? -1 : bytesRead;
  }
  
  public int read() throws IOException
  {
    do {
      if (bbuf.hasRemaining()) {
        return bbuf.get() & 0xFF;
      }
      fillBuffer();
    } while ((bbuf.hasRemaining()) || (cbuf.hasRemaining()));
    return -1;
  }
  


  public int read(byte[] b)
    throws IOException
  {
    return read(b, 0, b.length);
  }
  
  public long skip(long n) throws IOException
  {
    int skipped = 0;
    while ((n > 0L) && (cbuf.hasRemaining())) {
      cbuf.get();
      n -= 1L;
      skipped++;
    }
    return skipped;
  }
  
  public int available() throws IOException
  {
    return cbuf.remaining();
  }
  


  public void close()
    throws IOException
  {}
  


  public synchronized void mark(int readlimit)
  {
    mark = cbuf.position();
  }
  
  public synchronized void reset() throws IOException
  {
    if (mark != -1) {
      cbuf.position(mark);
      mark = -1;
    }
  }
  
  public boolean markSupported()
  {
    return true;
  }
}

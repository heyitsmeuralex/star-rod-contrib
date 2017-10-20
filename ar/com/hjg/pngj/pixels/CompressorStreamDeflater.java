package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.IDatChunkWriter;
import ar.com.hjg.pngj.PngjOutputException;
import java.util.zip.Deflater;








public class CompressorStreamDeflater
  extends CompressorStream
{
  protected Deflater deflater;
  protected byte[] buf1;
  protected boolean deflaterIsOwn = true;
  

  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen, Deflater def)
  {
    super(idatCw, maxBlockLen, totalLen);
    deflater = (def == null ? new Deflater() : def);
    deflaterIsOwn = (def == null);
  }
  
  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen) {
    this(idatCw, maxBlockLen, totalLen, null);
  }
  
  public CompressorStreamDeflater(IDatChunkWriter idatCw, int maxBlockLen, long totalLen, int deflaterCompLevel, int deflaterStrategy)
  {
    this(idatCw, maxBlockLen, totalLen, new Deflater(deflaterCompLevel));
    
    deflater.setStrategy(deflaterStrategy);
  }
  
  public void mywrite(byte[] data, int off, int len)
  {
    if ((deflater.finished()) || (done) || (closed))
      throw new PngjOutputException("write beyond end of stream");
    deflater.setInput(data, off, len);
    bytesIn += len;
    while (!deflater.needsInput())
      deflate(); }
  
  protected void deflate() { int n;
    byte[] buf;
    int off;
    int n;
    if (idatChunkWriter != null) {
      byte[] buf = idatChunkWriter.getBuf();
      int off = idatChunkWriter.getOffset();
      n = idatChunkWriter.getAvailLen();
    } else {
      if (buf1 == null)
        buf1 = new byte['က'];
      buf = buf1;
      off = 0;
      n = buf1.length;
    }
    int len = deflater.deflate(buf, off, n);
    if (len > 0) {
      if (idatChunkWriter != null)
        idatChunkWriter.incrementOffset(len);
      bytesOut += len;
    }
  }
  

  public void done()
  {
    if (done)
      return;
    if (!deflater.finished()) {
      deflater.finish();
      while (!deflater.finished())
        deflate();
    }
    done = true;
    if (idatChunkWriter != null)
      idatChunkWriter.close();
  }
  
  public void close() {
    done();
    try {
      if (deflaterIsOwn) {
        deflater.end();
      }
    }
    catch (Exception e) {}
    super.close();
  }
  
  public void reset()
  {
    deflater.reset();
    super.reset();
  }
}

package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;





























































































public class MagicNumberFileFilter
  extends AbstractFileFilter
  implements Serializable
{
  private static final long serialVersionUID = -547733176983104172L;
  private final byte[] magicNumbers;
  private final long byteOffset;
  
  public MagicNumberFileFilter(byte[] magicNumber)
  {
    this(magicNumber, 0L);
  }
  




















  public MagicNumberFileFilter(String magicNumber)
  {
    this(magicNumber, 0L);
  }
  



















  public MagicNumberFileFilter(String magicNumber, long offset)
  {
    if (magicNumber == null) {
      throw new IllegalArgumentException("The magic number cannot be null");
    }
    if (magicNumber.length() == 0) {
      throw new IllegalArgumentException("The magic number must contain at least one byte");
    }
    if (offset < 0L) {
      throw new IllegalArgumentException("The offset cannot be negative");
    }
    
    magicNumbers = magicNumber.getBytes();
    byteOffset = offset;
  }
  





























  public MagicNumberFileFilter(byte[] magicNumber, long offset)
  {
    if (magicNumber == null) {
      throw new IllegalArgumentException("The magic number cannot be null");
    }
    if (magicNumber.length == 0) {
      throw new IllegalArgumentException("The magic number must contain at least one byte");
    }
    if (offset < 0L) {
      throw new IllegalArgumentException("The offset cannot be negative");
    }
    
    magicNumbers = new byte[magicNumber.length];
    System.arraycopy(magicNumber, 0, magicNumbers, 0, magicNumber.length);
    byteOffset = offset;
  }
  
















  public boolean accept(File file)
  {
    if ((file != null) && (file.isFile()) && (file.canRead())) {
      RandomAccessFile randomAccessFile = null;
      try {
        byte[] fileBytes = new byte[magicNumbers.length];
        randomAccessFile = new RandomAccessFile(file, "r");
        randomAccessFile.seek(byteOffset);
        int read = randomAccessFile.read(fileBytes);
        boolean bool; if (read != magicNumbers.length) {
          return false;
        }
        return Arrays.equals(magicNumbers, fileBytes);
      }
      catch (IOException ioe) {}finally
      {
        IOUtils.closeQuietly(randomAccessFile);
      }
    }
    
    return false;
  }
  






  public String toString()
  {
    StringBuilder builder = new StringBuilder(super.toString());
    builder.append("(");
    builder.append(new String(magicNumbers));
    builder.append(",");
    builder.append(byteOffset);
    builder.append(")");
    return builder.toString();
  }
}

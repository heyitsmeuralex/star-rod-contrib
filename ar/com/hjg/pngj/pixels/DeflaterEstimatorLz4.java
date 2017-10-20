package ar.com.hjg.pngj.pixels;

import java.nio.ByteOrder;























public final class DeflaterEstimatorLz4
{
  public DeflaterEstimatorLz4() {}
  
  public int compressEstim(byte[] src, int srcOff, int srcLen)
  {
    if (srcLen < 10)
      return srcLen;
    int stride = 65546;
    int segments = (srcLen + stride - 1) / stride;
    stride = srcLen / segments;
    if ((stride >= 65546) || (stride * segments > srcLen) || (segments < 1) || (stride < 1))
      throw new RuntimeException("?? " + srcLen);
    int bytesIn = 0;
    int bytesOut = 0;
    int len = srcLen;
    while (len > 0) {
      if (len > stride)
        len = stride;
      bytesOut += compress64k(src, srcOff, len);
      srcOff += len;
      bytesIn += len;
      len = srcLen - bytesIn;
    }
    double ratio = bytesOut / bytesIn;
    return bytesIn == srcLen ? bytesOut : (int)(ratio * srcLen + 0.5D);
  }
  
  public int compressEstim(byte[] src) {
    return compressEstim(src, 0, src.length);
  }
  
  static final ByteOrder NATIVE_BYTE_ORDER = ;
  
  static final int MEMORY_USAGE = 14;
  
  static final int NOT_COMPRESSIBLE_DETECTION_LEVEL = 6;
  
  static final int MIN_MATCH = 4;
  
  static final int HASH_LOG = 12;
  static final int HASH_TABLE_SIZE = 4096;
  static final int SKIP_STRENGTH = Math.max(6, 2);
  
  static final int COPY_LENGTH = 8;
  
  static final int LAST_LITERALS = 5;
  
  static final int MF_LIMIT = 12;
  static final int MIN_LENGTH = 13;
  static final int MAX_DISTANCE = 65536;
  static final int ML_BITS = 4;
  static final int ML_MASK = 15;
  static final int RUN_BITS = 4;
  static final int RUN_MASK = 15;
  static final int LZ4_64K_LIMIT = 65547;
  static final int HASH_LOG_64K = 13;
  static final int HASH_TABLE_SIZE_64K = 8192;
  static final int HASH_LOG_HC = 15;
  static final int HASH_TABLE_SIZE_HC = 32768;
  static final int OPTIMAL_ML = 18;
  
  static int compress64k(byte[] src, int srcOff, int srcLen)
  {
    int srcEnd = srcOff + srcLen;
    int srcLimit = srcEnd - 5;
    int mflimit = srcEnd - 12;
    
    int sOff = srcOff;int dOff = 0;
    
    int anchor = sOff;
    
    if (srcLen >= 13)
    {
      short[] hashTable = new short[' '];
      
      sOff++;
      

      for (;;)
      {
        int forwardOff = sOff;
        

        int findMatchAttempts = (1 << SKIP_STRENGTH) + 3;
        int ref;
        do { sOff = forwardOff;
          forwardOff += (findMatchAttempts++ >>> SKIP_STRENGTH);
          
          if (forwardOff > mflimit) {
            break;
          }
          
          int h = hash64k(readInt(src, sOff));
          ref = srcOff + readShort(hashTable, h);
          writeShort(hashTable, h, sOff - srcOff);
        } while (!readIntEquals(src, ref, sOff));
        

        int excess = commonBytesBackward(src, ref, sOff, srcOff, anchor);
        sOff -= excess;
        ref -= excess;
        
        int runLen = sOff - anchor;
        dOff++;
        
        if (runLen >= 15) {
          if (runLen > 15)
            dOff += (runLen - 15) / 255;
          dOff++;
        }
        dOff += runLen;
        for (;;)
        {
          dOff += 2;
          
          sOff += 4;
          ref += 4;
          int matchLen = commonBytes(src, ref, sOff, srcLimit);
          sOff += matchLen;
          
          if (matchLen >= 15) {
            if (matchLen >= 270)
              dOff += (matchLen - 15) / 255;
            dOff++;
          }
          
          if (sOff > mflimit) {
            anchor = sOff;
            
            break label360;
          }
          writeShort(hashTable, hash64k(readInt(src, sOff - 2)), sOff - 2 - srcOff);
          
          int h = hash64k(readInt(src, sOff));
          ref = srcOff + readShort(hashTable, h);
          writeShort(hashTable, h, sOff - srcOff);
          if (!readIntEquals(src, sOff, ref)) {
            break;
          }
          dOff++;
        }
        
        anchor = sOff++;
      } }
    label360:
    int runLen = srcEnd - anchor;
    if (runLen >= 270) {
      dOff += (runLen - 15) / 255;
    }
    dOff++;
    dOff += runLen;
    return dOff;
  }
  
  static final int maxCompressedLength(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("length must be >= 0, got " + length);
    }
    return length + length / 255 + 16;
  }
  
  static int hash(int i) {
    return i * -1640531535 >>> 20;
  }
  
  static int hash64k(int i) {
    return i * -1640531535 >>> 19;
  }
  
  static int readShortLittleEndian(byte[] buf, int i) {
    return buf[i] & 0xFF | (buf[(i + 1)] & 0xFF) << 8;
  }
  
  static boolean readIntEquals(byte[] buf, int i, int j) {
    return (buf[i] == buf[j]) && (buf[(i + 1)] == buf[(j + 1)]) && (buf[(i + 2)] == buf[(j + 2)]) && (buf[(i + 3)] == buf[(j + 3)]);
  }
  
  static int commonBytes(byte[] b, int o1, int o2, int limit)
  {
    int count = 0;
    while ((o2 < limit) && (b[(o1++)] == b[(o2++)])) {
      count++;
    }
    return count;
  }
  
  static int commonBytesBackward(byte[] b, int o1, int o2, int l1, int l2) {
    int count = 0;
    while ((o1 > l1) && (o2 > l2) && (b[(--o1)] == b[(--o2)])) {
      count++;
    }
    return count;
  }
  
  static int readShort(short[] buf, int off) {
    return buf[off] & 0xFFFF;
  }
  
  static byte readByte(byte[] buf, int i) {
    return buf[i];
  }
  
  static void checkRange(byte[] buf, int off) {
    if ((off < 0) || (off >= buf.length)) {
      throw new ArrayIndexOutOfBoundsException(off);
    }
  }
  
  static void checkRange(byte[] buf, int off, int len) {
    checkLength(len);
    if (len > 0) {
      checkRange(buf, off);
      checkRange(buf, off + len - 1);
    }
  }
  
  static void checkLength(int len) {
    if (len < 0) {
      throw new IllegalArgumentException("lengths must be >= 0");
    }
  }
  
  static int readIntBE(byte[] buf, int i) {
    return (buf[i] & 0xFF) << 24 | (buf[(i + 1)] & 0xFF) << 16 | (buf[(i + 2)] & 0xFF) << 8 | buf[(i + 3)] & 0xFF;
  }
  
  static int readIntLE(byte[] buf, int i)
  {
    return buf[i] & 0xFF | (buf[(i + 1)] & 0xFF) << 8 | (buf[(i + 2)] & 0xFF) << 16 | (buf[(i + 3)] & 0xFF) << 24;
  }
  
  static int readInt(byte[] buf, int i)
  {
    if (NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
      return readIntBE(buf, i);
    }
    return readIntLE(buf, i);
  }
  
  static void writeShort(short[] buf, int off, int v)
  {
    buf[off] = ((short)v);
  }
}

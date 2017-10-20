package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;

public class PngChunkFCTL
  extends PngChunkMultiple
{
  public static final String ID = "fcTL";
  public static final byte APNG_DISPOSE_OP_NONE = 0;
  public static final byte APNG_DISPOSE_OP_BACKGROUND = 1;
  public static final byte APNG_DISPOSE_OP_PREVIOUS = 2;
  public static final byte APNG_BLEND_OP_SOURCE = 0;
  public static final byte APNG_BLEND_OP_OVER = 1;
  private int seqNum;
  private int width;
  private int height;
  private int xOff;
  private int yOff;
  private int delayNum;
  private int delayDen;
  private byte disposeOp;
  private byte blendOp;
  
  public PngChunkFCTL(ImageInfo info)
  {
    super("fcTL", info);
  }
  
  public ImageInfo getEquivImageInfo() {
    return new ImageInfo(width, height, imgInfo.bitDepth, imgInfo.alpha, imgInfo.greyscale, imgInfo.indexed);
  }
  

  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NONE;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(8, true);
    int off = 0;
    PngHelperInternal.writeInt4tobytes(seqNum, data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(width, data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(height, data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(xOff, data, off);
    off += 4;
    PngHelperInternal.writeInt4tobytes(yOff, data, off);
    off += 4;
    PngHelperInternal.writeInt2tobytes(delayNum, data, off);
    off += 2;
    PngHelperInternal.writeInt2tobytes(delayDen, data, off);
    off += 2;
    data[off] = disposeOp;
    off++;
    data[off] = blendOp;
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    int off = 0;
    seqNum = PngHelperInternal.readInt4fromBytes(data, off);
    off += 4;
    width = PngHelperInternal.readInt4fromBytes(data, off);
    off += 4;
    height = PngHelperInternal.readInt4fromBytes(data, off);
    off += 4;
    xOff = PngHelperInternal.readInt4fromBytes(data, off);
    off += 4;
    yOff = PngHelperInternal.readInt4fromBytes(data, off);
    off += 4;
    delayNum = PngHelperInternal.readInt2fromBytes(data, off);
    off += 2;
    delayDen = PngHelperInternal.readInt2fromBytes(data, off);
    off += 2;
    disposeOp = data[off];
    off++;
    blendOp = data[off];
  }
  
  public int getSeqNum() {
    return seqNum;
  }
  
  public void setSeqNum(int seqNum) {
    this.seqNum = seqNum;
  }
  
  public int getWidth() {
    return width;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public int getxOff() {
    return xOff;
  }
  
  public void setxOff(int xOff) {
    this.xOff = xOff;
  }
  
  public int getyOff() {
    return yOff;
  }
  
  public void setyOff(int yOff) {
    this.yOff = yOff;
  }
  
  public int getDelayNum() {
    return delayNum;
  }
  
  public void setDelayNum(int delayNum) {
    this.delayNum = delayNum;
  }
  
  public int getDelayDen() {
    return delayDen;
  }
  
  public void setDelayDen(int delayDen) {
    this.delayDen = delayDen;
  }
  
  public byte getDisposeOp() {
    return disposeOp;
  }
  
  public void setDisposeOp(byte disposeOp) {
    this.disposeOp = disposeOp;
  }
  
  public byte getBlendOp() {
    return blendOp;
  }
  
  public void setBlendOp(byte blendOp) {
    this.blendOp = blendOp;
  }
}

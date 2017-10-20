package ar.com.hjg.pngj;




public class ChunkSeqBuffering
  extends ChunkSeqReader
{
  protected boolean checkCrc = true;
  

  public ChunkSeqBuffering() {}
  

  protected boolean isIdatKind(String id)
  {
    return false;
  }
  
  protected boolean shouldCheckCrc(int len, String id)
  {
    return checkCrc;
  }
  
  public void setCheckCrc(boolean checkCrc) {
    this.checkCrc = checkCrc;
  }
}

package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.PngjException;
import java.util.ArrayList;
import java.util.List;









public class PngMetadata
{
  private final ChunksList chunkList;
  private final boolean readonly;
  
  public PngMetadata(ChunksList chunks)
  {
    chunkList = chunks;
    if ((chunks instanceof ChunksListForWrite)) {
      readonly = false;
    } else {
      readonly = true;
    }
  }
  





  public void queueChunk(final PngChunk c, boolean lazyOverwrite)
  {
    ChunksListForWrite cl = getChunkListW();
    if (readonly)
      throw new PngjException("cannot set chunk : readonly metadata");
    if (lazyOverwrite) {
      ChunkHelper.trimList(cl.getQueuedChunks(), new ChunkPredicate() {
        public boolean match(PngChunk c2) {
          return ChunkHelper.equivalent(c, c2);
        }
      });
    }
    cl.queue(c);
  }
  
  public void queueChunk(PngChunk c) {
    queueChunk(c, true);
  }
  
  private ChunksListForWrite getChunkListW() {
    return (ChunksListForWrite)chunkList;
  }
  






  public double[] getDpi()
  {
    PngChunk c = chunkList.getById1("pHYs", true);
    if (c == null) {
      return new double[] { -1.0D, -1.0D };
    }
    return ((PngChunkPHYS)c).getAsDpi2();
  }
  
  public void setDpi(double x) {
    setDpi(x, x);
  }
  
  public void setDpi(double x, double y) {
    PngChunkPHYS c = new PngChunkPHYS(chunkList.imageInfo);
    c.setAsDpi2(x, y);
    queueChunk(c);
  }
  







  public PngChunkTIME setTimeNow(int secsAgo)
  {
    PngChunkTIME c = new PngChunkTIME(chunkList.imageInfo);
    c.setNow(secsAgo);
    queueChunk(c);
    return c;
  }
  
  public PngChunkTIME setTimeNow() {
    return setTimeNow(0);
  }
  





  public PngChunkTIME setTimeYMDHMS(int yearx, int monx, int dayx, int hourx, int minx, int secx)
  {
    PngChunkTIME c = new PngChunkTIME(chunkList.imageInfo);
    c.setYMDHMS(yearx, monx, dayx, hourx, minx, secx);
    queueChunk(c, true);
    return c;
  }
  


  public PngChunkTIME getTime()
  {
    return (PngChunkTIME)chunkList.getById1("tIME");
  }
  
  public String getTimeAsString() {
    PngChunkTIME c = getTime();
    return c == null ? "" : c.getAsString();
  }
  











  public PngChunkTextVar setText(String k, String val, boolean useLatin1, boolean compress)
  {
    if ((compress) && (!useLatin1))
      throw new PngjException("cannot compress non latin text");
    PngChunkTextVar c;
    PngChunkTextVar c; if (useLatin1) { PngChunkTextVar c;
      if (compress) {
        c = new PngChunkZTXT(chunkList.imageInfo);
      } else {
        c = new PngChunkTEXT(chunkList.imageInfo);
      }
    } else {
      c = new PngChunkITXT(chunkList.imageInfo);
      ((PngChunkITXT)c).setLangtag(k);
    }
    c.setKeyVal(k, val);
    queueChunk(c, true);
    return c;
  }
  
  public PngChunkTextVar setText(String k, String val) {
    return setText(k, val, false, false);
  }
  








  public List<? extends PngChunkTextVar> getTxtsForKey(String k)
  {
    List c = new ArrayList();
    c.addAll(chunkList.getById("tEXt", k));
    c.addAll(chunkList.getById("zTXt", k));
    c.addAll(chunkList.getById("iTXt", k));
    return c;
  }
  




  public String getTxtForKey(String k)
  {
    List<? extends PngChunkTextVar> li = getTxtsForKey(k);
    if (li.isEmpty())
      return "";
    StringBuilder t = new StringBuilder();
    for (PngChunkTextVar c : li)
      t.append(c.getVal()).append("\n");
    return t.toString().trim();
  }
  




  public PngChunkPLTE getPLTE()
  {
    return (PngChunkPLTE)chunkList.getById1("PLTE");
  }
  


  public PngChunkPLTE createPLTEChunk()
  {
    PngChunkPLTE plte = new PngChunkPLTE(chunkList.imageInfo);
    queueChunk(plte);
    return plte;
  }
  




  public PngChunkTRNS getTRNS()
  {
    return (PngChunkTRNS)chunkList.getById1("tRNS");
  }
  


  public PngChunkTRNS createTRNSChunk()
  {
    PngChunkTRNS trns = new PngChunkTRNS(chunkList.imageInfo);
    queueChunk(trns);
    return trns;
  }
}

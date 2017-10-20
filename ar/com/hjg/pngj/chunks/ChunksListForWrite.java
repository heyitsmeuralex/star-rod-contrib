package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;
import ar.com.hjg.pngj.PngjOutputException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




public class ChunksListForWrite
  extends ChunksList
{
  private final List<PngChunk> queuedChunks = new ArrayList();
  

  private HashMap<String, Integer> alreadyWrittenKeys = new HashMap();
  
  public ChunksListForWrite(ImageInfo imfinfo) {
    super(imfinfo);
  }
  


  public List<? extends PngChunk> getQueuedById(String id)
  {
    return getQueuedById(id, null);
  }
  


  public List<? extends PngChunk> getQueuedById(String id, String innerid)
  {
    return getXById(queuedChunks, id, innerid);
  }
  


  public PngChunk getQueuedById1(String id, String innerid, boolean failIfMultiple)
  {
    List<? extends PngChunk> list = getQueuedById(id, innerid);
    if (list.isEmpty())
      return null;
    if ((list.size() > 1) && ((failIfMultiple) || (!((PngChunk)list.get(0)).allowsMultiple())))
      throw new PngjException("unexpected multiple chunks id=" + id);
    return (PngChunk)list.get(list.size() - 1);
  }
  


  public PngChunk getQueuedById1(String id, boolean failIfMultiple)
  {
    return getQueuedById1(id, null, failIfMultiple);
  }
  


  public PngChunk getQueuedById1(String id)
  {
    return getQueuedById1(id, false);
  }
  





  public List<PngChunk> getQueuedEquivalent(final PngChunk c2)
  {
    ChunkHelper.filterList(queuedChunks, new ChunkPredicate() {
      public boolean match(PngChunk c) {
        return ChunkHelper.equivalent(c, c2);
      }
    });
  }
  





  public boolean removeChunk(PngChunk c)
  {
    if (c == null)
      return false;
    return queuedChunks.remove(c);
  }
  






  public boolean queue(PngChunk c)
  {
    queuedChunks.add(c);
    return true;
  }
  


  private static boolean shouldWrite(PngChunk c, int currentGroup)
  {
    if (currentGroup == 2)
      return id.equals("PLTE");
    if (currentGroup % 2 == 0)
      throw new PngjOutputException("bad chunk group?");
    int minChunkGroup;
    int maxChunkGroup; int minChunkGroup; if (c.getOrderingConstraint().mustGoBeforePLTE()) { int maxChunkGroup;
      minChunkGroup = maxChunkGroup = 1; } else { int minChunkGroup;
      if (c.getOrderingConstraint().mustGoBeforeIDAT()) {
        int maxChunkGroup = 3;
        minChunkGroup = c.getOrderingConstraint().mustGoAfterPLTE() ? 3 : 1;
      }
      else
      {
        maxChunkGroup = 5;
        minChunkGroup = 1;
      }
    }
    int preferred = maxChunkGroup;
    if (c.hasPriority())
      preferred = minChunkGroup;
    if ((ChunkHelper.isUnknown(c)) && (c.getChunkGroup() > 0))
      preferred = c.getChunkGroup();
    if (currentGroup == preferred)
      return true;
    if ((currentGroup > preferred) && (currentGroup <= maxChunkGroup))
      return true;
    return false;
  }
  
  public int writeChunks(OutputStream os, int currentGroup) {
    int cont = 0;
    Iterator<PngChunk> it = queuedChunks.iterator();
    while (it.hasNext()) {
      PngChunk c = (PngChunk)it.next();
      if (shouldWrite(c, currentGroup))
      {
        if ((ChunkHelper.isCritical(id)) && (!id.equals("PLTE")))
          throw new PngjOutputException("bad chunk queued: " + c);
        if ((alreadyWrittenKeys.containsKey(id)) && (!c.allowsMultiple()))
          throw new PngjOutputException("duplicated chunk does not allow multiple: " + c);
        c.write(os);
        chunks.add(c);
        alreadyWrittenKeys.put(id, Integer.valueOf(alreadyWrittenKeys.containsKey(id) ? ((Integer)alreadyWrittenKeys.get(id)).intValue() + 1 : 1));
        
        c.setChunkGroup(currentGroup);
        it.remove();
        cont++;
      } }
    return cont;
  }
  


  public List<PngChunk> getQueuedChunks()
  {
    return queuedChunks;
  }
  
  public String toString() {
    return "ChunkList: written: " + getChunks().size() + " queue: " + queuedChunks.size();
  }
  


  public String toStringFull()
  {
    StringBuilder sb = new StringBuilder(toString());
    sb.append("\n Written:\n");
    for (PngChunk chunk : getChunks()) {
      sb.append(chunk).append(" G=" + chunk.getChunkGroup() + "\n");
    }
    if (!queuedChunks.isEmpty()) {
      sb.append(" Queued:\n");
      for (PngChunk chunk : queuedChunks) {
        sb.append(chunk).append("\n");
      }
    }
    
    return sb.toString();
  }
}

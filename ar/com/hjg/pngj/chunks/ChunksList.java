package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;
import java.util.ArrayList;
import java.util.List;













public class ChunksList
{
  public static final int CHUNK_GROUP_0_IDHR = 0;
  public static final int CHUNK_GROUP_1_AFTERIDHR = 1;
  public static final int CHUNK_GROUP_2_PLTE = 2;
  public static final int CHUNK_GROUP_3_AFTERPLTE = 3;
  public static final int CHUNK_GROUP_4_IDAT = 4;
  public static final int CHUNK_GROUP_5_AFTERIDAT = 5;
  public static final int CHUNK_GROUP_6_END = 6;
  List<PngChunk> chunks = new ArrayList();
  

  final ImageInfo imageInfo;
  

  boolean withPlte = false;
  
  public ChunksList(ImageInfo imfinfo) {
    imageInfo = imfinfo;
  }
  



  public List<PngChunk> getChunks()
  {
    return chunks;
  }
  
  protected static List<PngChunk> getXById(List<PngChunk> list, String id, final String innerid)
  {
    if (innerid == null) {
      ChunkHelper.filterList(list, new ChunkPredicate() {
        public boolean match(PngChunk c) {
          return id.equals(val$id);
        }
      });
    }
    ChunkHelper.filterList(list, new ChunkPredicate() {
      public boolean match(PngChunk c) {
        if (!id.equals(val$id))
          return false;
        if (((c instanceof PngChunkTextVar)) && (!((PngChunkTextVar)c).getKey().equals(innerid)))
          return false;
        if (((c instanceof PngChunkSPLT)) && (!((PngChunkSPLT)c).getPalName().equals(innerid)))
          return false;
        return true;
      }
    });
  }
  


  public void appendReadChunk(PngChunk chunk, int chunkGroup)
  {
    chunk.setChunkGroup(chunkGroup);
    chunks.add(chunk);
    if (id.equals("PLTE")) {
      withPlte = true;
    }
  }
  




  public List<? extends PngChunk> getById(String id)
  {
    return getById(id, null);
  }
  






  public List<? extends PngChunk> getById(String id, String innerid)
  {
    return getXById(chunks, id, innerid);
  }
  





  public PngChunk getById1(String id)
  {
    return getById1(id, false);
  }
  





  public PngChunk getById1(String id, boolean failIfMultiple)
  {
    return getById1(id, null, failIfMultiple);
  }
  





  public PngChunk getById1(String id, String innerid, boolean failIfMultiple)
  {
    List<? extends PngChunk> list = getById(id, innerid);
    if (list.isEmpty())
      return null;
    if ((list.size() > 1) && ((failIfMultiple) || (!((PngChunk)list.get(0)).allowsMultiple())))
      throw new PngjException("unexpected multiple chunks id=" + id);
    return (PngChunk)list.get(list.size() - 1);
  }
  





  public List<PngChunk> getEquivalent(final PngChunk c2)
  {
    ChunkHelper.filterList(chunks, new ChunkPredicate() {
      public boolean match(PngChunk c) {
        return ChunkHelper.equivalent(c, c2);
      }
    });
  }
  
  public String toString() {
    return "ChunkList: read: " + chunks.size();
  }
  


  public String toStringFull()
  {
    StringBuilder sb = new StringBuilder(toString());
    sb.append("\n Read:\n");
    for (PngChunk chunk : chunks) {
      sb.append(chunk).append(" G=" + chunk.getChunkGroup() + "\n");
    }
    return sb.toString();
  }
}

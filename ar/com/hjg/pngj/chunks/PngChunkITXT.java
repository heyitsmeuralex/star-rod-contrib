package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;






public class PngChunkITXT
  extends PngChunkTextVar
{
  public static final String ID = "iTXt";
  private boolean compressed = false;
  private String langTag = "";
  private String translatedTag = "";
  
  public PngChunkITXT(ImageInfo info)
  {
    super("iTXt", info);
  }
  
  public ChunkRaw createRawChunk()
  {
    if ((key == null) || (key.trim().length() == 0))
      throw new PngjException("Text chunk key must be non empty");
    try {
      ByteArrayOutputStream ba = new ByteArrayOutputStream();
      ba.write(ChunkHelper.toBytes(key));
      ba.write(0);
      ba.write(compressed ? 1 : 0);
      ba.write(0);
      ba.write(ChunkHelper.toBytes(langTag));
      ba.write(0);
      ba.write(ChunkHelper.toBytesUTF8(translatedTag));
      ba.write(0);
      byte[] textbytes = ChunkHelper.toBytesUTF8(val);
      if (compressed) {
        textbytes = ChunkHelper.compressBytes(textbytes, true);
      }
      ba.write(textbytes);
      byte[] b = ba.toByteArray();
      ChunkRaw chunk = createEmptyChunk(b.length, false);
      data = b;
      return chunk;
    } catch (IOException e) {
      throw new PngjException(e);
    }
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    int nullsFound = 0;
    int[] nullsIdx = new int[3];
    for (int i = 0; i < data.length; i++)
      if (data[i] == 0)
      {
        nullsIdx[nullsFound] = i;
        nullsFound++;
        if (nullsFound == 1)
          i += 2;
        if (nullsFound == 3)
          break;
      }
    if (nullsFound != 3)
      throw new PngjException("Bad formed PngChunkITXT chunk");
    key = ChunkHelper.toString(data, 0, nullsIdx[0]);
    int i = nullsIdx[0] + 1;
    compressed = (data[i] != 0);
    i++;
    if ((compressed) && (data[i] != 0))
      throw new PngjException("Bad formed PngChunkITXT chunk - bad compression method ");
    langTag = ChunkHelper.toString(data, i, nullsIdx[1] - i);
    translatedTag = ChunkHelper.toStringUTF8(data, nullsIdx[1] + 1, nullsIdx[2] - nullsIdx[1] - 1);
    
    i = nullsIdx[2] + 1;
    if (compressed) {
      byte[] bytes = ChunkHelper.compressBytes(data, i, data.length - i, false);
      val = ChunkHelper.toStringUTF8(bytes);
    } else {
      val = ChunkHelper.toStringUTF8(data, i, data.length - i);
    }
  }
  
  public boolean isCompressed() {
    return compressed;
  }
  
  public void setCompressed(boolean compressed) {
    this.compressed = compressed;
  }
  
  public String getLangtag() {
    return langTag;
  }
  
  public void setLangtag(String langtag) {
    langTag = langtag;
  }
  
  public String getTranslatedTag() {
    return translatedTag;
  }
  
  public void setTranslatedTag(String translatedTag) {
    this.translatedTag = translatedTag;
  }
}

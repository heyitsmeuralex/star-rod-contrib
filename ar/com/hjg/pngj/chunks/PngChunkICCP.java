package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;





public class PngChunkICCP
  extends PngChunkSingle
{
  public static final String ID = "iCCP";
  private String profileName;
  private byte[] compressedProfile;
  
  public PngChunkICCP(ImageInfo info)
  {
    super("iCCP", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(profileName.length() + compressedProfile.length + 2, true);
    System.arraycopy(ChunkHelper.toBytes(profileName), 0, data, 0, profileName.length());
    data[profileName.length()] = 0;
    data[(profileName.length() + 1)] = 0;
    System.arraycopy(compressedProfile, 0, data, profileName.length() + 2, compressedProfile.length);
    
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    int pos0 = ChunkHelper.posNullByte(data);
    profileName = ChunkHelper.toString(data, 0, pos0);
    int comp = data[(pos0 + 1)] & 0xFF;
    if (comp != 0)
      throw new PngjException("bad compression for ChunkTypeICCP");
    int compdatasize = data.length - (pos0 + 2);
    compressedProfile = new byte[compdatasize];
    System.arraycopy(data, pos0 + 2, compressedProfile, 0, compdatasize);
  }
  


  public void setProfileNameAndContent(String name, byte[] profile)
  {
    profileName = name;
    compressedProfile = ChunkHelper.compressBytes(profile, true);
  }
  
  public void setProfileNameAndContent(String name, String profile) {
    setProfileNameAndContent(name, ChunkHelper.toBytes(profile));
  }
  
  public String getProfileName() {
    return profileName;
  }
  


  public byte[] getProfile()
  {
    return ChunkHelper.compressBytes(compressedProfile, false);
  }
  
  public String getProfileAsString() {
    return ChunkHelper.toString(getProfile());
  }
}

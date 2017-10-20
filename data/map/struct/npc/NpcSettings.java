package data.map.struct.npc;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.nio.ByteBuffer;


public class NpcSettings
  extends BaseStruct.MapStruct
{
  public static final NpcSettings instance = new NpcSettings();
  

  private NpcSettings() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.getInt();
    fileBuffer.getInt();
    
    int ptrScript;
    
    if ((ptrScript = fileBuffer.getInt()) != 0) {
      decoder.enqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
    }
    if ((ptrScript = fileBuffer.getInt()) != 0) {
      decoder.enqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
    }
    

    if ((ptrScript = fileBuffer.getInt()) != 0)
    {
      Pointer scriptInfo = decoder.enqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
      scriptInfo.setDescriptor("NpcAI");
    }
    




    if ((ptrScript = fileBuffer.getInt()) != 0)
    {
      if ((decoder.tryEnqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT) == null) && 
        (!$assertionsDisabled) && (ptrScript != -2146992272)) { throw new AssertionError();
      }
    }
    if ((ptrScript = fileBuffer.getInt()) != 0) {
      decoder.enqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
    }
    if ((ptrScript = fileBuffer.getInt()) != 0)
    {
      if ((decoder.tryEnqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT) == null) && 
        (!$assertionsDisabled) && (ptrScript != -2146991972)) { throw new AssertionError();
      }
    }
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
  }
}

package data.map.struct.npc;

import data.map.MapStructTypes;
import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import reports.BattleMapTracker;


public class NpcGroupList
  extends BaseStruct.MapStruct
{
  public static final NpcGroupList instance = new NpcGroupList();
  

  private NpcGroupList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int numNPCs;
    while ((numNPCs = fileBuffer.getInt()) != 0)
    {
      int ptrNPCs = fileBuffer.getInt();
      int battleID = fileBuffer.getInt();
      
      Pointer groupInfo = decoder.enqueueAsChild(ptr, ptrNPCs, MapStructTypes.NpcGroupT);
      listLength = numNPCs;
      npcBattleID = battleID;
      BattleMapTracker.add(decoder.getSourceName(), battleID);
    }
    fileBuffer.getInt();
    fileBuffer.getInt();
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 3);
  }
}

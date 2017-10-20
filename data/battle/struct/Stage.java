package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import util.IOUtils;




public class Stage
  extends BaseStruct.BattleStruct
{
  public static final Stage instance = new Stage();
  

  private Stage() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int ptrTexName = fileBuffer.getInt();
    if (ptrTexName != 0) {
      decoder.enqueueAsChild(ptr, ptrTexName, SharedStructTypes.StringT);
    }
    int ptrShapeName = fileBuffer.getInt();
    if (ptrShapeName != 0) {
      decoder.enqueueAsChild(ptr, ptrShapeName, SharedStructTypes.StringT);
    }
    int ptrHitName = fileBuffer.getInt();
    if (ptrHitName != 0) {
      decoder.enqueueAsChild(ptr, ptrHitName, SharedStructTypes.StringT);
    }
    int pos = fileBuffer.position();
    fileBuffer.position(decoder.toOffset(ptrShapeName));
    String mapName = IOUtils.readString(fileBuffer);
    fileBuffer.position(pos);
    
    assert (mapName.endsWith("_shape"));
    mapName = mapName.substring(0, mapName.length() - 6);
    

    Pointer beforeScript = decoder.enqueueAsChild(ptr, fileBuffer.getInt(), SharedStructTypes.ScriptT);
    beforeScript.setDescriptor("BeforeBattle");
    mapName = mapName;
    


    Pointer afterScript = decoder.enqueueAsChild(ptr, fileBuffer.getInt(), SharedStructTypes.ScriptT);
    afterScript.setDescriptor("AfterBattle");
    mapName = mapName;
    
    int ptrBackgroundName = fileBuffer.getInt();
    if (ptrBackgroundName != 0) {
      decoder.enqueueAsChild(ptr, ptrBackgroundName, SharedStructTypes.StringT);
    }
    int ptrForegroundList = fileBuffer.getInt();
    if (ptrForegroundList != 0) {
      enqueueAsChildForegroundListTmapName = mapName;
    }
    int encounterSize = fileBuffer.getInt();
    int ptrSpecialFormation = fileBuffer.getInt();
    
    if (encounterSize > 0)
    {

      Pointer specialInfo = decoder.enqueueAsChild(ptr, ptrSpecialFormation, BattleStructTypes.SpecialFormationT);
      listLength = encounterSize;
    }
    

    fileBuffer.getInt();
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int v = fileBuffer.getInt();
    if (v != 0) {
      decoder.printWord(pw, v);
      pw.println(" % " + getPointercomment);
    } else {
      pw.println("00000000 ");
    }
    
    v = fileBuffer.getInt();
    if (v != 0) {
      decoder.printWord(pw, v);
      pw.println(" % " + getPointercomment);
    } else {
      pw.println("00000000 ");
    }
    
    v = fileBuffer.getInt();
    if (v != 0) {
      decoder.printWord(pw, v);
      pw.println(" % " + getPointercomment);
    } else {
      pw.println("00000000 ");
    }
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    v = fileBuffer.getInt();
    if (v != 0) {
      decoder.printWord(pw, v);
      pw.println(" % " + getPointercomment);
    } else {
      pw.println("00000000 ");
    }
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
  }
}

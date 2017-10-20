package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import reports.BattleMapTracker;




public class FormationTable
  extends BaseStruct.BattleStruct
{
  public static final FormationTable instance = new FormationTable();
  

  private FormationTable() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int formationIndex = 0;
    
    int v;
    while ((v = fileBuffer.getInt()) != 0)
    {
      int ptrSJIS = v;
      decoder.enqueueAsChild(ptr, ptrSJIS, BattleStructTypes.SjisT);
      
      int numEnemies = fileBuffer.getInt();
      int ptrFormation = fileBuffer.getInt();
      Pointer formationInfo = decoder.enqueueAsChild(ptr, ptrFormation, BattleStructTypes.IndexedFormationT);
      listIndex = formationIndex;
      listLength = numEnemies;
      
      int ptrResourcesList = fileBuffer.getInt();
      decoder.enqueueAsChild(ptr, ptrResourcesList, BattleStructTypes.StageT);
      

      int ptrUnknownScript = fileBuffer.getInt();
      if (ptrUnknownScript != 0) {
        decoder.enqueueAsChild(ptr, ptrUnknownScript, SharedStructTypes.ScriptT);
      }
      formationIndex++;
    }
    
    fileBuffer.position(fileBuffer.position() + 16);
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    for (int i = 0; i < length / 20; i++)
    {
      for (int j = 0; j < 5; j++)
      {
        int v = fileBuffer.getInt();
        decoder.printWord(pw, v);
      }
      
      int currentSection = decoder.getSectionID();
      assert (currentSection >= 0);
      int battleID = currentSection << 24 | i << 16;
      if ((BattleMapTracker.isEnabled()) && (!BattleMapTracker.hasBattleID(battleID))) {
        pw.print("% unused");
      }
      pw.println();
    }
  }
}

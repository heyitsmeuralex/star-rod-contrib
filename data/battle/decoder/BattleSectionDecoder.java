package data.battle.decoder;

import data.battle.BattleStructTypes;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import data.shared.struct.StructType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import main.Directories;
import util.IOUtils;
import util.IdentityHashSet;






public class BattleSectionDecoder
  extends BattleDecoder
{
  private final int sectionID;
  private final List<Pointer> enemyList;
  
  public int getSectionID()
  {
    return sectionID;
  }
  

  public BattleSectionDecoder(ByteBuffer fileBuffer, int section)
    throws IOException
  {
    sectionID = section;
    sourceName = data.battle.BattleConstants.SECTION_NAMES[section];
    readTableEntry(fileBuffer, section);
    
    enemyList = new LinkedList();
    super.decode(fileBuffer);
    
    File scriptFile = new File(Directories.DUMP_BATTLE_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_BATTLE_SRC + sourceName + ".bidx");
    File rawFile = new File(Directories.DUMP_BATTLE_RAW + sourceName + ".bin");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
    
    printEnemyFiles(fileBuffer);
  }
  
  private void readTableEntry(ByteBuffer fileBuffer, int section)
  {
    fileBuffer.position(462384 + section * 32);
    
    fileBuffer.getInt();
    int startOffset = fileBuffer.getInt();
    int endOffset = fileBuffer.getInt();
    int startAddress = fileBuffer.getInt();
    int ptrBattleTable = fileBuffer.getInt();
    int ptrMapTable = fileBuffer.getInt();
    fileBuffer.getInt();
    int ptrDmaTable = fileBuffer.getInt();
    
    int endAddress = endOffset - startOffset + startAddress;
    setAddressRange(startAddress, endAddress, -2145157120);
    setOffsetRange(startOffset, endOffset);
    

    findLocalPointers(fileBuffer);
    
    tryEnqueueAsRoot(ptrBattleTable, BattleStructTypes.FormationTableT, Pointer.Origin.DECODED);
    tryEnqueueAsRoot(ptrMapTable, BattleStructTypes.StageTableT, Pointer.Origin.DECODED);
    tryEnqueueAsRoot(ptrDmaTable, BattleStructTypes.DmaArgTableT, Pointer.Origin.DECODED);
  }
  
  private void printEnemyFiles(ByteBuffer fileBuffer) throws IOException
  {
    ArrayList<Integer> pointerList = getSortedLocalPointerList();
    
    for (Iterator localIterator1 = pointerList.iterator(); localIterator1.hasNext();) { int i = ((Integer)localIterator1.next()).intValue();
      
      Pointer ptr = (Pointer)localPointerMap.get(Integer.valueOf(i));
      ptr.setImportAffix(String.format("%02X", new Object[] { Integer.valueOf(sectionID) }));
    }
    
    for (Map.Entry<Integer, Pointer> e : localPointerMap.entrySet())
    {
      Pointer ptr = (Pointer)e.getValue();
      if (type == BattleStructTypes.ActorT) {
        enemyList.add(ptr);
      }
    }
    for (Pointer enemy : enemyList)
    {
      String enemyName = enemy.getImportName();
      File f = new File(Directories.DUMP_BATTLE_ENEMY + enemyName + ".bpat");
      
      PrintWriter pw = IOUtils.getBufferedPrintWriter(f);
      
      pw.printf("%% Automatically dumped from section %02X\r\n", new Object[] { Integer.valueOf(sectionID) });
      pw.println();
      
      pw.println("#new:" + BattleStructTypes.ActorT.toString() + " $" + enemyName);
      printPointer(enemy, fileBuffer, address, pw);
      pw.println();
      
      for (Iterator localIterator2 = pointerList.iterator(); localIterator2.hasNext();) { int address = ((Integer)localIterator2.next()).intValue();
        
        Pointer pointer = (Pointer)localPointerMap.get(Integer.valueOf(address));
        if (ancestors.contains(enemy))
        {
          pw.println("#new:" + type.toString() + " " + pointer.getPointerName());
          printPointer(pointer, fileBuffer, address, pw);
          pw.println();
        }
      }
      pw.close();
    }
  }
}

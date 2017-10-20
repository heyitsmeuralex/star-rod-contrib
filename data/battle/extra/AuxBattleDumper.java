package data.battle.extra;

import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import main.Directories;
import util.IOUtils;
import util.Logger;
import util.Priority;




public class AuxBattleDumper
{
  public AuxBattleDumper() {}
  
  public static void dumpMoves(ByteBuffer fileBuffer)
    throws IOException
  {
    List<Integer> startingOffsets = new LinkedList();
    HashMap<Integer, MoveDecoder.MoveSectionData> scriptDataMap = new HashMap();
    
    File configFile = new File(Directories.DUMP_MOVE + "Moves.txt");
    PrintWriter pw = IOUtils.getBufferedPrintWriter(configFile);
    pw.println("% Move table dumped from 0x1C2760");
    
    Logger.log(String.format("Dumping move data...", new Object[0]), Priority.MILESTONE);
    for (int i = 0; i < 49; i++)
    {
      pw.printf("%02X  ", new Object[] { Integer.valueOf(i) });
      
      if (i < 3)
      {
        pw.println("null");
      }
      else
      {
        fileBuffer.position(1845088 + i * 16);
        
        start = fileBuffer.getInt();
        int end = fileBuffer.getInt();
        int startAddress = fileBuffer.getInt();
        int ptrMainScript = fileBuffer.getInt();
        assert (startAddress == -2144727040);
        
        String scriptName = "";
        String moveName = DataConstants.getMoveName(i).replaceAll("\\s+", "");
        
        switch (start) {
        case 7554464: 
          scriptName = "Move_Hammer"; break;
        case 7663920:  scriptName = "Move_HammerCharge0"; break;
        case 7706032:  scriptName = "Move_HammerCharge1"; break;
        case 7726544:  scriptName = "Move_HammerCharge2"; break;
        case 7592032:  scriptName = "Move_Jump"; break;
        case 7656816:  scriptName = "Move_JumpCharge0"; break;
        case 7719296:  scriptName = "Move_JumpCharge1"; break;
        case 7739680:  scriptName = "Move_JumpCharge2"; break;
        default:  scriptName = "Move_" + moveName;
        }
        
        if (!startingOffsets.contains(Integer.valueOf(start)))
        {
          scriptDataMap.put(Integer.valueOf(start), new MoveDecoder.MoveSectionData(scriptName, start, end));
          startingOffsets.add(Integer.valueOf(start));
        }
        

        MoveDecoder.MoveSectionData section = (MoveDecoder.MoveSectionData)scriptDataMap.get(Integer.valueOf(start));
        String mainName; if (!mainAddressList.contains(Integer.valueOf(ptrMainScript)))
        {
          String mainName = "Script_UseMove" + mainAddressList.size();
          mainAddressList.add(Integer.valueOf(ptrMainScript));
          mainNameList.add(mainName);
        }
        else
        {
          int j = mainAddressList.indexOf(Integer.valueOf(ptrMainScript));
          mainName = (String)mainNameList.get(j);
        }
        
        pw.printf("%-20s %-20s %% %s\r\n", new Object[] { name, mainName, moveName });
      }
    }
    pw.close();
    

    MoveDecoder.MoveSectionData unusedScriptData = new MoveDecoder.MoveSectionData("Moves_Unused", 7838864, 7850880);
    mainAddressList.add(Integer.valueOf(-2144721180));
    mainNameList.add("Script_UseMove0");
    scriptDataMap.put(Integer.valueOf(7838864), unusedScriptData);
    
    for (int start = scriptDataMap.keySet().iterator(); start.hasNext();) { int i = ((Integer)start.next()).intValue();
      
      Logger.log("Generating source files for move: " + getvalueOfname, Priority.MILESTONE);
      MoveDecoder decoder = new MoveDecoder(fileBuffer, (MoveDecoder.MoveSectionData)scriptDataMap.get(Integer.valueOf(i)));
      int unknownPointers = unknownPointers;
      int missingSections = missingSections;
      
      if (unknownPointers > 0) {
        Logger.log(decoder.getSourceName() + " contains " + unknownPointers + " unknown pointers!");
      }
      if (missingSections > 0) {
        Logger.log(decoder.getSourceName() + " contains " + missingSections + " missing sections!");
      }
      Logger.log("");
    }
  }
  
  public static void dumpPartnerMoves(ByteBuffer fileBuffer) throws IOException
  {
    for (int i = 0; i < 11; i++)
    {
      if (i != 9)
      {

        fileBuffer.position(1779716 + i * 20);
        
        int start = fileBuffer.getInt();
        int end = fileBuffer.getInt();
        fileBuffer.getInt();
        int actor = fileBuffer.getInt();
        fileBuffer.getInt();
        
        String scriptName = String.format("%02X %s", new Object[] { Integer.valueOf(i), data.battle.BattleConstants.PARTNER_SCRIPT_NAMES[i] });
        
        Logger.log("Generating source files for partner moves: " + scriptName, Priority.MILESTONE);
        PartnerDecoder decoder = new PartnerDecoder(fileBuffer, scriptName, start, end, actor);
        int unknownPointers = unknownPointers;
        int missingSections = missingSections;
        
        if (unknownPointers > 0) {
          Logger.log(scriptName + " contains " + unknownPointers + " unknown pointers!");
        }
        if (missingSections > 0) {
          Logger.log(scriptName + " contains " + missingSections + " missing sections!");
        }
        Logger.log("");
      }
    }
  }
  
  public static void dumpStarPowers(ByteBuffer fileBuffer) throws IOException {
    for (int i = 0; i < 12; i++)
    {
      fileBuffer.position(1880240 + i * 16);
      
      int start = fileBuffer.getInt();
      int end = fileBuffer.getInt();
      fileBuffer.getInt();
      int main = fileBuffer.getInt();
      
      String scriptName = String.format("%02X %s", new Object[] { Integer.valueOf(i), data.battle.BattleConstants.STAR_POWER_NAME[i] });
      
      Logger.log("Generating source files for star power: " + scriptName, Priority.MILESTONE);
      StarPowerDecoder decoder = new StarPowerDecoder(fileBuffer, scriptName, start, end, main);
      int unknownPointers = unknownPointers;
      int missingSections = missingSections;
      
      if (unknownPointers > 0) {
        Logger.log(scriptName + " contains " + unknownPointers + " unknown pointers!");
      }
      if (missingSections > 0) {
        Logger.log(scriptName + " contains " + missingSections + " missing sections!");
      }
      Logger.log("");
    }
  }
  
  public static void dumpItemScripts(ByteBuffer fileBuffer) throws IOException
  {
    File configFile = new File(Directories.DUMP_ITEM + "Items.txt");
    PrintWriter pw = IOUtils.getBufferedPrintWriter(configFile);
    
    fileBuffer.position(1844320);
    int numItemScripts = 32;
    
    int[] ids = new int[numItemScripts];
    for (int i = 0; i < numItemScripts; i++) {
      ids[i] = fileBuffer.getInt();
    }
    List<Integer> decodedList = new ArrayList(32);
    
    for (int i = 0; i < numItemScripts; i++)
    {
      String itemName = i == 0 ? null : DataConstants.ItemType.getName(ids[i]);
      String scriptName = "";
      
      fileBuffer.position(1844452 + 16 * i);
      
      int start = fileBuffer.getInt();
      int end = fileBuffer.getInt();
      fileBuffer.getInt();
      int main = fileBuffer.getInt();
      
      if (i > 0)
      {
        switch (start) {
        case 7549136: 
          scriptName = "Item_Food"; break;
        case 7428176:  scriptName = "Item_Shroom"; break;
        case 7541504:  scriptName = "Item_StrangeCake"; break;
        default:  scriptName = "Item_" + itemName;
        }
        pw.printf("%08X  %-21s %% %s\r\n", new Object[] { Integer.valueOf(ids[i]), scriptName, itemName });
      } else {
        scriptName = "Item_Food";
        pw.printf("%08X  %-21s\r\n", new Object[] { Integer.valueOf(ids[i]), scriptName });
      }
      
      if (!decodedList.contains(Integer.valueOf(start)))
      {
        Logger.log("Generating source files for item: " + scriptName, Priority.MILESTONE);
        ItemDecoder decoder = new ItemDecoder(fileBuffer, scriptName, start, end, main);
        int unknownPointers = unknownPointers;
        int missingSections = missingSections;
        
        if (unknownPointers > 0) {
          Logger.log(scriptName + " contains " + unknownPointers + " unknown pointers!");
        }
        if (missingSections > 0) {
          Logger.log(scriptName + " contains " + missingSections + " missing sections!");
        }
        Logger.log("");
        
        decodedList.add(Integer.valueOf(start));
      }
    }
    
    Logger.log(String.format("Generating source files for partner moves: Unused_DriedShroom", new Object[0]), Priority.MILESTONE);
    new ItemDecoder(fileBuffer, "Unused_DriedShroom", 7458016, 7460720, -2144724740);
    Logger.log("");
    
    Logger.log(String.format("Generating source files for partner moves: Unused_UltraShroom", new Object[0]), Priority.MILESTONE);
    new ItemDecoder(fileBuffer, "Unused_UltraShroom", 7485312, 7490784, -2144721988, new int[] { -2144723456 });
    Logger.log("");
    
    pw.close();
  }
}

package data.battle;

import data.battle.decoder.BattleSectionDecoder;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import main.Directories;
import util.IOUtils;
import util.Logger;
import util.Priority;




public class BattleDumper
{
  public BattleDumper() {}
  
  public static void dumpBattles(ByteBuffer fileBuffer)
    throws IOException
  {
    PrintWriter pw = IOUtils.getBufferedPrintWriter(Directories.DUMP_BATTLE + "BattleSections.txt");
    
    int totalUnknownPointers = 0;
    int totalMissingSections = 0;
    
    for (int section = 0; section < 48; section++)
    {
      if ((section == 40) || (section == 47))
      {
        pw.println("empty");
      }
      else
      {
        Logger.log("Generating source files for battles: " + String.format(BattleConstants.SECTION_NAMES[section], new Object[0]), Priority.MILESTONE);
        
        BattleSectionDecoder decoder = new BattleSectionDecoder(fileBuffer, section);
        pw.printf("%08X : %s\r\n", new Object[] { Integer.valueOf(decoder.getStartAddress()), BattleConstants.SECTION_NAMES[section] });
        
        int unknownPointers = unknownPointers;
        int missingSections = missingSections;
        totalUnknownPointers += unknownPointers;
        totalMissingSections += missingSections;
        
        if (unknownPointers > 0) {
          Logger.log("Found " + unknownPointers + " unknown pointers.");
        }
        if (missingSections > 0) {
          Logger.log("Missing " + missingSections + " sections!");
        }
        Logger.log("");
      }
    }
    Logger.log(totalUnknownPointers + " total unknown pointers!", Priority.IMPORTANT);
    Logger.log(totalMissingSections + " total missing sections!", Priority.IMPORTANT);
    pw.close();
  }
}

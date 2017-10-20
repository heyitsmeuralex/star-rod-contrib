package data.battle.encoder;

import data.battle.BattlePatcher.BattleConfig;
import data.battle.BattleStructTypes;
import data.shared.DataConstants;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.InvalidExpressionException;
import data.shared.struct.Struct;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import main.Directories;
import patcher.SymbolDatabase;
import util.IOUtils;
import util.Logger;









public class BattleEncoder
  extends DataEncoder
{
  public BattleEncoder(SymbolDatabase db)
    throws IOException
  {
    super(BattleStructTypes.battleTypes, db);
    setFunctionNameMap(DataConstants.battleFunctionLib, DataConstants.battleScriptLib);
    setAddressLimit(-2145157120);
    setImportDirectory(new Directories[] { Directories.MOD_BATTLE_IMPORT, Directories.MOD_BATTLE_ENEMY });
  }
  
  public void encode(BattlePatcher.BattleConfig cfg) throws IOException
  {
    File patchFile = new File(Directories.MOD_BATTLE_PATCH + name + ".bpat");
    if (!patchFile.exists())
    {
      Logger.logDetail("No patch file found for section " + name);
      return;
    }
    
    File indexFile = new File(Directories.DUMP_BATTLE_SRC + name + ".bidx");
    File rawFile = new File(Directories.DUMP_BATTLE_RAW + name + ".bin");
    File outFile = new File(Directories.MOD_BATTLE_TEMP + name + ".bin");
    File outIndexFile = new File(Directories.MOD_BATTLE_TEMP + name + ".bidx");
    
    if (!rawFile.exists())
    {
      fileBuffer = ByteBuffer.allocateDirect(0);
      createSection(startAddress, -2145157120 - startAddress);
    } else {
      fileBuffer = IOUtils.getDirectBuffer(rawFile);
      readIndexFile(indexFile);
    }
    
    Logger.log("Reading patch file: " + patchFile.getName());
    readPatchFile(patchFile);
    encode(patchFile, outFile, outIndexFile);
  }
  

  protected void removeJapaneseStrings(Collection<Struct> structs)
  {
    for (Struct str : structs)
    {
      if (str.isTypeOf(BattleStructTypes.SjisT))
      {
        forceDelete(str);
      }
    }
  }
  
  protected void replaceExpression(String[] args, List<String> newTokenList)
    throws InvalidExpressionException
  {}
}

package data.battle.extra;

import data.battle.encoder.BattleEncoder;
import java.io.File;
import java.io.IOException;
import main.Directories;
import patcher.SymbolDatabase;
import util.IOUtils;






public class StarPowerEncoder
  extends BattleEncoder
{
  public StarPowerEncoder(SymbolDatabase db)
    throws IOException
  {
    super(db);
    setAddressLimit(-2144710656);
    setImportDirectory(new Directories[0]);
  }
  
  public void encode(String moveName) throws IOException
  {
    File patchFile = new File(Directories.MOD_STARS_PATCH + moveName + ".bpat");
    File indexFile = new File(Directories.DUMP_STARS_SRC + moveName + ".bidx");
    File rawFile = new File(Directories.DUMP_STARS_RAW + moveName + ".bin");
    File outFile = new File(Directories.MOD_STARS_TEMP + moveName + ".bin");
    File outIndexFile = new File(Directories.MOD_STARS_TEMP + moveName + ".bidx");
    
    fileBuffer = IOUtils.getDirectBuffer(rawFile);
    
    readIndexFile(indexFile);
    readPatchFile(patchFile);
    
    encode(patchFile, outFile, outIndexFile);
  }
}

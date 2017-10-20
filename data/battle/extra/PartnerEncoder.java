package data.battle.extra;

import data.battle.encoder.BattleEncoder;
import java.io.File;
import java.io.IOException;
import main.Directories;
import patcher.SymbolDatabase;
import util.IOUtils;






public class PartnerEncoder
  extends BattleEncoder
{
  public PartnerEncoder(SymbolDatabase db)
    throws IOException
  {
    super(db);
    setAddressLimit(-2145132544);
    setImportDirectory(new Directories[0]);
  }
  
  public void encode(String partnerName) throws IOException
  {
    File patchFile = new File(Directories.MOD_ALLY_PATCH + partnerName + ".bpat");
    File indexFile = new File(Directories.DUMP_ALLY_SRC + partnerName + ".bidx");
    File rawFile = new File(Directories.DUMP_ALLY_RAW + partnerName + ".bin");
    File outFile = new File(Directories.MOD_ALLY_TEMP + partnerName + ".bin");
    File outIndexFile = new File(Directories.MOD_ALLY_TEMP + partnerName + ".bidx");
    
    fileBuffer = IOUtils.getDirectBuffer(rawFile);
    
    readIndexFile(indexFile);
    readPatchFile(patchFile);
    
    encode(patchFile, outFile, outIndexFile);
  }
}

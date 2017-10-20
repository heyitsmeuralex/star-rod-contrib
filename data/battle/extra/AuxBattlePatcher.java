package data.battle.extra;

import java.io.IOException;
import patcher.Patcher;


public class AuxBattlePatcher
{
  private MovePatcher movePatcher;
  private PartnerPatcher partnerPatcher;
  private ItemPatcher itemPatcher;
  private StarPowerPatcher starPatcher;
  
  public AuxBattlePatcher(Patcher patcher)
  {
    movePatcher = new MovePatcher(patcher);
    partnerPatcher = new PartnerPatcher(patcher);
    itemPatcher = new ItemPatcher(patcher);
    starPatcher = new StarPowerPatcher(patcher);
  }
  
  public void patchData() throws IOException
  {
    movePatcher.patchMoveData();
    partnerPatcher.patchPartnerData();
    itemPatcher.patchItemData();
    starPatcher.patchStarPowerData();
  }
  
  public void generateConfigs() throws IOException
  {
    movePatcher.generateConfigs();
    partnerPatcher.generateConfigs();
    itemPatcher.generateConfigs();
    starPatcher.generateConfigs();
  }
  
  public void writeTables() throws IOException
  {
    movePatcher.writeMoveTable();
    partnerPatcher.writePartnerTable();
    itemPatcher.writeItemTable();
    starPatcher.writeStarPowerTable();
  }
  
  public void writeData() throws IOException
  {
    movePatcher.writeMoveData();
    partnerPatcher.writePartnerData();
    itemPatcher.writeItemData();
    starPatcher.writeStarPowerData();
  }
}

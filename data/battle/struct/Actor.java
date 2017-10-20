package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.battle.encoder.BattleEncoder;
import data.shared.Attribute;
import data.shared.Attribute.Style;
import data.shared.BaseStruct.BattleStruct;
import data.shared.DataConstants;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import data.shared.encoder.Patch;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import util.IdentityHashSet;


public class Actor
  extends BaseStruct.BattleStruct
{
  public static final Actor instance = new Actor();
  



  public static final String[] nameIDs = new String['Ô'];
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int start = fileBuffer.position();
    fileBuffer.position(start + 5);
    
    int nameIndex = fileBuffer.get() & 0xFF;
    ptr.setDescriptor(DataConstants.getActorName(nameIndex));
    
    if (nameIDs[nameIndex] == null) {
      nameIDs[nameIndex] = decoder.getSourceName();
    }
    fileBuffer.position(start + 8);
    int numStates = fileBuffer.getShort();
    fileBuffer.getShort();
    
    int ptrStateTable = fileBuffer.getInt();
    int ptrAIScript = fileBuffer.getInt();
    int ptrStatusTable = fileBuffer.getInt();
    
    Pointer stateTableInfo = decoder.enqueueAsChild(ptr, ptrStateTable, BattleStructTypes.SpriteTableT);
    listLength = numStates;
    ancestors.add(ptr);
    
    Pointer aiScriptInfo = decoder.enqueueAsChild(ptr, ptrAIScript, SharedStructTypes.ScriptT);
    aiScriptInfo.setDescriptor("Init");
    ancestors.add(ptr);
    
    if (ptrStatusTable != 0)
    {
      Pointer statusTableInfo = decoder.enqueueAsChild(ptr, ptrStatusTable, BattleStructTypes.StatusTableT);
      ancestors.add(ptr);
    }
    
    fileBuffer.position(start + 40);
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    byte[] enemyBytes = new byte[40];
    fileBuffer.get(enemyBytes);
    ByteBuffer bb = ByteBuffer.wrap(enemyBytes);
    
    pw.println("% stats");
    IndexA.print(decoder, bb, pw);
    LevelA.print(decoder, bb, pw);
    MaxHPA.print(decoder, bb, pw);
    CoinsA.print(decoder, bb, pw);
    FlagsA.print(decoder, bb, pw);
    StatusTableA.print(decoder, bb, pw);
    pw.println("% ai");
    SpriteCountA.print(decoder, bb, pw);
    SpriteTableA.print(decoder, bb, pw);
    ScriptA.print(decoder, bb, pw);
    pw.println("% move effectiveness");
    EscapeA.print(decoder, bb, pw);
    ItemA.print(decoder, bb, pw);
    AirLiftA.print(decoder, bb, pw);
    HurricaneA.print(decoder, bb, pw);
    UpAndAwayA.print(decoder, bb, pw);
    PowerBounceA.print(decoder, bb, pw);
    SpinSmashA.print(decoder, bb, pw);
    pw.println("% ui positions");
    SizeA.print(decoder, bb, pw);
    HealthBarA.print(decoder, bb, pw);
    StatusTurnA.print(decoder, bb, pw);
    StatusIconA.print(decoder, bb, pw);
  }
  
  private static final HashMap<String, Attribute> attributes = new HashMap();
  
  private static void addAttribute(Attribute a)
  {
    attributes.put("[" + name + "]", a);
  }
  
  private static final Attribute FlagsA = new Attribute("Flags", 0, 4, Attribute.Style.Ints, false);
  private static final Attribute IndexA = new Attribute("Index", 5, 1, Attribute.Style.Bytes, false);
  private static final Attribute LevelA = new Attribute("Level", 6, 1, Attribute.Style.Bytes, true);
  private static final Attribute MaxHPA = new Attribute("MaxHP", 7, 1, Attribute.Style.Bytes, true);
  private static final Attribute SpriteCountA = new Attribute("SpriteCount", 8, 2, Attribute.Style.Shorts, true);
  private static final Attribute SpriteTableA = new Attribute("SpriteTable", 12, 4, Attribute.Style.Ints, false);
  private static final Attribute ScriptA = new Attribute("Script", 16, 4, Attribute.Style.Ints, false);
  private static final Attribute StatusTableA = new Attribute("StatusTable", 20, 4, Attribute.Style.Ints, false);
  private static final Attribute EscapeA = new Attribute("Escape", 24, 1, Attribute.Style.Bytes, true);
  private static final Attribute AirLiftA = new Attribute("AirLift", 25, 1, Attribute.Style.Bytes, true);
  private static final Attribute HurricaneA = new Attribute("Hurricane", 26, 1, Attribute.Style.Bytes, true, "Bow's \"Spook\" as well");
  private static final Attribute ItemA = new Attribute("Item", 27, 1, Attribute.Style.Bytes, true);
  private static final Attribute UpAndAwayA = new Attribute("UpAndAway", 28, 1, Attribute.Style.Bytes, true);
  private static final Attribute SpinSmashA = new Attribute("SpinSmash", 29, 1, Attribute.Style.Bytes, true, "weight (0-4)");
  private static final Attribute PowerBounceA = new Attribute("PowerBounce", 30, 1, Attribute.Style.Bytes, true);
  private static final Attribute CoinsA = new Attribute("Coins", 31, 1, Attribute.Style.Bytes, true);
  private static final Attribute SizeA = new Attribute("Size", 32, 2, Attribute.Style.Bytes, true, "width height");
  private static final Attribute HealthBarA = new Attribute("HealthBar", 34, 2, Attribute.Style.Bytes, true, "dx dy");
  private static final Attribute StatusTurnA = new Attribute("StatusTurn", 36, 2, Attribute.Style.Bytes, true, "dx dy");
  private static final Attribute StatusIconA = new Attribute("StatusIcon", 38, 2, Attribute.Style.Bytes, true, "dx dy");
  
  static
  {
    addAttribute(FlagsA);
    addAttribute(IndexA);
    addAttribute(LevelA);
    addAttribute(MaxHPA);
    addAttribute(SpriteCountA);
    addAttribute(SpriteTableA);
    addAttribute(ScriptA);
    addAttribute(StatusTableA);
    addAttribute(EscapeA);
    addAttribute(AirLiftA);
    addAttribute(HurricaneA);
    addAttribute(ItemA);
    addAttribute(UpAndAwayA);
    addAttribute(SpinSmashA);
    addAttribute(PowerBounceA);
    addAttribute(CoinsA);
    addAttribute(SizeA);
    addAttribute(HealthBarA);
    addAttribute(StatusTurnA);
    addAttribute(StatusIconA);
  }
  

  public Patch parseStructOffset(BattleEncoder encoder, String offsetName)
  {
    Attribute attr = (Attribute)attributes.get(offsetName);
    if (attr == null) {
      return null;
    }
    return new Patch(encoder.getSource(), offsetName, offset, length);
  }
  
  private Actor() {}
}

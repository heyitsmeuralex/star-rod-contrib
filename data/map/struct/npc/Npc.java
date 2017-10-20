package data.map.struct.npc;

import data.map.decoder.MapDecoder;
import data.map.encoder.MapEncoder;
import data.shared.Attribute;
import data.shared.Attribute.Style;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.DataUtils;
import data.shared.decoder.Pointer;
import data.shared.encoder.InvalidExpressionException;
import editor.map.marker.Marker;
import editor.map.marker.NpcMovementSettings.MoveType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class Npc extends data.shared.BaseStruct.MapStruct
{
  public static final Npc instance;
  public static PrintWriter npcPrinter;
  public static PrintWriter enemyPrinter;
  public static final int MOVE_NONE = 0;
  public static final int MOVE_WANDER = 1;
  public static final int MOVE_PATROL = 2;
  private static final HashMap<String, Attribute> attributes;
  private static final Attribute NpcIDA;
  private static final Attribute SpawnPosA;
  private static final Attribute FlagsA;
  private static final Attribute ItemDropsA;
  private static final Attribute HeartDropsA;
  private static final Attribute FlowerDropsA;
  private static final Attribute MoveDataA;
  private static final Attribute AnimationsA;
  
  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    throw new IllegalStateException("NPC structs should never be scanned individually! They exist only to track dependency.");
  }
  
  public static NpcMovementSettings.MoveType getMovementType(int[] moveData, int npcAddress, String mapName)
  {
    if ((moveData[5] == 32769) || ((npcAddress == -2145068120) && (mapName.equals("nok_01"))))
      return NpcMovementSettings.MoveType.Wander;
    if (moveData[31] == 32769) {
      return NpcMovementSettings.MoveType.Patrol;
    }
    return NpcMovementSettings.MoveType.Stationary;
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    assert (parents.size() == 1);
    printNpcData(decoder, fileBuffer, pw, true);
  }
  
  public static void printNpcData(MapDecoder decoder, ByteBuffer fileBuffer, PrintWriter pw, boolean exportMode)
  {
    int npcAddress = decoder.toAddress(fileBuffer.position());
    String markerName = String.format("NPC_%08X", new Object[] { Integer.valueOf(decoder.toAddress(fileBuffer.position())) });
    
    decoder.printHex(fileBuffer, pw, 8, 2);
    
    int x = (int)fileBuffer.getFloat();
    int y = (int)fileBuffer.getFloat();
    int z = (int)fileBuffer.getFloat();
    
    if (exportMode) {
      pw.println("00000000 00000000 00000000 ");
    } else {
      pw.printf("{Vec3f:%s} %% %d %d %d\r\n", new Object[] { markerName, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z) });
    }
    decoder.printHex(fileBuffer, pw, 8, 5);
    pw.println();
    
    byte dropFlag = fileBuffer.get();
    byte dropChance = fileBuffer.get();
    
    short[][] itemDrops = new short[8][3];
    for (int j = 0; j < 8; j++)
    {
      itemDrops[j][0] = fileBuffer.getShort();
      itemDrops[j][1] = fileBuffer.getShort();
      itemDrops[j][2] = fileBuffer.getShort();
    }
    
    short[][] heartDrops = new short[8][4];
    for (int j = 0; j < 8; j++)
    {
      heartDrops[j][0] = fileBuffer.getShort();
      heartDrops[j][1] = fileBuffer.getShort();
      heartDrops[j][2] = fileBuffer.getShort();
      heartDrops[j][3] = fileBuffer.getShort();
    }
    
    short[][] flowerDrops = new short[8][4];
    for (int j = 0; j < 8; j++)
    {
      flowerDrops[j][0] = fileBuffer.getShort();
      flowerDrops[j][1] = fileBuffer.getShort();
      flowerDrops[j][2] = fileBuffer.getShort();
      flowerDrops[j][3] = fileBuffer.getShort();
    }
    
    short coinsMin = fileBuffer.getShort();
    short coinsMax = fileBuffer.getShort();
    fileBuffer.getShort();
    
    if (dropFlag == 0)
    {
      pw.println("{NoDrops}");
    }
    else
    {
      boolean noItems = dropChance == 0;
      boolean noHP = heartDrops[0][0] == Short.MAX_VALUE;
      boolean noFP = flowerDrops[0][0] == Short.MAX_VALUE;
      boolean noCoins = (coinsMin == 0) && (coinsMax == 0);
      
      if ((noItems) && (noHP) && (noFP) && (noCoins)) {
        pw.println("{NoItems} {NoHP} {NoFP} {NoCoinBonus}");
      }
      else {
        if (noItems) {
          pw.println("{NoItems}");
        }
        else {
          pw.print("{Items:" + dropChance);
          
          boolean empty = false;
          for (int j = 0; j < 8; j++)
          {
            if (!empty)
            {
              if (itemDrops[j][0] == 0)
              {
                pw.println("}");
                empty = true;
              }
              else
              {
                String itemName = DataConstants.ItemType.getName(itemDrops[j][0]);
                pw.printf(":%s:%X", new Object[] { itemName, Short.valueOf(itemDrops[j][1]) });
              }
            }
          }
          if (!empty) {
            pw.println("}");
          }
        }
        if (noHP) {
          pw.println("{NoHP}");
        }
        else {
          for (int j = 0; j < 8; j++)
          {
            short s1 = heartDrops[j][0];
            short s2 = heartDrops[j][1];
            short s3 = heartDrops[j][2];
            short s4 = heartDrops[j][3];
            
            if ((s1 == 0) && (s2 == 0) && (s3 == 0) && (s4 == 0)) {
              pw.printf("{HP:None} ", new Object[0]);
            } else
              pw.printf("{HP:%s:%s:%s:%s} ", new Object[] { Integer.valueOf(Math.round(s1 / 327.67F)), Integer.valueOf(Math.round(s2 / 327.67F)), Short.valueOf(s3), Integer.valueOf(Math.round(s4 / 327.67F)) });
          }
          pw.println();
        }
        
        if (noFP) {
          pw.println("{NoFP}");
        }
        else {
          for (int j = 0; j < 8; j++)
          {
            short s1 = flowerDrops[j][0];
            short s2 = flowerDrops[j][1];
            short s3 = flowerDrops[j][2];
            short s4 = flowerDrops[j][3];
            
            if ((s1 == 0) && (s2 == 0) && (s3 == 0) && (s4 == 0)) {
              pw.printf("{FP:None} ", new Object[0]);
            } else
              pw.printf("{FP:%s:%s:%s:%s} ", new Object[] { Integer.valueOf(Math.round(s1 / 327.67F)), Integer.valueOf(Math.round(s2 / 327.67F)), Short.valueOf(s3), Integer.valueOf(Math.round(s4 / 327.67F)) });
          }
          pw.println();
        }
        
        if (noCoins) {
          pw.println("{NoCoinBonus}");
        } else {
          pw.printf("{CoinBonus:%X:%X}\r\n", new Object[] { Short.valueOf(coinsMin), Short.valueOf(coinsMax) });
        }
      }
    }
    int[] moveData = new int[48];
    for (int j = 0; j < 48; j++) {
      moveData[j] = fileBuffer.getInt();
    }
    NpcMovementSettings.MoveType movementType = getMovementType(moveData, npcAddress, decoder.getSourceName());
    




    if (exportMode)
    {
      switch (1.$SwitchMap$editor$map$marker$NpcMovementSettings$MoveType[movementType.ordinal()])
      {
      case 1: 
        moveData[0] -= x;
        moveData[1] -= y;
        moveData[2] -= z;
        
        moveData[7] -= x;
        moveData[8] -= y;
        moveData[9] -= z;
        
        decoder.printHex(moveData, pw, 8);
        break;
      
      case 2: 
        for (int i = 0; i < 3 * moveData[0]; i += 3)
        {
          moveData[(i + 1)] -= x;
          moveData[(i + 2)] -= y;
          moveData[(i + 3)] -= z;
        }
        
        moveData[32] -= x;
        moveData[33] -= y;
        moveData[34] -= z;
        
        decoder.printHex(moveData, pw, 8);
        break;
      
      case 3: 
        decoder.printHex(moveData, pw, 8);
      
      }
      
    } else {
      pw.println("{Movement:" + markerName + "}");
    }
    





































    decoder.printHex(fileBuffer, pw, 8, 16);
    
    decoder.printHex(fileBuffer, pw, 8, 3);
    int tattleID = fileBuffer.getInt();
    if (tattleID != 0) {
      pw.printf("%08X %s", new Object[] { Integer.valueOf(tattleID), decoder.getStringComment(tattleID) });
    } else
      pw.printf("%08X %% no tattle string", new Object[] { Integer.valueOf(tattleID) });
    pw.println();
  }
  
  public void replaceExpression(MapEncoder encoder, String[] tokens, List<String> newTokens) throws InvalidExpressionException { int attempts;
    int attemptChance;
    int max;
    switch (tokens[0])
    {


    case "NoDrops": 
      for (int i = 0; i < 184; i += 4) {
        newTokens.add("00000000");
      }
      break;
    


    case "NoItems": 
      for (int i = 0; i < 48; i += 4)
        newTokens.add("00000000");
      newTokens.add("0000s");
      
      break;
    

    case "Items": 
      int totalChance = DataUtils.parseIntString(tokens[1]);
      
      if ((tokens.length < 2) || (tokens.length % 2 != 0) || (totalChance > 100) || (totalChance < 0)) {
        throw new InvalidExpressionException("Invalid item drop expression");
      }
      newTokens.add(String.format("80%02Xs", new Object[] { Integer.valueOf(totalChance) }));
      
      int numItems = tokens.length / 2 - 1;
      for (int i = 0; i < 8; i++)
      {
        if (i < numItems)
        {
          String itemID = DataConstants.ItemType.getID(tokens[(2 * i + 2)]);
          if (itemID == null) {
            itemID = tokens[(2 * i + 2)];
          }
          int weight = DataUtils.parseIntString(tokens[1]);
          
          newTokens.add(itemID + "s");
          newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(weight) }));
          newTokens.add("0000s");
        }
        else
        {
          newTokens.add("0000s");
          newTokens.add("0000s");
          newTokens.add("0000s");
        }
      }
      
      break;
    


    case "NoHP": 
    case "NoFP": 
      for (int i = 0; i < 64; i += 4) {
        newTokens.add("00000000");
      }
      break;
    

    case "HP": 
    case "FP": 
      if (tokens.length == 2)
      {
        if (!tokens[1].equals("None")) {
          throw new InvalidExpressionException("Invalid " + tokens[0] + " drop expression");
        }
        
        newTokens.add("00000000");
        newTokens.add("00000000");
      }
      else
      {
        if (tokens.length != 5) {
          throw new InvalidExpressionException("Invalid " + tokens[0] + " drop expression");
        }
        int cutoff = Integer.parseInt(tokens[1]);
        int baseChance = Integer.parseInt(tokens[2]);
        attempts = Integer.parseInt(tokens[3]);
        attemptChance = Integer.parseInt(tokens[4]);
        
        newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(Math.round(cutoff * 327.67F)) }));
        newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(Math.round(baseChance * 327.67F)) }));
        newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(attempts) }));
        newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(Math.round(attemptChance * 327.67F)) }));
      }
      
      break;
    


    case "NoCoinBonus": 
      newTokens.add("0000s");
      newTokens.add("0000s");
      newTokens.add("0000s");
      
      break;
    

    case "CoinBonus": 
      if (tokens.length != 3) {
        throw new InvalidExpressionException("Invalid coin bonus expression");
      }
      int min = DataUtils.parseIntString(tokens[1]);
      max = DataUtils.parseIntString(tokens[2]);
      
      newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(min) }));
      newTokens.add(String.format("%04Xs", new Object[] { Integer.valueOf(max) }));
      newTokens.add("0000s");
      
      break;
    

    case "Movement": 
      if (tokens.length != 2) {
        throw new InvalidExpressionException("Invalid movement expression");
      }
      Marker m = encoder.getMarker(tokens[1]);
      
      if (m == null) {
        throw new InvalidExpressionException("Cannot find marker");
      }
      if (m.getType() != editor.map.marker.Marker.MarkerType.NPC) {
        throw new InvalidExpressionException(tokens[1] + " is not an NPC marker");
      }
      if (moveSettings == null) {
        throw new InvalidExpressionException(tokens[1] + " does not have any movement data");
      }
      for (int v : moveSettings.getMovementData()) {
        newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(v) }));
      }
      break;
    

    case "NoMovement": 
      for (int i = 0; i < 48; i++) {
        newTokens.add("00000000");
      }
    }
    
  }
  


  private static void addAttribute(Attribute a)
  {
    attributes.put("[" + name + "]", a);
  }
  
  static
  {
    instance = new Npc();
    
    npcPrinter = null;
    enemyPrinter = null;
    































































































































































































































































































































































































































    attributes = new HashMap();
    





    NpcIDA = new Attribute("ID", 0, 4, Attribute.Style.Ints, false);
    SpawnPosA = new Attribute("SpawnPos", 8, 12, Attribute.Style.Shorts, false);
    FlagsA = new Attribute("Flags", 20, 4, Attribute.Style.Ints, false);
    ItemDropsA = new Attribute("ItemDrops", 40, 50, Attribute.Style.Shorts, false);
    HeartDropsA = new Attribute("HeartDrops", 90, 64, Attribute.Style.Shorts, false);
    FlowerDropsA = new Attribute("FlowerDrops", 154, 64, Attribute.Style.Shorts, false);
    MoveDataA = new Attribute("MoveData", 224, 192, Attribute.Style.Ints, false);
    AnimationsA = new Attribute("Animations", 416, 64, Attribute.Style.Ints, false);
    


    addAttribute(NpcIDA);
    addAttribute(FlagsA);
    addAttribute(SpawnPosA);
    addAttribute(ItemDropsA);
    addAttribute(HeartDropsA);
    addAttribute(FlowerDropsA);
    addAttribute(MoveDataA);
    addAttribute(AnimationsA);
  }
  

  public data.shared.encoder.Patch parseStructOffset(MapEncoder encoder, String offsetName)
  {
    Attribute attr = (Attribute)attributes.get(offsetName);
    if (attr == null) {
      return null;
    }
    return new data.shared.encoder.Patch(encoder.getSource(), offsetName, offset, length);
  }
  
  private Npc() {}
}

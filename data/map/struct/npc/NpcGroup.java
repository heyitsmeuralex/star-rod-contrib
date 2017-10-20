package data.map.struct.npc;

import data.map.MapStructTypes;
import data.map.decoder.MapDecoder;
import data.map.encoder.MapEncoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import data.shared.encoder.InvalidExpressionException;
import data.shared.encoder.Patch;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import editor.map.marker.NpcMovementSettings;
import editor.map.marker.NpcMovementSettings.MoveType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;




public class NpcGroup
  extends BaseStruct.MapStruct
{
  public static final NpcGroup instance = new NpcGroup();
  

  private NpcGroup() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    for (int i = 0; i < listLength; i++)
    {
      int pos = fileBuffer.position();
      int npcAddress = decoder.toAddress(fileBuffer.position());
      Pointer npc = new Pointer(npcAddress);
      type = MapStructTypes.NpcT;
      listIndex = i;
      origin = origin;
      ptr.addUniqueChild(npc);
      
      decoder.addNpc(npc);
      
      fileBuffer.getInt();
      decoder.tryEnqueueAsChild(npc, fileBuffer.getInt(), MapStructTypes.NpcSettingsT);
      float x = fileBuffer.getFloat();
      float y = fileBuffer.getFloat();
      float z = fileBuffer.getFloat();
      fileBuffer.getInt();
      int initScript = fileBuffer.getInt();
      Pointer initPtr = decoder.tryEnqueueAsChild(npc, initScript, SharedStructTypes.ScriptT);
      if (initPtr != null) {
        initPtr.setDescriptor("Init");
      }
      int w1 = fileBuffer.getInt();
      int w2 = fileBuffer.getInt();
      int angle = fileBuffer.getInt();
      
      assert ((w1 == 0) || (w1 == 1));
      assert ((angle >= 0) && (angle < 360));
      
      String markerName = String.format("NPC_%08X", new Object[] { Integer.valueOf(npcAddress) });
      Marker npcMarker = new Marker(markerName, Marker.MarkerType.NPC, x, y, z, angle);
      decoder.addMarker(npcMarker);
      
      byte dropFlag = fileBuffer.get();
      byte dropChance = fileBuffer.get();
      
      assert ((dropFlag == 0) || (dropFlag == Byte.MIN_VALUE));
      
      if (dropFlag == 0)
      {
        assert (dropChance == 0);
        for (int s = 0; s < 91; s++) {
          assert (fileBuffer.getShort() == 0);
        }
      }
      else {
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
        short coins3 = fileBuffer.getShort();
        
        checkItemDrops(ptr, dropChance, itemDrops);
        checkHeartDrops(heartDrops);
        checkFlowerDrops(flowerDrops);
        
        assert (coinsMin <= coinsMax);
        assert (coins3 == 0);
      }
      

      int[] moveData = new int[48];
      for (int j = 0; j < 48; j++) {
        moveData[j] = fileBuffer.getInt();
      }
      NpcMovementSettings.MoveType movementType = Npc.getMovementType(moveData, npcAddress, decoder.getSourceName());
      moveSettings = new NpcMovementSettings(npcMarker, moveData, movementType);
      
      checkMoveData(moveData, movementType);
      










      int[] animations = new int[16];
      for (int j = 0; j < 16; j++) {
        animations[j] = fileBuffer.getInt();
      }
      w1 = fileBuffer.getInt();
      w2 = fileBuffer.getInt();
      
      decoder.tryEnqueueAsChild(npc, fileBuffer.getInt(), MapStructTypes.ExtraAnimationListT);
      fileBuffer.getInt();
    }
  }
  





























  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    for (int i = 0; i < listLength; i++)
    {
      if (i > 0)
      {
        pw.println("% ");
        pw.printf("%% %s[%X]\r\n", new Object[] { ptr.getPointerName(), Integer.valueOf(496 * i) });
      }
      
      Npc.printNpcData(decoder, fileBuffer, pw, false);
    }
  }
  
  public void replaceExpression(MapEncoder encoder, String[] tokens, List<String> newTokens)
    throws InvalidExpressionException
  {
    Npc.instance.replaceExpression(encoder, tokens, newTokens);
  }
  

  public Patch parseStructOffset(MapEncoder encoder, String offsetName)
  {
    return Npc.instance.parseStructOffset(encoder, offsetName);
  }
  
  private static void checkItemDrops(Pointer ptr, int dropChance, short[][] itemDrops)
  {
    boolean empty = dropChance == 0;
    for (int j = 0; j < 8; j++)
    {
      if ((empty) && 
        (!$assertionsDisabled) && (itemDrops[j][0] != 0)) { throw new AssertionError();
      }
      if ((itemDrops[j][0] == 0) || ((j == 0) && (itemDrops[j][0] == 100)))
      {
        assert (itemDrops[j][1] == 0);
        empty = true;
      }
      assert (itemDrops[j][2] == 0) : (ptr.getPointerName() + " item drop " + j + " has unknown short = " + itemDrops[j][2]);
    }
  }
  
  private static void checkHeartDrops(short[][] heartDrops)
  {
    boolean empty = false;
    for (int j = 0; j < 8; j++)
    {
      if ((empty) || (heartDrops[j][0] == 0))
      {
        assert (heartDrops[j][1] == 0);
        assert (heartDrops[j][2] == 0);
        assert (heartDrops[j][3] == 0);
        empty = true;
      }
      
      if ((j == 0) && (heartDrops[j][0] == Short.MAX_VALUE))
      {
        assert (heartDrops[j][1] == 0);
        assert (heartDrops[j][2] == 0);
        assert (heartDrops[j][3] == 0);
        empty = true;
      }
      
      assert (onlyNeedsOneDecimal(heartDrops[j][0]));
      assert (onlyNeedsOneDecimal(heartDrops[j][1]));
      assert (onlyNeedsOneDecimal(heartDrops[j][3]));
    }
  }
  
  private static void checkFlowerDrops(short[][] flowerDrops)
  {
    boolean empty = false;
    for (int j = 0; j < 8; j++)
    {
      if ((empty) || (flowerDrops[j][0] == 0))
      {
        assert (flowerDrops[j][1] == 0);
        assert (flowerDrops[j][2] == 0);
        assert (flowerDrops[j][3] == 0);
        empty = true;
      }
      
      if ((j == 0) && (flowerDrops[j][0] == Short.MAX_VALUE))
      {
        assert (flowerDrops[j][1] == 0);
        assert (flowerDrops[j][2] == 0);
        assert (flowerDrops[j][3] == 0);
        empty = true;
      }
      
      assert (onlyNeedsOneDecimal(flowerDrops[j][0]));
      assert (onlyNeedsOneDecimal(flowerDrops[j][1]));
      assert (onlyNeedsOneDecimal(flowerDrops[j][3]));
    }
  }
  
  private static boolean onlyNeedsOneDecimal(short s)
  {
    float a = s / 32767.0F;
    float trunc = Math.round(a * 10.0F) / 10.0F;
    return Math.abs(a - trunc) < 1.0E-4D;
  }
  
  private static void checkMoveData(int[] moveData, NpcMovementSettings.MoveType type)
  {
    switch (1.$SwitchMap$editor$map$marker$NpcMovementSettings$MoveType[type.ordinal()])
    {
    case 1: 
      assert ((moveData[6] == 0) || (moveData[6] == 1));
      assert ((moveData[12] == 0) || (moveData[12] == 1));
      assert ((moveData[13] == 0) || (moveData[13] == 1));
      
      for (int k = 14; k < 48; k++)
        assert (moveData[k] == 0);
      break;
    
    case 2: 
      int num = moveData[0];
      for (int k = 3 * num + 1; k < 31; k++) {
        assert (moveData[k] == 0);
      }
      for (int k = 39; k < 48; k++)
        assert (moveData[k] == 0);
      break;
    
    case 3: 
      for (int k = 0; k < 48; k++) {
        assert (moveData[k] == 0);
      }
    }
  }
}

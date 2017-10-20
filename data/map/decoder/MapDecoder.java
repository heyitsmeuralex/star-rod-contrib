package data.map.decoder;

import data.map.MapBlueprint;
import data.map.MapStructTypes;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.SharedStructTypes;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import data.shared.lib.ArgumentList;
import data.shared.lib.FunctionArgument;
import data.shared.lib.FunctionArgument.ArgumentType;
import data.shared.lib.FunctionLibrary;
import data.shared.lib.NamedFunction;
import data.shared.struct.StructType;
import data.shared.struct.script.Script;
import data.shared.struct.script.Script.Command;
import data.shared.struct.script.ScriptFindings;
import data.shared.struct.script.ScriptTemplate;
import editor.map.Map;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import main.Directories;
import reports.EffectTypeTracker;
import reports.FunctionCallTracker;
import util.DualHashMap;
import util.IOUtils;
import util.IdentityHashSet;
import util.Logger;

















public class MapDecoder
  extends DataDecoder
{
  private final Map map;
  private List<Pointer> npcList;
  private List<Marker> markerList;
  
  public String getSourceName()
  {
    return map.name;
  }
  
  public void addNpc(Pointer ptr)
  {
    npcList.add(ptr);
  }
  
  public void addMarker(Marker m)
  {
    markerList.add(m);
  }
  
  public MapDecoder(Map map, MapBlueprint bp, File source) throws IOException
  {
    super(MapStructTypes.mapTypes, MapStructTypes.NpcT, DataConstants.mapFunctionLib, DataConstants.mapScriptLib);
    
    npcList = new LinkedList();
    markerList = new ArrayList();
    
    ByteBuffer fileBuffer = IOUtils.getDirectBuffer(source);
    this.map = map;
    
    initialize(bp, fileBuffer);
    decode(fileBuffer);
    
    File scriptFile = new File(Directories.DUMP_MAP_SRC + name + ".mscr");
    File indexFile = new File(Directories.DUMP_MAP_SRC + name + ".midx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    printNPCFiles(fileBuffer);
    
    MapObjectNode<Marker> rootNode = markerTree.getRoot();
    for (Marker m : markerList)
    {
      getNodeparentNode = rootNode;
      getNodechildIndex = rootNode.getChildCount();
      rootNode.add(m.getNode());
    }
    
    unknownPointers = unknownPointers;
    missingSections = missingSections;
  }
  
  private void initialize(MapBlueprint bp, ByteBuffer fileBuffer)
  {
    int startAddress = -2145124352;
    int endAddress = -2145124352 + fileBuffer.limit();
    setAddressRange(startAddress, endAddress, -2144960512);
    setOffsetRange(0, fileBuffer.limit());
    

    fileBuffer.position(startOffset);
    while (fileBuffer.position() < endOffset)
    {
      int v = fileBuffer.getInt();
      if (isLocalAddress(v)) {
        addPointer(v);
      }
    }
    enqueueAsRoot(headerAddr, MapStructTypes.HeaderT, Pointer.Origin.DECODED);
    if (initAddr != 0) {
      enqueueAsRoot(initAddr, MapStructTypes.InitFunctionT, Pointer.Origin.DECODED);
    }
  }
  
  public void scanScript(Pointer ptr, ByteBuffer fileBuffer) {
    ScriptFindings results = Script.scan(this, fileBuffer);
    int endPosition = fileBuffer.position();
    
    scanFunctionCalls(ptr, fileBuffer, results);
    
    for (Iterator localIterator = intBuffers.iterator(); localIterator.hasNext();) { int addr = ((Integer)localIterator.next()).intValue();
      
      addPointer(addr);
      enqueueAsChild(ptr, addr, SharedStructTypes.IntTableT);
    }
    
    for (localIterator = floatBuffers.iterator(); localIterator.hasNext();) { int addr = ((Integer)localIterator.next()).intValue();
      
      addPointer(addr);
      enqueueAsChild(ptr, addr, SharedStructTypes.FloatTableT);
    }
    
    for (localIterator = scriptExecs.iterator(); localIterator.hasNext();) { int lineNum = ((Integer)localIterator.next()).intValue();
      
      int offset = ((Integer)lineOffsets.get(lineNum)).intValue();
      int[] line = Script.getLine(fileBuffer, offset);
      int scriptAddress = line[2];
      
      if (isLocalAddress(scriptAddress))
      {
        enqueueAsChild(ptr, scriptAddress, SharedStructTypes.ScriptT);
      }
      else
      {
        switch (scriptAddress)
        {
        case -2144839328: 
          assert (lineNum > 0);
          int prevOffset = ((Integer)lineOffsets.get(lineNum - 1)).intValue();
          int[] prevLine = Script.getLine(fileBuffer, prevOffset);
          assert (prevLine[0] == 36);
          enqueueAsChild(ptr, prevLine[3], SharedStructTypes.ScriptT);
        }
        
      }
    }
    


    for (localIterator = triggers.iterator(); localIterator.hasNext();) { int lineNum = ((Integer)localIterator.next()).intValue();
      
      int offset = ((Integer)lineOffsets.get(lineNum)).intValue();
      int[] line = Script.getLine(fileBuffer, offset);
      int ptrScript = line[2];
      
      Pointer scriptPtr = tryEnqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
      
      if (line[3] == 1048576) {
        tryEnqueueAsChild(ptr, line[4], MapStructTypes.TriggerCoordT);
      }
      if (line.length == 8) {
        tryEnqueueAsChild(ptr, line[5], MapStructTypes.ItemListT);
      }
      if (ScriptTemplate.SHAKE_TREE.matches(fileBuffer, toOffset(ptrScript)))
      {
        scriptPtr.setDescriptor(ScriptTemplate.SHAKE_TREE.getName());
        
        assert (lineNum > 0);
        int prevOffset = ((Integer)lineOffsets.get(lineNum - 1)).intValue();
        int[] prevLine = Script.getLine(fileBuffer, prevOffset);
        
        if (prevLine[0] == SET_INTopcode)
        {
          assert ((line[3] == 4096) || (line[3] == 1048576));
          tryEnqueueAsChild(ptr, prevLine[3], MapStructTypes.ShakeTreeEventT);
        }
      }
      else if (ScriptTemplate.SEARCH_BUSH.matches(fileBuffer, toOffset(ptrScript)))
      {
        scriptPtr.setDescriptor(ScriptTemplate.SEARCH_BUSH.getName());
        
        assert (lineNum > 0);
        int prevOffset = ((Integer)lineOffsets.get(lineNum - 1)).intValue();
        int[] prevLine = Script.getLine(fileBuffer, prevOffset);
        
        if (prevLine[0] == SET_INTopcode)
        {
          assert (line[3] == 256);
          tryEnqueueAsChild(ptr, prevLine[3], MapStructTypes.SearchBushEventT);
        }
      }
    }
    
    for (localIterator = unknownChildPointers.iterator(); localIterator.hasNext();) { int addr = ((Integer)localIterator.next()).intValue();
      
      if (isLocalAddress(addr))
      {
        Pointer childPtr = getPointer(addr);
        ptr.addUniqueChild(childPtr);
      }
    }
    
    fileBuffer.position(endPosition);
  }
  
  private void scanFunctionCalls(Pointer scriptPtr, ByteBuffer fileBuffer, ScriptFindings results)
  {
    for (Iterator localIterator = functionCalls.iterator(); localIterator.hasNext();) { int lineNum = ((Integer)localIterator.next()).intValue();
      
      int offset = ((Integer)lineOffsets.get(lineNum)).intValue();
      int[] line = Script.getLine(fileBuffer, offset);
      int funcAddress = line[2];
      int nargs = line.length - 3;
      

      if (isLocalAddress(funcAddress))
      {
        enqueueAsChild(scriptPtr, funcAddress, SharedStructTypes.FunctionT);
        

        if ((scriptPtr.getDescriptor().equals("NpcAI")) && (nargs > 0))
        {
          if ((isLocalAddress(line[3])) && (getPointer3type == SharedStructTypes.UnknownT)) {
            enqueueAsChild(scriptPtr, line[3], MapStructTypes.AISettingsT);
          }
        }
        scanUnknownArguments(scriptPtr, line);

      }
      else
      {
        FunctionCallTracker.addCall(funcAddress);
        
        NamedFunction func = functionLibrary.get(funcAddress);
        

        switch (funcAddress)
        {
        case -2146362056: 
          scanEntityArgs(scriptPtr, line);
          break;
        
        case -2144562016: 
          tryEnqueueAsChild(scriptPtr, line[4], SharedStructTypes.FunctionT);
          tryEnqueueAsChild(scriptPtr, line[5], SharedStructTypes.FunctionT);
          if ((!$assertionsDisabled) && (func != null)) { throw new AssertionError(String.format("%08X has been identified as %s, remove it from switch!", new Object[] { Integer.valueOf(funcAddress), func.getName() }));
          }
        
        case -2144562136: 
          enqueueAsChild(scriptPtr, line[4], SharedStructTypes.DisplayListT);
          if ((!$assertionsDisabled) && (func != null)) { throw new AssertionError(String.format("%08X has been identified as %s, remove it from switch!", new Object[] { Integer.valueOf(funcAddress), func.getName() }));
          }
        

        default: 
          if (func == null)
          {
            scanUnknownArguments(scriptPtr, line);
          }
          else
          {
            if (functionLibrary.conflicts(funcAddress, nargs))
            {
              throw new RuntimeException(String.format("Call to %08X in %s does not match library signature: argc = %d", new Object[] {
              
                Integer.valueOf(funcAddress), getSourceName(), Integer.valueOf(nargs) }));
            }
            
            ArgumentList argList = func.getArguments(nargs);
            

            if (argList == null)
            {
              scanUnknownArguments(scriptPtr, line);



            }
            else if (!argList.hasPointerArg())
            {
              scanUnknownArguments(scriptPtr, line);

            }
            else
            {
              for (int i = 0; i < nargs; i++)
              {
                FunctionArgument arg = (FunctionArgument)argList.get(i);
                int v = line[(3 + i)];
                
                if (type == FunctionArgument.ArgumentType.Pointer)
                {
                  Pointer ptr = tryEnqueueAsChild(scriptPtr, v, arg.getPointerType());
                  
                  if (ptr != null)
                  {

                    String ptrDesc = arg.getPointerDesc();
                    if (ptrDesc != null) {
                      ptr.setDescriptor(ptrDesc);
                    }
                    int ptrListLen = arg.getPointerListLength();
                    if (ptrListLen > 0)
                    {
                      if (arg.listLengthAsIndex()) {
                        listLength = line[(ptrListLen + 3)];
                      } else {
                        listLength = ptrListLen;
                      }
                      if ((listLength > 128) || (listLength < 1))
                        throw new RuntimeException(String.format("Invalid list length for function: %s (%d)", new Object[] {func
                          .getName(), Integer.valueOf(listLength) }));
                    }
                  }
                } else if (isLocalAddress(line[(3 + i)]))
                {
                  Pointer childPtr = getPointer(v);
                  if (type == SharedStructTypes.UnknownT)
                    enqueueAsChild(scriptPtr, v, SharedStructTypes.UnknownT);
                }
              } }
          }
          break; }
      }
    }
  }
  
  private void scanUnknownArguments(Pointer scriptPtr, int[] line) {
    for (int i = 0; i < line.length - 3; i++)
    {
      int v = line[(3 + i)];
      if (isLocalAddress(v))
      {
        Pointer childPtr = getPointer(v);
        if (type == SharedStructTypes.UnknownT) {
          enqueueAsChild(scriptPtr, v, SharedStructTypes.UnknownT);
        }
      }
    }
  }
  
  private void scanEntityArgs(Pointer ptr, int[] line) {
    switch (line[3]) {
    case -2144614520: 
      break;
    case -2144614484: 
      break;
    case -2144614412: 
      break; case -2144613772:  enqueueAsChild(ptr, line[8], MapStructTypes.TweesterPathListT);
      break;
    case -2144613564: 
      break;
    case -2144613416: 
      break;
    case -2144613380: 
      break;
    case -2144613344: 
      break;
    case -2144613016: 
      break;
    case -2144612964: 
      break;
    case -2144612732: 
      break;
    case -2144612608: 
      break;
    case -2144612572: 
      break;
    case -2144429544: 
      break;
    case -2144429136: 
      break;
    case -2144429100: 
      break;
    case -2144429064: 
      break;
    case -2144429028: 
      break;
    case -2144427908: 
      break;
    case -2144427836: 
      break;
    case -2144427800: 
      break;
    case -2144427764: 
      break;
    case -2144427728: 
      break;
    case -2144427692: 
      break;
    case -2144427656: 
      break;
    case -2144427620:  break; case -2144427584:  break; case -2144427548:  break; case -2144427512:  break; case -2144427476:  break; case -2144427404:  break; case -2144427368:  break; case -2144427296:  break; case -2144426652:  break; case -2144426616:  break; case -2144426580:  break; case -2144426544:  break; case -2144426508:  break; case -2144426016:  break; case -2144425712:  break; case -2144425424:  break; case -2144425388:  break; case -2144425212:  break; case -2144424436:  break; case -2144424400:  break; case -2144424064:  enqueueAsChild(ptr, line[9], SharedStructTypes.ScriptT);
      break;
    case -2144424236: 
      break;
    case -2144423972: 
      break; default:  throw new RuntimeException(String.format("Encountered unknown entity type %08X", new Object[] { Integer.valueOf(line[3]) }));
    }
  }
  
  private void printNPCFiles(ByteBuffer fileBuffer) throws IOException
  {
    ArrayList<Integer> pointerList = getSortedLocalPointerList();
    
    String suffix = map.name;
    for (Iterator localIterator1 = pointerList.iterator(); localIterator1.hasNext();) { int i = ((Integer)localIterator1.next()).intValue();
      
      Pointer ptr = (Pointer)localPointerMap.get(Integer.valueOf(i));
      ptr.setImportAffix(suffix);
    }
    
    for (Pointer npc : npcList)
    {
      String npcGroupName = npc.getImportName();
      File f = new File(Directories.DUMP_MAP_NPC + npcGroupName + ".mpat");
      
      PrintWriter pw = IOUtils.getBufferedPrintWriter(f);
      
      pw.println("% automatically dumped from map " + map.name);
      pw.println();
      
      pw.println("#new:" + MapStructTypes.NpcT.toString() + " $" + npcGroupName);
      printPointer(npc, fileBuffer, address, pw);
      pw.println();
      
      for (Iterator localIterator2 = pointerList.iterator(); localIterator2.hasNext();) { int address = ((Integer)localIterator2.next()).intValue();
        
        Pointer pointer = (Pointer)localPointerMap.get(Integer.valueOf(address));
        if (ancestors.contains(npc))
        {
          pw.println("#new:" + type.toString() + " " + pointer.getPointerName());
          printPointer(pointer, fileBuffer, address, pw);
          pw.println();
        }
      }
      pw.close();
    }
  }
  


  public String printFunctionArgs(PrintWriter pw, int[] args, int lineAddress)
  {
    switch (args[0])
    {
    case -2146362056: 
      printEntityArgs(pw, args, lineAddress);
      return "";
    
    case -2144506688: 
      printItemArgs(pw, args, lineAddress);
      return "";
    
    case -2144501092: 
      int effectTypeA = args[1] << 16 | args[2] & 0xFFFF;
      int effectTypeB = args[1] << 16 | 0xFFFF;
      EffectTypeTracker.addEffect(effectTypeA, getSourceName());
      if (DataConstants.EffectType.contains(Integer.valueOf(effectTypeA)))
      {
        String effectName = (String)DataConstants.EffectType.get(Integer.valueOf(effectTypeA));
        pw.print("{FX:" + effectName + "} ");
        for (int i = 3; i < args.length; i++) {
          printScriptWord(pw, args[i]);
        }
      } else if (DataConstants.EffectType.contains(Integer.valueOf(effectTypeB)))
      {
        String effectName = (String)DataConstants.EffectType.get(Integer.valueOf(effectTypeB));
        pw.print("{FX:" + effectName + "} ");
        for (int i = 2; i < args.length; i++) {
          printScriptWord(pw, args[i]);
        }
      }
      else {
        for (int i = 1; i < args.length; i++)
          printScriptWord(pw, args[i]);
      }
      return "";
    
    case -2144849184: 
      int x = args[4];
      int y = args[5];
      int z = args[6];
      
      String markerName = String.format("Grid_%X", new Object[] { Integer.valueOf(lineAddress) });
      Marker m = new Marker(markerName, Marker.MarkerType.Grid, x, y, z, 0.0D);
      gridX = args[2];
      gridZ = args[3];
      gridSize = 25;
      markerList.add(m);
      
      printScriptWord(pw, args[1]);
      pw.print("{PushGrid:" + markerName + "} ");
      printScriptWord(pw, args[7]);
      return "";
    }
    
    NamedFunction func = functionLibrary.get(args[0]);
    if (func == null)
    {
      printArgs(pw, args);
      return "";
    }
    
    int nargs = args.length - 1;
    ArgumentList argList = func.getArguments(nargs);
    

    if (argList == null)
    {
      printArgs(pw, args);
      return "";
    }
    
    String comment = "";
    
    for (int i = 1; i <= nargs; i++)
    {
      FunctionArgument arg = (FunctionArgument)argList.get(i - 1);
      switch (1.$SwitchMap$data$shared$lib$FunctionArgument$ArgumentType[type.ordinal()]) {
      case 1: 
        printModelID(pw, args[i]); break;
      case 2:  printColliderID(pw, args[i]); break;
      case 3:  printZoneID(pw, args[i]); break;
      
      case 4: 
        printScriptWord(pw, args[i]);
        comment = getStringComment(args[i]);
        break;
      
      case 5: 
        printConstant(pw, constType, args[i]);
        break;
      
      case 6: 
        printCamera(pw, args[i]);
        break;
      
      case 7: 
        printBoolean(pw, args[i]);
        break;
      
      case 8: 
      case 9: 
      default: 
        printScriptWord(pw, args[i]);
      }
      
    }
    
    return comment;
  }
  
  private void printArgs(PrintWriter pw, int[] arg)
  {
    for (int i = 1; i < arg.length; i++) {
      printScriptWord(pw, arg[i]);
    }
  }
  
  private void printEntityArgs(PrintWriter pw, int[] arg, int lineAddress) {
    String entityName = DataConstants.EntityType.getConstantString(arg[1]);
    String markerName = String.format("Entity%X", new Object[] { Integer.valueOf(lineAddress) });
    pw.print(entityName + " ");
    
    int x = arg[2];
    int y = arg[3];
    int z = arg[4];
    int a = arg[5];
    Marker m = new Marker(markerName, Marker.MarkerType.Position, x, y, z, a);
    m.setDescription(entityName.substring(1));
    markerList.add(m);
    
    pw.print("{Vec4d:" + markerName + "} ");
    
    switch (arg[1])
    {
    case -2144426652: 
    case -2144426616: 
    case -2144426580: 
    case -2144426544: 
    case -2144424236: 
      printItemByID(pw, arg[6]);
      for (int i = 7; i < arg.length; i++)
        printScriptWord(pw, arg[i]);
      break;
    case -2144425212: 
      printModelID(pw, arg[6]);
      for (int i = 7; i < arg.length; i++)
        printScriptWord(pw, arg[i]);
      break;
    













































    default: 
      for (int i = 6; i < arg.length; i++) {
        printScriptWord(pw, arg[i]);
      }
    }
  }
  
  private void printItemArgs(PrintWriter pw, int[] arg, int lineAddress) {
    String itemName = null;
    
    if (DataConstants.ItemType.has(arg[1]))
    {
      pw.print(DataConstants.ItemType.getConstantString(arg[1]) + " ");
      itemName = DataConstants.ItemType.getName(arg[1]);
    }
    else
    {
      printScriptWord(pw, arg[1]);
      if ((arg[1] & 0xFFFFFFF0) == -30000000) {
        itemName = "VariableItem";
      } else {
        itemName = "UnknownItem";
      }
    }
    int x = arg[2];
    int y = arg[3];
    int z = arg[4];
    
    boolean varX = (x & 0xFFFFFFF0) == -30000000;
    boolean varY = (y & 0xFFFFFFF0) == -30000000;
    boolean varZ = (z & 0xFFFFFFF0) == -30000000;
    
    if ((varX) || (varY) || (varZ))
    {
      Logger.log(map.name + " has item with variable position! Cannot create marker.");
      for (int i = 2; i < arg.length; i++)
        printScriptWord(pw, arg[i]);
    } else {
      String markerName = String.format("Item%X", new Object[] { Integer.valueOf(lineAddress) });
      Marker m = new Marker(markerName, Marker.MarkerType.Position, x, y, z, 0.0D);
      
      m.setDescription(itemName);
      markerList.add(m);
      
      pw.print("{Vec3d:" + markerName + "} ");
      for (int i = 5; i < arg.length; i++) {
        printScriptWord(pw, arg[i]);
      }
    }
  }
  
  private void printAddItemArgs(PrintWriter pw, int[] arg) {
    printItemByID(pw, arg[1]);
    for (int i = 2; i < arg.length; i++) {
      printScriptWord(pw, arg[i]);
    }
  }
  
  public void printModelID(PrintWriter pw, int id)
  {
    String name = map.getModelName(id);
    if (name != null) {
      pw.printf("{Model:%s} ", new Object[] { name });
    } else {
      printScriptWord(pw, id);
    }
  }
  
  public String getModelName(int id) {
    return map.getModelName(id);
  }
  

  public void printColliderID(PrintWriter pw, int id)
  {
    String name = map.getColliderName(id);
    if (name != null) {
      pw.printf("{Collider:%s} ", new Object[] { name });
    } else {
      printScriptWord(pw, id);
    }
  }
  
  public String getColliderName(int id) {
    return map.getColliderName(id);
  }
  

  public void printZoneID(PrintWriter pw, int id)
  {
    String name = map.getZoneName(id);
    if (name != null) {
      pw.printf("{Zone:%s} ", new Object[] { name });
    } else {
      printScriptWord(pw, id);
    }
  }
  
  public String getZoneName(int id) {
    return map.getZoneName(id);
  }
}

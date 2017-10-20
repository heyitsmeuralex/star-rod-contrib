package data.battle.decoder;

import data.battle.BattleStructTypes;
import data.shared.DataConstants;
import data.shared.SharedStructTypes;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.PointerHeuristic;
import data.shared.lib.ArgumentList;
import data.shared.lib.FunctionArgument;
import data.shared.lib.FunctionArgument.ArgumentType;
import data.shared.lib.FunctionLibrary;
import data.shared.lib.NamedFunction;
import data.shared.struct.script.Script;
import data.shared.struct.script.ScriptFindings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import reports.EffectTypeTracker;
import reports.FunctionCallTracker;
import util.DualHashMap;










public abstract class BattleDecoder
  extends DataDecoder
{
  protected String sourceName;
  
  public String getSourceName()
  {
    return sourceName;
  }
  
  public int getSectionID()
  {
    return -1;
  }
  
  public BattleDecoder()
  {
    super(BattleStructTypes.battleTypes, BattleStructTypes.ActorT, DataConstants.battleFunctionLib, DataConstants.battleScriptLib);
  }
  

  protected void findLocalPointers(ByteBuffer fileBuffer)
  {
    fileBuffer.position(startOffset);
    while (fileBuffer.position() < endOffset)
    {
      int v = fileBuffer.getInt();
      if (isLocalAddress(v)) {
        addPointer(v);
      }
    }
  }
  
  public String printFunctionArgs(PrintWriter pw, int[] args, int lineAddress)
  {
    switch (args[0])
    {
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
      switch (1.$SwitchMap$data$shared$lib$FunctionArgument$ArgumentType[type.ordinal()])
      {







      case 1: 
        printScriptWord(pw, args[i]);
        comment = getStringComment(args[i]);
        break;
      
      case 2: 
        printConstant(pw, constType, args[i]);
        break;
      
      case 3: 
        printCamera(pw, args[i]);
        break;
      
      case 4: 
        printBoolean(pw, args[i]);
        break;
      
      case 5: 
      case 6: 
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
        scanUnknownArguments(scriptPtr, line);

      }
      else
      {
        FunctionCallTracker.addCall(funcAddress);
        NamedFunction func = functionLibrary.get(funcAddress);
        

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
                    if ((listLength > 128) || (listLength < 1)) {
                      throw new RuntimeException(String.format("Invalid list length for function: %s (%d)", new Object[] {func
                        .getName(), Integer.valueOf(listLength) }));
                    }
                  } else if (isLocalAddress(v))
                  {
                    Pointer childPtr = getPointer(v);
                    if (type == SharedStructTypes.UnknownT)
                      enqueueAsChild(scriptPtr, v, SharedStructTypes.UnknownT);
                  }
                }
              }
            } }
        }
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
  
  protected void writeRawFile(File f, ByteBuffer fileBuffer) throws IOException {
    byte[] sectionBytes = new byte[endOffset - startOffset];
    fileBuffer.position(startOffset);
    fileBuffer.get(sectionBytes);
    
    FileOutputStream fos = new FileOutputStream(f);
    FileChannel out = fos.getChannel();
    out.write(ByteBuffer.wrap(sectionBytes));
    fos.close();
  }
  

  protected int guessType(PointerHeuristic h, ByteBuffer fileBuffer)
  {
    int matches = super.guessType(h, fileBuffer);
    
    if (isEnemy(h, fileBuffer))
    {
      structType = BattleStructTypes.ActorT;
      matches++;
    }
    
    if (matches != 1) {
      structType = SharedStructTypes.UnknownT;
    }
    return matches;
  }
  
  private boolean isEnemy(PointerHeuristic h, ByteBuffer fileBuffer)
  {
    fileBuffer.position(start);
    int length = h.getLength();
    int enemyOffset = 0;
    

    if (length == 40) {
      fileBuffer.getInt();
    } else if (length != 36) {
      enemyOffset = -4;
    } else {
      return false;
    }
    
    if (fileBuffer.get() != 0) return false;
    int nameIndex = fileBuffer.get() & 0xFF;
    if (nameIndex > 212) { return false;
    }
    
    fileBuffer.get();
    if ((fileBuffer.get() & 0xFF) > 99) { return false;
    }
    
    if ((fileBuffer.getShort() & 0xFFFF) > 32) return false;
    if ((fileBuffer.getShort() & 0xFFFF) != 0) { return false;
    }
    
    if (!isLocalAddress(fileBuffer.getInt())) return false;
    if (!isLocalAddress(fileBuffer.getInt())) { return false;
    }
    

    structOffset = enemyOffset;
    return true;
  }
}

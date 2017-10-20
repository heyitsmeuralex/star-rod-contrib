package data.shared.struct.function;

import asm.AsmUtils;
import asm.MIPS;
import asm.pseudoinstruction.PseudoInstruction;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.PointerHeuristic;
import data.shared.lib.FunctionLibrary;
import data.shared.lib.NamedFunction;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import util.Logger;

public class Function
{
  public static final char LABEL_CHAR = '.';
  
  public Function() {}
  
  public static FunctionScanResults scan(DataDecoder decoder, ByteBuffer fileBuffer, int functionAddress)
  {
    int startOffset = fileBuffer.position();
    assert (startOffset == decoder.toOffset(functionAddress));
    
    FunctionScanResults results = new FunctionScanResults();
    List<String> asmList = scanPass(decoder, fileBuffer, results, functionAddress);
    Queue<JumpTable> newTables = findJumpTables(decoder, asmList, jumpTableAddresses);
    

    while (!newTables.isEmpty())
    {
      while (!newTables.isEmpty())
      {
        JumpTable table = (JumpTable)newTables.poll();
        jumpTables.add(table);
        jumpTableAddresses.add(Integer.valueOf(baseAddress));
        scanJumpTable(decoder, fileBuffer, results, table, functionAddress);
      }
      
      fileBuffer.position(startOffset);
      asmList = scanPass(decoder, fileBuffer, results, functionAddress);
      newTables = findJumpTables(decoder, asmList, jumpTableAddresses);
    }
    
    fileBuffer.position(startOffset);
    asmList = scanPass(decoder, fileBuffer, results, functionAddress);
    
    scanPseudoinstructions(decoder, asmList, results);
    intTables.removeAll(jumpTableAddresses);
    
    return results;
  }
  










  private static Queue<JumpTable> findJumpTables(DataDecoder decoder, List<String> asmList, Set<Integer> foundTables)
  {
    String[][] tokens = AsmUtils.tokenize(asmList);
    List<Integer> jumpTableLines = AsmUtils.findSequence(tokens, new String[] { "LUI", "ADDU", "LW", "JR" });
    Queue<JumpTable> jumpTables = new java.util.LinkedList();
    
    for (Iterator localIterator = jumpTableLines.iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
      
      int tableSize = -1;
      for (int j = i - 2; j >= 0; j--)
      {
        if (tokens[j][0].equals("SLTIU"))
        {

          assert (tokens[j][2].equals(tokens[(i - 1)][2]));
          tableSize = Integer.parseInt(tokens[j][3], 16);
          break;
        }
      }
      
      assert (tableSize > 0);
      
      assert (tokens[(i - 1)][1].equals("V0"));
      assert (tokens[(i - 1)][3].equals("2"));
      assert (tokens[i][1].equals("AT"));
      int upper = (int)Long.parseLong(tokens[i][2], 16);
      assert (tokens[(i + 1)][1].equals("AT"));
      assert (tokens[(i + 1)][2].equals("AT"));
      assert (tokens[(i + 1)][3].equals("V0"));
      assert (tokens[(i + 2)][1].equals("V0"));
      int lower = (int)Long.parseLong(tokens[(i + 2)][2], 16);
      assert (tokens[(i + 2)][3].equals("AT"));
      assert (tokens[(i + 3)][1].equals("V0"));
      
      int tableAddress = AsmUtils.makeAddress(upper, lower);
      
      JumpTable table = new JumpTable(tableAddress, tableSize);
      
      if (decoder.isLocalAddress(tableAddress))
      {
        if (!foundTables.contains(Integer.valueOf(tableAddress))) {
          jumpTables.add(table);
        }
      } else {
        Logger.log(String.format("Function uses nonlocal jump table at %08X\r\n", new Object[] { Integer.valueOf(tableAddress) }), util.Priority.WARNING);
      }
    }
    return jumpTables;
  }
  






  private static void scanJumpTable(DataDecoder decoder, ByteBuffer fileBuffer, FunctionScanResults findings, JumpTable table, int functionAddress)
  {
    fileBuffer.position(decoder.toOffset(baseAddress));
    
    for (int i = 0; i < numEntries; i++)
    {
      int v = fileBuffer.getInt();
      JumpTarget target = new JumpTarget(functionAddress, v, true);
      jumpTableAddress = baseAddress;
      jumpTableIndicies.add(Integer.valueOf(i));
      

      if (jumpTableTargets.contains(target)) {
        jumpTableTargets.remove(target);
      }
      jumpTableTargets.add(target);
    }
  }
  



  private static void scanPseudoinstructions(DataDecoder decoder, List<String> asmList, FunctionScanResults findings)
  {
    TreeSet<PseudoInstruction> piSet = PseudoInstruction.scanAll(asmList);
    
    for (PseudoInstruction pi : piSet)
    {
      int addr = pi.getAddress();
      if (decoder.isLocalAddress(addr))
      {
        switch (1.$SwitchMap$asm$pseudoinstruction$PseudoInstruction$Type[pi.getType().ordinal()])
        {
        case 1: 
        case 2: 
        case 3: 
        case 4: 
        case 5: 
        case 6: 
          unknownChildPointers.add(Integer.valueOf(addr));
          break;
        case 7: 
          constDoubles.add(Integer.valueOf(addr));
          break;
        case 8: 
        case 9: 
        case 10: 
        case 11: 
          break;
        case 12: 
        case 13: 
          byteTables.add(Integer.valueOf(addr));
          break;
        case 14: 
        case 15: 
          shortTables.add(Integer.valueOf(addr));
          break;
        case 16: 
        case 17: 
          intTables.add(Integer.valueOf(addr));
          break;
        case 18: 
        case 19: 
          floatTables.add(Integer.valueOf(addr));
          break;
        default: 
          unknownChildPointers.add(Integer.valueOf(addr));
        }
        
      }
    }
    for (String s : asmList)
    {
      if (s.startsWith("JAL "))
      {
        String[] tokens = s.split(",? +");
        int addr = (int)Long.parseLong(tokens[1], 16);
        if (decoder.isLocalAddress(addr)) {
          functionCalls.add(Integer.valueOf(addr));
        }
      }
    }
  }
  



  private static List<String> scanPass(DataDecoder decoder, ByteBuffer fileBuffer, FunctionScanResults findings, int functionAddress)
  {
    List<String> asmList = new ArrayList();
    TreeSet<JumpTarget> currentTargets = new TreeSet(branchTargets);
    int currentAddress = functionAddress;
    
    int nextTargetAddress = currentTargets.isEmpty() ? 0 : pollFirsttargetAddr;
    boolean isReturnDelaySlot = false;
    int v = 0;
    
    do
    {
      isReturnDelaySlot = v == 65011720;
      v = fileBuffer.getInt();
      
      if ((currentAddress == nextTargetAddress) || ((nextTargetAddress == 0) && (!currentTargets.isEmpty()))) {
        nextTargetAddress = currentTargets.isEmpty() ? 0 : pollFirsttargetAddr;
      }
      int jumpAddr = MIPS.getJumpAddress(currentAddress, v);
      if (jumpAddr != -1)
      {
        JumpTarget jumpTarget = new JumpTarget(functionAddress, jumpAddr);
        if (!branchTargets.contains(jumpTarget))
        {
          branchTargets.add(jumpTarget);
          currentTargets.add(jumpTarget);
        }
      }
      

      if (v >>> 26 == 2)
      {
        int dest = Integer.MIN_VALUE + ((v & 0x3FFFFFF) << 2);
        int jumpDistance = dest - currentAddress;
        v = 0x10000000 | jumpDistance - 4 >> 2;
      }
      
      String s = String.format("%08X", new Object[] { Integer.valueOf(v) });
      asmList.add(MIPS.disassemble(s));
      
      currentAddress += 4;
    } while ((Integer.compareUnsigned(currentAddress, nextTargetAddress) <= 0) || (!isReturnDelaySlot));
    
    return asmList;
  }
  







  public static void print(DataDecoder decoder, Pointer ptr, TreeMap<Integer, JumpTarget> jumpTargetMap, TreeMap<Integer, JumpTarget> jumpTableTargetMap, FunctionLibrary functionLib, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int functionAddress = address;
    ArrayList<String> asmList = new ArrayList();
    
    for (int i = 0; i < length / 4; i++)
    {
      int v = fileBuffer.getInt();
      

      if (v >>> 26 == 2)
      {
        int dest = Integer.MIN_VALUE + ((v & 0x3FFFFFF) << 2);
        int jumpDistance = dest - (functionAddress + 4 * i);
        v = 0x10000000 | jumpDistance - 4 >> 2;
      }
      
      String s = String.format("%08X", new Object[] { Integer.valueOf(v) });
      asmList.add(MIPS.disassemble(s));
    }
    
    TreeSet<JumpTarget> jumpTargetSet = new TreeSet(jumpTargetMap.values());
    TreeSet<JumpTarget> jumpTableTargetSet = new TreeSet(jumpTableTargetMap.values());
    
    JumpTarget nextJumpTarget;
    
    int nextTargetAddress;
    for (;;)
    {
      nextJumpTarget = (JumpTarget)jumpTargetSet.pollFirst();
      nextTargetAddress = nextJumpTarget == null ? 0 : targetAddr;
      
      if (nextJumpTarget != null)
      {

        if (nextTargetAddress >= functionAddress)
          break;
      }
    }
    JumpTarget nextJumpTableTarget;
    int nextJumpTargetAddress;
    for (;;) {
      nextJumpTableTarget = (JumpTarget)jumpTableTargetSet.pollFirst();
      nextJumpTargetAddress = nextJumpTableTarget == null ? 0 : targetAddr;
      
      if (nextJumpTableTarget != null)
      {

        if (nextJumpTargetAddress >= functionAddress)
          break;
      }
    }
    int insOffset = 0;
    List<String> newInstructions = PseudoInstruction.addAll(asmList, decoder);
    
    for (int i = 0; i < newInstructions.size(); i++) {
      Object localObject;
      int index;
      if (functionAddress + insOffset == nextJumpTargetAddress)
      {
        String tableName = decoder.getVariableName(jumpTableAddress);
        StringBuilder sb = jumpTableIndicies.size() > 1 ? new StringBuilder("entries") : new StringBuilder("entry");
        for (localObject = jumpTableIndicies.iterator(); ((Iterator)localObject).hasNext();) { index = ((Integer)((Iterator)localObject).next()).intValue();
          sb.append(String.format(" %d`", new Object[] { Integer.valueOf(index) })); }
        pw.println("% LBL: from " + tableName + " , " + sb.toString());
        
        nextJumpTableTarget = (JumpTarget)jumpTableTargetSet.pollFirst();
        nextJumpTargetAddress = nextJumpTableTarget == null ? 0 : targetAddr;
      }
      
      if (functionAddress + insOffset == nextTargetAddress)
      {
        if (printLineOffsets) {
          pw.printf("        %co%X\r\n", new Object[] { Character.valueOf('.'), Integer.valueOf(insOffset) });
        } else {
          pw.printf("\t%co%X\r\n", new Object[] { Character.valueOf('.'), Integer.valueOf(insOffset) });
        }
        nextJumpTarget = (JumpTarget)jumpTargetSet.pollFirst();
        nextTargetAddress = nextJumpTarget == null ? 0 : targetAddr;
      }
      
      String ins = (String)newInstructions.get(i);
      String[] tokens = ins.split("\\s+");
      

      if (tokens[0].startsWith("B"))
      {
        localObject = tokens[0];index = -1; switch (((String)localObject).hashCode()) {case 2041936:  if (((String)localObject).equals("BLTZ")) index = 0; break; case 63300092:  if (((String)localObject).equals("BLTZL")) index = 1; break; case 1962302587:  if (((String)localObject).equals("BLTZAL")) index = 2; break; case 701838129:  if (((String)localObject).equals("BLTZALL")) index = 3; break; case 2036666:  if (((String)localObject).equals("BGEZ")) index = 4; break; case 63136722:  if (((String)localObject).equals("BGEZL")) index = 5; break; case 1957238117:  if (((String)localObject).equals("BGEZAL")) index = 6; break; case 544839559:  if (((String)localObject).equals("BGEZALL")) index = 7; break; case 65646:  if (((String)localObject).equals("BEQ")) index = 8; break; case 2035102:  if (((String)localObject).equals("BEQL")) index = 9; break; case 65913:  if (((String)localObject).equals("BNE")) index = 10; break; case 2043379:  if (((String)localObject).equals("BNEL")) index = 11; break; case 2041471:  if (((String)localObject).equals("BLEZ")) index = 12; break; case 63285677:  if (((String)localObject).equals("BLEZL")) index = 13; break; case 2037131:  if (((String)localObject).equals("BGTZ")) index = 14; break; case 63151137:  if (((String)localObject).equals("BGTZL")) index = 15; break; case 2032182:  if (((String)localObject).equals("BC1F")) index = 16; break; case 62997718:  if (((String)localObject).equals("BC1FL")) index = 17; break; case 2032196:  if (((String)localObject).equals("BC1T")) index = 18; break; case 62998152:  if (((String)localObject).equals("BC1TL")) index = 19; break; } switch (index)
        {
        case 0: 
        case 1: 
        case 2: 
        case 3: 
        case 4: 
        case 5: 
        case 6: 
        case 7: 
        case 8: 
        case 9: 
        case 10: 
        case 11: 
        case 12: 
        case 13: 
        case 14: 
        case 15: 
        case 16: 
        case 17: 
        case 18: 
        case 19: 
          int lastSpaceIndex = ins.lastIndexOf(' ') + 1;
          int breakOffset = (int)Long.parseLong(ins.substring(lastSpaceIndex), 16);
          ins = String.format("%s.o%X", new Object[] { ins.substring(0, lastSpaceIndex), Integer.valueOf(insOffset + breakOffset + 4) });
        }
      }
      else if (tokens[0].equals("JAL"))
      {
        int address = (int)Long.parseLong(ins.substring(4).trim(), 16);
        NamedFunction func = functionLib.get(address);
        if (func != null) {
          ins = String.format("%-9s {Func:%s}", new Object[] { "JAL", func.getName() });
        } else {
          ins = String.format("%-9s %s", new Object[] { "JAL", decoder.getVariableName(address) });
        }
      }
      if (printLineOffsets) {
        pw.printf("%5X:  %s\n", new Object[] { Integer.valueOf(insOffset), ins });
      } else {
        pw.printf("\t%s\n", new Object[] { ins });
      }
      
      if (PseudoInstruction.isValidOpcode(tokens[0]))
      {
        if (i + 2 < newInstructions.size())
        {
          String k = (String)newInstructions.get(i + 2);
          if (k == "RESERVED")
          {
            insOffset -= 4;
          }
        }
        
        insOffset += 4 * PseudoInstruction.getLengthFromLine(tokens);
      }
      else
      {
        insOffset += 4;
      }
    }
  }
  
  public static int getPatchLength(List<String[]> lines)
  {
    int length = 0;
    
    for (String[] line : lines)
    {
      if (line[0].charAt(0) != '.')
      {

        String opname = line[0];
        if (PseudoInstruction.isValidOpcode(opname))
        {
          length += 4 * PseudoInstruction.getLengthFromLine(line);


        }
        else if (!opname.equals("RESERVED"))
        {


          if ((opname.indexOf('.') >= 0) || (MIPS.supportedInstructions.contains(opname))) {
            length += 4;
          } else
            throw new RuntimeException("Unknown instruction: " + line[0]); }
      }
    }
    return length;
  }
  
  public static boolean isFunction(PointerHeuristic h, ByteBuffer fileBuffer)
  {
    int length = h.getLength();
    
    if (length < 12) {
      return false;
    }
    fileBuffer.position(start);
    int pushStack = fileBuffer.getInt();
    
    fileBuffer.position(start + length - 8);
    int jumpRegister = fileBuffer.getInt();
    int popStack = fileBuffer.getInt();
    

    if ((pushStack & 0x8000) == 666730496)
    {

      structOffset = 0;
      return true;
    }
    return false;
  }
}

package asm.pseudoinstruction;

import asm.AssemblerException;
import asm.MIPS;
import asm.MIPS.Instruction;
import data.shared.DataUtils;
import data.shared.decoder.DataDecoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;

public class PseudoInstruction
{
  public static final String RESERVED = "RESERVED";
  private static HashMap<Format, Type> formatTypeMap;
  private static HashMap<String, Type> opcodeNameMap;
  private final Type type;
  private final int firstLine;
  private final boolean useDelaySlot;
  private String register;
  private String index;
  private int addr;
  
  private static class Format
  {
    private final PatternMatch.Pattern pattern;
    private final MIPS.Instruction lastIns;
    
    public Format(PatternMatch.Pattern pattern, MIPS.Instruction lastIns)
    {
      this.pattern = pattern;
      this.lastIns = lastIns;
    }
    

    public int hashCode()
    {
      return (pattern.ordinal() << 16) + lastIns.ordinal();
    }
    

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Format other = (Format)obj;
      if (lastIns != lastIns)
        return false;
      if (pattern != pattern)
        return false;
      return true;
    }
  }
  



  static
  {
    formatTypeMap = new HashMap();
    opcodeNameMap = new HashMap();
    for (Type t : Type.values())
    {
      opcodeNameMap.put(opcode, t);
      formatTypeMap.put(new Format(pattern, lastIns), t);
    }
  }
  
  public static boolean isValidOpcode(String opcode)
  {
    if ((opcode.equals("PUSH")) || (opcode.equals("POP")) || (opcode.equals("JPOP"))) {
      return true;
    }
    return opcodeNameMap.containsKey(opcode);
  }
  
  public static int getLengthFromLine(String[] tokens)
  {
    if ((tokens[0].equals("PUSH")) || (tokens[0].equals("POP"))) {
      return tokens.length;
    }
    if (tokens[0].equals("JPOP")) {
      return tokens.length + 1;
    }
    return opcodeNameMapget0pattern.length;
  }
  
  public static enum Type
  {
    LIA("LIA", 3, PatternMatch.Pattern.LI, MIPS.Instruction.ADDIU, "LIA  A0, 001CFFFF"), 
    LIO("LIO", 3, PatternMatch.Pattern.LI, MIPS.Instruction.ORI, "LIO  A0, 001CFFFF"), 
    LIF("LIF", 3, PatternMatch.Pattern.LI, MIPS.Instruction.MTC1, "LIF  F0, 4.0"), 
    
    LAB("LAB", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LB, "LAB  A0, 80240000"), 
    SAB("SAB", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.SB, "SAB  A0, 80240000"), 
    LABU("LABU", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LBU, "LABU A0, 80240000"), 
    LAH("LAH", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LH, "LAH  A0, 80240000"), 
    SAH("SAH", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.SH, "SAH  A0, 80240000"), 
    LAHU("LAHU", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LHU, "LAHU A0, 80240000"), 
    LAW("LAW", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LW, "LAW  A0, 80240000"), 
    SAW("SAW", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.SW, "SAW  A0, 80240000"), 
    LAF("LAF", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LWC1, "LAF  F0, 80240000"), 
    SAF("SAF", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.SWC1, "SAF  F0, 80240000"), 
    LAD("LAD", 3, PatternMatch.Pattern.LSA, MIPS.Instruction.LDC1, "LAD  F0, 80240000"), 
    
    LTB("LTB", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LB, "LTB  A0, V0 (80240000)"), 
    LTBU("LTBU", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LBU, "LTBU A0, V0 (80240000)"), 
    LTH("LTH", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LH, "LTH  A0, V0 (80240000)"), 
    LTHU("LTHU", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LHU, "LTHU A0, V0 (80240000)"), 
    LTW("LTW", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LW, "LTW  A0, V0 (80240000)"), 
    LTF("LTF", 4, PatternMatch.Pattern.LST, MIPS.Instruction.LWC1, "LTF  F0, V0 (80240000)"), 
    STB("STB", 4, PatternMatch.Pattern.LST, MIPS.Instruction.SB, "STB  A0, V0 (80240000)"), 
    STH("STH", 4, PatternMatch.Pattern.LST, MIPS.Instruction.SH, "STH  A0, V0 (80240000)"), 
    STW("STW", 4, PatternMatch.Pattern.LST, MIPS.Instruction.SW, "STW  A0, V0 (80240000)"), 
    STF("STF", 4, PatternMatch.Pattern.LST, MIPS.Instruction.SWC1, "STF  F0, V0 (80240000)");
    
    public final String opcode;
    public final PatternMatch.Pattern pattern;
    
    private Type(String opcode, int argc, PatternMatch.Pattern pattern, MIPS.Instruction lastIns, String example) { this.opcode = opcode;
      this.pattern = pattern;
      this.lastIns = lastIns;
      this.argc = argc;
      this.example = example;
    }
    





    public final MIPS.Instruction lastIns;
    



    public final int argc;
    



    public final String example;
  }
  




  public int getAddress()
  {
    return addr;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public static final Comparator<PseudoInstruction> LINE_ORDER_COMPARATOR = new Comparator()
  {
    public int compare(PseudoInstruction a, PseudoInstruction b)
    {
      return firstLine - firstLine;
    }
  };
  
  private PseudoInstruction(Type type, int line, boolean delayed)
  {
    this.type = type;
    firstLine = line;
    useDelaySlot = delayed;
    
    register = null;
    index = null;
  }
  






  private static PseudoInstruction createFromLine(String[][] tokens, int line)
  {
    String[] ins = tokens[line];
    
    boolean delaySlot = false;
    if (tokens.length > line + 2)
    {
      delaySlot = tokens[(line + 2)][0].equals("RESERVED");
    }
    
    Type type = (Type)opcodeNameMap.get(ins[0]);
    
    if (type == null) {
      return null;
    }
    if (ins.length != argc)
    {
      throw new AssemblerException(String.format("Incorrect format for %s: \n\"%s\" \nCorrect format example: \n\"%s\"", new Object[] { opcode, 
      



        DataUtils.combineTokens(ins), example }));
    }
    
    PseudoInstruction pi = new PseudoInstruction(type, line, delaySlot);
    try
    {
      switch (2.$SwitchMap$asm$pseudoinstruction$PseudoInstruction$Type[type.ordinal()])
      {
      case 1: 
      case 2: 
        register = ins[1];
        addr = ((int)Long.parseLong(ins[2], 16));
        break;
      case 3: 
        register = ins[1];
        float f = Float.parseFloat(ins[2]);
        addr = Float.floatToIntBits(f);
        break;
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
        register = ins[1];
        addr = ((int)Long.parseLong(ins[2], 16));
        break;
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 23: 
      case 24: 
        register = ins[1];
        index = ins[2];
        addr = ((int)Long.parseLong(ins[3], 16));
      }
    }
    catch (NumberFormatException e)
    {
      throw new AssemblerException("Invalid pseudoinstruction caused NumberFormatException:\n" + opcode + " from line \"" + DataUtils.combineTokens(ins) + "\"");
    }
    
    return pi;
  }
  






  private static PseudoInstruction createFromPattern(String[][] tokens, PatternMatch match)
  {
    Type type = (Type)formatTypeMap.get(new Format(pattern, lastIns));
    if (type == null) {
      throw new IllegalArgumentException("Unknown pseudoinstruction: " + pattern + " " + lastIns);
    }
    PseudoInstruction pi = new PseudoInstruction(type, line, delaySlot);
    if (pi.loadFromASM(tokens)) {
      return pi;
    }
    return null;
  }
  
  private boolean loadFromASM(String[][] tokens)
  {
    String[] lineLUI = null;
    String[] lineBRJ = null;
    String[] lineLLI = null;
    String[] lineMEM = null;
    String[] lineADD = null;
    


    switch (2.$SwitchMap$asm$pseudoinstruction$PatternMatch$Pattern[type.pattern.ordinal()])
    {
    case 1: 
      lineLUI = tokens[firstLine];
      lineBRJ = useDelaySlot ? tokens[(firstLine + 1)] : null;
      lineLLI = tokens[(firstLine + 1)];
      


      if (type.lastIns == MIPS.Instruction.MTC1)
      {
        register = lineLLI[2];
        if (!lineLUI[1].equals(lineLLI[1]))
          return false;
        addr = (Integer.parseInt(lineLUI[2], 16) << 16);

      }
      else
      {

        register = lineLUI[1];
        if (!register.equals(lineLLI[2])) {
          return false;
        }
        assert (register.equals(lineLLI[1]));
        
        int upper = Integer.parseInt(lineLUI[2], 16) << 16;
        int lower = (short)Integer.parseInt(lineLLI[3], 16);
        
        switch (type.lastIns) {
        case ADDIU: 
          addr = (upper + lower); break;
        case ORI:  addr = (upper | lower & 0xFFFF); break;
        default: 
          throw new IllegalArgumentException("Unknown pseudoinstruction: " + type.pattern + " " + type.lastIns);
        }
        
      }
      
      return true;
    
    case 2: 
      lineLUI = tokens[firstLine];
      lineBRJ = useDelaySlot ? tokens[(firstLine + 1)] : null;
      lineMEM = tokens[(firstLine + 1)];
      
      register = lineMEM[1];
      if (!lineLUI[1].equals(lineMEM[3])) {
        return false;
      }
      int upper = Integer.parseInt(lineLUI[2], 16) << 16;
      int lower = (short)Integer.parseInt(lineMEM[2], 16);
      addr = (upper + lower);
      
      switch (2.$SwitchMap$asm$MIPS$Instruction[type.lastIns.ordinal()])
      {


      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        assert (lineLUI[1].equals(register));
        if (useDelaySlot)
        {
          assert (!branchUsesRegister(lineBRJ, register));
        }
        return true;
      


      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
        assert (lineLUI[1].equals("AT"));
        if (useDelaySlot)
        {
          assert (!branchUsesRegister(lineBRJ, register));
        }
        return true;
      }
      
      throw new IllegalArgumentException("Unknown pseudoinstruction: " + type.pattern + " " + type.lastIns);
    


    case 3: 
      lineLUI = tokens[firstLine];
      lineADD = tokens[(firstLine + 1)];
      lineBRJ = useDelaySlot ? tokens[(firstLine + 2)] : null;
      lineMEM = tokens[(firstLine + 2)];
      
      register = lineMEM[1];
      index = lineADD[3];
      
      if (!lineLUI[1].equals(lineADD[1])) {
        return false;
      }
      if (!lineLUI[1].equals(lineADD[2])) {
        return false;
      }
      if (!lineLUI[1].equals(lineMEM[3])) {
        return false;
      }
      int upper = Integer.parseInt(lineLUI[2], 16) << 16;
      int lower = (short)Integer.parseInt(lineMEM[2], 16);
      addr = (upper + lower);
      
      switch (2.$SwitchMap$asm$MIPS$Instruction[type.lastIns.ordinal()])
      {



      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        if (register.equals(index)) {
          if ((!$assertionsDisabled) && (!lineLUI[1].equals("AT"))) throw new AssertionError();
        } else
          assert (lineLUI[1].equals(register));
        if (useDelaySlot)
        {
          assert (!branchUsesRegister(lineBRJ, register));
        }
        return true;
      



      case 10: 
      case 11: 
      case 13: 
        assert (lineLUI[1].equals("AT"));
        if (useDelaySlot)
        {
          assert (!branchUsesRegister(lineBRJ, register));
        }
        return true; }
      
      throw new IllegalArgumentException("Unknown pseudoinstruction: " + type.pattern + " " + type.lastIns);
    }
    
    
    throw new IllegalArgumentException("Unknown pseudoinstruction type: " + type.pattern);
  }
  


  private static boolean branchUsesRegister(String[] line, String register)
  {
    MIPS.Instruction ins = (MIPS.Instruction)MIPS.InstructionMap.get(line[0]);
    switch (2.$SwitchMap$asm$MIPS$Instruction[ins.ordinal()])
    {


    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
      return (line[1].equals(register)) || (line[2].equals(register));
    

    case 19: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
      return line[1].equals(register);
    

    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
      return false; }
    
    throw new IllegalArgumentException("Instruction is not a branch or jump: " + ins);
  }
  



  private String getPseudoASM(DataDecoder decoder)
  {
    String addrName;
    

    String addrName;
    

    if (decoder != null) {
      addrName = decoder.getVariableName(addr);
    } else
      addrName = String.format("%08X", new Object[] { Integer.valueOf(addr) });
    String replacement;
    String replacement; String replacement; switch (2.$SwitchMap$asm$pseudoinstruction$PatternMatch$Pattern[type.pattern.ordinal()])
    {
    case 1: 
      String replacement;
      
      if (type.lastIns == MIPS.Instruction.MTC1) {
        replacement = String.format("%-9s %s, %s", new Object[] { type.opcode, register, Float.valueOf(Float.intBitsToFloat(addr)) });
      }
      else {
        if (decoder != null)
          addrName = decoder.getScriptWord(addr);
        replacement = String.format("%-9s %s, %s", new Object[] { type.opcode, register, addrName });
      }
      break;
    


    case 2: 
      replacement = String.format("%-9s %s, %s", new Object[] { type.opcode, register, addrName });
      break;
    



    case 3: 
      replacement = String.format("%-9s %s, %s (%s)", new Object[] { type.opcode, register, index, addrName });
      break;
    
    default: 
      throw new IllegalArgumentException("Unknown pseudoinstruction type: " + type.pattern);
    }
    
    String replacement;
    return replacement;
  }
  




  private String[] getASM()
  {
    String[] replacement = new String[type.pattern.length];
    
    int lower = (short)addr;
    int upper = addr - lower >>> 16;
    lower &= 0xFFFF;
    
    switch (2.$SwitchMap$asm$pseudoinstruction$PseudoInstruction$Type[type.ordinal()])
    {
    case 1: 
      upper = addr >>> 16;
      lower = addr & 0xFFFF;
    


    case 2: 
      replacement[0] = String.format("%-9s %s, %X", new Object[] { "LUI", register, Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s %s, %s, %X", new Object[] { type.lastIns, register, register, Integer.valueOf(lower) });
      break;
    

    case 3: 
      replacement[0] = String.format("%-9s AT, %X", new Object[] { "LUI", Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s AT, %s", new Object[] { "MTC1", register });
      break;
    


    case 4: 
    case 6: 
    case 7: 
    case 9: 
    case 10: 
      replacement[0] = String.format("%-9s %s, %X", new Object[] { "LUI", register, Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s %s, %X (%s)", new Object[] { type.lastIns, register, Integer.valueOf(lower), register });
      break;
    



    case 5: 
    case 8: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
      replacement[0] = String.format("%-9s AT, %X", new Object[] { "LUI", Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s %s, %X (AT)", new Object[] { type.lastIns, register, Integer.valueOf(lower) });
      break;
    




    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
      String intermediary = register.equals(index) ? "AT" : register;
      replacement[0] = String.format("%-9s %s, %X", new Object[] { "LUI", intermediary, Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s %s, %s, %s", new Object[] { "ADDU", intermediary, intermediary, index });
      replacement[2] = String.format("%-9s %s, %X (%s)", new Object[] { type.lastIns, register, Integer.valueOf(lower), intermediary });
      break;
    




    case 20: 
    case 21: 
    case 22: 
    case 23: 
    case 24: 
      replacement[0] = String.format("%-9s AT, %X", new Object[] { "LUI", Integer.valueOf(upper) });
      replacement[1] = String.format("%-9s AT, AT, %s", new Object[] { "ADDU", index });
      replacement[2] = String.format("%-9s %s, %X (AT)", new Object[] { type.lastIns, register, Integer.valueOf(lower) });
    }
    
    
    return replacement;
  }
  

  public static List<String> removeAll(List<String> instructionList)
  {
    String[][] tokens = new String[instructionList.size()][];
    
    for (int i = 0; i < instructionList.size(); i++)
    {
      String asm = (String)instructionList.get(i);
      tokens[i] = asm.replaceAll("[()]", "").split(",? +");
    }
    
    List<String> newinstructions = new LinkedList();
    for (int i = 0; i < instructionList.size(); i++)
    {
      PseudoInstruction pi = createFromLine(tokens, i);
      if (pi == null)
      {
        if (tokens[i][0].equals("PUSH"))
        {
          int numArgs = tokens[i].length - 1;
          if (numArgs < 1) {
            throw new IllegalArgumentException("Invalid PUSH instruction: empty register list");
          }
          newinstructions.add(String.format("ADDIU SP, SP, %4X", new Object[] { Integer.valueOf(-(16 + numArgs * 4)) }));
          
          for (int j = 0; j < numArgs; j++)
          {
            String regName = tokens[i][(j + 1)];
            newinstructions.add(String.format("SW %s, %4X (SP)", new Object[] { regName, Integer.valueOf(16 + 4 * j) }));

          }
          

        }
        else if (tokens[i][0].equals("POP"))
        {
          int numArgs = tokens[i].length - 1;
          if (numArgs < 1) {
            throw new IllegalArgumentException("Invalid POP instruction: empty register list");
          }
          for (int j = 0; j < numArgs; j++)
          {
            String regName = tokens[i][(j + 1)];
            newinstructions.add(String.format("LW %s, %4X (SP)", new Object[] { regName, Integer.valueOf(16 + 4 * j) }));
          }
          
          newinstructions.add(String.format("ADDIU SP, SP, %4X", new Object[] { Integer.valueOf(16 + numArgs * 4) }));


        }
        else if (tokens[i][0].equals("JPOP"))
        {
          int numArgs = tokens[i].length - 1;
          if (numArgs < 1) {
            throw new IllegalArgumentException("Invalid JPOP instruction: empty register list");
          }
          for (int j = 0; j < numArgs; j++)
          {
            String regName = tokens[i][(j + 1)];
            newinstructions.add(String.format("LW %s, %4X (SP)", new Object[] { regName, Integer.valueOf(16 + 4 * j) }));
          }
          
          newinstructions.add("JR RA");
          newinstructions.add(String.format("ADDIU SP, SP, %4X", new Object[] { Integer.valueOf(16 + numArgs * 4) }));

        }
        else
        {
          newinstructions.add(instructionList.get(i));
        }
      }
      else {
        String[] replacements = pi.getASM();
        for (int j = 0; j < replacements.length; j++)
        {
          if ((useDelaySlot) && (j == replacements.length - 1))
          {
            String branch = (String)instructionList.get(i + 1);
            newinstructions.add(branch);
            i += 2;
          }
          
          newinstructions.add(replacements[j]);
        }
      }
    }
    return newinstructions;
  }
  
  public static List<String> addAll(List<String> instructionList)
  {
    return addAll(instructionList, null);
  }
  
  public static List<String> addAll(List<String> instructionList, DataDecoder decoder)
  {
    TreeSet<PseudoInstruction> piSet = scanAll(instructionList);
    return addAll(instructionList, piSet, decoder);
  }
  
  public static TreeSet<PseudoInstruction> scanAll(List<String> instructionList)
  {
    String[][] tokens = asm.AsmUtils.tokenize(instructionList);
    
    List<PatternMatch> matchList = PatternFinder.search(tokens);
    TreeSet<PseudoInstruction> piSet = new TreeSet(LINE_ORDER_COMPARATOR);
    
    for (PatternMatch m : matchList)
    {
      PseudoInstruction pi = createFromPattern(tokens, m);
      if (pi != null)
      {
        piSet.add(pi);
      }
    }
    
    return piSet;
  }
  
  private static List<String> addAll(List<String> instructionList, TreeSet<PseudoInstruction> piSet, DataDecoder decoder)
  {
    PseudoInstruction currentPI = (PseudoInstruction)piSet.pollFirst();
    List<String> newInstructions = new LinkedList();
    
    for (int i = 0; i < instructionList.size();)
    {
      if ((currentPI == null) || (i != firstLine))
      {
        newInstructions.add(instructionList.get(i));
        i++;
      }
      else
      {
        int insCount = type.pattern.length;
        String replacement = currentPI.getPseudoASM(decoder);
        newInstructions.add(replacement);
        
        if (useDelaySlot)
        {
          int branchLine = i + (insCount - 1);
          String branch = (String)instructionList.get(branchLine);
          newInstructions.add(branch);
          newInstructions.add("RESERVED");
          i++;
        }
        
        i += insCount;
        currentPI = (PseudoInstruction)piSet.pollFirst();
      }
    }
    return newInstructions;
  }
  
  public static void main(String[] args) throws java.io.IOException
  {
    File dumpDir = new File("./analysis/funcdump/");
    for (File f : dumpDir.listFiles())
    {
      if (f.getName().endsWith(".txt"))
      {
        System.out.println(f.getName());
        List<String> lines = new LinkedList();
        BufferedReader in = new BufferedReader(new java.io.FileReader(f));
        String line;
        while ((line = in.readLine()) != null)
        {
          if (!line.isEmpty())
          {

            lines.add(line); }
        }
        in.close();
        
        List<String> newlines = addAll(lines);
        
        File out = new File("./analysis/funcnew/NEW_" + f.getName());
        FileUtils.touch(out);
        PrintWriter pw = util.IOUtils.getBufferedPrintWriter(out);
        for (String s : newlines)
          pw.println(s);
        pw.close();
        
        Object newerlines = removeAll(newlines);
        
        assert (((List)newerlines).size() == lines.size());
        for (int i = 0; i < lines.size(); i++)
        {
          System.out.printf("%-30s %-30s\r\n", new Object[] { lines.get(i), ((List)newerlines).get(i) });
          assert (((String)((List)newerlines).get(i)).equals(lines.get(i)));
        }
      }
    }
    System.out.println("Done.");
  }
}

package asm;

import data.shared.DataUtils;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;































































public class MIPS
{
  private static final String NOP = "NOP";
  private static final String[] cpuRegNames = { "R0", "AT", "V0", "V1", "A0", "A1", "A2", "A3", "T0", "T1", "T2", "T3", "T4", "T5", "T6", "T7", "S0", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "T8", "T9", "K0", "K1", "GP", "SP", "S8", "RA" };
  



  private static final HashMap<String, Integer> cpuRegMap;
  


  private static final String[] fpuRegNames = { "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12", "F13", "F14", "F15", "F16", "F17", "F18", "F19", "F20", "F21", "F22", "F23", "F24", "F25", "F26", "F27", "F28", "F29", "F30", "F31" };
  



  private static final HashMap<String, Integer> fpuRegMap;
  


  private static final String[] fpuCondNames = { "F", "UN", "EQ", "UEQ", "OLT", "ULT", "OLE", "ULE", "SF", "NGLE", "SEQ", "NGL", "LT", "NGE", "LE", "NGT" };
  
  private static final HashMap<String, Integer> fpuCondMap;
  
  public static final HashMap<String, Instruction> InstructionMap;
  
  public static final TreeSet<String> supportedInstructions;
  
  private static final Instruction[] OpcodeTable;
  
  private static final Instruction[] SpecialTable;
  private static final Instruction[] RegimmTable;
  private static final Instruction[] Cop0Table;
  private static final Instruction[] Cop2Table;
  private static final int SPECIAL_OPCODE = 0;
  
  static
  {
    cpuRegMap = new HashMap();
    for (int i = 0; i < cpuRegNames.length; i++) {
      cpuRegMap.put(cpuRegNames[i], Integer.valueOf(i));
    }
    fpuRegMap = new HashMap();
    for (int i = 0; i < fpuRegNames.length; i++) {
      fpuRegMap.put(fpuRegNames[i], Integer.valueOf(i));
    }
    fpuCondMap = new HashMap();
    for (int i = 0; i < fpuCondNames.length; i++) {
      fpuCondMap.put(fpuCondNames[i], Integer.valueOf(i));
    }
    InstructionMap = new HashMap();
    supportedInstructions = new TreeSet();
    supportedInstructions.add("NOP");
    
    OpcodeTable = new Instruction[64];
    SpecialTable = new Instruction[64];
    RegimmTable = new Instruction[64];
    Cop0Table = new Instruction[64];
    Cop2Table = new Instruction[64];
    
    for (Instruction ins : Instruction.values())
    {
      InstructionMap.put(name, ins);
      supportedInstructions.add(name);
      
      switch (1.$SwitchMap$asm$MIPS$InstructionType[type.ordinal()]) {
      case 1: 
        OpcodeTable[id] = ins; break;
      case 2:  SpecialTable[id] = ins; break;
      case 3:  RegimmTable[id] = ins; break;
      case 4:  Cop0Table[id] = ins; break;
      case 5: 
        break; case 6:  Cop2Table[id] = ins;
      }
      
    }
  }
  





  protected static enum InstructionType
  {
    NORMAL(-1), 
    SPECIAL(0), 
    REGIMM(1), 
    COP0(16), 
    COP1(17), 
    COP2(18);
    
    public final int opcode;
    
    private InstructionType(int opcode)
    {
      this.opcode = opcode;
    }
  }
  

  protected static enum Format
  {
    RS_OFFSET(2), 
    RS_RT_OFFSET(3), 
    RT_OFFSET_BASE(3), 
    FPU_RT_OFFSET_BASE(3), 
    LUI_FMT(2), 
    RT_RS_IMMEDIATE(3), 
    J_TARGET(1), 
    

    RD_RS_RT(3), 
    RS_RT(2), 
    RD(1), 
    RS(1), 
    RD_RT_SA(3), 
    RD_RT_RS(3), 
    JALR_FMT(2), 
    JR_FMT(1), 
    SYSCALL_FMT(1), 
    

    FPU_OFFSET(1), 
    FD_FS(3), 
    FD_FS_FT(4), 
    COND_FMT(4), 
    RT_FS(2);
    
    private final int argc;
    
    private Format(int argc)
    {
      this.argc = argc;
    }
  }
  
  public static enum Instruction
  {
    BEQ("BEQ", MIPS.Format.RS_RT_OFFSET, 4), 
    BEQL("BEQL", MIPS.Format.RS_RT_OFFSET, 20), 
    BNE("BNE", MIPS.Format.RS_RT_OFFSET, 5), 
    BNEL("BNEL", MIPS.Format.RS_RT_OFFSET, 21), 
    
    BLEZ("BLEZ", MIPS.Format.RS_OFFSET, 6), 
    BGTZ("BGTZ", MIPS.Format.RS_OFFSET, 7), 
    BLEZL("BLEZL", MIPS.Format.RS_OFFSET, 22), 
    BGTZL("BGTZL", MIPS.Format.RS_OFFSET, 23), 
    
    BLTZ("BLTZ", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 0), 
    BGEZ("BGEZ", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 1), 
    BLTZL("BLTZL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 2), 
    BGEZL("BGEZL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 3), 
    BLTZAL("BLTZAL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 16), 
    BGEZAL("BGEZAL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 17), 
    BLTZALL("BLTZALL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 18), 
    BGEZALL("BGEZALL", MIPS.InstructionType.REGIMM, MIPS.Format.RS_OFFSET, 19), 
    
    J("J", MIPS.Format.J_TARGET, 2), 
    JAL("JAL", MIPS.Format.J_TARGET, 3), 
    
    JR("JR", MIPS.InstructionType.SPECIAL, MIPS.Format.JR_FMT, 8), 
    JALR("JALR", MIPS.InstructionType.SPECIAL, MIPS.Format.JALR_FMT, 9), 
    
    SYSCALL("SYSCALL", MIPS.InstructionType.SPECIAL, MIPS.Format.SYSCALL_FMT, 12), 
    BREAK("BREAK", MIPS.InstructionType.SPECIAL, MIPS.Format.SYSCALL_FMT, 13), 
    
    LB("LB", MIPS.Format.RT_OFFSET_BASE, 32), 
    LBU("LBU", MIPS.Format.RT_OFFSET_BASE, 36), 
    LD("LD", MIPS.Format.RT_OFFSET_BASE, 55), 
    LDL("LDL", MIPS.Format.RT_OFFSET_BASE, 26), 
    LDR("LDR", MIPS.Format.RT_OFFSET_BASE, 27), 
    LH("LH", MIPS.Format.RT_OFFSET_BASE, 33), 
    LHU("LHU", MIPS.Format.RT_OFFSET_BASE, 37), 
    LL("LL", MIPS.Format.RT_OFFSET_BASE, 48), 
    LLD("LLD", MIPS.Format.RT_OFFSET_BASE, 52), 
    LW("LW", MIPS.Format.RT_OFFSET_BASE, 35), 
    LWL("LWL", MIPS.Format.RT_OFFSET_BASE, 34), 
    LWR("LWR", MIPS.Format.RT_OFFSET_BASE, 38), 
    LWU("LWU", MIPS.Format.RT_OFFSET_BASE, 39), 
    SB("SB", MIPS.Format.RT_OFFSET_BASE, 40), 
    SC("SC", MIPS.Format.RT_OFFSET_BASE, 56), 
    SCD("SCD", MIPS.Format.RT_OFFSET_BASE, 60), 
    SD("SD", MIPS.Format.RT_OFFSET_BASE, 63), 
    SDL("SDL", MIPS.Format.RT_OFFSET_BASE, 44), 
    SDR("SDR", MIPS.Format.RT_OFFSET_BASE, 45), 
    SH("SH", MIPS.Format.RT_OFFSET_BASE, 41), 
    SW("SW", MIPS.Format.RT_OFFSET_BASE, 43), 
    SWL("SWL", MIPS.Format.RT_OFFSET_BASE, 42), 
    SWR("SWR", MIPS.Format.RT_OFFSET_BASE, 46), 
    
    LDC1("LDC1", MIPS.Format.FPU_RT_OFFSET_BASE, 53), 
    LWC1("LWC1", MIPS.Format.FPU_RT_OFFSET_BASE, 49), 
    SDC1("SDC1", MIPS.Format.FPU_RT_OFFSET_BASE, 61), 
    SWC1("SWC1", MIPS.Format.FPU_RT_OFFSET_BASE, 57), 
    
    ADDI("ADDI", MIPS.Format.RT_RS_IMMEDIATE, 8), 
    ADDIU("ADDIU", MIPS.Format.RT_RS_IMMEDIATE, 9), 
    SLTI("SLTI", MIPS.Format.RT_RS_IMMEDIATE, 10), 
    SLTIU("SLTIU", MIPS.Format.RT_RS_IMMEDIATE, 11), 
    ANDI("ANDI", MIPS.Format.RT_RS_IMMEDIATE, 12), 
    ORI("ORI", MIPS.Format.RT_RS_IMMEDIATE, 13), 
    XORI("XORI", MIPS.Format.RT_RS_IMMEDIATE, 14), 
    DADDI("DADDI", MIPS.Format.RT_RS_IMMEDIATE, 24), 
    DADDIU("DADDIU", MIPS.Format.RT_RS_IMMEDIATE, 25), 
    
    LUI("LUI", MIPS.Format.LUI_FMT, 15), 
    
    SLL("SLL", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 0), 
    SRL("SRL", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 2), 
    SRA("SRA", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 3), 
    DSLL("DSLL", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 56), 
    DSRL("DSRL", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 58), 
    DSRA("DSRA", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 59), 
    DSLL32("DSLL32", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 60), 
    DSRL32("DSRL32", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 62), 
    DSRA32("DSRA32", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_SA, 63), 
    
    SLLV("SLLV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 4), 
    SRLV("SRLV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 6), 
    SRAV("SRAV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 7), 
    DSLLV("DSLLV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 20), 
    DSRLV("DSRLV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 22), 
    DSRAV("DSRAV", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RT_RS, 23), 
    
    MULT("MULT", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 24), 
    MULTU("MULTU", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 25), 
    DIV("DIV", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 26), 
    DIVU("DIVU", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 27), 
    DMULT("DMULT", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 28), 
    DMULTU("DMULTU", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 29), 
    DDIV("DDIV", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 30), 
    DDIVU("DDIVU", MIPS.InstructionType.SPECIAL, MIPS.Format.RS_RT, 31), 
    
    MFHI("MFHI", MIPS.InstructionType.SPECIAL, MIPS.Format.RD, 16), 
    MFLO("MFLO", MIPS.InstructionType.SPECIAL, MIPS.Format.RD, 18), 
    
    MTHI("MTHI", MIPS.InstructionType.SPECIAL, MIPS.Format.RS, 17), 
    MTLO("MTLO", MIPS.InstructionType.SPECIAL, MIPS.Format.RS, 19), 
    
    ADD("ADD", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 32), 
    ADDU("ADDU", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 33), 
    SUB("SUB", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 34), 
    SUBU("SUBU", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 35), 
    DADD("DADD", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 44), 
    DADDU("DADDU", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 45), 
    DSUB("DSUB", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 46), 
    DSUBU("DSUBU", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 47), 
    AND("AND", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 36), 
    OR("OR", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 37), 
    XOR("XOR", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 38), 
    NOR("NOR", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 39), 
    SLT("SLT", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 42), 
    SLTU("SLTU", MIPS.InstructionType.SPECIAL, MIPS.Format.RD_RS_RT, 43), 
    
    BC1F("BC1F", MIPS.InstructionType.COP1, MIPS.Format.FPU_OFFSET, 0), 
    BC1T("BC1T", MIPS.InstructionType.COP1, MIPS.Format.FPU_OFFSET, 1), 
    BC1FL("BC1FL", MIPS.InstructionType.COP1, MIPS.Format.FPU_OFFSET, 2), 
    BC1TL("BC1TL", MIPS.InstructionType.COP1, MIPS.Format.FPU_OFFSET, 3), 
    
    FADD("ADD.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS_FT, 0), 
    FSUB("SUB.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS_FT, 1), 
    FMUL("MUL.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS_FT, 2), 
    FDIV("DIV.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS_FT, 3), 
    
    SQRT("SQRT.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 4), 
    ABS("ABS.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 5), 
    MOV("MOV.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 6), 
    NEG("NEG.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 7), 
    ROUNDL("ROUND.L.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 8), 
    TRUNCL("TRUNC.L.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 9), 
    CEILL("CEIL.L.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 10), 
    FLOORL("FLOOR.L.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 11), 
    ROUNDW("ROUND.W.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 12), 
    TRUNCW("TRUNC.W.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 13), 
    CEILW("CEIL.W.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 14), 
    FLOORW("FLOOR.W.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 15), 
    CVTS("CVT.S.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 32), 
    CVTD("CVT.D.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 33), 
    CVTW("CVT.W.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 36), 
    CVTL("CVT.L.", MIPS.InstructionType.COP1, MIPS.Format.FD_FS, 37), 
    
    MFC1("MFC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 0), 
    DMFC1("DMFC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 1), 
    CFC1("CFC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 2), 
    MTC1("MTC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 4), 
    DMTC1("DMTC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 5), 
    CTC1("CTC1", MIPS.InstructionType.COP1, MIPS.Format.RT_FS, 6), 
    
    COND("C.", MIPS.InstructionType.COP1, MIPS.Format.COND_FMT, -1);
    
    private final String name;
    private final int id;
    private final MIPS.InstructionType type;
    private final MIPS.Format format;
    
    private Instruction(String name, MIPS.Format fmt, int id)
    {
      this(name, MIPS.InstructionType.NORMAL, fmt, id);
    }
    
    private Instruction(String name, MIPS.InstructionType type, MIPS.Format fmt, int id)
    {
      this.name = name;
      this.type = type;
      format = fmt;
      this.id = id;
    }
  }
  

  public static String[] disassemble(String[] lines)
  {
    String[] out = new String[lines.length];
    
    for (int i = 0; i < lines.length; i++) {
      out[i] = disassemble(lines[i]);
    }
    return out;
  }
  
  public static List<String> disassemble(List<String> lines)
  {
    List<String> out = new LinkedList();
    
    for (String s : lines) {
      out.add(disassemble(s));
    }
    return out;
  }
  
  public static String disassemble(String in)
  {
    String out = "";
    
    int v = 0;
    try
    {
      v = (int)Long.parseLong(in, 16);
    } catch (NumberFormatException e) {
      throw new DisassemblerException("Could not parse instruction " + in);
    }
    
    if (v == 0) {
      return "NOP";
    }
    int opcode = v >>> 26;
    
    switch (opcode) {
    case 0: 
      out = disassembleSpecial(in, v); break;
    case 1:  out = disassembleRegimm(in, v); break;
    case 16:  throw new UnsupportedOperationException("MMU disassembly is not supported. " + in);
    case 17:  out = disassembleCop1(in, v); break;
    case 18:  throw new UnsupportedOperationException("RCP disassembly is not supported. " + in);
    default: 
      out = disassembleNormal(in, v);
    }
    
    return out;
  }
  
  private static String disassembleNormal(String s, int v)
  {
    int opcode = v >>> 26;
    
    Instruction ins = OpcodeTable[opcode];
    if (ins == null) {
      throw new DisassemblerException("Unknown opcode " + opcode + " from instruction " + s);
    }
    String line = "";
    int rs = v >>> 21 & 0x1F;
    int rt = v >>> 16 & 0x1F;
    
    int jmpoffset = v & 0x3FFFFFF;
    short immediate = (short)v;
    
    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 1: 
      line = String.format("%-9s %s, %s, %X", new Object[] { name, cpuRegNames[rs], cpuRegNames[rt], Integer.valueOf(4 * immediate) });
      break;
    case 2: 
      line = String.format("%-9s %s, %X", new Object[] { name, cpuRegNames[rs], Integer.valueOf(4 * immediate) });
      break;
    case 3: 
      jmpoffset = (jmpoffset << 2) + Integer.MIN_VALUE;
      line = String.format("%-9s %X", new Object[] { name, Integer.valueOf(jmpoffset) });
      break;
    case 4: 
      line = String.format("%-9s %s, %s, %X", new Object[] { name, cpuRegNames[rt], cpuRegNames[rs], Short.valueOf(immediate) });
      break;
    case 5: 
      line = String.format("%-9s %s, %X", new Object[] { name, cpuRegNames[rt], Short.valueOf(immediate) });
      break;
    case 6: 
      line = String.format("%-9s %s, %X (%s)", new Object[] { name, cpuRegNames[rt], Short.valueOf(immediate), cpuRegNames[rs] });
      break;
    case 7: 
      line = String.format("%-9s %s, %X (%s)", new Object[] { name, fpuRegNames[rt], Short.valueOf(immediate), cpuRegNames[rs] });
      break;
    default: 
      throw new DisassemblerException("Disassembler error on instruction type: " + name);
    }
    
    return line;
  }
  
  private static String disassembleSpecial(String s, int v)
  {
    int opcode = v & 0x3F;
    
    Instruction ins = SpecialTable[opcode];
    if (ins == null) {
      throw new DisassemblerException("Unknown special function " + opcode + " from instruction " + s);
    }
    String line = "";
    int rs = v >>> 21 & 0x1F;
    int rt = v >>> 16 & 0x1F;
    int rd = v >>> 11 & 0x1F;
    int sa = v >>> 6 & 0x1F;
    
    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 8: 
      line = String.format("%-9s %s", new Object[] { name, cpuRegNames[rs] });
      break;
    case 9: 
      line = String.format("%-9s %s, %s", new Object[] { name, cpuRegNames[rs], cpuRegNames[rd] });
      break;
    case 10: 
      int offset = v >>> 6 & 0xFFFFF;
      line = String.format("%-9s %X", new Object[] { name, Integer.valueOf(offset) });
      break;
    case 11: 
      line = String.format("%-9s %s, %s", new Object[] { name, cpuRegNames[rs], cpuRegNames[rt] });
      break;
    case 12: 
      line = String.format("%-9s %s", new Object[] { name, cpuRegNames[rd] });
      break;
    case 13: 
      line = String.format("%-9s %s", new Object[] { name, cpuRegNames[rs] });
      break;
    case 14: 
      line = String.format("%-9s %s, %s, %s", new Object[] { name, cpuRegNames[rd], cpuRegNames[rs], cpuRegNames[rt] });
      break;
    case 15: 
      line = String.format("%-9s %s, %s, %X", new Object[] { name, cpuRegNames[rd], cpuRegNames[rt], Integer.valueOf(sa) });
      break;
    case 16: 
      line = String.format("%-9s %s, %s, %s", new Object[] { name, cpuRegNames[rd], cpuRegNames[rt], cpuRegNames[rs] });
      break;
    default: 
      throw new DisassemblerException("Disassembler error on instruction type: " + name);
    }
    
    return line;
  }
  
  private static String disassembleRegimm(String s, int v)
  {
    int opcode = v >> 16 & 0x1F;
    
    Instruction ins = RegimmTable[opcode];
    if (ins == null) {
      throw new DisassemblerException("Unknown branch type " + opcode + " from instruction " + s);
    }
    String line = "";
    int rs = v >>> 21 & 0x1F;
    short immediate = (short)v;
    
    if (format == Format.RS_OFFSET) {
      line = String.format("%-9s %s, %X", new Object[] { name, cpuRegNames[rs], Integer.valueOf(4 * immediate) });
    } else {
      throw new DisassemblerException("Disassembler error on instruction type: " + name);
    }
    return line;
  }
  






  private static String disassembleCop1(String s, int v)
  {
    int function = v & 0x3F;
    int fmt = v >> 21 & 0x1F;
    
    Instruction ins = null;
    switch (fmt)
    {
    case 16: 
    case 17: 
      if ((function & 0x30) == 48)
      {
        ins = Instruction.COND;
      }
      else
      {
        switch (function) {
        case 0: 
          ins = Instruction.FADD; break;
        case 1:  ins = Instruction.FSUB; break;
        case 2:  ins = Instruction.FMUL; break;
        case 3:  ins = Instruction.FDIV; break;
        case 4:  ins = Instruction.SQRT; break;
        case 5:  ins = Instruction.ABS; break;
        case 6:  ins = Instruction.MOV; break;
        case 7:  ins = Instruction.NEG; break;
        case 8:  ins = Instruction.ROUNDL; break;
        case 9:  ins = Instruction.TRUNCL; break;
        case 10:  ins = Instruction.CEILL; break;
        case 11:  ins = Instruction.FLOORL; break;
        case 12:  ins = Instruction.ROUNDW; break;
        case 13:  ins = Instruction.TRUNCW; break;
        case 14:  ins = Instruction.CEILW; break;
        case 15:  ins = Instruction.FLOORW; break;
        case 32:  ins = Instruction.CVTS; break;
        case 33:  ins = Instruction.CVTD; break;
        case 36:  ins = Instruction.CVTW; break;
        case 37:  ins = Instruction.CVTL;
        }
      }
      break;
    case 20: 
    case 21: 
      switch (function) {
      case 32: 
        ins = Instruction.CVTS; break;
      case 33:  ins = Instruction.CVTD;
      }
      break;
    case 8: 
      int bc = v >> 16 & 0x3;
      switch (bc) {
      case 0: 
        ins = Instruction.BC1F; break;
      case 1:  ins = Instruction.BC1T; break;
      case 2:  ins = Instruction.BC1FL; break;
      case 3:  ins = Instruction.BC1TL;
      }
      break;
    case 0: 
      ins = Instruction.MFC1; break;
    case 1:  ins = Instruction.DMFC1; break;
    case 2:  ins = Instruction.CFC1; break;
    case 4:  ins = Instruction.MTC1; break;
    case 5:  ins = Instruction.DMTC1; break;
    case 6:  ins = Instruction.CTC1; break;
    case 3: case 7: case 9: case 10: case 11: case 12: 
    case 13: case 14: case 15: case 18: case 19: default: 
      throw new DisassemblerException("Unknown FPU format " + s);
    }
    
    if (ins == null) {
      throw new DisassemblerException("Unknown FPU instruction " + s);
    }
    if (((ins == Instruction.CVTS) && (fmt == 16)) || ((ins == Instruction.CVTD) && (fmt == 17))) {
      throw new DisassemblerException("Illegal conversion, FPU instruction " + s);
    }
    String line = "";
    String insName = "";
    int ft = v >> 16 & 0x1F;
    int fs = v >> 11 & 0x1F;
    int fd = v >> 6 & 0x1F;
    short immediate = (short)v;
    

    char fmtc = '\000';
    switch (fmt) {
    case 16: 
      fmtc = 'S'; break;
    case 17:  fmtc = 'D'; break;
    case 20:  fmtc = 'W'; break;
    case 21:  fmtc = 'L';
    }
    
    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 17: 
      insName = name + fmtc;
      line = String.format("%-9s %s, %s, %s", new Object[] { insName, fpuRegNames[fd], fpuRegNames[fs], fpuRegNames[ft] });
      break;
    case 18: 
      insName = name + fmtc;
      line = String.format("%-9s %s, %s", new Object[] { insName, fpuRegNames[fd], fpuRegNames[fs] });
      break;
    case 19: 
      line = String.format("%-9s %s, %s", new Object[] { name, cpuRegNames[ft], fpuRegNames[fs] });
      break;
    case 20: 
      int cc = v >> 18 & 0x3;
      if (cc != 0) {
        line = String.format("%-9s %X, %X", new Object[] { name, Integer.valueOf(cc), Integer.valueOf(4 * immediate) });
      } else
        line = String.format("%-9s %X", new Object[] { name, Integer.valueOf(4 * immediate) });
      break;
    case 21: 
      int cond = v & 0xF;
      insName = name + fpuCondNames[cond] + "." + fmtc;
      int cc = v >> 8 & 0x3;
      if (cc != 0) {
        line = String.format("%-9s %X, %s, %s", new Object[] { insName, Integer.valueOf(cc), fpuRegNames[fs], fpuRegNames[ft] });
      } else
        line = String.format("%-9s %s, %s", new Object[] { insName, fpuRegNames[fs], fpuRegNames[ft] });
      break;
    default: 
      throw new DisassemblerException("Disassembler error on instruction type: " + name);
    }
    
    return line;
  }
  
  public static String[] assemble(String[] lines) throws AssemblerException
  {
    HashMap<String, Integer> labelMap = new HashMap();
    
    int currentOffset = 0;
    for (int i = 0; i < lines.length; i++)
    {
      String line = cleanLine(lines[i]);
      if (line.startsWith(".")) {
        labelMap.put(line, Integer.valueOf(currentOffset));
      } else {
        currentOffset += 4;
      }
    }
    String[] out = new String[lines.length - labelMap.size()];
    
    int currentOutLine = 0;
    for (int i = 0; i < lines.length; i++)
    {
      String line = cleanLine(lines[i]);
      if (!line.startsWith("."))
      {

        line = substituteLabel(line, labelMap, 4 * currentOutLine);
        
        out[currentOutLine] = assemble(line);
        currentOutLine++;
      }
    }
    return out;
  }
  
  public static List<String> assemble(List<String> lines) throws AssemblerException
  {
    HashMap<String, Integer> labelMap = new HashMap();
    
    int currentOffset = 0;
    for (int i = 0; i < lines.size(); i++)
    {
      String line = cleanLine((String)lines.get(i));
      if (line.startsWith(".")) {
        labelMap.put(line, Integer.valueOf(currentOffset));
      } else
        currentOffset += 4;
    }
    List<String> out = new LinkedList();
    







    currentOffset = 0;
    for (int i = 0; i < lines.size(); i++)
    {
      String line = cleanLine((String)lines.get(i));
      if (!line.startsWith("."))
      {

        line = substituteLabel(line, labelMap, currentOffset);
        
        out.add(assemble(line));
        currentOffset += 4;
      }
    }
    return out;
  }
  



  private static final int REGIMM_OPCODE = 1;
  


  private static final int COP0_OPCODE = 16;
  


  private static final int COP1_OPCODE = 17;
  


  private static final int COP2_OPCODE = 18;
  


  private static final int FMT_S = 16;
  


  private static final int FMT_D = 17;
  

  private static final int FMT_W = 20;
  

  private static final int FMT_L = 21;
  

  private static final int FMT_BC1 = 8;
  

  private static String substituteLabel(String line, HashMap<String, Integer> labelMap, int currentOffset)
  {
    if ((line.contains(".")) && (line.startsWith("B")))
    {
      String[] tokens = line.split(" ");
      StringBuilder newLineBuilder = new StringBuilder();
      for (String token : tokens)
      {
        if (token.startsWith(".")) {
          newLineBuilder.append(String.format("%X", new Object[] { Integer.valueOf(((Integer)labelMap.get(token)).intValue() - currentOffset - 4) }));
        } else
          newLineBuilder.append(token).append(" ");
      }
      line = newLineBuilder.toString().trim();
    }
    
    return line;
  }
  
  private static String cleanLine(String in)
  {
    String s = in.trim().toUpperCase().replaceAll("\t", " ");
    return s.replaceAll("[)$]", "").replaceAll("[(]", " ");
  }
  
  public static String assemble(String in) throws AssemblerException
  {
    return assemble(in, true);
  }
  
  private static String assemble(String in, boolean shouldCleanLine) throws AssemblerException
  {
    String s = shouldCleanLine ? cleanLine(in) : in;
    
    if (s.equals("NOP")) {
      return "00000000";
    }
    String insName = "";
    if (s.startsWith("C."))
    {
      insName = "C.";
      s = s.substring(2).replaceAll("\\.", ", ");
    }
    else if (s.lastIndexOf('.') > 0)
    {
      insName = s.substring(0, s.lastIndexOf('.') + 1);
      s = s.substring(s.lastIndexOf('.') + 1).trim();
    }
    else
    {
      insName = s.substring(0, s.indexOf(' '));
      s = s.substring(s.indexOf(' ')).trim();
    }
    
    String out = "";
    String[] tokens = s.split(",* +");
    
    Instruction ins = (Instruction)InstructionMap.get(insName);
    
    if (ins != null)
    {
      if (tokens.length != access$300argc) {
        throw new AssemblerException("Incorrect format for instruction:\n\"" + insName + "\" from line \"" + in + "\"");
      }
      
      int v = 0;
      try
      {
        switch (1.$SwitchMap$asm$MIPS$InstructionType[type.ordinal()]) {
        case 1: 
          v = assembleNormal(ins, tokens); break;
        case 2:  v = assembleSpecial(ins, tokens); break;
        case 3:  v = assembleRegimm(ins, tokens); break;
        case 4:  throw new UnsupportedOperationException("COP0 not supported: " + in);
        case 5:  v = assembleCop1(ins, tokens); break;
        case 6:  throw new UnsupportedOperationException("COP2 not supported: " + in);
        }
      } catch (NumberFormatException e) {
        throw new AssemblerException("Invalid instruction caused NumberFormatException:\n\"" + insName + "\" from line \"" + in + "\"");
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new AssemblerException("Invalid instruction caused ArrayIndexOutOfBoundsException:\n\"" + insName + "\" from line \"" + in + "\"");
      }
      

      out = String.format("%08X", new Object[] { Integer.valueOf(v) });
    }
    else {
      throw new AssemblerException("Unrecognized opcode for instruction:\n\"" + insName + "\" from line \"" + in + "\"");
    }
    
    return out;
  }
  
  private static int assembleNormal(Instruction ins, String[] tokens)
  {
    int v = 0;
    


    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 1: 
      int rs = getCpuRegID(tokens[0]);
      int rt = getCpuRegID(tokens[1]);
      int immediate = (short)(int)Long.parseLong(tokens[2], 16) / 4 & 0xFFFF;
      v = id << 26 | rs << 21 | rt << 16 | immediate;
      break;
    case 2: 
      int rs = getCpuRegID(tokens[0]);
      int immediate = (short)(int)Long.parseLong(tokens[1], 16) / 4 & 0xFFFF;
      v = id << 26 | rs << 21 | immediate;
      break;
    case 3: 
      int jmpoffset = (int)Long.parseLong(tokens[0], 16);
      jmpoffset = jmpoffset - Integer.MIN_VALUE >> 2;
      v = id << 26 | jmpoffset;
      break;
    case 4: 
      int rt = getCpuRegID(tokens[0]);
      int rs = getCpuRegID(tokens[1]);
      
      int immediate = DataUtils.parseIntString(tokens[2]) & 0xFFFF;
      v = id << 26 | rs << 21 | rt << 16 | immediate;
      break;
    case 5: 
      int rt = getCpuRegID(tokens[0]);
      int immediate = (int)Long.parseLong(tokens[1], 16) & 0xFFFF;
      v = id << 26 | rt << 16 | immediate;
      break;
    case 6: 
      int rs = getCpuRegID(tokens[2]);
      int rt = getCpuRegID(tokens[0]);
      int immediate = (int)Long.parseLong(tokens[1], 16) & 0xFFFF;
      v = id << 26 | rs << 21 | rt << 16 | immediate;
      break;
    case 7: 
      int rs = getCpuRegID(tokens[2]);
      int rt = getFpuRegID(tokens[0]);
      int immediate = (int)Long.parseLong(tokens[1], 16) & 0xFFFF;
      v = id << 26 | rs << 21 | rt << 16 | immediate;
      break;
    default: 
      throw new AssemblerException("Assembler error on instruction type: " + name);
    }
    
    return v;
  }
  
  private static int assembleSpecial(Instruction ins, String[] tokens)
  {
    int v = 0;
    

    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 15: 
      int rd = getCpuRegID(tokens[0]);
      int rt = getCpuRegID(tokens[1]);
      int sa = Integer.parseInt(tokens[2], 16);
      v = 0x0 | rt << 16 | rd << 11 | sa << 6 | id;
      break;
    case 16: 
      int rd = getCpuRegID(tokens[0]);
      int rt = getCpuRegID(tokens[1]);
      int rs = getCpuRegID(tokens[2]);
      v = 0x0 | rs << 21 | rt << 16 | rd << 11 | id;
      break;
    case 8: 
      int rs = getCpuRegID(tokens[0]);
      v = rs << 21 | id;
      break;
    case 9: 
      int rs = getCpuRegID(tokens[0]);
      int rd = getCpuRegID(tokens[1]);
      v = rs << 21 | rd << 11 | id;
      break;
    case 10: 
      int offset = (int)Long.parseLong(tokens[0], 16) & 0xFFFFF;
      v = offset << 6 | id;
      break;
    case 11: 
      int rs = getCpuRegID(tokens[0]);
      int rt = getCpuRegID(tokens[1]);
      v = 0x0 | rs << 21 | rt << 16 | id;
      break;
    case 12: 
      int rd = getCpuRegID(tokens[0]);
      v = 0x0 | rd << 11 | id;
      break;
    case 13: 
      int rs = getCpuRegID(tokens[0]);
      v = 0x0 | rs << 21 | id;
      break;
    case 14: 
      int rd = getCpuRegID(tokens[0]);
      int rs = getCpuRegID(tokens[1]);
      int rt = getCpuRegID(tokens[2]);
      v = 0x0 | rs << 21 | rt << 16 | rd << 11 | id;
      break;
    default: 
      throw new AssemblerException("Assembler error on instruction type: " + name);
    }
    
    return v;
  }
  
  private static int assembleRegimm(Instruction ins, String[] tokens)
  {
    int v = 0;
    

    switch (format)
    {
    case RS_OFFSET: 
      int rs = getCpuRegID(tokens[0]);
      int immediate = (int)Long.parseLong(tokens[1], 16) / 4 & 0xFFFF;
      v = 0x4000000 | rs << 21 | id << 16 | immediate;
      break;
    default: 
      throw new AssemblerException("Assembler error on instruction type: " + name); }
    int immediate;
    int rs;
    return v;
  }
  
  private static int assembleCop1(Instruction ins, String[] tokens)
  {
    int v = 0;
    int cc = 0;
    

    switch (1.$SwitchMap$asm$MIPS$Format[format.ordinal()])
    {
    case 17: 
      int fmt = getFormat(tokens[0]);
      int fd = getFpuRegID(tokens[1]);
      int fs = getFpuRegID(tokens[2]);
      int ft = getFpuRegID(tokens[3]);
      v = 0x44000000 | fmt << 21 | ft << 16 | fs << 11 | fd << 6 | id;
      break;
    case 18: 
      int fmt = getFormat(tokens[0]);
      int fd = getFpuRegID(tokens[1]);
      int fs = getFpuRegID(tokens[2]);
      v = 0x44000000 | fmt << 21 | fs << 11 | fd << 6 | id;
      break;
    case 19: 
      int rt = getCpuRegID(tokens[0]);
      int fs = getFpuRegID(tokens[1]);
      v = 0x44000000 | id << 21 | rt << 16 | fs << 11;
      break; case 20:  int immediate;
      int immediate;
      if (tokens.length == 2)
      {
        cc = Integer.parseInt(tokens[0]) & 0x7;
        immediate = (int)Long.parseLong(tokens[1], 16) / 4 & 0xFFFF;
      } else {
        immediate = (int)Long.parseLong(tokens[0], 16) / 4 & 0xFFFF;
      }
      v = 0x45000000 | cc << 18 | id << 16 | immediate;
      break;
    case 21: 
      int cond = getFpuCond(tokens[0]);
      int fmt = getFormat(tokens[1]);
      int ft; int fs; int ft; if (tokens.length == 5)
      {
        cc = Integer.parseInt(tokens[2]) & 0x7;
        int fs = getFpuRegID(tokens[3]);
        ft = getFpuRegID(tokens[4]);
      } else {
        fs = getFpuRegID(tokens[2]);
        ft = getFpuRegID(tokens[3]);
      }
      v = 0x44000000 | fmt << 21 | ft << 16 | fs << 11 | cc << 8 | 0x30 | cond;
      break;
    default: 
      throw new AssemblerException("Assembler error on instruction type: " + name);
    }
    
    return v;
  }
  
  private static int getCpuRegID(String name)
  {
    if (cpuRegMap.containsKey(name)) {
      return ((Integer)cpuRegMap.get(name)).intValue();
    }
    throw new AssemblerException("No such CPU register: " + name);
  }
  
  private static int getFpuRegID(String name)
  {
    if (fpuRegMap.containsKey(name)) {
      return ((Integer)fpuRegMap.get(name)).intValue();
    }
    throw new AssemblerException("No such COP1 register: " + name);
  }
  
  private static int getFpuCond(String name)
  {
    if (fpuCondMap.containsKey(name)) {
      return ((Integer)fpuCondMap.get(name)).intValue();
    }
    throw new AssemblerException("No such FPU comparison: " + name);
  }
  
  private static int getFormat(String fmts)
  {
    switch (fmts) {
    case "S": 
      return 16;
    case "D":  return 17;
    case "W":  return 20;
    case "L":  return 21;
    }
    throw new AssemblerException("Invalid FPU format " + fmts);
  }
  


  public static int getJumpIns(long dest)
  {
    long opcode = 134217728L;
    long addr = (dest & 0xFFFFFFF) >>> 2;
    return (int)(opcode | addr);
  }
  
  public static int getJumpAddress(int insAddress, int v)
  {
    int jumpAddr = -1;
    
    switch (v >>> 26)
    {
    case 2: 
      jumpAddr = Integer.MIN_VALUE + ((v & 0x3FFFFFF) << 2);
      break;
    case 1: 
      switch (v >>> 16 & 0x1F)
      {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
        jumpAddr = insAddress + 4 * (short)v + 4;
      }
      
      break;
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
      jumpAddr = insAddress + 4 * (short)v + 4;
      break;
    
    case 17: 
      if ((v >>> 21 & 0x1F) == 8)
      {
        jumpAddr = insAddress + 4 * (short)v + 4;
      }
      break;
    }
    return jumpAddr;
  }
  
  public MIPS() {}
}

package data.requests;

import asm.MIPS;
import asm.pseudoinstruction.PseudoInstruction;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import main.DevContext;
import main.Directories;

public class SpecialRequestDumper
{
  public SpecialRequestDumper() {}
  
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    dumpRequestedFunctions();
    dumpRequestedScripts();
    System.exit(0);
  }
  
  public static void dumpRequestedScripts() throws IOException
  {
    File f = new File(Directories.DATABASE + "request_script_dumps.txt");
    List<String> lines = util.IOUtils.readTextFile(f, false);
    
    ByteBuffer romBuffer = DevContext.getPristineRomBuffer();
    
    for (String line : lines)
    {
      String[] tokens = line.split("\\s*:\\s*");
      
      if (tokens.length != 3) {
        throw new RuntimeException(String.format("Invalid line in %s: %s", new Object[] { f.getName(), line }));
      }
      String name = tokens[2].equals("?") ? tokens[0] : tokens[2];
      PrintWriter pw = new PrintWriter(new File(Directories.DUMP_REQUESTS + "script_" + name + ".txt"));
      
      int offset = (int)Long.parseLong(tokens[0], 16);
      if (tokens[1].equals("battle"))
      {
        IsolatedBattleScriptDumper dumper = new IsolatedBattleScriptDumper(romBuffer, pw);
        dumper.printScript(romBuffer, offset, name);

      }
      else if (tokens[1].equals("map"))
      {
        IsolatedMapScriptDumper dumper = new IsolatedMapScriptDumper(romBuffer, pw);
        dumper.printScript(romBuffer, offset, name);
      }
      else
      {
        pw.close();
        throw new RuntimeException(String.format("Invalid line in %s: %s", new Object[] { f.getName(), line }));
      }
      
      pw.close();
    }
    

    PrintWriter pw = new PrintWriter(System.out);
    
    IsolatedBattleScriptDumper dumper = new IsolatedBattleScriptDumper(romBuffer, pw);
  }
  
  public static void dumpRequestedFunctions() throws IOException
  {
    File f = new File(Directories.DATABASE + "request_func_dumps.txt");
    List<String> lines = util.IOUtils.readTextFile(f, false);
    
    for (String line : lines)
    {
      String[] tokens = line.split("\\s*:\\s*");
      
      if (tokens.length != 3) {
        throw new RuntimeException(String.format("Invalid line in %s: %s", new Object[] { f.getName(), line }));
      }
      String name = "offset_" + tokens[0];
      PrintWriter pw = new PrintWriter(new File(Directories.DUMP_REQUESTS + "func_" + name + ".txt"));
      
      int offset = (int)Long.parseLong(tokens[0], 16);
      if (tokens[1].isEmpty())
      {
        printFunction(pw, offset);
      }
      else
      {
        int addr = (int)Long.parseLong(tokens[1], 16);
        printFunction(pw, offset, addr);
      }
      
      pw.close();
    }
  }
  
  private static void printFunction(PrintWriter pw, int offset) throws IOException
  {
    ByteBuffer fileBuffer = DevContext.getPristineRomBuffer();
    fileBuffer.position(offset);
    
    List<String> insList = new LinkedList();
    


    do
    {
      v = fileBuffer.getInt();
      insList.add(String.format("%08X", new Object[] { Integer.valueOf(v) }));
    }
    while (v != 65011720);
    

    int v = fileBuffer.getInt();
    insList.add(String.format("%08X", new Object[] { Integer.valueOf(v) }));
    
    List<String> asmList = MIPS.disassemble(insList);
    asmList = PseudoInstruction.addAll(asmList);
    
    pw.printf("Function at %08X\r\n", new Object[] { Integer.valueOf(offset) });
    pw.println();
    
    for (String s : asmList) {
      pw.println(s);
    }
  }
  
  private static void printFunction(PrintWriter pw, int offset, int address) throws IOException {
    ByteBuffer fileBuffer = DevContext.getPristineRomBuffer();
    fileBuffer.position(offset);
    
    List<String> insList = new LinkedList();
    


    do
    {
      v = fileBuffer.getInt();
      insList.add(String.format("%08X", new Object[] { Integer.valueOf(v) }));
    }
    while (v != 65011720);
    

    int v = fileBuffer.getInt();
    insList.add(String.format("%08X", new Object[] { Integer.valueOf(v) }));
    
    List<String> asmList = MIPS.disassemble(insList);
    asmList = PseudoInstruction.addAll(asmList);
    
    int insOffset = 0;
    
    pw.printf("Function at %08X (%08X)\r\n", new Object[] { Integer.valueOf(address), Integer.valueOf(offset) });
    pw.println();
    
    for (int i = 0; i < asmList.size(); i++)
    {
      String ins = (String)asmList.get(i);
      String[] tokens = ins.split("\\s+");
      

      if (tokens[0].startsWith("B"))
      {
        switch (tokens[0])
        {
        case "BLTZ": 
        case "BLTZL": 
        case "BLTZAL": 
        case "BLTZALL": 
        case "BGEZ": 
        case "BGEZL": 
        case "BGEZAL": 
        case "BGEZALL": 
        case "BEQ": 
        case "BEQL": 
        case "BNE": 
        case "BNEL": 
        case "BLEZ": 
        case "BLEZL": 
        case "BGTZ": 
        case "BGTZL": 
        case "BC1F": 
        case "BC1FL": 
        case "BC1T": 
        case "BC1TL": 
          int lastSpaceIndex = ins.lastIndexOf(' ') + 1;
          int breakOffset = (int)Long.parseLong(ins.substring(lastSpaceIndex), 16);
          ins = String.format("%s.o%X", new Object[] { ins.substring(0, lastSpaceIndex), Integer.valueOf(insOffset + breakOffset + 4) });
        }
        
      }
      pw.printf("%5X:  %s\r\n", new Object[] { Integer.valueOf(insOffset), ins });
      

      if (PseudoInstruction.isValidOpcode(tokens[0]))
      {
        if (i + 2 < asmList.size())
        {
          String k = (String)asmList.get(i + 2);
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
}

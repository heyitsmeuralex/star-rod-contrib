package patcher;

import asm.AssemblerException;
import asm.MIPS;
import asm.pseudoinstruction.PseudoInstruction;
import data.shared.DataConstants;
import data.shared.DataUtils;
import data.shared.lib.FunctionLibrary;
import data.shared.lib.NamedFunction;
import data.shared.struct.function.Function;
import data.shared.struct.script.Script;
import data.shared.struct.script.ScriptVariable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Directories;
import main.InputFileException;
import util.DualHashMap;
import util.IOUtils;
import util.Logger;














public class DirectRomPatcher
{
  private final List<DirectPatch> patchList;
  private final List<DirectPatch> declaredList;
  private final SymbolDatabase symbolDatabase;
  private final HashMap<String, Integer> globalPointerMap;
  private static final Pattern WhitespacePattern = Pattern.compile("\\s+");
  private static final Pattern LineOffsetPattern = Pattern.compile("^\\s*[0-9A-Fa-f]+:\\s*");
  private static final Pattern FunctionIgnorePattern = Pattern.compile("[(),]");
  private static final Pattern ScriptIgnorePattern = Pattern.compile("[()]");
  
  public DirectRomPatcher(HashMap<String, Integer> globalPointerMap, SymbolDatabase symbolDatabase)
  {
    patchList = new LinkedList();
    declaredList = new LinkedList();
    this.globalPointerMap = globalPointerMap;
    this.symbolDatabase = symbolDatabase;
  }
  




















  private static enum PatchType
  {
    DATA("Data"), 
    FUNC("Function"), 
    HOOK("Hook"), 
    SCRIPT("Script");
    
    public final String name;
    
    private PatchType(String name)
    {
      this.name = name;
    }
    

    public String toString()
    {
      return name;
    }
  }
  
  private static enum ScriptEnv {
    GLOBAL,  MAP,  BATTLE;
    

    private ScriptEnv() {}
  }
  
  private static final HashMap<String, PatchType> typeMap = new HashMap();
  static { for (PatchType t : PatchType.values()) {
      typeMap.put(t.toString(), t);
    }
  }
  
  private class DirectPatch {
    public final File source;
    public final List<String> lines = new LinkedList();
    
    public final DirectRomPatcher.PatchType type;
    
    public final DirectRomPatcher.ScriptEnv scriptEnv;
    public int fileOffset;
    public String name;
    public final boolean isNew;
    
    public DirectPatch(File source, int lineNum, String line)
    {
      this.source = source;
      String s;
      if (line.startsWith("@"))
      {
        isNew = false;
        s = line.substring(1).trim();
        String[] tokens = s.split(":|\\s+");
        if (tokens.length < 2) {
          throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
        }
        type = ((DirectRomPatcher.PatchType)DirectRomPatcher.typeMap.get(tokens[0]));
        if (type == null) {
          throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
        }
        if (type == DirectRomPatcher.PatchType.SCRIPT)
        {
          if (tokens.length != 3) {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          if (tokens[1].equals("Global")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.GLOBAL;
          } else if (tokens[1].equals("Map")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.MAP;
          } else if (tokens[1].equals("Battle")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.BATTLE;
          } else {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          fileOffset = ((int)Long.parseLong(tokens[2], 16));
        }
        else
        {
          scriptEnv = DirectRomPatcher.ScriptEnv.GLOBAL;
          if (tokens.length != 2) {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          fileOffset = ((int)Long.parseLong(tokens[1], 16));
        }
      }
      else if (line.startsWith("#new:"))
      {
        isNew = true;
        String s = line.substring(5).trim();
        String[] tokens = s.split(":|\\s+");
        if (tokens.length < 2) {
          throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
        }
        type = ((DirectRomPatcher.PatchType)DirectRomPatcher.typeMap.get(tokens[0]));
        if (type == null) {
          throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
        }
        if (type == DirectRomPatcher.PatchType.SCRIPT)
        {
          if (tokens.length != 3) {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          if (tokens[1].equals("Global")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.GLOBAL;
          } else if (tokens[1].equals("Map")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.MAP;
          } else if (tokens[1].equals("Battle")) {
            scriptEnv = DirectRomPatcher.ScriptEnv.BATTLE;
          } else {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          name = tokens[2];
          if (!name.startsWith("$")) {
            throw new InputFileException(source, lineNum, "Declared name must begin with $:\n" + line);
          }
        }
        else {
          scriptEnv = DirectRomPatcher.ScriptEnv.GLOBAL;
          if (tokens.length != 2) {
            throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
          }
          name = tokens[1];
          if (!name.startsWith("$")) {
            throw new InputFileException(source, lineNum, "Declared name must begin with $:\n" + line);
          }
        }
      } else {
        throw new InputFileException(source, lineNum, "Cannot parse direct patch type:\n" + line);
      }
    }
  }
  
  public void readROMPatches() throws IOException {
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_PATCH, "patch", true);
    for (File f : patchFiles)
    {
      Logger.log("Reading patch file: " + f.getName());
      readPatchFile(f);
    }
  }
  
  public void writeGlobalStructs(RandomAccessFile raf) throws IOException
  {
    int currentStartOffset = Patcher.nextAlignedOffset(raf);
    
    for (DirectPatch patch : declaredList)
    {
      Logger.logf("Parsing global struct: %s", new Object[] { name });
      
      if (globalPointerMap.containsKey(name)) {
        throw new InputFileException(source, "Global function is declared more than once: " + name);
      }
      fileOffset = currentStartOffset;
      globalPointerMap.put(name, Integer.valueOf(Patcher.toAddress(currentStartOffset)));
      
      currentStartOffset += getPatchLength(patch);
    }
    
    for (DirectPatch patch : declaredList)
    {
      Logger.logf("Writing global struct %s to %08X", new Object[] { name, Integer.valueOf(Patcher.toAddress(fileOffset)) });
      writePatch(patch, raf);
    }
  }
  
  public void writePatches(RandomAccessFile raf) throws IOException
  {
    for (DirectPatch rec : patchList)
    {
      writePatch(rec, raf);
    }
  }
  
  private void readPatchFile(File f) throws IOException
  {
    List<String> lines = new ArrayList();
    

    BufferedReader in = new BufferedReader(new FileReader(f));
    String templine; while ((templine = in.readLine()) != null)
      lines.add(templine);
    in.close();
    
    boolean readingCommentBlock = false;
    boolean readingPatch = false;
    
    DirectPatch currentPatch = null;
    
    for (int lineNum = 1; lineNum <= lines.size(); lineNum++)
    {
      String line = (String)lines.get(lineNum - 1);
      boolean isCommentLine = false;
      

      if (currentPatch != null)
      {
        if ((type == PatchType.FUNC) || (type == PatchType.HOOK))
        {
          line = LineOffsetPattern.matcher(line).replaceAll("");
          line = FunctionIgnorePattern.matcher(line).replaceAll(" ");
        }
        else if (type == PatchType.SCRIPT)
        {
          line = LineOffsetPattern.matcher(line).replaceAll("");
          line = ScriptIgnorePattern.matcher(line).replaceAll(" ");
        }
      }
      

      line = WhitespacePattern.matcher(line).replaceAll(" ").trim();
      
      if (readingCommentBlock)
      {
        int endBlockPos = line.indexOf("%/");
        
        if (endBlockPos >= 0)
        {
          line = line.substring(endBlockPos + 2).trim();
          readingCommentBlock = false;
          isCommentLine = true;
        }
      }
      
      Matcher m = DataConstants.CommentPattern.matcher(line);
      if (m.find())
      {
        line = m.replaceAll("").trim();
        isCommentLine = true;
      }
      
      if (!readingCommentBlock)
      {
        int startBlockPos = line.indexOf("/%");
        if (startBlockPos >= 0)
        {
          line = line.substring(0, startBlockPos).trim();
          readingCommentBlock = true;
          isCommentLine = true;
        }
      }
      
      if (!readingCommentBlock)
      {

        if (readingPatch)
        {
          if (line.isEmpty())
          {

            if (isCommentLine)
              continue;
            readingPatch = false;
            currentPatch = null; continue;
          }
          


          if ((!line.startsWith("@")) && (!line.startsWith("#")))
          {
            lines.add(line);
            continue;
          }
        }
        

        if (!line.isEmpty())
        {


          if (line.startsWith("@"))
          {
            currentPatch = new DirectPatch(f, lineNum, line);
            patchList.add(currentPatch);
            readingPatch = true;
          } else if (line.startsWith("#new:")) {
            currentPatch = new DirectPatch(f, lineNum, line);
            declaredList.add(currentPatch);
            readingPatch = true;
          }
          else {
            throw new InputFileException(f, lineNum, "Could not parse line: %s", new Object[] { line });
          } }
      }
    }
  }
  
  private int getPatchLength(DirectPatch patch) { List<String[]> tokens = new ArrayList(lines.size());
    for (Iterator localIterator = lines.iterator(); localIterator.hasNext();) { s = (String)localIterator.next();
      tokens.add(s.split("\\s+")); }
    String s;
    int length = 0;
    switch (1.$SwitchMap$patcher$DirectRomPatcher$PatchType[type.ordinal()])
    {
    case 1: 
    case 2: 
      length += Function.getPatchLength(tokens);
      break;
    
    case 3: 
    case 4: 
      for (String[] line : tokens)
      {
        for (String s : line)
        {
          char prefix = s.charAt(0);
          if ((prefix == '$') || (prefix == '*')) {
            length += 4;
          } else {
            length += DataUtils.getSize(s);
          }
        }
      }
    }
    
    length = length + 3 & 0xFFFFFFFC;
    return length;
  }
  
  private void writePatch(DirectPatch patch, RandomAccessFile raf) throws IOException
  {
    List<String[]> tokens = new LinkedList();
    String[] line; String[] newLine; for (int i = 0; i < lines.size(); i++)
    {
      List<String> replaced = new LinkedList();
      line = ((String)lines.get(i)).split("\\s+");
      
      for (String s : line)
      {
        char prefix = s.charAt(0);
        
        if (prefix == '$')
        {
          if (!globalPointerMap.containsKey(s)) {
            throw new InputFileException(source, "Patch references unknown pointer " + s);
          }
          replaced.add(String.format("%08X", new Object[] { globalPointerMap.get(s) }));
        }
        else if (prefix == '.') {
          replaced.add(replaceConstant(patch, s));
        } else if (prefix == '*') {
          replaced.add(ScriptVariable.parseScriptVariable(s));
        } else if (prefix == '{') {
          replaced.addAll(replaceExpression(patch, s));
        } else {
          replaced.add(s);
        }
      }
      newLine = new String[replaced.size()];
      tokens.add(replaced.toArray(newLine)); }
    String[] line;
    int hookOffset;
    int ins; int ins; switch (1.$SwitchMap$patcher$DirectRomPatcher$PatchType[type.ordinal()])
    {
    case 3: 
      raf.seek(fileOffset);
      
      for (i = tokens.iterator(); i.hasNext();) { line = (String[])i.next();
        for (String s : line)
        {
          DataUtils.writeWord(raf, s); }
      }
      break;
    

    case 2: 
      hookOffset = Patcher.nextAlignedOffset(raf);
      

      raf.seek(fileOffset);
      raf.writeInt(getJumpIns(Patcher.toAddress(hookOffset)));
      raf.writeInt(0);
      

      raf.seek(hookOffset);
      
      for (String instruction : assembleTokens(patch, tokens))
      {
        ins = (int)Long.parseLong(instruction, 16);
        raf.writeInt(ins);
      }
      
      break;
    

    case 1: 
      raf.seek(fileOffset);
      
      for (String instruction : assembleTokens(patch, tokens))
      {
        ins = (int)Long.parseLong(instruction, 16);
        raf.writeInt(ins);
      }
      
      break;
    

    case 4: 
      switch (1.$SwitchMap$patcher$DirectRomPatcher$ScriptEnv[scriptEnv.ordinal()])
      {
      case 1: 
        Script.replaceKeywords(tokens, DataConstants.battleFunctionLib, DataConstants.battleScriptLib);
        break;
      case 2: 
        Script.replaceKeywords(tokens, DataConstants.sharedFunctionLib, DataConstants.sharedScriptLib);
        break;
      case 3: 
        Script.replaceKeywords(tokens, DataConstants.mapFunctionLib, DataConstants.mapScriptLib);
      }
      
      
      raf.seek(fileOffset);
      
      for (String[] line : tokens) {
        for (String s : line)
        {
          raf.writeInt((int)Long.parseLong(s, 16));
        }
      }
    }
    
  }
  
  private final String replaceConstant(DirectPatch patch, String s)
  {
    String constName = s.substring(1);
    
    boolean isFunction = (type == PatchType.FUNC) || (type == PatchType.HOOK);
    String sub = DataConstants.resolve(constName, !isFunction);
    
    return sub == null ? s : sub;
  }
  
  private final List<String> replaceExpression(DirectPatch patch, String s)
  {
    int len = s.length();
    if ((len < 3) || (s.charAt(len - 1) != '}')) {
      throw new InputFileException(source, "Invalid expression: " + s);
    }
    String exp = s.substring(1, len - 1);
    
    List<String> newTokens = new LinkedList();
    String[] tokens = exp.split(":");
    
    switch (tokens[0])
    {
    case "String": 
      if (symbolDatabase.stringNameExists(tokens[1])) {
        newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(symbolDatabase.getStringFromName(tokens[1])) }));
      } else {
        throw new InputFileException(source, "No such string: " + tokens[1]);
      }
      break;
    case "Index": 
      if ((tokens.length != 2) || (tokens[1] == null) || (tokens[1].charAt(0) != '*')) {
        throw new InputFileException(source, "Invalid index expression: " + s);
      }
      int index = ScriptVariable.getScriptVariableIndex(tokens[1]);
      
      if (index > 0) {
        newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(index) }));
      } else {
        throw new InputFileException(source, "No such string: " + tokens[1]);
      }
      break;
    case "Func": 
      NamedFunction func = DataConstants.sharedFunctionLib.get(tokens[1]);
      if (func != null) {
        newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(func.getAddress()) }));
      } else {
        throw new InputFileException(source, "Function is not defined: " + tokens[1]);
      }
      break;
    case "FX": 
      String effectName = exp.substring(3);
      if (!DataConstants.EffectType.containsInverse(effectName)) {
        throw new InputFileException(source, "No definition for effect: " + effectName);
      }
      int effect = ((Integer)DataConstants.EffectType.getInverse(effectName)).intValue();
      int type = effect >> 16 & 0xFFFF;
      int subtype = effect & 0xFFFF;
      
      newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(type) }));
      if (subtype != 65535) {
        newTokens.add(String.format("%08X", new Object[] { Integer.valueOf(subtype) }));
      }
      break;
    }
    if (newTokens.isEmpty()) {
      throw new InputFileException(source, "Unknown expression: " + s);
    }
    return newTokens;
  }
  
  private static List<String> assembleTokens(DirectPatch patch, List<String[]> tokenized)
  {
    List<String> instructions = new LinkedList();
    
    for (String[] line : tokenized)
    {
      StringBuilder sb = new StringBuilder();
      for (String t : line)
        sb.append(t + " ");
      instructions.add(sb.toString());
    }
    try
    {
      instructions = PseudoInstruction.removeAll(instructions);
      return MIPS.assemble(instructions);
    } catch (AssemblerException e) {
      if (isNew)
      {
        throw new InputFileException(source, name + "\n" + e.getMessage());
      }
      
      throw new InputFileException(source, String.format("Function %X\n", new Object[] {Integer.valueOf(fileOffset) }) + e.getMessage());
    }
  }
  
  private static int getJumpIns(long dest)
  {
    long opcode = 134217728L;
    long addr = (dest & 0xFFFFFFF) >>> 2;
    return (int)(opcode | addr);
  }
}

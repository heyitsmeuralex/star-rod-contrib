package data.shared.struct.script;

import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.PointerHeuristic;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.ScriptParsingException;
import data.shared.lib.ScriptLibrary;
import data.shared.struct.Struct;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import main.InputFileException;

public class Script
{
  private static final String IF = "If";
  private static final String CASE = "Case";
  private static final String EXEC = "Exec";
  private static int lineStart;
  private static int tabs;
  public Script() {}
  
  private static enum Format
  {
    Plain, 
    If, 
    Case, 
    CaseRange, 
    Set, 
    Call;
    


    private Format() {}
  }
  

  public static enum Command
  {
    END(1, 0, "End"), 
    RETURN(2, 0, "Return"), 
    LABEL(3, 1, "Label"), 
    GOTO(4, 1, "Goto"), 
    LOOP(5, 1, 0, 1, "Loop"), 
    END_LOOP(6, 0, -1, 0, "EndLoop"), 
    BREAK_LOOP(7, 0, "BreakLoop"), 
    WAIT_FRAMES(8, 1, "Wait"), 
    WAIT_SECS(9, 1, "WaitSeconds"), 
    
    IF_EQ(10, 2, 0, 1, "If", Script.Format.If, "=="), 
    IF_NEQ(11, 2, 0, 1, "If", Script.Format.If, "!="), 
    IF_LT(12, 2, 0, 1, "If", Script.Format.If, "<"), 
    IF_GT(13, 2, 0, 1, "If", Script.Format.If, ">"), 
    IF_LTEQ(14, 2, 0, 1, "If", Script.Format.If, "<="), 
    IF_GTEQ(15, 2, 0, 1, "If", Script.Format.If, ">="), 
    IF_AND(16, 2, 0, 1, "If", Script.Format.If, "&"), 
    IF_NAND(17, 2, 0, 1, "If", Script.Format.If, "!&"), 
    ELSE(18, 0, -1, 1, "Else"), 
    ENDIF(19, 0, -1, 0, "EndIf"), 
    
    SWITCH_VAR(20, 1, 0, 2, "Switch"), 
    SWITCH_CONST(21, 1, 0, 2, "SwitchConst"), 
    CASE_EQ(22, 1, -1, 1, "Case", Script.Format.Case, "=="), 
    CASE_NEQ(23, 1, -1, 1, "Case", Script.Format.Case, "!="), 
    CASE_LT(24, 1, -1, 1, "Case", Script.Format.Case, "<"), 
    CASE_GT(25, 1, -1, 1, "Case", Script.Format.Case, ">"), 
    CASE_LTEQ(26, 1, -1, 1, "Case", Script.Format.Case, "<="), 
    CASE_GTEQ(27, 1, -1, 1, "Case", Script.Format.Case, ">="), 
    CASE_DEFAULT(28, 0, -1, 1, "Default"), 
    MCASE_OR(29, 1, -1, 1, "CaseOR"), 
    MCASE_AND(30, 1, -1, 1, "CaseAND"), 
    CASE_AND(31, 1, -1, 1, "Case", Script.Format.Case, "&"), 
    END_GROUP(32, 0, -1, 1, "EndCaseGroup"), 
    CASE_RANGE(33, 2, -1, 1, "Case", Script.Format.CaseRange, "to"), 
    BREAK_CASE(34, 0, "BreakCase"), 
    END_SWITCH(35, 0, -2, 0, "EndSwitch"), 
    
    SET_INT(36, 2, "Set"), 
    SET_CONST(37, 2, "SetConst"), 
    SET_FLT(38, 2, "SetF"), 
    
    ADD_INT(39, 2, "Add"), 
    SUB_INT(40, 2, "Sub"), 
    MUL_INT(41, 2, "Mul"), 
    DIV_INT(42, 2, "Div"), 
    MOD_INT(43, 2, "Mod"), 
    ADD_FLT(44, 2, "AddF"), 
    SUB_FLT(45, 2, "SubF"), 
    MUL_FLT(46, 2, "MulF"), 
    DIV_FLT(47, 2, "DivF"), 
    
    SET_BUFFER(48, 1, "UseIntBuffer"), 
    GET_1_INT(49, 1, "Get1Int"), 
    GET_2_INT(50, 2, "Get2Int"), 
    GET_3_INT(51, 3, "Get3Int"), 
    GET_4_INT(52, 4, "Get4Int"), 
    GET_INT_N(53, 2, "GetIntN"), 
    SET_FBUFFER(54, 1, "UseFloatBuffer"), 
    GET_1_FLT(55, 1, "Get1Float"), 
    GET_2_FLT(56, 2, "Get2Float"), 
    GET_3_FLT(57, 3, "Get3Float"), 
    GET_4_FLT(58, 4, "Get4Float"), 
    GET_FLT_N(59, 2, "GetFloatN"), 
    SET_ARRAY(60, 1, "UseArray"), 
    SET_FLAGS(61, 1, "UseFlags"), 
    ALLOC_ARRAY(62, 2, "NewArray"), 
    
    AND(63, 2, "AND"), 
    AND_CONST(64, 2, "ConstAND"), 
    OR(65, 2, "OR"), 
    OR_CONST(66, 2, "ConstOR"), 
    
    CALL(67, -1, "Call", Script.Format.Call, ""), 
    EXEC1(68, 1, "Exec"), 
    EXEC2(69, 2, "Exec"), 
    EXEC_WAIT(70, 1, "ExecWait"), 
    TRIGGER(71, 5, "Bind"), 
    RM_TRIGGER(72, 0, "Unbind"), 
    KILL(73, 1, "Kill"), 
    JUMP(74, 1, "Jump"), 
    PRIORITY(75, 1, "SetPriority"), 
    TIMESCALE(76, 1, "SetTimescale"), 
    GROUP(77, 1, "SetGroup"), 
    LOCK(78, 6, "BindLock"), 
    SUSPEND_ALL(79, 1, "SuspendAll"), 
    RESUME_ALL(80, 1, "ResumeAll"), 
    SUSPEND_OTHERS(81, 1, "SuspendOthers"), 
    RESUME_OTHERS(82, 1, "ResumeOthers"), 
    SUSPEND(83, 1, "Suspend"), 
    RESUME(84, 1, "Resume"), 
    EXISTS(85, 2, "DoesScriptExist"), 
    THREAD1(86, 0, 0, 1, "Thread"), 
    END_THREAD1(87, 0, -1, 0, "EndThread"), 
    THREAD2(88, 0, 0, 1, "ChildThread"), 
    END_THREAD2(89, 0, -1, 0, "EndChildThread");
    

    public final int opcode;
    
    public final int argc;
    
    public final Script.Format fmt;
    
    public final String name;
    
    public final String operator;
    
    public final int tabsBefore;
    
    public final int tabsAfter;
    public final String opString;
    private static final Command[] table;
    private static final HashMap<String, Command> cmdMap;
    private static final HashMap<String, Command> ifOpMap;
    private static final HashMap<String, Command> caseOpMap;
    
    private Command(int opcode, int argc, String name)
    {
      this(opcode, argc, 0, 0, name, Script.Format.Plain, "");
    }
    

    private Command(int opcode, int argc, String name, Script.Format fmt, String operator)
    {
      this(opcode, argc, 0, 0, name, fmt, operator);
    }
    

    private Command(int opcode, int argc, int before, int after, String name)
    {
      this(opcode, argc, before, after, name, Script.Format.Plain, "");
    }
    

    private Command(int opcode, int argc, int before, int after, String name, Script.Format fmt, String operator)
    {
      this.opcode = opcode;
      this.argc = argc;
      tabsBefore = before;
      tabsAfter = after;
      this.name = name;
      this.fmt = fmt;
      this.operator = operator;
      opString = String.format("%08X", new Object[] { Integer.valueOf(opcode) });
    }
    






    static
    {
      table = new Command[values().length + 1];
      cmdMap = new HashMap();
      ifOpMap = new HashMap();
      caseOpMap = new HashMap();
      
      for (Command cmd : values())
      {
        table[opcode] = cmd;
        if (fmt == Script.Format.If) {
          ifOpMap.put(operator, cmd);
        } else if (fmt == Script.Format.Case) {
          caseOpMap.put(operator, cmd);
        } else {
          cmdMap.put(name, cmd);
        }
      }
    }
    
    public static Command get(int opcode) {
      if ((opcode <= 0) || (opcode >= table.length)) {
        return null;
      }
      return table[opcode];
    }
    
    public static Command get(String name)
    {
      return (Command)cmdMap.get(name);
    }
    
    public static Command getIf(String operator)
    {
      return (Command)ifOpMap.get(operator);
    }
    
    public static Command getCase(String operator)
    {
      return (Command)caseOpMap.get(operator);
    }
  }
  
  public static ScriptFindings scan(DataDecoder decoder, ByteBuffer fileBuffer)
  {
    ScriptFindings found = new ScriptFindings();
    
    int lineNum = 0;
    int opcode;
    while ((opcode = fileBuffer.getInt()) != 1)
    {
      lineOffsets.add(Integer.valueOf(fileBuffer.position() - 4));
      int nargs = fileBuffer.getInt();
      int[] arg = new int[nargs];
      
      for (int i = 0; i < nargs; i++) {
        arg[i] = fileBuffer.getInt();
      }
      Command cmd = Command.get(opcode);
      assert (cmd != null);
      
      assert ((argc == -1) || (nargs == argc));
      
      switch (1.$SwitchMap$data$shared$struct$script$Script$Command[cmd.ordinal()])
      {
      case 1: 
        if (decoder.isLocalAddress(arg[1])) {
          unknownChildPointers.add(Integer.valueOf(arg[1]));
        }
        break;
      case 2: 
        if (decoder.isLocalAddress(arg[0])) {
          intBuffers.add(Integer.valueOf(arg[0]));
        }
        break;
      case 3: 
        if (decoder.isLocalAddress(arg[0])) {
          floatBuffers.add(Integer.valueOf(arg[0]));
        }
        break;
      case 4: 
        functionCalls.add(Integer.valueOf(lineNum));
        break;
      
      case 5: 
      case 6: 
      case 7: 
        scriptExecs.add(Integer.valueOf(lineNum));
        break;
      
      case 8: 
      case 9: 
        triggers.add(Integer.valueOf(lineNum));
        break;
      }
      
      


      lineNum++;
    }
    fileBuffer.getInt();
    
    return found;
  }
  



  public static void print(DataDecoder decoder, data.shared.decoder.Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    print(decoder, address, length, fileBuffer, pw);
  }
  
  public static void print(DataDecoder decoder, int startAddress, int length, ByteBuffer fileBuffer, PrintWriter pw)
  {
    lineStart = 0;
    tabs = 0;
    Stack<DataConstants.ConstEnum> switchEnumStack = new Stack();
    ArrayList<int[]> lines = new ArrayList();
    
    while (lineStart < length)
    {
      int opcode = fileBuffer.getInt();
      int nargs = fileBuffer.getInt();
      int[] arg = new int[nargs];
      
      int[] line = new int[nargs + 2];
      line[0] = opcode;
      line[1] = nargs;
      lines.add(line);
      
      for (int i = 0; i < nargs; i++)
      {
        arg[i] = fileBuffer.getInt();
        line[(i + 2)] = arg[i];
      }
      
      Command cmd = Command.get(opcode);
      assert (cmd != null);
      
      assert ((argc == -1) || (nargs == argc));
      
      tabs += tabsBefore;
      prepareLine(decoder, pw);
      int[] prevLine;
      DataConstants.ConstEnum switchEnum; switch (1.$SwitchMap$data$shared$struct$script$Script$Command[cmd.ordinal()])
      {
      case 10: 
        pw.printf(nargs == 0 ? "%s" : "%-8s ", new Object[] { name });
        
        if (arg[0] != 0) {
          decoder.printScriptWord(pw, arg[0]);
        }
        break;
      case 1: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
        pw.printf(name + "  ", new Object[0]);
        decoder.printScriptWord(pw, arg[0]);
        pw.print(" ");
        decoder.printScriptWord(pw, arg[1]);
        break;
      


      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 29: 
        DataConstants.ConstEnum ifEnum = null;
        if (lines.size() > 1)
        {
          int[] prevLine = (int[])lines.get(lines.size() - 2);
          
          if (prevLine[0] == CALLopcode)
          {
            if (prevLine[2] == -2144877344)
            {
              if (line[2] == prevLine[4]) {
                ifEnum = data.shared.DataConstants.EventType;
              }
            } else if (prevLine[2] == -2144952792)
            {
              if (line[2] == prevLine[3]) {
                ifEnum = data.shared.DataConstants.PhaseType;
              }
            }
          }
        }
        pw.print(name + "  ");
        decoder.printScriptWord(pw, arg[0]);
        pw.printf(" %s  ", new Object[] { operator });
        
        if (ifEnum == null) {
          decoder.printScriptWord(pw, arg[1]);
        } else {
          pw.print(ifEnum.getConstantString(arg[1]));
        }
        break;
      

      case 30: 
        pw.print(name + "  ");
        decoder.printScriptWord(pw, arg[0]);
        
        DataConstants.ConstEnum switchEnum = null;
        if (lines.size() > 1)
        {
          prevLine = (int[])lines.get(lines.size() - 2);
          
          if (prevLine[0] == CALLopcode)
          {
            if (prevLine[2] == -2144877344)
            {
              if (prevLine[4] == line[2]) {
                switchEnum = data.shared.DataConstants.EventType;
              }
            } else if (prevLine[2] == -2144952792)
            {
              if (prevLine[3] == line[2])
                switchEnum = data.shared.DataConstants.PhaseType;
            }
          }
        }
        switchEnumStack.push(switchEnum);
        
        break;
      
      case 31: 
        pw.print(name + "  ");
        decoder.printScriptWord(pw, arg[0]);
        switchEnumStack.push(null);
        break;
      
      case 32: 
        pw.print(name);
        switchEnumStack.pop();
        break;
      

      case 33: 
      case 34: 
      case 35: 
      case 36: 
      case 37: 
      case 38: 
      case 39: 
        pw.printf("%s  %s  ", new Object[] { name, operator });
        DataConstants.ConstEnum switchEnum = (DataConstants.ConstEnum)switchEnumStack.peek();
        if (switchEnum == null) {
          decoder.printScriptWord(pw, arg[0]);
        } else {
          pw.print(switchEnum.getConstantString(arg[0]));
        }
        break;
      

      case 40: 
      case 41: 
        pw.printf("%s  ==  ", new Object[] { name });
        DataConstants.ConstEnum switchEnum = (DataConstants.ConstEnum)switchEnumStack.peek();
        if (switchEnum == null) {
          decoder.printScriptWord(pw, arg[0]);
        } else {
          pw.print(switchEnum.getConstantString(arg[0]));
        }
        break;
      

      case 42: 
        pw.print(name + "  ");
        switchEnum = (DataConstants.ConstEnum)switchEnumStack.peek();
        if (switchEnum == null)
        {
          decoder.printScriptWord(pw, arg[0]);
          pw.printf(" %s  ", new Object[] { operator });
          decoder.printScriptWord(pw, arg[1]);
        }
        else
        {
          pw.print(switchEnum.getConstantString(arg[0]));
          pw.printf("  %s  ", new Object[] { operator });
          pw.print(switchEnum.getConstantString(arg[1]));
        }
        
        break;
      
      case 4: 
        pw.printf("%-8s ", new Object[] { name });
        decoder.printFunctionCall(pw, arg, startAddress + lineStart);
        break;
      
      case 5: 
      case 6: 
      case 7: 
        pw.printf("%-8s ", new Object[] { name });
        decoder.printScriptExec(pw, arg, startAddress + lineStart);
        break;
      
      case 8: 
        pw.printf("%-8s ", new Object[] { name });
        decoder.printScriptWord(pw, arg[0]);
        decoder.printConstant(pw, data.shared.DataConstants.TriggerType, arg[1]);
        

        if (arg[2] >= 0) {
          decoder.printColliderID(pw, arg[2]);
        } else {
          decoder.printScriptWord(pw, arg[2]);
        }
        decoder.printScriptWord(pw, arg[3]);
        decoder.printScriptWord(pw, arg[4]);
        break;
      
      case 9: 
        pw.printf("%-8s ", new Object[] { name });
        decoder.printScriptWord(pw, arg[0]);
        decoder.printConstant(pw, data.shared.DataConstants.TriggerType, arg[1]);
        

        if ((arg[1] != 16) && (arg[2] >= 0)) {
          decoder.printColliderID(pw, arg[2]);
        } else {
          decoder.printScriptWord(pw, arg[2]);
        }
        decoder.printScriptWord(pw, arg[3]);
        decoder.printScriptWord(pw, arg[4]);
        decoder.printScriptWord(pw, arg[5]);
        break;
      case 2: case 3: 
      default: 
        pw.printf(nargs == 0 ? "%s" : "%-8s ", new Object[] { name });
        switchEnum = arg;prevLine = switchEnum.length; for (int[] arrayOfInt1 = 0; arrayOfInt1 < prevLine; arrayOfInt1++) { int i = switchEnum[arrayOfInt1];
          decoder.printScriptWord(pw, i);
        }
      }
      tabs += tabsAfter;
      pw.println();
      lineStart += 8 + 4 * nargs;
    }
  }
  
  private static void prepareLine(DataDecoder decoder, PrintWriter pw)
  {
    if (printLineOffsets) {
      pw.printf("%5X:  ", new Object[] { Integer.valueOf(lineStart) });
    } else {
      pw.print("\t");
    }
    if (useTabIndents)
    {
      for (int i = 0; i < tabs; i++) {
        pw.print("\t");
      }
      
    } else {
      for (int i = 0; i < tabs; i++) {
        pw.print("   ");
      }
    }
  }
  
  private static void checkLength(String[] tokens, int argc) {
    if (tokens.length - 1 != argc)
    {
      StringBuilder sb = new StringBuilder();
      for (String s : tokens)
        sb.append(s + "  ");
      throw new ScriptParsingException("Invalid script command: \r\n" + sb.toString());
    }
  }
  
  public static void replaceKeywords(List<String[]> lines, data.shared.lib.FunctionLibrary functionLib, ScriptLibrary scriptLib)
  {
    java.util.ListIterator<String[]> iter = lines.listIterator();
    HashMap<String, String> labelNameMap = new HashMap();
    int nextLabelIndex = 0;
    
    while (iter.hasNext())
    {
      String[] newTokens = null;
      String[] tokens = (String[])iter.next();
      iter.remove();
      
      if (tokens[0].equals("If"))
      {
        checkLength(tokens, 3);
        Command cmd = Command.getIf(tokens[2]);
        if (cmd == null)
          throw new ScriptParsingException("Unknown comparison operator: " + tokens[2]);
        newTokens = new String[] { opString, "00000002", tokens[1], tokens[3] };
      }
      else if (tokens[0].equals("Case"))
      {
        if (tokens.length == 4)
        {
          Command cmd = Command.CASE_RANGE;
          checkLength(tokens, argc + 1);
          newTokens = new String[] { opString, "00000002", tokens[1], tokens[3] };
        }
        else
        {
          Command cmd = Command.getCase(tokens[1]);
          if (cmd == null)
            throw new ScriptParsingException("Unknown case operator: " + tokens[2]);
          checkLength(tokens, argc + 1);
          newTokens = new String[] { opString, "00000001", tokens[2] };
        }
      }
      else if (tokens[0].equals(MCASE_ORname))
      {
        Command cmd = Command.MCASE_OR;
        checkLength(tokens, argc + 1);
        newTokens = new String[] { opString, "00000001", tokens[2] };
      }
      else if (tokens[0].equals(MCASE_ANDname))
      {
        Command cmd = Command.MCASE_AND;
        checkLength(tokens, argc + 1);
        newTokens = new String[] { opString, "00000001", tokens[2] };
      }
      else if (tokens[0].equals(CALLname))
      {
        Command cmd = Command.CALL;
        newTokens = new String[tokens.length + 1];
        newTokens[0] = opString;
        newTokens[1] = String.format("%08X", new Object[] { Integer.valueOf(tokens.length - 1) });
        
        data.shared.lib.NamedFunction func = functionLib.get(tokens[1]);
        if (func != null)
        {
          newTokens[2] = String.format("%08X", new Object[] { Integer.valueOf(func.getAddress()) });
          int nargs = newTokens.length - 3;
          if (func.conflicts(nargs)) {
            throw new ScriptParsingException("Call to %s(...) has incorrect number of arguments: %d", new Object[] { func.getName(), Integer.valueOf(nargs) });
          }
        }
        else {
          newTokens[2] = tokens[1];
        }
        
        for (int i = 3; i < tokens.length + 1; i++) {
          newTokens[i] = tokens[(i - 1)];
        }
      } else if (tokens[0].equals(LOOPname))
      {
        Command cmd = Command.LOOP;
        if (tokens.length == 2) {
          newTokens = new String[] { opString, "00000001", tokens[1] };
        } else if (tokens.length == 1) {
          newTokens = new String[] { opString, "00000001", "00000000" };
        } else {
          checkLength(tokens, -1);
        }
      } else if (tokens[0].equals(LABELname))
      {
        Command cmd = Command.LABEL;
        checkLength(tokens, argc);
        
        if (!data.shared.DataUtils.isInteger(tokens[1]))
        {
          if (!labelNameMap.containsKey(tokens[1]))
            labelNameMap.put(tokens[1], String.format("%X", new Object[] { Integer.valueOf(nextLabelIndex++) }));
          if (labelNameMap.size() > 15)
            throw new ScriptParsingException("Ran out of room for labels.\nScripts cannot have more than 16.\n%s %s", new Object[] { tokens[0], tokens[1] });
          newTokens = new String[] { opString, "00000001", (String)labelNameMap.get(tokens[1]) };
        }
        else {
          newTokens = new String[] { opString, "00000001", tokens[1] };
        }
      } else if (tokens[0].equals(GOTOname))
      {
        Command cmd = Command.GOTO;
        checkLength(tokens, argc);
        
        if (!data.shared.DataUtils.isInteger(tokens[1]))
        {
          if (!labelNameMap.containsKey(tokens[1]))
            labelNameMap.put(tokens[1], String.format("%X", new Object[] { Integer.valueOf(nextLabelIndex++) }));
          if (labelNameMap.size() > 15)
            throw new ScriptParsingException("Ran out of room for labels.\nScripts cannot have more than 16.\n%s %s", new Object[] { tokens[0], tokens[1] });
          newTokens = new String[] { opString, "00000001", (String)labelNameMap.get(tokens[1]) };
        }
        else {
          newTokens = new String[] { opString, "00000001", tokens[1] };
        }
      } else if (tokens[0].equals("Exec"))
      {
        if (scriptLib.has(tokens[1])) {
          tokens[1] = String.format("%08X", new Object[] { Integer.valueOf(scriptLib.get(tokens[1])) });
        }
        if (tokens.length == 3) {
          newTokens = new String[] { EXEC2opString, "00000002", tokens[1], tokens[2] };
        } else if (tokens.length == 2) {
          newTokens = new String[] { EXEC1opString, "00000001", tokens[1] };
        } else {
          checkLength(tokens, -1);
        }
      } else if (tokens[0].equals(EXEC_WAITname))
      {
        Command cmd = Command.EXEC_WAIT;
        
        if (scriptLib.has(tokens[1])) {
          tokens[1] = String.format("%08X", new Object[] { Integer.valueOf(scriptLib.get(tokens[1])) });
        }
        checkLength(tokens, argc);
        newTokens = new String[] { opString, "00000001", tokens[1] };
      }
      else
      {
        Command cmd = Command.get(tokens[0]);
        
        if (cmd == null) {
          throw new ScriptParsingException("Unknown script command: %s", new Object[] { tokens[0] });
        }
        checkLength(tokens, argc);
        newTokens = new String[tokens.length + 1];
        newTokens[0] = (cmd == null ? tokens[0] : opString);
        newTokens[1] = String.format("%08X", new Object[] { Integer.valueOf(tokens.length - 1) });
        for (int i = 2; i < tokens.length + 1; i++) {
          newTokens[i] = tokens[(i - 1)];
        }
      }
      
      for (int i = 0; i < newTokens.length; i++)
      {
        String s = newTokens[i];
        if (s.charAt(0) == '*') {
          newTokens[i] = ScriptVariable.parseScriptVariable(s);
        }
      }
      iter.add(newTokens);
    }
  }
  
  public static int[] getLine(ByteBuffer fileBuffer, int offset)
  {
    fileBuffer.position(offset);
    int opcode = fileBuffer.getInt();
    int nargs = fileBuffer.getInt();
    
    int[] line = new int[nargs + 2];
    line[0] = opcode;
    line[1] = nargs;
    
    for (int i = 2; i < line.length; i++) {
      line[i] = fileBuffer.getInt();
    }
    return line;
  }
  
  public static boolean isScript(PointerHeuristic h, ByteBuffer fileBuffer)
  {
    int length = h.getLength();
    int lastop = -1;
    
    if (length < 12) {
      return false;
    }
    for (int pos = start; pos < end - 12; pos += 4)
    {
      fileBuffer.position(pos);
      int op = fileBuffer.getInt();
      
      while ((op > 0) && (op < 95))
      {
        Command cmd = Command.get(op);
        if (cmd == null) {
          return false;
        }
        
        int argc = fileBuffer.getInt();
        
        if (argc != -1)
        {
          if (argc != argc) {
            return false;
          }
        } else if ((argc < 0) || (argc > 16)) {
          return false;
        }
        

        if ((op == 1) && (lastop == 2))
        {
          structOffset = (pos - start);
          return true;
        }
        
        fileBuffer.position(fileBuffer.position() + 4 * argc);
        
        if (fileBuffer.position() > end) {
          return false;
        }
        lastop = op;
        op = fileBuffer.getInt();
      }
    }
    
    return false;
  }
  
  public static void checkSyntax(DataEncoder encoder, ByteBuffer bb, Struct str)
  {
    boolean hasEnd = false;
    Stack<Integer> stackIf = new Stack();
    int numSwitch = 0;
    int numLoop = 0;
    int numThreadA = 0;
    int numThreadB = 0;
    
    int pos = finalLocation.offset();
    int end = pos + finalSize;
    
    while (pos < end)
    {
      bb.position(pos);
      
      int opcode = bb.getInt();
      
      if (opcode == 0)
      {
        pos += 4;
      }
      else
      {
        Command cmd = Command.get(opcode);
        
        if (cmd == Command.END) {
          hasEnd = true;
        }
        switch (1.$SwitchMap$data$shared$struct$script$Script$Command[cmd.ordinal()])
        {
        case 43: 
          hasEnd = true;
          break;
        
        case 22: 
        case 23: 
        case 24: 
        case 25: 
        case 26: 
        case 27: 
        case 28: 
        case 29: 
          stackIf.push(Integer.valueOf(1));
          break;
        case 44: 
          if ((stackIf.isEmpty()) || (((Integer)stackIf.peek()).intValue() == 2)) {
            throw new InputFileException(encoder.getSource(), "Else command with no matching If in script " + name);
          }
          stackIf.push(Integer.valueOf(2));
          break;
        case 45: 
          if (stackIf.isEmpty()) {
            throw new InputFileException(encoder.getSource(), "ndIf command with no matching If in script " + name);
          }
          if (((Integer)stackIf.peek()).intValue() == 2)
            stackIf.pop();
          if (stackIf.isEmpty()) {
            throw new InputFileException(encoder.getSource(), "EndIf command with no matching If in script " + name);
          }
          stackIf.pop();
          break;
        
        case 30: 
        case 31: 
          numSwitch++;
          break;
        
        case 32: 
          numSwitch--;
          if (numSwitch < 0) {
            throw new InputFileException(encoder.getSource(), "EndSwitch command with no matching Switch in script " + name);
          }
          
          break;
        case 10: 
          numLoop++;
          break;
        
        case 46: 
          numLoop--;
          if (numLoop < 0) {
            throw new InputFileException(encoder.getSource(), "Loop command with no matching Loop in script " + name);
          }
          
          break;
        case 47: 
          numThreadA++;
          break;
        
        case 48: 
          numThreadA--;
          if (numThreadA < 0) {
            throw new InputFileException(encoder.getSource(), END_THREAD1name + " command with no matching " + THREAD1name + " in script " + name);
          }
          
          break;
        case 49: 
          numThreadB++;
          break;
        
        case 50: 
          numThreadB--;
          if (numThreadB < 0) {
            throw new InputFileException(encoder.getSource(), END_THREAD2name + " command with no matching " + THREAD2name + " in script " + name);
          }
          
          break;
        }
        
        
        int nargs = bb.getInt();
        pos += 4 * (nargs + 2);
      }
    }
    if (!hasEnd) {
      throw new InputFileException(encoder.getSource(), "Missing End command for script " + name);
    }
    
    if (!stackIf.isEmpty()) {
      throw new InputFileException(encoder.getSource(), "If command is missing an EndIf in script " + name);
    }
    
    if (numSwitch > 0) {
      throw new InputFileException(encoder.getSource(), "Switch command is missing an EndSwitch in script " + name);
    }
    
    if (numLoop > 0) {
      throw new InputFileException(encoder.getSource(), "Loop command is missing an EndLoop in script " + name);
    }
    
    if (numThreadA > 0) {
      throw new InputFileException(encoder.getSource(), THREAD1name + " command is missing an " + END_THREAD1name + " in script " + name);
    }
    
    if (numThreadB > 0) {
      throw new InputFileException(encoder.getSource(), THREAD2name + " command is missing an " + END_THREAD2name + " in script " + name);
    }
  }
}

package asm.pseudoinstruction;

import asm.MIPS;
import asm.MIPS.Instruction;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PatternFinder
{
  public PatternFinder() {}
  
  private static enum State
  {
    OTHER,  LUI,  ADDU;
    
    private State() {}
  }
  
  public static List<PatternMatch> search(String[][] tokens)
  {
    List<PatternMatch> matchList = new LinkedList();
    State state = State.OTHER;
    boolean delaySlot = false;
    
    for (int i = 0; i < tokens.length; i++)
    {
      String[] line = tokens[i];
      


      MIPS.Instruction ins = (MIPS.Instruction)MIPS.InstructionMap.get(line[0]);
      

      if (ins == null)
      {
        state = State.OTHER;
        delaySlot = false;
      }
      else
      {
        switch (1.$SwitchMap$asm$MIPS$Instruction[ins.ordinal()])
        {
        case 1: 
          state = delaySlot ? State.OTHER : State.LUI;
          delaySlot = false;
          break;
        
        case 2: 
          if (delaySlot) {
            state = State.OTHER;
          } else
            state = state == State.LUI ? State.ADDU : State.OTHER;
          delaySlot = false;
          break;
        
        case 3: 
          if (state == State.LUI)
            matchList.add(new PatternMatch(PatternMatch.Pattern.LI, ins, i, delaySlot));
          state = State.OTHER;
          delaySlot = false;
          break;
        
        case 4: 
          if (state == State.LUI)
            matchList.add(new PatternMatch(PatternMatch.Pattern.LI, ins, i, delaySlot));
          state = State.OTHER;
          delaySlot = false;
          break;
        
        case 5: 
          if (state == State.LUI)
            matchList.add(new PatternMatch(PatternMatch.Pattern.LI, ins, i, delaySlot));
          state = State.OTHER;
          delaySlot = false;
          break;
        



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
          if (state == State.LUI) {
            matchList.add(new PatternMatch(PatternMatch.Pattern.LSA, ins, i, delaySlot));
          } else if (state == State.ADDU)
            matchList.add(new PatternMatch(PatternMatch.Pattern.LST, ins, i, delaySlot));
          state = State.OTHER;
          delaySlot = false;
          break;
        

        case 30: 
        case 31: 
        case 32: 
        case 33: 
        case 34: 
        case 35: 
        case 36: 
        case 37: 
        case 38: 
        case 39: 
        case 40: 
        case 41: 
        case 42: 
        case 43: 
        case 44: 
        case 45: 
        case 46: 
        case 47: 
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
          assert (!delaySlot);
          delaySlot = true;
          break;
        

        default: 
          state = State.OTHER;
          delaySlot = false;
        }
      }
    }
    return matchList;
  }
}

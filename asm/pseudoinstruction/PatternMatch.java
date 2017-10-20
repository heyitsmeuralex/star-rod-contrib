package asm.pseudoinstruction;

import asm.MIPS.Instruction;

public final class PatternMatch {
  public final Pattern pattern;
  public final int line;
  
  public static enum Pattern { LI(2), 
    LSA(2), 
    LST(3);
    
    public final int length;
    
    private Pattern(int length)
    {
      this.length = length;
    }
  }
  

  public final boolean delaySlot;
  
  public final MIPS.Instruction lastIns;
  
  public PatternMatch(Pattern pattern, MIPS.Instruction lastIns, int lastLine, boolean delaySlot)
  {
    this.pattern = pattern;
    this.lastIns = lastIns;
    this.delaySlot = delaySlot;
    
    int diff = lastLine - length;
    line = (delaySlot ? diff : diff + 1);
  }
  
  public String toString()
  {
    if (delaySlot) {
      return pattern + ", line " + line + " (delayed)";
    }
    return pattern + ", line " + line;
  }
}

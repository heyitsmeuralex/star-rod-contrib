package asm;

import java.util.List;

public abstract class AsmUtils
{
  public AsmUtils() {}
  
  public static final String[][] tokenize(List<String> asmList) {
    String[][] tokens = new String[asmList.size()][];
    for (int i = 0; i < asmList.size(); i++)
      tokens[i] = ((String)asmList.get(i)).replaceAll("[()]", "").split(",? +");
    return tokens;
  }
  



  public static final List<Integer> findSequence(String[][] tokens, String... sequence)
  {
    int seqPos = 0;
    int seqEnd = sequence.length;
    
    List<Integer> matches = new java.util.ArrayList();
    
    for (int i = 0; i < tokens.length; i++)
    {
      if (tokens[i][0].equals(sequence[seqPos])) {
        seqPos++;
      } else {
        seqPos = 0;
      }
      if (seqPos == seqEnd)
      {
        matches.add(Integer.valueOf(i - seqEnd + 1));
        seqPos = 0;
      }
    }
    
    return matches;
  }
  









  public static int makeAddress(int upper, int lower)
  {
    int dest = upper << 16 | lower;
    if (lower >>> 15 == 1) {
      dest -= 65536;
    }
    return dest;
  }
}

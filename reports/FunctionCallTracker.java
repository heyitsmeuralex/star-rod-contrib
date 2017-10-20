package reports;

import data.shared.lib.FunctionLibrary;
import data.shared.lib.NamedFunction;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.TreeMap;


public class FunctionCallTracker
{
  private static TreeMap<Long, Integer> calls = new TreeMap();
  
  public FunctionCallTracker() {}
  
  public static void clear() { calls.clear(); }
  

  public static void addCall(int address)
  {
    long unsignedAddress = address & 0xFFFFFFFF;
    
    if (calls.containsKey(Long.valueOf(unsignedAddress)))
    {
      int oldCount = ((Integer)calls.get(Long.valueOf(unsignedAddress))).intValue();
      calls.put(Long.valueOf(unsignedAddress), Integer.valueOf(oldCount + 1));
    }
    else
    {
      calls.put(Long.valueOf(unsignedAddress), Integer.valueOf(1));
    }
  }
  
  public static void printCalls(FunctionLibrary functionLib, PrintWriter out)
  {
    int identified = 0;
    int identifiedCount = 0;
    int totalCount = 0;
    
    for (Map.Entry<Long, Integer> entry : calls.entrySet())
    {
      Integer address = Integer.valueOf((int)((Long)entry.getKey()).longValue());
      Integer count = (Integer)entry.getValue();
      
      NamedFunction func = functionLib.get(address.intValue());
      
      if (func != null)
      {
        out.printf("%08X  %-4d  %s\r\n", new Object[] { address, count, func.getName() });
        identified++;
        identifiedCount += count.intValue();
      }
      else {
        out.printf("%08X  %-4d  \r\n", new Object[] { address, count });
      }
      totalCount += count.intValue();
    }
    
    out.println();
    out.printf("Identified %d of %d functions (%5.2f%%)\r\n", new Object[] {
      Integer.valueOf(identified), Integer.valueOf(calls.size()), Float.valueOf(100.0F * identified / calls.size()) });
    out.printf("Identified %d of %d function calls (%5.2f%%)\r\n", new Object[] {
      Integer.valueOf(identifiedCount), Integer.valueOf(totalCount), Float.valueOf(100.0F * identifiedCount / totalCount) });
    
    out.close();
  }
  
  public static void printCalls(FunctionLibrary functionLib)
  {
    printCalls(functionLib, new PrintWriter(System.out));
  }
  
  public static void printCalls(PrintWriter out)
  {
    for (Map.Entry<Long, Integer> entry : calls.entrySet())
    {
      Long address = (Long)entry.getKey();
      Integer count = (Integer)entry.getValue();
      out.printf("%08X  %-6d\r\n", new Object[] { address, count });
    }
    
    out.close();
  }
  
  public static void printCalls()
  {
    printCalls(new PrintWriter(System.out));
  }
}

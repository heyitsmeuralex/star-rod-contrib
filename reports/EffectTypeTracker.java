package reports;

import data.shared.DataConstants;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.TreeMap;
import util.DualHashMap;

public class EffectTypeTracker
{
  private static TreeMap<Integer, Integer> effectCount = new TreeMap();
  private static TreeMap<Integer, String> effectSource = new TreeMap();
  
  public EffectTypeTracker() {}
  
  public static void clear() { effectCount.clear();
    effectSource.clear();
  }
  
  public static void addEffect(int effect, String source)
  {
    if (effectCount.containsKey(Integer.valueOf(effect)))
    {
      int oldCount = ((Integer)effectCount.get(Integer.valueOf(effect))).intValue();
      effectCount.put(Integer.valueOf(effect), Integer.valueOf(oldCount + 1));
      effectSource.put(Integer.valueOf(effect), source);
    }
    else
    {
      effectCount.put(Integer.valueOf(effect), Integer.valueOf(1));
      effectSource.put(Integer.valueOf(effect), source);
    }
  }
  
  public static void printEffects(PrintWriter out)
  {
    for (Map.Entry<Integer, Integer> entry : effectCount.entrySet())
    {
      Integer effect = (Integer)entry.getKey();
      Integer count = (Integer)entry.getValue();
      String source = (String)effectSource.get(effect);
      
      if (DataConstants.EffectType.contains(effect)) {
        out.printf("** %08X  %-3d  %s\r\n", new Object[] { effect, count, source });
      } else
        out.printf("   %08X  %-3d  %s\r\n", new Object[] { effect, count, source });
    }
    out.close();
  }
}

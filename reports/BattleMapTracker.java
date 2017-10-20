package reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import main.Directories;
import util.IOUtils;


public class BattleMapTracker
{
  private static boolean enabled = false;
  




  private static final HashMap<String, List<Integer>> mapMap = new LinkedHashMap();
  private static final TreeMap<Integer, List<String>> battleMap = new TreeMap();
  private static final HashSet<Integer> battleIDSet = new HashSet();
  
  public BattleMapTracker() {}
  
  public static void enable() {
    enabled = true;
  }
  
  public static boolean isEnabled()
  {
    return enabled;
  }
  
  public static boolean hasBattleID(int id)
  {
    return battleIDSet.contains(Integer.valueOf(id & 0xFFFF0000));
  }
  
  public static List<String> getMaps(int id)
  {
    return (List)battleMap.get(Integer.valueOf(id & 0xFFFF0000));
  }
  
  public static void add(String mapName, int battleID)
  {
    if (!enabled) {
      return;
    }
    battleIDSet.add(Integer.valueOf(battleID & 0xFFFF0000));
    List<Integer> battleList;
    List<Integer> battleList;
    if (mapMap.containsKey(mapName))
    {
      battleList = (List)mapMap.get(mapName);
    }
    else
    {
      battleList = new LinkedList();
      mapMap.put(mapName, battleList);
    }
    battleList.add(Integer.valueOf(battleID));
    List<String> mapList;
    List<String> mapList;
    if (battleMap.containsKey(Integer.valueOf(battleID)))
    {
      mapList = (List)battleMap.get(Integer.valueOf(battleID));
    }
    else
    {
      mapList = new LinkedList();
      battleMap.put(Integer.valueOf(battleID), mapList);
    }
    mapList.add(mapName);
  }
  
  public static void printBattles() throws FileNotFoundException
  {
    if (!enabled) {
      return;
    }
    File out = new File(Directories.DUMP_REPORTS + "BattleList.txt");
    PrintWriter pw = IOUtils.getBufferedPrintWriter(out);
    pw.println("Maps featuring battles:");
    for (Iterator localIterator1 = battleMap.keySet().iterator(); localIterator1.hasNext();) { int battleID = ((Integer)localIterator1.next()).intValue();
      
      pw.printf("%08X : ", new Object[] { Integer.valueOf(battleID) });
      
      for (String mapName : (List)battleMap.get(Integer.valueOf(battleID))) {
        pw.printf("%-8s ", new Object[] { mapName });
      }
      pw.println();
    }
    pw.close();
  }
  
  public static void printMaps() throws FileNotFoundException
  {
    if (!enabled) {
      return;
    }
    File out = new File(Directories.DUMP_REPORTS + "BattleMapIndex.txt");
    PrintWriter pw = IOUtils.getBufferedPrintWriter(out);
    pw.println("Battles found in maps:");
    for (String mapName : mapMap.keySet())
    {
      pw.printf("%-8s : ", new Object[] { mapName });
      
      for (Iterator localIterator2 = ((List)mapMap.get(mapName)).iterator(); localIterator2.hasNext();) { int battleID = ((Integer)localIterator2.next()).intValue();
        pw.printf("%08X ", new Object[] { Integer.valueOf(battleID) });
      }
      pw.println();
    }
    pw.close();
  }
}

package data.map;


public class MapBlueprint
  implements Comparable<MapBlueprint>
{
  public String name = null;
  public boolean hasShape = false;
  public int shapeOffset;
  public int shapeLength;
  public boolean hasHit = false;
  public int hitOffset;
  public int hitLength;
  public boolean hasData = false;
  public int dataOffset;
  public int dataLength; public int unknownPointers = 0;
  public int missingSections = 0;
  
  public int headerAddr;
  public int initAddr;
  public int soundSetting;
  public boolean hasBackground = false;
  public String bgName = "nok_bg";
  
  public MapBlueprint() {}
  
  public int compareTo(MapBlueprint other) {
    return name.compareTo(name);
  }
}

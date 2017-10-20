package editor.map.shape.commands;












public abstract class DisplayCommand
{
  public DisplayCommand() {}
  











  public static enum CmdType
  {
    DrawTriangleBatch("Draw Triangles"), 
    PipeSync("RDP Pipe Sync"), 
    SetGeometryFlags("Set Geometry Flags"), 
    ClrGeometryFlags("Clear Geometry Flags"), 
    Custom("Custom Command");
    
    private final String name;
    
    private CmdType(String name) { this.name = name; }
    



    public String toString()
    {
      return name;
    }
  }
  
  public transient boolean selected = false;
  
  public static DisplayCommand resolveCommand(int r, int s)
  {
    switch (r >>> 24) {
    case 231: 
      return new FlushPipeline();
    case 217:  return ChangeGeometryFlags.getCommand(r, s); }
    return new CustomCommand(r, s);
  }
  
  public void doGL() {}
  
  public abstract CmdType getType();
  
  public abstract int[] getF3DEX2Command();
  
  public abstract DisplayCommand deepCopy();
}

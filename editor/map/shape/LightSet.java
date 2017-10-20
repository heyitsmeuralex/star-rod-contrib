package editor.map.shape;

import editor.commands.AbstractCommand;
import editor.ui.info.LightingPanel;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import shared.Globals;





public class LightSet
  implements Externalizable, Iterable<Light>
{
  private static final long serialVersionUID = 1L;
  public String name;
  public int A;
  public int B;
  private ArrayList<Light> lightList;
  public transient int listIndex;
  public transient int address;
  
  public LightSet() {}
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    name = in.readUTF();
    A = in.readInt();
    B = in.readInt();
    
    int numLights = in.readInt();
    lightList = new ArrayList(numLights);
    
    for (int i = 0; i < numLights; i++)
    {
      Light light = new Light(this);
      data[0] = in.readInt();
      data[1] = in.readInt();
      data[2] = in.readInt();
      lightList.add(light);
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeUTF(name);
    out.writeInt(A);
    out.writeInt(B);
    
    out.writeInt(lightList.size());
    
    for (Light light : lightList)
    {
      out.writeInt(data[0]);
      out.writeInt(data[1]);
      out.writeInt(data[2]);
    }
  }
  

  public Iterator<Light> iterator()
  {
    return lightList.iterator();
  }
  
  public int getLightCount()
  {
    return lightList.size();
  }
  
  public static LightSet createEmptySet()
  {
    LightSet empty = new LightSet();
    lightList = new ArrayList();
    name = "Lights_None";
    return empty;
  }
  
  public void get(ByteBuffer bb, int numLights)
  {
    A = bb.getInt();
    B = bb.getInt();
    
    lightList = new ArrayList(numLights);
    
    for (int i = 0; i < numLights; i++)
    {
      Light light = new Light(this);
      data[0] = bb.getInt();
      data[1] = bb.getInt();
      data[2] = bb.getInt();
      int zero = bb.getInt();
      
      assert (zero == 0);
      lightList.add(light);
    }
  }
  
  public void write(RandomAccessFile raf) throws IOException
  {
    raf.writeInt(A);
    raf.writeInt(B);
    
    for (Light light : lightList)
    {
      raf.writeInt(data[0]);
      raf.writeInt(data[1]);
      raf.writeInt(data[2]);
      raf.writeInt(0);
    }
    
    if (lightList.isEmpty())
    {
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(0);
    }
    

    raf.writeInt(0);
    raf.writeInt(0);
  }
  

  public String toString()
  {
    return name;
  }
  
  public void print()
  {
    System.out.println(name + " has " + lightList.size() + " lights:");
    for (Light light : lightList)
    {
      System.out.printf("%08X %08X %08X %08X" + Globals.NL, new Object[] {
        Integer.valueOf(data[0]), Integer.valueOf(data[1]), Integer.valueOf(data[2]), Integer.valueOf(data[3]) });
    }
  }
  
  public static final class SetLightingName extends AbstractCommand
  {
    private final LightSet lightSet;
    private final String oldName;
    private final String newName;
    
    public SetLightingName(LightSet lightSet, String s)
    {
      super();
      this.lightSet = lightSet;
      oldName = name;
      newName = s;
    }
    

    public boolean shouldExec()
    {
      return (!newName.isEmpty()) && (!oldName.equals(newName));
    }
    

    public void exec()
    {
      super.exec();
      lightSet.name = newName;
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.name = oldName;
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class SetLightingA extends AbstractCommand
  {
    private final LightSet lightSet;
    private final int oldValue;
    private final int newValue;
    
    public SetLightingA(LightSet lightSet, int val)
    {
      super();
      this.lightSet = lightSet;
      oldValue = A;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.A = newValue;
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.A = oldValue;
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class SetLightingB extends AbstractCommand
  {
    private final LightSet lightSet;
    private final int oldValue;
    private final int newValue;
    
    public SetLightingB(LightSet lightSet, int val)
    {
      super();
      this.lightSet = lightSet;
      oldValue = B;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.B = newValue;
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.B = oldValue;
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class MoveLightUp extends AbstractCommand
  {
    private final LightSet lightSet;
    private final Light light;
    private final int initialPos;
    
    public MoveLightUp(Light light)
    {
      super();
      lightSet = parent;
      this.light = light;
      initialPos = lightSet.lightList.indexOf(light);
    }
    

    public boolean shouldExec()
    {
      return initialPos > 0;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.lightList.remove(light);
      lightSet.lightList.add(initialPos - 1, light);
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.lightList.remove(light);
      lightSet.lightList.add(initialPos, light);
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class MoveLightDown extends AbstractCommand
  {
    private final LightSet lightSet;
    private final Light light;
    private final int initialPos;
    
    public MoveLightDown(Light light)
    {
      super();
      lightSet = parent;
      this.light = light;
      initialPos = lightSet.lightList.indexOf(light);
    }
    

    public boolean shouldExec()
    {
      return initialPos < lightSet.lightList.size() - 1;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.lightList.remove(light);
      lightSet.lightList.add(initialPos + 1, light);
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.lightList.remove(light);
      lightSet.lightList.add(initialPos, light);
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class CreateLight extends AbstractCommand
  {
    private final LightSet lightSet;
    private final Light light;
    
    public CreateLight(LightSet lightSet)
    {
      super();
      this.lightSet = lightSet;
      light = new Light(lightSet);
    }
    

    public boolean shouldExec()
    {
      return lightSet.getLightCount() < 8;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.lightList.add(light);
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.lightList.remove(light);
      LightingPanel.updateLights(lightSet);
    }
  }
  
  public static final class DeleteLight extends AbstractCommand
  {
    private final LightSet lightSet;
    private final Light light;
    private final int initialPos;
    
    public DeleteLight(Light light)
    {
      super();
      lightSet = parent;
      this.light = light;
      initialPos = lightSet.lightList.indexOf(light);
    }
    

    public boolean shouldExec()
    {
      return lightSet.getLightCount() > 1;
    }
    

    public void exec()
    {
      super.exec();
      lightSet.lightList.remove(light);
      LightingPanel.updateLights(lightSet);
    }
    

    public void undo()
    {
      super.undo();
      lightSet.lightList.add(initialPos, light);
      LightingPanel.updateLights(lightSet);
    }
  }
}

package data.sprites;

import java.util.LinkedList;
import java.util.List;

public class SpriteAnimation
{
  public final Sprite parentSprite;
  public final List<SpriteComponent> componentList;
  
  public SpriteAnimation(Sprite parentSprite)
  {
    this.parentSprite = parentSprite;
    componentList = new LinkedList();
  }
  
  public int getComponentCount()
  {
    return componentList.size();
  }
}

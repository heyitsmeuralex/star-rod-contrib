package editor.map;

public abstract class ReversibleTransform
{
  public ReversibleTransform() {}
  
  public abstract void transform();
  
  public abstract void revert();
}

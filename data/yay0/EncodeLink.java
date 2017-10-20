package data.yay0;

public class EncodeLink implements Encode
{
  public final int length;
  public final int distance;
  
  public EncodeLink(int length, int distance)
  {
    this.length = length;
    this.distance = distance;
  }
  
  public void exec(Yay0Encoder encoder)
  {
    encoder.addLink(length, distance);
  }
  
  public int getEncodeLength()
  {
    return length;
  }
  
  public int getBudgetCost()
  {
    return length > 17 ? 3 : 2;
  }
  
  public String toString()
  {
    return String.format("<L:%d D:%d>", new Object[] { Integer.valueOf(length), Integer.valueOf(distance) });
  }
}

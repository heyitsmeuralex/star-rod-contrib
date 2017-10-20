package editor;

public class UVGrid extends Grid
{
  private static final int MAX_PWR = 11;
  
  public UVGrid(int power)
  {
    super(true, power);
  }
  

  public void toggleType()
  {
    throw new UnsupportedOperationException("UV grid is binary only.");
  }
  

  public void increasePower()
  {
    power += 1;
    if (power > 11) { power = 11;
    }
  }
  
  protected int getBinaryGridSpacing(int pwr)
  {
    if (pwr < 0) pwr = 0;
    if (pwr > 11) pwr = 11;
    return 1 << pwr;
  }
  

  protected int getDecimalGridSpacing(int pwr)
  {
    throw new UnsupportedOperationException("UV grid is binary only.");
  }
}

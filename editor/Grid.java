package editor;


public class Grid
{
  public boolean binary;
  public int power;
  private static final int MAX_BIN_PWR = 9;
  private static final int MAX_DEC_PWR = 5;
  
  public Grid(boolean binary, int power)
  {
    this.binary = binary;
    this.power = power;
  }
  
  public void toggleType()
  {
    if (binary)
    {
      switch (power) {
      case 0: 
        power = 0; break;
      case 1:  power = 0; break;
      case 2:  power = 1; break;
      case 3:  power = 2; break;
      case 4:  power = 2; break;
      case 5:  power = 3; break;
      case 6:  power = 3; break;
      case 7:  power = 4; break;
      case 8:  power = 4; break;
      case 9:  power = 5;
      
      }
      
    } else {
      switch (power) {
      case 0: 
        power = 0; break;
      case 1:  power = 2; break;
      case 2:  power = 3; break;
      case 3:  power = 6; break;
      case 4:  power = 7; break;
      case 5:  power = 9;
      }
      
    }
    binary = (!binary);
  }
  
  public void increasePower()
  {
    int max = getMaxPower();
    power += 1;
    if (power > max) power = max;
  }
  
  public void decreasePower()
  {
    power -= 1;
    if (power < 0) power = 0;
  }
  
  public int getPower()
  {
    return power;
  }
  
  private int getMaxPower()
  {
    return binary ? 9 : 5;
  }
  
  public int getSpacing()
  {
    return getGridSpacing(power);
  }
  
  public int getSpacing(int width)
  {
    int pwr = power;
    int spacing = getGridSpacing(pwr);
    

    while ((pwr < getMaxPower()) && (width / spacing > 200.0D)) {
      spacing = getGridSpacing(++pwr);
    }
    return spacing;
  }
  
  private int getGridSpacing(int pwr)
  {
    return binary ? getBinaryGridSpacing(pwr) : getDecimalGridSpacing(pwr);
  }
  
  protected int getBinaryGridSpacing(int pwr)
  {
    if (pwr < 0) pwr = 0;
    if (pwr > 9) pwr = 9;
    return 1 << pwr;
  }
  
  protected int getDecimalGridSpacing(int pwr)
  {
    if (pwr < 0) pwr = 0;
    if (pwr > 5) { pwr = 5;
    }
    switch (pwr) {
    case 0: 
      return 1;
    case 1:  return 5;
    case 2:  return 10;
    case 3:  return 50;
    case 4:  return 100;
    case 5:  return 500;
    case 6:  return 1000;
    case 7:  return 5000;
    case 8:  return 10000;
    }
    
    return 32767;
  }
}

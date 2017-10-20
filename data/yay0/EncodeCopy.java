package data.yay0;

public class EncodeCopy implements Encode
{
  public final byte value;
  
  public EncodeCopy(byte b)
  {
    value = b;
  }
  
  public void exec(Yay0Encoder encoder)
  {
    encoder.addCopy(value);
  }
  
  public int getEncodeLength()
  {
    return 1;
  }
  
  public int getBudgetCost()
  {
    return 1;
  }
  
  public String toString()
  {
    return String.format("<%02X>", new Object[] { Byte.valueOf(value) });
  }
}

package data.shared;

public class Location implements Comparable<Location> {
  public static enum Type {
    Offset,  Address;
    private Type() {} }
  private static boolean initialized = false;
  private static int baseAddress;
  private final int offset;
  
  public static void setBaseAddress(int address) {
    baseAddress = address;
    initialized = true;
  }
  
  public static int toOffset(int address)
  {
    return address - baseAddress;
  }
  
  public static int toAddress(int offset)
  {
    return baseAddress + offset;
  }
  


  public Location(int value, Type type)
  {
    switch (1.$SwitchMap$data$shared$Location$Type[type.ordinal()])
    {
    case 1: 
      offset = value;
      break;
    case 2: 
      if (!initialized)
        throw new RuntimeException("Cannot create Location from address until base address is set.");
      offset = (value - baseAddress);
      break;
    default: 
      throw new IllegalArgumentException();
    }
  }
  
  public Location(Location other)
  {
    offset = offset;
  }
  
  public int offset()
  {
    return offset;
  }
  
  public int address()
  {
    if (!initialized)
      throw new RuntimeException("Cannot determine address until base address is set.");
    return baseAddress + offset;
  }
  















  public int compareTo(Location other)
  {
    return offset - offset;
  }
}

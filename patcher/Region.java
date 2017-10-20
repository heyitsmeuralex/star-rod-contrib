package patcher;

public class Region implements Comparable<Region>
{
  public int start;
  public int end;
  
  public Region(int start, int end)
  {
    this.start = start;
    this.end = end;
    if ((!$assertionsDisabled) && (end < start)) throw new AssertionError(String.format("%X to %X\r\n", new Object[] { Integer.valueOf(start), Integer.valueOf(end) }));
  }
  
  public int length()
  {
    return end - start;
  }
  

  public int compareTo(Region other)
  {
    return start - start;
  }
  
  public static boolean adjacent(Region a, Region b)
  {
    if (start < start) {
      return end == start;
    }
    return end == start;
  }
  
  public boolean contains(int value)
  {
    return (start <= value) && (value <= end);
  }
  
  public boolean contains(Region b)
  {
    return (start >= start) && (end <= end);
  }
  





  public static int overlap(Region a, Region b)
  {
    if (start < start) {
      return end - start;
    }
    return end - start;
  }
  
  public static Region merge(Region a, Region b)
  {
    if (!adjacent(a, b)) {
      return null;
    }
    if (start < start) {
      return new Region(start, end);
    }
    return new Region(end, start);
  }
  
  public String toString()
  {
    return String.format("[%X, %X]", new Object[] { Integer.valueOf(start), Integer.valueOf(end) });
  }
}

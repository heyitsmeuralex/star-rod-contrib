package util;

public enum Priority
{
  UPDATE, 
  DETAIL, 
  STANDARD, 
  IMPORTANT, 
  MILESTONE, 
  WARNING, 
  ERROR;
  
  private Priority() {}
  
  public boolean greaterThan(Priority other) { return compareTo(other) > 0; }
  

  public boolean lessThan(Priority other)
  {
    return compareTo(other) < 0;
  }
}

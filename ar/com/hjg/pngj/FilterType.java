package ar.com.hjg.pngj;

import java.util.HashMap;









public enum FilterType
{
  FILTER_NONE(0), 
  


  FILTER_SUB(1), 
  


  FILTER_UP(2), 
  


  FILTER_AVERAGE(3), 
  


  FILTER_PAETH(4), 
  


  FILTER_DEFAULT(-1), 
  


  FILTER_AGGRESSIVE(-2), 
  


  FILTER_VERYAGGRESSIVE(-4), 
  


  FILTER_ADAPTIVE_FULL(-4), 
  


  FILTER_ADAPTIVE_MEDIUM(-3), 
  


  FILTER_ADAPTIVE_FAST(-2), 
  


  FILTER_SUPER_ADAPTIVE(-10), 
  


  FILTER_PRESERVE(-40), 
  


  FILTER_CYCLIC(-50), 
  


  FILTER_UNKNOWN(-100);
  
  public final int val;
  private static HashMap<Integer, FilterType> byVal;
  
  private FilterType(int val) { this.val = val; }
  


  static
  {
    byVal = new HashMap();
    for (FilterType ft : values()) {
      byVal.put(Integer.valueOf(val), ft);
    }
  }
  
  public static FilterType getByVal(int i) {
    return (FilterType)byVal.get(Integer.valueOf(i));
  }
  
  public static boolean isValidStandard(int i)
  {
    return (i >= 0) && (i <= 4);
  }
  
  public static boolean isValidStandard(FilterType fy) {
    return (fy != null) && (isValidStandard(val));
  }
  
  public static boolean isAdaptive(FilterType fy) {
    return (val <= -2) && (val >= -4);
  }
  


  public static FilterType[] getAllStandard()
  {
    return new FilterType[] { FILTER_NONE, FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH };
  }
  
  public static FilterType[] getAllStandardNoneLast() {
    return new FilterType[] { FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH, FILTER_NONE };
  }
  
  public static FilterType[] getAllStandardExceptNone() {
    return new FilterType[] { FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH };
  }
  
  static FilterType[] getAllStandardForFirstRow() {
    return new FilterType[] { FILTER_SUB, FILTER_NONE };
  }
}

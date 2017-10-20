package org.apache.commons.math.stat.descriptive.rank;

import java.io.Serializable;






























public class Median
  extends Percentile
  implements Serializable
{
  private static final long serialVersionUID = -3961477041290915687L;
  
  public Median()
  {
    super(50.0D);
  }
  





  public Median(Median original)
  {
    super(original);
  }
}

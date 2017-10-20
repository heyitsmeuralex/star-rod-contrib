package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic;

















































public class SemiVariance
  extends AbstractUnivariateStatistic
  implements Serializable
{
  public static final Direction UPSIDE_VARIANCE = Direction.UPSIDE;
  




  public static final Direction DOWNSIDE_VARIANCE = Direction.DOWNSIDE;
  



  private static final long serialVersionUID = -2653430366886024994L;
  


  private boolean biasCorrected = true;
  



  private Direction varianceDirection = Direction.DOWNSIDE;
  






  public SemiVariance() {}
  






  public SemiVariance(boolean biasCorrected)
  {
    this.biasCorrected = biasCorrected;
  }
  







  public SemiVariance(Direction direction)
  {
    varianceDirection = direction;
  }
  











  public SemiVariance(boolean corrected, Direction direction)
  {
    biasCorrected = corrected;
    varianceDirection = direction;
  }
  






  public SemiVariance(SemiVariance original)
  {
    copy(original, this);
  }
  




  public SemiVariance copy()
  {
    SemiVariance result = new SemiVariance();
    copy(this, result);
    return result;
  }
  








  public static void copy(SemiVariance source, SemiVariance dest)
  {
    dest.setData(source.getDataRef());
    biasCorrected = biasCorrected;
    varianceDirection = varianceDirection;
  }
  










  public double evaluate(double[] values)
  {
    if (values == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    return evaluate(values, 0, values.length);
  }
  















  public double evaluate(double[] values, int start, int length)
  {
    double m = new Mean().evaluate(values, start, length);
    return evaluate(values, m, varianceDirection, biasCorrected, 0, values.length);
  }
  










  public double evaluate(double[] values, Direction direction)
  {
    double m = new Mean().evaluate(values);
    return evaluate(values, m, direction, biasCorrected, 0, values.length);
  }
  











  public double evaluate(double[] values, double cutoff)
  {
    return evaluate(values, cutoff, varianceDirection, biasCorrected, 0, values.length);
  }
  












  public double evaluate(double[] values, double cutoff, Direction direction)
  {
    return evaluate(values, cutoff, direction, biasCorrected, 0, values.length);
  }
  



















  public double evaluate(double[] values, double cutoff, Direction direction, boolean corrected, int start, int length)
  {
    test(values, start, length);
    if (values.length == 0) {
      return NaN.0D;
    }
    if (values.length == 1) {
      return 0.0D;
    }
    boolean booleanDirection = direction.getDirection();
    
    double dev = 0.0D;
    double sumsq = 0.0D;
    for (int i = start; i < length; i++) {
      if (values[i] > cutoff == booleanDirection) {
        dev = values[i] - cutoff;
        sumsq += dev * dev;
      }
    }
    
    if (corrected) {
      return sumsq / (length - 1.0D);
    }
    return sumsq / length;
  }
  







  public boolean isBiasCorrected()
  {
    return biasCorrected;
  }
  




  public void setBiasCorrected(boolean biasCorrected)
  {
    this.biasCorrected = biasCorrected;
  }
  




  public Direction getVarianceDirection()
  {
    return varianceDirection;
  }
  




  public void setVarianceDirection(Direction varianceDirection)
  {
    this.varianceDirection = varianceDirection;
  }
  







  public static enum Direction
  {
    UPSIDE(true), 
    




    DOWNSIDE(false);
    




    private boolean direction;
    



    private Direction(boolean b)
    {
      direction = b;
    }
    




    boolean getDirection()
    {
      return direction;
    }
  }
}

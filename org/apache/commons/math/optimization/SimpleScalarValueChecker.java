package org.apache.commons.math.optimization;

import org.apache.commons.math.util.FastMath;






































public class SimpleScalarValueChecker
  implements RealConvergenceChecker
{
  private static final double DEFAULT_RELATIVE_THRESHOLD = 1.1102230246251565E-14D;
  private static final double DEFAULT_ABSOLUTE_THRESHOLD = 2.2250738585072014E-306D;
  private final double relativeThreshold;
  private final double absoluteThreshold;
  
  public SimpleScalarValueChecker()
  {
    relativeThreshold = 1.1102230246251565E-14D;
    absoluteThreshold = 2.2250738585072014E-306D;
  }
  









  public SimpleScalarValueChecker(double relativeThreshold, double absoluteThreshold)
  {
    this.relativeThreshold = relativeThreshold;
    this.absoluteThreshold = absoluteThreshold;
  }
  


  public boolean converged(int iteration, RealPointValuePair previous, RealPointValuePair current)
  {
    double p = previous.getValue();
    double c = current.getValue();
    double difference = FastMath.abs(p - c);
    double size = FastMath.max(FastMath.abs(p), FastMath.abs(c));
    return (difference <= size * relativeThreshold) || (difference <= absoluteThreshold);
  }
}

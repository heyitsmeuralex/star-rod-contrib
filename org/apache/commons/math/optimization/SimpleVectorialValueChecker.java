package org.apache.commons.math.optimization;

import org.apache.commons.math.util.FastMath;






































public class SimpleVectorialValueChecker
  implements VectorialConvergenceChecker
{
  private static final double DEFAULT_RELATIVE_THRESHOLD = 1.1102230246251565E-14D;
  private static final double DEFAULT_ABSOLUTE_THRESHOLD = 2.2250738585072014E-306D;
  private final double relativeThreshold;
  private final double absoluteThreshold;
  
  public SimpleVectorialValueChecker()
  {
    relativeThreshold = 1.1102230246251565E-14D;
    absoluteThreshold = 2.2250738585072014E-306D;
  }
  









  public SimpleVectorialValueChecker(double relativeThreshold, double absoluteThreshold)
  {
    this.relativeThreshold = relativeThreshold;
    this.absoluteThreshold = absoluteThreshold;
  }
  


  public boolean converged(int iteration, VectorialPointValuePair previous, VectorialPointValuePair current)
  {
    double[] p = previous.getValueRef();
    double[] c = current.getValueRef();
    for (int i = 0; i < p.length; i++) {
      double pi = p[i];
      double ci = c[i];
      double difference = FastMath.abs(pi - ci);
      double size = FastMath.max(FastMath.abs(pi), FastMath.abs(ci));
      if ((difference > size * relativeThreshold) && (difference > absoluteThreshold))
      {
        return false;
      }
    }
    return true;
  }
}

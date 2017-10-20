package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;















































public class StatisticalSummaryValues
  implements Serializable, StatisticalSummary
{
  private static final long serialVersionUID = -5108854841843722536L;
  private final double mean;
  private final double variance;
  private final long n;
  private final double max;
  private final double min;
  private final double sum;
  
  public StatisticalSummaryValues(double mean, double variance, long n, double max, double min, double sum)
  {
    this.mean = mean;
    this.variance = variance;
    this.n = n;
    this.max = max;
    this.min = min;
    this.sum = sum;
  }
  


  public double getMax()
  {
    return max;
  }
  


  public double getMean()
  {
    return mean;
  }
  


  public double getMin()
  {
    return min;
  }
  


  public long getN()
  {
    return n;
  }
  


  public double getSum()
  {
    return sum;
  }
  


  public double getStandardDeviation()
  {
    return FastMath.sqrt(variance);
  }
  


  public double getVariance()
  {
    return variance;
  }
  








  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof StatisticalSummaryValues)) {
      return false;
    }
    StatisticalSummaryValues stat = (StatisticalSummaryValues)object;
    return (MathUtils.equalsIncludingNaN(stat.getMax(), getMax())) && (MathUtils.equalsIncludingNaN(stat.getMean(), getMean())) && (MathUtils.equalsIncludingNaN(stat.getMin(), getMin())) && (MathUtils.equalsIncludingNaN((float)stat.getN(), (float)getN())) && (MathUtils.equalsIncludingNaN(stat.getSum(), getSum())) && (MathUtils.equalsIncludingNaN(stat.getVariance(), getVariance()));
  }
  










  public int hashCode()
  {
    int result = 31 + MathUtils.hash(getMax());
    result = result * 31 + MathUtils.hash(getMean());
    result = result * 31 + MathUtils.hash(getMin());
    result = result * 31 + MathUtils.hash(getN());
    result = result * 31 + MathUtils.hash(getSum());
    result = result * 31 + MathUtils.hash(getVariance());
    return result;
  }
  






  public String toString()
  {
    StringBuilder outBuffer = new StringBuilder();
    String endl = "\n";
    outBuffer.append("StatisticalSummaryValues:").append(endl);
    outBuffer.append("n: ").append(getN()).append(endl);
    outBuffer.append("min: ").append(getMin()).append(endl);
    outBuffer.append("max: ").append(getMax()).append(endl);
    outBuffer.append("mean: ").append(getMean()).append(endl);
    outBuffer.append("std dev: ").append(getStandardDeviation()).append(endl);
    
    outBuffer.append("variance: ").append(getVariance()).append(endl);
    outBuffer.append("sum: ").append(getSum()).append(endl);
    return outBuffer.toString();
  }
}

package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;












































public class SummaryStatistics
  implements StatisticalSummary, Serializable
{
  private static final long serialVersionUID = -2021321786743555871L;
  protected long n = 0L;
  

  protected SecondMoment secondMoment = new SecondMoment();
  

  protected Sum sum = new Sum();
  

  protected SumOfSquares sumsq = new SumOfSquares();
  

  protected Min min = new Min();
  

  protected Max max = new Max();
  

  protected SumOfLogs sumLog = new SumOfLogs();
  

  protected GeometricMean geoMean = new GeometricMean(sumLog);
  

  protected Mean mean = new Mean();
  

  protected Variance variance = new Variance();
  

  private StorelessUnivariateStatistic sumImpl = sum;
  

  private StorelessUnivariateStatistic sumsqImpl = sumsq;
  

  private StorelessUnivariateStatistic minImpl = min;
  

  private StorelessUnivariateStatistic maxImpl = max;
  

  private StorelessUnivariateStatistic sumLogImpl = sumLog;
  

  private StorelessUnivariateStatistic geoMeanImpl = geoMean;
  

  private StorelessUnivariateStatistic meanImpl = mean;
  

  private StorelessUnivariateStatistic varianceImpl = variance;
  




  public SummaryStatistics() {}
  




  public SummaryStatistics(SummaryStatistics original)
  {
    copy(original, this);
  }
  




  public StatisticalSummary getSummary()
  {
    return new StatisticalSummaryValues(getMean(), getVariance(), getN(), getMax(), getMin(), getSum());
  }
  




  public void addValue(double value)
  {
    sumImpl.increment(value);
    sumsqImpl.increment(value);
    minImpl.increment(value);
    maxImpl.increment(value);
    sumLogImpl.increment(value);
    secondMoment.increment(value);
    

    if (!(meanImpl instanceof Mean)) {
      meanImpl.increment(value);
    }
    if (!(varianceImpl instanceof Variance)) {
      varianceImpl.increment(value);
    }
    if (!(geoMeanImpl instanceof GeometricMean)) {
      geoMeanImpl.increment(value);
    }
    n += 1L;
  }
  



  public long getN()
  {
    return n;
  }
  



  public double getSum()
  {
    return sumImpl.getResult();
  }
  






  public double getSumsq()
  {
    return sumsqImpl.getResult();
  }
  






  public double getMean()
  {
    if (mean == meanImpl) {
      return new Mean(secondMoment).getResult();
    }
    return meanImpl.getResult();
  }
  







  public double getStandardDeviation()
  {
    double stdDev = NaN.0D;
    if (getN() > 0L) {
      if (getN() > 1L) {
        stdDev = FastMath.sqrt(getVariance());
      } else {
        stdDev = 0.0D;
      }
    }
    return stdDev;
  }
  






  public double getVariance()
  {
    if (varianceImpl == variance) {
      return new Variance(secondMoment).getResult();
    }
    return varianceImpl.getResult();
  }
  







  public double getMax()
  {
    return maxImpl.getResult();
  }
  






  public double getMin()
  {
    return minImpl.getResult();
  }
  






  public double getGeometricMean()
  {
    return geoMeanImpl.getResult();
  }
  







  public double getSumOfLogs()
  {
    return sumLogImpl.getResult();
  }
  










  public double getSecondMoment()
  {
    return secondMoment.getResult();
  }
  






  public String toString()
  {
    StringBuilder outBuffer = new StringBuilder();
    String endl = "\n";
    outBuffer.append("SummaryStatistics:").append(endl);
    outBuffer.append("n: ").append(getN()).append(endl);
    outBuffer.append("min: ").append(getMin()).append(endl);
    outBuffer.append("max: ").append(getMax()).append(endl);
    outBuffer.append("mean: ").append(getMean()).append(endl);
    outBuffer.append("geometric mean: ").append(getGeometricMean()).append(endl);
    
    outBuffer.append("variance: ").append(getVariance()).append(endl);
    outBuffer.append("sum of squares: ").append(getSumsq()).append(endl);
    outBuffer.append("standard deviation: ").append(getStandardDeviation()).append(endl);
    
    outBuffer.append("sum of logs: ").append(getSumOfLogs()).append(endl);
    return outBuffer.toString();
  }
  


  public void clear()
  {
    n = 0L;
    minImpl.clear();
    maxImpl.clear();
    sumImpl.clear();
    sumLogImpl.clear();
    sumsqImpl.clear();
    geoMeanImpl.clear();
    secondMoment.clear();
    if (meanImpl != mean) {
      meanImpl.clear();
    }
    if (varianceImpl != variance) {
      varianceImpl.clear();
    }
  }
  







  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof SummaryStatistics)) {
      return false;
    }
    SummaryStatistics stat = (SummaryStatistics)object;
    return (MathUtils.equalsIncludingNaN(stat.getGeometricMean(), getGeometricMean())) && (MathUtils.equalsIncludingNaN(stat.getMax(), getMax())) && (MathUtils.equalsIncludingNaN(stat.getMean(), getMean())) && (MathUtils.equalsIncludingNaN(stat.getMin(), getMin())) && (MathUtils.equalsIncludingNaN((float)stat.getN(), (float)getN())) && (MathUtils.equalsIncludingNaN(stat.getSum(), getSum())) && (MathUtils.equalsIncludingNaN(stat.getSumsq(), getSumsq())) && (MathUtils.equalsIncludingNaN(stat.getVariance(), getVariance()));
  }
  











  public int hashCode()
  {
    int result = 31 + MathUtils.hash(getGeometricMean());
    result = result * 31 + MathUtils.hash(getGeometricMean());
    result = result * 31 + MathUtils.hash(getMax());
    result = result * 31 + MathUtils.hash(getMean());
    result = result * 31 + MathUtils.hash(getMin());
    result = result * 31 + MathUtils.hash(getN());
    result = result * 31 + MathUtils.hash(getSum());
    result = result * 31 + MathUtils.hash(getSumsq());
    result = result * 31 + MathUtils.hash(getVariance());
    return result;
  }
  





  public StorelessUnivariateStatistic getSumImpl()
  {
    return sumImpl;
  }
  














  public void setSumImpl(StorelessUnivariateStatistic sumImpl)
  {
    checkEmpty();
    this.sumImpl = sumImpl;
  }
  




  public StorelessUnivariateStatistic getSumsqImpl()
  {
    return sumsqImpl;
  }
  














  public void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl)
  {
    checkEmpty();
    this.sumsqImpl = sumsqImpl;
  }
  




  public StorelessUnivariateStatistic getMinImpl()
  {
    return minImpl;
  }
  














  public void setMinImpl(StorelessUnivariateStatistic minImpl)
  {
    checkEmpty();
    this.minImpl = minImpl;
  }
  




  public StorelessUnivariateStatistic getMaxImpl()
  {
    return maxImpl;
  }
  














  public void setMaxImpl(StorelessUnivariateStatistic maxImpl)
  {
    checkEmpty();
    this.maxImpl = maxImpl;
  }
  




  public StorelessUnivariateStatistic getSumLogImpl()
  {
    return sumLogImpl;
  }
  














  public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl)
  {
    checkEmpty();
    this.sumLogImpl = sumLogImpl;
    geoMean.setSumLogImpl(sumLogImpl);
  }
  




  public StorelessUnivariateStatistic getGeoMeanImpl()
  {
    return geoMeanImpl;
  }
  














  public void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl)
  {
    checkEmpty();
    this.geoMeanImpl = geoMeanImpl;
  }
  




  public StorelessUnivariateStatistic getMeanImpl()
  {
    return meanImpl;
  }
  














  public void setMeanImpl(StorelessUnivariateStatistic meanImpl)
  {
    checkEmpty();
    this.meanImpl = meanImpl;
  }
  




  public StorelessUnivariateStatistic getVarianceImpl()
  {
    return varianceImpl;
  }
  














  public void setVarianceImpl(StorelessUnivariateStatistic varianceImpl)
  {
    checkEmpty();
    this.varianceImpl = varianceImpl;
  }
  


  private void checkEmpty()
  {
    if (n > 0L) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, new Object[] { Long.valueOf(n) });
    }
  }
  






  public SummaryStatistics copy()
  {
    SummaryStatistics result = new SummaryStatistics();
    copy(this, result);
    return result;
  }
  







  public static void copy(SummaryStatistics source, SummaryStatistics dest)
  {
    maxImpl = maxImpl.copy();
    meanImpl = meanImpl.copy();
    minImpl = minImpl.copy();
    sumImpl = sumImpl.copy();
    varianceImpl = varianceImpl.copy();
    sumLogImpl = sumLogImpl.copy();
    sumsqImpl = sumsqImpl.copy();
    if ((source.getGeoMeanImpl() instanceof GeometricMean))
    {
      geoMeanImpl = new GeometricMean((SumOfLogs)sumLogImpl);
    } else {
      geoMeanImpl = geoMeanImpl.copy();
    }
    SecondMoment.copy(secondMoment, secondMoment);
    n = n;
    


    if (geoMean == geoMeanImpl) {
      geoMean = ((GeometricMean)geoMeanImpl);
    } else {
      GeometricMean.copy(geoMean, geoMean);
    }
    if (max == maxImpl) {
      max = ((Max)maxImpl);
    } else {
      Max.copy(max, max);
    }
    if (mean == meanImpl) {
      mean = ((Mean)meanImpl);
    } else {
      Mean.copy(mean, mean);
    }
    if (min == minImpl) {
      min = ((Min)minImpl);
    } else {
      Min.copy(min, min);
    }
    if (sum == sumImpl) {
      sum = ((Sum)sumImpl);
    } else {
      Sum.copy(sum, sum);
    }
    if (variance == varianceImpl) {
      variance = ((Variance)varianceImpl);
    } else {
      Variance.copy(variance, variance);
    }
    if (sumLog == sumLogImpl) {
      sumLog = ((SumOfLogs)sumLogImpl);
    } else {
      SumOfLogs.copy(sumLog, sumLog);
    }
    if (sumsq == sumsqImpl) {
      sumsq = ((SumOfSquares)sumsqImpl);
    } else {
      SumOfSquares.copy(sumsq, sumsq);
    }
  }
}

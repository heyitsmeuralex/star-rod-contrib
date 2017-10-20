package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.util.FastMath;





























public class TTestImpl
  implements TTest
{
  @Deprecated
  private TDistribution distribution;
  
  public TTestImpl()
  {
    this(new TDistributionImpl(1.0D));
  }
  







  @Deprecated
  public TTestImpl(TDistribution t)
  {
    setDistribution(t);
  }
  



















  public double pairedT(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sample1);
    checkSampleData(sample2);
    double meanDifference = StatUtils.meanDifference(sample1, sample2);
    return t(meanDifference, 0.0D, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
  }
  


































  public double pairedTTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    double meanDifference = StatUtils.meanDifference(sample1, sample2);
    return tTest(meanDifference, 0.0D, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
  }
  

































  public boolean pairedTTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return pairedTTest(sample1, sample2) < alpha;
  }
  














  public double t(double mu, double[] observed)
    throws IllegalArgumentException
  {
    checkSampleData(observed);
    return t(StatUtils.mean(observed), mu, StatUtils.variance(observed), observed.length);
  }
  
















  public double t(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException
  {
    checkSampleData(sampleStats);
    return t(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
  }
  

































  public double homoscedasticT(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    checkSampleData(sample1);
    checkSampleData(sample2);
    return homoscedasticT(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
  }
  





























  public double t(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    checkSampleData(sample1);
    checkSampleData(sample2);
    return t(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
  }
  

































  public double t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException
  {
    checkSampleData(sampleStats1);
    checkSampleData(sampleStats2);
    return t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
  }
  





































  public double homoscedasticT(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException
  {
    checkSampleData(sampleStats1);
    checkSampleData(sampleStats2);
    return homoscedasticT(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
  }
  



























  public double tTest(double mu, double[] sample)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sample);
    return tTest(StatUtils.mean(sample), mu, StatUtils.variance(sample), sample.length);
  }
  



































  public boolean tTest(double mu, double[] sample, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return tTest(mu, sample) < alpha;
  }
  



























  public double tTest(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sampleStats);
    return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
  }
  





































  public boolean tTest(double mu, StatisticalSummary sampleStats, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return tTest(mu, sampleStats) < alpha;
  }
  



































  public double tTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sample1);
    checkSampleData(sample2);
    return tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
  }
  


































  public double homoscedasticTTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sample1);
    checkSampleData(sample2);
    return homoscedasticTTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
  }
  























































  public boolean tTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return tTest(sample1, sample2) < alpha;
  }
  



















































  public boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return homoscedasticTTest(sample1, sample2) < alpha;
  }
  

































  public double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sampleStats1);
    checkSampleData(sampleStats2);
    return tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
  }
  



































  public double homoscedasticTTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException, MathException
  {
    checkSampleData(sampleStats1);
    checkSampleData(sampleStats2);
    return homoscedasticTTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), sampleStats1.getN(), sampleStats2.getN());
  }
  
























































  public boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2, double alpha)
    throws IllegalArgumentException, MathException
  {
    checkSignificanceLevel(alpha);
    return tTest(sampleStats1, sampleStats2) < alpha;
  }
  










  protected double df(double v1, double v2, double n1, double n2)
  {
    return (v1 / n1 + v2 / n2) * (v1 / n1 + v2 / n2) / (v1 * v1 / (n1 * n1 * (n1 - 1.0D)) + v2 * v2 / (n2 * n2 * (n2 - 1.0D)));
  }
  










  protected double t(double m, double mu, double v, double n)
  {
    return (m - mu) / FastMath.sqrt(v / n);
  }
  













  protected double t(double m1, double m2, double v1, double v2, double n1, double n2)
  {
    return (m1 - m2) / FastMath.sqrt(v1 / n1 + v2 / n2);
  }
  












  protected double homoscedasticT(double m1, double m2, double v1, double v2, double n1, double n2)
  {
    double pooledVariance = ((n1 - 1.0D) * v1 + (n2 - 1.0D) * v2) / (n1 + n2 - 2.0D);
    return (m1 - m2) / FastMath.sqrt(pooledVariance * (1.0D / n1 + 1.0D / n2));
  }
  









  protected double tTest(double m, double mu, double v, double n)
    throws MathException
  {
    double t = FastMath.abs(t(m, mu, v, n));
    distribution.setDegreesOfFreedom(n - 1.0D);
    return 2.0D * distribution.cumulativeProbability(-t);
  }
  















  protected double tTest(double m1, double m2, double v1, double v2, double n1, double n2)
    throws MathException
  {
    double t = FastMath.abs(t(m1, m2, v1, v2, n1, n2));
    double degreesOfFreedom = 0.0D;
    degreesOfFreedom = df(v1, v2, n1, n2);
    distribution.setDegreesOfFreedom(degreesOfFreedom);
    return 2.0D * distribution.cumulativeProbability(-t);
  }
  















  protected double homoscedasticTTest(double m1, double m2, double v1, double v2, double n1, double n2)
    throws MathException
  {
    double t = FastMath.abs(homoscedasticT(m1, m2, v1, v2, n1, n2));
    double degreesOfFreedom = n1 + n2 - 2.0D;
    distribution.setDegreesOfFreedom(degreesOfFreedom);
    return 2.0D * distribution.cumulativeProbability(-t);
  }
  





  @Deprecated
  public void setDistribution(TDistribution value)
  {
    distribution = value;
  }
  



  private void checkSignificanceLevel(double alpha)
    throws IllegalArgumentException
  {
    if ((alpha <= 0.0D) || (alpha > 0.5D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Double.valueOf(0.0D), Double.valueOf(0.5D) });
    }
  }
  





  private void checkSampleData(double[] data)
    throws IllegalArgumentException
  {
    if ((data == null) || (data.length < 2)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, new Object[] { Integer.valueOf(data == null ? 0 : data.length) });
    }
  }
  





  private void checkSampleData(StatisticalSummary stat)
    throws IllegalArgumentException
  {
    if ((stat == null) || (stat.getN() < 2L)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, new Object[] { Long.valueOf(stat == null ? 0L : stat.getN()) });
    }
  }
}

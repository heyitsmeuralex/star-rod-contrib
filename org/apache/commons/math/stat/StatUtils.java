package org.apache.commons.math.stat;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.apache.commons.math.stat.descriptive.summary.Product;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
























public final class StatUtils
{
  private static final UnivariateStatistic SUM = new Sum();
  

  private static final UnivariateStatistic SUM_OF_SQUARES = new SumOfSquares();
  

  private static final UnivariateStatistic PRODUCT = new Product();
  

  private static final UnivariateStatistic SUM_OF_LOGS = new SumOfLogs();
  

  private static final UnivariateStatistic MIN = new Min();
  

  private static final UnivariateStatistic MAX = new Max();
  

  private static final UnivariateStatistic MEAN = new Mean();
  

  private static final Variance VARIANCE = new Variance();
  

  private static final Percentile PERCENTILE = new Percentile();
  

  private static final GeometricMean GEOMETRIC_MEAN = new GeometricMean();
  








  private StatUtils() {}
  







  public static double sum(double[] values)
  {
    return SUM.evaluate(values);
  }
  














  public static double sum(double[] values, int begin, int length)
  {
    return SUM.evaluate(values, begin, length);
  }
  










  public static double sumSq(double[] values)
  {
    return SUM_OF_SQUARES.evaluate(values);
  }
  














  public static double sumSq(double[] values, int begin, int length)
  {
    return SUM_OF_SQUARES.evaluate(values, begin, length);
  }
  









  public static double product(double[] values)
  {
    return PRODUCT.evaluate(values);
  }
  














  public static double product(double[] values, int begin, int length)
  {
    return PRODUCT.evaluate(values, begin, length);
  }
  













  public static double sumLog(double[] values)
  {
    return SUM_OF_LOGS.evaluate(values);
  }
  


















  public static double sumLog(double[] values, int begin, int length)
  {
    return SUM_OF_LOGS.evaluate(values, begin, length);
  }
  












  public static double mean(double[] values)
  {
    return MEAN.evaluate(values);
  }
  

















  public static double mean(double[] values, int begin, int length)
  {
    return MEAN.evaluate(values, begin, length);
  }
  












  public static double geometricMean(double[] values)
  {
    return GEOMETRIC_MEAN.evaluate(values);
  }
  

















  public static double geometricMean(double[] values, int begin, int length)
  {
    return GEOMETRIC_MEAN.evaluate(values, begin, length);
  }
  















  public static double variance(double[] values)
  {
    return VARIANCE.evaluate(values);
  }
  




















  public static double variance(double[] values, int begin, int length)
  {
    return VARIANCE.evaluate(values, begin, length);
  }
  


























  public static double variance(double[] values, double mean, int begin, int length)
  {
    return VARIANCE.evaluate(values, mean, begin, length);
  }
  





















  public static double variance(double[] values, double mean)
  {
    return VARIANCE.evaluate(values, mean);
  }
  
















  public static double max(double[] values)
  {
    return MAX.evaluate(values);
  }
  






















  public static double max(double[] values, int begin, int length)
  {
    return MAX.evaluate(values, begin, length);
  }
  
















  public static double min(double[] values)
  {
    return MIN.evaluate(values);
  }
  






















  public static double min(double[] values, int begin, int length)
  {
    return MIN.evaluate(values, begin, length);
  }
  






















  public static double percentile(double[] values, double p)
  {
    return PERCENTILE.evaluate(values, p);
  }
  



























  public static double percentile(double[] values, int begin, int length, double p)
  {
    return PERCENTILE.evaluate(values, begin, length, p);
  }
  









  public static double sumDifference(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    int n = sample1.length;
    if (n != sample2.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(n), Integer.valueOf(sample2.length) });
    }
    
    if (n < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(sample2.length), Integer.valueOf(1) });
    }
    
    double result = 0.0D;
    for (int i = 0; i < n; i++) {
      result += sample1[i] - sample2[i];
    }
    return result;
  }
  









  public static double meanDifference(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    return sumDifference(sample1, sample2) / sample1.length;
  }
  











  public static double varianceDifference(double[] sample1, double[] sample2, double meanDifference)
    throws IllegalArgumentException
  {
    double sum1 = 0.0D;
    double sum2 = 0.0D;
    double diff = 0.0D;
    int n = sample1.length;
    if (n != sample2.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(n), Integer.valueOf(sample2.length) });
    }
    
    if (n < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(n), Integer.valueOf(2) });
    }
    
    for (int i = 0; i < n; i++) {
      diff = sample1[i] - sample2[i];
      sum1 += (diff - meanDifference) * (diff - meanDifference);
      sum2 += diff - meanDifference;
    }
    return (sum1 - sum2 * sum2 / n) / (n - 1);
  }
  







  public static double[] normalize(double[] sample)
  {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    

    for (int i = 0; i < sample.length; i++) {
      stats.addValue(sample[i]);
    }
    

    double mean = stats.getMean();
    double standardDeviation = stats.getStandardDeviation();
    

    double[] standardizedSample = new double[sample.length];
    
    for (int i = 0; i < sample.length; i++)
    {
      standardizedSample[i] = ((sample[i] - mean) / standardDeviation);
    }
    return standardizedSample;
  }
}

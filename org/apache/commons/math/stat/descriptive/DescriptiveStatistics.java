package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Skewness;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.ResizableDoubleArray;















































public class DescriptiveStatistics
  implements StatisticalSummary, Serializable
{
  public static final int INFINITE_WINDOW = -1;
  private static final long serialVersionUID = 4133067267405273064L;
  private static final String SET_QUANTILE_METHOD_NAME = "setQuantile";
  protected int windowSize = -1;
  



  protected ResizableDoubleArray eDA = new ResizableDoubleArray();
  

  private UnivariateStatistic meanImpl = new Mean();
  

  private UnivariateStatistic geometricMeanImpl = new GeometricMean();
  

  private UnivariateStatistic kurtosisImpl = new Kurtosis();
  

  private UnivariateStatistic maxImpl = new Max();
  

  private UnivariateStatistic minImpl = new Min();
  

  private UnivariateStatistic percentileImpl = new Percentile();
  

  private UnivariateStatistic skewnessImpl = new Skewness();
  

  private UnivariateStatistic varianceImpl = new Variance();
  

  private UnivariateStatistic sumsqImpl = new SumOfSquares();
  

  private UnivariateStatistic sumImpl = new Sum();
  




  public DescriptiveStatistics() {}
  




  public DescriptiveStatistics(int window)
  {
    setWindowSize(window);
  }
  







  public DescriptiveStatistics(double[] initialDoubleArray)
  {
    if (initialDoubleArray != null) {
      eDA = new ResizableDoubleArray(initialDoubleArray);
    }
  }
  





  public DescriptiveStatistics(DescriptiveStatistics original)
  {
    copy(original, this);
  }
  







  public void addValue(double v)
  {
    if (windowSize != -1) {
      if (getN() == windowSize) {
        eDA.addElementRolling(v);
      } else if (getN() < windowSize) {
        eDA.addElement(v);
      }
    } else {
      eDA.addElement(v);
    }
  }
  


  public void removeMostRecentValue()
  {
    eDA.discardMostRecentElements(1);
  }
  






  public double replaceMostRecentValue(double v)
  {
    return eDA.substituteMostRecentElement(v);
  }
  




  public double getMean()
  {
    return apply(meanImpl);
  }
  





  public double getGeometricMean()
  {
    return apply(geometricMeanImpl);
  }
  




  public double getVariance()
  {
    return apply(varianceImpl);
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
  





  public double getSkewness()
  {
    return apply(skewnessImpl);
  }
  





  public double getKurtosis()
  {
    return apply(kurtosisImpl);
  }
  



  public double getMax()
  {
    return apply(maxImpl);
  }
  



  public double getMin()
  {
    return apply(minImpl);
  }
  



  public long getN()
  {
    return eDA.getNumElements();
  }
  



  public double getSum()
  {
    return apply(sumImpl);
  }
  




  public double getSumsq()
  {
    return apply(sumsqImpl);
  }
  


  public void clear()
  {
    eDA.clear();
  }
  






  public int getWindowSize()
  {
    return windowSize;
  }
  








  public void setWindowSize(int windowSize)
  {
    if ((windowSize < 1) && 
      (windowSize != -1)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_WINDOW_SIZE, new Object[] { Integer.valueOf(windowSize) });
    }
    


    this.windowSize = windowSize;
    



    if ((windowSize != -1) && (windowSize < eDA.getNumElements())) {
      eDA.discardFrontElements(eDA.getNumElements() - windowSize);
    }
  }
  








  public double[] getValues()
  {
    return eDA.getElements();
  }
  







  public double[] getSortedValues()
  {
    double[] sort = getValues();
    Arrays.sort(sort);
    return sort;
  }
  




  public double getElement(int index)
  {
    return eDA.getElement(index);
  }
  


















  public double getPercentile(double p)
  {
    if ((percentileImpl instanceof Percentile)) {
      ((Percentile)percentileImpl).setQuantile(p);
    } else {
      try {
        percentileImpl.getClass().getMethod("setQuantile", new Class[] { Double.TYPE }).invoke(percentileImpl, new Object[] { Double.valueOf(p) });
      }
      catch (NoSuchMethodException e1)
      {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, new Object[] { percentileImpl.getClass().getName(), "setQuantile" });
      }
      catch (IllegalAccessException e2)
      {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, new Object[] { "setQuantile", percentileImpl.getClass().getName() });
      }
      catch (InvocationTargetException e3)
      {
        throw MathRuntimeException.createIllegalArgumentException(e3.getCause());
      }
    }
    return apply(percentileImpl);
  }
  







  public String toString()
  {
    StringBuilder outBuffer = new StringBuilder();
    String endl = "\n";
    outBuffer.append("DescriptiveStatistics:").append(endl);
    outBuffer.append("n: ").append(getN()).append(endl);
    outBuffer.append("min: ").append(getMin()).append(endl);
    outBuffer.append("max: ").append(getMax()).append(endl);
    outBuffer.append("mean: ").append(getMean()).append(endl);
    outBuffer.append("std dev: ").append(getStandardDeviation()).append(endl);
    
    outBuffer.append("median: ").append(getPercentile(50.0D)).append(endl);
    outBuffer.append("skewness: ").append(getSkewness()).append(endl);
    outBuffer.append("kurtosis: ").append(getKurtosis()).append(endl);
    return outBuffer.toString();
  }
  




  public double apply(UnivariateStatistic stat)
  {
    return stat.evaluate(eDA.getInternalValues(), eDA.start(), eDA.getNumElements());
  }
  







  public synchronized UnivariateStatistic getMeanImpl()
  {
    return meanImpl;
  }
  






  public synchronized void setMeanImpl(UnivariateStatistic meanImpl)
  {
    this.meanImpl = meanImpl;
  }
  





  public synchronized UnivariateStatistic getGeometricMeanImpl()
  {
    return geometricMeanImpl;
  }
  







  public synchronized void setGeometricMeanImpl(UnivariateStatistic geometricMeanImpl)
  {
    this.geometricMeanImpl = geometricMeanImpl;
  }
  





  public synchronized UnivariateStatistic getKurtosisImpl()
  {
    return kurtosisImpl;
  }
  






  public synchronized void setKurtosisImpl(UnivariateStatistic kurtosisImpl)
  {
    this.kurtosisImpl = kurtosisImpl;
  }
  





  public synchronized UnivariateStatistic getMaxImpl()
  {
    return maxImpl;
  }
  






  public synchronized void setMaxImpl(UnivariateStatistic maxImpl)
  {
    this.maxImpl = maxImpl;
  }
  





  public synchronized UnivariateStatistic getMinImpl()
  {
    return minImpl;
  }
  






  public synchronized void setMinImpl(UnivariateStatistic minImpl)
  {
    this.minImpl = minImpl;
  }
  





  public synchronized UnivariateStatistic getPercentileImpl()
  {
    return percentileImpl;
  }
  










  public synchronized void setPercentileImpl(UnivariateStatistic percentileImpl)
  {
    try
    {
      percentileImpl.getClass().getMethod("setQuantile", new Class[] { Double.TYPE }).invoke(percentileImpl, new Object[] { Double.valueOf(50.0D) });
    }
    catch (NoSuchMethodException e1)
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, new Object[] { percentileImpl.getClass().getName(), "setQuantile" });
    }
    catch (IllegalAccessException e2)
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, new Object[] { "setQuantile", percentileImpl.getClass().getName() });
    }
    catch (InvocationTargetException e3)
    {
      throw MathRuntimeException.createIllegalArgumentException(e3.getCause());
    }
    this.percentileImpl = percentileImpl;
  }
  





  public synchronized UnivariateStatistic getSkewnessImpl()
  {
    return skewnessImpl;
  }
  







  public synchronized void setSkewnessImpl(UnivariateStatistic skewnessImpl)
  {
    this.skewnessImpl = skewnessImpl;
  }
  





  public synchronized UnivariateStatistic getVarianceImpl()
  {
    return varianceImpl;
  }
  







  public synchronized void setVarianceImpl(UnivariateStatistic varianceImpl)
  {
    this.varianceImpl = varianceImpl;
  }
  





  public synchronized UnivariateStatistic getSumsqImpl()
  {
    return sumsqImpl;
  }
  






  public synchronized void setSumsqImpl(UnivariateStatistic sumsqImpl)
  {
    this.sumsqImpl = sumsqImpl;
  }
  





  public synchronized UnivariateStatistic getSumImpl()
  {
    return sumImpl;
  }
  






  public synchronized void setSumImpl(UnivariateStatistic sumImpl)
  {
    this.sumImpl = sumImpl;
  }
  




  public DescriptiveStatistics copy()
  {
    DescriptiveStatistics result = new DescriptiveStatistics();
    copy(this, result);
    return result;
  }
  








  public static void copy(DescriptiveStatistics source, DescriptiveStatistics dest)
  {
    eDA = eDA.copy();
    windowSize = windowSize;
    

    maxImpl = maxImpl.copy();
    meanImpl = meanImpl.copy();
    minImpl = minImpl.copy();
    sumImpl = sumImpl.copy();
    varianceImpl = varianceImpl.copy();
    sumsqImpl = sumsqImpl.copy();
    geometricMeanImpl = geometricMeanImpl.copy();
    kurtosisImpl = kurtosisImpl;
    skewnessImpl = skewnessImpl;
    percentileImpl = percentileImpl;
  }
}

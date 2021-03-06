package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

























public class ChiSquareTestImpl
  implements UnknownDistributionChiSquareTest
{
  private ChiSquaredDistribution distribution;
  
  public ChiSquareTestImpl()
  {
    this(new ChiSquaredDistributionImpl(1.0D));
  }
  






  public ChiSquareTestImpl(ChiSquaredDistribution x)
  {
    setDistribution(x);
  }
  










  public double chiSquare(double[] expected, long[] observed)
    throws IllegalArgumentException
  {
    if (expected.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(expected.length), Integer.valueOf(2) });
    }
    
    if (expected.length != observed.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(expected.length), Integer.valueOf(observed.length) });
    }
    
    checkPositive(expected);
    checkNonNegative(observed);
    double sumExpected = 0.0D;
    double sumObserved = 0.0D;
    for (int i = 0; i < observed.length; i++) {
      sumExpected += expected[i];
      sumObserved += observed[i];
    }
    double ratio = 1.0D;
    boolean rescale = false;
    if (FastMath.abs(sumExpected - sumObserved) > 1.0E-5D) {
      ratio = sumObserved / sumExpected;
      rescale = true;
    }
    double sumSq = 0.0D;
    for (int i = 0; i < observed.length; i++) {
      if (rescale) {
        double dev = observed[i] - ratio * expected[i];
        sumSq += dev * dev / (ratio * expected[i]);
      } else {
        double dev = observed[i] - expected[i];
        sumSq += dev * dev / expected[i];
      }
    }
    return sumSq;
  }
  











  public double chiSquareTest(double[] expected, long[] observed)
    throws IllegalArgumentException, MathException
  {
    distribution.setDegreesOfFreedom(expected.length - 1.0D);
    return 1.0D - distribution.cumulativeProbability(chiSquare(expected, observed));
  }
  














  public boolean chiSquareTest(double[] expected, long[] observed, double alpha)
    throws IllegalArgumentException, MathException
  {
    if ((alpha <= 0.0D) || (alpha > 0.5D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Integer.valueOf(0), Double.valueOf(0.5D) });
    }
    

    return chiSquareTest(expected, observed) < alpha;
  }
  




  public double chiSquare(long[][] counts)
    throws IllegalArgumentException
  {
    checkArray(counts);
    int nRows = counts.length;
    int nCols = counts[0].length;
    

    double[] rowSum = new double[nRows];
    double[] colSum = new double[nCols];
    double total = 0.0D;
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        rowSum[row] += counts[row][col];
        colSum[col] += counts[row][col];
        total += counts[row][col];
      }
    }
    

    double sumSq = 0.0D;
    double expected = 0.0D;
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        expected = rowSum[row] * colSum[col] / total;
        sumSq += (counts[row][col] - expected) * (counts[row][col] - expected) / expected;
      }
    }
    
    return sumSq;
  }
  





  public double chiSquareTest(long[][] counts)
    throws IllegalArgumentException, MathException
  {
    checkArray(counts);
    double df = (counts.length - 1.0D) * (counts[0].length - 1.0D);
    distribution.setDegreesOfFreedom(df);
    return 1.0D - distribution.cumulativeProbability(chiSquare(counts));
  }
  







  public boolean chiSquareTest(long[][] counts, double alpha)
    throws IllegalArgumentException, MathException
  {
    if ((alpha <= 0.0D) || (alpha > 0.5D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Double.valueOf(0.0D), Double.valueOf(0.5D) });
    }
    

    return chiSquareTest(counts) < alpha;
  }
  








  public double chiSquareDataSetsComparison(long[] observed1, long[] observed2)
    throws IllegalArgumentException
  {
    if (observed1.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(observed1.length), Integer.valueOf(2) });
    }
    
    if (observed1.length != observed2.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(observed1.length), Integer.valueOf(observed2.length) });
    }
    



    checkNonNegative(observed1);
    checkNonNegative(observed2);
    

    long countSum1 = 0L;
    long countSum2 = 0L;
    boolean unequalCounts = false;
    double weight = 0.0D;
    for (int i = 0; i < observed1.length; i++) {
      countSum1 += observed1[i];
      countSum2 += observed2[i];
    }
    
    if (countSum1 == 0L) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_ALL_ZERO, new Object[] { Integer.valueOf(1) });
    }
    
    if (countSum2 == 0L) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_ALL_ZERO, new Object[] { Integer.valueOf(2) });
    }
    

    unequalCounts = countSum1 != countSum2;
    if (unequalCounts) {
      weight = FastMath.sqrt(countSum1 / countSum2);
    }
    
    double sumSq = 0.0D;
    double dev = 0.0D;
    double obs1 = 0.0D;
    double obs2 = 0.0D;
    for (int i = 0; i < observed1.length; i++) {
      if ((observed1[i] == 0L) && (observed2[i] == 0L)) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, new Object[] { Integer.valueOf(i) });
      }
      
      obs1 = observed1[i];
      obs2 = observed2[i];
      if (unequalCounts) {
        dev = obs1 / weight - obs2 * weight;
      } else {
        dev = obs1 - obs2;
      }
      sumSq += dev * dev / (obs1 + obs2);
    }
    
    return sumSq;
  }
  







  public double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2)
    throws IllegalArgumentException, MathException
  {
    distribution.setDegreesOfFreedom(observed1.length - 1.0D);
    return 1.0D - distribution.cumulativeProbability(chiSquareDataSetsComparison(observed1, observed2));
  }
  










  public boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2, double alpha)
    throws IllegalArgumentException, MathException
  {
    if ((alpha <= 0.0D) || (alpha > 0.5D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Double.valueOf(0.0D), Double.valueOf(0.5D) });
    }
    

    return chiSquareTestDataSetsComparison(observed1, observed2) < alpha;
  }
  







  private void checkArray(long[][] in)
    throws IllegalArgumentException
  {
    if (in.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(in.length), Integer.valueOf(2) });
    }
    

    if (in[0].length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(in[0].length), Integer.valueOf(2) });
    }
    

    checkRectangular(in);
    checkNonNegative(in);
  }
  









  private void checkRectangular(long[][] in)
  {
    for (int i = 1; i < in.length; i++) {
      if (in[i].length != in[0].length) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(in[i].length), Integer.valueOf(in[0].length) });
      }
    }
  }
  






  private void checkPositive(double[] in)
    throws IllegalArgumentException
  {
    for (int i = 0; i < in.length; i++) {
      if (in[i] <= 0.0D) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_ELEMENT_AT_INDEX, new Object[] { Integer.valueOf(i), Double.valueOf(in[i]) });
      }
    }
  }
  






  private void checkNonNegative(long[] in)
    throws IllegalArgumentException
  {
    for (int i = 0; i < in.length; i++) {
      if (in[i] < 0L) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX, new Object[] { Integer.valueOf(i), Long.valueOf(in[i]) });
      }
    }
  }
  






  private void checkNonNegative(long[][] in)
    throws IllegalArgumentException
  {
    for (int i = 0; i < in.length; i++) {
      for (int j = 0; j < in[i].length; j++) {
        if (in[i][j] < 0L) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_2D_INDEX, new Object[] { Integer.valueOf(i), Integer.valueOf(j), Long.valueOf(in[i][j]) });
        }
      }
    }
  }
  








  public void setDistribution(ChiSquaredDistribution value)
  {
    distribution = value;
  }
}

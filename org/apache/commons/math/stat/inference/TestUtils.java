package org.apache.commons.math.stat.inference;

import java.util.Collection;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;






























public class TestUtils
{
  private static TTest tTest = new TTestImpl();
  

  private static ChiSquareTest chiSquareTest = new ChiSquareTestImpl();
  


  private static UnknownDistributionChiSquareTest unknownDistributionChiSquareTest = new ChiSquareTestImpl();
  


  private static OneWayAnova oneWayAnova = new OneWayAnovaImpl();
  






  protected TestUtils() {}
  






  @Deprecated
  public static void setChiSquareTest(TTest chiSquareTest)
  {
    tTest = chiSquareTest;
  }
  





  @Deprecated
  public static TTest getTTest()
  {
    return tTest;
  }
  






  @Deprecated
  public static void setChiSquareTest(ChiSquareTest chiSquareTest)
  {
    chiSquareTest = chiSquareTest;
  }
  





  @Deprecated
  public static ChiSquareTest getChiSquareTest()
  {
    return chiSquareTest;
  }
  






  @Deprecated
  public static void setUnknownDistributionChiSquareTest(UnknownDistributionChiSquareTest unknownDistributionChiSquareTest)
  {
    unknownDistributionChiSquareTest = unknownDistributionChiSquareTest;
  }
  





  @Deprecated
  public static UnknownDistributionChiSquareTest getUnknownDistributionChiSquareTest()
  {
    return unknownDistributionChiSquareTest;
  }
  






  @Deprecated
  public static void setOneWayAnova(OneWayAnova oneWayAnova)
  {
    oneWayAnova = oneWayAnova;
  }
  






  @Deprecated
  public static OneWayAnova getOneWayAnova()
  {
    return oneWayAnova;
  }
  





  public static double homoscedasticT(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    return tTest.homoscedasticT(sample1, sample2);
  }
  



  public static double homoscedasticT(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException
  {
    return tTest.homoscedasticT(sampleStats1, sampleStats2);
  }
  



  public static boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.homoscedasticTTest(sample1, sample2, alpha);
  }
  


  public static double homoscedasticTTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    return tTest.homoscedasticTTest(sample1, sample2);
  }
  



  public static double homoscedasticTTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException, MathException
  {
    return tTest.homoscedasticTTest(sampleStats1, sampleStats2);
  }
  


  public static double pairedT(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    return tTest.pairedT(sample1, sample2);
  }
  



  public static boolean pairedTTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.pairedTTest(sample1, sample2, alpha);
  }
  


  public static double pairedTTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    return tTest.pairedTTest(sample1, sample2);
  }
  


  public static double t(double mu, double[] observed)
    throws IllegalArgumentException
  {
    return tTest.t(mu, observed);
  }
  


  public static double t(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException
  {
    return tTest.t(mu, sampleStats);
  }
  


  public static double t(double[] sample1, double[] sample2)
    throws IllegalArgumentException
  {
    return tTest.t(sample1, sample2);
  }
  



  public static double t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException
  {
    return tTest.t(sampleStats1, sampleStats2);
  }
  


  public static boolean tTest(double mu, double[] sample, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(mu, sample, alpha);
  }
  


  public static double tTest(double mu, double[] sample)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(mu, sample);
  }
  



  public static boolean tTest(double mu, StatisticalSummary sampleStats, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(mu, sampleStats, alpha);
  }
  


  public static double tTest(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(mu, sampleStats);
  }
  


  public static boolean tTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(sample1, sample2, alpha);
  }
  


  public static double tTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(sample1, sample2);
  }
  



  public static boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2, double alpha)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(sampleStats1, sampleStats2, alpha);
  }
  



  public static double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException, MathException
  {
    return tTest.tTest(sampleStats1, sampleStats2);
  }
  


  public static double chiSquare(double[] expected, long[] observed)
    throws IllegalArgumentException
  {
    return chiSquareTest.chiSquare(expected, observed);
  }
  


  public static double chiSquare(long[][] counts)
    throws IllegalArgumentException
  {
    return chiSquareTest.chiSquare(counts);
  }
  



  public static boolean chiSquareTest(double[] expected, long[] observed, double alpha)
    throws IllegalArgumentException, MathException
  {
    return chiSquareTest.chiSquareTest(expected, observed, alpha);
  }
  


  public static double chiSquareTest(double[] expected, long[] observed)
    throws IllegalArgumentException, MathException
  {
    return chiSquareTest.chiSquareTest(expected, observed);
  }
  


  public static boolean chiSquareTest(long[][] counts, double alpha)
    throws IllegalArgumentException, MathException
  {
    return chiSquareTest.chiSquareTest(counts, alpha);
  }
  


  public static double chiSquareTest(long[][] counts)
    throws IllegalArgumentException, MathException
  {
    return chiSquareTest.chiSquareTest(counts);
  }
  




  public static double chiSquareDataSetsComparison(long[] observed1, long[] observed2)
    throws IllegalArgumentException
  {
    return unknownDistributionChiSquareTest.chiSquareDataSetsComparison(observed1, observed2);
  }
  




  public static double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2)
    throws IllegalArgumentException, MathException
  {
    return unknownDistributionChiSquareTest.chiSquareTestDataSetsComparison(observed1, observed2);
  }
  






  public static boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2, double alpha)
    throws IllegalArgumentException, MathException
  {
    return unknownDistributionChiSquareTest.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
  }
  




  public static double oneWayAnovaFValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException
  {
    return oneWayAnova.anovaFValue(categoryData);
  }
  




  public static double oneWayAnovaPValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException
  {
    return oneWayAnova.anovaPValue(categoryData);
  }
  




  public static boolean oneWayAnovaTest(Collection<double[]> categoryData, double alpha)
    throws IllegalArgumentException, MathException
  {
    return oneWayAnova.anovaTest(categoryData, alpha);
  }
}

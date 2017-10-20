package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

public abstract interface TTest
{
  public abstract double pairedT(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract double pairedTTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean pairedTTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble)
    throws IllegalArgumentException, MathException;
  
  public abstract double t(double paramDouble, double[] paramArrayOfDouble)
    throws IllegalArgumentException;
  
  public abstract double t(double paramDouble, StatisticalSummary paramStatisticalSummary)
    throws IllegalArgumentException;
  
  public abstract double homoscedasticT(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException;
  
  public abstract double t(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException;
  
  public abstract double t(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2)
    throws IllegalArgumentException;
  
  public abstract double homoscedasticT(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2)
    throws IllegalArgumentException;
  
  public abstract double tTest(double paramDouble, double[] paramArrayOfDouble)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean tTest(double paramDouble1, double[] paramArrayOfDouble, double paramDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract double tTest(double paramDouble, StatisticalSummary paramStatisticalSummary)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean tTest(double paramDouble1, StatisticalSummary paramStatisticalSummary, double paramDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract double tTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract double homoscedasticTTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean tTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean homoscedasticTTest(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble)
    throws IllegalArgumentException, MathException;
  
  public abstract double tTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2)
    throws IllegalArgumentException, MathException;
  
  public abstract double homoscedasticTTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean tTest(StatisticalSummary paramStatisticalSummary1, StatisticalSummary paramStatisticalSummary2, double paramDouble)
    throws IllegalArgumentException, MathException;
}

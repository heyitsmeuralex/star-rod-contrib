package org.apache.commons.math.stat.inference;

import org.apache.commons.math.MathException;

public abstract interface UnknownDistributionChiSquareTest
  extends ChiSquareTest
{
  public abstract double chiSquareDataSetsComparison(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
    throws IllegalArgumentException;
  
  public abstract double chiSquareTestDataSetsComparison(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean chiSquareTestDataSetsComparison(long[] paramArrayOfLong1, long[] paramArrayOfLong2, double paramDouble)
    throws IllegalArgumentException, MathException;
}

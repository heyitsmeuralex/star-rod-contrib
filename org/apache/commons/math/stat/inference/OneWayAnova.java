package org.apache.commons.math.stat.inference;

import java.util.Collection;
import org.apache.commons.math.MathException;

public abstract interface OneWayAnova
{
  public abstract double anovaFValue(Collection<double[]> paramCollection)
    throws IllegalArgumentException, MathException;
  
  public abstract double anovaPValue(Collection<double[]> paramCollection)
    throws IllegalArgumentException, MathException;
  
  public abstract boolean anovaTest(Collection<double[]> paramCollection, double paramDouble)
    throws IllegalArgumentException, MathException;
}

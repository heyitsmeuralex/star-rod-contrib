package org.apache.commons.math.linear;

import java.util.Iterator;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;



























public abstract interface RealVector
{
  public abstract RealVector mapToSelf(UnivariateRealFunction paramUnivariateRealFunction)
    throws FunctionEvaluationException;
  
  public abstract RealVector map(UnivariateRealFunction paramUnivariateRealFunction)
    throws FunctionEvaluationException;
  
  public abstract Iterator<Entry> iterator();
  
  public abstract Iterator<Entry> sparseIterator();
  
  public abstract RealVector copy();
  
  public abstract RealVector add(RealVector paramRealVector);
  
  public abstract RealVector add(double[] paramArrayOfDouble);
  
  public abstract RealVector subtract(RealVector paramRealVector);
  
  public abstract RealVector subtract(double[] paramArrayOfDouble);
  
  public abstract RealVector mapAdd(double paramDouble);
  
  public abstract RealVector mapAddToSelf(double paramDouble);
  
  public abstract RealVector mapSubtract(double paramDouble);
  
  public abstract RealVector mapSubtractToSelf(double paramDouble);
  
  public abstract RealVector mapMultiply(double paramDouble);
  
  public abstract RealVector mapMultiplyToSelf(double paramDouble);
  
  public abstract RealVector mapDivide(double paramDouble);
  
  public abstract RealVector mapDivideToSelf(double paramDouble);
  
  @Deprecated
  public abstract RealVector mapPow(double paramDouble);
  
  @Deprecated
  public abstract RealVector mapPowToSelf(double paramDouble);
  
  @Deprecated
  public abstract RealVector mapExp();
  
  @Deprecated
  public abstract RealVector mapExpToSelf();
  
  @Deprecated
  public abstract RealVector mapExpm1();
  
  @Deprecated
  public abstract RealVector mapExpm1ToSelf();
  
  @Deprecated
  public abstract RealVector mapLog();
  
  @Deprecated
  public abstract RealVector mapLogToSelf();
  
  public static abstract class Entry
  {
    public Entry() {}
    
    public abstract double getValue();
    
    public abstract void setValue(double paramDouble);
    
    public int getIndex()
    {
      return index;
    }
    

    private int index;
    
    public void setIndex(int index)
    {
      this.index = index;
    }
  }
  
  @Deprecated
  public abstract RealVector mapLog10();
  
  @Deprecated
  public abstract RealVector mapLog10ToSelf();
  
  @Deprecated
  public abstract RealVector mapLog1p();
  
  @Deprecated
  public abstract RealVector mapLog1pToSelf();
  
  @Deprecated
  public abstract RealVector mapCosh();
  
  @Deprecated
  public abstract RealVector mapCoshToSelf();
  
  @Deprecated
  public abstract RealVector mapSinh();
  
  @Deprecated
  public abstract RealVector mapSinhToSelf();
  
  @Deprecated
  public abstract RealVector mapTanh();
  
  @Deprecated
  public abstract RealVector mapTanhToSelf();
  
  @Deprecated
  public abstract RealVector mapCos();
  
  @Deprecated
  public abstract RealVector mapCosToSelf();
  
  @Deprecated
  public abstract RealVector mapSin();
  
  @Deprecated
  public abstract RealVector mapSinToSelf();
  
  @Deprecated
  public abstract RealVector mapTan();
  
  @Deprecated
  public abstract RealVector mapTanToSelf();
  
  @Deprecated
  public abstract RealVector mapAcos();
  
  @Deprecated
  public abstract RealVector mapAcosToSelf();
  
  @Deprecated
  public abstract RealVector mapAsin();
  
  @Deprecated
  public abstract RealVector mapAsinToSelf();
  
  @Deprecated
  public abstract RealVector mapAtan();
  
  @Deprecated
  public abstract RealVector mapAtanToSelf();
  
  @Deprecated
  public abstract RealVector mapInv();
  
  @Deprecated
  public abstract RealVector mapInvToSelf();
  
  @Deprecated
  public abstract RealVector mapAbs();
  
  @Deprecated
  public abstract RealVector mapAbsToSelf();
  
  @Deprecated
  public abstract RealVector mapSqrt();
  
  @Deprecated
  public abstract RealVector mapSqrtToSelf();
  
  @Deprecated
  public abstract RealVector mapCbrt();
  
  @Deprecated
  public abstract RealVector mapCbrtToSelf();
  
  @Deprecated
  public abstract RealVector mapCeil();
  
  @Deprecated
  public abstract RealVector mapCeilToSelf();
  
  @Deprecated
  public abstract RealVector mapFloor();
  
  @Deprecated
  public abstract RealVector mapFloorToSelf();
  
  @Deprecated
  public abstract RealVector mapRint();
  
  @Deprecated
  public abstract RealVector mapRintToSelf();
  
  @Deprecated
  public abstract RealVector mapSignum();
  
  @Deprecated
  public abstract RealVector mapSignumToSelf();
  
  @Deprecated
  public abstract RealVector mapUlp();
  
  @Deprecated
  public abstract RealVector mapUlpToSelf();
  
  public abstract RealVector ebeMultiply(RealVector paramRealVector);
  
  public abstract RealVector ebeMultiply(double[] paramArrayOfDouble);
  
  public abstract RealVector ebeDivide(RealVector paramRealVector);
  
  public abstract RealVector ebeDivide(double[] paramArrayOfDouble);
  
  public abstract double[] getData();
  
  public abstract double dotProduct(RealVector paramRealVector);
  
  public abstract double dotProduct(double[] paramArrayOfDouble);
  
  public abstract double getNorm();
  
  public abstract double getL1Norm();
  
  public abstract double getLInfNorm();
  
  public abstract double getDistance(RealVector paramRealVector);
  
  public abstract double getDistance(double[] paramArrayOfDouble);
  
  public abstract double getL1Distance(RealVector paramRealVector);
  
  public abstract double getL1Distance(double[] paramArrayOfDouble);
  
  public abstract double getLInfDistance(RealVector paramRealVector);
  
  public abstract double getLInfDistance(double[] paramArrayOfDouble);
  
  public abstract RealVector unitVector();
  
  public abstract void unitize();
  
  public abstract RealVector projection(RealVector paramRealVector);
  
  public abstract RealVector projection(double[] paramArrayOfDouble);
  
  public abstract RealMatrix outerProduct(RealVector paramRealVector);
  
  public abstract RealMatrix outerProduct(double[] paramArrayOfDouble);
  
  public abstract double getEntry(int paramInt);
  
  public abstract void setEntry(int paramInt, double paramDouble);
  
  public abstract int getDimension();
  
  public abstract RealVector append(RealVector paramRealVector);
  
  public abstract RealVector append(double paramDouble);
  
  public abstract RealVector append(double[] paramArrayOfDouble);
  
  public abstract RealVector getSubVector(int paramInt1, int paramInt2);
  
  public abstract void setSubVector(int paramInt, RealVector paramRealVector);
  
  public abstract void setSubVector(int paramInt, double[] paramArrayOfDouble);
  
  public abstract void set(double paramDouble);
  
  public abstract double[] toArray();
  
  public abstract boolean isNaN();
  
  public abstract boolean isInfinite();
}

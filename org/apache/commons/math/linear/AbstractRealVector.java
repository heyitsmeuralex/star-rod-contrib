package org.apache.commons.math.linear;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BinaryFunction;
import org.apache.commons.math.analysis.ComposableFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;



























public abstract class AbstractRealVector
  implements RealVector
{
  public AbstractRealVector() {}
  
  protected void checkVectorDimensions(RealVector v)
  {
    checkVectorDimensions(v.getDimension());
  }
  






  protected void checkVectorDimensions(int n)
    throws DimensionMismatchException
  {
    int d = getDimension();
    if (d != n) {
      throw new DimensionMismatchException(d, n);
    }
  }
  




  protected void checkIndex(int index)
    throws MatrixIndexException
  {
    if ((index < 0) || (index >= getDimension())) {
      throw new MatrixIndexException(LocalizedFormats.INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(index), Integer.valueOf(0), Integer.valueOf(getDimension() - 1) });
    }
  }
  
  public void setSubVector(int index, RealVector v)
    throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.getDimension() - 1);
    setSubVector(index, v.getData());
  }
  
  public void setSubVector(int index, double[] v) throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.length - 1);
    for (int i = 0; i < v.length; i++) {
      setEntry(i + index, v[i]);
    }
  }
  
  public RealVector add(double[] v) throws IllegalArgumentException
  {
    double[] result = (double[])v.clone();
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      result[e.getIndex()] += e.getValue();
    }
    return new ArrayRealVector(result, false);
  }
  
  public RealVector add(RealVector v) throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      double[] values = ((ArrayRealVector)v).getDataRef();
      return add(values);
    }
    RealVector result = v.copy();
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      int index = e.getIndex();
      result.setEntry(index, e.getValue() + result.getEntry(index));
    }
    return result;
  }
  
  public RealVector subtract(double[] v) throws IllegalArgumentException
  {
    double[] result = (double[])v.clone();
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      int index = e.getIndex();
      result[index] = (e.getValue() - result[index]);
    }
    return new ArrayRealVector(result, false);
  }
  
  public RealVector subtract(RealVector v) throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      double[] values = ((ArrayRealVector)v).getDataRef();
      return add(values);
    }
    RealVector result = v.copy();
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      int index = e.getIndex();
      v.setEntry(index, e.getValue() - result.getEntry(index));
    }
    return result;
  }
  
  public RealVector mapAdd(double d)
  {
    return copy().mapAddToSelf(d);
  }
  
  public RealVector mapAddToSelf(double d)
  {
    if (d != 0.0D) {
      try {
        return mapToSelf(BinaryFunction.ADD.fix1stArgument(d));
      } catch (FunctionEvaluationException e) {
        throw new IllegalArgumentException(e);
      }
    }
    return this;
  }
  
  public abstract AbstractRealVector copy();
  
  public double dotProduct(double[] v)
    throws IllegalArgumentException
  {
    return dotProduct(new ArrayRealVector(v, false));
  }
  
  public double dotProduct(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      d += e.getValue() * v.getEntry(e.getIndex());
    }
    return d;
  }
  
  public RealVector ebeDivide(double[] v) throws IllegalArgumentException
  {
    return ebeDivide(new ArrayRealVector(v, false));
  }
  
  public RealVector ebeMultiply(double[] v) throws IllegalArgumentException
  {
    return ebeMultiply(new ArrayRealVector(v, false));
  }
  
  public double getDistance(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      double diff = e.getValue() - v.getEntry(e.getIndex());
      d += diff * diff;
    }
    return FastMath.sqrt(d);
  }
  
  public double getNorm()
  {
    double sum = 0.0D;
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      double value = e.getValue();
      sum += value * value;
    }
    return FastMath.sqrt(sum);
  }
  
  public double getL1Norm()
  {
    double norm = 0.0D;
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      norm += FastMath.abs(e.getValue());
    }
    return norm;
  }
  
  public double getLInfNorm()
  {
    double norm = 0.0D;
    Iterator<RealVector.Entry> it = sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      norm = FastMath.max(norm, FastMath.abs(e.getValue()));
    }
    return norm;
  }
  
  public double getDistance(double[] v) throws IllegalArgumentException
  {
    return getDistance(new ArrayRealVector(v, false));
  }
  
  public double getL1Distance(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
    }
    return d;
  }
  
  public double getL1Distance(double[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      d += FastMath.abs(e.getValue() - v[e.getIndex()]);
    }
    return d;
  }
  
  public double getLInfDistance(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
    }
    return d;
  }
  
  public double getLInfDistance(double[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double d = 0.0D;
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      d = FastMath.max(FastMath.abs(e.getValue() - v[e.getIndex()]), d);
    }
    return d;
  }
  



  public int getMinIndex()
  {
    int minIndex = -1;
    double minValue = Double.POSITIVE_INFINITY;
    Iterator<RealVector.Entry> iterator = iterator();
    while (iterator.hasNext()) {
      RealVector.Entry entry = (RealVector.Entry)iterator.next();
      if (entry.getValue() <= minValue) {
        minIndex = entry.getIndex();
        minValue = entry.getValue();
      }
    }
    return minIndex;
  }
  


  public double getMinValue()
  {
    int minIndex = getMinIndex();
    return minIndex < 0 ? NaN.0D : getEntry(minIndex);
  }
  



  public int getMaxIndex()
  {
    int maxIndex = -1;
    double maxValue = Double.NEGATIVE_INFINITY;
    Iterator<RealVector.Entry> iterator = iterator();
    while (iterator.hasNext()) {
      RealVector.Entry entry = (RealVector.Entry)iterator.next();
      if (entry.getValue() >= maxValue) {
        maxIndex = entry.getIndex();
        maxValue = entry.getValue();
      }
    }
    return maxIndex;
  }
  


  public double getMaxValue()
  {
    int maxIndex = getMaxIndex();
    return maxIndex < 0 ? NaN.0D : getEntry(maxIndex);
  }
  
  public RealVector mapAbs()
  {
    return copy().mapAbsToSelf();
  }
  
  public RealVector mapAbsToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.ABS);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapAcos()
  {
    return copy().mapAcosToSelf();
  }
  
  public RealVector mapAcosToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.ACOS);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapAsin()
  {
    return copy().mapAsinToSelf();
  }
  
  public RealVector mapAsinToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.ASIN);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapAtan()
  {
    return copy().mapAtanToSelf();
  }
  
  public RealVector mapAtanToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.ATAN);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapCbrt()
  {
    return copy().mapCbrtToSelf();
  }
  
  public RealVector mapCbrtToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.CBRT);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapCeil()
  {
    return copy().mapCeilToSelf();
  }
  
  public RealVector mapCeilToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.CEIL);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapCos()
  {
    return copy().mapCosToSelf();
  }
  
  public RealVector mapCosToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.COS);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapCosh()
  {
    return copy().mapCoshToSelf();
  }
  
  public RealVector mapCoshToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.COSH);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapDivide(double d)
  {
    return copy().mapDivideToSelf(d);
  }
  
  public RealVector mapDivideToSelf(double d)
  {
    try {
      return mapToSelf(BinaryFunction.DIVIDE.fix2ndArgument(d));
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapExp()
  {
    return copy().mapExpToSelf();
  }
  
  public RealVector mapExpToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.EXP);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapExpm1()
  {
    return copy().mapExpm1ToSelf();
  }
  
  public RealVector mapExpm1ToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.EXPM1);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapFloor()
  {
    return copy().mapFloorToSelf();
  }
  
  public RealVector mapFloorToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.FLOOR);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapInv()
  {
    return copy().mapInvToSelf();
  }
  
  public RealVector mapInvToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.INVERT);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapLog()
  {
    return copy().mapLogToSelf();
  }
  
  public RealVector mapLogToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.LOG);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapLog10()
  {
    return copy().mapLog10ToSelf();
  }
  
  public RealVector mapLog10ToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.LOG10);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapLog1p()
  {
    return copy().mapLog1pToSelf();
  }
  
  public RealVector mapLog1pToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.LOG1P);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapMultiply(double d)
  {
    return copy().mapMultiplyToSelf(d);
  }
  
  public RealVector mapMultiplyToSelf(double d)
  {
    try {
      return mapToSelf(BinaryFunction.MULTIPLY.fix1stArgument(d));
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapPow(double d)
  {
    return copy().mapPowToSelf(d);
  }
  
  public RealVector mapPowToSelf(double d)
  {
    try {
      return mapToSelf(BinaryFunction.POW.fix2ndArgument(d));
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapRint()
  {
    return copy().mapRintToSelf();
  }
  
  public RealVector mapRintToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.RINT);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapSignum()
  {
    return copy().mapSignumToSelf();
  }
  
  public RealVector mapSignumToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.SIGNUM);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapSin()
  {
    return copy().mapSinToSelf();
  }
  
  public RealVector mapSinToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.SIN);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapSinh()
  {
    return copy().mapSinhToSelf();
  }
  
  public RealVector mapSinhToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.SINH);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapSqrt()
  {
    return copy().mapSqrtToSelf();
  }
  
  public RealVector mapSqrtToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.SQRT);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapSubtract(double d)
  {
    return copy().mapSubtractToSelf(d);
  }
  
  public RealVector mapSubtractToSelf(double d)
  {
    return mapAddToSelf(-d);
  }
  
  public RealVector mapTan()
  {
    return copy().mapTanToSelf();
  }
  
  public RealVector mapTanToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.TAN);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapTanh()
  {
    return copy().mapTanhToSelf();
  }
  
  public RealVector mapTanhToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.TANH);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealVector mapUlp()
  {
    return copy().mapUlpToSelf();
  }
  
  public RealVector mapUlpToSelf()
  {
    try {
      return mapToSelf(ComposableFunction.ULP);
    } catch (FunctionEvaluationException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  public RealMatrix outerProduct(RealVector v) throws IllegalArgumentException {
    RealMatrix product;
    RealMatrix product;
    if (((v instanceof SparseRealVector)) || ((this instanceof SparseRealVector))) {
      product = new OpenMapRealMatrix(getDimension(), v.getDimension());
    } else {
      product = new Array2DRowRealMatrix(getDimension(), v.getDimension());
    }
    Iterator<RealVector.Entry> thisIt = sparseIterator();
    RealVector.Entry thisE = null;
    for (; (thisIt.hasNext()) && ((thisE = (RealVector.Entry)thisIt.next()) != null); 
        


        goto 96)
    {
      Iterator<RealVector.Entry> otherIt = v.sparseIterator();
      RealVector.Entry otherE = null;
      if ((otherIt.hasNext()) && ((otherE = (RealVector.Entry)otherIt.next()) != null)) {
        product.setEntry(thisE.getIndex(), otherE.getIndex(), thisE.getValue() * otherE.getValue());
      }
    }
    

    return product;
  }
  
  public RealMatrix outerProduct(double[] v)
    throws IllegalArgumentException
  {
    return outerProduct(new ArrayRealVector(v, false));
  }
  
  public RealVector projection(double[] v) throws IllegalArgumentException
  {
    return projection(new ArrayRealVector(v, false));
  }
  
  public void set(double value)
  {
    Iterator<RealVector.Entry> it = iterator();
    RealVector.Entry e = null;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      e.setValue(value);
    }
  }
  
  public double[] toArray()
  {
    int dim = getDimension();
    double[] values = new double[dim];
    for (int i = 0; i < dim; i++) {
      values[i] = getEntry(i);
    }
    return values;
  }
  
  public double[] getData()
  {
    return toArray();
  }
  
  public RealVector unitVector()
  {
    RealVector copy = copy();
    copy.unitize();
    return copy;
  }
  
  public void unitize()
  {
    mapDivideToSelf(getNorm());
  }
  
  public Iterator<RealVector.Entry> sparseIterator()
  {
    return new SparseEntryIterator();
  }
  
  public Iterator<RealVector.Entry> iterator()
  {
    final int dim = getDimension();
    new Iterator()
    {

      private int i = 0;
      

      private AbstractRealVector.EntryImpl e = new AbstractRealVector.EntryImpl(AbstractRealVector.this);
      
      public boolean hasNext()
      {
        return i < dim;
      }
      
      public RealVector.Entry next()
      {
        e.setIndex(i++);
        return e;
      }
      
      public void remove()
      {
        throw new MathUnsupportedOperationException(new Object[0]);
      }
    };
  }
  
  public RealVector map(UnivariateRealFunction function) throws FunctionEvaluationException
  {
    return copy().mapToSelf(function);
  }
  
  public RealVector mapToSelf(UnivariateRealFunction function) throws FunctionEvaluationException
  {
    Iterator<RealVector.Entry> it = function.value(0.0D) == 0.0D ? sparseIterator() : iterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      e.setValue(function.value(e.getValue()));
    }
    return this;
  }
  
  protected class EntryImpl
    extends RealVector.Entry
  {
    public EntryImpl()
    {
      setIndex(0);
    }
    

    public double getValue()
    {
      return getEntry(getIndex());
    }
    

    public void setValue(double newValue)
    {
      setEntry(getIndex(), newValue);
    }
  }
  





  protected class SparseEntryIterator
    implements Iterator<RealVector.Entry>
  {
    private final int dim;
    



    private AbstractRealVector.EntryImpl current;
    



    private AbstractRealVector.EntryImpl next;
    




    protected SparseEntryIterator()
    {
      dim = getDimension();
      current = new AbstractRealVector.EntryImpl(AbstractRealVector.this);
      next = new AbstractRealVector.EntryImpl(AbstractRealVector.this);
      if (next.getValue() == 0.0D) {
        advance(next);
      }
    }
    


    protected void advance(AbstractRealVector.EntryImpl e)
    {
      if (e == null) {
        return;
      }
      do {
        e.setIndex(e.getIndex() + 1);
      } while ((e.getIndex() < dim) && (e.getValue() == 0.0D));
      if (e.getIndex() >= dim) {
        e.setIndex(-1);
      }
    }
    
    public boolean hasNext()
    {
      return next.getIndex() >= 0;
    }
    
    public RealVector.Entry next()
    {
      int index = next.getIndex();
      if (index < 0) {
        throw new NoSuchElementException();
      }
      current.setIndex(index);
      advance(next);
      return current;
    }
    
    public void remove()
    {
      throw new MathUnsupportedOperationException(new Object[0]);
    }
  }
}

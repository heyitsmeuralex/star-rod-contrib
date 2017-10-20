package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
























public class ArrayRealVector
  extends AbstractRealVector
  implements Serializable
{
  private static final long serialVersionUID = -1097961340710804027L;
  private static final RealVectorFormat DEFAULT_FORMAT = ;
  





  protected double[] data;
  





  public ArrayRealVector()
  {
    data = new double[0];
  }
  



  public ArrayRealVector(int size)
  {
    data = new double[size];
  }
  




  public ArrayRealVector(int size, double preset)
  {
    data = new double[size];
    Arrays.fill(data, preset);
  }
  



  public ArrayRealVector(double[] d)
  {
    data = ((double[])d.clone());
  }
  











  public ArrayRealVector(double[] d, boolean copyArray)
  {
    data = (copyArray ? (double[])d.clone() : d);
  }
  





  public ArrayRealVector(double[] d, int pos, int size)
  {
    if (d.length < pos + size) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POSITION_SIZE_MISMATCH_INPUT_ARRAY, new Object[] { Integer.valueOf(pos), Integer.valueOf(size), Integer.valueOf(d.length) });
    }
    
    data = new double[size];
    System.arraycopy(d, pos, data, 0, size);
  }
  



  public ArrayRealVector(Double[] d)
  {
    data = new double[d.length];
    for (int i = 0; i < d.length; i++) {
      data[i] = d[i].doubleValue();
    }
  }
  





  public ArrayRealVector(Double[] d, int pos, int size)
  {
    if (d.length < pos + size) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POSITION_SIZE_MISMATCH_INPUT_ARRAY, new Object[] { Integer.valueOf(pos), Integer.valueOf(size), Integer.valueOf(d.length) });
    }
    
    data = new double[size];
    for (int i = pos; i < pos + size; i++) {
      data[(i - pos)] = d[i].doubleValue();
    }
  }
  



  public ArrayRealVector(RealVector v)
  {
    data = new double[v.getDimension()];
    for (int i = 0; i < data.length; i++) {
      data[i] = v.getEntry(i);
    }
  }
  



  public ArrayRealVector(ArrayRealVector v)
  {
    this(v, true);
  }
  




  public ArrayRealVector(ArrayRealVector v, boolean deep)
  {
    data = (deep ? (double[])data.clone() : data);
  }
  




  public ArrayRealVector(ArrayRealVector v1, ArrayRealVector v2)
  {
    data = new double[data.length + data.length];
    System.arraycopy(data, 0, data, 0, data.length);
    System.arraycopy(data, 0, data, data.length, data.length);
  }
  




  public ArrayRealVector(ArrayRealVector v1, RealVector v2)
  {
    int l1 = data.length;
    int l2 = v2.getDimension();
    data = new double[l1 + l2];
    System.arraycopy(data, 0, data, 0, l1);
    for (int i = 0; i < l2; i++) {
      data[(l1 + i)] = v2.getEntry(i);
    }
  }
  




  public ArrayRealVector(RealVector v1, ArrayRealVector v2)
  {
    int l1 = v1.getDimension();
    int l2 = data.length;
    data = new double[l1 + l2];
    for (int i = 0; i < l1; i++) {
      data[i] = v1.getEntry(i);
    }
    System.arraycopy(data, 0, data, l1, l2);
  }
  




  public ArrayRealVector(ArrayRealVector v1, double[] v2)
  {
    int l1 = v1.getDimension();
    int l2 = v2.length;
    data = new double[l1 + l2];
    System.arraycopy(data, 0, data, 0, l1);
    System.arraycopy(v2, 0, data, l1, l2);
  }
  




  public ArrayRealVector(double[] v1, ArrayRealVector v2)
  {
    int l1 = v1.length;
    int l2 = v2.getDimension();
    data = new double[l1 + l2];
    System.arraycopy(v1, 0, data, 0, l1);
    System.arraycopy(data, 0, data, l1, l2);
  }
  




  public ArrayRealVector(double[] v1, double[] v2)
  {
    int l1 = v1.length;
    int l2 = v2.length;
    data = new double[l1 + l2];
    System.arraycopy(v1, 0, data, 0, l1);
    System.arraycopy(v2, 0, data, l1, l2);
  }
  

  public AbstractRealVector copy()
  {
    return new ArrayRealVector(this, true);
  }
  

  public RealVector add(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return add((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double[] out = (double[])data.clone();
    Iterator<RealVector.Entry> it = v.sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      out[e.getIndex()] += e.getValue();
    }
    return new ArrayRealVector(out, false);
  }
  


  public RealVector add(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] += v[i];
    }
    return new ArrayRealVector(out, false);
  }
  





  public ArrayRealVector add(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return (ArrayRealVector)add(data);
  }
  

  public RealVector subtract(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return subtract((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double[] out = (double[])data.clone();
    Iterator<RealVector.Entry> it = v.sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      out[e.getIndex()] -= e.getValue();
    }
    return new ArrayRealVector(out, false);
  }
  


  public RealVector subtract(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] -= v[i];
    }
    return new ArrayRealVector(out, false);
  }
  





  public ArrayRealVector subtract(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return (ArrayRealVector)subtract(data);
  }
  

  public RealVector mapAddToSelf(double d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] += d;
    }
    return this;
  }
  

  public RealVector mapSubtractToSelf(double d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] -= d;
    }
    return this;
  }
  

  public RealVector mapMultiplyToSelf(double d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] *= d;
    }
    return this;
  }
  

  public RealVector mapDivideToSelf(double d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] /= d;
    }
    return this;
  }
  

  public RealVector mapPowToSelf(double d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.pow(data[i], d);
    }
    return this;
  }
  

  public RealVector mapExpToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.exp(data[i]);
    }
    return this;
  }
  

  public RealVector mapExpm1ToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.expm1(data[i]);
    }
    return this;
  }
  

  public RealVector mapLogToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.log(data[i]);
    }
    return this;
  }
  

  public RealVector mapLog10ToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.log10(data[i]);
    }
    return this;
  }
  

  public RealVector mapLog1pToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.log1p(data[i]);
    }
    return this;
  }
  

  public RealVector mapCoshToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.cosh(data[i]);
    }
    return this;
  }
  

  public RealVector mapSinhToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.sinh(data[i]);
    }
    return this;
  }
  

  public RealVector mapTanhToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.tanh(data[i]);
    }
    return this;
  }
  

  public RealVector mapCosToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.cos(data[i]);
    }
    return this;
  }
  

  public RealVector mapSinToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.sin(data[i]);
    }
    return this;
  }
  

  public RealVector mapTanToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.tan(data[i]);
    }
    return this;
  }
  

  public RealVector mapAcosToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.acos(data[i]);
    }
    return this;
  }
  

  public RealVector mapAsinToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.asin(data[i]);
    }
    return this;
  }
  

  public RealVector mapAtanToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.atan(data[i]);
    }
    return this;
  }
  

  public RealVector mapInvToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = (1.0D / data[i]);
    }
    return this;
  }
  

  public RealVector mapAbsToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.abs(data[i]);
    }
    return this;
  }
  

  public RealVector mapSqrtToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.sqrt(data[i]);
    }
    return this;
  }
  

  public RealVector mapCbrtToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.cbrt(data[i]);
    }
    return this;
  }
  

  public RealVector mapCeilToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.ceil(data[i]);
    }
    return this;
  }
  

  public RealVector mapFloorToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.floor(data[i]);
    }
    return this;
  }
  

  public RealVector mapRintToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.rint(data[i]);
    }
    return this;
  }
  

  public RealVector mapSignumToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.signum(data[i]);
    }
    return this;
  }
  

  public RealVector mapUlpToSelf()
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = FastMath.ulp(data[i]);
    }
    return this;
  }
  
  public RealVector ebeMultiply(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return ebeMultiply((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] *= v.getEntry(i);
    }
    return new ArrayRealVector(out, false);
  }
  


  public RealVector ebeMultiply(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] *= v[i];
    }
    return new ArrayRealVector(out, false);
  }
  





  public ArrayRealVector ebeMultiply(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return (ArrayRealVector)ebeMultiply(data);
  }
  
  public RealVector ebeDivide(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return ebeDivide((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] /= v.getEntry(i);
    }
    return new ArrayRealVector(out, false);
  }
  


  public RealVector ebeDivide(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double[] out = (double[])data.clone();
    for (int i = 0; i < data.length; i++) {
      out[i] /= v[i];
    }
    return new ArrayRealVector(out, false);
  }
  





  public ArrayRealVector ebeDivide(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return (ArrayRealVector)ebeDivide(data);
  }
  

  public double[] getData()
  {
    return (double[])data.clone();
  }
  




  public double[] getDataRef()
  {
    return data;
  }
  

  public double dotProduct(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return dotProduct((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double dot = 0.0D;
    Iterator<RealVector.Entry> it = v.sparseIterator();
    RealVector.Entry e;
    while ((it.hasNext()) && ((e = (RealVector.Entry)it.next()) != null)) {
      dot += data[e.getIndex()] * e.getValue();
    }
    return dot;
  }
  


  public double dotProduct(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double dot = 0.0D;
    for (int i = 0; i < data.length; i++) {
      dot += data[i] * v[i];
    }
    return dot;
  }
  





  public double dotProduct(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return dotProduct(data);
  }
  

  public double getNorm()
  {
    double sum = 0.0D;
    for (double a : data) {
      sum += a * a;
    }
    return FastMath.sqrt(sum);
  }
  

  public double getL1Norm()
  {
    double sum = 0.0D;
    for (double a : data) {
      sum += FastMath.abs(a);
    }
    return sum;
  }
  

  public double getLInfNorm()
  {
    double max = 0.0D;
    for (double a : data) {
      max = FastMath.max(max, FastMath.abs(a));
    }
    return max;
  }
  

  public double getDistance(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return getDistance((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double sum = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v.getEntry(i);
      sum += delta * delta;
    }
    return FastMath.sqrt(sum);
  }
  


  public double getDistance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double sum = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v[i];
      sum += delta * delta;
    }
    return FastMath.sqrt(sum);
  }
  












  public double getDistance(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return getDistance(data);
  }
  

  public double getL1Distance(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return getL1Distance((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double sum = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v.getEntry(i);
      sum += FastMath.abs(delta);
    }
    return sum;
  }
  


  public double getL1Distance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double sum = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v[i];
      sum += FastMath.abs(delta);
    }
    return sum;
  }
  












  public double getL1Distance(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return getL1Distance(data);
  }
  

  public double getLInfDistance(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return getLInfDistance((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    double max = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v.getEntry(i);
      max = FastMath.max(max, FastMath.abs(delta));
    }
    return max;
  }
  


  public double getLInfDistance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double max = 0.0D;
    for (int i = 0; i < data.length; i++) {
      double delta = data[i] - v[i];
      max = FastMath.max(max, FastMath.abs(delta));
    }
    return max;
  }
  












  public double getLInfDistance(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return getLInfDistance(data);
  }
  
  public RealVector unitVector()
    throws ArithmeticException
  {
    double norm = getNorm();
    if (norm == 0.0D) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }
    return mapDivide(norm);
  }
  
  public void unitize()
    throws ArithmeticException
  {
    double norm = getNorm();
    if (norm == 0.0D) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
    }
    mapDivideToSelf(norm);
  }
  
  public RealVector projection(RealVector v)
  {
    return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
  }
  

  public RealVector projection(double[] v)
  {
    return projection(new ArrayRealVector(v, false));
  }
  




  public ArrayRealVector projection(ArrayRealVector v)
  {
    return (ArrayRealVector)v.mapMultiply(dotProduct(v) / v.dotProduct(v));
  }
  

  public RealMatrix outerProduct(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof ArrayRealVector)) {
      return outerProduct((ArrayRealVector)v);
    }
    checkVectorDimensions(v);
    int m = data.length;
    RealMatrix out = MatrixUtils.createRealMatrix(m, m);
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data.length; j++) {
        out.setEntry(i, j, data[i] * v.getEntry(j));
      }
    }
    return out;
  }
  






  public RealMatrix outerProduct(ArrayRealVector v)
    throws IllegalArgumentException
  {
    return outerProduct(data);
  }
  

  public RealMatrix outerProduct(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    int m = data.length;
    RealMatrix out = MatrixUtils.createRealMatrix(m, m);
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data.length; j++) {
        out.setEntry(i, j, data[i] * v[j]);
      }
    }
    return out;
  }
  
  public double getEntry(int index) throws MatrixIndexException
  {
    return data[index];
  }
  
  public int getDimension()
  {
    return data.length;
  }
  
  public RealVector append(RealVector v)
  {
    try {
      return new ArrayRealVector(this, (ArrayRealVector)v);
    } catch (ClassCastException cce) {}
    return new ArrayRealVector(this, v);
  }
  





  public ArrayRealVector append(ArrayRealVector v)
  {
    return new ArrayRealVector(this, v);
  }
  
  public RealVector append(double in)
  {
    double[] out = new double[data.length + 1];
    System.arraycopy(data, 0, out, 0, data.length);
    out[data.length] = in;
    return new ArrayRealVector(out, false);
  }
  
  public RealVector append(double[] in)
  {
    return new ArrayRealVector(this, in);
  }
  
  public RealVector getSubVector(int index, int n)
  {
    ArrayRealVector out = new ArrayRealVector(n);
    try {
      System.arraycopy(data, index, data, 0, n);
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + n - 1);
    }
    return out;
  }
  
  public void setEntry(int index, double value)
  {
    try {
      data[index] = value;
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
    }
  }
  
  public void setSubVector(int index, RealVector v)
  {
    try
    {
      try {
        set(index, (ArrayRealVector)v);
      } catch (ClassCastException cce) {
        for (int i = index; i < index + v.getDimension(); i++) {
          data[i] = v.getEntry(i - index);
        }
      }
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + v.getDimension() - 1);
    }
  }
  
  public void setSubVector(int index, double[] v)
  {
    try
    {
      System.arraycopy(v, 0, data, index, v.length);
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + v.length - 1);
    }
  }
  







  public void set(int index, ArrayRealVector v)
    throws MatrixIndexException
  {
    setSubVector(index, data);
  }
  

  public void set(double value)
  {
    Arrays.fill(data, value);
  }
  

  public double[] toArray()
  {
    return (double[])data.clone();
  }
  

  public String toString()
  {
    return DEFAULT_FORMAT.format(this);
  }
  






  protected void checkVectorDimensions(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
  }
  







  protected void checkVectorDimensions(int n)
    throws IllegalArgumentException
  {
    if (data.length != n) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(data.length), Integer.valueOf(n) });
    }
  }
  





  public boolean isNaN()
  {
    for (double v : data) {
      if (Double.isNaN(v)) {
        return true;
      }
    }
    return false;
  }
  






  public boolean isInfinite()
  {
    if (isNaN()) {
      return false;
    }
    
    for (double v : data) {
      if (Double.isInfinite(v)) {
        return true;
      }
    }
    
    return false;
  }
  





















  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other == null) || (!(other instanceof RealVector))) {
      return false;
    }
    

    RealVector rhs = (RealVector)other;
    if (data.length != rhs.getDimension()) {
      return false;
    }
    
    if (rhs.isNaN()) {
      return isNaN();
    }
    
    for (int i = 0; i < data.length; i++) {
      if (data[i] != rhs.getEntry(i)) {
        return false;
      }
    }
    return true;
  }
  





  public int hashCode()
  {
    if (isNaN()) {
      return 9;
    }
    return MathUtils.hash(data);
  }
}

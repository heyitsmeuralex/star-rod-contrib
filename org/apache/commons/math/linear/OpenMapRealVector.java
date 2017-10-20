package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Iterator;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;





































public class OpenMapRealVector
  extends AbstractRealVector
  implements SparseRealVector, Serializable
{
  public static final double DEFAULT_ZERO_TOLERANCE = 1.0E-12D;
  private static final long serialVersionUID = 8772222695580707260L;
  private final OpenIntToDoubleHashMap entries;
  private final int virtualSize;
  private final double epsilon;
  
  public OpenMapRealVector()
  {
    this(0, 1.0E-12D);
  }
  



  public OpenMapRealVector(int dimension)
  {
    this(dimension, 1.0E-12D);
  }
  




  public OpenMapRealVector(int dimension, double epsilon)
  {
    virtualSize = dimension;
    entries = new OpenIntToDoubleHashMap(0.0D);
    this.epsilon = epsilon;
  }
  




  protected OpenMapRealVector(OpenMapRealVector v, int resize)
  {
    virtualSize = (v.getDimension() + resize);
    entries = new OpenIntToDoubleHashMap(entries);
    epsilon = epsilon;
  }
  




  public OpenMapRealVector(int dimension, int expectedSize)
  {
    this(dimension, expectedSize, 1.0E-12D);
  }
  





  public OpenMapRealVector(int dimension, int expectedSize, double epsilon)
  {
    virtualSize = dimension;
    entries = new OpenIntToDoubleHashMap(expectedSize, 0.0D);
    this.epsilon = epsilon;
  }
  




  public OpenMapRealVector(double[] values)
  {
    this(values, 1.0E-12D);
  }
  





  public OpenMapRealVector(double[] values, double epsilon)
  {
    virtualSize = values.length;
    entries = new OpenIntToDoubleHashMap(0.0D);
    this.epsilon = epsilon;
    for (int key = 0; key < values.length; key++) {
      double value = values[key];
      if (!isDefaultValue(value)) {
        entries.put(key, value);
      }
    }
  }
  




  public OpenMapRealVector(Double[] values)
  {
    this(values, 1.0E-12D);
  }
  





  public OpenMapRealVector(Double[] values, double epsilon)
  {
    virtualSize = values.length;
    entries = new OpenIntToDoubleHashMap(0.0D);
    this.epsilon = epsilon;
    for (int key = 0; key < values.length; key++) {
      double value = values[key].doubleValue();
      if (!isDefaultValue(value)) {
        entries.put(key, value);
      }
    }
  }
  



  public OpenMapRealVector(OpenMapRealVector v)
  {
    virtualSize = v.getDimension();
    entries = new OpenIntToDoubleHashMap(v.getEntries());
    epsilon = epsilon;
  }
  



  public OpenMapRealVector(RealVector v)
  {
    virtualSize = v.getDimension();
    entries = new OpenIntToDoubleHashMap(0.0D);
    epsilon = 1.0E-12D;
    for (int key = 0; key < virtualSize; key++) {
      double value = v.getEntry(key);
      if (!isDefaultValue(value)) {
        entries.put(key, value);
      }
    }
  }
  



  private OpenIntToDoubleHashMap getEntries()
  {
    return entries;
  }
  





  protected boolean isDefaultValue(double value)
  {
    return FastMath.abs(value) < epsilon;
  }
  
  public RealVector add(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    if ((v instanceof OpenMapRealVector)) {
      return add((OpenMapRealVector)v);
    }
    return super.add(v);
  }
  





  public OpenMapRealVector add(OpenMapRealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    boolean copyThis = entries.size() > entries.size();
    OpenMapRealVector res = copyThis ? copy() : v.copy();
    OpenIntToDoubleHashMap.Iterator iter = copyThis ? entries.iterator() : entries.iterator();
    OpenIntToDoubleHashMap randomAccess = copyThis ? entries : entries;
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if (randomAccess.containsKey(key)) {
        res.setEntry(key, randomAccess.get(key) + iter.value());
      } else {
        res.setEntry(key, iter.value());
      }
    }
    return res;
  }
  




  public OpenMapRealVector append(OpenMapRealVector v)
  {
    OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key() + virtualSize, iter.value());
    }
    return res;
  }
  
  public OpenMapRealVector append(RealVector v)
  {
    if ((v instanceof OpenMapRealVector)) {
      return append((OpenMapRealVector)v);
    }
    return append(v.getData());
  }
  
  public OpenMapRealVector append(double d)
  {
    OpenMapRealVector res = new OpenMapRealVector(this, 1);
    res.setEntry(virtualSize, d);
    return res;
  }
  
  public OpenMapRealVector append(double[] a)
  {
    OpenMapRealVector res = new OpenMapRealVector(this, a.length);
    for (int i = 0; i < a.length; i++) {
      res.setEntry(i + virtualSize, a[i]);
    }
    return res;
  }
  




  public OpenMapRealVector copy()
  {
    return new OpenMapRealVector(this);
  }
  





  public double dotProduct(OpenMapRealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    boolean thisIsSmaller = entries.size() < entries.size();
    OpenIntToDoubleHashMap.Iterator iter = thisIsSmaller ? entries.iterator() : entries.iterator();
    OpenIntToDoubleHashMap larger = thisIsSmaller ? entries : entries;
    double d = 0.0D;
    while (iter.hasNext()) {
      iter.advance();
      d += iter.value() * larger.get(iter.key());
    }
    return d;
  }
  
  public double dotProduct(RealVector v)
    throws IllegalArgumentException
  {
    if ((v instanceof OpenMapRealVector)) {
      return dotProduct((OpenMapRealVector)v);
    }
    return super.dotProduct(v);
  }
  
  public OpenMapRealVector ebeDivide(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    OpenMapRealVector res = new OpenMapRealVector(this);
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
    }
    return res;
  }
  
  public OpenMapRealVector ebeDivide(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    OpenMapRealVector res = new OpenMapRealVector(this);
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), iter.value() / v[iter.key()]);
    }
    return res;
  }
  
  public OpenMapRealVector ebeMultiply(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    OpenMapRealVector res = new OpenMapRealVector(this);
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
    }
    return res;
  }
  
  public OpenMapRealVector ebeMultiply(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    OpenMapRealVector res = new OpenMapRealVector(this);
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), iter.value() * v[iter.key()]);
    }
    return res;
  }
  
  public OpenMapRealVector getSubVector(int index, int n) throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + n - 1);
    OpenMapRealVector res = new OpenMapRealVector(n);
    int end = index + n;
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if ((key >= index) && (key < end)) {
        res.setEntry(key - index, iter.value());
      }
    }
    return res;
  }
  

  public double[] getData()
  {
    double[] res = new double[virtualSize];
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res[iter.key()] = iter.value();
    }
    return res;
  }
  
  public int getDimension()
  {
    return virtualSize;
  }
  




  public double getDistance(OpenMapRealVector v)
    throws IllegalArgumentException
  {
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    double res = 0.0D;
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      
      double delta = iter.value() - v.getEntry(key);
      res += delta * delta;
    }
    iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if (!entries.containsKey(key)) {
        double value = iter.value();
        res += value * value;
      }
    }
    return FastMath.sqrt(res);
  }
  
  public double getDistance(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    if ((v instanceof OpenMapRealVector)) {
      return getDistance((OpenMapRealVector)v);
    }
    return getDistance(v.getData());
  }
  
  public double getDistance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double res = 0.0D;
    for (int i = 0; i < v.length; i++) {
      double delta = entries.get(i) - v[i];
      res += delta * delta;
    }
    return FastMath.sqrt(res);
  }
  
  public double getEntry(int index) throws MatrixIndexException
  {
    checkIndex(index);
    return entries.get(index);
  }
  







  public double getL1Distance(OpenMapRealVector v)
  {
    double max = 0.0D;
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
      max += delta;
    }
    iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if (!entries.containsKey(key)) {
        double delta = FastMath.abs(iter.value());
        max += FastMath.abs(delta);
      }
    }
    return max;
  }
  
  public double getL1Distance(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    if ((v instanceof OpenMapRealVector)) {
      return getL1Distance((OpenMapRealVector)v);
    }
    return getL1Distance(v.getData());
  }
  
  public double getL1Distance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double max = 0.0D;
    for (int i = 0; i < v.length; i++) {
      double delta = FastMath.abs(getEntry(i) - v[i]);
      max += delta;
    }
    return max;
  }
  




  private double getLInfDistance(OpenMapRealVector v)
  {
    double max = 0.0D;
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
      if (delta > max) {
        max = delta;
      }
    }
    iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if ((!entries.containsKey(key)) && 
        (iter.value() > max)) {
        max = iter.value();
      }
    }
    
    return max;
  }
  
  public double getLInfDistance(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    if ((v instanceof OpenMapRealVector)) {
      return getLInfDistance((OpenMapRealVector)v);
    }
    return getLInfDistance(v.getData());
  }
  
  public double getLInfDistance(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    double max = 0.0D;
    for (int i = 0; i < v.length; i++) {
      double delta = FastMath.abs(getEntry(i) - v[i]);
      if (delta > max) {
        max = delta;
      }
    }
    return max;
  }
  
  public boolean isInfinite()
  {
    boolean infiniteFound = false;
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      double value = iter.value();
      if (Double.isNaN(value)) {
        return false;
      }
      if (Double.isInfinite(value)) {
        infiniteFound = true;
      }
    }
    return infiniteFound;
  }
  
  public boolean isNaN()
  {
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      if (Double.isNaN(iter.value())) {
        return true;
      }
    }
    return false;
  }
  

  public OpenMapRealVector mapAdd(double d)
  {
    return copy().mapAddToSelf(d);
  }
  

  public OpenMapRealVector mapAddToSelf(double d)
  {
    for (int i = 0; i < virtualSize; i++) {
      setEntry(i, getEntry(i) + d);
    }
    return this;
  }
  
  public RealMatrix outerProduct(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    RealMatrix res = new OpenMapRealMatrix(virtualSize, virtualSize);
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int row = iter.key();
      double value = iter.value();
      for (int col = 0; col < virtualSize; col++) {
        res.setEntry(row, col, value * v[col]);
      }
    }
    return res;
  }
  
  public RealVector projection(RealVector v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
  }
  
  public OpenMapRealVector projection(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    return (OpenMapRealVector)projection(new OpenMapRealVector(v));
  }
  
  public void setEntry(int index, double value) throws MatrixIndexException
  {
    checkIndex(index);
    if (!isDefaultValue(value)) {
      entries.put(index, value);
    } else if (entries.containsKey(index)) {
      entries.remove(index);
    }
  }
  
  public void setSubVector(int index, RealVector v)
    throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.getDimension() - 1);
    setSubVector(index, v.getData());
  }
  
  public void setSubVector(int index, double[] v)
    throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.length - 1);
    for (int i = 0; i < v.length; i++) {
      setEntry(i + index, v[i]);
    }
  }
  

  public void set(double value)
  {
    for (int i = 0; i < virtualSize; i++) {
      setEntry(i, value);
    }
  }
  




  public OpenMapRealVector subtract(OpenMapRealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    OpenMapRealVector res = copy();
    OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if (entries.containsKey(key)) {
        res.setEntry(key, entries.get(key) - iter.value());
      } else {
        res.setEntry(key, -iter.value());
      }
    }
    return res;
  }
  
  public OpenMapRealVector subtract(RealVector v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    if ((v instanceof OpenMapRealVector)) {
      return subtract((OpenMapRealVector)v);
    }
    return subtract(v.getData());
  }
  
  public OpenMapRealVector subtract(double[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    OpenMapRealVector res = new OpenMapRealVector(this);
    for (int i = 0; i < v.length; i++) {
      if (entries.containsKey(i)) {
        res.setEntry(i, entries.get(i) - v[i]);
      } else {
        res.setEntry(i, -v[i]);
      }
    }
    return res;
  }
  


  public OpenMapRealVector unitVector()
  {
    OpenMapRealVector res = copy();
    res.unitize();
    return res;
  }
  

  public void unitize()
  {
    double norm = getNorm();
    if (isDefaultValue(norm)) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
    }
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      entries.put(iter.key(), iter.value() / norm);
    }
  }
  



  public double[] toArray()
  {
    return getData();
  }
  





  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    
    long temp = Double.doubleToLongBits(epsilon);
    result = 31 * result + (int)(temp ^ temp >>> 32);
    result = 31 * result + virtualSize;
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      temp = Double.doubleToLongBits(iter.value());
      result = 31 * result + (int)(temp ^ temp >> 32);
    }
    return result;
  }
  






  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OpenMapRealVector)) {
      return false;
    }
    OpenMapRealVector other = (OpenMapRealVector)obj;
    if (virtualSize != virtualSize) {
      return false;
    }
    if (Double.doubleToLongBits(epsilon) != Double.doubleToLongBits(epsilon))
    {
      return false;
    }
    OpenIntToDoubleHashMap.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      double test = other.getEntry(iter.key());
      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value())) {
        return false;
      }
    }
    iter = other.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      double test = iter.value();
      if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key()))) {
        return false;
      }
    }
    return true;
  }
  




  @Deprecated
  public double getSparcity()
  {
    return getSparsity();
  }
  




  public double getSparsity()
  {
    return entries.size() / getDimension();
  }
  

  public Iterator<RealVector.Entry> sparseIterator()
  {
    return new OpenMapSparseIterator();
  }
  




  protected class OpenMapEntry
    extends RealVector.Entry
  {
    private final OpenIntToDoubleHashMap.Iterator iter;
    



    protected OpenMapEntry(OpenIntToDoubleHashMap.Iterator iter)
    {
      this.iter = iter;
    }
    

    public double getValue()
    {
      return iter.value();
    }
    

    public void setValue(double value)
    {
      entries.put(iter.key(), value);
    }
    

    public int getIndex()
    {
      return iter.key();
    }
  }
  



  protected class OpenMapSparseIterator
    implements Iterator<RealVector.Entry>
  {
    private final OpenIntToDoubleHashMap.Iterator iter;
    


    private final RealVector.Entry current;
    


    protected OpenMapSparseIterator()
    {
      iter = entries.iterator();
      current = new OpenMapRealVector.OpenMapEntry(OpenMapRealVector.this, iter);
    }
    
    public boolean hasNext()
    {
      return iter.hasNext();
    }
    
    public RealVector.Entry next()
    {
      iter.advance();
      return current;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Not supported");
    }
  }
}

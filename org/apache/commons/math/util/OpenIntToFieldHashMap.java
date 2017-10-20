package org.apache.commons.math.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;



































































public class OpenIntToFieldHashMap<T extends FieldElement<T>>
  implements Serializable
{
  protected static final byte FREE = 0;
  protected static final byte FULL = 1;
  protected static final byte REMOVED = 2;
  private static final long serialVersionUID = -9179080286849120720L;
  private static final float LOAD_FACTOR = 0.5F;
  private static final int DEFAULT_EXPECTED_SIZE = 16;
  private static final int RESIZE_MULTIPLIER = 2;
  private static final int PERTURB_SHIFT = 5;
  private final Field<T> field;
  private int[] keys;
  private T[] values;
  private byte[] states;
  private final T missingEntries;
  private int size;
  private int mask;
  private transient int count;
  
  public OpenIntToFieldHashMap(Field<T> field)
  {
    this(field, 16, (FieldElement)field.getZero());
  }
  




  public OpenIntToFieldHashMap(Field<T> field, T missingEntries)
  {
    this(field, 16, missingEntries);
  }
  




  public OpenIntToFieldHashMap(Field<T> field, int expectedSize)
  {
    this(field, expectedSize, (FieldElement)field.getZero());
  }
  






  public OpenIntToFieldHashMap(Field<T> field, int expectedSize, T missingEntries)
  {
    this.field = field;
    int capacity = computeCapacity(expectedSize);
    keys = new int[capacity];
    values = buildArray(capacity);
    states = new byte[capacity];
    this.missingEntries = missingEntries;
    mask = (capacity - 1);
  }
  



  public OpenIntToFieldHashMap(OpenIntToFieldHashMap<T> source)
  {
    field = field;
    int length = keys.length;
    keys = new int[length];
    System.arraycopy(keys, 0, keys, 0, length);
    values = buildArray(length);
    System.arraycopy(values, 0, values, 0, length);
    states = new byte[length];
    System.arraycopy(states, 0, states, 0, length);
    missingEntries = missingEntries;
    size = size;
    mask = mask;
    count = count;
  }
  




  private static int computeCapacity(int expectedSize)
  {
    if (expectedSize == 0) {
      return 1;
    }
    int capacity = (int)FastMath.ceil(expectedSize / 0.5F);
    int powerOfTwo = Integer.highestOneBit(capacity);
    if (powerOfTwo == capacity) {
      return capacity;
    }
    return nextPowerOfTwo(capacity);
  }
  




  private static int nextPowerOfTwo(int i)
  {
    return Integer.highestOneBit(i) << 1;
  }
  





  public T get(int key)
  {
    int hash = hashOf(key);
    int index = hash & mask;
    if (containsKey(key, index)) {
      return values[index];
    }
    
    if (states[index] == 0) {
      return missingEntries;
    }
    
    int j = index;
    for (int perturb = perturb(hash); states[index] != 0; perturb >>= 5) {
      j = probe(perturb, j);
      index = j & mask;
      if (containsKey(key, index)) {
        return values[index];
      }
    }
    
    return missingEntries;
  }
  






  public boolean containsKey(int key)
  {
    int hash = hashOf(key);
    int index = hash & mask;
    if (containsKey(key, index)) {
      return true;
    }
    
    if (states[index] == 0) {
      return false;
    }
    
    int j = index;
    for (int perturb = perturb(hash); states[index] != 0; perturb >>= 5) {
      j = probe(perturb, j);
      index = j & mask;
      if (containsKey(key, index)) {
        return true;
      }
    }
    
    return false;
  }
  







  public OpenIntToFieldHashMap<T>.Iterator iterator()
  {
    return new Iterator(null);
  }
  




  private static int perturb(int hash)
  {
    return hash & 0x7FFFFFFF;
  }
  




  private int findInsertionIndex(int key)
  {
    return findInsertionIndex(keys, states, key, mask);
  }
  








  private static int findInsertionIndex(int[] keys, byte[] states, int key, int mask)
  {
    int hash = hashOf(key);
    int index = hash & mask;
    if (states[index] == 0)
      return index;
    if ((states[index] == 1) && (keys[index] == key)) {
      return changeIndexSign(index);
    }
    
    int perturb = perturb(hash);
    int j = index;
    if (states[index] == 1) {
      for (;;) {
        j = probe(perturb, j);
        index = j & mask;
        perturb >>= 5;
        
        if (states[index] == 1) { if (keys[index] == key) {
            break;
          }
        }
      }
    }
    if (states[index] == 0)
      return index;
    if (states[index] == 1)
    {

      return changeIndexSign(index);
    }
    
    int firstRemoved = index;
    for (;;) {
      j = probe(perturb, j);
      index = j & mask;
      
      if (states[index] == 0)
        return firstRemoved;
      if ((states[index] == 1) && (keys[index] == key)) {
        return changeIndexSign(index);
      }
      
      perturb >>= 5;
    }
  }
  







  private static int probe(int perturb, int j)
  {
    return (j << 2) + j + perturb + 1;
  }
  




  private static int changeIndexSign(int index)
  {
    return -index - 1;
  }
  



  public int size()
  {
    return size;
  }
  






  public T remove(int key)
  {
    int hash = hashOf(key);
    int index = hash & mask;
    if (containsKey(key, index)) {
      return doRemove(index);
    }
    
    if (states[index] == 0) {
      return missingEntries;
    }
    
    int j = index;
    for (int perturb = perturb(hash); states[index] != 0; perturb >>= 5) {
      j = probe(perturb, j);
      index = j & mask;
      if (containsKey(key, index)) {
        return doRemove(index);
      }
    }
    
    return missingEntries;
  }
  







  private boolean containsKey(int key, int index)
  {
    return ((key != 0) || (states[index] == 1)) && (keys[index] == key);
  }
  




  private T doRemove(int index)
  {
    keys[index] = 0;
    states[index] = 2;
    T previous = values[index];
    values[index] = missingEntries;
    size -= 1;
    count += 1;
    return previous;
  }
  





  public T put(int key, T value)
  {
    int index = findInsertionIndex(key);
    T previous = missingEntries;
    boolean newMapping = true;
    if (index < 0) {
      index = changeIndexSign(index);
      previous = values[index];
      newMapping = false;
    }
    keys[index] = key;
    states[index] = 1;
    values[index] = value;
    if (newMapping) {
      size += 1;
      if (shouldGrowTable()) {
        growTable();
      }
      count += 1;
    }
    return previous;
  }
  




  private void growTable()
  {
    int oldLength = states.length;
    int[] oldKeys = keys;
    T[] oldValues = values;
    byte[] oldStates = states;
    
    int newLength = 2 * oldLength;
    int[] newKeys = new int[newLength];
    T[] newValues = buildArray(newLength);
    byte[] newStates = new byte[newLength];
    int newMask = newLength - 1;
    for (int i = 0; i < oldLength; i++) {
      if (oldStates[i] == 1) {
        int key = oldKeys[i];
        int index = findInsertionIndex(newKeys, newStates, key, newMask);
        newKeys[index] = key;
        newValues[index] = oldValues[i];
        newStates[index] = 1;
      }
    }
    
    mask = newMask;
    keys = newKeys;
    values = newValues;
    states = newStates;
  }
  




  private boolean shouldGrowTable()
  {
    return size > (mask + 1) * 0.5F;
  }
  




  private static int hashOf(int key)
  {
    int h = key ^ key >>> 20 ^ key >>> 12;
    return h ^ h >>> 7 ^ h >>> 4;
  }
  



  public class Iterator
  {
    private final int referenceCount;
    


    private int current;
    


    private int next;
    


    private Iterator()
    {
      referenceCount = count;
      

      next = -1;
      try {
        advance();
      }
      catch (NoSuchElementException localNoSuchElementException) {}
      

      ???;
    }
    


    public boolean hasNext()
    {
      return next >= 0;
    }
    





    public int key()
      throws ConcurrentModificationException, NoSuchElementException
    {
      if (referenceCount != count) {
        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]);
      }
      if (current < 0) {
        throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]);
      }
      return keys[current];
    }
    





    public T value()
      throws ConcurrentModificationException, NoSuchElementException
    {
      if (referenceCount != count) {
        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]);
      }
      if (current < 0) {
        throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]);
      }
      return values[current];
    }
    





    public void advance()
      throws ConcurrentModificationException, NoSuchElementException
    {
      if (referenceCount != count) {
        throw MathRuntimeException.createConcurrentModificationException(LocalizedFormats.MAP_MODIFIED_WHILE_ITERATING, new Object[0]);
      }
      

      current = next;
      
      try
      {
        while (states[(++next)] != 1) {}
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        next = -2;
        if (current < 0) {
          throw MathRuntimeException.createNoSuchElementException(LocalizedFormats.ITERATOR_EXHAUSTED, new Object[0]);
        }
      }
    }
  }
  








  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    count = 0;
  }
  




  private T[] buildArray(int length)
  {
    return (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), length);
  }
}

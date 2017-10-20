package util;

import java.util.Map;

public class DualHashMap<K, V>
{
  public DualHashMap() {}
  
  private Map<K, V> forward = new java.util.LinkedHashMap();
  private Map<V, K> backward = new java.util.LinkedHashMap();
  
  public synchronized void add(K key, V value) {
    forward.put(key, value);
    backward.put(value, key);
  }
  
  public void clear()
  {
    forward.clear();
    backward.clear();
  }
  
  public boolean isEmpty()
  {
    return forward.isEmpty();
  }
  
  public java.util.Set<K> getKeySet()
  {
    return forward.keySet();
  }
  
  public java.util.Set<V> getValues()
  {
    return backward.keySet();
  }
  
  public boolean contains(K key)
  {
    return forward.containsKey(key);
  }
  
  public synchronized V get(K key)
  {
    return forward.get(key);
  }
  
  public boolean containsInverse(V key)
  {
    return backward.containsKey(key);
  }
  
  public synchronized K getInverse(V key)
  {
    return backward.get(key);
  }
}

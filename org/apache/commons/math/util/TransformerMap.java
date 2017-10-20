package org.apache.commons.math.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.math.MathException;




























public class TransformerMap
  implements NumberTransformer, Serializable
{
  private static final long serialVersionUID = 4605318041528645258L;
  private NumberTransformer defaultTransformer = null;
  



  private Map<Class<?>, NumberTransformer> map = null;
  


  public TransformerMap()
  {
    map = new HashMap();
    defaultTransformer = new DefaultTransformer();
  }
  




  public boolean containsClass(Class<?> key)
  {
    return map.containsKey(key);
  }
  




  public boolean containsTransformer(NumberTransformer value)
  {
    return map.containsValue(value);
  }
  





  public NumberTransformer getTransformer(Class<?> key)
  {
    return (NumberTransformer)map.get(key);
  }
  







  public NumberTransformer putTransformer(Class<?> key, NumberTransformer transformer)
  {
    return (NumberTransformer)map.put(key, transformer);
  }
  





  public NumberTransformer removeTransformer(Class<?> key)
  {
    return (NumberTransformer)map.remove(key);
  }
  


  public void clear()
  {
    map.clear();
  }
  



  public Set<Class<?>> classes()
  {
    return map.keySet();
  }
  




  public Collection<NumberTransformer> transformers()
  {
    return map.values();
  }
  







  public double transform(Object o)
    throws MathException
  {
    double value = NaN.0D;
    
    if (((o instanceof Number)) || ((o instanceof String))) {
      value = defaultTransformer.transform(o);
    } else {
      NumberTransformer trans = getTransformer(o.getClass());
      if (trans != null) {
        value = trans.transform(o);
      }
    }
    
    return value;
  }
  

  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    if ((other instanceof TransformerMap)) {
      TransformerMap rhs = (TransformerMap)other;
      if (!defaultTransformer.equals(defaultTransformer)) {
        return false;
      }
      if (map.size() != map.size()) {
        return false;
      }
      for (Map.Entry<Class<?>, NumberTransformer> entry : map.entrySet()) {
        if (!((NumberTransformer)entry.getValue()).equals(map.get(entry.getKey()))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  

  public int hashCode()
  {
    int hash = defaultTransformer.hashCode();
    for (NumberTransformer t : map.values()) {
      hash = hash * 31 + t.hashCode();
    }
    return hash;
  }
}

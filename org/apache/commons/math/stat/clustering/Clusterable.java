package org.apache.commons.math.stat.clustering;

import java.util.Collection;

public abstract interface Clusterable<T>
{
  public abstract double distanceFrom(T paramT);
  
  public abstract T centroidOf(Collection<T> paramCollection);
}

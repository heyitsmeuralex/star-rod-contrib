package org.apache.commons.math.genetics;

public abstract interface SelectionPolicy
{
  public abstract ChromosomePair select(Population paramPopulation);
}

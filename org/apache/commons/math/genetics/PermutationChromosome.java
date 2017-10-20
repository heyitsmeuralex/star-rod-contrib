package org.apache.commons.math.genetics;

import java.util.List;

public abstract interface PermutationChromosome<T>
{
  public abstract List<T> decode(List<T> paramList);
}

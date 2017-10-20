package org.apache.commons.math.genetics;

public abstract interface MutationPolicy
{
  public abstract Chromosome mutate(Chromosome paramChromosome);
}

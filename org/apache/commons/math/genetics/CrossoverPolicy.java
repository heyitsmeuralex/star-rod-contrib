package org.apache.commons.math.genetics;

public abstract interface CrossoverPolicy
{
  public abstract ChromosomePair crossover(Chromosome paramChromosome1, Chromosome paramChromosome2);
}

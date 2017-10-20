package org.apache.commons.math.genetics;

public abstract interface Population
  extends Iterable<Chromosome>
{
  public abstract int getPopulationSize();
  
  public abstract int getPopulationLimit();
  
  public abstract Population nextGeneration();
  
  public abstract void addChromosome(Chromosome paramChromosome);
  
  public abstract Chromosome getFittestChromosome();
}

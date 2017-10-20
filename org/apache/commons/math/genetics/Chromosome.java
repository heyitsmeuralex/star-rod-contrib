package org.apache.commons.math.genetics;




























public abstract class Chromosome
  implements Comparable<Chromosome>, Fitness
{
  private double fitness = Double.MIN_VALUE;
  



  public Chromosome() {}
  



  public double getFitness()
  {
    if (fitness == Double.MIN_VALUE)
    {
      fitness = fitness();
    }
    return fitness;
  }
  











  public int compareTo(Chromosome another)
  {
    return Double.valueOf(getFitness()).compareTo(Double.valueOf(another.getFitness()));
  }
  






  protected boolean isSame(Chromosome another)
  {
    return false;
  }
  









  protected Chromosome findSameChromosome(Population population)
  {
    for (Chromosome anotherChr : population) {
      if (isSame(anotherChr))
        return anotherChr;
    }
    return null;
  }
  






  public void searchForFitnessUpdate(Population population)
  {
    Chromosome sameChromosome = findSameChromosome(population);
    if (sameChromosome != null) {
      fitness = sameChromosome.getFitness();
    }
  }
}

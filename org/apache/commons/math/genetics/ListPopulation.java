package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
































public abstract class ListPopulation
  implements Population
{
  private List<Chromosome> chromosomes;
  private int populationLimit;
  
  public ListPopulation(List<Chromosome> chromosomes, int populationLimit)
  {
    if (chromosomes.size() > populationLimit) {
      throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, Integer.valueOf(chromosomes.size()), Integer.valueOf(populationLimit), false);
    }
    
    if (populationLimit < 0) {
      throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, Integer.valueOf(populationLimit));
    }
    
    this.chromosomes = chromosomes;
    this.populationLimit = populationLimit;
  }
  





  public ListPopulation(int populationLimit)
  {
    if (populationLimit < 0) {
      throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, Integer.valueOf(populationLimit));
    }
    this.populationLimit = populationLimit;
    chromosomes = new ArrayList(populationLimit);
  }
  



  public void setChromosomes(List<Chromosome> chromosomes)
  {
    this.chromosomes = chromosomes;
  }
  



  public List<Chromosome> getChromosomes()
  {
    return chromosomes;
  }
  



  public void addChromosome(Chromosome chromosome)
  {
    chromosomes.add(chromosome);
  }
  




  public Chromosome getFittestChromosome()
  {
    Chromosome bestChromosome = (Chromosome)chromosomes.get(0);
    for (Chromosome chromosome : chromosomes) {
      if (chromosome.compareTo(bestChromosome) > 0)
      {
        bestChromosome = chromosome;
      }
    }
    return bestChromosome;
  }
  



  public int getPopulationLimit()
  {
    return populationLimit;
  }
  



  public void setPopulationLimit(int populationLimit)
  {
    this.populationLimit = populationLimit;
  }
  



  public int getPopulationSize()
  {
    return chromosomes.size();
  }
  



  public String toString()
  {
    return chromosomes.toString();
  }
  




  public Iterator<Chromosome> iterator()
  {
    return chromosomes.iterator();
  }
}

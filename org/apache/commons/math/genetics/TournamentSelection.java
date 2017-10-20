package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.random.RandomGenerator;






























public class TournamentSelection
  implements SelectionPolicy
{
  private int arity;
  
  public TournamentSelection(int arity)
  {
    this.arity = arity;
  }
  









  public ChromosomePair select(Population population)
  {
    return new ChromosomePair(tournament((ListPopulation)population), tournament((ListPopulation)population));
  }
  











  private Chromosome tournament(ListPopulation population)
  {
    if (population.getPopulationSize() < arity) {
      throw new IllegalArgumentException("Tournament arity cannot be bigger than population size.");
    }
    ListPopulation tournamentPopulation = new ListPopulation(arity)
    {
      public Population nextGeneration() {
        return null;
      }
      

    };
    List<Chromosome> chromosomes = new ArrayList(population.getChromosomes());
    for (int i = 0; i < arity; i++)
    {
      int rind = GeneticAlgorithm.getRandomGenerator().nextInt(chromosomes.size());
      tournamentPopulation.addChromosome((Chromosome)chromosomes.get(rind));
      
      chromosomes.remove(rind);
    }
    
    return tournamentPopulation.getFittestChromosome();
  }
  




  public int getArity()
  {
    return arity;
  }
  




  public void setArity(int arity)
  {
    this.arity = arity;
  }
}

package org.apache.commons.math.genetics;

import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;































public class GeneticAlgorithm
{
  private static RandomGenerator randomGenerator = new JDKRandomGenerator();
  

  private final CrossoverPolicy crossoverPolicy;
  

  private final double crossoverRate;
  

  private final MutationPolicy mutationPolicy;
  

  private final double mutationRate;
  

  private final SelectionPolicy selectionPolicy;
  

  private int generationsEvolved = 0;
  









  public GeneticAlgorithm(CrossoverPolicy crossoverPolicy, double crossoverRate, MutationPolicy mutationPolicy, double mutationRate, SelectionPolicy selectionPolicy)
  {
    if ((crossoverRate < 0.0D) || (crossoverRate > 1.0D)) {
      throw new IllegalArgumentException("crossoverRate must be between 0 and 1");
    }
    if ((mutationRate < 0.0D) || (mutationRate > 1.0D)) {
      throw new IllegalArgumentException("mutationRate must be between 0 and 1");
    }
    this.crossoverPolicy = crossoverPolicy;
    this.crossoverRate = crossoverRate;
    this.mutationPolicy = mutationPolicy;
    this.mutationRate = mutationRate;
    this.selectionPolicy = selectionPolicy;
  }
  




  public static synchronized void setRandomGenerator(RandomGenerator random)
  {
    randomGenerator = random;
  }
  




  public static synchronized RandomGenerator getRandomGenerator()
  {
    return randomGenerator;
  }
  









  public Population evolve(Population initial, StoppingCondition condition)
  {
    Population current = initial;
    generationsEvolved = 0;
    while (!condition.isSatisfied(current)) {
      current = nextGeneration(current);
      generationsEvolved += 1;
    }
    return current;
  }
  





















  public Population nextGeneration(Population current)
  {
    Population nextGeneration = current.nextGeneration();
    
    RandomGenerator randGen = getRandomGenerator();
    
    while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit())
    {
      ChromosomePair pair = getSelectionPolicy().select(current);
      

      if (randGen.nextDouble() < getCrossoverRate())
      {
        pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
      }
      

      if (randGen.nextDouble() < getMutationRate())
      {
        pair = new ChromosomePair(getMutationPolicy().mutate(pair.getFirst()), getMutationPolicy().mutate(pair.getSecond()));
      }
      



      nextGeneration.addChromosome(pair.getFirst());
      
      if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit())
      {
        nextGeneration.addChromosome(pair.getSecond());
      }
    }
    
    return nextGeneration;
  }
  



  public CrossoverPolicy getCrossoverPolicy()
  {
    return crossoverPolicy;
  }
  



  public double getCrossoverRate()
  {
    return crossoverRate;
  }
  



  public MutationPolicy getMutationPolicy()
  {
    return mutationPolicy;
  }
  



  public double getMutationRate()
  {
    return mutationRate;
  }
  



  public SelectionPolicy getSelectionPolicy()
  {
    return selectionPolicy;
  }
  






  public int getGenerationsEvolved()
  {
    return generationsEvolved;
  }
}

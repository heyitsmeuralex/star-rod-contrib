package org.apache.commons.math.genetics;


























public class FixedGenerationCount
  implements StoppingCondition
{
  private int numGenerations = 0;
  


  private final int maxGenerations;
  



  public FixedGenerationCount(int maxGenerations)
  {
    if (maxGenerations <= 0)
      throw new IllegalArgumentException("The number of generations has to be >= 0");
    this.maxGenerations = maxGenerations;
  }
  







  public boolean isSatisfied(Population population)
  {
    if (numGenerations < maxGenerations) {
      numGenerations += 1;
      return false;
    }
    return true;
  }
  


  public int getNumGenerations()
  {
    return numGenerations;
  }
}

package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.random.RandomGenerator;























public class BinaryMutation
  implements MutationPolicy
{
  public BinaryMutation() {}
  
  public Chromosome mutate(Chromosome original)
  {
    if (!(original instanceof BinaryChromosome)) {
      throw new IllegalArgumentException("Binary mutation works on BinaryChromosome only.");
    }
    
    BinaryChromosome origChrom = (BinaryChromosome)original;
    List<Integer> newRepr = new ArrayList(origChrom.getRepresentation());
    

    int geneIndex = GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
    
    newRepr.set(geneIndex, Integer.valueOf(((Integer)origChrom.getRepresentation().get(geneIndex)).intValue() == 0 ? 1 : 0));
    
    Chromosome newChrom = origChrom.newFixedLengthChromosome(newRepr);
    return newChrom;
  }
}

package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.random.RandomGenerator;
























public abstract class BinaryChromosome
  extends AbstractListChromosome<Integer>
{
  public BinaryChromosome(List<Integer> representation)
  {
    super(representation);
  }
  



  public BinaryChromosome(Integer[] representation)
  {
    super(representation);
  }
  


  protected void checkValidity(List<Integer> chromosomeRepresentation)
    throws InvalidRepresentationException
  {
    for (Iterator i$ = chromosomeRepresentation.iterator(); i$.hasNext();) { int i = ((Integer)i$.next()).intValue();
      if ((i < 0) || (i > 1)) {
        throw new InvalidRepresentationException("Elements can be only 0 or 1.");
      }
    }
  }
  




  public static List<Integer> randomBinaryRepresentation(int length)
  {
    List<Integer> rList = new ArrayList(length);
    for (int j = 0; j < length; j++) {
      rList.add(Integer.valueOf(GeneticAlgorithm.getRandomGenerator().nextInt(2)));
    }
    return rList;
  }
  




  protected boolean isSame(Chromosome another)
  {
    if (!(another instanceof BinaryChromosome))
      return false;
    BinaryChromosome anotherBc = (BinaryChromosome)another;
    
    if (getLength() != anotherBc.getLength()) {
      return false;
    }
    for (int i = 0; i < getRepresentation().size(); i++) {
      if (!((Integer)getRepresentation().get(i)).equals(anotherBc.getRepresentation().get(i))) {
        return false;
      }
    }
    return true;
  }
}

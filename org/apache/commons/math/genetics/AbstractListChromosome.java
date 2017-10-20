package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


























public abstract class AbstractListChromosome<T>
  extends Chromosome
{
  private final List<T> representation;
  
  public AbstractListChromosome(List<T> representation)
  {
    try
    {
      checkValidity(representation);
    } catch (InvalidRepresentationException e) {
      throw new IllegalArgumentException(String.format("Invalid representation for %s", new Object[] { getClass().getSimpleName() }), e);
    }
    this.representation = Collections.unmodifiableList(new ArrayList(representation));
  }
  



  public AbstractListChromosome(T[] representation)
  {
    this(Arrays.asList(representation));
  }
  





  protected abstract void checkValidity(List<T> paramList)
    throws InvalidRepresentationException;
  




  protected List<T> getRepresentation()
  {
    return representation;
  }
  



  public int getLength()
  {
    return getRepresentation().size();
  }
  








  public abstract AbstractListChromosome<T> newFixedLengthChromosome(List<T> paramList);
  








  public String toString()
  {
    return String.format("(f=%s %s)", new Object[] { Double.valueOf(getFitness()), getRepresentation() });
  }
}

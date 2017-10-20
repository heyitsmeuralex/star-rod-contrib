package org.apache.commons.math.genetics;











public class ChromosomePair
{
  private final Chromosome first;
  









  private final Chromosome second;
  










  public ChromosomePair(Chromosome c1, Chromosome c2)
  {
    first = c1;
    second = c2;
  }
  




  public Chromosome getFirst()
  {
    return first;
  }
  




  public Chromosome getSecond()
  {
    return second;
  }
  



  public String toString()
  {
    return String.format("(%s,%s)", new Object[] { getFirst(), getSecond() });
  }
}

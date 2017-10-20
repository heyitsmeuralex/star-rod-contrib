package org.apache.commons.math.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.random.RandomGenerator;































































public abstract class RandomKey<T>
  extends AbstractListChromosome<Double>
  implements PermutationChromosome<T>
{
  private final List<Double> sortedRepresentation;
  private final List<Integer> baseSeqPermutation;
  
  public RandomKey(List<Double> representation)
  {
    super(representation);
    
    List<Double> sortedRepr = new ArrayList(getRepresentation());
    Collections.sort(sortedRepr);
    sortedRepresentation = Collections.unmodifiableList(sortedRepr);
    
    baseSeqPermutation = Collections.unmodifiableList(decodeGeneric(baseSequence(getLength()), getRepresentation(), sortedRepresentation));
  }
  






  public RandomKey(Double[] representation)
  {
    this(Arrays.asList(representation));
  }
  


  public List<T> decode(List<T> sequence)
  {
    return decodeGeneric(sequence, getRepresentation(), sortedRepresentation);
  }
  









  private static <S> List<S> decodeGeneric(List<S> sequence, List<Double> representation, List<Double> sortedRepr)
  {
    int l = sequence.size();
    
    if (representation.size() != l) {
      throw new IllegalArgumentException(String.format("Length of sequence for decoding (%s) has to be equal to the length of the RandomKey (%s)", new Object[] { Integer.valueOf(l), Integer.valueOf(representation.size()) }));
    }
    if (representation.size() != sortedRepr.size()) {
      throw new IllegalArgumentException(String.format("Representation and sortedRepr must have same sizes, %d != %d", new Object[] { Integer.valueOf(representation.size()), Integer.valueOf(sortedRepr.size()) }));
    }
    
    List<Double> reprCopy = new ArrayList(representation);
    

    List<S> res = new ArrayList(l);
    for (int i = 0; i < l; i++) {
      int index = reprCopy.indexOf(sortedRepr.get(i));
      res.add(sequence.get(index));
      reprCopy.set(index, null);
    }
    return res;
  }
  








  protected boolean isSame(Chromosome another)
  {
    if (!(another instanceof RandomKey))
      return false;
    RandomKey<?> anotherRk = (RandomKey)another;
    
    if (getLength() != anotherRk.getLength()) {
      return false;
    }
    

    List<Integer> thisPerm = baseSeqPermutation;
    List<Integer> anotherPerm = baseSeqPermutation;
    
    for (int i = 0; i < getLength(); i++) {
      if (thisPerm.get(i) != anotherPerm.get(i)) {
        return false;
      }
    }
    return true;
  }
  


  protected void checkValidity(List<Double> chromosomeRepresentation)
    throws InvalidRepresentationException
  {
    for (Iterator i$ = chromosomeRepresentation.iterator(); i$.hasNext();) { double val = ((Double)i$.next()).doubleValue();
      if ((val < 0.0D) || (val > 1.0D)) {
        throw new InvalidRepresentationException("Values of representation must be in [0,1] interval");
      }
    }
  }
  








  public static final List<Double> randomPermutation(int l)
  {
    List<Double> repr = new ArrayList(l);
    for (int i = 0; i < l; i++) {
      repr.add(Double.valueOf(GeneticAlgorithm.getRandomGenerator().nextDouble()));
    }
    return repr;
  }
  







  public static final List<Double> identityPermutation(int l)
  {
    List<Double> repr = new ArrayList(l);
    for (int i = 0; i < l; i++) {
      repr.add(Double.valueOf(i / l));
    }
    return repr;
  }
  












  public static <S> List<Double> comparatorPermutation(List<S> data, Comparator<S> comparator)
  {
    List<S> sortedData = new ArrayList(data);
    Collections.sort(sortedData, comparator);
    
    return inducedPermutation(data, sortedData);
  }
  











  public static <S> List<Double> inducedPermutation(List<S> originalData, List<S> permutedData)
    throws IllegalArgumentException
  {
    if (originalData.size() != permutedData.size()) {
      throw new IllegalArgumentException("originalData and permutedData must have same length");
    }
    int l = originalData.size();
    
    List<S> origDataCopy = new ArrayList(originalData);
    
    Double[] res = new Double[l];
    for (int i = 0; i < l; i++) {
      int index = origDataCopy.indexOf(permutedData.get(i));
      if (index == -1) {
        throw new IllegalArgumentException("originalData and permutedData must contain the same objects.");
      }
      res[index] = Double.valueOf(i / l);
      origDataCopy.set(index, null);
    }
    return Arrays.asList(res);
  }
  



  public String toString()
  {
    return String.format("(f=%s pi=(%s))", new Object[] { Double.valueOf(getFitness()), baseSeqPermutation });
  }
  





  private static List<Integer> baseSequence(int l)
  {
    List<Integer> baseSequence = new ArrayList(l);
    for (int i = 0; i < l; i++) {
      baseSequence.add(Integer.valueOf(i));
    }
    return baseSequence;
  }
}

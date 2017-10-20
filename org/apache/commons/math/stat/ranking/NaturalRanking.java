package org.apache.commons.math.stat.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.util.FastMath;



























































public class NaturalRanking
  implements RankingAlgorithm
{
  public static final NaNStrategy DEFAULT_NAN_STRATEGY = NaNStrategy.MAXIMAL;
  

  public static final TiesStrategy DEFAULT_TIES_STRATEGY = TiesStrategy.AVERAGE;
  


  private final NaNStrategy nanStrategy;
  

  private final TiesStrategy tiesStrategy;
  

  private final RandomData randomData;
  


  public NaturalRanking()
  {
    tiesStrategy = DEFAULT_TIES_STRATEGY;
    nanStrategy = DEFAULT_NAN_STRATEGY;
    randomData = null;
  }
  





  public NaturalRanking(TiesStrategy tiesStrategy)
  {
    this.tiesStrategy = tiesStrategy;
    nanStrategy = DEFAULT_NAN_STRATEGY;
    randomData = new RandomDataImpl();
  }
  





  public NaturalRanking(NaNStrategy nanStrategy)
  {
    this.nanStrategy = nanStrategy;
    tiesStrategy = DEFAULT_TIES_STRATEGY;
    randomData = null;
  }
  






  public NaturalRanking(NaNStrategy nanStrategy, TiesStrategy tiesStrategy)
  {
    this.nanStrategy = nanStrategy;
    this.tiesStrategy = tiesStrategy;
    randomData = new RandomDataImpl();
  }
  






  public NaturalRanking(RandomGenerator randomGenerator)
  {
    tiesStrategy = TiesStrategy.RANDOM;
    nanStrategy = DEFAULT_NAN_STRATEGY;
    randomData = new RandomDataImpl(randomGenerator);
  }
  









  public NaturalRanking(NaNStrategy nanStrategy, RandomGenerator randomGenerator)
  {
    this.nanStrategy = nanStrategy;
    tiesStrategy = TiesStrategy.RANDOM;
    randomData = new RandomDataImpl(randomGenerator);
  }
  




  public NaNStrategy getNanStrategy()
  {
    return nanStrategy;
  }
  




  public TiesStrategy getTiesStrategy()
  {
    return tiesStrategy;
  }
  









  public double[] rank(double[] data)
  {
    IntDoublePair[] ranks = new IntDoublePair[data.length];
    for (int i = 0; i < data.length; i++) {
      ranks[i] = new IntDoublePair(data[i], i);
    }
    

    List<Integer> nanPositions = null;
    switch (1.$SwitchMap$org$apache$commons$math$stat$ranking$NaNStrategy[nanStrategy.ordinal()]) {
    case 1: 
      recodeNaNs(ranks, Double.POSITIVE_INFINITY);
      break;
    case 2: 
      recodeNaNs(ranks, Double.NEGATIVE_INFINITY);
      break;
    case 3: 
      ranks = removeNaNs(ranks);
      break;
    case 4: 
      nanPositions = getNanPositions(ranks);
      break;
    default: 
      throw new MathInternalError();
    }
    
    
    Arrays.sort(ranks);
    


    double[] out = new double[ranks.length];
    int pos = 1;
    out[ranks[0].getPosition()] = pos;
    List<Integer> tiesTrace = new ArrayList();
    tiesTrace.add(Integer.valueOf(ranks[0].getPosition()));
    for (int i = 1; i < ranks.length; i++) {
      if (Double.compare(ranks[i].getValue(), ranks[(i - 1)].getValue()) > 0)
      {
        pos = i + 1;
        if (tiesTrace.size() > 1) {
          resolveTie(out, tiesTrace);
        }
        tiesTrace = new ArrayList();
        tiesTrace.add(Integer.valueOf(ranks[i].getPosition()));
      }
      else {
        tiesTrace.add(Integer.valueOf(ranks[i].getPosition()));
      }
      out[ranks[i].getPosition()] = pos;
    }
    if (tiesTrace.size() > 1) {
      resolveTie(out, tiesTrace);
    }
    if (nanStrategy == NaNStrategy.FIXED) {
      restoreNaNs(out, nanPositions);
    }
    return out;
  }
  






  private IntDoublePair[] removeNaNs(IntDoublePair[] ranks)
  {
    if (!containsNaNs(ranks)) {
      return ranks;
    }
    IntDoublePair[] outRanks = new IntDoublePair[ranks.length];
    int j = 0;
    for (int i = 0; i < ranks.length; i++) {
      if (Double.isNaN(ranks[i].getValue()))
      {
        for (int k = i + 1; k < ranks.length; k++) {
          ranks[k] = new IntDoublePair(ranks[k].getValue(), ranks[k].getPosition() - 1);
        }
      }
      else {
        outRanks[j] = new IntDoublePair(ranks[i].getValue(), ranks[i].getPosition());
        
        j++;
      }
    }
    IntDoublePair[] returnRanks = new IntDoublePair[j];
    System.arraycopy(outRanks, 0, returnRanks, 0, j);
    return returnRanks;
  }
  





  private void recodeNaNs(IntDoublePair[] ranks, double value)
  {
    for (int i = 0; i < ranks.length; i++) {
      if (Double.isNaN(ranks[i].getValue())) {
        ranks[i] = new IntDoublePair(value, ranks[i].getPosition());
      }
    }
  }
  






  private boolean containsNaNs(IntDoublePair[] ranks)
  {
    for (int i = 0; i < ranks.length; i++) {
      if (Double.isNaN(ranks[i].getValue())) {
        return true;
      }
    }
    return false;
  }
  















  private void resolveTie(double[] ranks, List<Integer> tiesTrace)
  {
    double c = ranks[((Integer)tiesTrace.get(0)).intValue()];
    

    int length = tiesTrace.size();
    Iterator<Integer> iterator;
    long f; switch (1.$SwitchMap$org$apache$commons$math$stat$ranking$TiesStrategy[tiesStrategy.ordinal()]) {
    case 1: 
      fill(ranks, tiesTrace, (2.0D * c + length - 1.0D) / 2.0D);
      break;
    case 2: 
      fill(ranks, tiesTrace, c + length - 1.0D);
      break;
    case 3: 
      fill(ranks, tiesTrace, c);
      break;
    case 4: 
      iterator = tiesTrace.iterator();
      f = FastMath.round(c);
    case 5: default:  while (iterator.hasNext()) {
        ranks[((Integer)iterator.next()).intValue()] = randomData.nextLong(f, f + length - 1L); continue;
        




        iterator = tiesTrace.iterator();
        f = FastMath.round(c);
        int i = 0;
        while (iterator.hasNext()) {
          ranks[((Integer)iterator.next()).intValue()] = (f + i++); continue;
          


          throw new MathInternalError();
        }
      }
    }
    
  }
  



  private void fill(double[] data, List<Integer> tiesTrace, double value)
  {
    Iterator<Integer> iterator = tiesTrace.iterator();
    while (iterator.hasNext()) {
      data[((Integer)iterator.next()).intValue()] = value;
    }
  }
  





  private void restoreNaNs(double[] ranks, List<Integer> nanPositions)
  {
    if (nanPositions.size() == 0) {
      return;
    }
    Iterator<Integer> iterator = nanPositions.iterator();
    while (iterator.hasNext()) {
      ranks[((Integer)iterator.next()).intValue()] = NaN.0D;
    }
  }
  






  private List<Integer> getNanPositions(IntDoublePair[] ranks)
  {
    ArrayList<Integer> out = new ArrayList();
    for (int i = 0; i < ranks.length; i++) {
      if (Double.isNaN(ranks[i].getValue())) {
        out.add(Integer.valueOf(i));
      }
    }
    return out;
  }
  




  private static class IntDoublePair
    implements Comparable<IntDoublePair>
  {
    private final double value;
    



    private final int position;
    




    public IntDoublePair(double value, int position)
    {
      this.value = value;
      this.position = position;
    }
    






    public int compareTo(IntDoublePair other)
    {
      return Double.compare(value, value);
    }
    



    public double getValue()
    {
      return value;
    }
    



    public int getPosition()
    {
      return position;
    }
  }
}

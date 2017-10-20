package org.apache.commons.math.stat.inference;

import java.util.Collection;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.apache.commons.math.stat.descriptive.summary.SumOfSquares;



















































public class OneWayAnovaImpl
  implements OneWayAnova
{
  public OneWayAnovaImpl() {}
  
  public double anovaFValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException
  {
    AnovaStats a = anovaStats(categoryData);
    return F;
  }
  









  public double anovaPValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException
  {
    AnovaStats a = anovaStats(categoryData);
    FDistribution fdist = new FDistributionImpl(dfbg, dfwg);
    return 1.0D - fdist.cumulativeProbability(F);
  }
  










  public boolean anovaTest(Collection<double[]> categoryData, double alpha)
    throws IllegalArgumentException, MathException
  {
    if ((alpha <= 0.0D) || (alpha > 0.5D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Integer.valueOf(0), Double.valueOf(0.5D) });
    }
    

    return anovaPValue(categoryData) < alpha;
  }
  












  private AnovaStats anovaStats(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException
  {
    if (categoryData.size() < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.TWO_OR_MORE_CATEGORIES_REQUIRED, new Object[] { Integer.valueOf(categoryData.size()) });
    }
    



    for (double[] array : categoryData) {
      if (array.length <= 1) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED, new Object[] { Integer.valueOf(array.length) });
      }
    }
    


    int dfwg = 0;
    double sswg = 0.0D;
    Sum totsum = new Sum();
    SumOfSquares totsumsq = new SumOfSquares();
    int totnum = 0;
    
    for (double[] data : categoryData)
    {
      Sum sum = new Sum();
      SumOfSquares sumsq = new SumOfSquares();
      int num = 0;
      
      for (int i = 0; i < data.length; i++) {
        double val = data[i];
        

        num++;
        sum.increment(val);
        sumsq.increment(val);
        

        totnum++;
        totsum.increment(val);
        totsumsq.increment(val);
      }
      dfwg += num - 1;
      double ss = sumsq.getResult() - sum.getResult() * sum.getResult() / num;
      sswg += ss;
    }
    double sst = totsumsq.getResult() - totsum.getResult() * totsum.getResult() / totnum;
    
    double ssbg = sst - sswg;
    int dfbg = categoryData.size() - 1;
    double msbg = ssbg / dfbg;
    double mswg = sswg / dfwg;
    double F = msbg / mswg;
    
    return new AnovaStats(dfbg, dfwg, F, null);
  }
  




  private static class AnovaStats
  {
    private int dfbg;
    



    private int dfwg;
    


    private double F;
    



    private AnovaStats(int dfbg, int dfwg, double F)
    {
      this.dfbg = dfbg;
      this.dfwg = dfwg;
      this.F = F;
    }
  }
}

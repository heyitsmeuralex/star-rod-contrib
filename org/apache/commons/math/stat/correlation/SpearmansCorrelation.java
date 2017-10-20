package org.apache.commons.math.stat.correlation;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.ranking.NaturalRanking;
import org.apache.commons.math.stat.ranking.RankingAlgorithm;











































public class SpearmansCorrelation
{
  private final RealMatrix data;
  private final RankingAlgorithm rankingAlgorithm;
  private final PearsonsCorrelation rankCorrelation;
  
  public SpearmansCorrelation(RealMatrix dataMatrix, RankingAlgorithm rankingAlgorithm)
  {
    data = dataMatrix.copy();
    this.rankingAlgorithm = rankingAlgorithm;
    rankTransform(data);
    rankCorrelation = new PearsonsCorrelation(data);
  }
  





  public SpearmansCorrelation(RealMatrix dataMatrix)
  {
    this(dataMatrix, new NaturalRanking());
  }
  


  public SpearmansCorrelation()
  {
    data = null;
    rankingAlgorithm = new NaturalRanking();
    rankCorrelation = null;
  }
  




  public RealMatrix getCorrelationMatrix()
  {
    return rankCorrelation.getCorrelationMatrix();
  }
  











  public PearsonsCorrelation getRankCorrelation()
  {
    return rankCorrelation;
  }
  






  public RealMatrix computeCorrelationMatrix(RealMatrix matrix)
  {
    RealMatrix matrixCopy = matrix.copy();
    rankTransform(matrixCopy);
    return new PearsonsCorrelation().computeCorrelationMatrix(matrixCopy);
  }
  







  public RealMatrix computeCorrelationMatrix(double[][] matrix)
  {
    return computeCorrelationMatrix(new BlockRealMatrix(matrix));
  }
  











  public double correlation(double[] xArray, double[] yArray)
    throws IllegalArgumentException
  {
    if (xArray.length != yArray.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(xArray.length), Integer.valueOf(yArray.length) });
    }
    if (xArray.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(xArray.length), Integer.valueOf(2) });
    }
    
    return new PearsonsCorrelation().correlation(rankingAlgorithm.rank(xArray), rankingAlgorithm.rank(yArray));
  }
  







  private void rankTransform(RealMatrix matrix)
  {
    for (int i = 0; i < matrix.getColumnDimension(); i++) {
      matrix.setColumn(i, rankingAlgorithm.rank(matrix.getColumn(i)));
    }
  }
}

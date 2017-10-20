package org.apache.commons.math.random;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.util.FastMath;


















































































public class CorrelatedRandomVectorGenerator
  implements RandomVectorGenerator
{
  private final double[] mean;
  private final NormalizedRandomGenerator generator;
  private final double[] normalized;
  private RealMatrix root;
  private int rank;
  
  public CorrelatedRandomVectorGenerator(double[] mean, RealMatrix covariance, double small, NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException, DimensionMismatchException
  {
    int order = covariance.getRowDimension();
    if (mean.length != order) {
      throw new DimensionMismatchException(mean.length, order);
    }
    this.mean = ((double[])mean.clone());
    
    decompose(covariance, small);
    
    this.generator = generator;
    normalized = new double[rank];
  }
  













  public CorrelatedRandomVectorGenerator(RealMatrix covariance, double small, NormalizedRandomGenerator generator)
    throws NotPositiveDefiniteMatrixException
  {
    int order = covariance.getRowDimension();
    mean = new double[order];
    for (int i = 0; i < order; i++) {
      mean[i] = 0.0D;
    }
    
    decompose(covariance, small);
    
    this.generator = generator;
    normalized = new double[rank];
  }
  



  public NormalizedRandomGenerator getGenerator()
  {
    return generator;
  }
  





  public RealMatrix getRootMatrix()
  {
    return root;
  }
  






  public int getRank()
  {
    return rank;
  }
  

















  private void decompose(RealMatrix covariance, double small)
    throws NotPositiveDefiniteMatrixException
  {
    int order = covariance.getRowDimension();
    double[][] c = covariance.getData();
    double[][] b = new double[order][order];
    
    int[] swap = new int[order];
    int[] index = new int[order];
    for (int i = 0; i < order; i++) {
      index[i] = i;
    }
    
    rank = 0;
    for (boolean loop = true; loop;)
    {

      swap[rank] = rank;
      for (int i = rank + 1; i < order; i++) {
        int ii = index[i];
        int isi = index[swap[i]];
        if (c[ii][ii] > c[isi][isi]) {
          swap[rank] = i;
        }
      }
      


      if (swap[rank] != rank) {
        int tmp = index[rank];
        index[rank] = index[swap[rank]];
        index[swap[rank]] = tmp;
      }
      

      int ir = index[rank];
      if (c[ir][ir] < small)
      {
        if (rank == 0) {
          throw new NotPositiveDefiniteMatrixException();
        }
        

        for (int i = rank; i < order; i++) {
          if (c[index[i]][index[i]] < -small)
          {

            throw new NotPositiveDefiniteMatrixException();
          }
        }
        


        rank += 1;
        loop = false;

      }
      else
      {
        double sqrt = FastMath.sqrt(c[ir][ir]);
        b[rank][rank] = sqrt;
        double inverse = 1.0D / sqrt;
        for (int i = rank + 1; i < order; i++) {
          int ii = index[i];
          double e = inverse * c[ii][ir];
          b[i][rank] = e;
          c[ii][ii] -= e * e;
          for (int j = rank + 1; j < i; j++) {
            int ij = index[j];
            double f = c[ii][ij] - e * b[j][rank];
            c[ii][ij] = f;
            c[ij][ii] = f;
          }
        }
        

        loop = ++rank < order;
      }
    }
    



    root = MatrixUtils.createRealMatrix(order, rank);
    for (int i = 0; i < order; i++) {
      for (int j = 0; j < rank; j++) {
        root.setEntry(index[i], j, b[i][j]);
      }
    }
  }
  






  public double[] nextVector()
  {
    for (int i = 0; i < rank; i++) {
      normalized[i] = generator.nextNormalizedDouble();
    }
    

    double[] correlated = new double[mean.length];
    for (int i = 0; i < correlated.length; i++) {
      correlated[i] = mean[i];
      for (int j = 0; j < rank; j++) {
        correlated[i] += root.getEntry(i, j) * normalized[j];
      }
    }
    
    return correlated;
  }
}

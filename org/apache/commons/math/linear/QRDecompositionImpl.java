package org.apache.commons.math.linear;

import java.util.Arrays;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;























































public class QRDecompositionImpl
  implements QRDecomposition
{
  private double[][] qrt;
  private double[] rDiag;
  private RealMatrix cachedQ;
  private RealMatrix cachedQT;
  private RealMatrix cachedR;
  private RealMatrix cachedH;
  
  public QRDecompositionImpl(RealMatrix matrix)
  {
    int m = matrix.getRowDimension();
    int n = matrix.getColumnDimension();
    qrt = matrix.transpose().getData();
    rDiag = new double[FastMath.min(m, n)];
    cachedQ = null;
    cachedQT = null;
    cachedR = null;
    cachedH = null;
    





    for (int minor = 0; minor < FastMath.min(m, n); minor++)
    {
      double[] qrtMinor = qrt[minor];
      







      double xNormSqr = 0.0D;
      for (int row = minor; row < m; row++) {
        double c = qrtMinor[row];
        xNormSqr += c * c;
      }
      double a = qrtMinor[minor] > 0.0D ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
      rDiag[minor] = a;
      
      if (a != 0.0D)
      {








        qrtMinor[minor] -= a;
        












        for (int col = minor + 1; col < n; col++) {
          double[] qrtCol = qrt[col];
          double alpha = 0.0D;
          for (int row = minor; row < m; row++) {
            alpha -= qrtCol[row] * qrtMinor[row];
          }
          alpha /= a * qrtMinor[minor];
          

          for (int row = minor; row < m; row++) {
            qrtCol[row] -= alpha * qrtMinor[row];
          }
        }
      }
    }
  }
  

  public RealMatrix getR()
  {
    if (cachedR == null)
    {

      int n = qrt.length;
      int m = qrt[0].length;
      cachedR = MatrixUtils.createRealMatrix(m, n);
      

      for (int row = FastMath.min(m, n) - 1; row >= 0; row--) {
        cachedR.setEntry(row, row, rDiag[row]);
        for (int col = row + 1; col < n; col++) {
          cachedR.setEntry(row, col, qrt[col][row]);
        }
      }
    }
    


    return cachedR;
  }
  

  public RealMatrix getQ()
  {
    if (cachedQ == null) {
      cachedQ = getQT().transpose();
    }
    return cachedQ;
  }
  

  public RealMatrix getQT()
  {
    if (cachedQT == null)
    {

      int n = qrt.length;
      int m = qrt[0].length;
      cachedQT = MatrixUtils.createRealMatrix(m, m);
      





      for (int minor = m - 1; minor >= FastMath.min(m, n); minor--) {
        cachedQT.setEntry(minor, minor, 1.0D);
      }
      
      for (int minor = FastMath.min(m, n) - 1; minor >= 0; minor--) {
        double[] qrtMinor = qrt[minor];
        cachedQT.setEntry(minor, minor, 1.0D);
        if (qrtMinor[minor] != 0.0D) {
          for (int col = minor; col < m; col++) {
            double alpha = 0.0D;
            for (int row = minor; row < m; row++) {
              alpha -= cachedQT.getEntry(col, row) * qrtMinor[row];
            }
            alpha /= rDiag[minor] * qrtMinor[minor];
            
            for (int row = minor; row < m; row++) {
              cachedQT.addToEntry(col, row, -alpha * qrtMinor[row]);
            }
          }
        }
      }
    }
    


    return cachedQT;
  }
  


  public RealMatrix getH()
  {
    if (cachedH == null)
    {
      int n = qrt.length;
      int m = qrt[0].length;
      cachedH = MatrixUtils.createRealMatrix(m, n);
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < FastMath.min(i + 1, n); j++) {
          cachedH.setEntry(i, j, qrt[j][i] / -rDiag[j]);
        }
      }
    }
    


    return cachedH;
  }
  

  public DecompositionSolver getSolver()
  {
    return new Solver(qrt, rDiag, null);
  }
  




  private static class Solver
    implements DecompositionSolver
  {
    private final double[][] qrt;
    



    private final double[] rDiag;
    




    private Solver(double[][] qrt, double[] rDiag)
    {
      this.qrt = qrt;
      this.rDiag = rDiag;
    }
    

    public boolean isNonSingular()
    {
      for (double diag : rDiag) {
        if (diag == 0.0D) {
          return false;
        }
      }
      return true;
    }
    


    public double[] solve(double[] b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int n = qrt.length;
      int m = qrt[0].length;
      if (b.length != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(m) });
      }
      

      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      double[] x = new double[n];
      double[] y = (double[])b.clone();
      

      for (int minor = 0; minor < FastMath.min(m, n); minor++)
      {
        double[] qrtMinor = qrt[minor];
        double dotProduct = 0.0D;
        for (int row = minor; row < m; row++) {
          dotProduct += y[row] * qrtMinor[row];
        }
        dotProduct /= rDiag[minor] * qrtMinor[minor];
        
        for (int row = minor; row < m; row++) {
          y[row] += dotProduct * qrtMinor[row];
        }
      }
      


      for (int row = rDiag.length - 1; row >= 0; row--) {
        y[row] /= rDiag[row];
        double yRow = y[row];
        double[] qrtRow = qrt[row];
        x[row] = yRow;
        for (int i = 0; i < row; i++) {
          y[i] -= yRow * qrtRow[i];
        }
      }
      
      return x;
    }
    
    public RealVector solve(RealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      try
      {
        return solve((ArrayRealVector)b);
      } catch (ClassCastException cce) {}
      return new ArrayRealVector(solve(b.getData()), false);
    }
    







    public ArrayRealVector solve(ArrayRealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      return new ArrayRealVector(solve(b.getDataRef()), false);
    }
    

    public RealMatrix solve(RealMatrix b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int n = qrt.length;
      int m = qrt[0].length;
      if (b.getRowDimension() != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(b.getRowDimension()), Integer.valueOf(b.getColumnDimension()), Integer.valueOf(m), "n" });
      }
      

      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      int columns = b.getColumnDimension();
      int blockSize = 52;
      int cBlocks = (columns + 52 - 1) / 52;
      double[][] xBlocks = BlockRealMatrix.createBlocksLayout(n, columns);
      double[][] y = new double[b.getRowDimension()][52];
      double[] alpha = new double[52];
      
      for (int kBlock = 0; kBlock < cBlocks; kBlock++) {
        int kStart = kBlock * 52;
        int kEnd = FastMath.min(kStart + 52, columns);
        int kWidth = kEnd - kStart;
        

        b.copySubMatrix(0, m - 1, kStart, kEnd - 1, y);
        

        for (int minor = 0; minor < FastMath.min(m, n); minor++) {
          double[] qrtMinor = qrt[minor];
          double factor = 1.0D / (rDiag[minor] * qrtMinor[minor]);
          
          Arrays.fill(alpha, 0, kWidth, 0.0D);
          for (int row = minor; row < m; row++) {
            double d = qrtMinor[row];
            double[] yRow = y[row];
            for (int k = 0; k < kWidth; k++) {
              alpha[k] += d * yRow[k];
            }
          }
          for (int k = 0; k < kWidth; k++) {
            alpha[k] *= factor;
          }
          
          for (int row = minor; row < m; row++) {
            double d = qrtMinor[row];
            double[] yRow = y[row];
            for (int k = 0; k < kWidth; k++) {
              yRow[k] += alpha[k] * d;
            }
          }
        }
        


        for (int j = rDiag.length - 1; j >= 0; j--) {
          int jBlock = j / 52;
          int jStart = jBlock * 52;
          double factor = 1.0D / rDiag[j];
          double[] yJ = y[j];
          double[] xBlock = xBlocks[(jBlock * cBlocks + kBlock)];
          int index = (j - jStart) * kWidth;
          for (int k = 0; k < kWidth; k++) {
            yJ[k] *= factor;
            xBlock[(index++)] = yJ[k];
          }
          
          double[] qrtJ = qrt[j];
          for (int i = 0; i < j; i++) {
            double rIJ = qrtJ[i];
            double[] yI = y[i];
            for (int k = 0; k < kWidth; k++) {
              yI[k] -= yJ[k] * rIJ;
            }
          }
        }
      }
      


      return new BlockRealMatrix(n, columns, xBlocks, false);
    }
    

    public RealMatrix getInverse()
      throws InvalidMatrixException
    {
      return solve(MatrixUtils.createRealIdentityMatrix(rDiag.length));
    }
  }
}

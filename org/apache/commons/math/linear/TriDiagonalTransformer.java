package org.apache.commons.math.linear;

import java.util.Arrays;
import org.apache.commons.math.util.FastMath;




















































class TriDiagonalTransformer
{
  private final double[][] householderVectors;
  private final double[] main;
  private final double[] secondary;
  private RealMatrix cachedQ;
  private RealMatrix cachedQt;
  private RealMatrix cachedT;
  
  public TriDiagonalTransformer(RealMatrix matrix)
    throws InvalidMatrixException
  {
    if (!matrix.isSquare()) {
      throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
    }
    
    int m = matrix.getRowDimension();
    householderVectors = matrix.getData();
    main = new double[m];
    secondary = new double[m - 1];
    cachedQ = null;
    cachedQt = null;
    cachedT = null;
    

    transform();
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
    if (cachedQt == null)
    {
      int m = householderVectors.length;
      cachedQt = MatrixUtils.createRealMatrix(m, m);
      

      for (int k = m - 1; k >= 1; k--) {
        double[] hK = householderVectors[(k - 1)];
        double inv = 1.0D / (secondary[(k - 1)] * hK[k]);
        cachedQt.setEntry(k, k, 1.0D);
        if (hK[k] != 0.0D) {
          double beta = 1.0D / secondary[(k - 1)];
          cachedQt.setEntry(k, k, 1.0D + beta * hK[k]);
          for (int i = k + 1; i < m; i++) {
            cachedQt.setEntry(k, i, beta * hK[i]);
          }
          for (int j = k + 1; j < m; j++) {
            beta = 0.0D;
            for (int i = k + 1; i < m; i++) {
              beta += cachedQt.getEntry(j, i) * hK[i];
            }
            beta *= inv;
            cachedQt.setEntry(j, k, beta * hK[k]);
            for (int i = k + 1; i < m; i++) {
              cachedQt.addToEntry(j, i, beta * hK[i]);
            }
          }
        }
      }
      cachedQt.setEntry(0, 0, 1.0D);
    }
    


    return cachedQt;
  }
  





  public RealMatrix getT()
  {
    if (cachedT == null)
    {
      int m = main.length;
      cachedT = MatrixUtils.createRealMatrix(m, m);
      for (int i = 0; i < m; i++) {
        cachedT.setEntry(i, i, main[i]);
        if (i > 0) {
          cachedT.setEntry(i, i - 1, secondary[(i - 1)]);
        }
        if (i < main.length - 1) {
          cachedT.setEntry(i, i + 1, secondary[i]);
        }
      }
    }
    


    return cachedT;
  }
  






  double[][] getHouseholderVectorsRef()
  {
    return householderVectors;
  }
  





  double[] getMainDiagonalRef()
  {
    return main;
  }
  





  double[] getSecondaryDiagonalRef()
  {
    return secondary;
  }
  




  private void transform()
  {
    int m = householderVectors.length;
    double[] z = new double[m];
    for (int k = 0; k < m - 1; k++)
    {

      double[] hK = householderVectors[k];
      main[k] = hK[k];
      double xNormSqr = 0.0D;
      for (int j = k + 1; j < m; j++) {
        double c = hK[j];
        xNormSqr += c * c;
      }
      double a = hK[(k + 1)] > 0.0D ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
      secondary[k] = a;
      if (a != 0.0D)
      {

        hK[(k + 1)] -= a;
        double beta = -1.0D / (a * hK[(k + 1)]);
        




        Arrays.fill(z, k + 1, m, 0.0D);
        for (int i = k + 1; i < m; i++) {
          double[] hI = householderVectors[i];
          double hKI = hK[i];
          double zI = hI[i] * hKI;
          for (int j = i + 1; j < m; j++) {
            double hIJ = hI[j];
            zI += hIJ * hK[j];
            z[j] += hIJ * hKI;
          }
          z[i] = (beta * (z[i] + zI));
        }
        

        double gamma = 0.0D;
        for (int i = k + 1; i < m; i++) {
          gamma += z[i] * hK[i];
        }
        gamma *= beta / 2.0D;
        

        for (int i = k + 1; i < m; i++) {
          z[i] -= gamma * hK[i];
        }
        


        for (int i = k + 1; i < m; i++) {
          double[] hI = householderVectors[i];
          for (int j = i; j < m; j++) {
            hI[j] -= hK[i] * z[j] + z[i] * hK[j];
          }
        }
      }
    }
    

    main[(m - 1)] = householderVectors[(m - 1)][(m - 1)];
  }
}

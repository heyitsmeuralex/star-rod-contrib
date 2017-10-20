package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

























































public class SingularValueDecompositionImpl
  implements SingularValueDecomposition
{
  private int m;
  private int n;
  private EigenDecomposition eigenDecomposition;
  private double[] singularValues;
  private RealMatrix cachedU;
  private RealMatrix cachedUt;
  private RealMatrix cachedS;
  private RealMatrix cachedV;
  private RealMatrix cachedVt;
  
  public SingularValueDecompositionImpl(RealMatrix matrix)
    throws InvalidMatrixException
  {
    m = matrix.getRowDimension();
    n = matrix.getColumnDimension();
    
    cachedU = null;
    cachedS = null;
    cachedV = null;
    cachedVt = null;
    
    double[][] localcopy = matrix.getData();
    double[][] matATA = new double[n][n];
    


    for (int i = 0; i < n; i++) {
      for (int j = i; j < n; j++) {
        matATA[i][j] = 0.0D;
        for (int k = 0; k < m; k++) {
          matATA[i][j] += localcopy[k][i] * localcopy[k][j];
        }
        matATA[j][i] = matATA[i][j];
      }
    }
    
    double[][] matAAT = new double[m][m];
    


    for (int i = 0; i < m; i++) {
      for (int j = i; j < m; j++) {
        matAAT[i][j] = 0.0D;
        for (int k = 0; k < n; k++) {
          matAAT[i][j] += localcopy[i][k] * localcopy[j][k];
        }
        matAAT[j][i] = matAAT[i][j];
      }
    }
    int p;
    if (m >= n) {
      int p = n;
      
      eigenDecomposition = new EigenDecompositionImpl(new Array2DRowRealMatrix(matATA), 1.0D);
      
      singularValues = eigenDecomposition.getRealEigenvalues();
      cachedV = eigenDecomposition.getV();
      
      eigenDecomposition = new EigenDecompositionImpl(new Array2DRowRealMatrix(matAAT), 1.0D);
      
      cachedU = eigenDecomposition.getV().getSubMatrix(0, m - 1, 0, p - 1);
    } else {
      p = m;
      
      eigenDecomposition = new EigenDecompositionImpl(new Array2DRowRealMatrix(matAAT), 1.0D);
      
      singularValues = eigenDecomposition.getRealEigenvalues();
      cachedU = eigenDecomposition.getV();
      

      eigenDecomposition = new EigenDecompositionImpl(new Array2DRowRealMatrix(matATA), 1.0D);
      
      cachedV = eigenDecomposition.getV().getSubMatrix(0, n - 1, 0, p - 1);
    }
    for (int i = 0; i < p; i++) {
      singularValues[i] = FastMath.sqrt(FastMath.abs(singularValues[i]));
    }
    



    for (int i = 0; i < p; i++) {
      RealVector tmp = cachedU.getColumnVector(i);
      double product = matrix.operate(cachedV.getColumnVector(i)).dotProduct(tmp);
      if (product < 0.0D) {
        cachedU.setColumnVector(i, tmp.mapMultiply(-1.0D));
      }
    }
  }
  
  public RealMatrix getU()
    throws InvalidMatrixException
  {
    return cachedU;
  }
  

  public RealMatrix getUT()
    throws InvalidMatrixException
  {
    if (cachedUt == null) {
      cachedUt = getU().transpose();
    }
    

    return cachedUt;
  }
  

  public RealMatrix getS()
    throws InvalidMatrixException
  {
    if (cachedS == null)
    {

      cachedS = MatrixUtils.createRealDiagonalMatrix(singularValues);
    }
    
    return cachedS;
  }
  
  public double[] getSingularValues() throws InvalidMatrixException
  {
    return (double[])singularValues.clone();
  }
  
  public RealMatrix getV()
    throws InvalidMatrixException
  {
    return cachedV;
  }
  

  public RealMatrix getVT()
    throws InvalidMatrixException
  {
    if (cachedVt == null) {
      cachedVt = getV().transpose();
    }
    

    return cachedVt;
  }
  



  public RealMatrix getCovariance(double minSingularValue)
  {
    int p = singularValues.length;
    int dimension = 0;
    while ((dimension < p) && (singularValues[dimension] >= minSingularValue)) {
      dimension++;
    }
    
    if (dimension == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE, new Object[] { Double.valueOf(minSingularValue), Double.valueOf(singularValues[0]) });
    }
    


    final double[][] data = new double[dimension][p];
    getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor()
    {


      public void visit(int row, int column, double value) {
        data[row][column] = (value / singularValues[row]); } }, 0, dimension - 1, 0, p - 1);
    


    RealMatrix jv = new Array2DRowRealMatrix(data, false);
    return jv.transpose().multiply(jv);
  }
  
  public double getNorm()
    throws InvalidMatrixException
  {
    return singularValues[0];
  }
  
  public double getConditionNumber() throws InvalidMatrixException
  {
    return singularValues[0] / singularValues[(singularValues.length - 1)];
  }
  
  public int getRank()
    throws IllegalStateException
  {
    double threshold = FastMath.max(m, n) * FastMath.ulp(singularValues[0]);
    
    for (int i = singularValues.length - 1; i >= 0; i--) {
      if (singularValues[i] > threshold) {
        return i + 1;
      }
    }
    return 0;
  }
  

  public DecompositionSolver getSolver()
  {
    return new Solver(singularValues, getUT(), getV(), getRank() == Math.max(m, n), null);
  }
  





  private static class Solver
    implements DecompositionSolver
  {
    private final RealMatrix pseudoInverse;
    




    private boolean nonSingular;
    





    private Solver(double[] singularValues, RealMatrix uT, RealMatrix v, boolean nonSingular)
    {
      double[][] suT = uT.getData();
      for (int i = 0; i < singularValues.length; i++) { double a;
        double a;
        if (singularValues[i] > 0.0D) {
          a = 1.0D / singularValues[i];
        } else {
          a = 0.0D;
        }
        double[] suTi = suT[i];
        for (int j = 0; j < suTi.length; j++) {
          suTi[j] *= a;
        }
      }
      pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
      this.nonSingular = nonSingular;
    }
    










    public double[] solve(double[] b)
      throws IllegalArgumentException
    {
      return pseudoInverse.operate(b);
    }
    











    public RealVector solve(RealVector b)
      throws IllegalArgumentException
    {
      return pseudoInverse.operate(b);
    }
    











    public RealMatrix solve(RealMatrix b)
      throws IllegalArgumentException
    {
      return pseudoInverse.multiply(b);
    }
    



    public boolean isNonSingular()
    {
      return nonSingular;
    }
    



    public RealMatrix getInverse()
    {
      return pseudoInverse;
    }
  }
}

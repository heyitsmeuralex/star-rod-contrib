package org.apache.commons.math.ode.nonstiff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.linear.Array2DRowFieldMatrix;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DefaultFieldMatrixChangingVisitor;
import org.apache.commons.math.linear.FieldDecompositionSolver;
import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.apache.commons.math.linear.MatrixUtils;
























































































































public class AdamsNordsieckTransformer
{
  private static final Map<Integer, AdamsNordsieckTransformer> CACHE = new HashMap();
  


  private final Array2DRowRealMatrix initialization;
  


  private final Array2DRowRealMatrix update;
  


  private final double[] c1;
  



  private AdamsNordsieckTransformer(int nSteps)
  {
    FieldMatrix<BigFraction> bigP = buildP(nSteps);
    FieldDecompositionSolver<BigFraction> pSolver = new FieldLUDecompositionImpl(bigP).getSolver();
    

    BigFraction[] u = new BigFraction[nSteps];
    Arrays.fill(u, BigFraction.ONE);
    BigFraction[] bigC1 = (BigFraction[])pSolver.solve(u);
    



    BigFraction[][] shiftedP = (BigFraction[][])bigP.getData();
    for (int i = shiftedP.length - 1; i > 0; i--)
    {
      shiftedP[i] = shiftedP[(i - 1)];
    }
    shiftedP[0] = new BigFraction[nSteps];
    Arrays.fill(shiftedP[0], BigFraction.ZERO);
    FieldMatrix<BigFraction> bigMSupdate = pSolver.solve(new Array2DRowFieldMatrix(shiftedP, false));
    


    bigP.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor(BigFraction.ZERO)
    {
      public BigFraction visit(int row, int column, BigFraction value)
      {
        return (column & 0x1) == 1 ? value : value.negate();
      }
    });
    FieldMatrix<BigFraction> bigRInverse = new FieldLUDecompositionImpl(bigP).getSolver().getInverse();
    


    initialization = MatrixUtils.bigFractionMatrixToRealMatrix(bigRInverse);
    update = MatrixUtils.bigFractionMatrixToRealMatrix(bigMSupdate);
    c1 = new double[nSteps];
    for (int i = 0; i < nSteps; i++) {
      c1[i] = bigC1[i].doubleValue();
    }
  }
  





  public static AdamsNordsieckTransformer getInstance(int nSteps)
  {
    synchronized (CACHE) {
      AdamsNordsieckTransformer t = (AdamsNordsieckTransformer)CACHE.get(Integer.valueOf(nSteps));
      if (t == null) {
        t = new AdamsNordsieckTransformer(nSteps);
        CACHE.put(Integer.valueOf(nSteps), t);
      }
      return t;
    }
  }
  




  public int getNSteps()
  {
    return c1.length;
  }
  













  private FieldMatrix<BigFraction> buildP(int nSteps)
  {
    BigFraction[][] pData = new BigFraction[nSteps][nSteps];
    
    for (int i = 0; i < pData.length; i++)
    {
      BigFraction[] pI = pData[i];
      int factor = -(i + 1);
      int aj = factor;
      for (int j = 0; j < pI.length; j++) {
        pI[j] = new BigFraction(aj * (j + 2));
        aj *= factor;
      }
    }
    
    return new Array2DRowFieldMatrix(pData, false);
  }
  







  public Array2DRowRealMatrix initializeHighOrderDerivatives(double[] first, double[][] multistep)
  {
    for (int i = 0; i < multistep.length; i++) {
      double[] msI = multistep[i];
      for (int j = 0; j < first.length; j++) {
        msI[j] -= first[j];
      }
    }
    return initialization.multiply(new Array2DRowRealMatrix(multistep, false));
  }
  










  public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(Array2DRowRealMatrix highOrder)
  {
    return update.multiply(highOrder);
  }
  














  public void updateHighOrderDerivativesPhase2(double[] start, double[] end, Array2DRowRealMatrix highOrder)
  {
    double[][] data = highOrder.getDataRef();
    for (int i = 0; i < data.length; i++) {
      double[] dataI = data[i];
      double c1I = c1[i];
      for (int j = 0; j < dataI.length; j++) {
        dataI[j] += c1I * (start[j] - end[j]);
      }
    }
  }
}

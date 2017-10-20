package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.VectorialConvergenceChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;






































public class GaussNewtonOptimizer
  extends AbstractLeastSquaresOptimizer
{
  private final boolean useLU;
  
  public GaussNewtonOptimizer(boolean useLU)
  {
    this.useLU = useLU;
  }
  



  public VectorialPointValuePair doOptimize()
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException
  {
    VectorialPointValuePair current = null;
    for (boolean converged = false; !converged;)
    {
      incrementIterationsCounter();
      

      VectorialPointValuePair previous = current;
      updateResidualsAndCost();
      updateJacobian();
      current = new VectorialPointValuePair(point, objective);
      

      double[] b = new double[cols];
      double[][] a = new double[cols][cols];
      for (int i = 0; i < rows; i++)
      {
        double[] grad = jacobian[i];
        double weight = residualsWeights[i];
        double residual = objective[i] - targetValues[i];
        

        double wr = weight * residual;
        for (int j = 0; j < cols; j++) {
          b[j] += wr * grad[j];
        }
        

        for (int k = 0; k < cols; k++) {
          double[] ak = a[k];
          double wgk = weight * grad[k];
          for (int l = 0; l < cols; l++) {
            ak[l] += wgk * grad[l];
          }
        }
      }
      


      try
      {
        RealMatrix mA = new BlockRealMatrix(a);
        DecompositionSolver solver = useLU ? new LUDecompositionImpl(mA).getSolver() : new QRDecompositionImpl(mA).getSolver();
        

        double[] dX = solver.solve(b);
        

        for (int i = 0; i < cols; i++) {
          point[i] += dX[i];
        }
      }
      catch (InvalidMatrixException e) {
        throw new OptimizationException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[0]);
      }
      

      if (previous != null) {
        converged = checker.converged(getIterations(), previous, current);
      }
    }
    


    return current;
  }
}

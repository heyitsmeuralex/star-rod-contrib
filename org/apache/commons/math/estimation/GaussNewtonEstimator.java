package org.apache.commons.math.estimation;

import java.io.Serializable;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.util.FastMath;













































@Deprecated
public class GaussNewtonEstimator
  extends AbstractEstimator
  implements Serializable
{
  private static final long serialVersionUID = 5485001826076289109L;
  private static final double DEFAULT_STEADY_STATE_THRESHOLD = 1.0E-6D;
  private static final double DEFAULT_CONVERGENCE = 1.0E-6D;
  private double steadyStateThreshold;
  private double convergence;
  
  public GaussNewtonEstimator()
  {
    steadyStateThreshold = 1.0E-6D;
    convergence = 1.0E-6D;
  }
  































  public GaussNewtonEstimator(int maxCostEval, double convergence, double steadyStateThreshold)
  {
    setMaxCostEval(maxCostEval);
    this.steadyStateThreshold = steadyStateThreshold;
    this.convergence = convergence;
  }
  




  public void setConvergence(double convergence)
  {
    this.convergence = convergence;
  }
  










  public void setSteadyStateThreshold(double steadyStateThreshold)
  {
    this.steadyStateThreshold = steadyStateThreshold;
  }
  
























  public void estimate(EstimationProblem problem)
    throws EstimationException
  {
    initializeEstimate(problem);
    

    double[] grad = new double[parameters.length];
    ArrayRealVector bDecrement = new ArrayRealVector(parameters.length);
    double[] bDecrementData = bDecrement.getDataRef();
    RealMatrix wGradGradT = MatrixUtils.createRealMatrix(parameters.length, parameters.length);
    

    double previous = Double.POSITIVE_INFINITY;
    
    do
    {
      incrementJacobianEvaluationsCounter();
      RealVector b = new ArrayRealVector(parameters.length);
      RealMatrix a = MatrixUtils.createRealMatrix(parameters.length, parameters.length);
      for (int i = 0; i < measurements.length; i++) {
        if (!measurements[i].isIgnored())
        {
          double weight = measurements[i].getWeight();
          double residual = measurements[i].getResidual();
          

          for (int j = 0; j < parameters.length; j++) {
            grad[j] = measurements[i].getPartial(parameters[j]);
            bDecrementData[j] = (weight * residual * grad[j]);
          }
          

          for (int k = 0; k < parameters.length; k++) {
            double gk = grad[k];
            for (int l = 0; l < parameters.length; l++) {
              wGradGradT.setEntry(k, l, weight * gk * grad[l]);
            }
          }
          

          a = a.add(wGradGradT);
          b = b.add(bDecrement);
        }
      }
      


      try
      {
        RealVector dX = new LUDecompositionImpl(a).getSolver().solve(b);
        

        for (int i = 0; i < parameters.length; i++) {
          parameters[i].setEstimate(parameters[i].getEstimate() + dX.getEntry(i));
        }
      }
      catch (InvalidMatrixException e) {
        throw new EstimationException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[0]);
      }
      

      previous = cost;
      updateResidualsAndCost();
    }
    while ((getCostEvaluations() < 2) || ((FastMath.abs(previous - cost) > cost * steadyStateThreshold) && (FastMath.abs(cost) > convergence)));
  }
}

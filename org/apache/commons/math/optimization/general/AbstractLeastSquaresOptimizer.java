package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.VectorialConvergenceChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.util.FastMath;














































































public abstract class AbstractLeastSquaresOptimizer
  implements DifferentiableMultivariateVectorialOptimizer
{
  public static final int DEFAULT_MAX_ITERATIONS = 100;
  protected VectorialConvergenceChecker checker;
  protected double[][] jacobian;
  protected int cols;
  protected int rows;
  protected double[] targetValues;
  protected double[] residualsWeights;
  protected double[] point;
  protected double[] objective;
  protected double[] residuals;
  protected double[][] wjacobian;
  protected double[] wresiduals;
  protected double cost;
  private int maxIterations;
  private int iterations;
  private int maxEvaluations;
  private int objectiveEvaluations;
  private int jacobianEvaluations;
  private DifferentiableMultivariateVectorialFunction function;
  private MultivariateMatrixFunction jF;
  
  protected AbstractLeastSquaresOptimizer()
  {
    setConvergenceChecker(new SimpleVectorialValueChecker());
    setMaxIterations(100);
    setMaxEvaluations(Integer.MAX_VALUE);
  }
  
  public void setMaxIterations(int maxIterations)
  {
    this.maxIterations = maxIterations;
  }
  
  public int getMaxIterations()
  {
    return maxIterations;
  }
  
  public int getIterations()
  {
    return iterations;
  }
  
  public void setMaxEvaluations(int maxEvaluations)
  {
    this.maxEvaluations = maxEvaluations;
  }
  
  public int getMaxEvaluations()
  {
    return maxEvaluations;
  }
  
  public int getEvaluations()
  {
    return objectiveEvaluations;
  }
  
  public int getJacobianEvaluations()
  {
    return jacobianEvaluations;
  }
  
  public void setConvergenceChecker(VectorialConvergenceChecker convergenceChecker)
  {
    checker = convergenceChecker;
  }
  
  public VectorialConvergenceChecker getConvergenceChecker()
  {
    return checker;
  }
  



  protected void incrementIterationsCounter()
    throws OptimizationException
  {
    if (++iterations > maxIterations) {
      throw new OptimizationException(new MaxIterationsExceededException(maxIterations));
    }
  }
  



  protected void updateJacobian()
    throws FunctionEvaluationException
  {
    jacobianEvaluations += 1;
    jacobian = jF.value(point);
    if (jacobian.length != rows) {
      throw new FunctionEvaluationException(point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(jacobian.length), Integer.valueOf(rows) });
    }
    
    for (int i = 0; i < rows; i++) {
      double[] ji = jacobian[i];
      double wi = FastMath.sqrt(residualsWeights[i]);
      for (int j = 0; j < cols; j++) {
        ji[j] *= -1.0D;
        wjacobian[i][j] = (ji[j] * wi);
      }
    }
  }
  






  protected void updateResidualsAndCost()
    throws FunctionEvaluationException
  {
    if (++objectiveEvaluations > maxEvaluations) {
      throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations), point);
    }
    
    objective = function.value(point);
    if (objective.length != rows) {
      throw new FunctionEvaluationException(point, LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(objective.length), Integer.valueOf(rows) });
    }
    
    cost = 0.0D;
    int index = 0;
    for (int i = 0; i < rows; i++) {
      double residual = targetValues[i] - objective[i];
      residuals[i] = residual;
      wresiduals[i] = (residual * FastMath.sqrt(residualsWeights[i]));
      cost += residualsWeights[i] * residual * residual;
      index += cols;
    }
    cost = FastMath.sqrt(cost);
  }
  










  public double getRMS()
  {
    return FastMath.sqrt(getChiSquare() / rows);
  }
  





  public double getChiSquare()
  {
    return cost * cost;
  }
  









  public double[][] getCovariances()
    throws FunctionEvaluationException, OptimizationException
  {
    updateJacobian();
    

    double[][] jTj = new double[cols][cols];
    for (int i = 0; i < cols; i++) {
      for (int j = i; j < cols; j++) {
        double sum = 0.0D;
        for (int k = 0; k < rows; k++) {
          sum += wjacobian[k][i] * wjacobian[k][j];
        }
        jTj[i][j] = sum;
        jTj[j][i] = sum;
      }
    }
    
    try
    {
      RealMatrix inverse = new LUDecompositionImpl(MatrixUtils.createRealMatrix(jTj)).getSolver().getInverse();
      
      return inverse.getData();
    } catch (InvalidMatrixException ime) {
      throw new OptimizationException(LocalizedFormats.UNABLE_TO_COMPUTE_COVARIANCE_SINGULAR_PROBLEM, new Object[0]);
    }
  }
  









  public double[] guessParametersErrors()
    throws FunctionEvaluationException, OptimizationException
  {
    if (rows <= cols) {
      throw new OptimizationException(LocalizedFormats.NO_DEGREES_OF_FREEDOM, new Object[] { Integer.valueOf(rows), Integer.valueOf(cols) });
    }
    

    double[] errors = new double[cols];
    double c = FastMath.sqrt(getChiSquare() / (rows - cols));
    double[][] covar = getCovariances();
    for (int i = 0; i < errors.length; i++) {
      errors[i] = (FastMath.sqrt(covar[i][i]) * c);
    }
    return errors;
  }
  



  public VectorialPointValuePair optimize(DifferentiableMultivariateVectorialFunction f, double[] target, double[] weights, double[] startPoint)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException
  {
    if (target.length != weights.length) {
      throw new OptimizationException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(target.length), Integer.valueOf(weights.length) });
    }
    


    iterations = 0;
    objectiveEvaluations = 0;
    jacobianEvaluations = 0;
    

    function = f;
    jF = f.jacobian();
    targetValues = ((double[])target.clone());
    residualsWeights = ((double[])weights.clone());
    point = ((double[])startPoint.clone());
    residuals = new double[target.length];
    

    rows = target.length;
    cols = point.length;
    jacobian = new double[rows][cols];
    
    wjacobian = new double[rows][cols];
    wresiduals = new double[rows];
    
    cost = Double.POSITIVE_INFINITY;
    
    return doOptimize();
  }
  
  protected abstract VectorialPointValuePair doOptimize()
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;
}

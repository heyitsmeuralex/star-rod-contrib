package org.apache.commons.math.estimation;

@Deprecated
public abstract interface Estimator
{
  public abstract void estimate(EstimationProblem paramEstimationProblem)
    throws EstimationException;
  
  public abstract double getRMS(EstimationProblem paramEstimationProblem);
  
  public abstract double[][] getCovariances(EstimationProblem paramEstimationProblem)
    throws EstimationException;
  
  public abstract double[] guessParametersErrors(EstimationProblem paramEstimationProblem)
    throws EstimationException;
}

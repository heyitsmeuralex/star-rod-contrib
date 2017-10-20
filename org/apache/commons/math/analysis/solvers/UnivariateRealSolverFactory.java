package org.apache.commons.math.analysis.solvers;























public abstract class UnivariateRealSolverFactory
{
  protected UnivariateRealSolverFactory() {}
  





















  public static UnivariateRealSolverFactory newInstance()
  {
    return new UnivariateRealSolverFactoryImpl();
  }
  
  public abstract UnivariateRealSolver newDefaultSolver();
  
  public abstract UnivariateRealSolver newBisectionSolver();
  
  public abstract UnivariateRealSolver newBrentSolver();
  
  public abstract UnivariateRealSolver newNewtonSolver();
  
  public abstract UnivariateRealSolver newSecantSolver();
}

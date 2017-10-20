package org.apache.commons.math.analysis.solvers;















public class UnivariateRealSolverFactoryImpl
  extends UnivariateRealSolverFactory
{
  public UnivariateRealSolverFactoryImpl() {}
  














  public UnivariateRealSolver newDefaultSolver()
  {
    return newBrentSolver();
  }
  

  public UnivariateRealSolver newBisectionSolver()
  {
    return new BisectionSolver();
  }
  

  public UnivariateRealSolver newBrentSolver()
  {
    return new BrentSolver();
  }
  

  public UnivariateRealSolver newNewtonSolver()
  {
    return new NewtonSolver();
  }
  

  public UnivariateRealSolver newSecantSolver()
  {
    return new SecantSolver();
  }
}

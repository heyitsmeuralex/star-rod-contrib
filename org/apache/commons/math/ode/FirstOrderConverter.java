package org.apache.commons.math.ode;












public class FirstOrderConverter
  implements FirstOrderDifferentialEquations
{
  private final SecondOrderDifferentialEquations equations;
  










  private final int dimension;
  










  private final double[] z;
  










  private final double[] zDot;
  










  private final double[] zDDot;
  











  public FirstOrderConverter(SecondOrderDifferentialEquations equations)
  {
    this.equations = equations;
    dimension = equations.getDimension();
    z = new double[dimension];
    zDot = new double[dimension];
    zDDot = new double[dimension];
  }
  




  public int getDimension()
  {
    return 2 * dimension;
  }
  








  public void computeDerivatives(double t, double[] y, double[] yDot)
    throws DerivativeException
  {
    System.arraycopy(y, 0, z, 0, dimension);
    System.arraycopy(y, dimension, zDot, 0, dimension);
    

    equations.computeSecondDerivatives(t, z, zDot, zDDot);
    

    System.arraycopy(zDot, 0, yDot, 0, dimension);
    System.arraycopy(zDDot, 0, yDot, dimension, dimension);
  }
}

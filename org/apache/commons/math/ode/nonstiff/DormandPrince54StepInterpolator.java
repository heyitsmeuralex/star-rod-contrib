package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.AbstractIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;










































































class DormandPrince54StepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final double A70 = 0.09114583333333333D;
  private static final double A72 = 0.44923629829290207D;
  private static final double A73 = 0.6510416666666666D;
  private static final double A74 = -0.322376179245283D;
  private static final double A75 = 0.13095238095238096D;
  private static final double D0 = -1.1270175653862835D;
  private static final double D2 = 2.675424484351598D;
  private static final double D3 = -5.685526961588504D;
  private static final double D4 = 3.5219323679207912D;
  private static final double D5 = -1.7672812570757455D;
  private static final double D6 = 2.382468931778144D;
  private static final long serialVersionUID = 4104157279605906956L;
  private double[] v1;
  private double[] v2;
  private double[] v3;
  private double[] v4;
  private boolean vectorsInitialized;
  
  public DormandPrince54StepInterpolator()
  {
    v1 = null;
    v2 = null;
    v3 = null;
    v4 = null;
    vectorsInitialized = false;
  }
  





  public DormandPrince54StepInterpolator(DormandPrince54StepInterpolator interpolator)
  {
    super(interpolator);
    
    if (v1 == null)
    {
      v1 = null;
      v2 = null;
      v3 = null;
      v4 = null;
      vectorsInitialized = false;
    }
    else
    {
      v1 = ((double[])v1.clone());
      v2 = ((double[])v2.clone());
      v3 = ((double[])v3.clone());
      v4 = ((double[])v4.clone());
      vectorsInitialized = vectorsInitialized;
    }
  }
  



  protected StepInterpolator doCopy()
  {
    return new DormandPrince54StepInterpolator(this);
  }
  



  public void reinitialize(AbstractIntegrator integrator, double[] y, double[][] yDotK, boolean forward)
  {
    super.reinitialize(integrator, y, yDotK, forward);
    v1 = null;
    v2 = null;
    v3 = null;
    v4 = null;
    vectorsInitialized = false;
  }
  

  public void storeTime(double t)
  {
    super.storeTime(t);
    vectorsInitialized = false;
  }
  



  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
    throws DerivativeException
  {
    if (!vectorsInitialized)
    {
      if (v1 == null) {
        v1 = new double[interpolatedState.length];
        v2 = new double[interpolatedState.length];
        v3 = new double[interpolatedState.length];
        v4 = new double[interpolatedState.length];
      }
      



      for (int i = 0; i < interpolatedState.length; i++) {
        double yDot0 = yDotK[0][i];
        double yDot2 = yDotK[2][i];
        double yDot3 = yDotK[3][i];
        double yDot4 = yDotK[4][i];
        double yDot5 = yDotK[5][i];
        double yDot6 = yDotK[6][i];
        v1[i] = (0.09114583333333333D * yDot0 + 0.44923629829290207D * yDot2 + 0.6510416666666666D * yDot3 + -0.322376179245283D * yDot4 + 0.13095238095238096D * yDot5);
        v2[i] = (yDot0 - v1[i]);
        v3[i] = (v1[i] - v2[i] - yDot6);
        v4[i] = (-1.1270175653862835D * yDot0 + 2.675424484351598D * yDot2 + -5.685526961588504D * yDot3 + 3.5219323679207912D * yDot4 + -1.7672812570757455D * yDot5 + 2.382468931778144D * yDot6);
      }
      
      vectorsInitialized = true;
    }
    


    double eta = 1.0D - theta;
    double twoTheta = 2.0D * theta;
    double dot2 = 1.0D - twoTheta;
    double dot3 = theta * (2.0D - 3.0D * theta);
    double dot4 = twoTheta * (1.0D + theta * (twoTheta - 3.0D));
    for (int i = 0; i < interpolatedState.length; i++) {
      interpolatedState[i] = (currentState[i] - oneMinusThetaH * (v1[i] - theta * (v2[i] + theta * (v3[i] + eta * v4[i]))));
      
      interpolatedDerivatives[i] = (v1[i] + dot2 * v2[i] + dot3 * v3[i] + dot4 * v4[i]);
    }
  }
}

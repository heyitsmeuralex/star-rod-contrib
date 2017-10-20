package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.AbstractIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ExtendedFirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.util.FastMath;



















































































public abstract class AdaptiveStepsizeIntegrator
  extends AbstractIntegrator
{
  protected final double scalAbsoluteTolerance;
  protected final double scalRelativeTolerance;
  protected final double[] vecAbsoluteTolerance;
  protected final double[] vecRelativeTolerance;
  protected int mainSetDimension;
  private double initialStep;
  private final double minStep;
  private final double maxStep;
  
  public AdaptiveStepsizeIntegrator(String name, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
  {
    super(name);
    
    this.minStep = FastMath.abs(minStep);
    this.maxStep = FastMath.abs(maxStep);
    initialStep = -1.0D;
    
    this.scalAbsoluteTolerance = scalAbsoluteTolerance;
    this.scalRelativeTolerance = scalRelativeTolerance;
    vecAbsoluteTolerance = null;
    vecRelativeTolerance = null;
    
    resetInternalState();
  }
  














  public AdaptiveStepsizeIntegrator(String name, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
  {
    super(name);
    
    this.minStep = minStep;
    this.maxStep = maxStep;
    initialStep = -1.0D;
    
    scalAbsoluteTolerance = 0.0D;
    scalRelativeTolerance = 0.0D;
    this.vecAbsoluteTolerance = ((double[])vecAbsoluteTolerance.clone());
    this.vecRelativeTolerance = ((double[])vecRelativeTolerance.clone());
    
    resetInternalState();
  }
  











  public void setInitialStepSize(double initialStepSize)
  {
    if ((initialStepSize < minStep) || (initialStepSize > maxStep)) {
      initialStep = -1.0D;
    } else {
      initialStep = initialStepSize;
    }
  }
  











  protected void sanityChecks(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y)
    throws IntegratorException
  {
    super.sanityChecks(equations, t0, y0, t, y);
    
    if ((equations instanceof ExtendedFirstOrderDifferentialEquations)) {
      mainSetDimension = ((ExtendedFirstOrderDifferentialEquations)equations).getMainSetDimension();
    } else {
      mainSetDimension = equations.getDimension();
    }
    
    if ((vecAbsoluteTolerance != null) && (vecAbsoluteTolerance.length != mainSetDimension)) {
      throw new IntegratorException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(mainSetDimension), Integer.valueOf(vecAbsoluteTolerance.length) });
    }
    

    if ((vecRelativeTolerance != null) && (vecRelativeTolerance.length != mainSetDimension)) {
      throw new IntegratorException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(mainSetDimension), Integer.valueOf(vecRelativeTolerance.length) });
    }
  }
  



















  public double initializeStep(FirstOrderDifferentialEquations equations, boolean forward, int order, double[] scale, double t0, double[] y0, double[] yDot0, double[] y1, double[] yDot1)
    throws DerivativeException
  {
    if (initialStep > 0.0D)
    {
      return forward ? initialStep : -initialStep;
    }
    



    double yOnScale2 = 0.0D;
    double yDotOnScale2 = 0.0D;
    for (int j = 0; j < scale.length; j++) {
      double ratio = y0[j] / scale[j];
      yOnScale2 += ratio * ratio;
      ratio = yDot0[j] / scale[j];
      yDotOnScale2 += ratio * ratio;
    }
    
    double h = (yOnScale2 < 1.0E-10D) || (yDotOnScale2 < 1.0E-10D) ? 1.0E-6D : 0.01D * FastMath.sqrt(yOnScale2 / yDotOnScale2);
    
    if (!forward) {
      h = -h;
    }
    

    for (int j = 0; j < y0.length; j++) {
      y0[j] += h * yDot0[j];
    }
    computeDerivatives(t0 + h, y1, yDot1);
    

    double yDDotOnScale = 0.0D;
    for (int j = 0; j < scale.length; j++) {
      double ratio = (yDot1[j] - yDot0[j]) / scale[j];
      yDDotOnScale += ratio * ratio;
    }
    yDDotOnScale = FastMath.sqrt(yDDotOnScale) / h;
    


    double maxInv2 = FastMath.max(FastMath.sqrt(yDotOnScale2), yDDotOnScale);
    double h1 = maxInv2 < 1.0E-15D ? FastMath.max(1.0E-6D, 0.001D * FastMath.abs(h)) : FastMath.pow(0.01D / maxInv2, 1.0D / order);
    

    h = FastMath.min(100.0D * FastMath.abs(h), h1);
    h = FastMath.max(h, 1.0E-12D * FastMath.abs(t0));
    if (h < getMinStep()) {
      h = getMinStep();
    }
    if (h > getMaxStep()) {
      h = getMaxStep();
    }
    if (!forward) {
      h = -h;
    }
    
    return h;
  }
  










  protected double filterStep(double h, boolean forward, boolean acceptSmall)
    throws IntegratorException
  {
    double filteredH = h;
    if (FastMath.abs(h) < minStep) {
      if (acceptSmall) {
        filteredH = forward ? minStep : -minStep;
      } else {
        throw new IntegratorException(LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, new Object[] { Double.valueOf(minStep), Double.valueOf(FastMath.abs(h)) });
      }
    }
    


    if (filteredH > maxStep) {
      filteredH = maxStep;
    } else if (filteredH < -maxStep) {
      filteredH = -maxStep;
    }
    
    return filteredH;
  }
  



  public abstract double integrate(FirstOrderDifferentialEquations paramFirstOrderDifferentialEquations, double paramDouble1, double[] paramArrayOfDouble1, double paramDouble2, double[] paramArrayOfDouble2)
    throws DerivativeException, IntegratorException;
  


  public double getCurrentStepStart()
  {
    return stepStart;
  }
  
  protected void resetInternalState()
  {
    stepStart = NaN.0D;
    stepSize = FastMath.sqrt(minStep * maxStep);
  }
  


  public double getMinStep()
  {
    return minStep;
  }
  


  public double getMaxStep()
  {
    return maxStep;
  }
}

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;
















































































































































public class AdamsBashforthIntegrator
  extends AdamsIntegrator
{
  private static final String METHOD_NAME = "Adams-Bashforth";
  
  public AdamsBashforthIntegrator(int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
    throws IllegalArgumentException
  {
    super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
  }
  














  public AdamsBashforthIntegrator(int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
    throws IllegalArgumentException
  {
    super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
  }
  





  public double integrate(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y)
    throws DerivativeException, IntegratorException
  {
    int n = y0.length;
    sanityChecks(equations, t0, y0, t, y);
    setEquations(equations);
    resetEvaluations();
    boolean forward = t > t0;
    

    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, n);
    }
    double[] yDot = new double[n];
    

    NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
    interpolator.reinitialize(y, forward);
    

    for (StepHandler handler : stepHandlers) {
      handler.reset();
    }
    setStateInitialized(false);
    

    start(t0, y, t);
    interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
    interpolator.storeTime(stepStart);
    int lastRow = nordsieck.getRowDimension() - 1;
    

    double hNew = stepSize;
    interpolator.rescale(hNew);
    

    isLastStep = false;
    do
    {
      double error = 10.0D;
      while (error >= 1.0D)
      {
        stepSize = hNew;
        

        error = 0.0D;
        for (int i = 0; i < mainSetDimension; i++) {
          double yScale = FastMath.abs(y[i]);
          double tol = vecAbsoluteTolerance == null ? scalAbsoluteTolerance + scalRelativeTolerance * yScale : vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * yScale;
          

          double ratio = nordsieck.getEntry(lastRow, i) / tol;
          error += ratio * ratio;
        }
        error = FastMath.sqrt(error / mainSetDimension);
        
        if (error >= 1.0D)
        {
          double factor = computeStepGrowShrinkFactor(error);
          hNew = filterStep(stepSize * factor, forward, false);
          interpolator.rescale(hNew);
        }
      }
      


      double stepEnd = stepStart + stepSize;
      interpolator.shift();
      interpolator.setInterpolatedTime(stepEnd);
      System.arraycopy(interpolator.getInterpolatedState(), 0, y, 0, y0.length);
      

      computeDerivatives(stepEnd, y, yDot);
      

      double[] predictedScaled = new double[y0.length];
      for (int j = 0; j < y0.length; j++) {
        predictedScaled[j] = (stepSize * yDot[j]);
      }
      Array2DRowRealMatrix nordsieckTmp = updateHighOrderDerivativesPhase1(nordsieck);
      updateHighOrderDerivativesPhase2(scaled, predictedScaled, nordsieckTmp);
      interpolator.reinitialize(stepEnd, stepSize, predictedScaled, nordsieckTmp);
      

      interpolator.storeTime(stepEnd);
      stepStart = acceptStep(interpolator, y, yDot, t);
      scaled = predictedScaled;
      nordsieck = nordsieckTmp;
      interpolator.reinitialize(stepEnd, stepSize, scaled, nordsieck);
      
      if (!isLastStep)
      {

        interpolator.storeTime(stepStart);
        
        if (resetOccurred)
        {

          start(stepStart, y, t);
          interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
        }
        

        double factor = computeStepGrowShrinkFactor(error);
        double scaledH = stepSize * factor;
        double nextT = stepStart + scaledH;
        boolean nextIsLast = nextT >= t;
        hNew = filterStep(scaledH, forward, nextIsLast);
        
        double filteredNextT = stepStart + hNew;
        boolean filteredNextIsLast = filteredNextT >= t;
        if (filteredNextIsLast) {
          hNew = t - stepStart;
        }
        
        interpolator.rescale(hNew);
      }
      
    }
    while (!isLastStep);
    
    double stopTime = stepStart;
    resetInternalState();
    return stopTime;
  }
}

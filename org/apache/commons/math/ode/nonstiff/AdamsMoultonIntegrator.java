package org.apache.commons.math.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;






























































































































































public class AdamsMoultonIntegrator
  extends AdamsIntegrator
{
  private static final String METHOD_NAME = "Adams-Moulton";
  
  public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
    throws IllegalArgumentException
  {
    super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
  }
  














  public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
    throws IllegalArgumentException
  {
    super("Adams-Moulton", nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
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
    double[] yDot = new double[y0.length];
    double[] yTmp = new double[y0.length];
    double[] predictedScaled = new double[y0.length];
    Array2DRowRealMatrix nordsieckTmp = null;
    

    NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
    interpolator.reinitialize(y, forward);
    

    for (StepHandler handler : stepHandlers) {
      handler.reset();
    }
    setStateInitialized(false);
    

    start(t0, y, t);
    interpolator.reinitialize(stepStart, stepSize, scaled, nordsieck);
    interpolator.storeTime(stepStart);
    
    double hNew = stepSize;
    interpolator.rescale(hNew);
    
    isLastStep = false;
    do
    {
      double error = 10.0D;
      while (error >= 1.0D)
      {
        stepSize = hNew;
        

        double stepEnd = stepStart + stepSize;
        interpolator.setInterpolatedTime(stepEnd);
        System.arraycopy(interpolator.getInterpolatedState(), 0, yTmp, 0, y0.length);
        

        computeDerivatives(stepEnd, yTmp, yDot);
        

        for (int j = 0; j < y0.length; j++) {
          predictedScaled[j] = (stepSize * yDot[j]);
        }
        nordsieckTmp = updateHighOrderDerivativesPhase1(nordsieck);
        updateHighOrderDerivativesPhase2(scaled, predictedScaled, nordsieckTmp);
        

        error = nordsieckTmp.walkInOptimizedOrder(new Corrector(y, predictedScaled, yTmp));
        
        if (error >= 1.0D)
        {
          double factor = computeStepGrowShrinkFactor(error);
          hNew = filterStep(stepSize * factor, forward, false);
          interpolator.rescale(hNew);
        }
      }
      

      double stepEnd = stepStart + stepSize;
      computeDerivatives(stepEnd, yTmp, yDot);
      

      double[] correctedScaled = new double[y0.length];
      for (int j = 0; j < y0.length; j++) {
        correctedScaled[j] = (stepSize * yDot[j]);
      }
      updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, nordsieckTmp);
      

      System.arraycopy(yTmp, 0, y, 0, n);
      interpolator.reinitialize(stepEnd, stepSize, correctedScaled, nordsieckTmp);
      interpolator.storeTime(stepStart);
      interpolator.shift();
      interpolator.storeTime(stepEnd);
      stepStart = acceptStep(interpolator, y, yDot, t);
      scaled = correctedScaled;
      nordsieck = nordsieckTmp;
      
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
      
    } while (!isLastStep);
    
    double stopTime = stepStart;
    stepStart = NaN.0D;
    stepSize = NaN.0D;
    return stopTime;
  }
  




  private class Corrector
    implements RealMatrixPreservingVisitor
  {
    private final double[] previous;
    



    private final double[] scaled;
    



    private final double[] before;
    



    private final double[] after;
    



    public Corrector(double[] previous, double[] scaled, double[] state)
    {
      this.previous = previous;
      this.scaled = scaled;
      after = state;
      before = ((double[])state.clone());
    }
    

    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
    {
      Arrays.fill(after, 0.0D);
    }
    
    public void visit(int row, int column, double value)
    {
      if ((row & 0x1) == 0) {
        after[column] -= value;
      } else {
        after[column] += value;
      }
    }
    









    public double end()
    {
      double error = 0.0D;
      for (int i = 0; i < after.length; i++) {
        after[i] += previous[i] + scaled[i];
        if (i < mainSetDimension) {
          double yScale = FastMath.max(FastMath.abs(previous[i]), FastMath.abs(after[i]));
          double tol = vecAbsoluteTolerance == null ? scalAbsoluteTolerance + scalRelativeTolerance * yScale : vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * yScale;
          

          double ratio = (after[i] - before[i]) / tol;
          error += ratio * ratio;
        }
      }
      
      return FastMath.sqrt(error / mainSetDimension);
    }
  }
}

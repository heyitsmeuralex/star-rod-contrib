package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;



























































































public abstract class EmbeddedRungeKuttaIntegrator
  extends AdaptiveStepsizeIntegrator
{
  private final boolean fsal;
  private final double[] c;
  private final double[][] a;
  private final double[] b;
  private final RungeKuttaStepInterpolator prototype;
  private final double exp;
  private double safety;
  private double minReduction;
  private double maxGrowth;
  
  protected EmbeddedRungeKuttaIntegrator(String name, boolean fsal, double[] c, double[][] a, double[] b, RungeKuttaStepInterpolator prototype, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
  {
    super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    
    this.fsal = fsal;
    this.c = c;
    this.a = a;
    this.b = b;
    this.prototype = prototype;
    
    exp = (-1.0D / getOrder());
    

    setSafety(0.9D);
    setMinReduction(0.2D);
    setMaxGrowth(10.0D);
  }
  




















  protected EmbeddedRungeKuttaIntegrator(String name, boolean fsal, double[] c, double[][] a, double[] b, RungeKuttaStepInterpolator prototype, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
  {
    super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    
    this.fsal = fsal;
    this.c = c;
    this.a = a;
    this.b = b;
    this.prototype = prototype;
    
    exp = (-1.0D / getOrder());
    

    setSafety(0.9D);
    setMinReduction(0.2D);
    setMaxGrowth(10.0D);
  }
  



  public abstract int getOrder();
  



  public double getSafety()
  {
    return safety;
  }
  


  public void setSafety(double safety)
  {
    this.safety = safety;
  }
  




  public double integrate(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y)
    throws DerivativeException, IntegratorException
  {
    sanityChecks(equations, t0, y0, t, y);
    setEquations(equations);
    resetEvaluations();
    boolean forward = t > t0;
    

    int stages = c.length + 1;
    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, y0.length);
    }
    double[][] yDotK = new double[stages][y0.length];
    double[] yTmp = new double[y0.length];
    double[] yDotTmp = new double[y0.length];
    
    AbstractStepInterpolator interpolator;
    AbstractStepInterpolator interpolator;
    if (requiresDenseOutput()) {
      RungeKuttaStepInterpolator rki = (RungeKuttaStepInterpolator)prototype.copy();
      rki.reinitialize(this, yTmp, yDotK, forward);
      interpolator = rki;
    } else {
      interpolator = new DummyStepInterpolator(yTmp, yDotK[(stages - 1)], forward);
    }
    interpolator.storeTime(t0);
    

    stepStart = t0;
    double hNew = 0.0D;
    boolean firstTime = true;
    for (StepHandler handler : stepHandlers) {
      handler.reset();
    }
    setStateInitialized(false);
    

    isLastStep = false;
    do
    {
      interpolator.shift();
      

      double error = 10.0D;
      while (error >= 1.0D)
      {
        if ((firstTime) || (!fsal))
        {
          computeDerivatives(stepStart, y, yDotK[0]);
        }
        
        if (firstTime) {
          double[] scale = new double[mainSetDimension];
          if (vecAbsoluteTolerance == null) {
            for (int i = 0; i < scale.length; i++) {
              scale[i] = (scalAbsoluteTolerance + scalRelativeTolerance * FastMath.abs(y[i]));
            }
          } else {
            for (int i = 0; i < scale.length; i++) {
              scale[i] = (vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * FastMath.abs(y[i]));
            }
          }
          hNew = initializeStep(equations, forward, getOrder(), scale, stepStart, y, yDotK[0], yTmp, yDotK[1]);
          
          firstTime = false;
        }
        
        stepSize = hNew;
        

        for (int k = 1; k < stages; k++)
        {
          for (int j = 0; j < y0.length; j++) {
            double sum = a[(k - 1)][0] * yDotK[0][j];
            for (int l = 1; l < k; l++) {
              sum += a[(k - 1)][l] * yDotK[l][j];
            }
            y[j] += stepSize * sum;
          }
          
          computeDerivatives(stepStart + c[(k - 1)] * stepSize, yTmp, yDotK[k]);
        }
        


        for (int j = 0; j < y0.length; j++) {
          double sum = b[0] * yDotK[0][j];
          for (int l = 1; l < stages; l++) {
            sum += b[l] * yDotK[l][j];
          }
          y[j] += stepSize * sum;
        }
        

        error = estimateError(yDotK, y, yTmp, stepSize);
        if (error >= 1.0D)
        {
          double factor = FastMath.min(maxGrowth, FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
          

          hNew = filterStep(stepSize * factor, forward, false);
        }
      }
      


      interpolator.storeTime(stepStart + stepSize);
      System.arraycopy(yTmp, 0, y, 0, y0.length);
      System.arraycopy(yDotK[(stages - 1)], 0, yDotTmp, 0, y0.length);
      stepStart = acceptStep(interpolator, y, yDotTmp, t);
      
      if (!isLastStep)
      {

        interpolator.storeTime(stepStart);
        
        if (fsal)
        {
          System.arraycopy(yDotTmp, 0, yDotK[0], 0, y0.length);
        }
        

        double factor = FastMath.min(maxGrowth, FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
        
        double scaledH = stepSize * factor;
        double nextT = stepStart + scaledH;
        boolean nextIsLast = nextT >= t;
        hNew = filterStep(scaledH, forward, nextIsLast);
        
        double filteredNextT = stepStart + hNew;
        boolean filteredNextIsLast = filteredNextT >= t;
        if (filteredNextIsLast) {
          hNew = t - stepStart;
        }
        
      }
      
    } while (!isLastStep);
    
    double stopTime = stepStart;
    resetInternalState();
    return stopTime;
  }
  



  public double getMinReduction()
  {
    return minReduction;
  }
  


  public void setMinReduction(double minReduction)
  {
    this.minReduction = minReduction;
  }
  


  public double getMaxGrowth()
  {
    return maxGrowth;
  }
  


  public void setMaxGrowth(double maxGrowth)
  {
    this.maxGrowth = maxGrowth;
  }
  
  protected abstract double estimateError(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble);
}

package org.apache.commons.math.ode.sampling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.util.FastMath;


























































public class NordsieckStepInterpolator
  extends AbstractStepInterpolator
{
  private static final long serialVersionUID = -7179861704951334960L;
  protected double[] stateVariation;
  private double scalingH;
  private double referenceTime;
  private double[] scaled;
  private Array2DRowRealMatrix nordsieck;
  
  public NordsieckStepInterpolator() {}
  
  public NordsieckStepInterpolator(NordsieckStepInterpolator interpolator)
  {
    super(interpolator);
    scalingH = scalingH;
    referenceTime = referenceTime;
    if (scaled != null) {
      scaled = ((double[])scaled.clone());
    }
    if (nordsieck != null) {
      nordsieck = new Array2DRowRealMatrix(nordsieck.getDataRef(), true);
    }
    if (stateVariation != null) {
      stateVariation = ((double[])stateVariation.clone());
    }
  }
  

  protected StepInterpolator doCopy()
  {
    return new NordsieckStepInterpolator(this);
  }
  







  public void reinitialize(double[] y, boolean forward)
  {
    super.reinitialize(y, forward);
    stateVariation = new double[y.length];
  }
  











  public void reinitialize(double time, double stepSize, double[] scaledDerivative, Array2DRowRealMatrix nordsieckVector)
  {
    referenceTime = time;
    scalingH = stepSize;
    scaled = scaledDerivative;
    nordsieck = nordsieckVector;
    

    setInterpolatedTime(getInterpolatedTime());
  }
  






  public void rescale(double stepSize)
  {
    double ratio = stepSize / scalingH;
    for (int i = 0; i < scaled.length; i++) {
      scaled[i] *= ratio;
    }
    
    double[][] nData = nordsieck.getDataRef();
    double power = ratio;
    for (int i = 0; i < nData.length; i++) {
      power *= ratio;
      double[] nDataI = nData[i];
      for (int j = 0; j < nDataI.length; j++) {
        nDataI[j] *= power;
      }
    }
    
    scalingH = stepSize;
  }
  















  public double[] getInterpolatedStateVariation()
    throws DerivativeException
  {
    getInterpolatedState();
    return stateVariation;
  }
  


  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
  {
    double x = interpolatedTime - referenceTime;
    double normalizedAbscissa = x / scalingH;
    
    Arrays.fill(stateVariation, 0.0D);
    Arrays.fill(interpolatedDerivatives, 0.0D);
    


    double[][] nData = nordsieck.getDataRef();
    for (int i = nData.length - 1; i >= 0; i--) {
      int order = i + 2;
      double[] nDataI = nData[i];
      double power = FastMath.pow(normalizedAbscissa, order);
      for (int j = 0; j < nDataI.length; j++) {
        double d = nDataI[j] * power;
        stateVariation[j] += d;
        interpolatedDerivatives[j] += order * d;
      }
    }
    
    for (int j = 0; j < currentState.length; j++) {
      stateVariation[j] += scaled[j] * normalizedAbscissa;
      interpolatedState[j] = (currentState[j] + stateVariation[j]);
      interpolatedDerivatives[j] = ((interpolatedDerivatives[j] + scaled[j] * normalizedAbscissa) / x);
    }
  }
  





  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    writeBaseExternal(out);
    

    out.writeDouble(scalingH);
    out.writeDouble(referenceTime);
    
    int n = currentState == null ? -1 : currentState.length;
    if (scaled == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      for (int j = 0; j < n; j++) {
        out.writeDouble(scaled[j]);
      }
    }
    
    if (nordsieck == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      out.writeObject(nordsieck);
    }
  }
  






  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    double t = readBaseExternal(in);
    

    scalingH = in.readDouble();
    referenceTime = in.readDouble();
    
    int n = currentState == null ? -1 : currentState.length;
    boolean hasScaled = in.readBoolean();
    if (hasScaled) {
      scaled = new double[n];
      for (int j = 0; j < n; j++) {
        scaled[j] = in.readDouble();
      }
    } else {
      scaled = null;
    }
    
    boolean hasNordsieck = in.readBoolean();
    if (hasNordsieck) {
      nordsieck = ((Array2DRowRealMatrix)in.readObject());
    } else {
      nordsieck = null;
    }
    
    if ((hasScaled) && (hasNordsieck))
    {
      stateVariation = new double[n];
      setInterpolatedTime(t);
    } else {
      stateVariation = null;
    }
  }
}

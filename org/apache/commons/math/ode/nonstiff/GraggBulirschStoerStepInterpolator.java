package org.apache.commons.math.ode.nonstiff;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;
























































































class GraggBulirschStoerStepInterpolator
  extends AbstractStepInterpolator
{
  private static final long serialVersionUID = 7320613236731409847L;
  private double[] y0Dot;
  private double[] y1;
  private double[] y1Dot;
  private double[][] yMidDots;
  private double[][] polynoms;
  private double[] errfac;
  private int currentDegree;
  
  public GraggBulirschStoerStepInterpolator()
  {
    y0Dot = null;
    y1 = null;
    y1Dot = null;
    yMidDots = ((double[][])null);
    resetTables(-1);
  }
  















  public GraggBulirschStoerStepInterpolator(double[] y, double[] y0Dot, double[] y1, double[] y1Dot, double[][] yMidDots, boolean forward)
  {
    super(y, forward);
    this.y0Dot = y0Dot;
    this.y1 = y1;
    this.y1Dot = y1Dot;
    this.yMidDots = yMidDots;
    
    resetTables(yMidDots.length + 4);
  }
  







  public GraggBulirschStoerStepInterpolator(GraggBulirschStoerStepInterpolator interpolator)
  {
    super(interpolator);
    
    int dimension = currentState.length;
    


    y0Dot = null;
    y1 = null;
    y1Dot = null;
    yMidDots = ((double[][])null);
    

    if (polynoms == null) {
      polynoms = ((double[][])null);
      currentDegree = -1;
    } else {
      resetTables(currentDegree);
      for (int i = 0; i < polynoms.length; i++) {
        polynoms[i] = new double[dimension];
        System.arraycopy(polynoms[i], 0, polynoms[i], 0, dimension);
      }
      
      currentDegree = currentDegree;
    }
  }
  






  private void resetTables(int maxDegree)
  {
    if (maxDegree < 0) {
      polynoms = ((double[][])null);
      errfac = null;
      currentDegree = -1;
    }
    else {
      double[][] newPols = new double[maxDegree + 1][];
      if (polynoms != null) {
        System.arraycopy(polynoms, 0, newPols, 0, polynoms.length);
        for (int i = polynoms.length; i < newPols.length; i++) {
          newPols[i] = new double[currentState.length];
        }
      } else {
        for (int i = 0; i < newPols.length; i++) {
          newPols[i] = new double[currentState.length];
        }
      }
      polynoms = newPols;
      

      if (maxDegree <= 4) {
        errfac = null;
      } else {
        errfac = new double[maxDegree - 4];
        for (int i = 0; i < errfac.length; i++) {
          int ip5 = i + 5;
          errfac[i] = (1.0D / (ip5 * ip5));
          double e = 0.5D * FastMath.sqrt((i + 1) / ip5);
          for (int j = 0; j <= i; j++) {
            errfac[i] *= e / (j + 1);
          }
        }
      }
      
      currentDegree = 0;
    }
  }
  



  protected StepInterpolator doCopy()
  {
    return new GraggBulirschStoerStepInterpolator(this);
  }
  





  public void computeCoefficients(int mu, double h)
  {
    if ((polynoms == null) || (polynoms.length <= mu + 4)) {
      resetTables(mu + 4);
    }
    
    currentDegree = (mu + 4);
    
    for (int i = 0; i < currentState.length; i++)
    {
      double yp0 = h * y0Dot[i];
      double yp1 = h * y1Dot[i];
      double ydiff = y1[i] - currentState[i];
      double aspl = ydiff - yp1;
      double bspl = yp0 - ydiff;
      
      polynoms[0][i] = currentState[i];
      polynoms[1][i] = ydiff;
      polynoms[2][i] = aspl;
      polynoms[3][i] = bspl;
      
      if (mu < 0) {
        return;
      }
      

      double ph0 = 0.5D * (currentState[i] + y1[i]) + 0.125D * (aspl + bspl);
      polynoms[4][i] = (16.0D * (yMidDots[0][i] - ph0));
      
      if (mu > 0) {
        double ph1 = ydiff + 0.25D * (aspl - bspl);
        polynoms[5][i] = (16.0D * (yMidDots[1][i] - ph1));
        
        if (mu > 1) {
          double ph2 = yp1 - yp0;
          polynoms[6][i] = (16.0D * (yMidDots[2][i] - ph2 + polynoms[4][i]));
          
          if (mu > 2) {
            double ph3 = 6.0D * (bspl - aspl);
            polynoms[7][i] = (16.0D * (yMidDots[3][i] - ph3 + 3.0D * polynoms[5][i]));
            
            for (int j = 4; j <= mu; j++) {
              double fac1 = 0.5D * j * (j - 1);
              double fac2 = 2.0D * fac1 * (j - 2) * (j - 3);
              polynoms[(j + 4)][i] = (16.0D * (yMidDots[j][i] + fac1 * polynoms[(j + 2)][i] - fac2 * polynoms[j][i]));
            }
          }
        }
      }
    }
  }
  






  public double estimateError(double[] scale)
  {
    double error = 0.0D;
    if (currentDegree >= 5) {
      for (int i = 0; i < scale.length; i++) {
        double e = polynoms[currentDegree][i] / scale[i];
        error += e * e;
      }
      error = FastMath.sqrt(error / scale.length) * errfac[(currentDegree - 5)];
    }
    return error;
  }
  



  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
  {
    int dimension = currentState.length;
    
    double oneMinusTheta = 1.0D - theta;
    double theta05 = theta - 0.5D;
    double tOmT = theta * oneMinusTheta;
    double t4 = tOmT * tOmT;
    double t4Dot = 2.0D * tOmT * (1.0D - 2.0D * theta);
    double dot1 = 1.0D / h;
    double dot2 = theta * (2.0D - 3.0D * theta) / h;
    double dot3 = ((3.0D * theta - 4.0D) * theta + 1.0D) / h;
    
    for (int i = 0; i < dimension; i++)
    {
      double p0 = polynoms[0][i];
      double p1 = polynoms[1][i];
      double p2 = polynoms[2][i];
      double p3 = polynoms[3][i];
      interpolatedState[i] = (p0 + theta * (p1 + oneMinusTheta * (p2 * theta + p3 * oneMinusTheta)));
      interpolatedDerivatives[i] = (dot1 * p1 + dot2 * p2 + dot3 * p3);
      
      if (currentDegree > 3) {
        double cDot = 0.0D;
        double c = polynoms[currentDegree][i];
        for (int j = currentDegree - 1; j > 3; j--) {
          double d = 1.0D / (j - 3);
          cDot = d * (theta05 * cDot + c);
          c = polynoms[j][i] + c * d * theta05;
        }
        interpolatedState[i] += t4 * c;
        interpolatedDerivatives[i] += (t4 * cDot + t4Dot * c) / h;
      }
    }
    

    if (h == 0.0D)
    {

      System.arraycopy(yMidDots[1], 0, interpolatedDerivatives, 0, dimension);
    }
  }
  



  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    int dimension = currentState == null ? -1 : currentState.length;
    

    writeBaseExternal(out);
    

    out.writeInt(currentDegree);
    for (int k = 0; k <= currentDegree; k++) {
      for (int l = 0; l < dimension; l++) {
        out.writeDouble(polynoms[k][l]);
      }
    }
  }
  




  public void readExternal(ObjectInput in)
    throws IOException
  {
    double t = readBaseExternal(in);
    int dimension = currentState == null ? -1 : currentState.length;
    

    int degree = in.readInt();
    resetTables(degree);
    currentDegree = degree;
    
    for (int k = 0; k <= currentDegree; k++) {
      for (int l = 0; l < dimension; l++) {
        polynoms[k][l] = in.readDouble();
      }
    }
    

    setInterpolatedTime(t);
  }
}

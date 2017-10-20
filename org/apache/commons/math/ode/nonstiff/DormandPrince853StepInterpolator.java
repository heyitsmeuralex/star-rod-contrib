package org.apache.commons.math.ode.nonstiff;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.math.ode.AbstractIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;






























































































































class DormandPrince853StepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final long serialVersionUID = 7152276390558450974L;
  private static final double B_01 = 0.054293734116568765D;
  private static final double B_06 = 4.450312892752409D;
  private static final double B_07 = 1.8915178993145003D;
  private static final double B_08 = -5.801203960010585D;
  private static final double B_09 = 0.3111643669578199D;
  private static final double B_10 = -0.1521609496625161D;
  private static final double B_11 = 0.20136540080403034D;
  private static final double B_12 = 0.04471061572777259D;
  private static final double C14 = 0.1D;
  private static final double K14_01 = 0.0018737681664791894D;
  private static final double K14_06 = -4.450312892752409D;
  private static final double K14_07 = -1.6380176890978755D;
  private static final double K14_08 = 5.554964922539782D;
  private static final double K14_09 = -0.4353557902216363D;
  private static final double K14_10 = 0.30545274794128174D;
  private static final double K14_11 = -0.19316434850839564D;
  private static final double K14_12 = -0.03714271806722689D;
  private static final double K14_13 = -0.008298D;
  private static final double C15 = 0.2D;
  private static final double K15_01 = -0.022459085953066622D;
  private static final double K15_06 = -4.422011983080043D;
  private static final double K15_07 = -1.8379759110070617D;
  private static final double K15_08 = 5.746280211439194D;
  private static final double K15_09 = -0.3111643669578199D;
  private static final double K15_10 = 0.1521609496625161D;
  private static final double K15_11 = -0.2014737481327276D;
  private static final double K15_12 = -0.04432804463693693D;
  private static final double K15_13 = -3.4046500868740456E-4D;
  private static final double K15_14 = 0.1413124436746325D;
  private static final double C16 = 0.7777777777777778D;
  private static final double K16_01 = -0.4831900357003607D;
  private static final double K16_06 = -9.147934308113573D;
  private static final double K16_07 = 5.791903296748099D;
  private static final double K16_08 = 9.870193778407696D;
  private static final double K16_09 = 0.04556282049746119D;
  private static final double K16_10 = 0.1521609496625161D;
  private static final double K16_11 = -0.20136540080403034D;
  private static final double K16_12 = -0.04471061572777259D;
  private static final double K16_13 = -0.0013990241651590145D;
  private static final double K16_14 = 2.9475147891527724D;
  private static final double K16_15 = -9.15095847217987D;
  private static final double[][] D = { { -8.428938276109013D, 0.5667149535193777D, -3.0689499459498917D, 2.38466765651207D, 2.1170345824450285D, -0.871391583777973D, 2.2404374302607883D, 0.6315787787694688D, -0.08899033645133331D, 18.148505520854727D, -9.194632392478356D, -4.436036387594894D }, { 10.427508642579134D, 242.28349177525817D, 165.20045171727028D, -374.5467547226902D, -22.113666853125302D, 7.733432668472264D, -30.674084731089398D, -9.332130526430229D, 15.697238121770845D, -31.139403219565178D, -9.35292435884448D, 35.81684148639408D }, { 19.985053242002433D, -387.0373087493518D, -189.17813819516758D, 527.8081592054236D, -11.573902539959631D, 6.8812326946963D, -1.0006050966910838D, 0.7777137798053443D, -2.778205752353508D, -60.19669523126412D, 84.32040550667716D, 11.99229113618279D }, { -25.69393346270375D, -154.18974869023643D, -231.5293791760455D, 357.6391179106141D, 93.4053241836243D, -37.45832313645163D, 104.0996495089623D, 29.8402934266605D, -43.53345659001114D, 96.32455395918828D, -39.17726167561544D, -149.72683625798564D } };
  











  private double[][] yDotKLast;
  










  private double[][] v;
  










  private boolean vectorsInitialized;
  











  public DormandPrince853StepInterpolator()
  {
    yDotKLast = ((double[][])null);
    v = ((double[][])null);
    vectorsInitialized = false;
  }
  





  public DormandPrince853StepInterpolator(DormandPrince853StepInterpolator interpolator)
  {
    super(interpolator);
    
    if (currentState == null)
    {
      yDotKLast = ((double[][])null);
      v = ((double[][])null);
      vectorsInitialized = false;
    }
    else
    {
      int dimension = currentState.length;
      
      yDotKLast = new double[3][];
      for (int k = 0; k < yDotKLast.length; k++) {
        yDotKLast[k] = new double[dimension];
        System.arraycopy(yDotKLast[k], 0, yDotKLast[k], 0, dimension);
      }
      

      v = new double[7][];
      for (int k = 0; k < v.length; k++) {
        v[k] = new double[dimension];
        System.arraycopy(v[k], 0, v[k], 0, dimension);
      }
      
      vectorsInitialized = vectorsInitialized;
    }
  }
  



  protected StepInterpolator doCopy()
  {
    return new DormandPrince853StepInterpolator(this);
  }
  



  public void reinitialize(AbstractIntegrator integrator, double[] y, double[][] yDotK, boolean forward)
  {
    super.reinitialize(integrator, y, yDotK, forward);
    
    int dimension = currentState.length;
    
    yDotKLast = new double[3][];
    for (int k = 0; k < yDotKLast.length; k++) {
      yDotKLast[k] = new double[dimension];
    }
    
    v = new double[7][];
    for (int k = 0; k < v.length; k++) {
      v[k] = new double[dimension];
    }
    
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
      if (v == null) {
        v = new double[7][];
        for (int k = 0; k < 7; k++) {
          v[k] = new double[interpolatedState.length];
        }
      }
      

      finalizeStep();
      

      for (int i = 0; i < interpolatedState.length; i++) {
        double yDot1 = yDotK[0][i];
        double yDot6 = yDotK[5][i];
        double yDot7 = yDotK[6][i];
        double yDot8 = yDotK[7][i];
        double yDot9 = yDotK[8][i];
        double yDot10 = yDotK[9][i];
        double yDot11 = yDotK[10][i];
        double yDot12 = yDotK[11][i];
        double yDot13 = yDotK[12][i];
        double yDot14 = yDotKLast[0][i];
        double yDot15 = yDotKLast[1][i];
        double yDot16 = yDotKLast[2][i];
        v[0][i] = (0.054293734116568765D * yDot1 + 4.450312892752409D * yDot6 + 1.8915178993145003D * yDot7 + -5.801203960010585D * yDot8 + 0.3111643669578199D * yDot9 + -0.1521609496625161D * yDot10 + 0.20136540080403034D * yDot11 + 0.04471061572777259D * yDot12);
        

        v[1][i] = (yDot1 - v[0][i]);
        v[2][i] = (v[0][i] - v[1][i] - yDotK[12][i]);
        for (int k = 0; k < D.length; k++) {
          v[(k + 3)][i] = (D[k][0] * yDot1 + D[k][1] * yDot6 + D[k][2] * yDot7 + D[k][3] * yDot8 + D[k][4] * yDot9 + D[k][5] * yDot10 + D[k][6] * yDot11 + D[k][7] * yDot12 + D[k][8] * yDot13 + D[k][9] * yDot14 + D[k][10] * yDot15 + D[k][11] * yDot16);
        }
      }
      



      vectorsInitialized = true;
    }
    

    double eta = 1.0D - theta;
    double twoTheta = 2.0D * theta;
    double theta2 = theta * theta;
    double dot1 = 1.0D - twoTheta;
    double dot2 = theta * (2.0D - 3.0D * theta);
    double dot3 = twoTheta * (1.0D + theta * (twoTheta - 3.0D));
    double dot4 = theta2 * (3.0D + theta * (5.0D * theta - 8.0D));
    double dot5 = theta2 * (3.0D + theta * (-12.0D + theta * (15.0D - 6.0D * theta)));
    double dot6 = theta2 * theta * (4.0D + theta * (-15.0D + theta * (18.0D - 7.0D * theta)));
    
    for (int i = 0; i < interpolatedState.length; i++) {
      interpolatedState[i] = (currentState[i] - oneMinusThetaH * (v[0][i] - theta * (v[1][i] + theta * (v[2][i] + eta * (v[3][i] + theta * (v[4][i] + eta * (v[5][i] + theta * v[6][i])))))));
      






      interpolatedDerivatives[i] = (v[0][i] + dot1 * v[1][i] + dot2 * v[2][i] + dot3 * v[3][i] + dot4 * v[4][i] + dot5 * v[5][i] + dot6 * v[6][i]);
    }
  }
  





  protected void doFinalize()
    throws DerivativeException
  {
    if (currentState == null)
    {
      return;
    }
    

    double[] yTmp = new double[currentState.length];
    double pT = getGlobalPreviousTime();
    

    for (int j = 0; j < currentState.length; j++) {
      double s = 0.0018737681664791894D * yDotK[0][j] + -4.450312892752409D * yDotK[5][j] + -1.6380176890978755D * yDotK[6][j] + 5.554964922539782D * yDotK[7][j] + -0.4353557902216363D * yDotK[8][j] + 0.30545274794128174D * yDotK[9][j] + -0.19316434850839564D * yDotK[10][j] + -0.03714271806722689D * yDotK[11][j] + -0.008298D * yDotK[12][j];
      

      yTmp[j] = (currentState[j] + h * s);
    }
    integrator.computeDerivatives(pT + 0.1D * h, yTmp, yDotKLast[0]);
    

    for (int j = 0; j < currentState.length; j++) {
      double s = -0.022459085953066622D * yDotK[0][j] + -4.422011983080043D * yDotK[5][j] + -1.8379759110070617D * yDotK[6][j] + 5.746280211439194D * yDotK[7][j] + -0.3111643669578199D * yDotK[8][j] + 0.1521609496625161D * yDotK[9][j] + -0.2014737481327276D * yDotK[10][j] + -0.04432804463693693D * yDotK[11][j] + -3.4046500868740456E-4D * yDotK[12][j] + 0.1413124436746325D * yDotKLast[0][j];
      


      yTmp[j] = (currentState[j] + h * s);
    }
    integrator.computeDerivatives(pT + 0.2D * h, yTmp, yDotKLast[1]);
    

    for (int j = 0; j < currentState.length; j++) {
      double s = -0.4831900357003607D * yDotK[0][j] + -9.147934308113573D * yDotK[5][j] + 5.791903296748099D * yDotK[6][j] + 9.870193778407696D * yDotK[7][j] + 0.04556282049746119D * yDotK[8][j] + 0.1521609496625161D * yDotK[9][j] + -0.20136540080403034D * yDotK[10][j] + -0.04471061572777259D * yDotK[11][j] + -0.0013990241651590145D * yDotK[12][j] + 2.9475147891527724D * yDotKLast[0][j] + -9.15095847217987D * yDotKLast[1][j];
      


      yTmp[j] = (currentState[j] + h * s);
    }
    integrator.computeDerivatives(pT + 0.7777777777777778D * h, yTmp, yDotKLast[2]);
  }
  



  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    try
    {
      finalizeStep();
    } catch (DerivativeException e) {
      IOException ioe = new IOException(e.getLocalizedMessage());
      ioe.initCause(e);
      throw ioe;
    }
    int dimension = currentState == null ? -1 : currentState.length;
    out.writeInt(dimension);
    for (int i = 0; i < dimension; i++) {
      out.writeDouble(yDotKLast[0][i]);
      out.writeDouble(yDotKLast[1][i]);
      out.writeDouble(yDotKLast[2][i]);
    }
    

    super.writeExternal(out);
  }
  




  public void readExternal(ObjectInput in)
    throws IOException
  {
    yDotKLast = new double[3][];
    int dimension = in.readInt();
    yDotKLast[0] = (dimension < 0 ? null : new double[dimension]);
    yDotKLast[1] = (dimension < 0 ? null : new double[dimension]);
    yDotKLast[2] = (dimension < 0 ? null : new double[dimension]);
    
    for (int i = 0; i < dimension; i++) {
      yDotKLast[0][i] = in.readDouble();
      yDotKLast[1][i] = in.readDouble();
      yDotKLast[2][i] = in.readDouble();
    }
    

    super.readExternal(in);
  }
}

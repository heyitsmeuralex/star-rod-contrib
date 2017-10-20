package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.util.FastMath;























































public class DormandPrince853Integrator
  extends EmbeddedRungeKuttaIntegrator
{
  private static final String METHOD_NAME = "Dormand-Prince 8 (5, 3)";
  private static final double[] STATIC_C = { (12.0D - 2.0D * FastMath.sqrt(6.0D)) / 135.0D, (6.0D - FastMath.sqrt(6.0D)) / 45.0D, (6.0D - FastMath.sqrt(6.0D)) / 30.0D, (6.0D + FastMath.sqrt(6.0D)) / 30.0D, 0.3333333333333333D, 0.25D, 0.3076923076923077D, 0.6512820512820513D, 0.6D, 0.8571428571428571D, 1.0D, 1.0D };
  





  private static final double[][] STATIC_A = { { (12.0D - 2.0D * FastMath.sqrt(6.0D)) / 135.0D }, { (6.0D - FastMath.sqrt(6.0D)) / 180.0D, (6.0D - FastMath.sqrt(6.0D)) / 60.0D }, { (6.0D - FastMath.sqrt(6.0D)) / 120.0D, 0.0D, (6.0D - FastMath.sqrt(6.0D)) / 40.0D }, { (462.0D + 107.0D * FastMath.sqrt(6.0D)) / 3000.0D, 0.0D, (-402.0D - 197.0D * FastMath.sqrt(6.0D)) / 1000.0D, (168.0D + 73.0D * FastMath.sqrt(6.0D)) / 375.0D }, { 0.037037037037037035D, 0.0D, 0.0D, (16.0D + FastMath.sqrt(6.0D)) / 108.0D, (16.0D - FastMath.sqrt(6.0D)) / 108.0D }, { 0.037109375D, 0.0D, 0.0D, (118.0D + 23.0D * FastMath.sqrt(6.0D)) / 1024.0D, (118.0D - 23.0D * FastMath.sqrt(6.0D)) / 1024.0D, -0.017578125D }, { 0.03709200011850479D, 0.0D, 0.0D, (51544.0D + 4784.0D * FastMath.sqrt(6.0D)) / 371293.0D, (51544.0D - 4784.0D * FastMath.sqrt(6.0D)) / 371293.0D, -0.015319437748624402D, 0.008273789163814023D }, { 0.6241109587160757D, 0.0D, 0.0D, (-1.324889724104E12D - 3.18801444819E11D * FastMath.sqrt(6.0D)) / 6.265569375E11D, (-1.324889724104E12D + 3.18801444819E11D * FastMath.sqrt(6.0D)) / 6.265569375E11D, 27.59209969944671D, 20.154067550477894D, -43.48988418106996D }, { 0.47766253643826434D, 0.0D, 0.0D, (-4521408.0D - 1137963.0D * FastMath.sqrt(6.0D)) / 2937500.0D, (-4521408.0D + 1137963.0D * FastMath.sqrt(6.0D)) / 2937500.0D, 21.230051448181193D, 15.279233632882423D, -33.28821096898486D, -0.020331201708508627D }, { -0.9371424300859873D, 0.0D, 0.0D, (354216.0D + 94326.0D * FastMath.sqrt(6.0D)) / 112847.0D, (354216.0D - 94326.0D * FastMath.sqrt(6.0D)) / 112847.0D, -8.149787010746927D, -18.52006565999696D, 22.739487099350505D, 2.4936055526796523D, -3.0467644718982196D }, { 2.273310147516538D, 0.0D, 0.0D, (-3457480.0D - 960905.0D * FastMath.sqrt(6.0D)) / 551636.0D, (-3457480.0D + 960905.0D * FastMath.sqrt(6.0D)) / 551636.0D, -17.9589318631188D, 27.94888452941996D, -2.8589982771350235D, -8.87285693353063D, 12.360567175794303D, 0.6433927460157636D }, { 0.054293734116568765D, 0.0D, 0.0D, 0.0D, 0.0D, 4.450312892752409D, 1.8915178993145003D, -5.801203960010585D, 0.3111643669578199D, -0.1521609496625161D, 0.20136540080403034D, 0.04471061572777259D } };
  

































































  private static final double[] STATIC_B = { 0.054293734116568765D, 0.0D, 0.0D, 0.0D, 0.0D, 4.450312892752409D, 1.8915178993145003D, -5.801203960010585D, 0.3111643669578199D, -0.1521609496625161D, 0.20136540080403034D, 0.04471061572777259D, 0.0D };
  



  private static final double E1_01 = 0.01312004499419488D;
  



  private static final double E1_06 = -1.2251564463762044D;
  



  private static final double E1_07 = -0.4957589496572502D;
  



  private static final double E1_08 = 1.6643771824549864D;
  



  private static final double E1_09 = -0.35032884874997366D;
  



  private static final double E1_10 = 0.3341791187130175D;
  



  private static final double E1_11 = 0.08192320648511571D;
  



  private static final double E1_12 = -0.022355307863886294D;
  



  private static final double E2_01 = -0.18980075407240762D;
  



  private static final double E2_06 = 4.450312892752409D;
  


  private static final double E2_07 = 1.8915178993145003D;
  


  private static final double E2_08 = -5.801203960010585D;
  


  private static final double E2_09 = -0.42268232132379197D;
  


  private static final double E2_10 = -0.1521609496625161D;
  


  private static final double E2_11 = 0.20136540080403034D;
  


  private static final double E2_12 = 0.022651792198360825D;
  



  public DormandPrince853Integrator(double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
  {
    super("Dormand-Prince 8 (5, 3)", true, STATIC_C, STATIC_A, STATIC_B, new DormandPrince853StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
  }
  












  public DormandPrince853Integrator(double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
  {
    super("Dormand-Prince 8 (5, 3)", true, STATIC_C, STATIC_A, STATIC_B, new DormandPrince853StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
  }
  



  public int getOrder()
  {
    return 8;
  }
  



  protected double estimateError(double[][] yDotK, double[] y0, double[] y1, double h)
  {
    double error1 = 0.0D;
    double error2 = 0.0D;
    
    for (int j = 0; j < mainSetDimension; j++) {
      double errSum1 = 0.01312004499419488D * yDotK[0][j] + -1.2251564463762044D * yDotK[5][j] + -0.4957589496572502D * yDotK[6][j] + 1.6643771824549864D * yDotK[7][j] + -0.35032884874997366D * yDotK[8][j] + 0.3341791187130175D * yDotK[9][j] + 0.08192320648511571D * yDotK[10][j] + -0.022355307863886294D * yDotK[11][j];
      


      double errSum2 = -0.18980075407240762D * yDotK[0][j] + 4.450312892752409D * yDotK[5][j] + 1.8915178993145003D * yDotK[6][j] + -5.801203960010585D * yDotK[7][j] + -0.42268232132379197D * yDotK[8][j] + -0.1521609496625161D * yDotK[9][j] + 0.20136540080403034D * yDotK[10][j] + 0.022651792198360825D * yDotK[11][j];
      



      double yScale = FastMath.max(FastMath.abs(y0[j]), FastMath.abs(y1[j]));
      double tol = vecAbsoluteTolerance == null ? scalAbsoluteTolerance + scalRelativeTolerance * yScale : vecAbsoluteTolerance[j] + vecRelativeTolerance[j] * yScale;
      

      double ratio1 = errSum1 / tol;
      error1 += ratio1 * ratio1;
      double ratio2 = errSum2 / tol;
      error2 += ratio2 * ratio2;
    }
    
    double den = error1 + 0.01D * error2;
    if (den <= 0.0D) {
      den = 1.0D;
    }
    
    return FastMath.abs(h) * error1 / FastMath.sqrt(mainSetDimension * den);
  }
}

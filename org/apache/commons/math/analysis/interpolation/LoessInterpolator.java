package org.apache.commons.math.analysis.interpolation;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;









































































public class LoessInterpolator
  implements UnivariateRealInterpolator, Serializable
{
  public static final double DEFAULT_BANDWIDTH = 0.3D;
  public static final int DEFAULT_ROBUSTNESS_ITERS = 2;
  public static final double DEFAULT_ACCURACY = 1.0E-12D;
  private static final long serialVersionUID = 5204927143605193821L;
  private final double bandwidth;
  private final int robustnessIters;
  private final double accuracy;
  
  public LoessInterpolator()
  {
    bandwidth = 0.3D;
    robustnessIters = 2;
    accuracy = 1.0E-12D;
  }
  





















  public LoessInterpolator(double bandwidth, int robustnessIters)
    throws MathException
  {
    this(bandwidth, robustnessIters, 1.0E-12D);
  }
  



















  public LoessInterpolator(double bandwidth, int robustnessIters, double accuracy)
    throws MathException
  {
    if ((bandwidth < 0.0D) || (bandwidth > 1.0D)) {
      throw new MathException(LocalizedFormats.BANDWIDTH_OUT_OF_INTERVAL, new Object[] { Double.valueOf(bandwidth) });
    }
    
    this.bandwidth = bandwidth;
    if (robustnessIters < 0) {
      throw new MathException(LocalizedFormats.NEGATIVE_ROBUSTNESS_ITERATIONS, new Object[] { Integer.valueOf(robustnessIters) });
    }
    this.robustnessIters = robustnessIters;
    this.accuracy = accuracy;
  }
  
















  public final PolynomialSplineFunction interpolate(double[] xval, double[] yval)
    throws MathException
  {
    return new SplineInterpolator().interpolate(xval, smooth(xval, yval));
  }
  














  public final double[] smooth(double[] xval, double[] yval, double[] weights)
    throws MathException
  {
    if (xval.length != yval.length) {
      throw new MathException(LocalizedFormats.MISMATCHED_LOESS_ABSCISSA_ORDINATE_ARRAYS, new Object[] { Integer.valueOf(xval.length), Integer.valueOf(yval.length) });
    }
    

    int n = xval.length;
    
    if (n == 0) {
      throw new MathException(LocalizedFormats.LOESS_EXPECTS_AT_LEAST_ONE_POINT, new Object[0]);
    }
    
    checkAllFiniteReal(xval, LocalizedFormats.NON_REAL_FINITE_ABSCISSA);
    checkAllFiniteReal(yval, LocalizedFormats.NON_REAL_FINITE_ORDINATE);
    checkAllFiniteReal(weights, LocalizedFormats.NON_REAL_FINITE_WEIGHT);
    
    checkStrictlyIncreasing(xval);
    
    if (n == 1) {
      return new double[] { yval[0] };
    }
    
    if (n == 2) {
      return new double[] { yval[0], yval[1] };
    }
    
    int bandwidthInPoints = (int)(bandwidth * n);
    
    if (bandwidthInPoints < 2) {
      throw new MathException(LocalizedFormats.TOO_SMALL_BANDWIDTH, new Object[] { Integer.valueOf(n), Double.valueOf(2.0D / n), Double.valueOf(bandwidth) });
    }
    

    double[] res = new double[n];
    
    double[] residuals = new double[n];
    double[] sortedResiduals = new double[n];
    
    double[] robustnessWeights = new double[n];
    



    Arrays.fill(robustnessWeights, 1.0D);
    
    for (int iter = 0; iter <= robustnessIters; iter++) {
      int[] bandwidthInterval = { 0, bandwidthInPoints - 1 };
      
      for (int i = 0; i < n; i++) {
        double x = xval[i];
        


        if (i > 0) {
          updateBandwidthInterval(xval, weights, i, bandwidthInterval);
        }
        
        int ileft = bandwidthInterval[0];
        int iright = bandwidthInterval[1];
        
        int edge;
        
        int edge;
        if (xval[i] - xval[ileft] > xval[iright] - xval[i]) {
          edge = ileft;
        } else {
          edge = iright;
        }
        







        double sumWeights = 0.0D;
        double sumX = 0.0D;
        double sumXSquared = 0.0D;
        double sumY = 0.0D;
        double sumXY = 0.0D;
        double denom = FastMath.abs(1.0D / (xval[edge] - x));
        for (int k = ileft; k <= iright; k++) {
          double xk = xval[k];
          double yk = yval[k];
          double dist = k < i ? x - xk : xk - x;
          double w = tricube(dist * denom) * robustnessWeights[k] * weights[k];
          double xkw = xk * w;
          sumWeights += w;
          sumX += xkw;
          sumXSquared += xk * xkw;
          sumY += yk * w;
          sumXY += yk * xkw;
        }
        
        double meanX = sumX / sumWeights;
        double meanY = sumY / sumWeights;
        double meanXY = sumXY / sumWeights;
        double meanXSquared = sumXSquared / sumWeights;
        double beta;
        double beta;
        if (FastMath.sqrt(FastMath.abs(meanXSquared - meanX * meanX)) < accuracy) {
          beta = 0.0D;
        } else {
          beta = (meanXY - meanX * meanY) / (meanXSquared - meanX * meanX);
        }
        
        double alpha = meanY - beta * meanX;
        
        res[i] = (beta * x + alpha);
        residuals[i] = FastMath.abs(yval[i] - res[i]);
      }
      


      if (iter == robustnessIters) {
        break;
      }
      





      System.arraycopy(residuals, 0, sortedResiduals, 0, n);
      Arrays.sort(sortedResiduals);
      double medianResidual = sortedResiduals[(n / 2)];
      
      if (FastMath.abs(medianResidual) < accuracy) {
        break;
      }
      
      for (int i = 0; i < n; i++) {
        double arg = residuals[i] / (6.0D * medianResidual);
        if (arg >= 1.0D) {
          robustnessWeights[i] = 0.0D;
        } else {
          double w = 1.0D - arg * arg;
          robustnessWeights[i] = (w * w);
        }
      }
    }
    
    return res;
  }
  












  public final double[] smooth(double[] xval, double[] yval)
    throws MathException
  {
    if (xval.length != yval.length) {
      throw new MathException(LocalizedFormats.MISMATCHED_LOESS_ABSCISSA_ORDINATE_ARRAYS, new Object[] { Integer.valueOf(xval.length), Integer.valueOf(yval.length) });
    }
    

    double[] unitWeights = new double[xval.length];
    Arrays.fill(unitWeights, 1.0D);
    
    return smooth(xval, yval, unitWeights);
  }
  















  private static void updateBandwidthInterval(double[] xval, double[] weights, int i, int[] bandwidthInterval)
  {
    int left = bandwidthInterval[0];
    int right = bandwidthInterval[1];
    


    int nextRight = nextNonzero(weights, right);
    if ((nextRight < xval.length) && (xval[nextRight] - xval[i] < xval[i] - xval[left])) {
      int nextLeft = nextNonzero(weights, bandwidthInterval[0]);
      bandwidthInterval[0] = nextLeft;
      bandwidthInterval[1] = nextRight;
    }
  }
  





  private static int nextNonzero(double[] weights, int i)
  {
    int j = i + 1;
    while ((j < weights.length) && (weights[j] == 0.0D)) {
      j++;
    }
    return j;
  }
  







  private static double tricube(double x)
  {
    double tmp = 1.0D - x * x * x;
    return tmp * tmp * tmp;
  }
  






  private static void checkAllFiniteReal(double[] values, Localizable pattern)
    throws MathException
  {
    for (int i = 0; i < values.length; i++) {
      double x = values[i];
      if ((Double.isInfinite(x)) || (Double.isNaN(x))) {
        throw new MathException(pattern, new Object[] { Integer.valueOf(i), Double.valueOf(x) });
      }
    }
  }
  







  private static void checkStrictlyIncreasing(double[] xval)
    throws MathException
  {
    for (int i = 0; i < xval.length; i++) {
      if ((i >= 1) && (xval[(i - 1)] >= xval[i])) {
        throw new MathException(LocalizedFormats.OUT_OF_ORDER_ABSCISSA_ARRAY, new Object[] { Integer.valueOf(i - 1), Double.valueOf(xval[(i - 1)]), Integer.valueOf(i), Double.valueOf(xval[i]) });
      }
    }
  }
}

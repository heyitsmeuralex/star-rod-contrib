package org.apache.commons.math.analysis.interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math.util.FastMath;















































public class MicrosphereInterpolatingFunction
  implements MultivariateRealFunction
{
  private final int dimension;
  private final List<MicrosphereSurfaceElement> microsphere;
  private final double brightnessExponent;
  private final Map<RealVector, Double> samples;
  
  private static class MicrosphereSurfaceElement
  {
    private final RealVector normal;
    private double brightestIllumination;
    private Map.Entry<RealVector, Double> brightestSample;
    
    MicrosphereSurfaceElement(double[] n)
    {
      normal = new ArrayRealVector(n);
    }
    



    RealVector normal()
    {
      return normal;
    }
    


    void reset()
    {
      brightestIllumination = 0.0D;
      brightestSample = null;
    }
    





    void store(double illuminationFromSample, Map.Entry<RealVector, Double> sample)
    {
      if (illuminationFromSample > brightestIllumination) {
        brightestIllumination = illuminationFromSample;
        brightestSample = sample;
      }
    }
    



    double illumination()
    {
      return brightestIllumination;
    }
    



    Map.Entry<RealVector, Double> sample()
    {
      return brightestSample;
    }
  }
  





















  public MicrosphereInterpolatingFunction(double[][] xval, double[] yval, int brightnessExponent, int microsphereElements, UnitSphereRandomVectorGenerator rand)
    throws DimensionMismatchException, NoDataException
  {
    if ((xval.length == 0) || (xval[0] == null)) {
      throw new NoDataException();
    }
    
    if (xval.length != yval.length) {
      throw new DimensionMismatchException(xval.length, yval.length);
    }
    
    dimension = xval[0].length;
    this.brightnessExponent = brightnessExponent;
    

    samples = new HashMap(yval.length);
    for (int i = 0; i < xval.length; i++) {
      double[] xvalI = xval[i];
      if (xvalI.length != dimension) {
        throw new DimensionMismatchException(xvalI.length, dimension);
      }
      
      samples.put(new ArrayRealVector(xvalI), Double.valueOf(yval[i]));
    }
    
    microsphere = new ArrayList(microsphereElements);
    

    for (int i = 0; i < microsphereElements; i++) {
      microsphere.add(new MicrosphereSurfaceElement(rand.nextVector()));
    }
  }
  





  public double value(double[] point)
  {
    RealVector p = new ArrayRealVector(point);
    

    for (MicrosphereSurfaceElement md : microsphere) {
      md.reset();
    }
    

    for (Iterator i$ = samples.entrySet().iterator(); i$.hasNext();) { sd = (Map.Entry)i$.next();
      

      diff = ((RealVector)sd.getKey()).subtract(p);
      diffNorm = diff.getNorm();
      
      if (FastMath.abs(diffNorm) < FastMath.ulp(1.0D))
      {

        return ((Double)sd.getValue()).doubleValue();
      }
      
      for (MicrosphereSurfaceElement md : microsphere) {
        double w = FastMath.pow(diffNorm, -brightnessExponent);
        md.store(cosAngle(diff, md.normal()) * w, sd);
      }
    }
    Map.Entry<RealVector, Double> sd;
    RealVector diff;
    double diffNorm;
    double value = 0.0D;
    double totalWeight = 0.0D;
    for (MicrosphereSurfaceElement md : microsphere) {
      double iV = md.illumination();
      Map.Entry<RealVector, Double> sd = md.sample();
      if (sd != null) {
        value += iV * ((Double)sd.getValue()).doubleValue();
        totalWeight += iV;
      }
    }
    
    return value / totalWeight;
  }
  







  private double cosAngle(RealVector v, RealVector w)
  {
    return v.dotProduct(w) / (v.getNorm() * w.getNorm());
  }
}

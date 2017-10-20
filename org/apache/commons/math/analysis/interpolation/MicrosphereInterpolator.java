package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.random.UnitSphereRandomVectorGenerator;














































public class MicrosphereInterpolator
  implements MultivariateRealInterpolator
{
  public static final int DEFAULT_MICROSPHERE_ELEMENTS = 2000;
  public static final int DEFAULT_BRIGHTNESS_EXPONENT = 2;
  private int microsphereElements;
  private int brightnessExponent;
  
  public MicrosphereInterpolator()
  {
    this(2000, 2);
  }
  







  public MicrosphereInterpolator(int microsphereElements, int brightnessExponent)
  {
    setMicropshereElements(microsphereElements);
    setBrightnessExponent(brightnessExponent);
  }
  



  public MultivariateRealFunction interpolate(double[][] xval, double[] yval)
    throws MathException, IllegalArgumentException
  {
    UnitSphereRandomVectorGenerator rand = new UnitSphereRandomVectorGenerator(xval[0].length);
    
    return new MicrosphereInterpolatingFunction(xval, yval, brightnessExponent, microsphereElements, rand);
  }
  








  public void setBrightnessExponent(int exponent)
  {
    if (exponent < 0) {
      throw new NotPositiveException(Integer.valueOf(exponent));
    }
    brightnessExponent = exponent;
  }
  




  public void setMicropshereElements(int elements)
  {
    if (elements <= 0) {
      throw new NotStrictlyPositiveException(Integer.valueOf(elements));
    }
    microsphereElements = elements;
  }
}

package org.apache.commons.math.random;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collection;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.BetaDistributionImpl;
import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.CauchyDistributionImpl;
import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
import org.apache.commons.math.distribution.ContinuousDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.IntegerDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.distribution.WeibullDistributionImpl;
import org.apache.commons.math.distribution.ZipfDistributionImpl;
import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;














































































public class RandomDataImpl
  implements RandomData, Serializable
{
  private static final long serialVersionUID = -626730818244969716L;
  private RandomGenerator rand = null;
  

  private SecureRandom secRand = null;
  






  public RandomDataImpl() {}
  






  public RandomDataImpl(RandomGenerator rand)
  {
    this.rand = rand;
  }
  

















  public String nextHexString(int len)
  {
    if (len <= 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, Integer.valueOf(len));
    }
    

    RandomGenerator ran = getRan();
    

    StringBuilder outBuffer = new StringBuilder();
    

    byte[] randomBytes = new byte[len / 2 + 1];
    ran.nextBytes(randomBytes);
    

    for (int i = 0; i < randomBytes.length; i++) {
      Integer c = Integer.valueOf(randomBytes[i]);
      





      String hex = Integer.toHexString(c.intValue() + 128);
      

      if (hex.length() == 1) {
        hex = "0" + hex;
      }
      outBuffer.append(hex);
    }
    return outBuffer.toString().substring(0, len);
  }
  










  public int nextInt(int lower, int upper)
  {
    if (lower >= upper) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Integer.valueOf(lower), Integer.valueOf(upper), false);
    }
    
    double r = getRan().nextDouble();
    return (int)(r * upper + (1.0D - r) * lower + r);
  }
  










  public long nextLong(long lower, long upper)
  {
    if (lower >= upper) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Long.valueOf(lower), Long.valueOf(upper), false);
    }
    
    double r = getRan().nextDouble();
    return (r * upper + (1.0D - r) * lower + r);
  }
  




















  public String nextSecureHexString(int len)
  {
    if (len <= 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, Integer.valueOf(len));
    }
    

    SecureRandom secRan = getSecRan();
    MessageDigest alg = null;
    try {
      alg = MessageDigest.getInstance("SHA-1");
    }
    catch (NoSuchAlgorithmException ex) {
      throw new MathInternalError(ex);
    }
    alg.reset();
    

    int numIter = len / 40 + 1;
    
    StringBuilder outBuffer = new StringBuilder();
    for (int iter = 1; iter < numIter + 1; iter++) {
      byte[] randomBytes = new byte[40];
      secRan.nextBytes(randomBytes);
      alg.update(randomBytes);
      

      byte[] hash = alg.digest();
      

      for (int i = 0; i < hash.length; i++) {
        Integer c = Integer.valueOf(hash[i]);
        





        String hex = Integer.toHexString(c.intValue() + 128);
        

        if (hex.length() == 1) {
          hex = "0" + hex;
        }
        outBuffer.append(hex);
      }
    }
    return outBuffer.toString().substring(0, len);
  }
  











  public int nextSecureInt(int lower, int upper)
  {
    if (lower >= upper) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Integer.valueOf(lower), Integer.valueOf(upper), false);
    }
    
    SecureRandom sec = getSecRan();
    return lower + (int)(sec.nextDouble() * (upper - lower + 1));
  }
  











  public long nextSecureLong(long lower, long upper)
  {
    if (lower >= upper) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Long.valueOf(lower), Long.valueOf(upper), false);
    }
    
    SecureRandom sec = getSecRan();
    return lower + (sec.nextDouble() * (upper - lower + 1L));
  }
  
















  public long nextPoisson(double mean)
  {
    if (mean <= 0.0D) {
      throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, Double.valueOf(mean));
    }
    
    RandomGenerator generator = getRan();
    
    double pivot = 40.0D;
    if (mean < 40.0D) {
      double p = FastMath.exp(-mean);
      long n = 0L;
      double r = 1.0D;
      double rnd = 1.0D;
      
      while (n < 1000.0D * mean) {
        rnd = generator.nextDouble();
        r *= rnd;
        if (r >= p) {
          n += 1L;
        } else {
          return n;
        }
      }
      return n;
    }
    double lambda = FastMath.floor(mean);
    double lambdaFractional = mean - lambda;
    double logLambda = FastMath.log(lambda);
    double logLambdaFactorial = MathUtils.factorialLog((int)lambda);
    long y2 = lambdaFractional < Double.MIN_VALUE ? 0L : nextPoisson(lambdaFractional);
    double delta = FastMath.sqrt(lambda * FastMath.log(32.0D * lambda / 3.141592653589793D + 1.0D));
    double halfDelta = delta / 2.0D;
    double twolpd = 2.0D * lambda + delta;
    double a1 = FastMath.sqrt(3.141592653589793D * twolpd) * FastMath.exp(0.0D * lambda);
    double a2 = twolpd / delta * FastMath.exp(-delta * (1.0D + delta) / twolpd);
    double aSum = a1 + a2 + 1.0D;
    double p1 = a1 / aSum;
    double p2 = a2 / aSum;
    double c1 = 1.0D / (8.0D * lambda);
    
    double x = 0.0D;
    double y = 0.0D;
    double v = 0.0D;
    int a = 0;
    double t = 0.0D;
    double qr = 0.0D;
    double qa = 0.0D;
    for (;;) {
      double u = nextUniform(0.0D, 1.0D);
      if (u <= p1) {
        double n = nextGaussian(0.0D, 1.0D);
        x = n * FastMath.sqrt(lambda + halfDelta) - 0.5D;
        if ((x > delta) || (x < -lambda)) {
          continue;
        }
        y = x < 0.0D ? FastMath.floor(x) : FastMath.ceil(x);
        double e = nextExponential(1.0D);
        v = -e - n * n / 2.0D + c1;
      } else {
        if (u > p1 + p2) {
          y = lambda;
          break;
        }
        x = delta + twolpd / delta * nextExponential(1.0D);
        y = FastMath.ceil(x);
        v = -nextExponential(1.0D) - delta * (x + 1.0D) / twolpd;
      }
      
      a = x < 0.0D ? 1 : 0;
      t = y * (y + 1.0D) / (2.0D * lambda);
      if ((v < -t) && (a == 0)) {
        y = lambda + y;
        break;
      }
      qr = t * ((2.0D * y + 1.0D) / (6.0D * lambda) - 1.0D);
      qa = qr - t * t / (3.0D * (lambda + a * (y + 1.0D)));
      if (v < qa) {
        y = lambda + y;
        break;
      }
      if (v <= qr)
      {

        if (v < y * logLambda - MathUtils.factorialLog((int)(y + lambda)) + logLambdaFactorial) {
          y = lambda + y;
          break;
        } }
    }
    return y2 + y;
  }
  












  public double nextGaussian(double mu, double sigma)
  {
    if (sigma <= 0.0D) {
      throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, Double.valueOf(sigma));
    }
    return sigma * getRan().nextGaussian() + mu;
  }
  













  public double nextExponential(double mean)
  {
    if (mean <= 0.0D) {
      throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, Double.valueOf(mean));
    }
    RandomGenerator generator = getRan();
    double unif = generator.nextDouble();
    while (unif == 0.0D) {
      unif = generator.nextDouble();
    }
    return -mean * FastMath.log(unif);
  }
  
















  public double nextUniform(double lower, double upper)
  {
    if (lower >= upper) {
      throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Double.valueOf(lower), Double.valueOf(upper), false);
    }
    
    RandomGenerator generator = getRan();
    

    double u = generator.nextDouble();
    while (u <= 0.0D) {
      u = generator.nextDouble();
    }
    
    return lower + u * (upper - lower);
  }
  









  public double nextBeta(double alpha, double beta)
    throws MathException
  {
    return nextInversionDeviate(new BetaDistributionImpl(alpha, beta));
  }
  









  public int nextBinomial(int numberOfTrials, double probabilityOfSuccess)
    throws MathException
  {
    return nextInversionDeviate(new BinomialDistributionImpl(numberOfTrials, probabilityOfSuccess));
  }
  









  public double nextCauchy(double median, double scale)
    throws MathException
  {
    return nextInversionDeviate(new CauchyDistributionImpl(median, scale));
  }
  








  public double nextChiSquare(double df)
    throws MathException
  {
    return nextInversionDeviate(new ChiSquaredDistributionImpl(df));
  }
  









  public double nextF(double numeratorDf, double denominatorDf)
    throws MathException
  {
    return nextInversionDeviate(new FDistributionImpl(numeratorDf, denominatorDf));
  }
  









  public double nextGamma(double shape, double scale)
    throws MathException
  {
    return nextInversionDeviate(new GammaDistributionImpl(shape, scale));
  }
  










  public int nextHypergeometric(int populationSize, int numberOfSuccesses, int sampleSize)
    throws MathException
  {
    return nextInversionDeviate(new HypergeometricDistributionImpl(populationSize, numberOfSuccesses, sampleSize));
  }
  









  public int nextPascal(int r, double p)
    throws MathException
  {
    return nextInversionDeviate(new PascalDistributionImpl(r, p));
  }
  








  public double nextT(double df)
    throws MathException
  {
    return nextInversionDeviate(new TDistributionImpl(df));
  }
  









  public double nextWeibull(double shape, double scale)
    throws MathException
  {
    return nextInversionDeviate(new WeibullDistributionImpl(shape, scale));
  }
  









  public int nextZipf(int numberOfElements, double exponent)
    throws MathException
  {
    return nextInversionDeviate(new ZipfDistributionImpl(numberOfElements, exponent));
  }
  








  private RandomGenerator getRan()
  {
    if (rand == null) {
      rand = new JDKRandomGenerator();
      rand.setSeed(System.currentTimeMillis());
    }
    return rand;
  }
  







  private SecureRandom getSecRan()
  {
    if (secRand == null) {
      secRand = new SecureRandom();
      secRand.setSeed(System.currentTimeMillis());
    }
    return secRand;
  }
  








  public void reSeed(long seed)
  {
    if (rand == null) {
      rand = new JDKRandomGenerator();
    }
    rand.setSeed(seed);
  }
  






  public void reSeedSecure()
  {
    if (secRand == null) {
      secRand = new SecureRandom();
    }
    secRand.setSeed(System.currentTimeMillis());
  }
  








  public void reSeedSecure(long seed)
  {
    if (secRand == null) {
      secRand = new SecureRandom();
    }
    secRand.setSeed(seed);
  }
  



  public void reSeed()
  {
    if (rand == null) {
      rand = new JDKRandomGenerator();
    }
    rand.setSeed(System.currentTimeMillis());
  }
  



















  public void setSecureAlgorithm(String algorithm, String provider)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    secRand = SecureRandom.getInstance(algorithm, provider);
  }
  





























  public int[] nextPermutation(int n, int k)
  {
    if (k > n) {
      throw new NumberIsTooLargeException(LocalizedFormats.PERMUTATION_EXCEEDS_N, Integer.valueOf(k), Integer.valueOf(n), true);
    }
    
    if (k == 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.PERMUTATION_SIZE, Integer.valueOf(k));
    }
    

    int[] index = getNatural(n);
    shuffle(index, n - k);
    int[] result = new int[k];
    for (int i = 0; i < k; i++) {
      result[i] = index[(n - i - 1)];
    }
    
    return result;
  }
  

















  public Object[] nextSample(Collection<?> c, int k)
  {
    int len = c.size();
    if (k > len) {
      throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE, Integer.valueOf(k), Integer.valueOf(len), true);
    }
    
    if (k <= 0) {
      throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(k));
    }
    
    Object[] objects = c.toArray();
    int[] index = nextPermutation(len, k);
    Object[] result = new Object[k];
    for (int i = 0; i < k; i++) {
      result[i] = objects[index[i]];
    }
    return result;
  }
  







  public double nextInversionDeviate(ContinuousDistribution distribution)
    throws MathException
  {
    return distribution.inverseCumulativeProbability(nextUniform(0.0D, 1.0D));
  }
  








  public int nextInversionDeviate(IntegerDistribution distribution)
    throws MathException
  {
    double target = nextUniform(0.0D, 1.0D);
    int glb = distribution.inverseCumulativeProbability(target);
    if (distribution.cumulativeProbability(glb) == 1.0D) {
      return glb;
    }
    return glb + 1;
  }
  











  private void shuffle(int[] list, int end)
  {
    int target = 0;
    for (int i = list.length - 1; i >= end; i--) {
      if (i == 0) {
        target = 0;
      } else {
        target = nextInt(0, i);
      }
      int temp = list[target];
      list[target] = list[i];
      list[i] = temp;
    }
  }
  






  private int[] getNatural(int n)
  {
    int[] natural = new int[n];
    for (int i = 0; i < n; i++) {
      natural[i] = i;
    }
    return natural;
  }
}

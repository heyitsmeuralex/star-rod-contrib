package org.apache.commons.math.random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;


















































public class ValueServer
{
  public static final int DIGEST_MODE = 0;
  public static final int REPLAY_MODE = 1;
  public static final int UNIFORM_MODE = 2;
  public static final int EXPONENTIAL_MODE = 3;
  public static final int GAUSSIAN_MODE = 4;
  public static final int CONSTANT_MODE = 5;
  private int mode = 5;
  

  private URL valuesFileURL = null;
  

  private double mu = 0.0D;
  

  private double sigma = 0.0D;
  

  private EmpiricalDistribution empiricalDistribution = null;
  

  private BufferedReader filePointer = null;
  

  private final RandomData randomData;
  


  public ValueServer()
  {
    randomData = new RandomDataImpl();
  }
  






  public ValueServer(RandomData randomData)
  {
    this.randomData = randomData;
  }
  





  public double getNext()
    throws IOException
  {
    switch (mode) {
    case 0:  return getNextDigest();
    case 1:  return getNextReplay();
    case 2:  return getNextUniform();
    case 3:  return getNextExponential();
    case 4:  return getNextGaussian();
    case 5:  return mu; }
    throw MathRuntimeException.createIllegalStateException(LocalizedFormats.UNKNOWN_MODE, new Object[] { Integer.valueOf(mode), "DIGEST_MODE", Integer.valueOf(0), "REPLAY_MODE", Integer.valueOf(1), "UNIFORM_MODE", Integer.valueOf(2), "EXPONENTIAL_MODE", Integer.valueOf(3), "GAUSSIAN_MODE", Integer.valueOf(4), "CONSTANT_MODE", Integer.valueOf(5) });
  }
  










  public void fill(double[] values)
    throws IOException
  {
    for (int i = 0; i < values.length; i++) {
      values[i] = getNext();
    }
  }
  






  public double[] fill(int length)
    throws IOException
  {
    double[] out = new double[length];
    for (int i = 0; i < length; i++) {
      out[i] = getNext();
    }
    return out;
  }
  










  public void computeDistribution()
    throws IOException
  {
    empiricalDistribution = new EmpiricalDistributionImpl();
    empiricalDistribution.load(valuesFileURL);
  }
  













  public void computeDistribution(int binCount)
    throws IOException
  {
    empiricalDistribution = new EmpiricalDistributionImpl(binCount);
    empiricalDistribution.load(valuesFileURL);
    mu = empiricalDistribution.getSampleStats().getMean();
    sigma = empiricalDistribution.getSampleStats().getStandardDeviation();
  }
  


  public int getMode()
  {
    return mode;
  }
  


  public void setMode(int mode)
  {
    this.mode = mode;
  }
  



  public URL getValuesFileURL()
  {
    return valuesFileURL;
  }
  



  public void setValuesFileURL(String url)
    throws MalformedURLException
  {
    valuesFileURL = new URL(url);
  }
  



  public void setValuesFileURL(URL url)
  {
    valuesFileURL = url;
  }
  


  public EmpiricalDistribution getEmpiricalDistribution()
  {
    return empiricalDistribution;
  }
  



  public void resetReplayFile()
    throws IOException
  {
    if (filePointer != null) {
      try {
        filePointer.close();
        filePointer = null;
      }
      catch (IOException ex) {}
    }
    
    filePointer = new BufferedReader(new InputStreamReader(valuesFileURL.openStream()));
  }
  



  public void closeReplayFile()
    throws IOException
  {
    if (filePointer != null) {
      filePointer.close();
      filePointer = null;
    }
  }
  


  public double getMu()
  {
    return mu;
  }
  


  public void setMu(double mu)
  {
    this.mu = mu;
  }
  


  public double getSigma()
  {
    return sigma;
  }
  


  public void setSigma(double sigma)
  {
    this.sigma = sigma;
  }
  











  private double getNextDigest()
  {
    if ((empiricalDistribution == null) || (empiricalDistribution.getBinStats().size() == 0))
    {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.DIGEST_NOT_INITIALIZED, new Object[0]);
    }
    return empiricalDistribution.getNextValue();
  }
  
















  private double getNextReplay()
    throws IOException
  {
    String str = null;
    if (filePointer == null) {
      resetReplayFile();
    }
    if ((str = filePointer.readLine()) == null)
    {
      closeReplayFile();
      resetReplayFile();
      if ((str = filePointer.readLine()) == null) {
        throw MathRuntimeException.createEOFException(LocalizedFormats.URL_CONTAINS_NO_DATA, new Object[] { valuesFileURL });
      }
    }
    
    return Double.valueOf(str).doubleValue();
  }
  




  private double getNextUniform()
  {
    return randomData.nextUniform(0.0D, 2.0D * mu);
  }
  




  private double getNextExponential()
  {
    return randomData.nextExponential(mu);
  }
  





  private double getNextGaussian()
  {
    return randomData.nextGaussian(mu, sigma);
  }
}

package org.apache.commons.math.random;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.util.FastMath;


















































public class EmpiricalDistributionImpl
  implements Serializable, EmpiricalDistribution
{
  private static final long serialVersionUID = 5729073523949762654L;
  private final List<SummaryStatistics> binStats;
  private SummaryStatistics sampleStats = null;
  

  private double max = Double.NEGATIVE_INFINITY;
  

  private double min = Double.POSITIVE_INFINITY;
  

  private double delta = 0.0D;
  

  private final int binCount;
  

  private boolean loaded = false;
  

  private double[] upperBounds = null;
  

  private final RandomData randomData = new RandomDataImpl();
  


  public EmpiricalDistributionImpl()
  {
    binCount = 1000;
    binStats = new ArrayList();
  }
  




  public EmpiricalDistributionImpl(int binCount)
  {
    this.binCount = binCount;
    binStats = new ArrayList();
  }
  





  public void load(double[] in)
  {
    DataAdapter da = new ArrayDataAdapter(in);
    try {
      da.computeStats();
      fillBinStats(in);
    } catch (IOException e) {
      throw new MathRuntimeException(e);
    }
    loaded = true;
  }
  





  public void load(URL url)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    try
    {
      DataAdapter da = new StreamDataAdapter(in);
      da.computeStats();
      if (sampleStats.getN() == 0L) {
        throw MathRuntimeException.createEOFException(LocalizedFormats.URL_CONTAINS_NO_DATA, new Object[] { url });
      }
      
      in = new BufferedReader(new InputStreamReader(url.openStream()));
      fillBinStats(in);
      loaded = true; return;
    } finally {
      try {
        in.close();
      }
      catch (IOException ex) {}
    }
  }
  





  public void load(File file)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(file));
    try {
      DataAdapter da = new StreamDataAdapter(in);
      da.computeStats();
      in = new BufferedReader(new FileReader(file));
      fillBinStats(in);
      loaded = true; return;
    } finally {
      try {
        in.close();
      }
      catch (IOException ex) {}
    }
  }
  




  private abstract class DataAdapter
  {
    private DataAdapter() {}
    




    public abstract void computeBinStats()
      throws IOException;
    




    public abstract void computeStats()
      throws IOException;
  }
  




  private class DataAdapterFactory
  {
    private DataAdapterFactory() {}
    



    public EmpiricalDistributionImpl.DataAdapter getAdapter(Object in)
    {
      if ((in instanceof BufferedReader)) {
        BufferedReader inputStream = (BufferedReader)in;
        return new EmpiricalDistributionImpl.StreamDataAdapter(EmpiricalDistributionImpl.this, inputStream); }
      if ((in instanceof double[])) {
        double[] inputArray = (double[])in;
        return new EmpiricalDistributionImpl.ArrayDataAdapter(EmpiricalDistributionImpl.this, inputArray);
      }
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INPUT_DATA_FROM_UNSUPPORTED_DATASOURCE, new Object[] { in.getClass().getName(), BufferedReader.class.getName(), [D.class.getName() });
    }
  }
  





  private class StreamDataAdapter
    extends EmpiricalDistributionImpl.DataAdapter
  {
    private BufferedReader inputStream;
    





    public StreamDataAdapter(BufferedReader in)
    {
      super(null);
      inputStream = in;
    }
    
    public void computeBinStats()
      throws IOException
    {
      String str = null;
      double val = 0.0D;
      while ((str = inputStream.readLine()) != null) {
        val = Double.parseDouble(str);
        SummaryStatistics stats = (SummaryStatistics)binStats.get(EmpiricalDistributionImpl.this.findBin(val));
        stats.addValue(val);
      }
      
      inputStream.close();
      inputStream = null;
    }
    
    public void computeStats()
      throws IOException
    {
      String str = null;
      double val = 0.0D;
      sampleStats = new SummaryStatistics();
      while ((str = inputStream.readLine()) != null) {
        val = Double.valueOf(str).doubleValue();
        sampleStats.addValue(val);
      }
      inputStream.close();
      inputStream = null;
    }
  }
  




  private class ArrayDataAdapter
    extends EmpiricalDistributionImpl.DataAdapter
  {
    private double[] inputArray;
    



    public ArrayDataAdapter(double[] in)
    {
      super(null);
      inputArray = in;
    }
    
    public void computeStats()
      throws IOException
    {
      sampleStats = new SummaryStatistics();
      for (int i = 0; i < inputArray.length; i++) {
        sampleStats.addValue(inputArray[i]);
      }
    }
    
    public void computeBinStats()
      throws IOException
    {
      for (int i = 0; i < inputArray.length; i++) {
        SummaryStatistics stats = (SummaryStatistics)binStats.get(EmpiricalDistributionImpl.this.findBin(inputArray[i]));
        
        stats.addValue(inputArray[i]);
      }
    }
  }
  





  private void fillBinStats(Object in)
    throws IOException
  {
    min = sampleStats.getMin();
    max = sampleStats.getMax();
    delta = ((max - min) / Double.valueOf(binCount).doubleValue());
    

    if (!binStats.isEmpty()) {
      binStats.clear();
    }
    for (int i = 0; i < binCount; i++) {
      SummaryStatistics stats = new SummaryStatistics();
      binStats.add(i, stats);
    }
    

    DataAdapterFactory aFactory = new DataAdapterFactory(null);
    DataAdapter da = aFactory.getAdapter(in);
    da.computeBinStats();
    

    upperBounds = new double[binCount];
    upperBounds[0] = (((SummaryStatistics)binStats.get(0)).getN() / sampleStats.getN());
    
    for (int i = 1; i < binCount - 1; i++) {
      upperBounds[i] = (upperBounds[(i - 1)] + ((SummaryStatistics)binStats.get(i)).getN() / sampleStats.getN());
    }
    
    upperBounds[(binCount - 1)] = 1.0D;
  }
  





  private int findBin(double value)
  {
    return FastMath.min(FastMath.max((int)FastMath.ceil((value - min) / delta) - 1, 0), binCount - 1);
  }
  







  public double getNextValue()
    throws IllegalStateException
  {
    if (!loaded) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.DISTRIBUTION_NOT_LOADED, new Object[0]);
    }
    

    double x = FastMath.random();
    

    for (int i = 0; i < binCount; i++) {
      if (x <= upperBounds[i]) {
        SummaryStatistics stats = (SummaryStatistics)binStats.get(i);
        if (stats.getN() > 0L) {
          if (stats.getStandardDeviation() > 0.0D) {
            return randomData.nextGaussian(stats.getMean(), stats.getStandardDeviation());
          }
          
          return stats.getMean();
        }
      }
    }
    
    throw new MathRuntimeException(LocalizedFormats.NO_BIN_SELECTED, new Object[0]);
  }
  







  public StatisticalSummary getSampleStats()
  {
    return sampleStats;
  }
  




  public int getBinCount()
  {
    return binCount;
  }
  






  public List<SummaryStatistics> getBinStats()
  {
    return binStats;
  }
  












  public double[] getUpperBounds()
  {
    double[] binUpperBounds = new double[binCount];
    binUpperBounds[0] = (min + delta);
    for (int i = 1; i < binCount - 1; i++) {
      binUpperBounds[i] = (binUpperBounds[(i - 1)] + delta);
    }
    binUpperBounds[(binCount - 1)] = max;
    return binUpperBounds;
  }
  










  public double[] getGeneratorUpperBounds()
  {
    int len = upperBounds.length;
    double[] out = new double[len];
    System.arraycopy(upperBounds, 0, out, 0, len);
    return out;
  }
  




  public boolean isLoaded()
  {
    return loaded;
  }
}

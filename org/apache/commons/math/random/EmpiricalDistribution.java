package org.apache.commons.math.random;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

public abstract interface EmpiricalDistribution
{
  public abstract void load(double[] paramArrayOfDouble);
  
  public abstract void load(File paramFile)
    throws IOException;
  
  public abstract void load(URL paramURL)
    throws IOException;
  
  public abstract double getNextValue()
    throws IllegalStateException;
  
  public abstract StatisticalSummary getSampleStats()
    throws IllegalStateException;
  
  public abstract boolean isLoaded();
  
  public abstract int getBinCount();
  
  public abstract List<SummaryStatistics> getBinStats();
  
  public abstract double[] getUpperBounds();
}

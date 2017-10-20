package org.apache.commons.math.stat.regression;

public abstract interface MultipleLinearRegression
{
  public abstract double[] estimateRegressionParameters();
  
  public abstract double[][] estimateRegressionParametersVariance();
  
  public abstract double[] estimateResiduals();
  
  public abstract double estimateRegressandVariance();
  
  public abstract double[] estimateRegressionParametersStandardErrors();
}

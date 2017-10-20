package org.apache.commons.math.estimation;

@Deprecated
public abstract interface EstimationProblem
{
  public abstract WeightedMeasurement[] getMeasurements();
  
  public abstract EstimatedParameter[] getUnboundParameters();
  
  public abstract EstimatedParameter[] getAllParameters();
}

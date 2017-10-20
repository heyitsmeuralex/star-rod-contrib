package org.apache.commons.math.estimation;

import java.io.Serializable;














































@Deprecated
public class EstimatedParameter
  implements Serializable
{
  private static final long serialVersionUID = -555440800213416949L;
  protected double estimate;
  private final String name;
  private boolean bound;
  
  public EstimatedParameter(String name, double firstEstimate)
  {
    this.name = name;
    estimate = firstEstimate;
    bound = false;
  }
  








  public EstimatedParameter(String name, double firstEstimate, boolean bound)
  {
    this.name = name;
    estimate = firstEstimate;
    this.bound = bound;
  }
  



  public EstimatedParameter(EstimatedParameter parameter)
  {
    name = name;
    estimate = estimate;
    bound = bound;
  }
  


  public void setEstimate(double estimate)
  {
    this.estimate = estimate;
  }
  


  public double getEstimate()
  {
    return estimate;
  }
  


  public String getName()
  {
    return name;
  }
  



  public void setBound(boolean bound)
  {
    this.bound = bound;
  }
  

  public boolean isBound()
  {
    return bound;
  }
}

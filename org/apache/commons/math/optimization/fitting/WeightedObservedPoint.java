package org.apache.commons.math.optimization.fitting;

import java.io.Serializable;


































public class WeightedObservedPoint
  implements Serializable
{
  private static final long serialVersionUID = 5306874947404636157L;
  private final double weight;
  private final double x;
  private final double y;
  
  public WeightedObservedPoint(double weight, double x, double y)
  {
    this.weight = weight;
    this.x = x;
    this.y = y;
  }
  


  public double getWeight()
  {
    return weight;
  }
  


  public double getX()
  {
    return x;
  }
  


  public double getY()
  {
    return y;
  }
}

package org.apache.commons.math.stat.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;































public class Cluster<T extends Clusterable<T>>
  implements Serializable
{
  private static final long serialVersionUID = -3442297081515880464L;
  private final List<T> points;
  private final T center;
  
  public Cluster(T center)
  {
    this.center = center;
    points = new ArrayList();
  }
  



  public void addPoint(T point)
  {
    points.add(point);
  }
  



  public List<T> getPoints()
  {
    return points;
  }
  



  public T getCenter()
  {
    return center;
  }
}

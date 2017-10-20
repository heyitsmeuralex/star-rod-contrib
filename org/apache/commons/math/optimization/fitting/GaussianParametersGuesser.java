package org.apache.commons.math.optimization.fitting;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;


































public class GaussianParametersGuesser
{
  private final WeightedObservedPoint[] observations;
  private double[] parameters;
  
  public GaussianParametersGuesser(WeightedObservedPoint[] observations)
  {
    if (observations == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    if (observations.length < 3) {
      throw new NumberIsTooSmallException(Integer.valueOf(observations.length), Integer.valueOf(3), true);
    }
    this.observations = ((WeightedObservedPoint[])observations.clone());
  }
  




  public double[] guess()
  {
    if (parameters == null) {
      parameters = basicGuess(observations);
    }
    return (double[])parameters.clone();
  }
  






  private double[] basicGuess(WeightedObservedPoint[] points)
  {
    Arrays.sort(points, createWeightedObservedPointComparator());
    double[] params = new double[4];
    
    int minYIdx = findMinY(points);
    params[0] = points[minYIdx].getY();
    
    int maxYIdx = findMaxY(points);
    params[1] = points[maxYIdx].getY();
    params[2] = points[maxYIdx].getX();
    double fwhmApprox;
    try
    {
      double halfY = params[0] + (params[1] - params[0]) / 2.0D;
      double fwhmX1 = interpolateXAtY(points, maxYIdx, -1, halfY);
      double fwhmX2 = interpolateXAtY(points, maxYIdx, 1, halfY);
      fwhmApprox = fwhmX2 - fwhmX1;
    } catch (OutOfRangeException e) {
      fwhmApprox = points[(points.length - 1)].getX() - points[0].getX();
    }
    params[3] = (fwhmApprox / (2.0D * Math.sqrt(2.0D * Math.log(2.0D))));
    
    return params;
  }
  






  private int findMinY(WeightedObservedPoint[] points)
  {
    int minYIdx = 0;
    for (int i = 1; i < points.length; i++) {
      if (points[i].getY() < points[minYIdx].getY()) {
        minYIdx = i;
      }
    }
    return minYIdx;
  }
  






  private int findMaxY(WeightedObservedPoint[] points)
  {
    int maxYIdx = 0;
    for (int i = 1; i < points.length; i++) {
      if (points[i].getY() > points[maxYIdx].getY()) {
        maxYIdx = i;
      }
    }
    return maxYIdx;
  }
  















  private double interpolateXAtY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y)
    throws OutOfRangeException
  {
    if (idxStep == 0) {
      throw new ZeroException();
    }
    WeightedObservedPoint[] twoPoints = getInterpolationPointsForY(points, startIdx, idxStep, y);
    WeightedObservedPoint pointA = twoPoints[0];
    WeightedObservedPoint pointB = twoPoints[1];
    if (pointA.getY() == y) {
      return pointA.getX();
    }
    if (pointB.getY() == y) {
      return pointB.getX();
    }
    return pointA.getX() + (y - pointA.getY()) * (pointB.getX() - pointA.getX()) / (pointB.getY() - pointA.getY());
  }
  


















  private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y)
    throws OutOfRangeException
  {
    if (idxStep == 0) {
      throw new ZeroException();
    }
    int i = startIdx;
    for (; idxStep < 0 ? i + idxStep >= 0 : i + idxStep < points.length; 
        i += idxStep) {
      if (isBetween(y, points[i].getY(), points[(i + idxStep)].getY())) {
        return new WeightedObservedPoint[] { points[i], idxStep < 0 ? new WeightedObservedPoint[] { points[(i + idxStep)], points[i] } : points[(i + idxStep)] };
      }
    }
    


    double minY = Double.POSITIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    for (WeightedObservedPoint point : points) {
      minY = Math.min(minY, point.getY());
      maxY = Math.max(maxY, point.getY());
    }
    throw new OutOfRangeException(Double.valueOf(y), Double.valueOf(minY), Double.valueOf(maxY));
  }
  











  private boolean isBetween(double value, double boundary1, double boundary2)
  {
    return ((value >= boundary1) && (value <= boundary2)) || ((value >= boundary2) && (value <= boundary1));
  }
  






  private Comparator<WeightedObservedPoint> createWeightedObservedPointComparator()
  {
    new Comparator() {
      public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
        if ((p1 == null) && (p2 == null)) {
          return 0;
        }
        if (p1 == null) {
          return -1;
        }
        if (p2 == null) {
          return 1;
        }
        if (p1.getX() < p2.getX()) {
          return -1;
        }
        if (p1.getX() > p2.getX()) {
          return 1;
        }
        if (p1.getY() < p2.getY()) {
          return -1;
        }
        if (p1.getY() > p2.getY()) {
          return 1;
        }
        if (p1.getWeight() < p2.getWeight()) {
          return -1;
        }
        if (p1.getWeight() > p2.getWeight()) {
          return 1;
        }
        return 0;
      }
    };
  }
}

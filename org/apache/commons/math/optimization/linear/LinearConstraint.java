package org.apache.commons.math.optimization.linear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealVector;





























































public class LinearConstraint
  implements Serializable
{
  private static final long serialVersionUID = -764632794033034092L;
  private final transient RealVector coefficients;
  private final Relationship relationship;
  private final double value;
  
  public LinearConstraint(double[] coefficients, Relationship relationship, double value)
  {
    this(new ArrayRealVector(coefficients), relationship, value);
  }
  














  public LinearConstraint(RealVector coefficients, Relationship relationship, double value)
  {
    this.coefficients = coefficients;
    this.relationship = relationship;
    this.value = value;
  }
  




















  public LinearConstraint(double[] lhsCoefficients, double lhsConstant, Relationship relationship, double[] rhsCoefficients, double rhsConstant)
  {
    double[] sub = new double[lhsCoefficients.length];
    for (int i = 0; i < sub.length; i++) {
      lhsCoefficients[i] -= rhsCoefficients[i];
    }
    coefficients = new ArrayRealVector(sub, false);
    this.relationship = relationship;
    value = (rhsConstant - lhsConstant);
  }
  




















  public LinearConstraint(RealVector lhsCoefficients, double lhsConstant, Relationship relationship, RealVector rhsCoefficients, double rhsConstant)
  {
    coefficients = lhsCoefficients.subtract(rhsCoefficients);
    this.relationship = relationship;
    value = (rhsConstant - lhsConstant);
  }
  



  public RealVector getCoefficients()
  {
    return coefficients;
  }
  



  public Relationship getRelationship()
  {
    return relationship;
  }
  



  public double getValue()
  {
    return value;
  }
  


  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other instanceof LinearConstraint)) {
      LinearConstraint rhs = (LinearConstraint)other;
      return (relationship == relationship) && (value == value) && (coefficients.equals(coefficients));
    }
    

    return false;
  }
  

  public int hashCode()
  {
    return relationship.hashCode() ^ Double.valueOf(value).hashCode() ^ coefficients.hashCode();
  }
  





  private void writeObject(ObjectOutputStream oos)
    throws IOException
  {
    oos.defaultWriteObject();
    MatrixUtils.serializeRealVector(coefficients, oos);
  }
  




  private void readObject(ObjectInputStream ois)
    throws ClassNotFoundException, IOException
  {
    ois.defaultReadObject();
    MatrixUtils.deserializeRealVector(this, "coefficients", ois);
  }
}

package org.apache.commons.math.optimization.linear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealVector;







































public class LinearObjectiveFunction
  implements Serializable
{
  private static final long serialVersionUID = -4531815507568396090L;
  private final transient RealVector coefficients;
  private final double constantTerm;
  
  public LinearObjectiveFunction(double[] coefficients, double constantTerm)
  {
    this(new ArrayRealVector(coefficients), constantTerm);
  }
  



  public LinearObjectiveFunction(RealVector coefficients, double constantTerm)
  {
    this.coefficients = coefficients;
    this.constantTerm = constantTerm;
  }
  



  public RealVector getCoefficients()
  {
    return coefficients;
  }
  



  public double getConstantTerm()
  {
    return constantTerm;
  }
  




  public double getValue(double[] point)
  {
    return coefficients.dotProduct(point) + constantTerm;
  }
  




  public double getValue(RealVector point)
  {
    return coefficients.dotProduct(point) + constantTerm;
  }
  


  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other instanceof LinearObjectiveFunction)) {
      LinearObjectiveFunction rhs = (LinearObjectiveFunction)other;
      return (constantTerm == constantTerm) && (coefficients.equals(coefficients));
    }
    
    return false;
  }
  

  public int hashCode()
  {
    return Double.valueOf(constantTerm).hashCode() ^ coefficients.hashCode();
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

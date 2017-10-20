package org.apache.commons.math.optimization.linear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.MathUtils;






















































class SimplexTableau
  implements Serializable
{
  private static final String NEGATIVE_VAR_COLUMN_LABEL = "x-";
  private static final long serialVersionUID = -1369660067587938365L;
  private final LinearObjectiveFunction f;
  private final List<LinearConstraint> constraints;
  private final boolean restrictToNonNegative;
  private final List<String> columnLabels = new ArrayList();
  



  private transient RealMatrix tableau;
  



  private final int numDecisionVariables;
  



  private final int numSlackVariables;
  


  private int numArtificialVariables;
  


  private final double epsilon;
  



  SimplexTableau(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative, double epsilon)
  {
    this.f = f;
    this.constraints = normalizeConstraints(constraints);
    this.restrictToNonNegative = restrictToNonNegative;
    this.epsilon = epsilon;
    numDecisionVariables = (f.getCoefficients().getDimension() + (restrictToNonNegative ? 0 : 1));
    
    numSlackVariables = (getConstraintTypeCounts(Relationship.LEQ) + getConstraintTypeCounts(Relationship.GEQ));
    
    numArtificialVariables = (getConstraintTypeCounts(Relationship.EQ) + getConstraintTypeCounts(Relationship.GEQ));
    
    tableau = createTableau(goalType == GoalType.MAXIMIZE);
    initializeColumnLabels();
  }
  


  protected void initializeColumnLabels()
  {
    if (getNumObjectiveFunctions() == 2) {
      columnLabels.add("W");
    }
    columnLabels.add("Z");
    for (int i = 0; i < getOriginalNumDecisionVariables(); i++) {
      columnLabels.add("x" + i);
    }
    if (!restrictToNonNegative) {
      columnLabels.add("x-");
    }
    for (int i = 0; i < getNumSlackVariables(); i++) {
      columnLabels.add("s" + i);
    }
    for (int i = 0; i < getNumArtificialVariables(); i++) {
      columnLabels.add("a" + i);
    }
    columnLabels.add("RHS");
  }
  






  protected RealMatrix createTableau(boolean maximize)
  {
    int width = numDecisionVariables + numSlackVariables + numArtificialVariables + getNumObjectiveFunctions() + 1;
    
    int height = constraints.size() + getNumObjectiveFunctions();
    Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(height, width);
    

    if (getNumObjectiveFunctions() == 2) {
      matrix.setEntry(0, 0, -1.0D);
    }
    int zIndex = getNumObjectiveFunctions() == 1 ? 0 : 1;
    matrix.setEntry(zIndex, zIndex, maximize ? 1.0D : -1.0D);
    RealVector objectiveCoefficients = maximize ? f.getCoefficients().mapMultiply(-1.0D) : f.getCoefficients();
    
    copyArray(objectiveCoefficients.getData(), matrix.getDataRef()[zIndex]);
    matrix.setEntry(zIndex, width - 1, maximize ? f.getConstantTerm() : -1.0D * f.getConstantTerm());
    

    if (!restrictToNonNegative) {
      matrix.setEntry(zIndex, getSlackVariableOffset() - 1, getInvertedCoeffiecientSum(objectiveCoefficients));
    }
    


    int slackVar = 0;
    int artificialVar = 0;
    for (int i = 0; i < constraints.size(); i++) {
      LinearConstraint constraint = (LinearConstraint)constraints.get(i);
      int row = getNumObjectiveFunctions() + i;
      

      copyArray(constraint.getCoefficients().getData(), matrix.getDataRef()[row]);
      

      if (!restrictToNonNegative) {
        matrix.setEntry(row, getSlackVariableOffset() - 1, getInvertedCoeffiecientSum(constraint.getCoefficients()));
      }
      


      matrix.setEntry(row, width - 1, constraint.getValue());
      

      if (constraint.getRelationship() == Relationship.LEQ) {
        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, 1.0D);
      } else if (constraint.getRelationship() == Relationship.GEQ) {
        matrix.setEntry(row, getSlackVariableOffset() + slackVar++, -1.0D);
      }
      

      if ((constraint.getRelationship() == Relationship.EQ) || (constraint.getRelationship() == Relationship.GEQ))
      {
        matrix.setEntry(0, getArtificialVariableOffset() + artificialVar, 1.0D);
        matrix.setEntry(row, getArtificialVariableOffset() + artificialVar++, 1.0D);
        matrix.setRowVector(0, matrix.getRowVector(0).subtract(matrix.getRowVector(row)));
      }
    }
    
    return matrix;
  }
  




  public List<LinearConstraint> normalizeConstraints(Collection<LinearConstraint> originalConstraints)
  {
    List<LinearConstraint> normalized = new ArrayList();
    for (LinearConstraint constraint : originalConstraints) {
      normalized.add(normalize(constraint));
    }
    return normalized;
  }
  




  private LinearConstraint normalize(LinearConstraint constraint)
  {
    if (constraint.getValue() < 0.0D) {
      return new LinearConstraint(constraint.getCoefficients().mapMultiply(-1.0D), constraint.getRelationship().oppositeRelationship(), -1.0D * constraint.getValue());
    }
    

    return new LinearConstraint(constraint.getCoefficients(), constraint.getRelationship(), constraint.getValue());
  }
  




  protected final int getNumObjectiveFunctions()
  {
    return numArtificialVariables > 0 ? 2 : 1;
  }
  




  private int getConstraintTypeCounts(Relationship relationship)
  {
    int count = 0;
    for (LinearConstraint constraint : constraints) {
      if (constraint.getRelationship() == relationship) {
        count++;
      }
    }
    return count;
  }
  




  protected static double getInvertedCoeffiecientSum(RealVector coefficients)
  {
    double sum = 0.0D;
    for (double coefficient : coefficients.getData()) {
      sum -= coefficient;
    }
    return sum;
  }
  




  protected Integer getBasicRow(int col)
  {
    Integer row = null;
    for (int i = 0; i < getHeight(); i++) {
      if ((MathUtils.equals(getEntry(i, col), 1.0D, epsilon)) && (row == null)) {
        row = Integer.valueOf(i);
      } else if (!MathUtils.equals(getEntry(i, col), 0.0D, epsilon)) {
        return null;
      }
    }
    return row;
  }
  



  protected void dropPhase1Objective()
  {
    if (getNumObjectiveFunctions() == 1) {
      return;
    }
    
    List<Integer> columnsToDrop = new ArrayList();
    columnsToDrop.add(Integer.valueOf(0));
    

    for (int i = getNumObjectiveFunctions(); i < getArtificialVariableOffset(); i++) {
      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, epsilon) > 0) {
        columnsToDrop.add(Integer.valueOf(i));
      }
    }
    

    for (int i = 0; i < getNumArtificialVariables(); i++) {
      int col = i + getArtificialVariableOffset();
      if (getBasicRow(col) == null) {
        columnsToDrop.add(Integer.valueOf(col));
      }
    }
    
    double[][] matrix = new double[getHeight() - 1][getWidth() - columnsToDrop.size()];
    for (int i = 1; i < getHeight(); i++) {
      int col = 0;
      for (int j = 0; j < getWidth(); j++) {
        if (!columnsToDrop.contains(Integer.valueOf(j))) {
          matrix[(i - 1)][(col++)] = tableau.getEntry(i, j);
        }
      }
    }
    
    for (int i = columnsToDrop.size() - 1; i >= 0; i--) {
      columnLabels.remove(((Integer)columnsToDrop.get(i)).intValue());
    }
    
    tableau = new Array2DRowRealMatrix(matrix);
    numArtificialVariables = 0;
  }
  



  private void copyArray(double[] src, double[] dest)
  {
    System.arraycopy(src, 0, dest, getNumObjectiveFunctions(), src.length);
  }
  



  boolean isOptimal()
  {
    for (int i = getNumObjectiveFunctions(); i < getWidth() - 1; i++) {
      if (MathUtils.compareTo(tableau.getEntry(0, i), 0.0D, epsilon) < 0) {
        return false;
      }
    }
    return true;
  }
  




  protected RealPointValuePair getSolution()
  {
    int negativeVarColumn = columnLabels.indexOf("x-");
    Integer negativeVarBasicRow = negativeVarColumn > 0 ? getBasicRow(negativeVarColumn) : null;
    double mostNegative = negativeVarBasicRow == null ? 0.0D : getEntry(negativeVarBasicRow.intValue(), getRhsOffset());
    
    Set<Integer> basicRows = new HashSet();
    double[] coefficients = new double[getOriginalNumDecisionVariables()];
    for (int i = 0; i < coefficients.length; i++) {
      int colIndex = columnLabels.indexOf("x" + i);
      if (colIndex < 0) {
        coefficients[i] = 0.0D;
      }
      else {
        Integer basicRow = getBasicRow(colIndex);
        if (basicRows.contains(basicRow))
        {

          coefficients[i] = 0.0D;
        } else {
          basicRows.add(basicRow);
          coefficients[i] = ((basicRow == null ? 0.0D : getEntry(basicRow.intValue(), getRhsOffset())) - (restrictToNonNegative ? 0.0D : mostNegative));
        }
      }
    }
    
    return new RealPointValuePair(coefficients, f.getValue(coefficients));
  }
  








  protected void divideRow(int dividendRow, double divisor)
  {
    for (int j = 0; j < getWidth(); j++) {
      tableau.setEntry(dividendRow, j, tableau.getEntry(dividendRow, j) / divisor);
    }
  }
  










  protected void subtractRow(int minuendRow, int subtrahendRow, double multiple)
  {
    tableau.setRowVector(minuendRow, tableau.getRowVector(minuendRow).subtract(tableau.getRowVector(subtrahendRow).mapMultiply(multiple)));
  }
  




  protected final int getWidth()
  {
    return tableau.getColumnDimension();
  }
  



  protected final int getHeight()
  {
    return tableau.getRowDimension();
  }
  




  protected final double getEntry(int row, int column)
  {
    return tableau.getEntry(row, column);
  }
  





  protected final void setEntry(int row, int column, double value)
  {
    tableau.setEntry(row, column, value);
  }
  



  protected final int getSlackVariableOffset()
  {
    return getNumObjectiveFunctions() + numDecisionVariables;
  }
  



  protected final int getArtificialVariableOffset()
  {
    return getNumObjectiveFunctions() + numDecisionVariables + numSlackVariables;
  }
  



  protected final int getRhsOffset()
  {
    return getWidth() - 1;
  }
  









  protected final int getNumDecisionVariables()
  {
    return numDecisionVariables;
  }
  




  protected final int getOriginalNumDecisionVariables()
  {
    return f.getCoefficients().getDimension();
  }
  



  protected final int getNumSlackVariables()
  {
    return numSlackVariables;
  }
  



  protected final int getNumArtificialVariables()
  {
    return numArtificialVariables;
  }
  



  protected final double[][] getData()
  {
    return tableau.getData();
  }
  


  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other instanceof SimplexTableau)) {
      SimplexTableau rhs = (SimplexTableau)other;
      return (restrictToNonNegative == restrictToNonNegative) && (numDecisionVariables == numDecisionVariables) && (numSlackVariables == numSlackVariables) && (numArtificialVariables == numArtificialVariables) && (epsilon == epsilon) && (f.equals(f)) && (constraints.equals(constraints)) && (tableau.equals(tableau));
    }
    






    return false;
  }
  

  public int hashCode()
  {
    return Boolean.valueOf(restrictToNonNegative).hashCode() ^ numDecisionVariables ^ numSlackVariables ^ numArtificialVariables ^ Double.valueOf(epsilon).hashCode() ^ f.hashCode() ^ constraints.hashCode() ^ tableau.hashCode();
  }
  










  private void writeObject(ObjectOutputStream oos)
    throws IOException
  {
    oos.defaultWriteObject();
    MatrixUtils.serializeRealMatrix(tableau, oos);
  }
  




  private void readObject(ObjectInputStream ois)
    throws ClassNotFoundException, IOException
  {
    ois.defaultReadObject();
    MatrixUtils.deserializeRealMatrix(this, "tableau", ois);
  }
}

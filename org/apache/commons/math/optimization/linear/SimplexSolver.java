package org.apache.commons.math.optimization.linear;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.MathUtils;





























public class SimplexSolver
  extends AbstractLinearOptimizer
{
  private static final double DEFAULT_EPSILON = 1.0E-6D;
  protected final double epsilon;
  
  public SimplexSolver()
  {
    this(1.0E-6D);
  }
  



  public SimplexSolver(double epsilon)
  {
    this.epsilon = epsilon;
  }
  




  private Integer getPivotColumn(SimplexTableau tableau)
  {
    double minValue = 0.0D;
    Integer minPos = null;
    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getWidth() - 1; i++) {
      if (MathUtils.compareTo(tableau.getEntry(0, i), minValue, epsilon) < 0) {
        minValue = tableau.getEntry(0, i);
        minPos = Integer.valueOf(i);
      }
    }
    return minPos;
  }
  






  private Integer getPivotRow(SimplexTableau tableau, int col)
  {
    List<Integer> minRatioPositions = new ArrayList();
    double minRatio = Double.MAX_VALUE;
    for (int i = tableau.getNumObjectiveFunctions(); i < tableau.getHeight(); i++) {
      double rhs = tableau.getEntry(i, tableau.getWidth() - 1);
      double entry = tableau.getEntry(i, col);
      if (MathUtils.compareTo(entry, 0.0D, epsilon) > 0) {
        double ratio = rhs / entry;
        if (MathUtils.equals(ratio, minRatio, epsilon)) {
          minRatioPositions.add(Integer.valueOf(i));
        } else if (ratio < minRatio) {
          minRatio = ratio;
          minRatioPositions = new ArrayList();
          minRatioPositions.add(Integer.valueOf(i));
        }
      }
    }
    
    if (minRatioPositions.size() == 0)
      return null;
    if (minRatioPositions.size() > 1)
    {

      for (Integer row : minRatioPositions) {
        for (int i = 0; i < tableau.getNumArtificialVariables(); i++) {
          int column = i + tableau.getArtificialVariableOffset();
          if ((MathUtils.equals(tableau.getEntry(row.intValue(), column), 1.0D, epsilon)) && (row.equals(tableau.getBasicRow(column))))
          {
            return row;
          }
        }
      }
    }
    return (Integer)minRatioPositions.get(0);
  }
  






  protected void doIteration(SimplexTableau tableau)
    throws OptimizationException
  {
    incrementIterationsCounter();
    
    Integer pivotCol = getPivotColumn(tableau);
    Integer pivotRow = getPivotRow(tableau, pivotCol.intValue());
    if (pivotRow == null) {
      throw new UnboundedSolutionException();
    }
    

    double pivotVal = tableau.getEntry(pivotRow.intValue(), pivotCol.intValue());
    tableau.divideRow(pivotRow.intValue(), pivotVal);
    

    for (int i = 0; i < tableau.getHeight(); i++) {
      if (i != pivotRow.intValue()) {
        double multiplier = tableau.getEntry(i, pivotCol.intValue());
        tableau.subtractRow(i, pivotRow.intValue(), multiplier);
      }
    }
  }
  







  protected void solvePhase1(SimplexTableau tableau)
    throws OptimizationException
  {
    if (tableau.getNumArtificialVariables() == 0) {
      return;
    }
    
    while (!tableau.isOptimal()) {
      doIteration(tableau);
    }
    

    if (!MathUtils.equals(tableau.getEntry(0, tableau.getRhsOffset()), 0.0D, epsilon)) {
      throw new NoFeasibleSolutionException();
    }
  }
  
  public RealPointValuePair doOptimize()
    throws OptimizationException
  {
    SimplexTableau tableau = new SimplexTableau(function, linearConstraints, goal, nonNegative, epsilon);
    

    solvePhase1(tableau);
    tableau.dropPhase1Objective();
    
    while (!tableau.isOptimal()) {
      doIteration(tableau);
    }
    return tableau.getSolution();
  }
}

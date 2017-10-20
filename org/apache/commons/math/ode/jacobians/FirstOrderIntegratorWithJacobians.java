package org.apache.commons.math.ode.jacobians;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ExtendedFirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;























































@Deprecated
public class FirstOrderIntegratorWithJacobians
{
  private final FirstOrderIntegrator integrator;
  private final ODEWithJacobians ode;
  private int maxEvaluations;
  private int evaluations;
  
  public FirstOrderIntegratorWithJacobians(FirstOrderIntegrator integrator, ParameterizedODE ode, double[] p, double[] hY, double[] hP)
  {
    checkDimension(ode.getDimension(), hY);
    checkDimension(ode.getParametersDimension(), p);
    checkDimension(ode.getParametersDimension(), hP);
    this.integrator = integrator;
    this.ode = new FiniteDifferencesWrapper(ode, p, hY, hP);
    setMaxEvaluations(-1);
  }
  






  public FirstOrderIntegratorWithJacobians(FirstOrderIntegrator integrator, ODEWithJacobians ode)
  {
    this.integrator = integrator;
    this.ode = ode;
    setMaxEvaluations(-1);
  }
  






  public void addStepHandler(StepHandlerWithJacobians handler)
  {
    int n = ode.getDimension();
    int k = ode.getParametersDimension();
    integrator.addStepHandler(new StepHandlerWrapper(handler, n, k));
  }
  




  public Collection<StepHandlerWithJacobians> getStepHandlers()
  {
    Collection<StepHandlerWithJacobians> handlers = new ArrayList();
    
    for (StepHandler handler : integrator.getStepHandlers()) {
      if ((handler instanceof StepHandlerWrapper)) {
        handlers.add(((StepHandlerWrapper)handler).getHandler());
      }
    }
    return handlers;
  }
  



  public void clearStepHandlers()
  {
    integrator.clearStepHandlers();
  }
  













  public void addEventHandler(EventHandlerWithJacobians handler, double maxCheckInterval, double convergence, int maxIterationCount)
  {
    int n = ode.getDimension();
    int k = ode.getParametersDimension();
    integrator.addEventHandler(new EventHandlerWrapper(handler, n, k), maxCheckInterval, convergence, maxIterationCount);
  }
  





  public Collection<EventHandlerWithJacobians> getEventHandlers()
  {
    Collection<EventHandlerWithJacobians> handlers = new ArrayList();
    
    for (EventHandler handler : integrator.getEventHandlers()) {
      if ((handler instanceof EventHandlerWrapper)) {
        handlers.add(((EventHandlerWrapper)handler).getHandler());
      }
    }
    return handlers;
  }
  



  public void clearEventHandlers()
  {
    integrator.clearEventHandlers();
  }
  





























  public double integrate(double t0, double[] y0, double[][] dY0dP, double t, double[] y, double[][] dYdY0, double[][] dYdP)
    throws DerivativeException, IntegratorException
  {
    int n = ode.getDimension();
    int k = ode.getParametersDimension();
    checkDimension(n, y0);
    checkDimension(n, y);
    checkDimension(n, dYdY0);
    checkDimension(n, dYdY0[0]);
    if (k != 0) {
      checkDimension(n, dY0dP);
      checkDimension(k, dY0dP[0]);
      checkDimension(n, dYdP);
      checkDimension(k, dYdP[0]);
    }
    






    double[] z = new double[n * (1 + n + k)];
    System.arraycopy(y0, 0, z, 0, n);
    for (int i = 0; i < n; i++)
    {

      z[(i * (1 + n) + n)] = 1.0D;
      

      System.arraycopy(dY0dP[i], 0, z, n * (n + 1) + i * k, k);
    }
    


    evaluations = 0;
    double stopTime = integrator.integrate(new MappingWrapper(), t0, z, t, z);
    

    dispatchCompoundState(z, y, dYdY0, dYdP);
    
    return stopTime;
  }
  








  private static void dispatchCompoundState(double[] z, double[] y, double[][] dydy0, double[][] dydp)
  {
    int n = y.length;
    int k = dydp[0].length;
    

    System.arraycopy(z, 0, y, 0, n);
    

    for (int i = 0; i < n; i++) {
      System.arraycopy(z, n * (i + 1), dydy0[i], 0, n);
    }
    

    for (int i = 0; i < n; i++) {
      System.arraycopy(z, n * (n + 1) + i * k, dydp[i], 0, k);
    }
  }
  









  public double getCurrentStepStart()
  {
    return integrator.getCurrentStepStart();
  }
  








  public double getCurrentSignedStepsize()
  {
    return integrator.getCurrentSignedStepsize();
  }
  







  public void setMaxEvaluations(int maxEvaluations)
  {
    this.maxEvaluations = (maxEvaluations < 0 ? Integer.MAX_VALUE : maxEvaluations);
  }
  


  public int getMaxEvaluations()
  {
    return maxEvaluations;
  }
  






  public int getEvaluations()
  {
    return evaluations;
  }
  




  private void checkDimension(int expected, Object array)
    throws IllegalArgumentException
  {
    int arrayDimension = array == null ? 0 : Array.getLength(array);
    if (arrayDimension != expected) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(arrayDimension), Integer.valueOf(expected) });
    }
  }
  


  private class MappingWrapper
    implements ExtendedFirstOrderDifferentialEquations
  {
    private final double[] y;
    

    private final double[] yDot;
    

    private final double[][] dFdY;
    

    private final double[][] dFdP;
    


    public MappingWrapper()
    {
      int n = ode.getDimension();
      int k = ode.getParametersDimension();
      y = new double[n];
      yDot = new double[n];
      dFdY = new double[n][n];
      dFdP = new double[n][k];
    }
    

    public int getDimension()
    {
      int n = y.length;
      int k = dFdP[0].length;
      return n * (1 + n + k);
    }
    
    public int getMainSetDimension()
    {
      return ode.getDimension();
    }
    

    public void computeDerivatives(double t, double[] z, double[] zDot)
      throws DerivativeException
    {
      int n = y.length;
      int k = dFdP[0].length;
      

      System.arraycopy(z, 0, y, 0, n);
      if (FirstOrderIntegratorWithJacobians.access$104(FirstOrderIntegratorWithJacobians.this) > maxEvaluations) {
        throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
      }
      ode.computeDerivatives(t, y, yDot);
      ode.computeJacobians(t, y, yDot, dFdY, dFdP);
      

      System.arraycopy(yDot, 0, zDot, 0, n);
      

      for (int i = 0; i < n; i++) {
        double[] dFdYi = dFdY[i];
        for (int j = 0; j < n; j++) {
          double s = 0.0D;
          int startIndex = n + j;
          int zIndex = startIndex;
          for (int l = 0; l < n; l++) {
            s += dFdYi[l] * z[zIndex];
            zIndex += n;
          }
          zDot[(startIndex + i * n)] = s;
        }
      }
      

      for (int i = 0; i < n; i++) {
        double[] dFdYi = dFdY[i];
        double[] dFdPi = dFdP[i];
        for (int j = 0; j < k; j++) {
          double s = dFdPi[j];
          int startIndex = n * (n + 1) + j;
          int zIndex = startIndex;
          for (int l = 0; l < n; l++) {
            s += dFdYi[l] * z[zIndex];
            zIndex += k;
          }
          zDot[(startIndex + i * k)] = s;
        }
      }
    }
  }
  



  private class FiniteDifferencesWrapper
    implements ODEWithJacobians
  {
    private final ParameterizedODE ode;
    


    private final double[] p;
    


    private final double[] hY;
    


    private final double[] hP;
    


    private final double[] tmpDot;
    


    public FiniteDifferencesWrapper(ParameterizedODE ode, double[] p, double[] hY, double[] hP)
    {
      this.ode = ode;
      this.p = ((double[])p.clone());
      this.hY = ((double[])hY.clone());
      this.hP = ((double[])hP.clone());
      tmpDot = new double[ode.getDimension()];
    }
    
    public int getDimension()
    {
      return ode.getDimension();
    }
    

    public void computeDerivatives(double t, double[] y, double[] yDot)
      throws DerivativeException
    {
      ode.computeDerivatives(t, y, yDot);
    }
    
    public int getParametersDimension()
    {
      return ode.getParametersDimension();
    }
    


    public void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP)
      throws DerivativeException
    {
      int n = hY.length;
      int k = hP.length;
      
      FirstOrderIntegratorWithJacobians.access$112(FirstOrderIntegratorWithJacobians.this, n + k);
      if (evaluations > maxEvaluations) {
        throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
      }
      

      for (int j = 0; j < n; j++) {
        double savedYj = y[j];
        y[j] += hY[j];
        ode.computeDerivatives(t, y, tmpDot);
        for (int i = 0; i < n; i++) {
          dFdY[i][j] = ((tmpDot[i] - yDot[i]) / hY[j]);
        }
        y[j] = savedYj;
      }
      

      for (int j = 0; j < k; j++) {
        ode.setParameter(j, p[j] + hP[j]);
        ode.computeDerivatives(t, y, tmpDot);
        for (int i = 0; i < n; i++) {
          dFdP[i][j] = ((tmpDot[i] - yDot[i]) / hP[j]);
        }
        ode.setParameter(j, p[j]);
      }
    }
  }
  



  private static class StepHandlerWrapper
    implements StepHandler
  {
    private final StepHandlerWithJacobians handler;
    


    private final int n;
    


    private final int k;
    



    public StepHandlerWrapper(StepHandlerWithJacobians handler, int n, int k)
    {
      this.handler = handler;
      this.n = n;
      this.k = k;
    }
    


    public StepHandlerWithJacobians getHandler()
    {
      return handler;
    }
    
    public void handleStep(StepInterpolator interpolator, boolean isLast)
      throws DerivativeException
    {
      handler.handleStep(new FirstOrderIntegratorWithJacobians.StepInterpolatorWrapper(interpolator, n, k), isLast);
    }
    
    public boolean requiresDenseOutput()
    {
      return handler.requiresDenseOutput();
    }
    
    public void reset()
    {
      handler.reset();
    }
  }
  



  private static class StepInterpolatorWrapper
    implements StepInterpolatorWithJacobians
  {
    private StepInterpolator interpolator;
    


    private double[] y;
    


    private double[][] dydy0;
    


    private double[][] dydp;
    


    private double[] yDot;
    


    private double[][] dydy0Dot;
    


    private double[][] dydpDot;
    


    public StepInterpolatorWrapper() {}
    


    public StepInterpolatorWrapper(StepInterpolator interpolator, int n, int k)
    {
      this.interpolator = interpolator;
      y = new double[n];
      dydy0 = new double[n][n];
      dydp = new double[n][k];
      yDot = new double[n];
      dydy0Dot = new double[n][n];
      dydpDot = new double[n][k];
    }
    
    public void setInterpolatedTime(double time)
    {
      interpolator.setInterpolatedTime(time);
    }
    
    public boolean isForward()
    {
      return interpolator.isForward();
    }
    
    public double getPreviousTime()
    {
      return interpolator.getPreviousTime();
    }
    
    public double getInterpolatedTime()
    {
      return interpolator.getInterpolatedTime();
    }
    
    public double[] getInterpolatedY() throws DerivativeException
    {
      double[] extendedState = interpolator.getInterpolatedState();
      System.arraycopy(extendedState, 0, y, 0, y.length);
      return y;
    }
    
    public double[][] getInterpolatedDyDy0() throws DerivativeException
    {
      double[] extendedState = interpolator.getInterpolatedState();
      int n = y.length;
      int start = n;
      for (int i = 0; i < n; i++) {
        System.arraycopy(extendedState, start, dydy0[i], 0, n);
        start += n;
      }
      return dydy0;
    }
    
    public double[][] getInterpolatedDyDp() throws DerivativeException
    {
      double[] extendedState = interpolator.getInterpolatedState();
      int n = y.length;
      int k = dydp[0].length;
      int start = n * (n + 1);
      for (int i = 0; i < n; i++) {
        System.arraycopy(extendedState, start, dydp[i], 0, k);
        start += k;
      }
      return dydp;
    }
    
    public double[] getInterpolatedYDot() throws DerivativeException
    {
      double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
      System.arraycopy(extendedDerivatives, 0, yDot, 0, yDot.length);
      return yDot;
    }
    
    public double[][] getInterpolatedDyDy0Dot() throws DerivativeException
    {
      double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
      int n = y.length;
      int start = n;
      for (int i = 0; i < n; i++) {
        System.arraycopy(extendedDerivatives, start, dydy0Dot[i], 0, n);
        start += n;
      }
      return dydy0Dot;
    }
    
    public double[][] getInterpolatedDyDpDot() throws DerivativeException
    {
      double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
      int n = y.length;
      int k = dydpDot[0].length;
      int start = n * (n + 1);
      for (int i = 0; i < n; i++) {
        System.arraycopy(extendedDerivatives, start, dydpDot[i], 0, k);
        start += k;
      }
      return dydpDot;
    }
    
    public double getCurrentTime()
    {
      return interpolator.getCurrentTime();
    }
    
    public StepInterpolatorWithJacobians copy() throws DerivativeException
    {
      int n = y.length;
      int k = dydp[0].length;
      StepInterpolatorWrapper copied = new StepInterpolatorWrapper(interpolator.copy(), n, k);
      
      copyArray(y, y);
      copyArray(dydy0, dydy0);
      copyArray(dydp, dydp);
      copyArray(yDot, yDot);
      copyArray(dydy0Dot, dydy0Dot);
      copyArray(dydpDot, dydpDot);
      return copied;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException
    {
      out.writeObject(interpolator);
      out.writeInt(y.length);
      out.writeInt(dydp[0].length);
      writeArray(out, y);
      writeArray(out, dydy0);
      writeArray(out, dydp);
      writeArray(out, yDot);
      writeArray(out, dydy0Dot);
      writeArray(out, dydpDot);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
      interpolator = ((StepInterpolator)in.readObject());
      int n = in.readInt();
      int k = in.readInt();
      y = new double[n];
      dydy0 = new double[n][n];
      dydp = new double[n][k];
      yDot = new double[n];
      dydy0Dot = new double[n][n];
      dydpDot = new double[n][k];
      readArray(in, y);
      readArray(in, dydy0);
      readArray(in, dydp);
      readArray(in, yDot);
      readArray(in, dydy0Dot);
      readArray(in, dydpDot);
    }
    



    private static void copyArray(double[] src, double[] dest)
    {
      System.arraycopy(src, 0, dest, 0, src.length);
    }
    



    private static void copyArray(double[][] src, double[][] dest)
    {
      for (int i = 0; i < src.length; i++) {
        copyArray(src[i], dest[i]);
      }
    }
    




    private static void writeArray(ObjectOutput out, double[] array)
      throws IOException
    {
      for (int i = 0; i < array.length; i++) {
        out.writeDouble(array[i]);
      }
    }
    




    private static void writeArray(ObjectOutput out, double[][] array)
      throws IOException
    {
      for (int i = 0; i < array.length; i++) {
        writeArray(out, array[i]);
      }
    }
    




    private static void readArray(ObjectInput in, double[] array)
      throws IOException
    {
      for (int i = 0; i < array.length; i++) {
        array[i] = in.readDouble();
      }
    }
    




    private static void readArray(ObjectInput in, double[][] array)
      throws IOException
    {
      for (int i = 0; i < array.length; i++) {
        readArray(in, array[i]);
      }
    }
  }
  



  private static class EventHandlerWrapper
    implements EventHandler
  {
    private final EventHandlerWithJacobians handler;
    


    private double[] y;
    


    private double[][] dydy0;
    

    private double[][] dydp;
    


    public EventHandlerWrapper(EventHandlerWithJacobians handler, int n, int k)
    {
      this.handler = handler;
      y = new double[n];
      dydy0 = new double[n][n];
      dydp = new double[n][k];
    }
    


    public EventHandlerWithJacobians getHandler()
    {
      return handler;
    }
    
    public int eventOccurred(double t, double[] z, boolean increasing)
      throws EventException
    {
      FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
      return handler.eventOccurred(t, y, dydy0, dydp, increasing);
    }
    
    public double g(double t, double[] z)
      throws EventException
    {
      FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
      return handler.g(t, y, dydy0, dydp);
    }
    
    public void resetState(double t, double[] z)
      throws EventException
    {
      FirstOrderIntegratorWithJacobians.dispatchCompoundState(z, y, dydy0, dydp);
      handler.resetState(t, y, dydy0, dydp);
    }
  }
}

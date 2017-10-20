package org.apache.commons.math.ode.events;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;




































































public class EventState
{
  private final EventHandler handler;
  private final double maxCheckInterval;
  private final double convergence;
  private final int maxIterationCount;
  private double t0;
  private double g0;
  private boolean g0Positive;
  private boolean pendingEvent;
  private double pendingEventTime;
  private double previousEventTime;
  private boolean forward;
  private boolean increasing;
  private int nextAction;
  
  public EventState(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount)
  {
    this.handler = handler;
    this.maxCheckInterval = maxCheckInterval;
    this.convergence = FastMath.abs(convergence);
    this.maxIterationCount = maxIterationCount;
    

    t0 = NaN.0D;
    g0 = NaN.0D;
    g0Positive = true;
    pendingEvent = false;
    pendingEventTime = NaN.0D;
    previousEventTime = NaN.0D;
    increasing = true;
    nextAction = 3;
  }
  



  public EventHandler getEventHandler()
  {
    return handler;
  }
  


  public double getMaxCheckInterval()
  {
    return maxCheckInterval;
  }
  


  public double getConvergence()
  {
    return convergence;
  }
  


  public int getMaxIterationCount()
  {
    return maxIterationCount;
  }
  






















  public void reinitializeBegin(StepInterpolator interpolator)
    throws EventException
  {
    try
    {
      double ignoreZone = interpolator.isForward() ? getConvergence() : -getConvergence();
      t0 = (interpolator.getPreviousTime() + ignoreZone);
      interpolator.setInterpolatedTime(t0);
      g0 = handler.g(t0, interpolator.getInterpolatedState());
      if (g0 == 0.0D)
      {

        double tStart = interpolator.getPreviousTime();
        interpolator.setInterpolatedTime(tStart);
        g0Positive = (handler.g(tStart, interpolator.getInterpolatedState()) <= 0.0D);
      } else {
        g0Positive = (g0 >= 0.0D);
      }
    }
    catch (DerivativeException mue) {
      throw new EventException(mue);
    }
  }
  










  public boolean evaluateStep(final StepInterpolator interpolator)
    throws DerivativeException, EventException, ConvergenceException
  {
    try
    {
      forward = interpolator.isForward();
      double t1 = interpolator.getCurrentTime();
      if (FastMath.abs(t1 - t0) < convergence)
      {
        return false;
      }
      double start = forward ? t0 + convergence : t0 - convergence;
      double dt = t1 - start;
      int n = FastMath.max(1, (int)FastMath.ceil(FastMath.abs(dt) / maxCheckInterval));
      double h = dt / n;
      
      double ta = t0;
      double ga = g0;
      for (int i = 0; i < n; i++)
      {

        double tb = start + (i + 1) * h;
        interpolator.setInterpolatedTime(tb);
        double gb = handler.g(tb, interpolator.getInterpolatedState());
        

        if ((g0Positive ^ gb >= 0.0D))
        {


          increasing = (gb >= ga);
          
          UnivariateRealFunction f = new UnivariateRealFunction() {
            public double value(double t) {
              try {
                interpolator.setInterpolatedTime(t);
                return handler.g(t, interpolator.getInterpolatedState());
              } catch (DerivativeException e) {
                throw new EventState.EmbeddedDerivativeException(e);
              } catch (EventException e) {
                throw new EventState.EmbeddedEventException(e);
              }
            }
          };
          BrentSolver solver = new BrentSolver(convergence);
          
          if (ga * gb >= 0.0D)
          {






            double epsilon = (forward ? 0.25D : -0.25D) * convergence;
            for (int k = 0; (k < 4) && (ga * gb > 0.0D); k++) {
              ta += epsilon;
              try {
                ga = f.value(ta);
              } catch (FunctionEvaluationException ex) {
                throw new DerivativeException(ex);
              }
            }
            if (ga * gb > 0.0D)
            {
              throw new MathInternalError();
            }
          }
          double root;
          try
          {
            root = ta <= tb ? solver.solve(maxIterationCount, f, ta, tb) : solver.solve(maxIterationCount, f, tb, ta);
          }
          catch (FunctionEvaluationException ex)
          {
            throw new DerivativeException(ex);
          }
          
          if ((!Double.isNaN(previousEventTime)) && (FastMath.abs(root - ta) <= convergence) && (FastMath.abs(root - previousEventTime) <= convergence))
          {


            ta = tb;
            ga = gb;
          } else { if ((Double.isNaN(previousEventTime)) || (FastMath.abs(previousEventTime - root) > convergence))
            {
              pendingEventTime = root;
              pendingEvent = true;
              return true;
            }
            
            ta = tb;
            ga = gb;
          }
        }
        else
        {
          ta = tb;
          ga = gb;
        }
      }
      


      pendingEvent = false;
      pendingEventTime = NaN.0D;
      return false;
    }
    catch (EmbeddedDerivativeException ede) {
      throw ede.getDerivativeException();
    } catch (EmbeddedEventException eee) {
      throw eee.getEventException();
    }
  }
  




  public double getEventTime()
  {
    return pendingEvent ? pendingEventTime : Double.POSITIVE_INFINITY;
  }
  








  public void stepAccepted(double t, double[] y)
    throws EventException
  {
    t0 = t;
    g0 = handler.g(t, y);
    
    if ((pendingEvent) && (FastMath.abs(pendingEventTime - t) <= convergence))
    {
      previousEventTime = t;
      g0Positive = increasing;
      nextAction = handler.eventOccurred(t, y, !(increasing ^ forward));
    } else {
      g0Positive = (g0 >= 0.0D);
      nextAction = 3;
    }
  }
  



  public boolean stop()
  {
    return nextAction == 0;
  }
  









  public boolean reset(double t, double[] y)
    throws EventException
  {
    if ((!pendingEvent) || (FastMath.abs(pendingEventTime - t) > convergence)) {
      return false;
    }
    
    if (nextAction == 1) {
      handler.resetState(t, y);
    }
    pendingEvent = false;
    pendingEventTime = NaN.0D;
    
    return (nextAction == 1) || (nextAction == 2);
  }
  



  private static class EmbeddedDerivativeException
    extends RuntimeException
  {
    private static final long serialVersionUID = 3574188382434584610L;
    

    private final DerivativeException derivativeException;
    


    public EmbeddedDerivativeException(DerivativeException derivativeException)
    {
      this.derivativeException = derivativeException;
    }
    


    public DerivativeException getDerivativeException()
    {
      return derivativeException;
    }
  }
  


  private static class EmbeddedEventException
    extends RuntimeException
  {
    private static final long serialVersionUID = -1337749250090455474L;
    

    private final EventException eventException;
    


    public EmbeddedEventException(EventException eventException)
    {
      this.eventException = eventException;
    }
    


    public EventException getEventException()
    {
      return eventException;
    }
  }
}

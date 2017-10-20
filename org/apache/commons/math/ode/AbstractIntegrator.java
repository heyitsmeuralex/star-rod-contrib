package org.apache.commons.math.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.EventState;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;














































public abstract class AbstractIntegrator
  implements FirstOrderIntegrator
{
  protected Collection<StepHandler> stepHandlers;
  protected double stepStart;
  protected double stepSize;
  protected boolean isLastStep;
  protected boolean resetOccurred;
  private Collection<EventState> eventsStates;
  private boolean statesInitialized;
  private final String name;
  private int maxEvaluations;
  private int evaluations;
  private transient FirstOrderDifferentialEquations equations;
  
  public AbstractIntegrator(String name)
  {
    this.name = name;
    stepHandlers = new ArrayList();
    stepStart = NaN.0D;
    stepSize = NaN.0D;
    eventsStates = new ArrayList();
    statesInitialized = false;
    setMaxEvaluations(-1);
    resetEvaluations();
  }
  

  protected AbstractIntegrator()
  {
    this(null);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void addStepHandler(StepHandler handler)
  {
    stepHandlers.add(handler);
  }
  
  public Collection<StepHandler> getStepHandlers()
  {
    return Collections.unmodifiableCollection(stepHandlers);
  }
  
  public void clearStepHandlers()
  {
    stepHandlers.clear();
  }
  



  public void addEventHandler(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount)
  {
    eventsStates.add(new EventState(handler, maxCheckInterval, convergence, maxIterationCount));
  }
  
  public Collection<EventHandler> getEventHandlers()
  {
    List<EventHandler> list = new ArrayList();
    for (EventState state : eventsStates) {
      list.add(state.getEventHandler());
    }
    return Collections.unmodifiableCollection(list);
  }
  
  public void clearEventHandlers()
  {
    eventsStates.clear();
  }
  



  protected boolean requiresDenseOutput()
  {
    if (!eventsStates.isEmpty()) {
      return true;
    }
    for (StepHandler handler : stepHandlers) {
      if (handler.requiresDenseOutput()) {
        return true;
      }
    }
    return false;
  }
  
  public double getCurrentStepStart()
  {
    return stepStart;
  }
  
  public double getCurrentSignedStepsize()
  {
    return stepSize;
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
  

  protected void resetEvaluations()
  {
    evaluations = 0;
  }
  



  protected void setEquations(FirstOrderDifferentialEquations equations)
  {
    this.equations = equations;
  }
  






  public void computeDerivatives(double t, double[] y, double[] yDot)
    throws DerivativeException
  {
    if (++evaluations > maxEvaluations) {
      throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
    }
    equations.computeDerivatives(t, y, yDot);
  }
  






  protected void setStateInitialized(boolean stateInitialized)
  {
    statesInitialized = stateInitialized;
  }
  












  protected double acceptStep(AbstractStepInterpolator interpolator, double[] y, double[] yDot, double tEnd)
    throws DerivativeException, IntegratorException
  {
    try
    {
      double previousT = interpolator.getGlobalPreviousTime();
      double currentT = interpolator.getGlobalCurrentTime();
      resetOccurred = false;
      

      if (!statesInitialized) {
        for (EventState state : eventsStates) {
          state.reinitializeBegin(interpolator);
        }
        statesInitialized = true;
      }
      

      final int orderingSign = interpolator.isForward() ? 1 : -1;
      SortedSet<EventState> occuringEvents = new TreeSet(new Comparator()
      {
        public int compare(EventState es0, EventState es1)
        {
          return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
        }
      });
      

      for (EventState state : eventsStates) {
        if (state.evaluateStep(interpolator))
        {
          occuringEvents.add(state);
        }
      }
      
      while (!occuringEvents.isEmpty())
      {

        Iterator<EventState> iterator = occuringEvents.iterator();
        EventState currentEvent = (EventState)iterator.next();
        iterator.remove();
        

        double eventT = currentEvent.getEventTime();
        interpolator.setSoftPreviousTime(previousT);
        interpolator.setSoftCurrentTime(eventT);
        

        interpolator.setInterpolatedTime(eventT);
        double[] eventY = interpolator.getInterpolatedState();
        currentEvent.stepAccepted(eventT, eventY);
        isLastStep = currentEvent.stop();
        

        for (StepHandler handler : stepHandlers) {
          handler.handleStep(interpolator, isLastStep);
        }
        
        if (isLastStep)
        {
          System.arraycopy(eventY, 0, y, 0, y.length);
          return eventT;
        }
        
        if (currentEvent.reset(eventT, eventY))
        {

          System.arraycopy(eventY, 0, y, 0, y.length);
          computeDerivatives(eventT, y, yDot);
          resetOccurred = true;
          return eventT;
        }
        

        previousT = eventT;
        interpolator.setSoftPreviousTime(eventT);
        interpolator.setSoftCurrentTime(currentT);
        

        if (currentEvent.evaluateStep(interpolator))
        {
          occuringEvents.add(currentEvent);
        }
      }
      

      interpolator.setInterpolatedTime(currentT);
      double[] currentY = interpolator.getInterpolatedState();
      for (EventState state : eventsStates) {
        state.stepAccepted(currentT, currentY);
        isLastStep = ((isLastStep) || (state.stop()));
      }
      isLastStep = ((isLastStep) || (MathUtils.equals(currentT, tEnd, 1)));
      

      for (StepHandler handler : stepHandlers) {
        handler.handleStep(interpolator, isLastStep);
      }
      
      return currentT;
    } catch (EventException se) {
      Throwable cause = se.getCause();
      if ((cause != null) && ((cause instanceof DerivativeException))) {
        throw ((DerivativeException)cause);
      }
      throw new IntegratorException(se);
    } catch (ConvergenceException ce) {
      throw new IntegratorException(ce);
    }
  }
  











  protected void sanityChecks(FirstOrderDifferentialEquations ode, double t0, double[] y0, double t, double[] y)
    throws IntegratorException
  {
    if (ode.getDimension() != y0.length) {
      throw new IntegratorException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(ode.getDimension()), Integer.valueOf(y0.length) });
    }
    

    if (ode.getDimension() != y.length) {
      throw new IntegratorException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(ode.getDimension()), Integer.valueOf(y.length) });
    }
    

    if (FastMath.abs(t - t0) <= 1.0E-12D * FastMath.max(FastMath.abs(t0), FastMath.abs(t))) {
      throw new IntegratorException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, new Object[] { Double.valueOf(FastMath.abs(t - t0)) });
    }
  }
  















  @Deprecated
  protected CombinedEventsManager addEndTimeChecker(double startTime, double endTime, CombinedEventsManager manager)
  {
    CombinedEventsManager newManager = new CombinedEventsManager();
    for (EventState state : manager.getEventsStates()) {
      newManager.addEventHandler(state.getEventHandler(), state.getMaxCheckInterval(), state.getConvergence(), state.getMaxIterationCount());
    }
    


    newManager.addEventHandler(new EndTimeChecker(endTime), Double.POSITIVE_INFINITY, FastMath.ulp(FastMath.max(FastMath.abs(startTime), FastMath.abs(endTime))), 100);
    


    return newManager;
  }
  



  @Deprecated
  private static class EndTimeChecker
    implements EventHandler
  {
    private final double endTime;
    


    public EndTimeChecker(double endTime)
    {
      this.endTime = endTime;
    }
    
    public int eventOccurred(double t, double[] y, boolean increasing)
    {
      return 0;
    }
    
    public double g(double t, double[] y)
    {
      return t - endTime;
    }
    
    public void resetState(double t, double[] y) {}
  }
}

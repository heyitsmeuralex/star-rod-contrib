package org.apache.commons.math.ode;

import java.util.Collection;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.sampling.StepHandler;

public abstract interface ODEIntegrator
{
  public abstract String getName();
  
  public abstract void addStepHandler(StepHandler paramStepHandler);
  
  public abstract Collection<StepHandler> getStepHandlers();
  
  public abstract void clearStepHandlers();
  
  public abstract void addEventHandler(EventHandler paramEventHandler, double paramDouble1, double paramDouble2, int paramInt);
  
  public abstract Collection<EventHandler> getEventHandlers();
  
  public abstract void clearEventHandlers();
  
  public abstract double getCurrentStepStart();
  
  public abstract double getCurrentSignedStepsize();
  
  public abstract void setMaxEvaluations(int paramInt);
  
  public abstract int getMaxEvaluations();
  
  public abstract int getEvaluations();
}

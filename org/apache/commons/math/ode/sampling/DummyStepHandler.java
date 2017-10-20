package org.apache.commons.math.ode.sampling;





















public class DummyStepHandler
  implements StepHandler
{
  private DummyStepHandler() {}
  




















  public static DummyStepHandler getInstance()
  {
    return LazyHolder.INSTANCE;
  }
  



  public boolean requiresDenseOutput()
  {
    return false;
  }
  








  public void reset() {}
  







  public void handleStep(StepInterpolator interpolator, boolean isLast) {}
  







  private static class LazyHolder
  {
    private static final DummyStepHandler INSTANCE = new DummyStepHandler(null);
    

    private LazyHolder() {}
  }
  

  private Object readResolve()
  {
    return LazyHolder.INSTANCE;
  }
}

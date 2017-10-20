package org.apache.commons.math.geometry;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.LocalizedFormats;






























public class CardanEulerSingularityException
  extends MathException
{
  private static final long serialVersionUID = -1360952845582206770L;
  
  public CardanEulerSingularityException(boolean isCardan)
  {
    super(isCardan ? LocalizedFormats.CARDAN_ANGLES_SINGULARITY : LocalizedFormats.EULER_ANGLES_SINGULARITY, new Object[0]);
  }
}

package org.apache.commons.math.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;


























public class SingularMatrixException
  extends InvalidMatrixException
{
  private static final long serialVersionUID = -7379143356784298432L;
  
  public SingularMatrixException()
  {
    super(LocalizedFormats.SINGULAR_MATRIX, new Object[0]);
  }
}

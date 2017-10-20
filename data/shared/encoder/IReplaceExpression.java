package data.shared.encoder;

import java.util.List;

public abstract interface IReplaceExpression<E extends DataEncoder>
{
  public abstract void replaceExpression(E paramE, String[] paramArrayOfString, List<String> paramList)
    throws InvalidExpressionException;
}

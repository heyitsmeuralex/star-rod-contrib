package data.shared.encoder;

public abstract interface IParseOffset<E extends DataEncoder>
{
  public abstract Patch parseStructOffset(E paramE, String paramString);
}

package data.shared.encoder;

public abstract interface IReplaceSpecial<E extends DataEncoder>
{
  public abstract void replaceStructConstants(E paramE, Patch paramPatch);
}

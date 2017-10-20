package data.shared.encoder;

public abstract interface IPatchLength<E extends DataEncoder>
{
  public abstract int getPatchLength(E paramE, Patch paramPatch);
}

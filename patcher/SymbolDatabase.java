package patcher;

public abstract interface SymbolDatabase
{
  public abstract boolean isFunctionDeclared(String paramString);
  
  public abstract int getFunctionAddress(String paramString);
  
  public abstract boolean stringNameExists(String paramString);
  
  public abstract int getStringFromName(String paramString);
}

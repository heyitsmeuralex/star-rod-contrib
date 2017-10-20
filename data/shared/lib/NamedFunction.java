package data.shared.lib;

import java.util.LinkedList;
import java.util.List;


public class NamedFunction
{
  private final String name;
  private final int addr;
  protected boolean hasWildcard = false;
  private List<ArgumentList> sigList;
  
  protected NamedFunction(int addr, String name)
  {
    this.name = name;
    this.addr = addr;
    sigList = new LinkedList();
  }
  
  public ArgumentList getArguments(int nargs)
  {
    for (ArgumentList sig : sigList)
    {
      if (sig.size() == nargs)
        return sig;
    }
    return null;
  }
  
  public void add(ArgumentList args)
  {
    sigList.add(args);
  }
  
  public boolean conflicts(int nargs)
  {
    if (hasWildcard) {
      return false;
    }
    for (ArgumentList argList : sigList)
    {
      if (argList.size() == nargs) {
        return false;
      }
    }
    return true;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getAddress()
  {
    return addr;
  }
}

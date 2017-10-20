package data.shared.lib;

import java.util.ArrayList;

public class ArgumentList extends ArrayList<FunctionArgument>
{
  private static final long serialVersionUID = 1L;
  private boolean hasPointerArg = false;
  

  protected ArgumentList(FunctionLibrary lib)
  {
    super(0);
  }
  
  protected ArgumentList(FunctionLibrary lib, String... args)
  {
    super(args.length);
    
    for (int i = 0; i < args.length; i++)
    {
      FunctionArgument arg = new FunctionArgument(lib, args[i]);
      add(arg);
      if (ptr != null) {
        hasPointerArg = true;
      }
    }
  }
  
  public boolean hasPointerArg() {
    return hasPointerArg;
  }
}

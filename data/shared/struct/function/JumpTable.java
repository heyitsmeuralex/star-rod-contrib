package data.shared.struct.function;

public class JumpTable implements Comparable<JumpTable>
{
  public final int baseAddress;
  public final int numEntries;
  
  public JumpTable(int tableAddress, int numEntries)
  {
    baseAddress = tableAddress;
    this.numEntries = numEntries;
  }
  

  public int compareTo(JumpTable other)
  {
    return baseAddress - baseAddress;
  }
  

  public int hashCode()
  {
    return baseAddress;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JumpTable other = (JumpTable)obj;
    if (baseAddress != baseAddress)
      return false;
    return true;
  }
}

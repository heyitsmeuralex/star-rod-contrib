package data.strings;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;




public class StringSection
{
  private final int section;
  private final TreeMap<Integer, StringData> strings;
  private final Queue<StringData> mobileStrings;
  private int greatestID;
  private int totalLength;
  private StringData[] stringTable;
  private int[] offsetTable;
  private boolean writeReady = false;
  
  public StringSection(int section)
  {
    this.section = section;
    greatestID = -1;
    totalLength = 0;
    
    strings = new TreeMap();
    mobileStrings = new LinkedList();
  }
  
  public void addString(StringData string)
  {
    assert (section == section);
    assert (!writeReady);
    
    if (index == -1) {
      mobileStrings.add(string);
    } else {
      strings.put(Integer.valueOf(index), string);
    }
    totalLength += bytes.length;
    if (index > greatestID) {
      greatestID = index;
    }
  }
  
  public int getNumStrings() {
    return greatestID + 1;
  }
  
  public void prepareForWriting() throws IOException
  {
    int emptyPositions = greatestID + 1 - strings.size();
    if (mobileStrings.size() > emptyPositions)
    {
      greatestID += mobileStrings.size() - emptyPositions;
    }
    
    stringTable = new StringData[getNumStrings()];
    offsetTable = new int[getNumStrings() + 1];
    
    for (int i = 0; i <= greatestID; i++)
    {
      if (strings.containsKey(Integer.valueOf(i)))
      {
        stringTable[i] = ((StringData)strings.get(Integer.valueOf(i)));
      }
      else if (!mobileStrings.isEmpty())
      {
        StringData string = (StringData)mobileStrings.poll();
        index = i;
        stringTable[i] = string;
      }
    }
    
    writeReady = true;
  }
  
  public void writeStrings(RandomAccessFile raf) throws IOException
  {
    assert (writeReady);
    

    for (int i = 0; i < stringTable.length; i++)
    {
      offsetTable[i] = ((int)raf.getFilePointer() - 28848128);
      if (stringTable[i] != null) {
        raf.write(stringTable[i].bytes);
      } else {
        raf.writeInt(624046845);
      }
    }
    offsetTable[stringTable.length] = ((int)raf.getFilePointer() - 28848128);
  }
  
  public int getStringSize()
  {
    assert (writeReady);
    
    return totalLength + 4 * (getNumStrings() - strings.size());
  }
  
  public void writeStringOffsetTable(RandomAccessFile raf) throws IOException
  {
    assert (writeReady);
    
    for (int i = 0; i < offsetTable.length; i++)
    {
      raf.writeInt(offsetTable[i]);
    }
  }
  
  public int getOffsetTableSize()
  {
    assert (writeReady);
    
    return offsetTable.length * 4;
  }
}

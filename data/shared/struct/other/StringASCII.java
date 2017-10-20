package data.shared.struct.other;

import data.shared.BaseStruct;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.Patch;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import util.IOUtils;

public class StringASCII
  extends BaseStruct<DataDecoder, DataEncoder>
{
  public static final StringASCII instance = new StringASCII();
  

  private StringASCII() {}
  

  public void scan(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int start = fileBuffer.position();
    while (fileBuffer.get() != 0) {}
    
    fileBuffer.position(fileBuffer.position() + 3 & 0xFFFFFFFC);
    int end = fileBuffer.position();
    
    fileBuffer.position(start);
    comment = IOUtils.readString(fileBuffer);
    fileBuffer.position(end);
  }
  

  public void print(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    pw.println(IOUtils.readString(fileBuffer, length));
  }
  

  public void replaceStructConstants(DataEncoder encoder, Patch patch)
  {
    List<String> charList = new ArrayList(64);
    
    for (String[] line : lines) {
      for (String s : line)
        for (char c : s.toCharArray())
          charList.add(String.format("%02Xb", new Object[] { Integer.valueOf(c) }));
    }
    charList.add("00b");
    
    if (charList.size() % 4 != 0) {
      for (int i = 0; i < 4 - charList.size() % 4; i++)
        charList.add("00b");
    }
    String[] newLines = new String[charList.size()];
    charList.toArray(newLines);
    
    lines.clear();
    lines.add(newLines);
  }
  
  public static void replaceString(DataEncoder encoder, Patch patch)
  {
    List<String> charList = new ArrayList(64);
    
    for (String[] line : lines) {
      for (String s : line)
        for (char c : s.toCharArray())
          charList.add(String.format("%02Xb", new Object[] { Integer.valueOf(c) }));
    }
    charList.add("00b");
    
    if (charList.size() % 4 != 0) {
      for (int i = 0; i < 4 - charList.size() % 4; i++)
        charList.add("00b");
    }
    String[] newLines = new String[charList.size()];
    charList.toArray(newLines);
    
    lines.clear();
    lines.add(newLines);
  }
}

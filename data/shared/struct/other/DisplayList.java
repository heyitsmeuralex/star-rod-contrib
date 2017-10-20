package data.shared.struct.other;

import data.shared.BaseStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.encoder.DataEncoder;
import java.io.PrintWriter;
import java.nio.ByteBuffer;


public class DisplayList
  extends BaseStruct<DataDecoder, DataEncoder>
{
  public static final DisplayList instance = new DisplayList();
  

  private DisplayList() {}
  

  public void scan(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int cmd = fileBuffer.getInt();
    
    while (cmd != -553648128)
    {
      int arg = fileBuffer.getInt();
      
      switch (cmd >>> 24)
      {
      case 1: 
        decoder.tryEnqueueAsChild(ptr, arg, SharedStructTypes.VertexListT);
        break;
      case 253: 
        break;
      }
      
      

      cmd = fileBuffer.getInt();
    }
    fileBuffer.getInt();
  }
  

  public void print(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw);
  }
}

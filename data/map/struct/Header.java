package data.map.struct;

import data.map.MapStructTypes;
import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;




public class Header
  extends BaseStruct.MapStruct
{
  public static final Header instance = new Header();
  

  private Header() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    
    int ptrLoadScript = fileBuffer.getInt();
    int ptrEntryList = fileBuffer.getInt();
    int entryListLength = fileBuffer.getInt();
    fileBuffer.getInt();
    
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    
    fileBuffer.getInt();
    fileBuffer.getInt();
    int ptrBackground = fileBuffer.getInt();
    int tattleID = fileBuffer.getInt();
    


    decoder.enqueueAsChild(ptr, ptrLoadScript, MapStructTypes.MainScriptT);
    enqueueAsChildEntryListTlistLength = entryListLength;
    decoder.tryEnqueueAsChild(ptr, tattleID, MapStructTypes.GetTattleFunctionT);
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 4);
  }
}

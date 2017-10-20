package data.strings;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import main.DevContext;
import main.Directories;
import util.IOUtils;
import util.Logger;
import util.Priority;







public class StringDumper
{
  public StringDumper() {}
  
  public static void dumpAllStrings()
    throws IOException
  {
    List<List<ByteBuffer>> allStringBuffers = new ArrayList(47);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    


    int[] sectionOffsets = new int[47];
    raf.seek(28848128L);
    for (int i = 0; i < 47; i++) {
      sectionOffsets[i] = raf.readInt();
    }
    for (int i = 0; i < 47; i++)
    {
      raf.seek(28848128 + sectionOffsets[i]);
      
      List<Integer> stringOffsets = new ArrayList();
      
      int stringOffset;
      while ((stringOffset = raf.readInt()) != sectionOffsets[i])
        stringOffsets.add(Integer.valueOf(stringOffset));
      stringOffsets.add(Integer.valueOf(stringOffset));
      
      List<ByteBuffer> currentBuffers = new ArrayList();
      allStringBuffers.add(currentBuffers);
      
      for (int j = 0; j < stringOffsets.size() - 1; j++)
      {
        int start = 28848128 + ((Integer)stringOffsets.get(j)).intValue();
        int next = 28848128 + ((Integer)stringOffsets.get(j + 1)).intValue();
        ByteBuffer buf = ByteBuffer.allocate(next - start);
        
        raf.seek(start);
        byte b;
        do
        {
          b = raf.readByte();
          buf.put(b);
        }
        while (b != -3);
        
        buf.flip();
        currentBuffers.add(buf);
      }
    }
    
    raf.close();
    
    HashMap<String, StringData> stringsMap = new HashMap();
    

    for (int i = 0; i < allStringBuffers.size(); i++)
    {
      String name = StringConstants.STRING_SECTION_NAMES[i];
      
      PrintWriter out = IOUtils.getBufferedPrintWriter(Directories.DUMP_STRINGS + name + ".txt");
      List<ByteBuffer> currentBuffers = (List)allStringBuffers.get(i);
      
      for (int j = 0; j < currentBuffers.size(); j++)
      {
        ByteBuffer buf = (ByteBuffer)currentBuffers.get(j);
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        String msg = StringDecoder.toMarkup(bytes);
        
        out.printf("%02X-%03X\r\n", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
        out.println(msg);
        
        String id = String.format("%04X%04X", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
        stringsMap.put(id, new StringData(bytes, i, j));
      }
      
      Logger.log(String.format("Dumped strings from %07X: %s", new Object[] { Integer.valueOf(28848128 + sectionOffsets[i]), name }), Priority.MILESTONE);
      out.close();
    }
    
    File custom = new File(Directories.DUMP_STRINGS + StringConstants.STRING_SECTION_NAMES[47] + ".txt");
    custom.createNewFile();
    
    File f = new File(Directories.DUMP_STRINGS + "stringsMap.ser");
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(f));
    ObjectOutputStream out = new ObjectOutputStream(buffer);
    out.writeObject(stringsMap);
    out.close();
    out.close();
    Logger.log("Wrote serialized string map.", Priority.MILESTONE);
  }
  




  public static void dumpAllStrings2()
    throws IOException
  {
    List<List<ByteBuffer>> allStringBuffers = new ArrayList(47);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    


    for (int i = 0;; i++)
    {

      raf.seek(28848128 + i * 4);
      int offset = raf.readInt();
      if (offset == 0) {
        break;
      }
      raf.seek(28848128 + offset);
      
      List<Integer> offsets = new ArrayList();
      
      int nextOffset;
      while ((nextOffset = raf.readInt()) != offset)
        offsets.add(Integer.valueOf(nextOffset));
      offsets.add(Integer.valueOf(offset));
      
      List<ByteBuffer> currentBuffers = new ArrayList();
      allStringBuffers.add(currentBuffers);
      
      for (int j = 0; j < offsets.size() - 1; j++)
      {
        int start = ((Integer)offsets.get(j)).intValue() + 28848128;
        int next = ((Integer)offsets.get(j + 1)).intValue() + 28848128;
        ByteBuffer buf = ByteBuffer.allocate(next - start);
        
        raf.seek(start);
        byte b;
        do
        {
          b = raf.readByte();
          buf.put(b);
        }
        while (b != -3);
        
        buf.flip();
        currentBuffers.add(buf);
      }
    }
    
    raf.close();
    
    HashMap<String, StringData> stringsMap = new HashMap();
    

    for (int i = 0; i < allStringBuffers.size(); i++)
    {
      String name = StringConstants.STRING_SECTION_NAMES[i];
      
      PrintWriter out = IOUtils.getBufferedPrintWriter(Directories.DUMP_STRINGS + name + ".txt");
      List<ByteBuffer> currentBuffers = (List)allStringBuffers.get(i);
      
      for (int j = 0; j < currentBuffers.size(); j++)
      {
        ByteBuffer buf = (ByteBuffer)currentBuffers.get(j);
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        String msg = StringDecoder.toMarkup(bytes);
        
        out.printf("%02X-%03X\r\n", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
        out.println(msg);
        
        String id = String.format("%04X%04X", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
        stringsMap.put(id, new StringData(bytes, i, j));
      }
      
      Logger.log("Wrote file \"" + name + ".txt\"");
      out.close();
    }
    
    File f = new File(Directories.DUMP_STRINGS + "stringsMap.ser");
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(f));
    ObjectOutputStream out = new ObjectOutputStream(buffer);
    out.writeObject(stringsMap);
    out.close();
    out.close();
    Logger.log("Wrote file \"stringsMap.ser\"");
  }
}

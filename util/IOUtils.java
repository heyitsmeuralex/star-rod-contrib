package util;

import data.shared.DataConstants;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.Directories;
import main.InputFileException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class IOUtils
{
  public IOUtils() {}
  
  public static Collection<File> getFilesWithExtension(Directories dir, String ext, boolean recursive) throws IOException
  {
    if (!dir.toFile().exists()) {
      throw new IOException("Directory " + dir + " does not exist!");
    }
    return FileUtils.listFiles(dir.toFile(), new String[] { ext }, recursive);
  }
  
  public static File getFileWithin(Directories dir, String name, boolean recursive)
  {
    IOFileFilter fileFilter = FileFilterUtils.nameFileFilter(name);
    IOFileFilter directoryFilter = recursive ? TrueFileFilter.INSTANCE : null;
    
    Collection<File> matches = FileUtils.listFiles(dir.toFile(), fileFilter, directoryFilter);
    
    if (matches.isEmpty()) {
      return null;
    }
    if (matches.size() > 1) {
      throw new RuntimeException(String.format("Multiple copies of %s found within %s.", new Object[] { name, dir }));
    }
    return (File)matches.toArray()[0];
  }
  
  public static ByteBuffer getDirectBuffer(File source) throws IOException
  {
    byte[] sourceBytes = FileUtils.readFileToByteArray(source);
    ByteBuffer bb = ByteBuffer.allocateDirect((int)source.length());
    bb.put(sourceBytes);
    bb.flip();
    return bb;
  }
  
  public static ByteBuffer getDirectBuffer(byte[] array)
  {
    ByteBuffer bb = ByteBuffer.allocateDirect(array.length);
    bb.put(array);
    bb.flip();
    return bb;
  }
  




  public static String readString(RandomAccessFile raf, int maxlength)
    throws IOException
  {
    StringBuilder sb = new StringBuilder();
    for (int read = 0; 
        
        read < maxlength; read++)
    {
      byte b = raf.readByte();
      
      if (b == 0) {
        break;
      }
      sb.append((char)b);
    }
    
    raf.skipBytes(maxlength - read - 1);
    
    return sb.toString();
  }
  


  public static String readString(RandomAccessFile raf)
    throws IOException
  {
    StringBuilder sb = new StringBuilder();
    
    for (;;)
    {
      byte b = raf.readByte();
      
      if (b == 0) {
        break;
      }
      sb.append((char)b);
    }
    
    return sb.toString();
  }
  





  public static String readString(ByteBuffer bb, int maxlength)
  {
    StringBuilder sb = new StringBuilder();
    for (int read = 0; 
        
        read < maxlength; read++)
    {
      byte b = bb.get();
      
      if (b == 0) {
        break;
      }
      sb.append((char)b);
    }
    
    for (int i = 0; i < maxlength - read - 1; i++) {
      if (bb.hasRemaining())
        bb.get();
    }
    return sb.toString();
  }
  



  public static String readString(ByteBuffer bb)
  {
    StringBuilder sb = new StringBuilder();
    
    for (;;)
    {
      byte b = bb.get();
      
      if (b == 0) {
        break;
      }
      sb.append((char)b);
    }
    
    return sb.toString();
  }
  





  public static List<String> readTextFile(File f)
    throws IOException
  {
    return readTextFile(f, true);
  }
  
  public static List<String> readTextFile(File f, boolean keepEmptyLines) throws IOException
  {
    List<String> lines = new ArrayList();
    BufferedReader in = new BufferedReader(new FileReader(f));
    

    boolean readingCommentBlock = false;
    String line;
    while ((line = in.readLine()) != null)
    {
      line = line.trim();
      
      if (readingCommentBlock)
      {
        int endBlockPos = line.indexOf("%/");
        
        if (endBlockPos < 0)
        {
          if (keepEmptyLines) {
            lines.add("");
          }
        }
        else {
          line = line.substring(endBlockPos + 2).trim();
          readingCommentBlock = false;
        }
      } else {
        line = DataConstants.CommentPattern.matcher(line).replaceAll("").trim();
        
        if (!readingCommentBlock)
        {
          int startBlockPos = line.indexOf("/%");
          if (startBlockPos >= 0)
          {
            line = line.substring(0, startBlockPos).trim();
            readingCommentBlock = true;
          }
        }
        
        if ((!line.isEmpty()) || (keepEmptyLines))
        {
          lines.add(line);
        }
      }
    }
    in.close();
    return lines;
  }
  




  public static String[] getKeyValuePair(File f, String line, int lineNumber)
  {
    if (!line.contains("=")) {
      throw new InputFileException(f, lineNumber, "Missing assignment:\r\n" + line);
    }
    String[] tokens = line.split("\\s*=\\s*");
    
    if (tokens.length != 2) {
      throw new InputFileException(f, lineNumber, "Multiple assignments:\r\n" + line);
    }
    return tokens;
  }
  
  public static PrintWriter getBufferedPrintWriter(String filename) throws FileNotFoundException
  {
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(filename));
    return new PrintWriter(buffer);
  }
  
  public static PrintWriter getBufferedPrintWriter(File f) throws FileNotFoundException
  {
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(f));
    return new PrintWriter(buffer);
  }
}

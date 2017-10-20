package data.yay0;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import main.DevContext;
import org.apache.commons.io.FileUtils;
import util.SimpleProgressBarDialog;

public class Yay0AlgorithmTester
{
  private static final String ENCODED = "./yay0/encoded/";
  private static final String DECODED = "./yay0/decoded/";
  private static final String REFERENCE = "./yay0/reference/";
  
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    new Yay0AlgorithmTester();
    DevContext.exit();
  }
  








  private Yay0AlgorithmTester()
    throws IOException
  {
    verifyCompressedFiles();
  }
  












  private void compressFiles()
    throws IOException
  {
    FileUtils.deleteDirectory(new File("./yay0/encoded/"));
    File[] decompressedFiles = new File("./yay0/decoded/").listFiles();
    
    SimpleProgressBarDialog progressBar = new SimpleProgressBarDialog("Yay0 Compressor", "Compressing files...");
    float count = 0.01F;
    
    for (File f : decompressedFiles)
    {
      System.out.println("Compressing " + f.getName());
      byte[] source = FileUtils.readFileToByteArray(f);
      byte[] encoded = Yay0Helper.encode(source);
      FileUtils.writeByteArrayToFile(new File("./yay0/encoded/" + f.getName()), encoded);
      
      progressBar.setProgress((int)(100.0F * (count / decompressedFiles.length)));
      count += 1.0F;
    }
    
    progressBar.destroy();
  }
  
  private void compressTest(String name) throws IOException
  {
    byte[] source = FileUtils.readFileToByteArray(new File("./yay0/decoded/" + name));
    byte[] encoded = Yay0Helper.encode(source);
    FileUtils.writeByteArrayToFile(new File("./yay0/encoded/" + name), encoded);
  }
  
  private void printBytes(byte[] buf, boolean newLines)
  {
    for (int i = 0; i < buf.length;)
    {
      System.out.print(String.format("%02X", new Object[] { Byte.valueOf(buf[i]) }));
      i++;
      
      if (i % 4 == 0) {
        System.out.print(" ");
      }
      if ((i % 16 == 0) && (newLines)) {
        System.out.println("");
      }
    }
    if ((buf.length % 16 != 0) && (newLines)) {
      System.out.println("");
    }
    System.out.println("");
  }
  
  private void verifyTest(String name) throws IOException
  {
    String title = "Verify " + name + ": ";
    
    util.Logger.log("From REFERENCE:", util.Priority.DETAIL);
    byte[] reference = FileUtils.readFileToByteArray(new File("./yay0/reference/" + name));
    byte[] decodedFromReference = Yay0Helper.decode(reference);
    
    util.Logger.log("From ENCODED:", util.Priority.DETAIL);
    byte[] encoded = FileUtils.readFileToByteArray(new File("./yay0/encoded/" + name));
    byte[] decodedFromEncoded = Yay0Helper.decode(encoded);
    



    if (decodedFromEncoded.length != decodedFromReference.length)
    {
      System.out.println(title + "Lengths do not match!");
    }
    else
    {
      System.out.println(title + "Length matches.");
      boolean matches = true;
      for (int i = 0; i < decodedFromEncoded.length; i++)
      {

        if (decodedFromEncoded[i] != decodedFromReference[i])
        {
          System.out.println(title + String.format("Decoded bytes not equal to reference at offset %X", new Object[] { Integer.valueOf(i) }));
          matches = false;
          break;
        }
      }
      if (matches) {
        System.out.println("VERIFY: Decompressed files match.");
      }
    }
  }
  
  private void dump2() throws IOException {
    FileUtils.deleteDirectory(new File("./yay0/encoded/"));
    FileUtils.deleteDirectory(new File("./yay0/decoded/"));
    FileUtils.deleteDirectory(new File("./yay0/reference/"));
    
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    raf.seek(31719456L);
    for (int i = 0; i < 1033; i++)
    {
      raf.seek(31719456 + i * 28);
      String name = readString(raf, 16);
      int offset = raf.readInt() + 31719456;
      int compressedLength = raf.readInt();
      int decompressedLength = raf.readInt();
      
      raf.seek(offset);
      if (raf.readInt() == 1499560240)
      {
        int yay0length = raf.readInt();
        assert (yay0length == decompressedLength);
        
        byte[] dumpedBytes = new byte[compressedLength];
        raf.seek(offset);
        raf.read(dumpedBytes);
        
        File referenceFile = new File("./yay0/reference/" + String.format("%08X.bin", new Object[] { Integer.valueOf(offset) }));
        FileUtils.writeByteArrayToFile(referenceFile, dumpedBytes);
        
        byte[] decodedBytes = Yay0Helper.decode(dumpedBytes);
        File decodedFile = new File("./yay0/decoded/" + String.format("%08X.bin", new Object[] { Integer.valueOf(offset) }));
        FileUtils.writeByteArrayToFile(decodedFile, decodedBytes);
        
        byte[] encodedBytes = Yay0Helper.encode(decodedBytes);
        File encodedFile = new File("./yay0/encoded/" + String.format("%08X.bin", new Object[] { Integer.valueOf(offset) }));
        FileUtils.writeByteArrayToFile(encodedFile, encodedBytes);
      }
    }
    


    raf.close();
  }
  




































































  public void checkCompressedFiles()
    throws IOException
  {
    int total = 0;
    int larger = 0;
    int smaller = 0;
    
    int totalDiff = 0;
    int totalReferenceSize = 0;
    
    for (File f : new File("./yay0/encoded/").listFiles())
    {
      File ref = new File("./yay0/reference/" + f.getName());
      assert (ref.exists());
      
      if (f.length() != ref.length())
      {
        float diff = (float)(f.length() - ref.length());
        
        if (diff > 0.0F)
        {
          System.out.println(String.format("%s is %d bytes LARGER than reference! (%.3f", new Object[] {f
            .getName(), Integer.valueOf((int)diff), Float.valueOf(100.0F * diff / (float)ref.length()) }) + "%) " + ref.length());
          larger++;
        }
        
        if (diff < 0.0F)
        {
          System.out.println(String.format("%s is %d bytes SMALLER than reference! (%.3f", new Object[] {f
            .getName(), Integer.valueOf((int)-diff), Float.valueOf(100.0F * -diff / (float)ref.length()) }) + "%) " + ref.length());
          smaller++;
        }
        
        totalDiff = (int)(totalDiff + diff);
      }
      
      totalReferenceSize = (int)(totalReferenceSize + ref.length());
      total++;
    }
    
    System.out.println(total + " files compressed: " + smaller + " are smaller and " + larger + " are larger.");
    System.out.println(String.format("Total difference: %d bytes (%.3f", new Object[] {
      Integer.valueOf(totalDiff), Float.valueOf(100.0F * Math.abs(totalDiff) / totalReferenceSize) }) + "%)");
  }
  
  public void verifyCompressedFiles()
    throws IOException
  {
    int errors = 0;
    int total = 0;
    
    for (File f : new File("./yay0/encoded/").listFiles())
    {
      System.out.println(f.getName());
      File ref = new File("./yay0/reference/" + f.getName());
      assert (ref.exists());
      
      byte[] encoded = FileUtils.readFileToByteArray(f);
      byte[] decodedFromEncoded = Yay0Helper.decode(encoded);
      
      byte[] reference = FileUtils.readFileToByteArray(ref);
      byte[] decodedFromRefernece = Yay0Helper.decode(reference);
      
      if (decodedFromEncoded.length != decodedFromRefernece.length)
      {
        System.out.println(f.getName() + " does not match reference length!");
        errors++;
      }
      else
      {
        for (int i = 0; i < decodedFromEncoded.length; i++)
        {
          if (decodedFromEncoded[i] != decodedFromRefernece[i])
          {
            System.out.println(String.format("%s not equal to reference at offset %X", new Object[] { f.getName(), Integer.valueOf(i) }));
            errors++;
          }
        }
      }
      
      total++;
    }
    
    System.out.println(total + " files decoded with " + (total - errors) + " correct and " + errors + " errors.");
  }
  
  public static String readString(RandomAccessFile raf, int maxlength) throws IOException
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
  
  public static boolean equals(File f1, File f2) throws IOException
  {
    if (f1.length() != f2.length()) {
      return false;
    }
    byte[] b1 = FileUtils.readFileToByteArray(f1);
    byte[] b2 = FileUtils.readFileToByteArray(f2);
    
    for (int i = 0; i < b1.length; i++)
    {
      if (b1[i] != b2[i]) {
        return false;
      }
    }
    return true;
  }
}

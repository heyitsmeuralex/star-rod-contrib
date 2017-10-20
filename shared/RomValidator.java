package shared;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;



public class RomValidator
{
  private static final int LENGTH = 41943040;
  private static final int CRC1 = 1710155066;
  private static final int CRC2 = -310545604;
  private static final int CRC1_BS = -295355675;
  private static final int CRC2_BS = 2112699507;
  private static final String MD5 = "A722F8161FF489943191330BF8416496";
  
  public RomValidator() {}
  
  public static File validateROM(File f)
    throws IOException
  {
    if (f.length() != 41943040L)
    {
      SwingUtils.showFramedMessageDialog(null, "Selected file is not the correct size.", "ROM Validation Error", 0);
      


      return null;
    }
    
    JDialog pleaseWait = new JDialog((JDialog)null);
    pleaseWait.setLocationRelativeTo(null);
    pleaseWait.setTitle("Please Wait");
    pleaseWait.setIconImage(Globals.getDefaultIconImage());
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    
    pleaseWait.setLayout(new MigLayout("fill"));
    pleaseWait.add(new JLabel("Validating selected ROM..."), "wrap");
    pleaseWait.add(progressBar, "growx");
    
    pleaseWait.setMinimumSize(new Dimension(240, 80));
    pleaseWait.pack();
    pleaseWait.setResizable(false);
    pleaseWait.setVisible(false);
    
    RandomAccessFile raf = new RandomAccessFile(f, "r");
    
    raf.seek(16L);
    int crc1 = raf.readInt();
    int crc2 = raf.readInt();
    
    if (crc1 != 1710155066)
    {
      if ((crc1 == -295355675) && (crc2 == 2112699507))
      {

        SwingUtils.showFramedMessageDialog(null, "Selected ROM has incorrect endianness.\nA byte swapped copy will be made.", "ROM Validation Warning", 2);
        



        pleaseWait.setVisible(true);
        
        String path = f.getAbsolutePath();
        String extension = path.substring(path.lastIndexOf("."));
        path = path.substring(0, path.lastIndexOf("."));
        
        File swapped;
        File swapped;
        if (extension.equals(".n64")) {
          swapped = new File(path + ".v64");
        } else {
          swapped = new File(path + "_swapped" + extension);
        }
        byteswap(f, swapped);
        f = swapped;
        
        raf.close();
        raf = new RandomAccessFile(f, "r");
        raf.seek(16L);
        crc1 = raf.readInt();
        crc2 = raf.readInt();
      }
      else
      {
        pleaseWait.setVisible(false);
        SwingUtils.showFramedMessageDialog(null, "Incorrect ROM or version, CRC does not match.", "ROM Validation Failure", 0);
        


        raf.close();
        return null;
      }
    }
    
    pleaseWait.setVisible(true);
    
    if ((crc1 == 1710155066) && (crc2 == -310545604))
    {

      if (!verifyCRCs(raf))
      {
        pleaseWait.setVisible(false);
        SwingUtils.showFramedMessageDialog(null, "ROM data does not match CRC values!", "ROM Validation Failure", 0);
        


        return null;
      }
    }
    else
    {
      pleaseWait.setVisible(false);
      SwingUtils.showFramedMessageDialog(null, "Incorrect ROM or version, CRC does not match.", "ROM Validation Failure", 0);
      


      raf.close();
      return null;
    }
    
    raf.close();
    
    try
    {
      byte[] fileBytes = FileUtils.readFileToByteArray(f);
      MessageDigest digest = MessageDigest.getInstance("MD5");
      
      digest.update(fileBytes);
      byte[] hashedBytes = digest.digest();
      
      StringBuilder sb = new StringBuilder(2 * hashedBytes.length);
      for (byte b : hashedBytes) {
        sb.append(String.format("%02X", new Object[] { Byte.valueOf(b) }));
      }
      String fileMD5 = sb.toString();
      if (!fileMD5.equals("A722F8161FF489943191330BF8416496"))
      {
        pleaseWait.setVisible(false);
        SwingUtils.showFramedMessageDialog(null, "MD5 hash does not match!", "ROM Validation Failure", 0);
        


        return null;
      }
    }
    catch (NoSuchAlgorithmException e)
    {
      SwingUtils.showFramedMessageDialog(null, "Missing MD5 hash algorithm, could not complete validation!", "ROM Validation Warning", 2);
    }
    



    pleaseWait.setVisible(false);
    pleaseWait.dispose();
    
    return f;
  }
  
  private static void byteswap(File in, File out) throws IOException
  {
    byte[] file = FileUtils.readFileToByteArray(in);
    byte[] swapped = new byte[file.length];
    
    for (int i = 0; i < file.length; i += 2)
    {
      swapped[i] = file[(i + 1)];
      swapped[(i + 1)] = file[i];
    }
    
    FileUtils.writeByteArrayToFile(out, swapped);
  }
  

  private static boolean verifyCRCs(RandomAccessFile raf)
    throws IOException
  {
    long t6;
    
    long t5;
    
    long t4;
    
    long t3;
    long t2;
    long t1 = t2 = t3 = t4 = t5 = t6 = -1551341735L;
    


    for (int i = 4096; i < 1052672; i += 4)
    {
      raf.seek(i);
      long d = raf.readInt() & 0xFFFFFFFF;
      if ((t6 + d & 0xFFFFFFFF) < (t6 & 0xFFFFFFFF)) t4 += 1L;
      t6 += d;
      t3 ^= d;
      
      long r = (d << (int)(d & 0x1F) | d >> (int)(32L - (d & 0x1F))) & 0xFFFFFFFF;
      
      t5 += r;
      if ((t2 & 0xFFFFFFFF) > (d & 0xFFFFFFFF)) t2 ^= r; else {
        t2 ^= t6 ^ d;
      }
      t1 += (t5 ^ d);
    }
    
    int crc1 = (int)((t6 ^ t4) + t3);
    int crc2 = (int)((t5 ^ t2) + t1);
    
    return (crc1 == 1710155066) && (crc2 == -310545604);
  }
}

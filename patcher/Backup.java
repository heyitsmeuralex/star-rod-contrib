package patcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import main.Directories;
import org.apache.commons.io.FileUtils;

public class Backup
{
  private ArrayList<File> fileList;
  
  public Backup()
  {
    fileList = new ArrayList();
  }
  
  public void addDirectory(File dir)
  {
    if (!dir.isDirectory()) {
      throw new RuntimeException("Tried to add file as directory: " + dir.getAbsolutePath());
    }
    for (File f : dir.listFiles())
    {
      if (f.isDirectory()) {
        addDirectory(f);
      } else {
        addFile(f);
      }
    }
  }
  
  public void addFile(File f) {
    if (f.isDirectory()) {
      throw new RuntimeException("Tried to add directory as file: " + f.getAbsolutePath());
    }
    fileList.add(f);
  }
  
  public void writeAllFiles(File dest) throws IOException
  {
    System.out.print("Backing up " + fileList.size() + " files");
    

    long totalSize = 4L;
    

    for (Iterator localIterator = fileList.iterator(); localIterator.hasNext();) { f = (File)localIterator.next();
      

      Path filePath = Paths.get(f.getAbsolutePath(), new String[0]);
      Path modPath = Paths.get(Directories.getModPath(), new String[0]);
      String relPath = modPath.relativize(filePath).toString();
      totalSize += 2 * relPath.toCharArray().length;
      totalSize += 8L;
      

      totalSize += f.length();
    }
    File f;
    System.out.printf(" (%04.2f Mb)... ", new Object[] { Float.valueOf((float)totalSize / 1048576.0F) });
    

    ByteBuffer bb = ByteBuffer.allocateDirect((int)totalSize);
    bb.putInt(fileList.size());
    
    for (File f : fileList)
    {
      Path filePath = Paths.get(f.getAbsolutePath(), new String[0]);
      Path modPath = Paths.get(Directories.getModPath(), new String[0]);
      char[] relPath = modPath.relativize(filePath).toString().toCharArray();
      
      bb.putInt((int)f.length());
      bb.putInt(relPath.length);
      for (char c : relPath) {
        bb.putChar(c);
      }
    }
    
    for (File f : fileList)
    {
      byte[] bytes = FileUtils.readFileToByteArray(f);
      bb.put(bytes);
    }
    
    byte[] archive = new byte[bb.position()];
    bb.flip();
    bb.get(archive);
    


    FileUtils.writeByteArrayToFile(dest, archive);
    
    System.out.println("done.");
  }
  
  public static void unpackFiles(File source) throws IOException
  {
    byte[] archive = FileUtils.readFileToByteArray(source);
    
    ByteBuffer bb = ByteBuffer.wrap(archive);
    
    int numFiles = bb.getInt();
    int[] lengths = new int[numFiles];
    String[] names = new String[numFiles];
    
    System.out.print("Unpacking " + numFiles + " files from backup... ");
    
    for (int i = 0; i < numFiles; i++)
    {
      lengths[i] = bb.getInt();
      int pathLength = bb.getInt();
      StringBuilder sb = new StringBuilder(pathLength);
      for (int j = 0; j < pathLength; j++)
      {
        sb.append(bb.getChar());
      }
      names[i] = sb.toString();
    }
    
    FileUtils.forceMkdir(Directories.MOD_BACKUP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_BACKUP.toFile());
    
    for (int i = 0; i < numFiles; i++)
    {
      byte[] fileBytes = new byte[lengths[i]];
      bb.get(fileBytes, 0, lengths[i]);
      
      File f = new File(Directories.MOD_BACKUP + names[i]);
      FileUtils.writeByteArrayToFile(f, fileBytes);
    }
    
    System.out.println("done.");
  }
}

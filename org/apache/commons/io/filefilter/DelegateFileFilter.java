package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;






























public class DelegateFileFilter
  extends AbstractFileFilter
  implements Serializable
{
  private final FilenameFilter filenameFilter;
  private final FileFilter fileFilter;
  
  public DelegateFileFilter(FilenameFilter filter)
  {
    if (filter == null) {
      throw new IllegalArgumentException("The FilenameFilter must not be null");
    }
    filenameFilter = filter;
    fileFilter = null;
  }
  




  public DelegateFileFilter(FileFilter filter)
  {
    if (filter == null) {
      throw new IllegalArgumentException("The FileFilter must not be null");
    }
    fileFilter = filter;
    filenameFilter = null;
  }
  






  public boolean accept(File file)
  {
    if (fileFilter != null) {
      return fileFilter.accept(file);
    }
    return super.accept(file);
  }
  








  public boolean accept(File dir, String name)
  {
    if (filenameFilter != null) {
      return filenameFilter.accept(dir, name);
    }
    return super.accept(dir, name);
  }
  






  public String toString()
  {
    String delegate = fileFilter != null ? fileFilter.toString() : filenameFilter.toString();
    return super.toString() + "(" + delegate + ")";
  }
}

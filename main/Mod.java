package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import main.config.Config;
import main.config.Options;
import main.config.Options.Scope;
import org.apache.commons.io.FileUtils;
import shared.SwingUtils;










public final class Mod
{
  private static final String CONFIG_FILENAME = "mod.cfg";
  private final File directory;
  public Config config;
  public String name;
  
  public Mod(File modDirectory)
  {
    directory = modDirectory;
    
    File modConfig = new File(modDirectory.getAbsolutePath() + "\\" + "mod.cfg");
    try {
      readModConfig(modConfig);
    } catch (IOException e) {
      SwingUtils.showFramedMessageDialog(null, "IOException while attempting to read mod.cfg", "Config Read Exception", 0);
      


      System.exit(-1);
    }
  }
  
  private void readModConfig(File configFile) throws IOException
  {
    if (!configFile.exists())
    {
      int choice = SwingUtils.showFramedConfirmDialog(null, "Could not find mod config!\nCreate a new one?", "Missing Config", 0, 3);
      




      if (choice != 0) {
        System.exit(0);
      }
      boolean success = makeNewConfig(configFile);
      
      if (!success)
      {
        SwingUtils.showFramedMessageDialog(null, "Failed to create new config.\nPlease try again.", "Create Config Failed", 0);
        


        System.exit(0);
      }
      
      config.saveConfigFile();
      return;
    }
    

    config = new Config(configFile, new Options.Scope[] { Options.Scope.Patch, Options.Scope.Editor });
    config.readConfig();
  }
  
  private boolean makeNewConfig(File configFile) throws IOException
  {
    FileUtils.touch(configFile);
    
    config = new Config(configFile, new Options.Scope[] { Options.Scope.Patch, Options.Scope.Editor });
    for (Options opt : Options.values())
    {
      switch (1.$SwitchMap$main$config$Options$Scope[scope.ordinal()])
      {
      case 1: 
      case 2: 
        opt.setToDefault(config);
      }
      
    }
    

    return true;
  }
  
  public File getDirectory()
  {
    return directory;
  }
  
  public void prepareNewRom() throws IOException
  {
    File targetRom = new File(Directories.MOD_OUT + DevContext.getPristineRomName());
    DevContext.copyPristineRom(targetRom);
  }
  
  public RandomAccessFile getTargetRomWriter() throws FileNotFoundException
  {
    File targetRom = new File(Directories.MOD_OUT + DevContext.getPristineRomName());
    return new RandomAccessFile(targetRom, "rw");
  }
  
  public File getTargetRom()
  {
    return new File(Directories.MOD_OUT + DevContext.getPristineRomName());
  }
}

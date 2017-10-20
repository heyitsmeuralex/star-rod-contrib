package main.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import main.InputFileException;
import shared.SwingUtils;
import util.IOUtils;
import util.Logger;






public class Config
{
  private final LinkedHashMap<String, String> settings = new LinkedHashMap();
  
  private final File file;
  private Options.Scope[] permittedOptions;
  
  public Config(File cfg, Options.Scope... permittedOptions)
  {
    file = cfg;
    this.permittedOptions = permittedOptions;
  }
  
  public void readConfig() throws IOException
  {
    List<String> lines = IOUtils.readTextFile(file, false);
    
    for (String line : lines)
    {
      if (!line.contains("=")) {
        throw new InputFileException(file, "Missing assignment on line:\r\n" + line);
      }
      String[] tokens = line.split("\\s*=\\s*");
      
      if (tokens.length != 2) {
        throw new InputFileException(file, "Multiple assignments on line:\r\n" + line);
      }
      settings.put(tokens[0], tokens[1]);
    }
  }
  
  public void saveConfigFile()
  {
    try {
      PrintWriter pw = new PrintWriter(file);
      pw.println("% Auto-generated config file, modify with care.");
      
      for (Map.Entry<String, String> entry : settings.entrySet()) {
        pw.printf("%s = %s\r\n", new Object[] { entry.getKey(), entry.getValue() });
      }
      pw.close();
    }
    catch (FileNotFoundException e) {
      SwingUtils.showFramedMessageDialog(null, "Could not update config: " + file
        .getAbsolutePath(), "Config Write Exception", 0);
      

      System.exit(-1);
    }
    
    Logger.log("Saved config: " + file.getName());
  }
  
  private boolean allowed(Options opt)
  {
    for (Options.Scope s : permittedOptions)
    {
      if (scope == s)
        return true;
    }
    return false;
  }
  
  public void setString(Options opt, String value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.String) {
      throw new RuntimeException("Cannot set option as string: " + key);
    }
    if (value == null) {
      Logger.logWarning("Attempted to set " + key + " to null");
    } else {
      settings.put(key, value);
    }
  }
  
  public String getString(Options opt) {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to get option " + key);
    }
    if (type != Options.Type.String) {
      throw new RuntimeException("Cannot get string value for option: " + key);
    }
    String s = (String)settings.get(key);
    return (s == null) || (s.equals("null")) ? defaultValue : s;
  }
  
  public void setBoolean(Options opt, boolean value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Boolean) {
      throw new RuntimeException("Cannot set option as boolean: " + key);
    }
    if (value) {
      settings.put(key, "true");
    } else {
      settings.put(key, "false");
    }
  }
  
  public void setBoolean(Options opt, String value) {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Boolean) {
      throw new RuntimeException("Cannot set option as boolean: " + key);
    }
    if (value.equalsIgnoreCase("true")) {
      settings.put(key, "true");
    } else if (value.equalsIgnoreCase("false")) {
      settings.put(key, "false");
    } else {
      throw new RuntimeException(key + " requires a boolean value (true|false).");
    }
  }
  
  public boolean getBoolean(Options opt) {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to get option " + key);
    }
    if (type != Options.Type.Boolean) {
      throw new RuntimeException("Cannot get boolean value for option: " + key);
    }
    String s = (String)settings.get(key);
    if (s == null) {
      s = defaultValue;
    }
    return s.equalsIgnoreCase("true");
  }
  
  public void setInteger(Options opt, int value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Integer) {
      throw new RuntimeException("Cannot set option as integer: " + key);
    }
    settings.put(key, String.valueOf(value));
  }
  
  public void setInteger(Options opt, String value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Integer) {
      throw new RuntimeException("Cannot set option as integer: " + key);
    }
    try {
      Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new RuntimeException(key + " requires an integer value.");
    }
    
    settings.put(key, value);
  }
  
  public int getInteger(Options opt)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to get option " + key);
    }
    if (type != Options.Type.Integer) {
      throw new RuntimeException("Cannot get integer value for option: " + key);
    }
    String s = (String)settings.get(key);
    if (s == null) {
      s = defaultValue;
    }
    return Integer.parseInt(s);
  }
  
  public void setFloat(Options opt, float value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Float) {
      throw new RuntimeException("Cannot set option as float: " + key);
    }
    settings.put(key, String.valueOf(value));
  }
  
  public void setFloat(Options opt, String value)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to set option " + key);
    }
    if (type != Options.Type.Float) {
      throw new RuntimeException("Cannot set option as float: " + key);
    }
    try {
      Float.parseFloat(value);
    } catch (NumberFormatException e) {
      throw new RuntimeException(key + " requires a float value.");
    }
    
    settings.put(key, value);
  }
  
  public float getFloat(Options opt)
  {
    if (!allowed(opt)) {
      throw new RuntimeException(file.getName() + " does not have permission to get option " + key);
    }
    if (type != Options.Type.Float) {
      throw new RuntimeException("Cannot get float value for option: " + key);
    }
    String s = (String)settings.get(key);
    if (s == null) {
      s = defaultValue;
    }
    return Float.parseFloat(s);
  }
}

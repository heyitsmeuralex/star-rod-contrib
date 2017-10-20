package data.strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import main.InputFileException;
import util.DualHashMap;














public class StringEncoder
{
  public StringEncoder() {}
  
  protected static enum Mode
  {
    TEXT,  TAG,  FUNC;
    
    private Mode() {}
  }
  
  private static class EncodedMessageBuilder {
    private final List<Byte> bytes;
    private StringEncoder.Mode mode = StringEncoder.Mode.TEXT;
    private boolean creditsEncoding = false;
    
    private EncodedMessageBuilder()
    {
      bytes = new ArrayList(512);
    }
    
    public ByteBuffer toByteBuffer()
    {
      int paddedSize = bytes.size();
      int remainder = paddedSize % 4;
      if (remainder != 0) {
        paddedSize += 4 - remainder;
      }
      ByteBuffer bb = ByteBuffer.allocateDirect(paddedSize);
      for (Iterator localIterator = bytes.iterator(); localIterator.hasNext();) { byte b = ((Byte)localIterator.next()).byteValue();
        bb.put(b); }
      bb.rewind();
      return bb;
    }
  }
  
  public static ArrayList<StringData> parseStringFile(File f)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    ArrayList<StringData> stringsList = new ArrayList();
    
    boolean readingString = false;
    List<String> currentStringLines = new LinkedList();
    
    int section = 65535;
    int index = 65535;
    String name = "";
    
    String line;
    while ((line = in.readLine()) != null)
    {
      if (!readingString)
      {
        line = line.trim();
        if (!line.isEmpty())
        {

          if (line.contains("="))
          {
            String[] tokens = line.split("=");
            if (tokens.length != 2)
            {
              in.close();
              throw new StringEncodingException("Incorrect format for string identifier: " + line);
            }
            
            name = tokens[1].trim();
            line = tokens[0].trim();
          }
          
          String[] tokens = line.split("-");
          if (tokens.length != 2)
          {
            in.close();
            throw new StringEncodingException("Incorrect format for string identifier: " + line);
          }
          
          try
          {
            section = Integer.parseInt(tokens[0], 16);
            if (tokens[1].equals("?")) {
              index = -1;
            } else
              index = Integer.parseInt(tokens[1], 16);
          } catch (NumberFormatException e) {
            in.close();
            throw new StringEncodingException("Incorrect format for string identifier: " + line);
          }
          
          readingString = true;
        }
        
      }
      else if (!line.isEmpty())
      {
        currentStringLines.add(line);
      }
      else
      {
        try
        {
          ByteBuffer bb = encodeText(currentStringLines);
          stringsList.add(new StringData(bb, section, index, name));
        } catch (StringEncodingException e) {
          in.close();
          throw new InputFileException(f, e.getMessage());
        }
        
        currentStringLines.clear();
        name = "";
        readingString = false;
      }
    }
    in.close();
    

    if (readingString)
    {
      try
      {
        ByteBuffer bb = encodeText(currentStringLines);
        stringsList.add(new StringData(bb, section, index, name));
      } catch (StringEncodingException e) {
        throw new InputFileException(f, e.getMessage());
      }
    }
    
    return stringsList;
  }
  
  public static ByteBuffer encodeString(String s)
  {
    ByteBuffer bb = ByteBuffer.allocate(s.length() + 1);
    for (char c : s.toCharArray())
    {
      if (StringConstants.characterMap.containsInverse(Character.valueOf(c))) {
        bb.put(((Byte)StringConstants.characterMap.getInverse(Character.valueOf(c))).byteValue());
      } else
        throw new StringEncodingException("Unknown character: " + c + ". Could not encode string: " + s);
    }
    bb.flip();
    return bb;
  }
  
  public static ByteBuffer encodeText(List<String> lines)
  {
    EncodedMessageBuilder msg = new EncodedMessageBuilder(null);
    boolean escaping = false;
    
    for (String line : lines)
    {
      StringBuilder tag = new StringBuilder();
      
      for (char c : line.toCharArray())
      {
        if ((c != '\r') && (c != '\n') && (c != '\t'))
        {
          switch (1.$SwitchMap$data$strings$StringEncoder$Mode[mode.ordinal()])
          {
          case 1: 
            if (escaping)
            {
              switch (c)
              {
              case '[': 
              case '\\': 
              case ']': 
              case '{': 
              case '}': 
                encodeChar(msg, c);
                break;
              default: 
                throw new StringEncodingException("Invalid escape sequence: \\" + c);
              }
              escaping = false;
            }
            else if (c == '[')
            {
              mode = Mode.TAG;
              tag = new StringBuilder();
            }
            else if (c == '{')
            {
              mode = Mode.FUNC;
              tag = new StringBuilder();
            }
            else if (c == '\\')
            {
              escaping = true;
            }
            else {
              encodeChar(msg, c); }
            break;
          
          case 2: 
            if (c == ']')
            {
              mode = Mode.TEXT;
              String s = tag.toString();
              String[] tokens = s.split(Character.toString(':'));
              encodeTag(tokens, msg);
            }
            else {
              tag.append(c); }
            break;
          
          case 3: 
            if (c == '}')
            {
              mode = Mode.TEXT;
              String s = tag.toString();
              String[] tokens = s.split(Character.toString(':'));
              encodeFunctionCall(tokens, msg);
            }
            else {
              tag.append(c);
            }
            break;
          } }
      }
    }
    return msg.toByteBuffer();
  }
  
  private static void encodeChar(EncodedMessageBuilder msg, char c)
  {
    DualHashMap<Byte, Character> map = creditsEncoding ? StringConstants.creditsCharacterMap : StringConstants.characterMap;
    
    if (map.containsInverse(Character.valueOf(c))) {
      bytes.add(map.getInverse(Character.valueOf(c)));
    } else {
      throw new StringEncodingException("Could not encode character " + c);
    }
  }
  
  private static void encodeTag(String[] tokens, EncodedMessageBuilder msg) {
    if (creditsEncoding)
    {
      if (StringConstants.SpecialCharacter.encodeMap.containsKey(tokens[0])) {
        throw new StringEncodingException("Special characters can only be used with " + NORMALname + " character style.");
      }
      
      encodeControlCharacter(tokens, msg);


    }
    else if (StringConstants.SpecialCharacter.encodeMap.containsKey(tokens[0])) {
      encodeSpecialCharacter(tokens, msg);
    } else {
      encodeControlCharacter(tokens, msg);
    }
  }
  
  private static void encodeSpecialCharacter(String[] tokens, EncodedMessageBuilder msg)
  {
    if (!StringConstants.SpecialCharacter.encodeMap.containsKey(tokens[0])) {
      throw new StringEncodingException("Invalid special character " + tokens[0]);
    }
    StringConstants.SpecialCharacter tag = (StringConstants.SpecialCharacter)StringConstants.SpecialCharacter.encodeMap.get(tokens[0]);
    bytes.add(Byte.valueOf((byte)code));
  }
  
  private static void encodeControlCharacter(String[] tokens, EncodedMessageBuilder msg)
  {
    if (!StringConstants.ControlCharacter.encodeMap.containsKey(tokens[0])) {
      throw new StringEncodingException("Invalid control character " + tokens[0]);
    }
    StringConstants.ControlCharacter tag = (StringConstants.ControlCharacter)StringConstants.ControlCharacter.encodeMap.get(tokens[0]);
    bytes.add(Byte.valueOf((byte)code));
    
    switch (tag) {
    case STYLE: 
      encodeBoxStyle(tokens, msg); break;
    case FUNC: 
      throw new StringEncodingException("Invalid tag " + StringConstants.ControlCharacter.FUNC + ", use <> to mark functions.");
    default: 
      for (int i = 1; i <= args; i++) {
        bytes.add(Byte.valueOf((byte)Integer.parseInt(tokens[i], 16)));
      }
    }
  }
  
  private static void encodeBoxStyle(String[] tokens, EncodedMessageBuilder msg) {
    if (!StringConstants.StringBoxStyle.encodeMap.containsKey(tokens[1])) {
      throw new StringEncodingException("Invalid message box style type " + tokens[1]);
    }
    StringConstants.StringBoxStyle type = (StringConstants.StringBoxStyle)StringConstants.StringBoxStyle.encodeMap.get(tokens[1]);
    bytes.add(Byte.valueOf((byte)code));
    
    if (tokens.length != args + 2) {
      throw new StringEncodingException("Incorrect number of arguments for " + tokens[1]);
    }
    for (int i = 0; i < args; i++) {
      bytes.add(Byte.valueOf((byte)Integer.parseInt(tokens[(i + 2)], 16)));
    }
  }
  
  private static void encodeFunctionCall(String[] tokens, EncodedMessageBuilder msg) {
    if (!StringConstants.StringFunction.encodeMap.containsKey(tokens[0])) {
      throw new StringEncodingException("Invalid function " + tokens[0]);
    }
    StringConstants.StringFunction func = (StringConstants.StringFunction)StringConstants.StringFunction.encodeMap.get(tokens[0]);
    
    if ((creditsEncoding) && (func != StringConstants.StringFunction.FONT)) {
      throw new StringEncodingException("Invalid function: " + tokens[0] + ".Only the " + FONTname + " function may be called when using special character styles.");
    }
    
    bytes.add(Byte.valueOf((byte)-1));
    bytes.add(Byte.valueOf((byte)code));
    
    switch (1.$SwitchMap$data$strings$StringConstants$StringFunction[func.ordinal()]) {
    case 1: 
      encodeFont(tokens, msg); break;
    case 2:  encodeEffect(tokens, msg, true); break;
    case 3:  encodeEffect(tokens, msg, false); break;
    default: 
      for (int i = 1; i <= args; i++) {
        bytes.add(Byte.valueOf((byte)Integer.parseInt(tokens[i], 16)));
      }
    }
  }
  
  private static void encodeFont(String[] tokens, EncodedMessageBuilder msg) {
    if (!StringConstants.StringFont.encodeMap.containsKey(tokens[1])) {
      throw new StringEncodingException("Invalid character style " + tokens[1]);
    }
    StringConstants.StringFont style = (StringConstants.StringFont)StringConstants.StringFont.encodeMap.get(tokens[1]);
    bytes.add(Byte.valueOf((byte)code));
    
    creditsEncoding = (style != StringConstants.StringFont.NORMAL);
  }
  
  private static void encodeEffect(String[] tokens, EncodedMessageBuilder msg, boolean starting)
  {
    if (!StringConstants.StringEffect.encodeMap.containsKey(tokens[1])) {
      throw new StringEncodingException("Invalid function " + tokens[1]);
    }
    StringConstants.StringEffect effect = (StringConstants.StringEffect)StringConstants.StringEffect.encodeMap.get(tokens[1]);
    bytes.add(Byte.valueOf((byte)code));
    
    if (starting)
    {
      for (int i = 0; i < args; i++) {
        bytes.add(Byte.valueOf((byte)Integer.parseInt(tokens[(i + 2)], 16)));
      }
    }
  }
}

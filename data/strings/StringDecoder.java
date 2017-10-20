package data.strings;

import java.nio.ByteBuffer;
import java.util.HashMap;
import util.DualHashMap;
import util.IOUtils;



public class StringDecoder
{
  public StringDecoder() {}
  
  private static class DecodedMessageBuilder
  {
    private final StringBuilder text;
    private final StringBuilder markup;
    private boolean creditsEncoding = false;
    
    private DecodedMessageBuilder(boolean plaintext)
    {
      text = new StringBuilder();
      if (plaintext) {
        markup = new StringBuilder();
      } else {
        markup = text;
      }
    }
    
    public String toString() {
      return text.toString();
    }
    
    public DecodedMessageBuilder appendText(String s)
    {
      text.append(s);
      return this;
    }
    
    public DecodedMessageBuilder appendMarkup(String s)
    {
      markup.append(s);
      return this;
    }
    
    public DecodedMessageBuilder appendMarkup(char c)
    {
      markup.append(c);
      return this;
    }
  }
  
  public static String toASCII(byte[] message)
  {
    DecodedMessageBuilder msg = new DecodedMessageBuilder(true, null);
    ByteBuffer buffer = IOUtils.getDirectBuffer(message);
    byte b = 0;
    
    while ((buffer.hasRemaining()) && (b != -3))
    {
      b = buffer.get();
      if (StringConstants.ControlCharacter.decodeMap.containsKey(Byte.valueOf(b)))
      {
        StringConstants.ControlCharacter tag = (StringConstants.ControlCharacter)StringConstants.ControlCharacter.decodeMap.get(Byte.valueOf(b));
        switch (1.$SwitchMap$data$strings$StringConstants$ControlCharacter[tag.ordinal()]) {
        case 1: 
          appendTag(msg, name).appendText(" "); break;
        case 2:  appendTag(msg, name); break;
        case 3:  appendTag(msg, name); break;
        case 4:  decodeBoxStyle(msg, buffer); break;
        case 5:  decodeFunctionCall(msg, buffer); break;
        default:  appendTag(msg, buffer, tag, args);
        }
      }
      else if ((!creditsEncoding) && (StringConstants.SpecialCharacter.decodeMap.containsKey(Byte.valueOf(b)))) {
        appendTag(msg, decodeMapgetvalueOfname);
      } else {
        decodeChar(msg, b);
      } }
    return msg.toString();
  }
  
  public static String toMarkup(byte[] message)
  {
    DecodedMessageBuilder msg = new DecodedMessageBuilder(false, null);
    
    ByteBuffer buffer = IOUtils.getDirectBuffer(message);
    byte b = 0;
    
    while ((buffer.hasRemaining()) && (b != -3))
    {
      b = buffer.get();
      
      if (StringConstants.ControlCharacter.decodeMap.containsKey(Byte.valueOf(b)))
      {
        StringConstants.ControlCharacter tag = (StringConstants.ControlCharacter)StringConstants.ControlCharacter.decodeMap.get(Byte.valueOf(b));
        switch (1.$SwitchMap$data$strings$StringConstants$ControlCharacter[tag.ordinal()]) {
        case 1: 
          appendTag(msg, name).appendText("\r\n"); break;
        case 2:  appendTag(msg, name).appendText("\r\n"); break;
        case 3:  appendTag(msg, name).appendText("\r\n"); break;
        case 4:  decodeBoxStyle(msg, buffer); break;
        case 5:  decodeFunctionCall(msg, buffer); break;
        default:  appendTag(msg, buffer, tag, args);
        }
      }
      else if ((!creditsEncoding) && (StringConstants.SpecialCharacter.decodeMap.containsKey(Byte.valueOf(b)))) {
        appendTag(msg, decodeMapgetvalueOfname);
      } else {
        decodeChar(msg, b);
      }
    }
    return msg.toString();
  }
  
  private static void decodeChar(DecodedMessageBuilder msg, byte b)
  {
    DualHashMap<Byte, Character> map = creditsEncoding ? StringConstants.creditsCharacterMap : StringConstants.characterMap;
    
    if (map.contains(Byte.valueOf(b)))
    {
      char c = ((Character)map.get(Byte.valueOf(b))).charValue();
      switch (c)
      {
      case '[': 
      case '\\': 
      case ']': 
      case '{': 
      case '}': 
        text.append('\\');
      }
      text.append(c);
    }
    else {
      throw new RuntimeException("Could not decode byte " + String.format("%02X", new Object[] { Byte.valueOf(b) }));
    }
  }
  
  private static DecodedMessageBuilder appendTag(DecodedMessageBuilder msg, String s) {
    msg.appendMarkup('[');
    msg.appendMarkup(s);
    msg.appendMarkup(']');
    return msg;
  }
  
  private static DecodedMessageBuilder appendTag(DecodedMessageBuilder msg, ByteBuffer buffer, StringConstants.ControlCharacter tag, int args)
  {
    msg.appendMarkup('[');
    msg.appendMarkup(name);
    
    for (int i = 0; i < args; i++)
    {
      msg.appendMarkup(':');
      msg.appendMarkup(String.format("%02X", new Object[] { Byte.valueOf(buffer.get()) }));
    }
    
    msg.appendMarkup(']');
    return msg;
  }
  
  private static DecodedMessageBuilder decodeBoxStyle(DecodedMessageBuilder msg, ByteBuffer buffer)
  {
    msg.appendMarkup('[');
    msg.appendMarkup(STYLEname);
    byte b = buffer.get();
    
    if (StringConstants.StringBoxStyle.decodeMap.containsKey(Byte.valueOf(b)))
    {
      StringConstants.StringBoxStyle type = (StringConstants.StringBoxStyle)StringConstants.StringBoxStyle.decodeMap.get(Byte.valueOf(b));
      msg.appendMarkup(':');
      msg.appendMarkup(name);
      
      for (int i = 0; i < args; i++)
      {
        msg.appendMarkup(':');
        msg.appendMarkup(String.format("%02X", new Object[] { Byte.valueOf(buffer.get()) }));
      }
    }
    else {
      throw new RuntimeException("Unknown begin type " + String.format("%02X", new Object[] { Byte.valueOf(b) }));
    }
    msg.appendMarkup(']');
    return msg;
  }
  
  private static DecodedMessageBuilder decodeFunctionCall(DecodedMessageBuilder msg, ByteBuffer buffer)
  {
    msg.appendMarkup('{');
    byte b = buffer.get();
    
    if (StringConstants.StringFunction.decodeMap.containsKey(Byte.valueOf(b)))
    {
      StringConstants.StringFunction type = (StringConstants.StringFunction)StringConstants.StringFunction.decodeMap.get(Byte.valueOf(b));
      msg.appendMarkup(name);
      
      switch (1.$SwitchMap$data$strings$StringConstants$StringFunction[type.ordinal()]) {
      case 1: 
        decodeFont(msg, buffer); break;
      case 2:  decodeEffect(msg, buffer, true); break;
      case 3:  decodeEffect(msg, buffer, false); break;
      default: 
        for (int i = 0; i < args; i++)
        {
          msg.appendMarkup(':');
          msg.appendMarkup(String.format("%02X", new Object[] { Byte.valueOf(buffer.get()) }));
        }
      }
    }
    else {
      throw new RuntimeException("Unknown function type " + String.format("%02X", new Object[] { Byte.valueOf(b) }));
    }
    msg.appendMarkup('}');
    return msg;
  }
  
  private static DecodedMessageBuilder decodeEffect(DecodedMessageBuilder msg, ByteBuffer buffer, boolean starting)
  {
    msg.appendMarkup(':');
    byte b = buffer.get();
    
    if (StringConstants.StringEffect.decodeMap.containsKey(Byte.valueOf(b)))
    {
      StringConstants.StringEffect type = (StringConstants.StringEffect)StringConstants.StringEffect.decodeMap.get(Byte.valueOf(b));
      msg.appendMarkup(name);
      
      for (int i = 0; i < args; i++)
      {
        msg.appendMarkup(':');
        msg.appendMarkup(String.format("%02X", new Object[] { Byte.valueOf(buffer.get()) }));
      }
    }
    else {
      throw new RuntimeException("Unknown effect type " + String.format("%02X", new Object[] { Byte.valueOf(b) }));
    }
    return msg;
  }
  
  private static DecodedMessageBuilder decodeFont(DecodedMessageBuilder msg, ByteBuffer buffer)
  {
    msg.appendMarkup(':');
    byte b = buffer.get();
    
    if (StringConstants.StringFont.decodeMap.containsKey(Byte.valueOf(b)))
    {
      StringConstants.StringFont type = (StringConstants.StringFont)StringConstants.StringFont.decodeMap.get(Byte.valueOf(b));
      msg.appendMarkup(name);
      creditsEncoding = (type != StringConstants.StringFont.NORMAL);
    }
    else {
      throw new RuntimeException("Unknown font style " + String.format("%02X", new Object[] { Byte.valueOf(b) }));
    }
    return msg;
  }
}

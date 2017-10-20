package data.strings;

import java.util.HashMap;
import util.DualHashMap;



public abstract class StringConstants
{
  public static final int NUM_STRING_SECTIONS = 47;
  public static final String[] STRING_SECTION_NAMES = { "00 Introduction", "01 Postgame Celebration", "02 Toad Town Gate Sector", "03 Toad Town Castle Sector", "04 Toad Town Bridge Sector", "05 Toad Town Train Sector", "06 Toad Town Warehouse Sector", "07 Toad Town Docks Sector", "08 Minigames", "09 Castle Grounds", "0A Shooting Star Summit", "0B Chapter 0", "0C Chapter 1", "0D Chapter 2", "0E Chapter 3", "0F Chapter 4", "10 Chapter 5", "11 Chapter 6", "12 Chapter 7", "13 Bowser's Castle", "14 Peach Segments", "15 Koopa Koot Favors", "16 Russ T Advice", "17 News Bulletin", "18 Gossip Bulletin", "19 Map Tattles", "1A NPC Tattles", "1B Entity Tattles", "1C Enemy Tattles", "1D Menus I", "1E Choices", "1F Menus II", "20 Party Letters + Luigi's Diary", "21 Advice Fortunes", "22 Treasure Fortunes", "23 Item Descriptions I", "24 Item Descriptions II", "25 Item Descriptions III", "26 Item Names", "27 Shop Messages", "28 Partner Descriptions", "29 Enemy Names", "2A Mario Moves", "2B Partner Moves", "2C Quiz Questions", "2D Quiz Options", "2E Credits", "2F Custom" };
  





























  public StringConstants() {}
  





























  public static enum ControlCharacter
  {
    ENDL(0, 240, "BR"), 
    WAIT(0, 241, "WAIT"), 
    PAUSE(1, 242, "PAUSE"), 
    NEXT(0, 251, "NEXT"), 
    STYLE(-1, 252, "STYLE"), 
    END(0, 253, "END"), 
    FUNC(-1, 255, "FUNC");
    
    public final int args;
    public final int code;
    public final String name;
    public static final HashMap<Byte, ControlCharacter> decodeMap;
    public static final HashMap<String, ControlCharacter> encodeMap;
    
    private ControlCharacter(int args, int code, String name) { this.args = args;
      this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (ControlCharacter type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  
  public static enum SpecialCharacter
  {
    NOTE(0, "NOTE"), 
    HEART(144, "HEART"), 
    STAR(145, "STAR"), 
    UP(146, "UP"), 
    DOWN(147, "DOWN"), 
    LEFT(148, "LEFT"), 
    RIGHT(149, "RIGHT"), 
    CIRCLE(150, "CIRCLE"), 
    CROSS(151, "CROSS"), 
    A(152, "A"), 
    B(153, "B"), 
    L(154, "L"), 
    R(155, "R"), 
    Z(156, "Z"), 
    C_UP(157, "C-UP"), 
    C_DOWN(158, "C-DOWN"), 
    C_LEFT(159, "C-LEFT"), 
    C_RIGHT(160, "C-RIGHT"), 
    START(161, "START");
    
    public final int code;
    public final String name;
    public static final HashMap<Byte, SpecialCharacter> decodeMap;
    public static final HashMap<String, SpecialCharacter> encodeMap;
    
    private SpecialCharacter(int code, String name) { this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (SpecialCharacter type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  
  public static enum StringFont
  {
    NORMAL(0, "Normal"), 
    TITLE(3, "Title"), 
    SUBTITLE(4, "Subtitle");
    
    public final int code;
    public final String name;
    public static final HashMap<Byte, StringFont> decodeMap;
    public static final HashMap<String, StringFont> encodeMap;
    
    private StringFont(int code, String name) { this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (StringFont type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  

  public static enum StringBoxStyle
  {
    RIGHT(0, 1, "RIGHT"), 
    LEFT(0, 2, "LEFT"), 
    CENTER(0, 3, "CENTER"), 
    TATTLE(0, 4, "TATTLE"), 
    CHOICE(4, 5, "CHOICE"), 
    INSPECT(0, 6, "INSPECT"), 
    SIGN(0, 7, "SIGN"), 
    LAMPPOST(1, 8, "LAMPPOST"), 
    POSTCARD(1, 9, "POSTCARD"), 
    POPUP(0, 10, "POPUP"), 
    UPGRADE(4, 12, "UPGRADE"), 
    NARRATE(0, 13, "NARRATE"), 
    EPILOGUE(0, 14, "EPILOGUE");
    
    public final int args;
    public final int code;
    public final String name;
    public static final HashMap<Byte, StringBoxStyle> decodeMap;
    public static final HashMap<String, StringBoxStyle> encodeMap;
    
    private StringBoxStyle(int args, int code, String name) { this.args = args;
      this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (StringBoxStyle type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  
  public static enum StringFunction
  {
    FONT(1, 0, "Font"), 
    


    FUNC_04(0, 4, "Func_04"), 
    COLOR(1, 5, "Color"), 
    SPACING(1, 6, "Spacing"), 
    INPUT_OFF(0, 7, "InputOff"), 
    INPUT_ON(0, 8, "InputOn"), 
    DELAY_OFF(0, 9, "DelayOff"), 
    DELAY_ON(0, 10, "DelayOn"), 
    SCROLL(1, 12, "Scroll"), 
    SIZE(2, 13, "Size"), 
    SIZE_RESET(0, 14, "SizeReset"), 
    SPEED(2, 15, "Speed"), 
    SET_XY(2, 16, "SetPrintPos"), 
    SET_Y(1, 17, "SetPrintY"), 
    INDENT(1, 18, "Indent"), 
    DOWN(1, 19, "Down"), 
    UP(1, 20, "Up"), 
    IMAGE1(1, 21, "Image1"), 
    SPRITE(3, 22, "Sprite"), 
    ITEM(2, 23, "Item"), 
    IMAGE7(7, 24, "Image7"), 
    
    FUNC_1A(3, 26, "Func_1A"), 
    FUNC_1B(2, 27, "Func_1B"), 
    FUNC_1C(1, 28, "Func_1C"), 
    
    CURSOR(1, 30, "Cursor"), 
    END_CHOICE(1, 31, "EndChoice"), 
    SET_CANCEL(1, 32, "SetCancel"), 
    OPTION(1, 33, "Option"), 
    START_ANIM(0, 34, "StartAnim"), 
    END_ANIM(0, 35, "EndAnim"), 
    PUSH_COLOR(0, 36, "PushColor"), 
    POP_COLOR(0, 37, "PopColor"), 
    START_FX(-1, 38, "StartFX"), 
    END_FX(-1, 39, "EndFX"), 
    VAR(1, 40, "Var"), 
    FUNC_29(1, 41, "Func_29"), 
    
    FUNC_2B(0, 43, "Func_2B"), 
    

    VOLUME(1, 46, "Volume"), 
    FUNC_2F(1, 47, "SpeechSound");
    
    public final int args;
    public final int code;
    public final String name;
    public static final HashMap<Byte, StringFunction> decodeMap;
    public static final HashMap<String, StringFunction> encodeMap;
    
    private StringFunction(int args, int code, String name) { this.args = args;
      this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (StringFunction type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  

  public static enum StringEffect
  {
    JITTER(0, 0, "Jitter"), 
    WAVY1(0, 1, "Wavy"), 
    NOISE(0, 2, "Noise"), 
    FADED_NOISE(1, 3, "FadedNoise"), 
    UNKNOWN(0, 4, "Unknown"), 
    FADED_JITTER(1, 5, "FadedJitter"), 
    RAINBOW1(0, 6, "Rainbow"), 
    FADED(1, 7, "Faded"), 
    WAVY2(0, 8, "WavyB"), 
    RAINBOW2(0, 9, "RainbowB"), 
    SHRINKING(0, 10, "Shrinking"), 
    GROWING(0, 11, "Growing"), 
    SIZE_JITTER(0, 12, "SizeJitter"), 
    SIZE_WAVE(0, 13, "SizeWave"), 
    DROP_SHADOW(0, 14, "DropShadow");
    
    public final int args;
    public final int code;
    public final String name;
    public static final HashMap<Byte, StringEffect> decodeMap;
    public static final HashMap<String, StringEffect> encodeMap;
    
    private StringEffect(int args, int code, String name) { this.args = args;
      this.code = code;
      this.name = name;
    }
    



    static
    {
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      for (StringEffect type : values())
      {
        decodeMap.put(Byte.valueOf((byte)code), type);
        encodeMap.put(name, type);
      }
    }
  }
  


  protected static final DualHashMap<Byte, Character> characterMap = new DualHashMap();
  
  static { characterMap.add(Byte.valueOf((byte)1), Character.valueOf('!'));
    characterMap.add(Byte.valueOf((byte)2), Character.valueOf('"'));
    characterMap.add(Byte.valueOf((byte)3), Character.valueOf('#'));
    characterMap.add(Byte.valueOf((byte)4), Character.valueOf('$'));
    characterMap.add(Byte.valueOf((byte)5), Character.valueOf('%'));
    characterMap.add(Byte.valueOf((byte)6), Character.valueOf('&'));
    characterMap.add(Byte.valueOf((byte)7), Character.valueOf('\''));
    characterMap.add(Byte.valueOf((byte)8), Character.valueOf('('));
    characterMap.add(Byte.valueOf((byte)9), Character.valueOf(')'));
    characterMap.add(Byte.valueOf((byte)10), Character.valueOf('*'));
    characterMap.add(Byte.valueOf((byte)11), Character.valueOf('+'));
    characterMap.add(Byte.valueOf((byte)12), Character.valueOf(','));
    characterMap.add(Byte.valueOf((byte)13), Character.valueOf('-'));
    characterMap.add(Byte.valueOf((byte)14), Character.valueOf('.'));
    characterMap.add(Byte.valueOf((byte)15), Character.valueOf('/'));
    characterMap.add(Byte.valueOf((byte)16), Character.valueOf('0'));
    characterMap.add(Byte.valueOf((byte)17), Character.valueOf('1'));
    characterMap.add(Byte.valueOf((byte)18), Character.valueOf('2'));
    characterMap.add(Byte.valueOf((byte)19), Character.valueOf('3'));
    characterMap.add(Byte.valueOf((byte)20), Character.valueOf('4'));
    characterMap.add(Byte.valueOf((byte)21), Character.valueOf('5'));
    characterMap.add(Byte.valueOf((byte)22), Character.valueOf('6'));
    characterMap.add(Byte.valueOf((byte)23), Character.valueOf('7'));
    characterMap.add(Byte.valueOf((byte)24), Character.valueOf('8'));
    characterMap.add(Byte.valueOf((byte)25), Character.valueOf('9'));
    characterMap.add(Byte.valueOf((byte)26), Character.valueOf(':'));
    characterMap.add(Byte.valueOf((byte)27), Character.valueOf(';'));
    characterMap.add(Byte.valueOf((byte)28), Character.valueOf('<'));
    characterMap.add(Byte.valueOf((byte)29), Character.valueOf('='));
    characterMap.add(Byte.valueOf((byte)30), Character.valueOf('>'));
    characterMap.add(Byte.valueOf((byte)31), Character.valueOf('?'));
    characterMap.add(Byte.valueOf((byte)32), Character.valueOf('@'));
    characterMap.add(Byte.valueOf((byte)33), Character.valueOf('A'));
    characterMap.add(Byte.valueOf((byte)34), Character.valueOf('B'));
    characterMap.add(Byte.valueOf((byte)35), Character.valueOf('C'));
    characterMap.add(Byte.valueOf((byte)36), Character.valueOf('D'));
    characterMap.add(Byte.valueOf((byte)37), Character.valueOf('E'));
    characterMap.add(Byte.valueOf((byte)38), Character.valueOf('F'));
    characterMap.add(Byte.valueOf((byte)39), Character.valueOf('G'));
    characterMap.add(Byte.valueOf((byte)40), Character.valueOf('H'));
    characterMap.add(Byte.valueOf((byte)41), Character.valueOf('I'));
    characterMap.add(Byte.valueOf((byte)42), Character.valueOf('J'));
    characterMap.add(Byte.valueOf((byte)43), Character.valueOf('K'));
    characterMap.add(Byte.valueOf((byte)44), Character.valueOf('L'));
    characterMap.add(Byte.valueOf((byte)45), Character.valueOf('M'));
    characterMap.add(Byte.valueOf((byte)46), Character.valueOf('N'));
    characterMap.add(Byte.valueOf((byte)47), Character.valueOf('O'));
    characterMap.add(Byte.valueOf((byte)48), Character.valueOf('P'));
    characterMap.add(Byte.valueOf((byte)49), Character.valueOf('Q'));
    characterMap.add(Byte.valueOf((byte)50), Character.valueOf('R'));
    characterMap.add(Byte.valueOf((byte)51), Character.valueOf('S'));
    characterMap.add(Byte.valueOf((byte)52), Character.valueOf('T'));
    characterMap.add(Byte.valueOf((byte)53), Character.valueOf('U'));
    characterMap.add(Byte.valueOf((byte)54), Character.valueOf('V'));
    characterMap.add(Byte.valueOf((byte)55), Character.valueOf('W'));
    characterMap.add(Byte.valueOf((byte)56), Character.valueOf('X'));
    characterMap.add(Byte.valueOf((byte)57), Character.valueOf('Y'));
    characterMap.add(Byte.valueOf((byte)58), Character.valueOf('Z'));
    characterMap.add(Byte.valueOf((byte)59), Character.valueOf('['));
    characterMap.add(Byte.valueOf((byte)60), Character.valueOf('¥'));
    characterMap.add(Byte.valueOf((byte)61), Character.valueOf(']'));
    characterMap.add(Byte.valueOf((byte)62), Character.valueOf('^'));
    characterMap.add(Byte.valueOf((byte)63), Character.valueOf('_'));
    characterMap.add(Byte.valueOf((byte)64), Character.valueOf('`'));
    characterMap.add(Byte.valueOf((byte)65), Character.valueOf('a'));
    characterMap.add(Byte.valueOf((byte)66), Character.valueOf('b'));
    characterMap.add(Byte.valueOf((byte)67), Character.valueOf('c'));
    characterMap.add(Byte.valueOf((byte)68), Character.valueOf('d'));
    characterMap.add(Byte.valueOf((byte)69), Character.valueOf('e'));
    characterMap.add(Byte.valueOf((byte)70), Character.valueOf('f'));
    characterMap.add(Byte.valueOf((byte)71), Character.valueOf('g'));
    characterMap.add(Byte.valueOf((byte)72), Character.valueOf('h'));
    characterMap.add(Byte.valueOf((byte)73), Character.valueOf('i'));
    characterMap.add(Byte.valueOf((byte)74), Character.valueOf('j'));
    characterMap.add(Byte.valueOf((byte)75), Character.valueOf('k'));
    characterMap.add(Byte.valueOf((byte)76), Character.valueOf('l'));
    characterMap.add(Byte.valueOf((byte)77), Character.valueOf('m'));
    characterMap.add(Byte.valueOf((byte)78), Character.valueOf('n'));
    characterMap.add(Byte.valueOf((byte)79), Character.valueOf('o'));
    characterMap.add(Byte.valueOf((byte)80), Character.valueOf('p'));
    characterMap.add(Byte.valueOf((byte)81), Character.valueOf('q'));
    characterMap.add(Byte.valueOf((byte)82), Character.valueOf('r'));
    characterMap.add(Byte.valueOf((byte)83), Character.valueOf('s'));
    characterMap.add(Byte.valueOf((byte)84), Character.valueOf('t'));
    characterMap.add(Byte.valueOf((byte)85), Character.valueOf('u'));
    characterMap.add(Byte.valueOf((byte)86), Character.valueOf('v'));
    characterMap.add(Byte.valueOf((byte)87), Character.valueOf('w'));
    characterMap.add(Byte.valueOf((byte)88), Character.valueOf('x'));
    characterMap.add(Byte.valueOf((byte)89), Character.valueOf('y'));
    characterMap.add(Byte.valueOf((byte)90), Character.valueOf('z'));
    characterMap.add(Byte.valueOf((byte)91), Character.valueOf('{'));
    characterMap.add(Byte.valueOf((byte)92), Character.valueOf('|'));
    characterMap.add(Byte.valueOf((byte)93), Character.valueOf('}'));
    characterMap.add(Byte.valueOf((byte)94), Character.valueOf('~'));
    characterMap.add(Byte.valueOf((byte)95), Character.valueOf('°'));
    characterMap.add(Byte.valueOf((byte)96), Character.valueOf('À'));
    characterMap.add(Byte.valueOf((byte)97), Character.valueOf('Á'));
    characterMap.add(Byte.valueOf((byte)98), Character.valueOf('Â'));
    characterMap.add(Byte.valueOf((byte)99), Character.valueOf('Ä'));
    characterMap.add(Byte.valueOf((byte)100), Character.valueOf('Ç'));
    characterMap.add(Byte.valueOf((byte)101), Character.valueOf('È'));
    characterMap.add(Byte.valueOf((byte)102), Character.valueOf('É'));
    characterMap.add(Byte.valueOf((byte)103), Character.valueOf('Ê'));
    characterMap.add(Byte.valueOf((byte)104), Character.valueOf('Ë'));
    characterMap.add(Byte.valueOf((byte)105), Character.valueOf('Ì'));
    characterMap.add(Byte.valueOf((byte)106), Character.valueOf('Í'));
    characterMap.add(Byte.valueOf((byte)107), Character.valueOf('Î'));
    characterMap.add(Byte.valueOf((byte)108), Character.valueOf('Ï'));
    characterMap.add(Byte.valueOf((byte)109), Character.valueOf('Ñ'));
    characterMap.add(Byte.valueOf((byte)110), Character.valueOf('Ò'));
    characterMap.add(Byte.valueOf((byte)111), Character.valueOf('Ó'));
    characterMap.add(Byte.valueOf((byte)112), Character.valueOf('Ô'));
    characterMap.add(Byte.valueOf((byte)113), Character.valueOf('Ö'));
    characterMap.add(Byte.valueOf((byte)114), Character.valueOf('Ù'));
    characterMap.add(Byte.valueOf((byte)115), Character.valueOf('Ú'));
    characterMap.add(Byte.valueOf((byte)116), Character.valueOf('Û'));
    characterMap.add(Byte.valueOf((byte)117), Character.valueOf('Ü'));
    characterMap.add(Byte.valueOf((byte)118), Character.valueOf('ß'));
    characterMap.add(Byte.valueOf((byte)119), Character.valueOf('à'));
    characterMap.add(Byte.valueOf((byte)120), Character.valueOf('á'));
    characterMap.add(Byte.valueOf((byte)121), Character.valueOf('â'));
    characterMap.add(Byte.valueOf((byte)122), Character.valueOf('ä'));
    characterMap.add(Byte.valueOf((byte)123), Character.valueOf('ç'));
    characterMap.add(Byte.valueOf((byte)124), Character.valueOf('è'));
    characterMap.add(Byte.valueOf((byte)125), Character.valueOf('é'));
    characterMap.add(Byte.valueOf((byte)126), Character.valueOf('ê'));
    characterMap.add(Byte.valueOf((byte)Byte.MAX_VALUE), Character.valueOf('ë'));
    characterMap.add(Byte.valueOf((byte)Byte.MIN_VALUE), Character.valueOf('ì'));
    characterMap.add(Byte.valueOf((byte)-127), Character.valueOf('í'));
    characterMap.add(Byte.valueOf((byte)-126), Character.valueOf('î'));
    characterMap.add(Byte.valueOf((byte)-125), Character.valueOf('ï'));
    characterMap.add(Byte.valueOf((byte)-124), Character.valueOf('ñ'));
    characterMap.add(Byte.valueOf((byte)-123), Character.valueOf('ò'));
    characterMap.add(Byte.valueOf((byte)-122), Character.valueOf('ó'));
    characterMap.add(Byte.valueOf((byte)-121), Character.valueOf('ô'));
    characterMap.add(Byte.valueOf((byte)-120), Character.valueOf('ö'));
    characterMap.add(Byte.valueOf((byte)-119), Character.valueOf('ù'));
    characterMap.add(Byte.valueOf((byte)-118), Character.valueOf('ú'));
    characterMap.add(Byte.valueOf((byte)-117), Character.valueOf('û'));
    characterMap.add(Byte.valueOf((byte)-116), Character.valueOf('ü'));
    characterMap.add(Byte.valueOf((byte)-115), Character.valueOf('¡'));
    characterMap.add(Byte.valueOf((byte)-114), Character.valueOf('¿'));
    characterMap.add(Byte.valueOf((byte)-113), Character.valueOf('ª'));
    
    characterMap.add(Byte.valueOf((byte)-94), Character.valueOf(' '));
    
    characterMap.add(Byte.valueOf((byte)-9), Character.valueOf(' '));
    

    creditsCharacterMap = new DualHashMap();
    creditsCharacterMap.add(Byte.valueOf((byte)0), Character.valueOf('A'));
    creditsCharacterMap.add(Byte.valueOf((byte)1), Character.valueOf('B'));
    creditsCharacterMap.add(Byte.valueOf((byte)2), Character.valueOf('C'));
    creditsCharacterMap.add(Byte.valueOf((byte)3), Character.valueOf('D'));
    creditsCharacterMap.add(Byte.valueOf((byte)4), Character.valueOf('E'));
    creditsCharacterMap.add(Byte.valueOf((byte)5), Character.valueOf('F'));
    creditsCharacterMap.add(Byte.valueOf((byte)6), Character.valueOf('G'));
    creditsCharacterMap.add(Byte.valueOf((byte)7), Character.valueOf('H'));
    creditsCharacterMap.add(Byte.valueOf((byte)8), Character.valueOf('I'));
    creditsCharacterMap.add(Byte.valueOf((byte)9), Character.valueOf('J'));
    creditsCharacterMap.add(Byte.valueOf((byte)10), Character.valueOf('K'));
    creditsCharacterMap.add(Byte.valueOf((byte)11), Character.valueOf('L'));
    creditsCharacterMap.add(Byte.valueOf((byte)12), Character.valueOf('M'));
    creditsCharacterMap.add(Byte.valueOf((byte)13), Character.valueOf('N'));
    creditsCharacterMap.add(Byte.valueOf((byte)14), Character.valueOf('O'));
    creditsCharacterMap.add(Byte.valueOf((byte)15), Character.valueOf('P'));
    creditsCharacterMap.add(Byte.valueOf((byte)16), Character.valueOf('Q'));
    creditsCharacterMap.add(Byte.valueOf((byte)17), Character.valueOf('R'));
    creditsCharacterMap.add(Byte.valueOf((byte)18), Character.valueOf('S'));
    creditsCharacterMap.add(Byte.valueOf((byte)19), Character.valueOf('T'));
    creditsCharacterMap.add(Byte.valueOf((byte)20), Character.valueOf('U'));
    creditsCharacterMap.add(Byte.valueOf((byte)21), Character.valueOf('V'));
    creditsCharacterMap.add(Byte.valueOf((byte)22), Character.valueOf('W'));
    creditsCharacterMap.add(Byte.valueOf((byte)23), Character.valueOf('X'));
    creditsCharacterMap.add(Byte.valueOf((byte)24), Character.valueOf('Y'));
    creditsCharacterMap.add(Byte.valueOf((byte)25), Character.valueOf('Z'));
    creditsCharacterMap.add(Byte.valueOf((byte)26), Character.valueOf('\''));
    creditsCharacterMap.add(Byte.valueOf((byte)27), Character.valueOf('.'));
    creditsCharacterMap.add(Byte.valueOf((byte)28), Character.valueOf(','));
    creditsCharacterMap.add(Byte.valueOf((byte)29), Character.valueOf('0'));
    creditsCharacterMap.add(Byte.valueOf((byte)30), Character.valueOf('1'));
    creditsCharacterMap.add(Byte.valueOf((byte)31), Character.valueOf('2'));
    creditsCharacterMap.add(Byte.valueOf((byte)32), Character.valueOf('3'));
    creditsCharacterMap.add(Byte.valueOf((byte)33), Character.valueOf('4'));
    creditsCharacterMap.add(Byte.valueOf((byte)34), Character.valueOf('5'));
    creditsCharacterMap.add(Byte.valueOf((byte)35), Character.valueOf('6'));
    creditsCharacterMap.add(Byte.valueOf((byte)36), Character.valueOf('7'));
    creditsCharacterMap.add(Byte.valueOf((byte)37), Character.valueOf('8'));
    creditsCharacterMap.add(Byte.valueOf((byte)38), Character.valueOf('9'));
    creditsCharacterMap.add(Byte.valueOf((byte)39), Character.valueOf('©'));
    creditsCharacterMap.add(Byte.valueOf((byte)40), Character.valueOf('&'));
    creditsCharacterMap.add(Byte.valueOf((byte)-9), Character.valueOf(' '));
  }
  
  protected static final DualHashMap<Byte, Character> creditsCharacterMap;
  protected static final char OPEN_TAG = '[';
  protected static final char CLOSE_TAG = ']';
  protected static final char DELIMITER = ':';
  protected static final char OPEN_FUNC = '{';
  protected static final char CLOSE_FUNC = '}';
  protected static final char ESCAPE = '\\';
}

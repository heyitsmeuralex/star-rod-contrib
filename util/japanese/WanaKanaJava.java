package util.japanese;

import java.io.PrintStream;
import util.DualHashMap;

public class WanaKanaJava
{
  private static final int HIRAGANA_START = 12353;
  private static final int HIRAGANA_END = 12438;
  private static final int KATAKANA_START = 12449;
  private static final int KATAKANA_END = 12538;
  
  public static void main(String[] args)
  {
    WanaKanaJava wkj = new WanaKanaJava();
    wkj.print();
  }
  

  public void print()
  {
    for (String s : J2R.getKeySet())
    {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i++) {
        sb.append(toUnicode(s.charAt(i)));
      }
      System.out.printf("J2R.add(\"%s\", \"%s\"); // %s \r\n", new Object[] { sb.toString(), J2R.get(s), s });
    }
  }
  
  private static String toUnicode(char ch)
  {
    return String.format("\\u%04x", new Object[] { Integer.valueOf(ch) });
  }
  




  private DualHashMap<String, String> J2R = new DualHashMap();
  





  public WanaKanaJava()
  {
    prepareMap();
  }
  

  private boolean _allTrue(String checkStr, Command func)
  {
    for (int _i = 0; _i < checkStr.length(); _i++)
    {
      if (!func.run(String.valueOf(checkStr.charAt(_i))))
      {
        return false;
      }
    }
    return true;
  }
  

  private boolean _isCharInRange(char chr, int start, int end)
  {
    int code = chr;
    return (start <= code) && (code <= end);
  }
  
  private boolean _isCharKatakana(char chr)
  {
    return _isCharInRange(chr, 12449, 12538);
  }
  
  private boolean _isCharHiragana(char chr)
  {
    return _isCharInRange(chr, 12353, 12438);
  }
  

  private String _katakanaToHiragana(String kata)
  {
    String hira = "";
    
    for (int _i = 0; _i < kata.length(); _i++)
    {
      char kataChar = kata.charAt(_i);
      
      if (_isCharKatakana(kataChar))
      {
        int code = kataChar;
        code -= 96;
        hira = hira + String.valueOf(Character.toChars(code));
      }
      else
      {
        hira = hira + kataChar;
      }
    }
    
    return hira;
  }
  
  public String _hiraganaToRomaji(String hira)
  {
    if (isRomaji(hira))
    {
      return hira;
    }
    
    String chunk = "";
    
    int cursor = 0;
    int len = hira.length();
    int maxChunk = 2;
    boolean nextCharIsDoubleConsonant = false;
    String roma = "";
    String romaChar = null;
    
    while (cursor < len)
    {
      int chunkSize = Math.min(maxChunk, len - cursor);
      while (chunkSize > 0)
      {
        chunk = hira.substring(cursor, cursor + chunkSize);
        
        if (isKatakana(chunk))
        {
          chunk = _katakanaToHiragana(chunk);
        }
        
        if ((String.valueOf(chunk.charAt(0)).equals("ï¿½?ï¿½")) && (chunkSize == 1) && (cursor < len - 1))
        {
          nextCharIsDoubleConsonant = true;
          romaChar = "";
          break;
        }
        
        romaChar = (String)J2R.get(chunk);
        
        if ((romaChar != null) && (nextCharIsDoubleConsonant))
        {
          romaChar = romaChar.charAt(0) + romaChar;
          nextCharIsDoubleConsonant = false;
        }
        
        if (romaChar != null) {
          break;
        }
        

        chunkSize--;
      }
      if (romaChar == null)
      {
        romaChar = chunk;
      }
      
      roma = roma + romaChar;
      cursor += (chunkSize > 0 ? chunkSize : 1);
    }
    return roma;
  }
  
  public boolean isHiragana(String input)
  {
    _allTrue(input, new Command()
    {

      public boolean run(String str)
      {
        return WanaKanaJava.this._isCharHiragana(str.charAt(0));
      }
    });
  }
  
  public boolean isKatakana(String input)
  {
    _allTrue(input, new Command()
    {

      public boolean run(String str)
      {
        return WanaKanaJava.this._isCharKatakana(str.charAt(0));
      }
    });
  }
  
  public boolean isKana(String input)
  {
    _allTrue(input, new Command()
    {

      public boolean run(String str)
      {
        return (isHiragana(str)) || (isKatakana(str));
      }
    });
  }
  
  public boolean isRomaji(String input)
  {
    _allTrue(input, new Command()
    {

      public boolean run(String str)
      {
        return (!isHiragana(str)) && (!isKatakana(str));
      }
    });
  }
  
  public String toRomaji(String input)
  {
    return _hiraganaToRomaji(input);
  }
  










































































































































































































































  private void prepareMap()
  {
    J2R.add("ー", "-");
    J2R.add("、", ",");
    J2R.add("０", "0");
    J2R.add("１", "1");
    J2R.add("２", "2");
    J2R.add("３", "3");
    J2R.add("４", "4");
    J2R.add("５", "5");
    J2R.add("６", "6");
    J2R.add("７", "7");
    J2R.add("８", "8");
    J2R.add("９", "9");
    J2R.add("x", "x");
    J2R.add("あ", "a");
    J2R.add("い", "i");
    J2R.add("う", "u");
    J2R.add("え", "e");
    J2R.add("お", "o");
    J2R.add("ゔぁ", "va");
    J2R.add("ゔぃ", "vi");
    J2R.add("ゔ", "vu");
    J2R.add("ゔぇ", "ve");
    J2R.add("ゔぉ", "vo");
    J2R.add("か", "ka");
    J2R.add("き", "ki");
    J2R.add("きゃ", "kya");
    J2R.add("きぃ", "kyi");
    J2R.add("きゅ", "kyu");
    J2R.add("く", "ku");
    J2R.add("け", "ke");
    J2R.add("こ", "ko");
    J2R.add("が", "ga");
    J2R.add("ぎ", "gi");
    J2R.add("ぐ", "gu");
    J2R.add("げ", "ge");
    J2R.add("ご", "go");
    J2R.add("ぎゃ", "gya");
    J2R.add("ぎぃ", "gyi");
    J2R.add("ぎゅ", "gyu");
    J2R.add("ぎぇ", "gye");
    J2R.add("ぎょ", "gyo");
    J2R.add("さ", "sa");
    J2R.add("す", "su");
    J2R.add("せ", "se");
    J2R.add("そ", "so");
    J2R.add("ざ", "za");
    J2R.add("ず", "zu");
    J2R.add("ぜ", "ze");
    J2R.add("ぞ", "zo");
    J2R.add("し", "shi");
    J2R.add("しゃ", "sha");
    J2R.add("しゅ", "shu");
    J2R.add("しょ", "sho");
    J2R.add("じ", "ji");
    J2R.add("じゃ", "ja");
    J2R.add("じゅ", "ju");
    J2R.add("じょ", "jo");
    J2R.add("た", "ta");
    J2R.add("ち", "chi");
    J2R.add("ちゃ", "cha");
    J2R.add("ちゅ", "chu");
    J2R.add("ちょ", "cho");
    J2R.add("つ", "tsu");
    J2R.add("て", "te");
    J2R.add("と", "to");
    J2R.add("だ", "da");
    J2R.add("ぢ", "di");
    J2R.add("づ", "du");
    J2R.add("で", "de");
    J2R.add("ど", "do");
    J2R.add("な", "na");
    J2R.add("に", "ni");
    J2R.add("にゃ", "nya");
    J2R.add("にゅ", "nyu");
    J2R.add("にょ", "nyo");
    J2R.add("ぬ", "nu");
    J2R.add("ね", "ne");
    J2R.add("の", "no");
    J2R.add("は", "ha");
    J2R.add("ひ", "hi");
    J2R.add("ふ", "fu");
    J2R.add("へ", "he");
    J2R.add("ほ", "ho");
    J2R.add("ひゃ", "hya");
    J2R.add("ひゅ", "hyu");
    J2R.add("ひょ", "hyo");
    J2R.add("ふぁ", "fa");
    J2R.add("ふぃ", "fi");
    J2R.add("ふぇ", "fe");
    J2R.add("ふぉ", "fo");
    J2R.add("ば", "ba");
    J2R.add("び", "bi");
    J2R.add("ぶ", "bu");
    J2R.add("べ", "be");
    J2R.add("ぼ", "bo");
    J2R.add("びゃ", "bya");
    J2R.add("びゅ", "byu");
    J2R.add("びょ", "byo");
    J2R.add("ぱ", "pa");
    J2R.add("ぴ", "pi");
    J2R.add("ぷ", "pu");
    J2R.add("ぺ", "pe");
    J2R.add("ぽ", "po");
    J2R.add("ぴゃ", "pya");
    J2R.add("ぴゅ", "pyu");
    J2R.add("ぴょ", "pyo");
    J2R.add("ま", "ma");
    J2R.add("み", "mi");
    J2R.add("む", "mu");
    J2R.add("め", "me");
    J2R.add("も", "mo");
    J2R.add("みゃ", "mya");
    J2R.add("みゅ", "myu");
    J2R.add("みょ", "myo");
    J2R.add("や", "ya");
    J2R.add("ゆ", "yu");
    J2R.add("よ", "yo");
    J2R.add("ら", "ra");
    J2R.add("り", "ri");
    J2R.add("る", "ru");
    J2R.add("れ", "re");
    J2R.add("ろ", "ro");
    J2R.add("りゃ", "rya");
    J2R.add("りゅ", "ryu");
    J2R.add("りょ", "ryo");
    J2R.add("わ", "wa");
    J2R.add("を", "wo");
    J2R.add("ん", "n");
    J2R.add("ゐ", "wi");
    J2R.add("ゑ", "we");
    J2R.add("きぇ", "kye");
    J2R.add("きょ", "kyo");
    J2R.add("じぃ", "jyi");
    J2R.add("じぇ", "jye");
    J2R.add("ちぃ", "cyi");
    J2R.add("ちぇ", "che");
    J2R.add("ひぃ", "hyi");
    J2R.add("ひぇ", "hye");
    J2R.add("びぃ", "byi");
    J2R.add("びぇ", "bye");
    J2R.add("ぴぃ", "pyi");
    J2R.add("ぴぇ", "pye");
    J2R.add("みぇ", "mye");
    J2R.add("みぃ", "myi");
    J2R.add("りぃ", "ryi");
    J2R.add("りぇ", "rye");
    J2R.add("にぃ", "nyi");
    J2R.add("にぇ", "nye");
    J2R.add("しぃ", "syi");
    J2R.add("しぇ", "she");
    J2R.add("いぇ", "ye");
    J2R.add("うぁ", "wha");
    J2R.add("うぉ", "who");
    J2R.add("うぃ", "wi");
    J2R.add("うぇ", "we");
    J2R.add("ゔゃ", "vya");
    J2R.add("ゔゅ", "vyu");
    J2R.add("ゔょ", "vyo");
    J2R.add("すぁ", "swa");
    J2R.add("すぃ", "swi");
    J2R.add("すぅ", "swu");
    J2R.add("すぇ", "swe");
    J2R.add("すぉ", "swo");
    J2R.add("くゃ", "qya");
    J2R.add("くゅ", "qyu");
    J2R.add("くょ", "qyo");
    J2R.add("くぁ", "qwa");
    J2R.add("くぃ", "qwi");
    J2R.add("くぅ", "qwu");
    J2R.add("くぇ", "qwe");
    J2R.add("くぉ", "qwo");
    J2R.add("ぐぁ", "gwa");
    J2R.add("ぐぃ", "gwi");
    J2R.add("ぐぅ", "gwu");
    J2R.add("ぐぇ", "gwe");
    J2R.add("ぐぉ", "gwo");
    J2R.add("つぁ", "tsa");
    J2R.add("つぃ", "tsi");
    J2R.add("つぇ", "tse");
    J2R.add("つぉ", "tso");
    J2R.add("てゃ", "tha");
    J2R.add("てぃ", "thi");
    J2R.add("てゅ", "thu");
    J2R.add("てぇ", "the");
    J2R.add("てょ", "tho");
    J2R.add("とぁ", "twa");
    J2R.add("とぃ", "twi");
    J2R.add("とぅ", "twu");
    J2R.add("とぇ", "twe");
    J2R.add("とぉ", "two");
    J2R.add("ぢゃ", "dya");
    J2R.add("ぢぃ", "dyi");
    J2R.add("ぢゅ", "dyu");
    J2R.add("ぢぇ", "dye");
    J2R.add("ぢょ", "dyo");
    J2R.add("でゃ", "dha");
    J2R.add("でぃ", "dhi");
    J2R.add("でゅ", "dhu");
    J2R.add("でぇ", "dhe");
    J2R.add("でょ", "dho");
    J2R.add("どぁ", "dwa");
    J2R.add("どぃ", "dwi");
    J2R.add("どぅ", "dwu");
    J2R.add("どぇ", "dwe");
    J2R.add("どぉ", "dwo");
    J2R.add("ふぅ", "fwu");
    J2R.add("ふゃ", "fya");
    J2R.add("ふゅ", "fyu");
    J2R.add("ふょ", "fyo");
    J2R.add("ぁ", "a");
    J2R.add("ぃ", "i");
    J2R.add("ぇ", "e");
    J2R.add("ぅ", "u");
    J2R.add("ぉ", "o");
    J2R.add("ゃ", "ya");
    J2R.add("ゅ", "yu");
    J2R.add("ょ", "yo");
    J2R.add("っ", "");
    J2R.add("ゕ", "ka");
    J2R.add("ゖ", "ka");
    J2R.add("ゎ", "wa");
    J2R.add("　", " ");
    J2R.add("んあ", "n'a");
    J2R.add("んい", "n'i");
    J2R.add("んう", "n'u");
    J2R.add("んえ", "n'e");
    J2R.add("んお", "n'o");
    J2R.add("んや", "n'ya");
    J2R.add("んゆ", "n'yu");
    J2R.add("んよ", "n'yo");
  }
  
  private static abstract interface Command
  {
    public abstract boolean run(String paramString);
  }
}

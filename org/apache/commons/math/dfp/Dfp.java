package org.apache.commons.math.dfp;

import java.util.Arrays;
import org.apache.commons.math.FieldElement;

















































































































































public class Dfp
  implements FieldElement<Dfp>
{
  public static final int RADIX = 10000;
  public static final int MIN_EXP = -32767;
  public static final int MAX_EXP = 32768;
  public static final int ERR_SCALE = 32760;
  public static final byte FINITE = 0;
  public static final byte INFINITE = 1;
  public static final byte SNAN = 2;
  public static final byte QNAN = 3;
  private static final String NAN_STRING = "NaN";
  private static final String POS_INFINITY_STRING = "Infinity";
  private static final String NEG_INFINITY_STRING = "-Infinity";
  private static final String ADD_TRAP = "add";
  private static final String MULTIPLY_TRAP = "multiply";
  private static final String DIVIDE_TRAP = "divide";
  private static final String SQRT_TRAP = "sqrt";
  private static final String ALIGN_TRAP = "align";
  private static final String TRUNC_TRAP = "trunc";
  private static final String NEXT_AFTER_TRAP = "nextAfter";
  private static final String LESS_THAN_TRAP = "lessThan";
  private static final String GREATER_THAN_TRAP = "greaterThan";
  private static final String NEW_INSTANCE_TRAP = "newInstance";
  protected int[] mant;
  protected byte sign;
  protected int exp;
  protected byte nans;
  private final DfpField field;
  
  protected Dfp(DfpField field)
  {
    mant = new int[field.getRadixDigits()];
    sign = 1;
    exp = 0;
    nans = 0;
    this.field = field;
  }
  



  protected Dfp(DfpField field, byte x)
  {
    this(field, x);
  }
  



  protected Dfp(DfpField field, int x)
  {
    this(field, x);
  }
  





  protected Dfp(DfpField field, long x)
  {
    mant = new int[field.getRadixDigits()];
    nans = 0;
    this.field = field;
    
    boolean isLongMin = false;
    if (x == Long.MIN_VALUE)
    {

      isLongMin = true;
      x += 1L;
    }
    

    if (x < 0L) {
      sign = -1;
      x = -x;
    } else {
      sign = 1;
    }
    
    exp = 0;
    while (x != 0L) {
      System.arraycopy(mant, mant.length - exp, mant, mant.length - 1 - exp, exp);
      mant[(mant.length - 1)] = ((int)(x % 10000L));
      x /= 10000L;
      exp += 1;
    }
    
    if (isLongMin)
    {

      for (int i = 0; i < mant.length - 1; i++) {
        if (mant[i] != 0) {
          mant[i] += 1;
          break;
        }
      }
    }
  }
  





  protected Dfp(DfpField field, double x)
  {
    mant = new int[field.getRadixDigits()];
    sign = 1;
    exp = 0;
    nans = 0;
    this.field = field;
    
    long bits = Double.doubleToLongBits(x);
    long mantissa = bits & 0xFFFFFFFFFFFFF;
    int exponent = (int)((bits & 0x7FF0000000000000) >> 52) - 1023;
    
    if (exponent == 64513)
    {
      if (x == 0.0D) {
        return;
      }
      
      exponent++;
      

      while ((mantissa & 0x10000000000000) == 0L) {
        exponent--;
        mantissa <<= 1;
      }
      mantissa &= 0xFFFFFFFFFFFFF;
    }
    
    if (exponent == 1024)
    {
      if (x != x) {
        sign = 1;
        nans = 3;
      } else if (x < 0.0D) {
        sign = -1;
        nans = 1;
      } else {
        sign = 1;
        nans = 1;
      }
      return;
    }
    
    Dfp xdfp = new Dfp(field, mantissa);
    xdfp = xdfp.divide(new Dfp(field, 4503599627370496L)).add(field.getOne());
    xdfp = xdfp.multiply(DfpMath.pow(field.getTwo(), exponent));
    
    if ((bits & 0x8000000000000000) != 0L) {
      xdfp = xdfp.negate();
    }
    
    System.arraycopy(mant, 0, mant, 0, mant.length);
    sign = sign;
    exp = exp;
    nans = nans;
  }
  



  public Dfp(Dfp d)
  {
    mant = ((int[])mant.clone());
    sign = sign;
    exp = exp;
    nans = nans;
    field = field;
  }
  





  protected Dfp(DfpField field, String s)
  {
    mant = new int[field.getRadixDigits()];
    sign = 1;
    exp = 0;
    nans = 0;
    this.field = field;
    
    boolean decimalFound = false;
    int rsize = 4;
    int offset = 4;
    char[] striped = new char[getRadixDigits() * 4 + 8];
    

    if (s.equals("Infinity")) {
      sign = 1;
      nans = 1;
      return;
    }
    
    if (s.equals("-Infinity")) {
      sign = -1;
      nans = 1;
      return;
    }
    
    if (s.equals("NaN")) {
      sign = 1;
      nans = 3;
      return;
    }
    

    int p = s.indexOf("e");
    if (p == -1) {
      p = s.indexOf("E");
    }
    

    int sciexp = 0;
    String fpdecimal; if (p != -1)
    {
      String fpdecimal = s.substring(0, p);
      String fpexp = s.substring(p + 1);
      boolean negative = false;
      
      for (int i = 0; i < fpexp.length(); i++)
      {
        if (fpexp.charAt(i) == '-')
        {
          negative = true;

        }
        else if ((fpexp.charAt(i) >= '0') && (fpexp.charAt(i) <= '9')) {
          sciexp = sciexp * 10 + fpexp.charAt(i) - 48;
        }
      }
      if (negative) {
        sciexp = -sciexp;
      }
    }
    else {
      fpdecimal = s;
    }
    

    if (fpdecimal.indexOf("-") != -1) {
      sign = -1;
    }
    

    p = 0;
    

    int decimalPos = 0;
    
    while ((fpdecimal.charAt(p) < '1') || (fpdecimal.charAt(p) > '9'))
    {


      if ((decimalFound) && (fpdecimal.charAt(p) == '0')) {
        decimalPos--;
      }
      
      if (fpdecimal.charAt(p) == '.') {
        decimalFound = true;
      }
      
      p++;
      
      if (p == fpdecimal.length()) {
        break;
      }
    }
    

    int q = 4;
    striped[0] = '0';
    striped[1] = '0';
    striped[2] = '0';
    striped[3] = '0';
    int significantDigits = 0;
    
    while (p != fpdecimal.length())
    {



      if (q == mant.length * 4 + 4 + 1) {
        break;
      }
      
      if (fpdecimal.charAt(p) == '.') {
        decimalFound = true;
        decimalPos = significantDigits;
        p++;


      }
      else if ((fpdecimal.charAt(p) < '0') || (fpdecimal.charAt(p) > '9')) {
        p++;
      }
      else
      {
        striped[q] = fpdecimal.charAt(p);
        q++;
        p++;
        significantDigits++;
      }
    }
    

    if ((decimalFound) && (q != 4)) {
      for (;;) {
        q--;
        if (q == 4) {
          break;
        }
        if (striped[q] != '0') break;
        significantDigits--;
      }
    }
    




    if ((decimalFound) && (significantDigits == 0)) {
      decimalPos = 0;
    }
    

    if (!decimalFound) {
      decimalPos = q - 4;
    }
    

    q = 4;
    p = significantDigits - 1 + 4;
    
    int trailingZeros = 0;
    while ((p > q) && 
      (striped[p] == '0'))
    {

      trailingZeros++;
      p--;
    }
    

    int i = (400 - decimalPos - sciexp % 4) % 4;
    q -= i;
    decimalPos += i;
    

    while (p - q < mant.length * 4) {
      for (i = 0; i < 4; i++) {
        striped[(++p)] = '0';
      }
    }
    


    for (i = mant.length - 1; i >= 0; i--) {
      mant[i] = ((striped[q] - '0') * 1000 + (striped[(q + 1)] - '0') * 100 + (striped[(q + 2)] - '0') * 10 + (striped[(q + 3)] - '0'));
      


      q += 4;
    }
    

    exp = ((decimalPos + sciexp) / 4);
    
    if (q < striped.length)
    {
      round((striped[q] - '0') * 1000);
    }
  }
  






  protected Dfp(DfpField field, byte sign, byte nans)
  {
    this.field = field;
    mant = new int[field.getRadixDigits()];
    this.sign = sign;
    exp = 0;
    this.nans = nans;
  }
  



  public Dfp newInstance()
  {
    return new Dfp(getField());
  }
  



  public Dfp newInstance(byte x)
  {
    return new Dfp(getField(), x);
  }
  



  public Dfp newInstance(int x)
  {
    return new Dfp(getField(), x);
  }
  



  public Dfp newInstance(long x)
  {
    return new Dfp(getField(), x);
  }
  



  public Dfp newInstance(double x)
  {
    return new Dfp(getField(), x);
  }
  






  public Dfp newInstance(Dfp d)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "newInstance", d, result);
    }
    
    return new Dfp(d);
  }
  





  public Dfp newInstance(String s)
  {
    return new Dfp(field, s);
  }
  





  public Dfp newInstance(byte sig, byte code)
  {
    return field.newDfp(sig, code);
  }
  






  public DfpField getField()
  {
    return field;
  }
  


  public int getRadixDigits()
  {
    return field.getRadixDigits();
  }
  


  public Dfp getZero()
  {
    return field.getZero();
  }
  


  public Dfp getOne()
  {
    return field.getOne();
  }
  


  public Dfp getTwo()
  {
    return field.getTwo();
  }
  

  protected void shiftLeft()
  {
    for (int i = mant.length - 1; i > 0; i--) {
      mant[i] = mant[(i - 1)];
    }
    mant[0] = 0;
    exp -= 1;
  }
  



  protected void shiftRight()
  {
    for (int i = 0; i < mant.length - 1; i++) {
      mant[i] = mant[(i + 1)];
    }
    mant[(mant.length - 1)] = 0;
    exp += 1;
  }
  







  protected int align(int e)
  {
    int lostdigit = 0;
    boolean inexact = false;
    
    int diff = exp - e;
    
    int adiff = diff;
    if (adiff < 0) {
      adiff = -adiff;
    }
    
    if (diff == 0) {
      return 0;
    }
    
    if (adiff > mant.length + 1)
    {
      Arrays.fill(mant, 0);
      exp = e;
      
      field.setIEEEFlagsBits(16);
      dotrap(16, "align", this, this);
      
      return 0;
    }
    
    for (int i = 0; i < adiff; i++) {
      if (diff < 0)
      {



        if (lostdigit != 0) {
          inexact = true;
        }
        
        lostdigit = mant[0];
        
        shiftRight();
      } else {
        shiftLeft();
      }
    }
    
    if (inexact) {
      field.setIEEEFlagsBits(16);
      dotrap(16, "align", this, this);
    }
    
    return lostdigit;
  }
  






  public boolean lessThan(Dfp x)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      dotrap(1, "lessThan", x, result);
      return false;
    }
    

    if ((isNaN()) || (x.isNaN())) {
      field.setIEEEFlagsBits(1);
      dotrap(1, "lessThan", x, newInstance(getZero()));
      return false;
    }
    
    return compare(this, x) < 0;
  }
  





  public boolean greaterThan(Dfp x)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      dotrap(1, "greaterThan", x, result);
      return false;
    }
    

    if ((isNaN()) || (x.isNaN())) {
      field.setIEEEFlagsBits(1);
      dotrap(1, "greaterThan", x, newInstance(getZero()));
      return false;
    }
    
    return compare(this, x) > 0;
  }
  


  public boolean isInfinite()
  {
    return nans == 1;
  }
  


  public boolean isNaN()
  {
    return (nans == 3) || (nans == 2);
  }
  





  public boolean equals(Object other)
  {
    if ((other instanceof Dfp)) {
      Dfp x = (Dfp)other;
      if ((isNaN()) || (x.isNaN()) || (field.getRadixDigits() != field.getRadixDigits())) {
        return false;
      }
      
      return compare(this, x) == 0;
    }
    
    return false;
  }
  





  public int hashCode()
  {
    return 17 + (sign << 8) + (nans << 16) + exp + Arrays.hashCode(mant);
  }
  



  public boolean unequal(Dfp x)
  {
    if ((isNaN()) || (x.isNaN()) || (field.getRadixDigits() != field.getRadixDigits())) {
      return false;
    }
    
    return (greaterThan(x)) || (lessThan(x));
  }
  






  private static int compare(Dfp a, Dfp b)
  {
    if ((mant[(mant.length - 1)] == 0) && (mant[(mant.length - 1)] == 0) && (nans == 0) && (nans == 0))
    {
      return 0;
    }
    
    if (sign != sign) {
      if (sign == -1) {
        return -1;
      }
      return 1;
    }
    


    if ((nans == 1) && (nans == 0)) {
      return sign;
    }
    
    if ((nans == 0) && (nans == 1)) {
      return -sign;
    }
    
    if ((nans == 1) && (nans == 1)) {
      return 0;
    }
    

    if ((mant[(mant.length - 1)] != 0) && (mant[(mant.length - 1)] != 0)) {
      if (exp < exp) {
        return -sign;
      }
      
      if (exp > exp) {
        return sign;
      }
    }
    

    for (int i = mant.length - 1; i >= 0; i--) {
      if (mant[i] > mant[i]) {
        return sign;
      }
      
      if (mant[i] < mant[i]) {
        return -sign;
      }
    }
    
    return 0;
  }
  





  public Dfp rint()
  {
    return trunc(DfpField.RoundingMode.ROUND_HALF_EVEN);
  }
  



  public Dfp floor()
  {
    return trunc(DfpField.RoundingMode.ROUND_FLOOR);
  }
  



  public Dfp ceil()
  {
    return trunc(DfpField.RoundingMode.ROUND_CEIL);
  }
  




  public Dfp remainder(Dfp d)
  {
    Dfp result = subtract(divide(d).rint().multiply(d));
    

    if (mant[(mant.length - 1)] == 0) {
      sign = sign;
    }
    
    return result;
  }
  




  protected Dfp trunc(DfpField.RoundingMode rmode)
  {
    boolean changed = false;
    
    if (isNaN()) {
      return newInstance(this);
    }
    
    if (nans == 1) {
      return newInstance(this);
    }
    
    if (mant[(mant.length - 1)] == 0)
    {
      return newInstance(this);
    }
    


    if (exp < 0) {
      field.setIEEEFlagsBits(16);
      Dfp result = newInstance(getZero());
      result = dotrap(16, "trunc", this, result);
      return result;
    }
    




    if (exp >= mant.length) {
      return newInstance(this);
    }
    



    Dfp result = newInstance(this);
    for (int i = 0; i < mant.length - exp; i++) {
      changed |= mant[i] != 0;
      mant[i] = 0;
    }
    
    if (changed) {
      switch (1.$SwitchMap$org$apache$commons$math$dfp$DfpField$RoundingMode[rmode.ordinal()]) {
      case 1: 
        if (sign == -1)
        {
          result = result.add(newInstance(-1));
        }
        
        break;
      case 2: 
        if (sign == 1)
        {
          result = result.add(getOne());
        }
        
        break;
      case 3: 
      default: 
        Dfp half = newInstance("0.5");
        Dfp a = subtract(result);
        sign = 1;
        if (a.greaterThan(half)) {
          a = newInstance(getOne());
          sign = sign;
          result = result.add(a);
        }
        

        if ((a.equals(half)) && (exp > 0) && ((mant[(mant.length - exp)] & 0x1) != 0)) {
          a = newInstance(getOne());
          sign = sign;
          result = result.add(a);
        }
        break;
      }
      
      field.setIEEEFlagsBits(16);
      result = dotrap(16, "trunc", this, result);
      return result;
    }
    
    return result;
  }
  




  public int intValue()
  {
    int result = 0;
    
    Dfp rounded = rint();
    
    if (rounded.greaterThan(newInstance(Integer.MAX_VALUE))) {
      return Integer.MAX_VALUE;
    }
    
    if (rounded.lessThan(newInstance(Integer.MIN_VALUE))) {
      return Integer.MIN_VALUE;
    }
    
    for (int i = mant.length - 1; i >= mant.length - exp; i--) {
      result = result * 10000 + mant[i];
    }
    
    if (sign == -1) {
      result = -result;
    }
    
    return result;
  }
  




  public int log10K()
  {
    return exp - 1;
  }
  



  public Dfp power10K(int e)
  {
    Dfp d = newInstance(getOne());
    exp = (e + 1);
    return d;
  }
  


  public int log10()
  {
    if (mant[(mant.length - 1)] > 1000) {
      return exp * 4 - 1;
    }
    if (mant[(mant.length - 1)] > 100) {
      return exp * 4 - 2;
    }
    if (mant[(mant.length - 1)] > 10) {
      return exp * 4 - 3;
    }
    return exp * 4 - 4;
  }
  



  public Dfp power10(int e)
  {
    Dfp d = newInstance(getOne());
    
    if (e >= 0) {
      exp = (e / 4 + 1);
    } else {
      exp = ((e + 1) / 4);
    }
    
    switch ((e % 4 + 4) % 4) {
    case 0: 
      break;
    case 1: 
      d = d.multiply(10);
      break;
    case 2: 
      d = d.multiply(100);
      break;
    default: 
      d = d.multiply(1000);
    }
    
    return d;
  }
  






  protected int complement(int extra)
  {
    extra = 10000 - extra;
    for (int i = 0; i < mant.length; i++) {
      mant[i] = (10000 - mant[i] - 1);
    }
    
    int rh = extra / 10000;
    extra -= rh * 10000;
    for (int i = 0; i < mant.length; i++) {
      int r = mant[i] + rh;
      rh = r / 10000;
      mant[i] = (r - rh * 10000);
    }
    
    return extra;
  }
  





  public Dfp add(Dfp x)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "add", x, result);
    }
    

    if ((nans != 0) || (nans != 0)) {
      if (isNaN()) {
        return this;
      }
      
      if (x.isNaN()) {
        return x;
      }
      
      if ((nans == 1) && (nans == 0)) {
        return this;
      }
      
      if ((nans == 1) && (nans == 0)) {
        return x;
      }
      
      if ((nans == 1) && (nans == 1) && (sign == sign)) {
        return x;
      }
      
      if ((nans == 1) && (nans == 1) && (sign != sign)) {
        field.setIEEEFlagsBits(1);
        Dfp result = newInstance(getZero());
        nans = 3;
        result = dotrap(1, "add", x, result);
        return result;
      }
    }
    

    Dfp a = newInstance(this);
    Dfp b = newInstance(x);
    

    Dfp result = newInstance(getZero());
    

    byte asign = sign;
    byte bsign = sign;
    
    sign = 1;
    sign = 1;
    

    byte rsign = bsign;
    if (compare(a, b) > 0) {
      rsign = asign;
    }
    



    if (mant[(mant.length - 1)] == 0) {
      exp = exp;
    }
    
    if (mant[(mant.length - 1)] == 0) {
      exp = exp;
    }
    

    int aextradigit = 0;
    int bextradigit = 0;
    if (exp < exp) {
      aextradigit = a.align(exp);
    } else {
      bextradigit = b.align(exp);
    }
    

    if (asign != bsign) {
      if (asign == rsign) {
        bextradigit = b.complement(bextradigit);
      } else {
        aextradigit = a.complement(aextradigit);
      }
    }
    

    int rh = 0;
    for (int i = 0; i < mant.length; i++) {
      int r = mant[i] + mant[i] + rh;
      rh = r / 10000;
      mant[i] = (r - rh * 10000);
    }
    exp = exp;
    sign = rsign;
    



    if ((rh != 0) && (asign == bsign)) {
      int lostdigit = mant[0];
      result.shiftRight();
      mant[(mant.length - 1)] = rh;
      int excp = result.round(lostdigit);
      if (excp != 0) {
        result = dotrap(excp, "add", x, result);
      }
    }
    

    for (int i = 0; i < mant.length; i++) {
      if (mant[(mant.length - 1)] != 0) {
        break;
      }
      result.shiftLeft();
      if (i == 0) {
        mant[0] = (aextradigit + bextradigit);
        aextradigit = 0;
        bextradigit = 0;
      }
    }
    

    if (mant[(mant.length - 1)] == 0) {
      exp = 0;
      
      if (asign != bsign)
      {
        sign = 1;
      }
    }
    

    int excp = result.round(aextradigit + bextradigit);
    if (excp != 0) {
      result = dotrap(excp, "add", x, result);
    }
    
    return result;
  }
  


  public Dfp negate()
  {
    Dfp result = newInstance(this);
    sign = ((byte)-sign);
    return result;
  }
  



  public Dfp subtract(Dfp x)
  {
    return add(x.negate());
  }
  



  protected int round(int n)
  {
    boolean inc = false;
    switch (1.$SwitchMap$org$apache$commons$math$dfp$DfpField$RoundingMode[field.getRoundingMode().ordinal()]) {
    case 4: 
      inc = false;
      break;
    
    case 5: 
      inc = n != 0;
      break;
    
    case 6: 
      inc = n >= 5000;
      break;
    
    case 7: 
      inc = n > 5000;
      break;
    
    case 3: 
      inc = (n > 5000) || ((n == 5000) && ((mant[0] & 0x1) == 1));
      break;
    
    case 8: 
      inc = (n > 5000) || ((n == 5000) && ((mant[0] & 0x1) == 0));
      break;
    
    case 2: 
      inc = (sign == 1) && (n != 0);
      break;
    
    case 1: 
    default: 
      inc = (sign == -1) && (n != 0);
    }
    
    
    if (inc)
    {
      int rh = 1;
      for (int i = 0; i < mant.length; i++) {
        int r = mant[i] + rh;
        rh = r / 10000;
        mant[i] = (r - rh * 10000);
      }
      
      if (rh != 0) {
        shiftRight();
        mant[(mant.length - 1)] = rh;
      }
    }
    

    if (exp < 32769)
    {
      field.setIEEEFlagsBits(8);
      return 8;
    }
    
    if (exp > 32768)
    {
      field.setIEEEFlagsBits(4);
      return 4;
    }
    
    if (n != 0)
    {
      field.setIEEEFlagsBits(16);
      return 16;
    }
    
    return 0;
  }
  






  public Dfp multiply(Dfp x)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "multiply", x, result);
    }
    
    Dfp result = newInstance(getZero());
    

    if ((nans != 0) || (nans != 0)) {
      if (isNaN()) {
        return this;
      }
      
      if (x.isNaN()) {
        return x;
      }
      
      if ((nans == 1) && (nans == 0) && (mant[(mant.length - 1)] != 0)) {
        result = newInstance(this);
        sign = ((byte)(sign * sign));
        return result;
      }
      
      if ((nans == 1) && (nans == 0) && (mant[(mant.length - 1)] != 0)) {
        result = newInstance(x);
        sign = ((byte)(sign * sign));
        return result;
      }
      
      if ((nans == 1) && (nans == 1)) {
        result = newInstance(this);
        sign = ((byte)(sign * sign));
        return result;
      }
      
      if (((nans == 1) && (nans == 0) && (mant[(mant.length - 1)] == 0)) || ((nans == 1) && (nans == 0) && (mant[(mant.length - 1)] == 0)))
      {
        field.setIEEEFlagsBits(1);
        result = newInstance(getZero());
        nans = 3;
        result = dotrap(1, "multiply", x, result);
        return result;
      }
    }
    
    int[] product = new int[mant.length * 2];
    
    for (int i = 0; i < mant.length; i++) {
      int rh = 0;
      for (int j = 0; j < mant.length; j++) {
        int r = mant[i] * mant[j];
        r = r + product[(i + j)] + rh;
        
        rh = r / 10000;
        product[(i + j)] = (r - rh * 10000);
      }
      product[(i + mant.length)] = rh;
    }
    

    int md = mant.length * 2 - 1;
    for (int i = mant.length * 2 - 1; i >= 0; i--) {
      if (product[i] != 0) {
        md = i;
        break;
      }
    }
    

    for (int i = 0; i < mant.length; i++) {
      mant[(mant.length - i - 1)] = product[(md - i)];
    }
    

    exp = (exp + exp + md - 2 * mant.length + 1);
    sign = ((byte)(sign == sign ? 1 : -1));
    
    if (mant[(mant.length - 1)] == 0)
    {
      exp = 0;
    }
    int excp;
    int excp;
    if (md > mant.length - 1) {
      excp = result.round(product[(md - mant.length)]);
    } else {
      excp = result.round(0);
    }
    
    if (excp != 0) {
      result = dotrap(excp, "multiply", x, result);
    }
    
    return result;
  }
  





  public Dfp multiply(int x)
  {
    Dfp result = newInstance(this);
    

    if (nans != 0) {
      if (isNaN()) {
        return this;
      }
      
      if ((nans == 1) && (x != 0)) {
        result = newInstance(this);
        return result;
      }
      
      if ((nans == 1) && (x == 0)) {
        field.setIEEEFlagsBits(1);
        result = newInstance(getZero());
        nans = 3;
        result = dotrap(1, "multiply", newInstance(getZero()), result);
        return result;
      }
    }
    

    if ((x < 0) || (x >= 10000)) {
      field.setIEEEFlagsBits(1);
      result = newInstance(getZero());
      nans = 3;
      result = dotrap(1, "multiply", result, result);
      return result;
    }
    
    int rh = 0;
    for (int i = 0; i < mant.length; i++) {
      int r = mant[i] * x + rh;
      rh = r / 10000;
      mant[i] = (r - rh * 10000);
    }
    
    int lostdigit = 0;
    if (rh != 0) {
      lostdigit = mant[0];
      result.shiftRight();
      mant[(mant.length - 1)] = rh;
    }
    
    if (mant[(mant.length - 1)] == 0) {
      exp = 0;
    }
    
    int excp = result.round(lostdigit);
    if (excp != 0) {
      result = dotrap(excp, "multiply", result, result);
    }
    
    return result;
  }
  








  public Dfp divide(Dfp divisor)
  {
    int trial = 0;
    

    int md = 0;
    


    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "divide", divisor, result);
    }
    
    Dfp result = newInstance(getZero());
    

    if ((nans != 0) || (nans != 0)) {
      if (isNaN()) {
        return this;
      }
      
      if (divisor.isNaN()) {
        return divisor;
      }
      
      if ((nans == 1) && (nans == 0)) {
        result = newInstance(this);
        sign = ((byte)(sign * sign));
        return result;
      }
      
      if ((nans == 1) && (nans == 0)) {
        result = newInstance(getZero());
        sign = ((byte)(sign * sign));
        return result;
      }
      
      if ((nans == 1) && (nans == 1)) {
        field.setIEEEFlagsBits(1);
        result = newInstance(getZero());
        nans = 3;
        result = dotrap(1, "divide", divisor, result);
        return result;
      }
    }
    

    if (mant[(mant.length - 1)] == 0) {
      field.setIEEEFlagsBits(2);
      result = newInstance(getZero());
      sign = ((byte)(sign * sign));
      nans = 1;
      result = dotrap(2, "divide", divisor, result);
      return result;
    }
    
    int[] dividend = new int[mant.length + 1];
    int[] quotient = new int[mant.length + 2];
    int[] remainder = new int[mant.length + 1];
    


    dividend[mant.length] = 0;
    quotient[mant.length] = 0;
    quotient[(mant.length + 1)] = 0;
    remainder[mant.length] = 0;
    



    for (int i = 0; i < mant.length; i++) {
      dividend[i] = mant[i];
      quotient[i] = 0;
      remainder[i] = 0;
    }
    

    int nsqd = 0;
    for (int qd = mant.length + 1; qd >= 0; qd--)
    {


      int divMsb = dividend[mant.length] * 10000 + dividend[(mant.length - 1)];
      int min = divMsb / (mant[(mant.length - 1)] + 1);
      int max = (divMsb + 1) / mant[(mant.length - 1)];
      
      boolean trialgood = false;
      while (!trialgood)
      {
        trial = (min + max) / 2;
        

        int rh = 0;
        for (int i = 0; i < mant.length + 1; i++) {
          int dm = i < mant.length ? mant[i] : 0;
          int r = dm * trial + rh;
          rh = r / 10000;
          remainder[i] = (r - rh * 10000);
        }
        

        rh = 1;
        for (int i = 0; i < mant.length + 1; i++) {
          int r = 9999 - remainder[i] + dividend[i] + rh;
          rh = r / 10000;
          remainder[i] = (r - rh * 10000);
        }
        

        if (rh == 0)
        {
          max = trial - 1;

        }
        else
        {
          int minadj = remainder[mant.length] * 10000 + remainder[(mant.length - 1)];
          minadj /= (mant[(mant.length - 1)] + 1);
          
          if (minadj >= 2) {
            min = trial + minadj;

          }
          else
          {

            trialgood = false;
            for (int i = mant.length - 1; i >= 0; i--) {
              if (mant[i] > remainder[i]) {
                trialgood = true;
              }
              if (mant[i] < remainder[i]) {
                break;
              }
            }
            
            if (remainder[mant.length] != 0) {
              trialgood = false;
            }
            
            if (!trialgood) {
              min = trial + 1;
            }
          }
        }
      }
      quotient[qd] = trial;
      if ((trial != 0) || (nsqd != 0)) {
        nsqd++;
      }
      
      if ((field.getRoundingMode() == DfpField.RoundingMode.ROUND_DOWN) && (nsqd == mant.length)) {
        break;
      }
      

      if (nsqd > mant.length) {
        break;
      }
      


      dividend[0] = 0;
      for (int i = 0; i < mant.length; i++) {
        dividend[(i + 1)] = remainder[i];
      }
    }
    

    md = mant.length;
    for (int i = mant.length + 1; i >= 0; i--) {
      if (quotient[i] != 0) {
        md = i;
        break;
      }
    }
    

    for (int i = 0; i < mant.length; i++) {
      mant[(mant.length - i - 1)] = quotient[(md - i)];
    }
    

    exp = (exp - exp + md - mant.length);
    sign = ((byte)(sign == sign ? 1 : -1));
    
    if (mant[(mant.length - 1)] == 0)
      exp = 0;
    int excp;
    int excp;
    if (md > mant.length - 1) {
      excp = result.round(quotient[(md - mant.length)]);
    } else {
      excp = result.round(0);
    }
    
    if (excp != 0) {
      result = dotrap(excp, "divide", divisor, result);
    }
    
    return result;
  }
  






  public Dfp divide(int divisor)
  {
    if (nans != 0) {
      if (isNaN()) {
        return this;
      }
      
      if (nans == 1) {
        return newInstance(this);
      }
    }
    

    if (divisor == 0) {
      field.setIEEEFlagsBits(2);
      Dfp result = newInstance(getZero());
      sign = sign;
      nans = 1;
      result = dotrap(2, "divide", getZero(), result);
      return result;
    }
    

    if ((divisor < 0) || (divisor >= 10000)) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      result = dotrap(1, "divide", result, result);
      return result;
    }
    
    Dfp result = newInstance(this);
    
    int rl = 0;
    for (int i = mant.length - 1; i >= 0; i--) {
      int r = rl * 10000 + mant[i];
      int rh = r / divisor;
      rl = r - rh * divisor;
      mant[i] = rh;
    }
    
    if (mant[(mant.length - 1)] == 0)
    {
      result.shiftLeft();
      int r = rl * 10000;
      int rh = r / divisor;
      rl = r - rh * divisor;
      mant[0] = rh;
    }
    
    int excp = result.round(rl * 10000 / divisor);
    if (excp != 0) {
      result = dotrap(excp, "divide", result, result);
    }
    
    return result;
  }
  





  public Dfp sqrt()
  {
    if ((nans == 0) && (mant[(mant.length - 1)] == 0))
    {
      return newInstance(this);
    }
    
    if (nans != 0) {
      if ((nans == 1) && (sign == 1))
      {
        return newInstance(this);
      }
      
      if (nans == 3) {
        return newInstance(this);
      }
      
      if (nans == 2)
      {

        field.setIEEEFlagsBits(1);
        Dfp result = newInstance(this);
        result = dotrap(1, "sqrt", null, result);
        return result;
      }
    }
    
    if (sign == -1)
    {


      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(this);
      nans = 3;
      result = dotrap(1, "sqrt", null, result);
      return result;
    }
    
    Dfp x = newInstance(this);
    

    if ((exp < -1) || (exp > 1)) {
      exp /= 2;
    }
    

    switch (mant[(mant.length - 1)] / 2000) {
    case 0: 
      mant[(mant.length - 1)] = (mant[(mant.length - 1)] / 2 + 1);
      break;
    case 2: 
      mant[(mant.length - 1)] = 1500;
      break;
    case 3: 
      mant[(mant.length - 1)] = 2200;
      break;
    case 1: default: 
      mant[(mant.length - 1)] = 3000;
    }
    
    Dfp dx = newInstance(x);
    



    Dfp px = getZero();
    Dfp ppx = getZero();
    while (x.unequal(px)) {
      dx = newInstance(x);
      sign = -1;
      dx = dx.add(divide(x));
      dx = dx.divide(2);
      ppx = px;
      px = x;
      x = x.add(dx);
      
      if (!x.equals(ppx))
      {





        if (mant[(mant.length - 1)] == 0) {
          break;
        }
      }
    }
    return x;
  }
  




  public String toString()
  {
    if (nans != 0)
    {
      if (nans == 1) {
        return sign < 0 ? "-Infinity" : "Infinity";
      }
      return "NaN";
    }
    

    if ((exp > mant.length) || (exp < -1)) {
      return dfp2sci();
    }
    
    return dfp2string();
  }
  



  protected String dfp2sci()
  {
    char[] rawdigits = new char[mant.length * 4];
    char[] outputbuffer = new char[mant.length * 4 + 20];
    






    int p = 0;
    for (int i = mant.length - 1; i >= 0; i--) {
      rawdigits[(p++)] = ((char)(mant[i] / 1000 + 48));
      rawdigits[(p++)] = ((char)(mant[i] / 100 % 10 + 48));
      rawdigits[(p++)] = ((char)(mant[i] / 10 % 10 + 48));
      rawdigits[(p++)] = ((char)(mant[i] % 10 + 48));
    }
    

    for (p = 0; p < rawdigits.length; p++) {
      if (rawdigits[p] != '0') {
        break;
      }
    }
    int shf = p;
    

    int q = 0;
    if (sign == -1) {
      outputbuffer[(q++)] = '-';
    }
    
    if (p != rawdigits.length)
    {
      outputbuffer[(q++)] = rawdigits[(p++)];
      outputbuffer[(q++)] = '.';
      
      while (p < rawdigits.length) {
        outputbuffer[(q++)] = rawdigits[(p++)];
      }
    }
    outputbuffer[(q++)] = '0';
    outputbuffer[(q++)] = '.';
    outputbuffer[(q++)] = '0';
    outputbuffer[(q++)] = 'e';
    outputbuffer[(q++)] = '0';
    return new String(outputbuffer, 0, 5);
    

    outputbuffer[(q++)] = 'e';
    


    int e = exp * 4 - shf - 1;
    int ae = e;
    if (e < 0) {
      ae = -e;
    }
    

    for (p = 1000000000; p > ae; p /= 10) {}
    


    if (e < 0) {
      outputbuffer[(q++)] = '-';
    }
    
    while (p > 0) {
      outputbuffer[(q++)] = ((char)(ae / p + 48));
      ae %= p;
      p /= 10;
    }
    
    return new String(outputbuffer, 0, q);
  }
  



  protected String dfp2string()
  {
    char[] buffer = new char[mant.length * 4 + 20];
    int p = 1;
    
    int e = exp;
    boolean pointInserted = false;
    
    buffer[0] = ' ';
    
    if (e <= 0) {
      buffer[(p++)] = '0';
      buffer[(p++)] = '.';
      pointInserted = true;
    }
    
    while (e < 0) {
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      e++;
    }
    
    for (int i = mant.length - 1; i >= 0; i--) {
      buffer[(p++)] = ((char)(mant[i] / 1000 + 48));
      buffer[(p++)] = ((char)(mant[i] / 100 % 10 + 48));
      buffer[(p++)] = ((char)(mant[i] / 10 % 10 + 48));
      buffer[(p++)] = ((char)(mant[i] % 10 + 48));
      e--; if (e == 0) {
        buffer[(p++)] = '.';
        pointInserted = true;
      }
    }
    
    while (e > 0) {
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      buffer[(p++)] = '0';
      e--;
    }
    
    if (!pointInserted)
    {
      buffer[(p++)] = '.';
    }
    

    int q = 1;
    while (buffer[q] == '0') {
      q++;
    }
    if (buffer[q] == '.') {
      q--;
    }
    

    while (buffer[(p - 1)] == '0') {
      p--;
    }
    

    if (sign < 0) {
      buffer[(--q)] = '-';
    }
    
    return new String(buffer, q, p - q);
  }
  







  public Dfp dotrap(int type, String what, Dfp oper, Dfp result)
  {
    Dfp def = result;
    
    switch (type) {
    case 1: 
      def = newInstance(getZero());
      sign = sign;
      nans = 3;
      break;
    
    case 2: 
      if ((nans == 0) && (mant[(mant.length - 1)] != 0))
      {
        def = newInstance(getZero());
        sign = ((byte)(sign * sign));
        nans = 1;
      }
      
      if ((nans == 0) && (mant[(mant.length - 1)] == 0))
      {
        def = newInstance(getZero());
        nans = 3;
      }
      
      if ((nans == 1) || (nans == 3)) {
        def = newInstance(getZero());
        nans = 3;
      }
      
      if ((nans == 1) || (nans == 2)) {
        def = newInstance(getZero());
        nans = 3;
      }
      
      break;
    case 8: 
      if (exp + mant.length < 32769) {
        def = newInstance(getZero());
        sign = sign;
      } else {
        def = newInstance(result);
      }
      exp += 32760;
      break;
    
    case 4: 
      exp -= 32760;
      def = newInstance(getZero());
      sign = sign;
      nans = 1;
      break;
    case 3: case 5: case 6: case 7: default: 
      def = result;
    }
    
    return trap(type, what, oper, def, result);
  }
  











  protected Dfp trap(int type, String what, Dfp oper, Dfp def, Dfp result)
  {
    return def;
  }
  


  public int classify()
  {
    return nans;
  }
  





  public static Dfp copysign(Dfp x, Dfp y)
  {
    Dfp result = x.newInstance(x);
    sign = sign;
    return result;
  }
  






  public Dfp nextAfter(Dfp x)
  {
    if (field.getRadixDigits() != field.getRadixDigits()) {
      field.setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "nextAfter", x, result);
    }
    

    boolean up = false;
    if (lessThan(x)) {
      up = true;
    }
    
    if (compare(this, x) == 0) {
      return newInstance(x);
    }
    
    if (lessThan(getZero())) {
      up = !up;
    }
    
    Dfp result;
    Dfp result;
    if (up) {
      Dfp inc = newInstance(getOne());
      exp = (exp - mant.length + 1);
      sign = sign;
      
      if (equals(getZero())) {
        exp = (32769 - mant.length);
      }
      
      result = add(inc);
    } else {
      Dfp inc = newInstance(getOne());
      exp = exp;
      sign = sign;
      
      if (equals(inc)) {
        exp -= mant.length;
      } else {
        exp = (exp - mant.length + 1);
      }
      
      if (equals(getZero())) {
        exp = (32769 - mant.length);
      }
      
      result = subtract(inc);
    }
    
    if ((result.classify() == 1) && (classify() != 1)) {
      field.setIEEEFlagsBits(16);
      result = dotrap(16, "nextAfter", x, result);
    }
    
    if ((result.equals(getZero())) && (!equals(getZero()))) {
      field.setIEEEFlagsBits(16);
      result = dotrap(16, "nextAfter", x, result);
    }
    
    return result;
  }
  





  public double toDouble()
  {
    if (isInfinite()) {
      if (lessThan(getZero())) {
        return Double.NEGATIVE_INFINITY;
      }
      return Double.POSITIVE_INFINITY;
    }
    

    if (isNaN()) {
      return NaN.0D;
    }
    
    Dfp y = this;
    boolean negate = false;
    if (lessThan(getZero())) {
      y = negate();
      negate = true;
    }
    


    int exponent = (int)(y.log10() * 3.32D);
    if (exponent < 0) {
      exponent--;
    }
    
    Dfp tempDfp = DfpMath.pow(getTwo(), exponent);
    while ((tempDfp.lessThan(y)) || (tempDfp.equals(y))) {
      tempDfp = tempDfp.multiply(2);
      exponent++;
    }
    exponent--;
    


    y = y.divide(DfpMath.pow(getTwo(), exponent));
    if (exponent > 64513) {
      y = y.subtract(getOne());
    }
    
    if (exponent < 64462) {
      return 0.0D;
    }
    
    if (exponent > 1023) {
      return negate ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }
    

    y = y.multiply(newInstance(4503599627370496L)).rint();
    String str = y.toString();
    str = str.substring(0, str.length() - 1);
    long mantissa = Long.parseLong(str);
    
    if (mantissa == 4503599627370496L)
    {
      mantissa = 0L;
      exponent++;
    }
    

    if (exponent <= 64513) {
      exponent--;
    }
    
    while (exponent < 64513) {
      exponent++;
      mantissa >>>= 1;
    }
    
    long bits = mantissa | exponent + 1023L << 52;
    double x = Double.longBitsToDouble(bits);
    
    if (negate) {
      x = -x;
    }
    
    return x;
  }
  




  public double[] toSplitDouble()
  {
    double[] split = new double[2];
    long mask = -1073741824L;
    
    split[0] = Double.longBitsToDouble(Double.doubleToLongBits(toDouble()) & mask);
    split[1] = subtract(newInstance(split[0])).toDouble();
    
    return split;
  }
}

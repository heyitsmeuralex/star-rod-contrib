package org.apache.commons.math.dfp;


























public class DfpDec
  extends Dfp
{
  protected DfpDec(DfpField factory)
  {
    super(factory);
  }
  



  protected DfpDec(DfpField factory, byte x)
  {
    super(factory, x);
  }
  



  protected DfpDec(DfpField factory, int x)
  {
    super(factory, x);
  }
  



  protected DfpDec(DfpField factory, long x)
  {
    super(factory, x);
  }
  



  protected DfpDec(DfpField factory, double x)
  {
    super(factory, x);
    round(0);
  }
  


  public DfpDec(Dfp d)
  {
    super(d);
    round(0);
  }
  



  protected DfpDec(DfpField factory, String s)
  {
    super(factory, s);
    round(0);
  }
  





  protected DfpDec(DfpField factory, byte sign, byte nans)
  {
    super(factory, sign, nans);
  }
  

  public Dfp newInstance()
  {
    return new DfpDec(getField());
  }
  

  public Dfp newInstance(byte x)
  {
    return new DfpDec(getField(), x);
  }
  

  public Dfp newInstance(int x)
  {
    return new DfpDec(getField(), x);
  }
  

  public Dfp newInstance(long x)
  {
    return new DfpDec(getField(), x);
  }
  

  public Dfp newInstance(double x)
  {
    return new DfpDec(getField(), x);
  }
  



  public Dfp newInstance(Dfp d)
  {
    if (getField().getRadixDigits() != d.getField().getRadixDigits()) {
      getField().setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "newInstance", d, result);
    }
    
    return new DfpDec(d);
  }
  


  public Dfp newInstance(String s)
  {
    return new DfpDec(getField(), s);
  }
  

  public Dfp newInstance(byte sign, byte nans)
  {
    return new DfpDec(getField(), sign, nans);
  }
  




  protected int getDecimalDigits()
  {
    return getRadixDigits() * 4 - 3;
  }
  


  protected int round(int in)
  {
    int msb = mant[(mant.length - 1)];
    if (msb == 0)
    {
      return 0;
    }
    
    int cmaxdigits = mant.length * 4;
    int lsbthreshold = 1000;
    while (lsbthreshold > msb) {
      lsbthreshold /= 10;
      cmaxdigits--;
    }
    

    int digits = getDecimalDigits();
    int lsbshift = cmaxdigits - digits;
    int lsd = lsbshift / 4;
    
    lsbthreshold = 1;
    for (int i = 0; i < lsbshift % 4; i++) {
      lsbthreshold *= 10;
    }
    
    int lsb = mant[lsd];
    
    if ((lsbthreshold <= 1) && (digits == 4 * mant.length - 3)) {
      return super.round(in);
    }
    
    int discarded = in;
    int n;
    if (lsbthreshold == 1)
    {
      int n = mant[(lsd - 1)] / 1000 % 10;
      mant[(lsd - 1)] %= 1000;
      discarded |= mant[(lsd - 1)];
    } else {
      n = lsb * 10 / lsbthreshold % 10;
      discarded |= lsb % (lsbthreshold / 10);
    }
    
    for (int i = 0; i < lsd; i++) {
      discarded |= mant[i];
      mant[i] = 0;
    }
    
    mant[lsd] = (lsb / lsbthreshold * lsbthreshold);
    
    boolean inc;
    switch (1.$SwitchMap$org$apache$commons$math$dfp$DfpField$RoundingMode[getField().getRoundingMode().ordinal()]) {
    case 1: 
      inc = false;
      break;
    
    case 2: 
      inc = (n != 0) || (discarded != 0);
      break;
    
    case 3: 
      inc = n >= 5;
      break;
    
    case 4: 
      inc = n > 5;
      break;
    
    case 5: 
      inc = (n > 5) || ((n == 5) && (discarded != 0)) || ((n == 5) && (discarded == 0) && ((lsb / lsbthreshold & 0x1) == 1));
      

      break;
    
    case 6: 
      inc = (n > 5) || ((n == 5) && (discarded != 0)) || ((n == 5) && (discarded == 0) && ((lsb / lsbthreshold & 0x1) == 0));
      

      break;
    
    case 7: 
      inc = (sign == 1) && ((n != 0) || (discarded != 0));
      break;
    
    case 8: 
    default: 
      inc = (sign == -1) && ((n != 0) || (discarded != 0));
    }
    
    
    if (inc)
    {
      int rh = lsbthreshold;
      for (int i = lsd; i < mant.length; i++) {
        int r = mant[i] + rh;
        rh = r / 10000;
        mant[i] = (r % 10000);
      }
      
      if (rh != 0) {
        shiftRight();
        mant[(mant.length - 1)] = rh;
      }
    }
    

    if (exp < 32769)
    {
      getField().setIEEEFlagsBits(8);
      return 8;
    }
    
    if (exp > 32768)
    {
      getField().setIEEEFlagsBits(4);
      return 4;
    }
    
    if ((n != 0) || (discarded != 0))
    {
      getField().setIEEEFlagsBits(16);
      return 16;
    }
    return 0;
  }
  


  public Dfp nextAfter(Dfp x)
  {
    String trapName = "nextAfter";
    

    if (getField().getRadixDigits() != x.getField().getRadixDigits()) {
      getField().setIEEEFlagsBits(1);
      Dfp result = newInstance(getZero());
      nans = 3;
      return dotrap(1, "nextAfter", x, result);
    }
    
    boolean up = false;
    



    if (lessThan(x)) {
      up = true;
    }
    
    if (equals(x)) {
      return newInstance(x);
    }
    
    if (lessThan(getZero()))
      up = !up;
    Dfp result;
    Dfp result;
    if (up) {
      Dfp inc = power10(log10() - getDecimalDigits() + 1);
      inc = copysign(inc, this);
      
      if (equals(getZero())) {
        inc = power10K(32769 - mant.length - 1);
      }
      Dfp result;
      if (inc.equals(getZero())) {
        result = copysign(newInstance(getZero()), this);
      } else {
        result = add(inc);
      }
    } else {
      Dfp inc = power10(log10());
      inc = copysign(inc, this);
      
      if (equals(inc)) {
        inc = inc.divide(power10(getDecimalDigits()));
      } else {
        inc = inc.divide(power10(getDecimalDigits() - 1));
      }
      
      if (equals(getZero())) {
        inc = power10K(32769 - mant.length - 1);
      }
      Dfp result;
      if (inc.equals(getZero())) {
        result = copysign(newInstance(getZero()), this);
      } else {
        result = subtract(inc);
      }
    }
    
    if ((result.classify() == 1) && (classify() != 1)) {
      getField().setIEEEFlagsBits(16);
      result = dotrap(16, "nextAfter", x, result);
    }
    
    if ((result.equals(getZero())) && (!equals(getZero()))) {
      getField().setIEEEFlagsBits(16);
      result = dotrap(16, "nextAfter", x, result);
    }
    
    return result;
  }
}

package org.apache.commons.math.dfp;













public class DfpMath
{
  private static final String POW_TRAP = "pow";
  












  private DfpMath() {}
  











  protected static Dfp[] split(DfpField field, String a)
  {
    Dfp[] result = new Dfp[2];
    
    boolean leading = true;
    int sp = 0;
    int sig = 0;
    
    char[] buf = new char[a.length()];
    
    for (int i = 0; i < buf.length; i++) {
      buf[i] = a.charAt(i);
      
      if ((buf[i] >= '1') && (buf[i] <= '9')) {
        leading = false;
      }
      
      if (buf[i] == '.') {
        sig += (400 - sig) % 4;
        leading = false;
      }
      
      if (sig == field.getRadixDigits() / 2 * 4) {
        sp = i;
        break;
      }
      
      if ((buf[i] >= '0') && (buf[i] <= '9') && (!leading)) {
        sig++;
      }
    }
    
    result[0] = field.newDfp(new String(buf, 0, sp));
    
    for (int i = 0; i < buf.length; i++) {
      buf[i] = a.charAt(i);
      if ((buf[i] >= '0') && (buf[i] <= '9') && (i < sp)) {
        buf[i] = '0';
      }
    }
    
    result[1] = field.newDfp(new String(buf));
    
    return result;
  }
  



  protected static Dfp[] split(Dfp a)
  {
    Dfp[] result = new Dfp[2];
    Dfp shift = a.multiply(a.power10K(a.getRadixDigits() / 2));
    result[0] = a.add(shift).subtract(shift);
    result[1] = a.subtract(result[0]);
    return result;
  }
  







  protected static Dfp[] splitMult(Dfp[] a, Dfp[] b)
  {
    Dfp[] result = new Dfp[2];
    
    result[1] = a[0].getZero();
    result[0] = a[0].multiply(b[0]);
    




    if ((result[0].classify() == 1) || (result[0].equals(result[1]))) {
      return result;
    }
    
    result[1] = a[0].multiply(b[1]).add(a[1].multiply(b[0])).add(a[1].multiply(b[1]));
    
    return result;
  }
  








  protected static Dfp[] splitDiv(Dfp[] a, Dfp[] b)
  {
    Dfp[] result = new Dfp[2];
    
    result[0] = a[0].divide(b[0]);
    result[1] = a[1].multiply(b[0]).subtract(a[0].multiply(b[1]));
    result[1] = result[1].divide(b[0].multiply(b[0]).add(b[0].multiply(b[1])));
    
    return result;
  }
  




  protected static Dfp splitPow(Dfp[] base, int a)
  {
    boolean invert = false;
    
    Dfp[] r = new Dfp[2];
    
    Dfp[] result = new Dfp[2];
    result[0] = base[0].getOne();
    result[1] = base[0].getZero();
    
    if (a == 0)
    {
      return result[0].add(result[1]);
    }
    
    if (a < 0)
    {
      invert = true;
      a = -a;
    }
    
    do
    {
      r[0] = new Dfp(base[0]);
      r[1] = new Dfp(base[1]);
      int trial = 1;
      int prevtrial;
      for (;;)
      {
        prevtrial = trial;
        trial *= 2;
        if (trial > a) {
          break;
        }
        r = splitMult(r, r);
      }
      
      trial = prevtrial;
      
      a -= trial;
      result = splitMult(result, r);
    }
    while (a >= 1);
    
    result[0] = result[0].add(result[1]);
    
    if (invert) {
      result[0] = base[0].getOne().divide(result[0]);
    }
    
    return result[0];
  }
  






  public static Dfp pow(Dfp base, int a)
  {
    boolean invert = false;
    
    Dfp result = base.getOne();
    
    if (a == 0)
    {
      return result;
    }
    
    if (a < 0) {
      invert = true;
      a = -a;
    }
    
    do
    {
      Dfp r = new Dfp(base);
      
      int trial = 1;
      Dfp prevr;
      int prevtrial;
      do {
        prevr = new Dfp(r);
        prevtrial = trial;
        r = r.multiply(r);
        trial *= 2;
      } while (a > trial);
      
      r = prevr;
      trial = prevtrial;
      
      a -= trial;
      result = result.multiply(r);
    }
    while (a >= 1);
    
    if (invert) {
      result = base.getOne().divide(result);
    }
    
    return base.newInstance(result);
  }
  








  public static Dfp exp(Dfp a)
  {
    Dfp inta = a.rint();
    Dfp fraca = a.subtract(inta);
    
    int ia = inta.intValue();
    if (ia > 2147483646)
    {
      return a.newInstance((byte)1, (byte)1);
    }
    
    if (ia < -2147483646)
    {
      return a.newInstance();
    }
    
    Dfp einta = splitPow(a.getField().getESplit(), ia);
    Dfp efraca = expInternal(fraca);
    
    return einta.multiply(efraca);
  }
  




  protected static Dfp expInternal(Dfp a)
  {
    Dfp y = a.getOne();
    Dfp x = a.getOne();
    Dfp fact = a.getOne();
    Dfp py = new Dfp(y);
    
    for (int i = 1; i < 90; i++) {
      x = x.multiply(a);
      fact = fact.divide(i);
      y = y.add(x.multiply(fact));
      if (y.equals(py)) {
        break;
      }
      py = new Dfp(y);
    }
    
    return y;
  }
  









  public static Dfp log(Dfp a)
  {
    int p2 = 0;
    

    if ((a.equals(a.getZero())) || (a.lessThan(a.getZero())) || (a.isNaN()))
    {
      a.getField().setIEEEFlagsBits(1);
      return a.dotrap(1, "ln", a, a.newInstance((byte)1, (byte)3));
    }
    
    if (a.classify() == 1) {
      return a;
    }
    
    Dfp x = new Dfp(a);
    int lr = x.log10K();
    
    x = x.divide(pow(a.newInstance(10000), lr));
    int ix = x.floor().intValue();
    
    while (ix > 2) {
      ix >>= 1;
      p2++;
    }
    

    Dfp[] spx = split(x);
    Dfp[] spy = new Dfp[2];
    spy[0] = pow(a.getTwo(), p2);
    spx[0] = spx[0].divide(spy[0]);
    spx[1] = spx[1].divide(spy[0]);
    
    spy[0] = a.newInstance("1.33333");
    while (spx[0].add(spx[1]).greaterThan(spy[0])) {
      spx[0] = spx[0].divide(2);
      spx[1] = spx[1].divide(2);
      p2++;
    }
    

    Dfp[] spz = logInternal(spx);
    
    spx[0] = a.newInstance(p2 + 4 * lr);
    spx[1] = a.getZero();
    spy = splitMult(a.getField().getLn2Split(), spx);
    
    spz[0] = spz[0].add(spy[0]);
    spz[1] = spz[1].add(spy[1]);
    
    spx[0] = a.newInstance(4 * lr);
    spx[1] = a.getZero();
    spy = splitMult(a.getField().getLn5Split(), spx);
    
    spz[0] = spz[0].add(spy[0]);
    spz[1] = spz[1].add(spy[1]);
    
    return a.newInstance(spz[0].add(spz[1]));
  }
  



























































  protected static Dfp[] logInternal(Dfp[] a)
  {
    Dfp t = a[0].divide(4).add(a[1].divide(4));
    Dfp x = t.add(a[0].newInstance("-0.25")).divide(t.add(a[0].newInstance("0.25")));
    
    Dfp y = new Dfp(x);
    Dfp num = new Dfp(x);
    Dfp py = new Dfp(y);
    int den = 1;
    for (int i = 0; i < 10000; i++) {
      num = num.multiply(x);
      num = num.multiply(x);
      den += 2;
      t = num.divide(den);
      y = y.add(t);
      if (y.equals(py)) {
        break;
      }
      py = new Dfp(y);
    }
    
    y = y.multiply(a[0].getTwo());
    
    return split(y);
  }
  










































  public static Dfp pow(Dfp x, Dfp y)
  {
    if (x.getField().getRadixDigits() != y.getField().getRadixDigits()) {
      x.getField().setIEEEFlagsBits(1);
      Dfp result = x.newInstance(x.getZero());
      nans = 3;
      return x.dotrap(1, "pow", x, result);
    }
    
    Dfp zero = x.getZero();
    Dfp one = x.getOne();
    Dfp two = x.getTwo();
    boolean invert = false;
    


    if (y.equals(zero)) {
      return x.newInstance(one);
    }
    
    if (y.equals(one)) {
      if (x.isNaN())
      {
        x.getField().setIEEEFlagsBits(1);
        return x.dotrap(1, "pow", x, x);
      }
      return x;
    }
    
    if ((x.isNaN()) || (y.isNaN()))
    {
      x.getField().setIEEEFlagsBits(1);
      return x.dotrap(1, "pow", x, x.newInstance((byte)1, (byte)3));
    }
    

    if (x.equals(zero)) {
      if (Dfp.copysign(one, x).greaterThan(zero))
      {
        if (y.greaterThan(zero)) {
          return x.newInstance(zero);
        }
        return x.newInstance(x.newInstance((byte)1, (byte)1));
      }
      

      if ((y.classify() == 0) && (y.rint().equals(y)) && (!y.remainder(two).equals(zero)))
      {
        if (y.greaterThan(zero)) {
          return x.newInstance(zero.negate());
        }
        return x.newInstance(x.newInstance((byte)-1, (byte)1));
      }
      

      if (y.greaterThan(zero)) {
        return x.newInstance(zero);
      }
      return x.newInstance(x.newInstance((byte)1, (byte)1));
    }
    



    if (x.lessThan(zero))
    {
      x = x.negate();
      invert = true;
    }
    
    if ((x.greaterThan(one)) && (y.classify() == 1)) {
      if (y.greaterThan(zero)) {
        return y;
      }
      return x.newInstance(zero);
    }
    

    if ((x.lessThan(one)) && (y.classify() == 1)) {
      if (y.greaterThan(zero)) {
        return x.newInstance(zero);
      }
      return x.newInstance(Dfp.copysign(y, one));
    }
    

    if ((x.equals(one)) && (y.classify() == 1)) {
      x.getField().setIEEEFlagsBits(1);
      return x.dotrap(1, "pow", x, x.newInstance((byte)1, (byte)3));
    }
    
    if (x.classify() == 1)
    {
      if (invert)
      {
        if ((y.classify() == 0) && (y.rint().equals(y)) && (!y.remainder(two).equals(zero)))
        {
          if (y.greaterThan(zero)) {
            return x.newInstance(x.newInstance((byte)-1, (byte)1));
          }
          return x.newInstance(zero.negate());
        }
        

        if (y.greaterThan(zero)) {
          return x.newInstance(x.newInstance((byte)1, (byte)1));
        }
        return x.newInstance(zero);
      }
      


      if (y.greaterThan(zero)) {
        return x;
      }
      return x.newInstance(zero);
    }
    


    if ((invert) && (!y.rint().equals(y))) {
      x.getField().setIEEEFlagsBits(1);
      return x.dotrap(1, "pow", x, x.newInstance((byte)1, (byte)3));
    }
    
    Dfp r;
    
    Dfp r;
    if ((y.lessThan(x.newInstance(100000000))) && (y.greaterThan(x.newInstance(-100000000)))) {
      Dfp u = y.rint();
      int ui = u.intValue();
      
      Dfp v = y.subtract(u);
      
      if (v.unequal(zero)) {
        Dfp a = v.multiply(log(x));
        Dfp b = a.divide(x.getField().getLn2()).rint();
        
        Dfp c = a.subtract(b.multiply(x.getField().getLn2()));
        Dfp r = splitPow(split(x), ui);
        r = r.multiply(pow(two, b.intValue()));
        r = r.multiply(exp(c));
      } else {
        r = splitPow(split(x), ui);
      }
    }
    else {
      r = exp(log(x).multiply(y));
    }
    
    if (invert)
    {
      if ((y.rint().equals(y)) && (!y.remainder(two).equals(zero))) {
        r = r.negate();
      }
    }
    
    return x.newInstance(r);
  }
  






  protected static Dfp sinInternal(Dfp[] a)
  {
    Dfp c = a[0].add(a[1]);
    Dfp y = c;
    c = c.multiply(c);
    Dfp x = y;
    Dfp fact = a[0].getOne();
    Dfp py = new Dfp(y);
    
    for (int i = 3; i < 90; i += 2) {
      x = x.multiply(c);
      x = x.negate();
      
      fact = fact.divide((i - 1) * i);
      y = y.add(x.multiply(fact));
      if (y.equals(py))
        break;
      py = new Dfp(y);
    }
    
    return y;
  }
  





  protected static Dfp cosInternal(Dfp[] a)
  {
    Dfp one = a[0].getOne();
    

    Dfp x = one;
    Dfp y = one;
    Dfp c = a[0].add(a[1]);
    c = c.multiply(c);
    
    Dfp fact = one;
    Dfp py = new Dfp(y);
    
    for (int i = 2; i < 90; i += 2) {
      x = x.multiply(c);
      x = x.negate();
      
      fact = fact.divide((i - 1) * i);
      
      y = y.add(x.multiply(fact));
      if (y.equals(py)) {
        break;
      }
      py = new Dfp(y);
    }
    
    return y;
  }
  




  public static Dfp sin(Dfp a)
  {
    Dfp pi = a.getField().getPi();
    Dfp zero = a.getField().getZero();
    boolean neg = false;
    

    Dfp x = a.remainder(pi.multiply(2));
    


    if (x.lessThan(zero)) {
      x = x.negate();
      neg = true;
    }
    




    if (x.greaterThan(pi.divide(2))) {
      x = pi.subtract(x);
    }
    Dfp y;
    Dfp y;
    if (x.lessThan(pi.divide(4))) {
      Dfp[] c = new Dfp[2];
      c[0] = x;
      c[1] = zero;
      

      y = sinInternal(split(x));
    } else {
      Dfp[] c = new Dfp[2];
      Dfp[] piSplit = a.getField().getPiSplit();
      c[0] = piSplit[0].divide(2).subtract(x);
      c[1] = piSplit[1].divide(2);
      y = cosInternal(c);
    }
    
    if (neg) {
      y = y.negate();
    }
    
    return a.newInstance(y);
  }
  




  public static Dfp cos(Dfp a)
  {
    Dfp pi = a.getField().getPi();
    Dfp zero = a.getField().getZero();
    boolean neg = false;
    

    Dfp x = a.remainder(pi.multiply(2));
    


    if (x.lessThan(zero)) {
      x = x.negate();
    }
    




    if (x.greaterThan(pi.divide(2))) {
      x = pi.subtract(x);
      neg = true;
    }
    Dfp y;
    Dfp y;
    if (x.lessThan(pi.divide(4))) {
      Dfp[] c = new Dfp[2];
      c[0] = x;
      c[1] = zero;
      
      y = cosInternal(c);
    } else {
      Dfp[] c = new Dfp[2];
      Dfp[] piSplit = a.getField().getPiSplit();
      c[0] = piSplit[0].divide(2).subtract(x);
      c[1] = piSplit[1].divide(2);
      y = sinInternal(c);
    }
    
    if (neg) {
      y = y.negate();
    }
    
    return a.newInstance(y);
  }
  




  public static Dfp tan(Dfp a)
  {
    return sin(a).divide(cos(a));
  }
  




  protected static Dfp atanInternal(Dfp a)
  {
    Dfp y = new Dfp(a);
    Dfp x = new Dfp(y);
    Dfp py = new Dfp(y);
    
    for (int i = 3; i < 90; i += 2) {
      x = x.multiply(a);
      x = x.multiply(a);
      x = x.negate();
      y = y.add(x.divide(i));
      if (y.equals(py)) {
        break;
      }
      py = new Dfp(y);
    }
    
    return y;
  }
  













  public static Dfp atan(Dfp a)
  {
    Dfp zero = a.getField().getZero();
    Dfp one = a.getField().getOne();
    Dfp[] sqr2Split = a.getField().getSqr2Split();
    Dfp[] piSplit = a.getField().getPiSplit();
    boolean recp = false;
    boolean neg = false;
    boolean sub = false;
    
    Dfp ty = sqr2Split[0].subtract(one).add(sqr2Split[1]);
    
    Dfp x = new Dfp(a);
    if (x.lessThan(zero)) {
      neg = true;
      x = x.negate();
    }
    
    if (x.greaterThan(one)) {
      recp = true;
      x = one.divide(x);
    }
    
    if (x.greaterThan(ty)) {
      Dfp[] sty = new Dfp[2];
      sub = true;
      
      sty[0] = sqr2Split[0].subtract(one);
      sty[1] = sqr2Split[1];
      
      Dfp[] xs = split(x);
      
      Dfp[] ds = splitMult(xs, sty);
      ds[0] = ds[0].add(one);
      
      xs[0] = xs[0].subtract(sty[0]);
      xs[1] = xs[1].subtract(sty[1]);
      
      xs = splitDiv(xs, ds);
      x = xs[0].add(xs[1]);
    }
    


    Dfp y = atanInternal(x);
    
    if (sub) {
      y = y.add(piSplit[0].divide(8)).add(piSplit[1].divide(8));
    }
    
    if (recp) {
      y = piSplit[0].divide(2).subtract(y).add(piSplit[1].divide(2));
    }
    
    if (neg) {
      y = y.negate();
    }
    
    return a.newInstance(y);
  }
  




  public static Dfp asin(Dfp a)
  {
    return atan(a.divide(a.getOne().subtract(a.multiply(a)).sqrt()));
  }
  




  public static Dfp acos(Dfp a)
  {
    boolean negative = false;
    
    if (a.lessThan(a.getZero())) {
      negative = true;
    }
    
    a = Dfp.copysign(a, a.getOne());
    
    Dfp result = atan(a.getOne().subtract(a.multiply(a)).sqrt().divide(a));
    
    if (negative) {
      result = a.getField().getPi().subtract(result);
    }
    
    return a.newInstance(result);
  }
}

package org.apache.commons.math.util;

















public class FastMath
{
  public static final double PI = 3.141592653589793D;
  















  public static final double E = 2.718281828459045D;
  















  private static final double[] EXP_INT_TABLE_A = new double['ל'];
  



  private static final double[] EXP_INT_TABLE_B = new double['ל'];
  



  private static final double[] EXP_FRAC_TABLE_A = new double['Ё'];
  



  private static final double[] EXP_FRAC_TABLE_B = new double['Ё'];
  

  private static final double[] FACT = new double[20];
  

  private static final double[][] LN_MANT = new double['Ѐ'][];
  

  private static final double LN_2_A = 0.6931470632553101D;
  

  private static final double LN_2_B = 1.1730463525082348E-7D;
  

  private static final double[][] LN_SPLIT_COEF = { { 2.0D, 0.0D }, { 0.6666666269302368D, 3.9736429850260626E-8D }, { 0.3999999761581421D, 2.3841857910019882E-8D }, { 0.2857142686843872D, 1.7029898543501842E-8D }, { 0.2222222089767456D, 1.3245471311735498E-8D }, { 0.1818181574344635D, 2.4384203044354907E-8D }, { 0.1538461446762085D, 9.140260083262505E-9D }, { 0.13333332538604736D, 9.220590270857665E-9D }, { 0.11764700710773468D, 1.2393345855018391E-8D }, { 0.10526403784751892D, 8.251545029714408E-9D }, { 0.0952233225107193D, 1.2675934823758863E-8D }, { 0.08713622391223907D, 1.1430250008909141E-8D }, { 0.07842259109020233D, 2.404307984052299E-9D }, { 0.08371849358081818D, 1.176342548272881E-8D }, { 0.030589580535888672D, 1.2958646899018938E-9D }, { 0.14982303977012634D, 1.225743062930824E-8D } };
  


















  private static final double[][] LN_QUICK_COEF = { { 1.0D, 5.669184079525E-24D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.986821492305628E-8D }, { -0.25D, -6.663542893624021E-14D }, { 0.19999998807907104D, 1.1921056801463227E-8D }, { -0.1666666567325592D, -7.800414592973399E-9D }, { 0.1428571343421936D, 5.650007086920087E-9D }, { -0.12502530217170715D, -7.44321345601866E-11D }, { 0.11113807559013367D, 9.219544613762692E-9D } };
  











  private static final double[][] LN_HI_PREC_COEF = { { 1.0D, -6.032174644509064E-23D }, { -0.25D, -0.25D }, { 0.3333333134651184D, 1.9868161777724352E-8D }, { -0.2499999701976776D, -2.957007209750105E-8D }, { 0.19999954104423523D, 1.5830993332061267E-10D }, { -0.16624879837036133D, -2.6033824355191673E-8D } };
  








  private static final double[] SINE_TABLE_A = new double[14];
  

  private static final double[] SINE_TABLE_B = new double[14];
  

  private static final double[] COSINE_TABLE_A = new double[14];
  

  private static final double[] COSINE_TABLE_B = new double[14];
  

  private static final double[] TANGENT_TABLE_A = new double[14];
  

  private static final double[] TANGENT_TABLE_B = new double[14];
  

  private static final long[] RECIP_2PI = { 2935890503282001226L, 9154082963658192752L, 3952090531849364496L, 9193070505571053912L, 7910884519577875640L, 113236205062349959L, 4577762542105553359L, -5034868814120038111L, 4208363204685324176L, 5648769086999809661L, 2819561105158720014L, -4035746434778044925L, -302932621132653753L, -2644281811660520851L, -3183605296591799669L, 6722166367014452318L, -3512299194304650054L, -7278142539171889152L };
  



















  private static final long[] PI_O_4_BITS = { -3958705157555305932L, -4267615245585081135L };
  






  private static final double[] EIGHTHS = { 0.0D, 0.125D, 0.25D, 0.375D, 0.5D, 0.625D, 0.75D, 0.875D, 1.0D, 1.125D, 1.25D, 1.375D, 1.5D, 1.625D };
  

  private static final double[] CBRTTWO = { 0.6299605249474366D, 0.7937005259840998D, 1.0D, 1.2599210498948732D, 1.5874010519681994D };
  





  private static final long HEX_40000000 = 1073741824L;
  





  private static final long MASK_30BITS = -1073741824L;
  





  private static final double TWO_POWER_52 = 4.503599627370496E15D;
  





  static
  {
    FACT[0] = 1.0D;
    for (int i = 1; i < FACT.length; i++) {
      FACT[i] = (FACT[(i - 1)] * i);
    }
    
    double[] tmp = new double[2];
    double[] recip = new double[2];
    

    for (i = 0; i < 750; i++) {
      expint(i, tmp);
      EXP_INT_TABLE_A[(i + 750)] = tmp[0];
      EXP_INT_TABLE_B[(i + 750)] = tmp[1];
      
      if (i != 0)
      {
        splitReciprocal(tmp, recip);
        EXP_INT_TABLE_A[(750 - i)] = recip[0];
        EXP_INT_TABLE_B[(750 - i)] = recip[1];
      }
    }
    

    for (i = 0; i < EXP_FRAC_TABLE_A.length; i++) {
      slowexp(i / 1024.0D, tmp);
      EXP_FRAC_TABLE_A[i] = tmp[0];
      EXP_FRAC_TABLE_B[i] = tmp[1];
    }
    

    for (i = 0; i < LN_MANT.length; i++) {
      double d = Double.longBitsToDouble(i << 42 | 0x3FF0000000000000);
      LN_MANT[i] = slowLog(d);
    }
    

    buildSinCosTables();
  }
  














  private static double doubleHighPart(double d)
  {
    if ((d > -2.2250738585072014E-308D) && (d < 2.2250738585072014E-308D)) {
      return d;
    }
    long xl = Double.doubleToLongBits(d);
    xl &= 0xFFFFFFFFC0000000;
    return Double.longBitsToDouble(xl);
  }
  




  public static double sqrt(double a)
  {
    return Math.sqrt(a);
  }
  



  public static double cosh(double x)
  {
    if (x != x) {
      return x;
    }
    
    if (x > 20.0D) {
      return exp(x) / 2.0D;
    }
    
    if (x < -20.0D) {
      return exp(-x) / 2.0D;
    }
    
    double[] hiPrec = new double[2];
    if (x < 0.0D) {
      x = -x;
    }
    exp(x, 0.0D, hiPrec);
    
    double ya = hiPrec[0] + hiPrec[1];
    double yb = -(ya - hiPrec[0] - hiPrec[1]);
    
    double temp = ya * 1.073741824E9D;
    double yaa = ya + temp - temp;
    double yab = ya - yaa;
    

    double recip = 1.0D / ya;
    temp = recip * 1.073741824E9D;
    double recipa = recip + temp - temp;
    double recipb = recip - recipa;
    

    recipb += (1.0D - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
    
    recipb += -yb * recip * recip;
    

    temp = ya + recipa;
    yb += -(temp - ya - recipa);
    ya = temp;
    temp = ya + recipb;
    yb += -(temp - ya - recipb);
    ya = temp;
    
    double result = ya + yb;
    result *= 0.5D;
    return result;
  }
  



  public static double sinh(double x)
  {
    boolean negate = false;
    if (x != x) {
      return x;
    }
    
    if (x > 20.0D) {
      return exp(x) / 2.0D;
    }
    
    if (x < -20.0D) {
      return -exp(-x) / 2.0D;
    }
    
    if (x == 0.0D) {
      return x;
    }
    
    if (x < 0.0D) {
      x = -x;
      negate = true;
    }
    
    double result;
    
    if (x > 0.25D) {
      double[] hiPrec = new double[2];
      exp(x, 0.0D, hiPrec);
      
      double ya = hiPrec[0] + hiPrec[1];
      double yb = -(ya - hiPrec[0] - hiPrec[1]);
      
      double temp = ya * 1.073741824E9D;
      double yaa = ya + temp - temp;
      double yab = ya - yaa;
      

      double recip = 1.0D / ya;
      temp = recip * 1.073741824E9D;
      double recipa = recip + temp - temp;
      double recipb = recip - recipa;
      

      recipb += (1.0D - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
      
      recipb += -yb * recip * recip;
      
      recipa = -recipa;
      recipb = -recipb;
      

      temp = ya + recipa;
      yb += -(temp - ya - recipa);
      ya = temp;
      temp = ya + recipb;
      yb += -(temp - ya - recipb);
      ya = temp;
      
      double result = ya + yb;
      result *= 0.5D;
    }
    else {
      double[] hiPrec = new double[2];
      expm1(x, hiPrec);
      
      double ya = hiPrec[0] + hiPrec[1];
      double yb = -(ya - hiPrec[0] - hiPrec[1]);
      

      double denom = 1.0D + ya;
      double denomr = 1.0D / denom;
      double denomb = -(denom - 1.0D - ya) + yb;
      double ratio = ya * denomr;
      double temp = ratio * 1.073741824E9D;
      double ra = ratio + temp - temp;
      double rb = ratio - ra;
      
      temp = denom * 1.073741824E9D;
      double za = denom + temp - temp;
      double zb = denom - za;
      
      rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
      

      rb += yb * denomr;
      rb += -ya * denomb * denomr * denomr;
      

      temp = ya + ra;
      yb += -(temp - ya - ra);
      ya = temp;
      temp = ya + rb;
      yb += -(temp - ya - rb);
      ya = temp;
      
      result = ya + yb;
      result *= 0.5D;
    }
    
    if (negate) {
      result = -result;
    }
    
    return result;
  }
  



  public static double tanh(double x)
  {
    boolean negate = false;
    
    if (x != x) {
      return x;
    }
    
    if (x > 20.0D) {
      return 1.0D;
    }
    
    if (x < -20.0D) {
      return -1.0D;
    }
    
    if (x == 0.0D) {
      return x;
    }
    
    if (x < 0.0D) {
      x = -x;
      negate = true;
    }
    double result;
    double result;
    if (x >= 0.5D) {
      double[] hiPrec = new double[2];
      
      exp(x * 2.0D, 0.0D, hiPrec);
      
      double ya = hiPrec[0] + hiPrec[1];
      double yb = -(ya - hiPrec[0] - hiPrec[1]);
      

      double na = -1.0D + ya;
      double nb = -(na + 1.0D - ya);
      double temp = na + yb;
      nb += -(temp - na - yb);
      na = temp;
      

      double da = 1.0D + ya;
      double db = -(da - 1.0D - ya);
      temp = da + yb;
      db += -(temp - da - yb);
      da = temp;
      
      temp = da * 1.073741824E9D;
      double daa = da + temp - temp;
      double dab = da - daa;
      

      double ratio = na / da;
      temp = ratio * 1.073741824E9D;
      double ratioa = ratio + temp - temp;
      double ratiob = ratio - ratioa;
      

      ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da;
      

      ratiob += nb / da;
      
      ratiob += -db * na / da / da;
      
      result = ratioa + ratiob;
    }
    else {
      double[] hiPrec = new double[2];
      
      expm1(x * 2.0D, hiPrec);
      
      double ya = hiPrec[0] + hiPrec[1];
      double yb = -(ya - hiPrec[0] - hiPrec[1]);
      

      double na = ya;
      double nb = yb;
      

      double da = 2.0D + ya;
      double db = -(da - 2.0D - ya);
      double temp = da + yb;
      db += -(temp - da - yb);
      da = temp;
      
      temp = da * 1.073741824E9D;
      double daa = da + temp - temp;
      double dab = da - daa;
      

      double ratio = na / da;
      temp = ratio * 1.073741824E9D;
      double ratioa = ratio + temp - temp;
      double ratiob = ratio - ratioa;
      

      ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da;
      

      ratiob += nb / da;
      
      ratiob += -db * na / da / da;
      
      result = ratioa + ratiob;
    }
    
    if (negate) {
      result = -result;
    }
    
    return result;
  }
  



  public static double acosh(double a)
  {
    return log(a + sqrt(a * a - 1.0D));
  }
  




  public static double asinh(double a)
  {
    boolean negative = false;
    if (a < 0.0D) {
      negative = true;
      a = -a;
    }
    double absAsinh;
    double absAsinh;
    if (a > 0.167D) {
      absAsinh = log(sqrt(a * a + 1.0D) + a);
    } else {
      double a2 = a * a;
      double absAsinh; if (a > 0.097D) {
        absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * (0.1111111111111111D - a2 * (0.09090909090909091D - a2 * (0.07692307692307693D - a2 * (0.06666666666666667D - a2 * 0.058823529411764705D * 15.0D / 16.0D) * 13.0D / 14.0D) * 11.0D / 12.0D) * 9.0D / 10.0D) * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D); } else { double absAsinh;
        if (a > 0.036D) {
          absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * (0.1111111111111111D - a2 * (0.09090909090909091D - a2 * 0.07692307692307693D * 11.0D / 12.0D) * 9.0D / 10.0D) * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D); } else { double absAsinh;
          if (a > 0.0036D) {
            absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * (0.2D - a2 * (0.14285714285714285D - a2 * 0.1111111111111111D * 7.0D / 8.0D) * 5.0D / 6.0D) * 3.0D / 4.0D) / 2.0D);
          } else
            absAsinh = a * (1.0D - a2 * (0.3333333333333333D - a2 * 0.2D * 3.0D / 4.0D) / 2.0D);
        }
      }
    }
    return negative ? -absAsinh : absAsinh;
  }
  





  public static double atanh(double a)
  {
    boolean negative = false;
    if (a < 0.0D) {
      negative = true;
      a = -a;
    }
    double absAtanh;
    double absAtanh;
    if (a > 0.15D) {
      absAtanh = 0.5D * log((1.0D + a) / (1.0D - a));
    } else {
      double a2 = a * a;
      double absAtanh; if (a > 0.087D) {
        absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * (0.1111111111111111D + a2 * (0.09090909090909091D + a2 * (0.07692307692307693D + a2 * (0.06666666666666667D + a2 * 0.058823529411764705D)))))))); } else { double absAtanh;
        if (a > 0.031D) {
          absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * (0.1111111111111111D + a2 * (0.09090909090909091D + a2 * 0.07692307692307693D)))))); } else { double absAtanh;
          if (a > 0.003D) {
            absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * (0.2D + a2 * (0.14285714285714285D + a2 * 0.1111111111111111D))));
          } else
            absAtanh = a * (1.0D + a2 * (0.3333333333333333D + a2 * 0.2D));
        }
      }
    }
    return negative ? -absAtanh : absAtanh;
  }
  





  public static double signum(double a)
  {
    return a > 0.0D ? 1.0D : a < 0.0D ? -1.0D : a;
  }
  




  public static float signum(float a)
  {
    return a > 0.0F ? 1.0F : a < 0.0F ? -1.0F : a;
  }
  



  public static double nextUp(double a)
  {
    return nextAfter(a, Double.POSITIVE_INFINITY);
  }
  



  public static float nextUp(float a)
  {
    return nextAfter(a, Double.POSITIVE_INFINITY);
  }
  



  public static double random()
  {
    return Math.random();
  }
  



















  public static double exp(double x)
  {
    return exp(x, 0.0D, null);
  }
  



  private static double exp(double x, double extra, double[] hiPrec)
  {
    int intVal;
    


    double intPartA;
    


    double intPartB;
    

    if (x < 0.0D) {
      int intVal = (int)-x;
      
      if (intVal > 746) {
        if (hiPrec != null) {
          hiPrec[0] = 0.0D;
          hiPrec[1] = 0.0D;
        }
        return 0.0D;
      }
      
      if (intVal > 709)
      {
        double result = exp(x + 40.19140625D, extra, hiPrec) / 2.85040095144011776E17D;
        if (hiPrec != null) {
          hiPrec[0] /= 2.85040095144011776E17D;
          hiPrec[1] /= 2.85040095144011776E17D;
        }
        return result;
      }
      
      if (intVal == 709)
      {
        double result = exp(x + 1.494140625D, extra, hiPrec) / 4.455505956692757D;
        if (hiPrec != null) {
          hiPrec[0] /= 4.455505956692757D;
          hiPrec[1] /= 4.455505956692757D;
        }
        return result;
      }
      
      intVal++;
      
      double intPartA = EXP_INT_TABLE_A[(750 - intVal)];
      double intPartB = EXP_INT_TABLE_B[(750 - intVal)];
      
      intVal = -intVal;
    } else {
      intVal = (int)x;
      
      if (intVal > 709) {
        if (hiPrec != null) {
          hiPrec[0] = Double.POSITIVE_INFINITY;
          hiPrec[1] = 0.0D;
        }
        return Double.POSITIVE_INFINITY;
      }
      
      intPartA = EXP_INT_TABLE_A[(750 + intVal)];
      intPartB = EXP_INT_TABLE_B[(750 + intVal)];
    }
    




    int intFrac = (int)((x - intVal) * 1024.0D);
    double fracPartA = EXP_FRAC_TABLE_A[intFrac];
    double fracPartB = EXP_FRAC_TABLE_B[intFrac];
    




    double epsilon = x - (intVal + intFrac / 1024.0D);
    







    double z = 0.04168701738764507D;
    z = z * epsilon + 0.1666666505023083D;
    z = z * epsilon + 0.5000000000042687D;
    z = z * epsilon + 1.0D;
    z = z * epsilon + -3.940510424527919E-20D;
    





    double tempA = intPartA * fracPartA;
    double tempB = intPartA * fracPartB + intPartB * fracPartA + intPartB * fracPartB;
    




    double tempC = tempB + tempA;
    double result;
    double result; if (extra != 0.0D) {
      result = tempC * extra * z + tempC * extra + tempC * z + tempB + tempA;
    } else {
      result = tempC * z + tempB + tempA;
    }
    
    if (hiPrec != null)
    {
      hiPrec[0] = tempA;
      hiPrec[1] = (tempC * extra * z + tempC * extra + tempC * z + tempB);
    }
    
    return result;
  }
  



  public static double expm1(double x)
  {
    return expm1(x, null);
  }
  




  private static double expm1(double x, double[] hiPrecOut)
  {
    if ((x != x) || (x == 0.0D)) {
      return x;
    }
    
    if ((x <= -1.0D) || (x >= 1.0D))
    {

      double[] hiPrec = new double[2];
      exp(x, 0.0D, hiPrec);
      if (x > 0.0D) {
        return -1.0D + hiPrec[0] + hiPrec[1];
      }
      double ra = -1.0D + hiPrec[0];
      double rb = -(ra + 1.0D - hiPrec[0]);
      rb += hiPrec[1];
      return ra + rb;
    }
    




    boolean negative = false;
    
    if (x < 0.0D) {
      x = -x;
      negative = true;
    }
    

    int intFrac = (int)(x * 1024.0D);
    double tempA = EXP_FRAC_TABLE_A[intFrac] - 1.0D;
    double tempB = EXP_FRAC_TABLE_B[intFrac];
    
    double temp = tempA + tempB;
    tempB = -(temp - tempA - tempB);
    tempA = temp;
    
    temp = tempA * 1.073741824E9D;
    double baseA = tempA + temp - temp;
    double baseB = tempB + (tempA - baseA);
    
    double epsilon = x - intFrac / 1024.0D;
    



    double zb = 0.008336750013465571D;
    zb = zb * epsilon + 0.041666663879186654D;
    zb = zb * epsilon + 0.16666666666745392D;
    zb = zb * epsilon + 0.49999999999999994D;
    zb *= epsilon;
    zb *= epsilon;
    
    double za = epsilon;
    double temp = za + zb;
    zb = -(temp - za - zb);
    za = temp;
    
    temp = za * 1.073741824E9D;
    temp = za + temp - temp;
    zb += za - temp;
    za = temp;
    

    double ya = za * baseA;
    
    temp = ya + za * baseB;
    double yb = -(temp - ya - za * baseB);
    ya = temp;
    
    temp = ya + zb * baseA;
    yb += -(temp - ya - zb * baseA);
    ya = temp;
    
    temp = ya + zb * baseB;
    yb += -(temp - ya - zb * baseB);
    ya = temp;
    


    temp = ya + baseA;
    yb += -(temp - baseA - ya);
    ya = temp;
    
    temp = ya + za;
    
    yb += -(temp - ya - za);
    ya = temp;
    
    temp = ya + baseB;
    
    yb += -(temp - ya - baseB);
    ya = temp;
    
    temp = ya + zb;
    
    yb += -(temp - ya - zb);
    ya = temp;
    
    if (negative)
    {
      double denom = 1.0D + ya;
      double denomr = 1.0D / denom;
      double denomb = -(denom - 1.0D - ya) + yb;
      double ratio = ya * denomr;
      temp = ratio * 1.073741824E9D;
      double ra = ratio + temp - temp;
      double rb = ratio - ra;
      
      temp = denom * 1.073741824E9D;
      za = denom + temp - temp;
      zb = denom - za;
      
      rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
      









      rb += yb * denomr;
      rb += -ya * denomb * denomr * denomr;
      

      ya = -ra;
      yb = -rb;
    }
    
    if (hiPrecOut != null) {
      hiPrecOut[0] = ya;
      hiPrecOut[1] = yb;
    }
    
    return ya + yb;
  }
  






  private static double slowexp(double x, double[] result)
  {
    double[] xs = new double[2];
    double[] ys = new double[2];
    double[] facts = new double[2];
    double[] as = new double[2];
    split(x, xs); double 
      tmp31_30 = 0.0D;ys[1] = tmp31_30;ys[0] = tmp31_30;
    
    for (int i = 19; i >= 0; i--) {
      splitMult(xs, ys, as);
      ys[0] = as[0];
      ys[1] = as[1];
      
      split(FACT[i], as);
      splitReciprocal(as, facts);
      
      splitAdd(ys, facts, as);
      ys[0] = as[0];
      ys[1] = as[1];
    }
    
    if (result != null) {
      result[0] = ys[0];
      result[1] = ys[1];
    }
    
    return ys[0] + ys[1];
  }
  




  private static void split(double d, double[] split)
  {
    if ((d < 8.0E298D) && (d > -8.0E298D)) {
      double a = d * 1.073741824E9D;
      split[0] = (d + a - a);
      split[1] = (d - split[0]);
    } else {
      double a = d * 9.313225746154785E-10D;
      split[0] = ((d + a - d) * 1.073741824E9D);
      split[1] = (d - split[0]);
    }
  }
  



  private static void resplit(double[] a)
  {
    double c = a[0] + a[1];
    double d = -(c - a[0] - a[1]);
    
    if ((c < 8.0E298D) && (c > -8.0E298D)) {
      double z = c * 1.073741824E9D;
      a[0] = (c + z - z);
      a[1] = (c - a[0] + d);
    } else {
      double z = c * 9.313225746154785E-10D;
      a[0] = ((c + z - c) * 1.073741824E9D);
      a[1] = (c - a[0] + d);
    }
  }
  




  private static void splitMult(double[] a, double[] b, double[] ans)
  {
    a[0] *= b[0];
    ans[1] = (a[0] * b[1] + a[1] * b[0] + a[1] * b[1]);
    

    resplit(ans);
  }
  




  private static void splitAdd(double[] a, double[] b, double[] ans)
  {
    a[0] += b[0];
    a[1] += b[1];
    
    resplit(ans);
  }
  

















  private static void splitReciprocal(double[] in, double[] result)
  {
    double b = 2.384185791015625E-7D;
    double a = 0.9999997615814209D;
    
    if (in[0] == 0.0D) {
      in[0] = in[1];
      in[1] = 0.0D;
    }
    
    result[0] = (0.9999997615814209D / in[0]);
    result[1] = ((2.384185791015625E-7D * in[0] - 0.9999997615814209D * in[1]) / (in[0] * in[0] + in[0] * in[1]));
    
    if (result[1] != result[1]) {
      result[1] = 0.0D;
    }
    

    resplit(result);
    
    for (int i = 0; i < 2; i++)
    {
      double err = 1.0D - result[0] * in[0] - result[0] * in[1] - result[1] * in[0] - result[1] * in[1];
      

      err *= (result[0] + result[1]);
      
      result[1] += err;
    }
  }
  




  private static void quadMult(double[] a, double[] b, double[] result)
  {
    double[] xs = new double[2];
    double[] ys = new double[2];
    double[] zs = new double[2];
    

    split(a[0], xs);
    split(b[0], ys);
    splitMult(xs, ys, zs);
    
    result[0] = zs[0];
    result[1] = zs[1];
    

    split(b[1], ys);
    splitMult(xs, ys, zs);
    
    double tmp = result[0] + zs[0];
    result[1] -= tmp - result[0] - zs[0];
    result[0] = tmp;
    tmp = result[0] + zs[1];
    result[1] -= tmp - result[0] - zs[1];
    result[0] = tmp;
    

    split(a[1], xs);
    split(b[0], ys);
    splitMult(xs, ys, zs);
    
    tmp = result[0] + zs[0];
    result[1] -= tmp - result[0] - zs[0];
    result[0] = tmp;
    tmp = result[0] + zs[1];
    result[1] -= tmp - result[0] - zs[1];
    result[0] = tmp;
    

    split(a[1], xs);
    split(b[1], ys);
    splitMult(xs, ys, zs);
    
    tmp = result[0] + zs[0];
    result[1] -= tmp - result[0] - zs[0];
    result[0] = tmp;
    tmp = result[0] + zs[1];
    result[1] -= tmp - result[0] - zs[1];
    result[0] = tmp;
  }
  





  private static double expint(int p, double[] result)
  {
    double[] xs = new double[2];
    double[] as = new double[2];
    double[] ys = new double[2];
    







    xs[0] = 2.718281828459045D;
    xs[1] = 1.4456468917292502E-16D;
    
    split(1.0D, ys);
    
    while (p > 0) {
      if ((p & 0x1) != 0) {
        quadMult(ys, xs, as);
        ys[0] = as[0];ys[1] = as[1];
      }
      
      quadMult(xs, xs, as);
      xs[0] = as[0];xs[1] = as[1];
      
      p >>= 1;
    }
    
    if (result != null) {
      result[0] = ys[0];
      result[1] = ys[1];
      
      resplit(result);
    }
    
    return ys[0] + ys[1];
  }
  






  public static double log(double x)
  {
    return log(x, null);
  }
  





  private static double log(double x, double[] hiPrec)
  {
    if (x == 0.0D) {
      return Double.NEGATIVE_INFINITY;
    }
    long bits = Double.doubleToLongBits(x);
    

    if ((((bits & 0x8000000000000000) != 0L) || (x != x)) && 
      (x != 0.0D)) {
      if (hiPrec != null) {
        hiPrec[0] = NaN.0D;
      }
      
      return NaN.0D;
    }
    


    if (x == Double.POSITIVE_INFINITY) {
      if (hiPrec != null) {
        hiPrec[0] = Double.POSITIVE_INFINITY;
      }
      
      return Double.POSITIVE_INFINITY;
    }
    

    int exp = (int)(bits >> 52) - 1023;
    
    if ((bits & 0x7FF0000000000000) == 0L)
    {
      if (x == 0.0D)
      {
        if (hiPrec != null) {
          hiPrec[0] = Double.NEGATIVE_INFINITY;
        }
        
        return Double.NEGATIVE_INFINITY;
      }
      

      bits <<= 1;
      while ((bits & 0x10000000000000) == 0L) {
        exp--;
        bits <<= 1;
      }
    }
    

    if (((exp == -1) || (exp == 0)) && 
      (x < 1.01D) && (x > 0.99D) && (hiPrec == null))
    {



      double xa = x - 1.0D;
      double xb = xa - x + 1.0D;
      double tmp = xa * 1.073741824E9D;
      double aa = xa + tmp - tmp;
      double ab = xa - aa;
      xa = aa;
      xb = ab;
      
      double ya = LN_QUICK_COEF[(LN_QUICK_COEF.length - 1)][0];
      double yb = LN_QUICK_COEF[(LN_QUICK_COEF.length - 1)][1];
      
      for (int i = LN_QUICK_COEF.length - 2; i >= 0; i--)
      {
        aa = ya * xa;
        ab = ya * xb + yb * xa + yb * xb;
        
        tmp = aa * 1.073741824E9D;
        ya = aa + tmp - tmp;
        yb = aa - ya + ab;
        

        aa = ya + LN_QUICK_COEF[i][0];
        ab = yb + LN_QUICK_COEF[i][1];
        
        tmp = aa * 1.073741824E9D;
        ya = aa + tmp - tmp;
        yb = aa - ya + ab;
      }
      

      aa = ya * xa;
      ab = ya * xb + yb * xa + yb * xb;
      
      tmp = aa * 1.073741824E9D;
      ya = aa + tmp - tmp;
      yb = aa - ya + ab;
      
      return ya + yb;
    }
    


    double[] lnm = LN_MANT[((int)((bits & 0xFFC0000000000) >> 42))];
    









    double epsilon = (bits & 0x3FFFFFFFFFF) / (4.503599627370496E15D + (bits & 0xFFC0000000000));
    
    double lnza = 0.0D;
    double lnzb = 0.0D;
    
    if (hiPrec != null)
    {
      double tmp = epsilon * 1.073741824E9D;
      double aa = epsilon + tmp - tmp;
      double ab = epsilon - aa;
      double xa = aa;
      double xb = ab;
      

      double numer = bits & 0x3FFFFFFFFFF;
      double denom = 4.503599627370496E15D + (bits & 0xFFC0000000000);
      aa = numer - xa * denom - xb * denom;
      xb += aa / denom;
      

      double ya = LN_HI_PREC_COEF[(LN_HI_PREC_COEF.length - 1)][0];
      double yb = LN_HI_PREC_COEF[(LN_HI_PREC_COEF.length - 1)][1];
      
      for (int i = LN_HI_PREC_COEF.length - 2; i >= 0; i--)
      {
        aa = ya * xa;
        ab = ya * xb + yb * xa + yb * xb;
        
        tmp = aa * 1.073741824E9D;
        ya = aa + tmp - tmp;
        yb = aa - ya + ab;
        

        aa = ya + LN_HI_PREC_COEF[i][0];
        ab = yb + LN_HI_PREC_COEF[i][1];
        
        tmp = aa * 1.073741824E9D;
        ya = aa + tmp - tmp;
        yb = aa - ya + ab;
      }
      

      aa = ya * xa;
      ab = ya * xb + yb * xa + yb * xb;
      






      lnza = aa + ab;
      lnzb = -(lnza - aa - ab);
    }
    else
    {
      lnza = -0.16624882440418567D;
      lnza = lnza * epsilon + 0.19999954120254515D;
      lnza = lnza * epsilon + -0.2499999997677497D;
      lnza = lnza * epsilon + 0.3333333333332802D;
      lnza = lnza * epsilon + -0.5D;
      lnza = lnza * epsilon + 1.0D;
      lnza *= epsilon;
    }
    














    double a = 0.6931470632553101D * exp;
    double b = 0.0D;
    double c = a + lnm[0];
    double d = -(c - a - lnm[0]);
    a = c;
    b += d;
    
    c = a + lnza;
    d = -(c - a - lnza);
    a = c;
    b += d;
    
    c = a + 1.1730463525082348E-7D * exp;
    d = -(c - a - 1.1730463525082348E-7D * exp);
    a = c;
    b += d;
    
    c = a + lnm[1];
    d = -(c - a - lnm[1]);
    a = c;
    b += d;
    
    c = a + lnzb;
    d = -(c - a - lnzb);
    a = c;
    b += d;
    
    if (hiPrec != null) {
      hiPrec[0] = a;
      hiPrec[1] = b;
    }
    
    return a + b;
  }
  



  public static double log1p(double x)
  {
    double xpa = 1.0D + x;
    double xpb = -(xpa - 1.0D - x);
    
    if (x == -1.0D) {
      return x / 0.0D;
    }
    
    if ((x > 0.0D) && (1.0D / x == 0.0D)) {
      return x;
    }
    
    if ((x > 1.0E-6D) || (x < -1.0E-6D)) {
      double[] hiPrec = new double[2];
      
      double lores = log(xpa, hiPrec);
      if (Double.isInfinite(lores)) {
        return lores;
      }
      


      double fx1 = xpb / xpa;
      
      double epsilon = 0.5D * fx1 + 1.0D;
      epsilon *= fx1;
      
      return epsilon + hiPrec[1] + hiPrec[0];
    }
    

    double y = x * 0.333333333333333D - 0.5D;
    y = y * x + 1.0D;
    y *= x;
    
    return y;
  }
  



  public static double log10(double x)
  {
    double[] hiPrec = new double[2];
    
    double lores = log(x, hiPrec);
    if (Double.isInfinite(lores)) {
      return lores;
    }
    
    double tmp = hiPrec[0] * 1.073741824E9D;
    double lna = hiPrec[0] + tmp - tmp;
    double lnb = hiPrec[0] - lna + hiPrec[1];
    
    double rln10a = 0.4342944622039795D;
    double rln10b = 1.9699272335463627E-8D;
    
    return 1.9699272335463627E-8D * lnb + 1.9699272335463627E-8D * lna + 0.4342944622039795D * lnb + 0.4342944622039795D * lna;
  }
  






  public static double pow(double x, double y)
  {
    double[] lns = new double[2];
    
    if (y == 0.0D) {
      return 1.0D;
    }
    
    if (x != x) {
      return x;
    }
    

    if (x == 0.0D) {
      long bits = Double.doubleToLongBits(x);
      if ((bits & 0x8000000000000000) != 0L)
      {
        long yi = y;
        
        if ((y < 0.0D) && (y == yi) && ((yi & 1L) == 1L)) {
          return Double.NEGATIVE_INFINITY;
        }
        
        if ((y < 0.0D) && (y == yi) && ((yi & 1L) == 1L)) {
          return -0.0D;
        }
        
        if ((y > 0.0D) && (y == yi) && ((yi & 1L) == 1L)) {
          return -0.0D;
        }
      }
      
      if (y < 0.0D) {
        return Double.POSITIVE_INFINITY;
      }
      if (y > 0.0D) {
        return 0.0D;
      }
      
      return NaN.0D;
    }
    
    if (x == Double.POSITIVE_INFINITY) {
      if (y != y) {
        return y;
      }
      if (y < 0.0D) {
        return 0.0D;
      }
      return Double.POSITIVE_INFINITY;
    }
    

    if (y == Double.POSITIVE_INFINITY) {
      if (x * x == 1.0D) {
        return NaN.0D;
      }
      if (x * x > 1.0D) {
        return Double.POSITIVE_INFINITY;
      }
      return 0.0D;
    }
    

    if (x == Double.NEGATIVE_INFINITY) {
      if (y != y) {
        return y;
      }
      
      if (y < 0.0D) {
        long yi = y;
        if ((y == yi) && ((yi & 1L) == 1L)) {
          return -0.0D;
        }
        
        return 0.0D;
      }
      
      if (y > 0.0D) {
        long yi = y;
        if ((y == yi) && ((yi & 1L) == 1L)) {
          return Double.NEGATIVE_INFINITY;
        }
        
        return Double.POSITIVE_INFINITY;
      }
    }
    
    if (y == Double.NEGATIVE_INFINITY)
    {
      if (x * x == 1.0D) {
        return NaN.0D;
      }
      
      if (x * x < 1.0D) {
        return Double.POSITIVE_INFINITY;
      }
      return 0.0D;
    }
    


    if (x < 0.0D)
    {
      if ((y >= 4.503599627370496E15D) || (y <= -4.503599627370496E15D)) {
        return pow(-x, y);
      }
      
      if (y == y)
      {
        return (y & 1L) == 0L ? pow(-x, y) : -pow(-x, y);
      }
      return NaN.0D;
    }
    
    double yb;
    
    double ya;
    double yb;
    if ((y < 8.0E298D) && (y > -8.0E298D)) {
      double tmp1 = y * 1.073741824E9D;
      double ya = y + tmp1 - tmp1;
      yb = y - ya;
    } else {
      double tmp1 = y * 9.313225746154785E-10D;
      double tmp2 = tmp1 * 9.313225746154785E-10D;
      ya = (tmp1 + tmp2 - tmp1) * 1.073741824E9D * 1.073741824E9D;
      yb = y - ya;
    }
    

    double lores = log(x, lns);
    if (Double.isInfinite(lores)) {
      return lores;
    }
    
    double lna = lns[0];
    double lnb = lns[1];
    

    double tmp1 = lna * 1.073741824E9D;
    double tmp2 = lna + tmp1 - tmp1;
    lnb += lna - tmp2;
    lna = tmp2;
    

    double aa = lna * ya;
    double ab = lna * yb + lnb * ya + lnb * yb;
    
    lna = aa + ab;
    lnb = -(lna - aa - ab);
    
    double z = 0.008333333333333333D;
    z = z * lnb + 0.041666666666666664D;
    z = z * lnb + 0.16666666666666666D;
    z = z * lnb + 0.5D;
    z = z * lnb + 1.0D;
    z *= lnb;
    
    double result = exp(lna, z, null);
    
    return result;
  }
  


















  private static double[] slowLog(double xi)
  {
    double[] x = new double[2];
    double[] x2 = new double[2];
    double[] y = new double[2];
    double[] a = new double[2];
    
    split(xi, x);
    

    x[0] += 1.0D;
    resplit(x);
    splitReciprocal(x, a);
    x[0] -= 2.0D;
    resplit(x);
    splitMult(x, a, y);
    x[0] = y[0];
    x[1] = y[1];
    

    splitMult(x, x, x2);
    




    y[0] = LN_SPLIT_COEF[(LN_SPLIT_COEF.length - 1)][0];
    y[1] = LN_SPLIT_COEF[(LN_SPLIT_COEF.length - 1)][1];
    
    for (int i = LN_SPLIT_COEF.length - 2; i >= 0; i--) {
      splitMult(y, x2, a);
      y[0] = a[0];
      y[1] = a[1];
      splitAdd(y, LN_SPLIT_COEF[i], a);
      y[0] = a[0];
      y[1] = a[1];
    }
    
    splitMult(y, x, a);
    y[0] = a[0];
    y[1] = a[1];
    
    return y;
  }
  





  private static double slowSin(double x, double[] result)
  {
    double[] xs = new double[2];
    double[] ys = new double[2];
    double[] facts = new double[2];
    double[] as = new double[2];
    split(x, xs); double 
      tmp31_30 = 0.0D;ys[1] = tmp31_30;ys[0] = tmp31_30;
    
    for (int i = 19; i >= 0; i--) {
      splitMult(xs, ys, as);
      ys[0] = as[0];ys[1] = as[1];
      
      if ((i & 0x1) != 0)
      {


        split(FACT[i], as);
        splitReciprocal(as, facts);
        
        if ((i & 0x2) != 0) {
          facts[0] = (-facts[0]);
          facts[1] = (-facts[1]);
        }
        
        splitAdd(ys, facts, as);
        ys[0] = as[0];ys[1] = as[1];
      }
    }
    if (result != null) {
      result[0] = ys[0];
      result[1] = ys[1];
    }
    
    return ys[0] + ys[1];
  }
  






  private static double slowCos(double x, double[] result)
  {
    double[] xs = new double[2];
    double[] ys = new double[2];
    double[] facts = new double[2];
    double[] as = new double[2];
    split(x, xs); double 
      tmp31_30 = 0.0D;ys[1] = tmp31_30;ys[0] = tmp31_30;
    
    for (int i = 19; i >= 0; i--) {
      splitMult(xs, ys, as);
      ys[0] = as[0];ys[1] = as[1];
      
      if ((i & 0x1) == 0)
      {


        split(FACT[i], as);
        splitReciprocal(as, facts);
        
        if ((i & 0x2) != 0) {
          facts[0] = (-facts[0]);
          facts[1] = (-facts[1]);
        }
        
        splitAdd(ys, facts, as);
        ys[0] = as[0];ys[1] = as[1];
      }
    }
    if (result != null) {
      result[0] = ys[0];
      result[1] = ys[1];
    }
    
    return ys[0] + ys[1];
  }
  

  private static void buildSinCosTables()
  {
    double[] result = new double[2];
    

    for (int i = 0; i < 7; i++) {
      double x = i / 8.0D;
      
      slowSin(x, result);
      SINE_TABLE_A[i] = result[0];
      SINE_TABLE_B[i] = result[1];
      
      slowCos(x, result);
      COSINE_TABLE_A[i] = result[0];
      COSINE_TABLE_B[i] = result[1];
    }
    

    for (int i = 7; i < 14; i++) {
      double[] xs = new double[2];
      double[] ys = new double[2];
      double[] as = new double[2];
      double[] bs = new double[2];
      double[] temps = new double[2];
      
      if ((i & 0x1) == 0)
      {
        xs[0] = SINE_TABLE_A[(i / 2)];
        xs[1] = SINE_TABLE_B[(i / 2)];
        ys[0] = COSINE_TABLE_A[(i / 2)];
        ys[1] = COSINE_TABLE_B[(i / 2)];
        

        splitMult(xs, ys, result);
        SINE_TABLE_A[i] = (result[0] * 2.0D);
        SINE_TABLE_B[i] = (result[1] * 2.0D);
        

        splitMult(ys, ys, as);
        splitMult(xs, xs, temps);
        temps[0] = (-temps[0]);
        temps[1] = (-temps[1]);
        splitAdd(as, temps, result);
        COSINE_TABLE_A[i] = result[0];
        COSINE_TABLE_B[i] = result[1];
      } else {
        xs[0] = SINE_TABLE_A[(i / 2)];
        xs[1] = SINE_TABLE_B[(i / 2)];
        ys[0] = COSINE_TABLE_A[(i / 2)];
        ys[1] = COSINE_TABLE_B[(i / 2)];
        as[0] = SINE_TABLE_A[(i / 2 + 1)];
        as[1] = SINE_TABLE_B[(i / 2 + 1)];
        bs[0] = COSINE_TABLE_A[(i / 2 + 1)];
        bs[1] = COSINE_TABLE_B[(i / 2 + 1)];
        

        splitMult(xs, bs, temps);
        splitMult(ys, as, result);
        splitAdd(result, temps, result);
        SINE_TABLE_A[i] = result[0];
        SINE_TABLE_B[i] = result[1];
        

        splitMult(ys, bs, result);
        splitMult(xs, as, temps);
        temps[0] = (-temps[0]);
        temps[1] = (-temps[1]);
        splitAdd(result, temps, result);
        COSINE_TABLE_A[i] = result[0];
        COSINE_TABLE_B[i] = result[1];
      }
    }
    

    for (int i = 0; i < 14; i++) {
      double[] xs = new double[2];
      double[] ys = new double[2];
      double[] as = new double[2];
      
      as[0] = COSINE_TABLE_A[i];
      as[1] = COSINE_TABLE_B[i];
      
      splitReciprocal(as, ys);
      
      xs[0] = SINE_TABLE_A[i];
      xs[1] = SINE_TABLE_B[i];
      
      splitMult(xs, ys, as);
      
      TANGENT_TABLE_A[i] = as[0];
      TANGENT_TABLE_B[i] = as[1];
    }
  }
  







  private static double polySine(double x)
  {
    double x2 = x * x;
    
    double p = 2.7553817452272217E-6D;
    p = p * x2 + -1.9841269659586505E-4D;
    p = p * x2 + 0.008333333333329196D;
    p = p * x2 + -0.16666666666666666D;
    

    p = p * x2 * x;
    
    return p;
  }
  





  private static double polyCosine(double x)
  {
    double x2 = x * x;
    
    double p = 2.479773539153719E-5D;
    p = p * x2 + -0.0013888888689039883D;
    p = p * x2 + 0.041666666666621166D;
    p = p * x2 + -0.49999999999999994D;
    p *= x2;
    
    return p;
  }
  






  private static double sinQ(double xa, double xb)
  {
    int idx = (int)(xa * 8.0D + 0.5D);
    double epsilon = xa - EIGHTHS[idx];
    

    double sintA = SINE_TABLE_A[idx];
    double sintB = SINE_TABLE_B[idx];
    double costA = COSINE_TABLE_A[idx];
    double costB = COSINE_TABLE_B[idx];
    

    double sinEpsA = epsilon;
    double sinEpsB = polySine(epsilon);
    double cosEpsA = 1.0D;
    double cosEpsB = polyCosine(epsilon);
    

    double temp = sinEpsA * 1.073741824E9D;
    double temp2 = sinEpsA + temp - temp;
    sinEpsB += sinEpsA - temp2;
    sinEpsA = temp2;
    
























    double a = 0.0D;
    double b = 0.0D;
    
    double t = sintA;
    double c = a + t;
    double d = -(c - a - t);
    a = c;
    b += d;
    
    t = costA * sinEpsA;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b += d;
    
    b = b + sintA * cosEpsB + costA * sinEpsB;
    













    b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
    

























    if (xb != 0.0D) {
      t = ((costA + costB) * (1.0D + cosEpsB) - (sintA + sintB) * (sinEpsA + sinEpsB)) * xb;
      
      c = a + t;
      d = -(c - a - t);
      a = c;
      b += d;
    }
    
    double result = a + b;
    
    return result;
  }
  






  private static double cosQ(double xa, double xb)
  {
    double pi2a = 1.5707963267948966D;
    double pi2b = 6.123233995736766E-17D;
    
    double a = 1.5707963267948966D - xa;
    double b = -(a - 1.5707963267948966D + xa);
    b += 6.123233995736766E-17D - xb;
    
    return sinQ(a, b);
  }
  








  private static double tanQ(double xa, double xb, boolean cotanFlag)
  {
    int idx = (int)(xa * 8.0D + 0.5D);
    double epsilon = xa - EIGHTHS[idx];
    

    double sintA = SINE_TABLE_A[idx];
    double sintB = SINE_TABLE_B[idx];
    double costA = COSINE_TABLE_A[idx];
    double costB = COSINE_TABLE_B[idx];
    

    double sinEpsA = epsilon;
    double sinEpsB = polySine(epsilon);
    double cosEpsA = 1.0D;
    double cosEpsB = polyCosine(epsilon);
    

    double temp = sinEpsA * 1.073741824E9D;
    double temp2 = sinEpsA + temp - temp;
    sinEpsB += sinEpsA - temp2;
    sinEpsA = temp2;
    























    double a = 0.0D;
    double b = 0.0D;
    

    double t = sintA;
    double c = a + t;
    double d = -(c - a - t);
    a = c;
    b += d;
    
    t = costA * sinEpsA;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b += d;
    
    b = b + sintA * cosEpsB + costA * sinEpsB;
    b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
    
    double sina = a + b;
    double sinb = -(sina - a - b);
    


    a = b = c = d = 0.0D;
    
    t = costA * 1.0D;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b += d;
    
    t = -sintA * sinEpsA;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b += d;
    
    b = b + costB * 1.0D + costA * cosEpsB + costB * cosEpsB;
    b -= sintB * sinEpsA + sintA * sinEpsB + sintB * sinEpsB;
    
    double cosa = a + b;
    double cosb = -(cosa - a - b);
    
    if (cotanFlag)
    {
      double tmp = cosa;cosa = sina;sina = tmp;
      tmp = cosb;cosb = sinb;sinb = tmp;
    }
    











    double est = sina / cosa;
    

    temp = est * 1.073741824E9D;
    double esta = est + temp - temp;
    double estb = est - esta;
    
    temp = cosa * 1.073741824E9D;
    double cosaa = cosa + temp - temp;
    double cosab = cosa - cosaa;
    

    double err = (sina - esta * cosaa - esta * cosab - estb * cosaa - estb * cosab) / cosa;
    err += sinb / cosa;
    err += -sina * cosb / cosa / cosa;
    
    if (xb != 0.0D)
    {

      double xbadj = xb + est * est * xb;
      if (cotanFlag) {
        xbadj = -xbadj;
      }
      
      err += xbadj;
    }
    
    return est + err;
  }
  












  private static void reducePayneHanek(double x, double[] result)
  {
    long inbits = Double.doubleToLongBits(x);
    int exponent = (int)(inbits >> 52 & 0x7FF) - 1023;
    

    inbits &= 0xFFFFFFFFFFFFF;
    inbits |= 0x10000000000000;
    

    exponent++;
    inbits <<= 11;
    




    int idx = exponent >> 6;
    int shift = exponent - (idx << 6);
    long shpiB;
    long shpi0; long shpiA; long shpiB; if (shift != 0) {
      long shpi0 = idx == 0 ? 0L : RECIP_2PI[(idx - 1)] << shift;
      shpi0 |= RECIP_2PI[idx] >>> 64 - shift;
      long shpiA = RECIP_2PI[idx] << shift | RECIP_2PI[(idx + 1)] >>> 64 - shift;
      shpiB = RECIP_2PI[(idx + 1)] << shift | RECIP_2PI[(idx + 2)] >>> 64 - shift;
    } else {
      shpi0 = idx == 0 ? 0L : RECIP_2PI[(idx - 1)];
      shpiA = RECIP_2PI[idx];
      shpiB = RECIP_2PI[(idx + 1)];
    }
    

    long a = inbits >>> 32;
    long b = inbits & 0xFFFFFFFF;
    
    long c = shpiA >>> 32;
    long d = shpiA & 0xFFFFFFFF;
    
    long ac = a * c;
    long bd = b * d;
    long bc = b * c;
    long ad = a * d;
    
    long prodB = bd + (ad << 32);
    long prodA = ac + (ad >>> 32);
    
    boolean bita = (bd & 0x8000000000000000) != 0L;
    boolean bitb = (ad & 0x80000000) != 0L;
    boolean bitsum = (prodB & 0x8000000000000000) != 0L;
    

    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prodA += 1L;
    }
    
    bita = (prodB & 0x8000000000000000) != 0L;
    bitb = (bc & 0x80000000) != 0L;
    
    prodB += (bc << 32);
    prodA += (bc >>> 32);
    
    bitsum = (prodB & 0x8000000000000000) != 0L;
    

    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prodA += 1L;
    }
    

    c = shpiB >>> 32;
    d = shpiB & 0xFFFFFFFF;
    ac = a * c;
    bc = b * c;
    ad = a * d;
    

    ac += (bc + ad >>> 32);
    
    bita = (prodB & 0x8000000000000000) != 0L;
    bitb = (ac & 0x8000000000000000) != 0L;
    prodB += ac;
    bitsum = (prodB & 0x8000000000000000) != 0L;
    
    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prodA += 1L;
    }
    

    c = shpi0 >>> 32;
    d = shpi0 & 0xFFFFFFFF;
    
    bd = b * d;
    bc = b * c;
    ad = a * d;
    
    prodA += bd + (bc + ad << 32);
    










    int intPart = (int)(prodA >>> 62);
    

    prodA <<= 2;
    prodA |= prodB >>> 62;
    prodB <<= 2;
    

    a = prodA >>> 32;
    b = prodA & 0xFFFFFFFF;
    
    c = PI_O_4_BITS[0] >>> 32;
    d = PI_O_4_BITS[0] & 0xFFFFFFFF;
    
    ac = a * c;
    bd = b * d;
    bc = b * c;
    ad = a * d;
    
    long prod2B = bd + (ad << 32);
    long prod2A = ac + (ad >>> 32);
    
    bita = (bd & 0x8000000000000000) != 0L;
    bitb = (ad & 0x80000000) != 0L;
    bitsum = (prod2B & 0x8000000000000000) != 0L;
    

    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prod2A += 1L;
    }
    
    bita = (prod2B & 0x8000000000000000) != 0L;
    bitb = (bc & 0x80000000) != 0L;
    
    prod2B += (bc << 32);
    prod2A += (bc >>> 32);
    
    bitsum = (prod2B & 0x8000000000000000) != 0L;
    

    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prod2A += 1L;
    }
    

    c = PI_O_4_BITS[1] >>> 32;
    d = PI_O_4_BITS[1] & 0xFFFFFFFF;
    ac = a * c;
    bc = b * c;
    ad = a * d;
    

    ac += (bc + ad >>> 32);
    
    bita = (prod2B & 0x8000000000000000) != 0L;
    bitb = (ac & 0x8000000000000000) != 0L;
    prod2B += ac;
    bitsum = (prod2B & 0x8000000000000000) != 0L;
    
    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prod2A += 1L;
    }
    

    a = prodB >>> 32;
    b = prodB & 0xFFFFFFFF;
    c = PI_O_4_BITS[0] >>> 32;
    d = PI_O_4_BITS[0] & 0xFFFFFFFF;
    ac = a * c;
    bc = b * c;
    ad = a * d;
    

    ac += (bc + ad >>> 32);
    
    bita = (prod2B & 0x8000000000000000) != 0L;
    bitb = (ac & 0x8000000000000000) != 0L;
    prod2B += ac;
    bitsum = (prod2B & 0x8000000000000000) != 0L;
    
    if (((bita) && (bitb)) || (((bita) || (bitb)) && (!bitsum)))
    {
      prod2A += 1L;
    }
    

    double tmpA = (prod2A >>> 12) / 4.503599627370496E15D;
    double tmpB = (((prod2A & 0xFFF) << 40) + (prod2B >>> 24)) / 4.503599627370496E15D / 4.503599627370496E15D;
    
    double sumA = tmpA + tmpB;
    double sumB = -(sumA - tmpA - tmpB);
    

    result[0] = intPart;
    result[1] = (sumA * 2.0D);
    result[2] = (sumB * 2.0D);
  }
  




  public static double sin(double x)
  {
    boolean negative = false;
    int quadrant = 0;
    
    double xb = 0.0D;
    

    double xa = x;
    if (x < 0.0D) {
      negative = true;
      xa = -xa;
    }
    

    if (xa == 0.0D) {
      long bits = Double.doubleToLongBits(x);
      if (bits < 0L) {
        return -0.0D;
      }
      return 0.0D;
    }
    
    if ((xa != xa) || (xa == Double.POSITIVE_INFINITY)) {
      return NaN.0D;
    }
    

    if (xa > 3294198.0D)
    {


      double[] reduceResults = new double[3];
      reducePayneHanek(xa, reduceResults);
      quadrant = (int)reduceResults[0] & 0x3;
      xa = reduceResults[1];
      xb = reduceResults[2];
    } else if (xa > 1.5707963267948966D)
    {



      int k = (int)(xa * 0.6366197723675814D);
      
      double remA;
      double remB;
      for (;;)
      {
        double a = -k * 1.570796251296997D;
        remA = xa + a;
        remB = -(remA - xa - a);
        
        a = -k * 7.549789948768648E-8D;
        double b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        a = -k * 6.123233995736766E-17D;
        b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        if (remA > 0.0D) {
          break;
        }
        


        k--;
      }
      quadrant = k & 0x3;
      xa = remA;
      xb = remB;
    }
    
    if (negative) {
      quadrant ^= 0x2;
    }
    
    switch (quadrant) {
    case 0: 
      return sinQ(xa, xb);
    case 1: 
      return cosQ(xa, xb);
    case 2: 
      return -sinQ(xa, xb);
    case 3: 
      return -cosQ(xa, xb);
    }
    return NaN.0D;
  }
  





  public static double cos(double x)
  {
    int quadrant = 0;
    

    double xa = x;
    if (x < 0.0D) {
      xa = -xa;
    }
    
    if ((xa != xa) || (xa == Double.POSITIVE_INFINITY)) {
      return NaN.0D;
    }
    

    double xb = 0.0D;
    if (xa > 3294198.0D)
    {


      double[] reduceResults = new double[3];
      reducePayneHanek(xa, reduceResults);
      quadrant = (int)reduceResults[0] & 0x3;
      xa = reduceResults[1];
      xb = reduceResults[2];
    } else if (xa > 1.5707963267948966D)
    {



      int k = (int)(xa * 0.6366197723675814D);
      
      double remA;
      double remB;
      for (;;)
      {
        double a = -k * 1.570796251296997D;
        remA = xa + a;
        remB = -(remA - xa - a);
        
        a = -k * 7.549789948768648E-8D;
        double b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        a = -k * 6.123233995736766E-17D;
        b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        if (remA > 0.0D) {
          break;
        }
        


        k--;
      }
      quadrant = k & 0x3;
      xa = remA;
      xb = remB;
    }
    



    switch (quadrant) {
    case 0: 
      return cosQ(xa, xb);
    case 1: 
      return -sinQ(xa, xb);
    case 2: 
      return -cosQ(xa, xb);
    case 3: 
      return sinQ(xa, xb);
    }
    return NaN.0D;
  }
  





  public static double tan(double x)
  {
    boolean negative = false;
    int quadrant = 0;
    

    double xa = x;
    if (x < 0.0D) {
      negative = true;
      xa = -xa;
    }
    

    if (xa == 0.0D) {
      long bits = Double.doubleToLongBits(x);
      if (bits < 0L) {
        return -0.0D;
      }
      return 0.0D;
    }
    
    if ((xa != xa) || (xa == Double.POSITIVE_INFINITY)) {
      return NaN.0D;
    }
    

    double xb = 0.0D;
    if (xa > 3294198.0D)
    {


      double[] reduceResults = new double[3];
      reducePayneHanek(xa, reduceResults);
      quadrant = (int)reduceResults[0] & 0x3;
      xa = reduceResults[1];
      xb = reduceResults[2];
    } else if (xa > 1.5707963267948966D)
    {



      int k = (int)(xa * 0.6366197723675814D);
      
      double remA;
      double remB;
      for (;;)
      {
        double a = -k * 1.570796251296997D;
        remA = xa + a;
        remB = -(remA - xa - a);
        
        a = -k * 7.549789948768648E-8D;
        double b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        a = -k * 6.123233995736766E-17D;
        b = remA;
        remA = a + b;
        remB += -(remA - b - a);
        
        if (remA > 0.0D) {
          break;
        }
        


        k--;
      }
      quadrant = k & 0x3;
      xa = remA;
      xb = remB;
    }
    
    if (xa > 1.5D)
    {
      double pi2a = 1.5707963267948966D;
      double pi2b = 6.123233995736766E-17D;
      
      double a = 1.5707963267948966D - xa;
      double b = -(a - 1.5707963267948966D + xa);
      b += 6.123233995736766E-17D - xb;
      
      xa = a + b;
      xb = -(xa - a - b);
      quadrant ^= 0x1;
      negative ^= true;
    }
    double result;
    double result;
    if ((quadrant & 0x1) == 0) {
      result = tanQ(xa, xb, false);
    } else {
      result = -tanQ(xa, xb, true);
    }
    
    if (negative) {
      result = -result;
    }
    
    return result;
  }
  




  public static double atan(double x)
  {
    return atan(x, 0.0D, false);
  }
  





  private static double atan(double xa, double xb, boolean leftPlane)
  {
    boolean negate = false;
    

    if (xa == 0.0D) {
      return leftPlane ? copySign(3.141592653589793D, xa) : xa;
    }
    
    if (xa < 0.0D)
    {
      xa = -xa;
      xb = -xb;
      negate = true;
    }
    
    if (xa > 1.633123935319537E16D) {
      return (negate ^ leftPlane) ? -1.5707963267948966D : 1.5707963267948966D;
    }
    int idx;
    int idx;
    if (xa < 1.0D) {
      idx = (int)((-1.7168146928204135D * xa * xa + 8.0D) * xa + 0.5D);
    } else {
      double temp = 1.0D / xa;
      idx = (int)(-((-1.7168146928204135D * temp * temp + 8.0D) * temp) + 13.07D);
    }
    double epsA = xa - TANGENT_TABLE_A[idx];
    double epsB = -(epsA - xa + TANGENT_TABLE_A[idx]);
    epsB += xb - TANGENT_TABLE_B[idx];
    
    double temp = epsA + epsB;
    epsB = -(temp - epsA - epsB);
    epsA = temp;
    

    temp = xa * 1.073741824E9D;
    double ya = xa + temp - temp;
    double yb = xb + xa - ya;
    xa = ya;
    xb += yb;
    

    if (idx == 0)
    {

      double denom = 1.0D / (1.0D + (xa + xb) * (TANGENT_TABLE_A[idx] + TANGENT_TABLE_B[idx]));
      
      ya = epsA * denom;
      yb = epsB * denom;
    } else {
      double temp2 = xa * TANGENT_TABLE_A[idx];
      double za = 1.0D + temp2;
      double zb = -(za - 1.0D - temp2);
      temp2 = xb * TANGENT_TABLE_A[idx] + xa * TANGENT_TABLE_B[idx];
      temp = za + temp2;
      zb += -(temp - za - temp2);
      za = temp;
      
      zb += xb * TANGENT_TABLE_B[idx];
      ya = epsA / za;
      
      temp = ya * 1.073741824E9D;
      double yaa = ya + temp - temp;
      double yab = ya - yaa;
      
      temp = za * 1.073741824E9D;
      double zaa = za + temp - temp;
      double zab = za - zaa;
      

      yb = (epsA - yaa * zaa - yaa * zab - yab * zaa - yab * zab) / za;
      
      yb += -epsA * zb / za / za;
      yb += epsB / za;
    }
    

    epsA = ya;
    epsB = yb;
    

    double epsA2 = epsA * epsA;
    









    yb = 0.07490822288864472D;
    yb = yb * epsA2 + -0.09088450866185192D;
    yb = yb * epsA2 + 0.11111095942313305D;
    yb = yb * epsA2 + -0.1428571423679182D;
    yb = yb * epsA2 + 0.19999999999923582D;
    yb = yb * epsA2 + -0.33333333333333287D;
    yb = yb * epsA2 * epsA;
    

    ya = epsA;
    
    temp = ya + yb;
    yb = -(temp - ya - yb);
    ya = temp;
    

    yb += epsB / (1.0D + epsA * epsA);
    




    double za = EIGHTHS[idx] + ya;
    double zb = -(za - EIGHTHS[idx] - ya);
    temp = za + yb;
    zb += -(temp - za - yb);
    za = temp;
    
    double result = za + zb;
    double resultb = -(result - za - zb);
    
    if (leftPlane)
    {
      double pia = 3.141592653589793D;
      double pib = 1.2246467991473532E-16D;
      
      za = 3.141592653589793D - result;
      zb = -(za - 3.141592653589793D + result);
      zb += 1.2246467991473532E-16D - resultb;
      
      result = za + zb;
      resultb = -(result - za - zb);
    }
    

    if ((negate ^ leftPlane)) {
      result = -result;
    }
    
    return result;
  }
  





  public static double atan2(double y, double x)
  {
    if ((x != x) || (y != y)) {
      return NaN.0D;
    }
    
    if (y == 0.0D) {
      double result = x * y;
      double invx = 1.0D / x;
      double invy = 1.0D / y;
      
      if (invx == 0.0D) {
        if (x > 0.0D) {
          return y;
        }
        return copySign(3.141592653589793D, y);
      }
      

      if ((x < 0.0D) || (invx < 0.0D)) {
        if ((y < 0.0D) || (invy < 0.0D)) {
          return -3.141592653589793D;
        }
        return 3.141592653589793D;
      }
      
      return result;
    }
    



    if (y == Double.POSITIVE_INFINITY) {
      if (x == Double.POSITIVE_INFINITY) {
        return 0.7853981633974483D;
      }
      
      if (x == Double.NEGATIVE_INFINITY) {
        return 2.356194490192345D;
      }
      
      return 1.5707963267948966D;
    }
    
    if (y == Double.NEGATIVE_INFINITY) {
      if (x == Double.POSITIVE_INFINITY) {
        return -0.7853981633974483D;
      }
      
      if (x == Double.NEGATIVE_INFINITY) {
        return -2.356194490192345D;
      }
      
      return -1.5707963267948966D;
    }
    
    if (x == Double.POSITIVE_INFINITY) {
      if ((y > 0.0D) || (1.0D / y > 0.0D)) {
        return 0.0D;
      }
      
      if ((y < 0.0D) || (1.0D / y < 0.0D)) {
        return -0.0D;
      }
    }
    
    if (x == Double.NEGATIVE_INFINITY)
    {
      if ((y > 0.0D) || (1.0D / y > 0.0D)) {
        return 3.141592653589793D;
      }
      
      if ((y < 0.0D) || (1.0D / y < 0.0D)) {
        return -3.141592653589793D;
      }
    }
    


    if (x == 0.0D) {
      if ((y > 0.0D) || (1.0D / y > 0.0D)) {
        return 1.5707963267948966D;
      }
      
      if ((y < 0.0D) || (1.0D / y < 0.0D)) {
        return -1.5707963267948966D;
      }
    }
    

    double r = y / x;
    if (Double.isInfinite(r)) {
      return atan(r, 0.0D, x < 0.0D);
    }
    
    double ra = doubleHighPart(r);
    double rb = r - ra;
    

    double xa = doubleHighPart(x);
    double xb = x - xa;
    
    rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
    
    double temp = ra + rb;
    rb = -(temp - ra - rb);
    ra = temp;
    
    if (ra == 0.0D) {
      ra = copySign(0.0D, y);
    }
    

    double result = atan(ra, rb, x < 0.0D);
    
    return result;
  }
  



  public static double asin(double x)
  {
    if (x != x) {
      return NaN.0D;
    }
    
    if ((x > 1.0D) || (x < -1.0D)) {
      return NaN.0D;
    }
    
    if (x == 1.0D) {
      return 1.5707963267948966D;
    }
    
    if (x == -1.0D) {
      return -1.5707963267948966D;
    }
    
    if (x == 0.0D) {
      return x;
    }
    



    double temp = x * 1.073741824E9D;
    double xa = x + temp - temp;
    double xb = x - xa;
    

    double ya = xa * xa;
    double yb = xa * xb * 2.0D + xb * xb;
    

    ya = -ya;
    yb = -yb;
    
    double za = 1.0D + ya;
    double zb = -(za - 1.0D - ya);
    
    temp = za + yb;
    zb += -(temp - za - yb);
    za = temp;
    


    double y = sqrt(za);
    temp = y * 1.073741824E9D;
    ya = y + temp - temp;
    yb = y - ya;
    

    yb += (za - ya * ya - 2.0D * ya * yb - yb * yb) / (2.0D * y);
    

    double dx = zb / (2.0D * y);
    

    double r = x / y;
    temp = r * 1.073741824E9D;
    double ra = r + temp - temp;
    double rb = r - ra;
    
    rb += (x - ra * ya - ra * yb - rb * ya - rb * yb) / y;
    rb += -x * dx / y / y;
    
    temp = ra + rb;
    rb = -(temp - ra - rb);
    ra = temp;
    
    return atan(ra, rb, false);
  }
  



  public static double acos(double x)
  {
    if (x != x) {
      return NaN.0D;
    }
    
    if ((x > 1.0D) || (x < -1.0D)) {
      return NaN.0D;
    }
    
    if (x == -1.0D) {
      return 3.141592653589793D;
    }
    
    if (x == 1.0D) {
      return 0.0D;
    }
    
    if (x == 0.0D) {
      return 1.5707963267948966D;
    }
    



    double temp = x * 1.073741824E9D;
    double xa = x + temp - temp;
    double xb = x - xa;
    

    double ya = xa * xa;
    double yb = xa * xb * 2.0D + xb * xb;
    

    ya = -ya;
    yb = -yb;
    
    double za = 1.0D + ya;
    double zb = -(za - 1.0D - ya);
    
    temp = za + yb;
    zb += -(temp - za - yb);
    za = temp;
    

    double y = sqrt(za);
    temp = y * 1.073741824E9D;
    ya = y + temp - temp;
    yb = y - ya;
    

    yb += (za - ya * ya - 2.0D * ya * yb - yb * yb) / (2.0D * y);
    

    yb += zb / (2.0D * y);
    y = ya + yb;
    yb = -(y - ya - yb);
    

    double r = y / x;
    

    if (Double.isInfinite(r)) {
      return 1.5707963267948966D;
    }
    
    double ra = doubleHighPart(r);
    double rb = r - ra;
    
    rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
    rb += yb / x;
    
    temp = ra + rb;
    rb = -(temp - ra - rb);
    ra = temp;
    
    return atan(ra, rb, x < 0.0D);
  }
  




  public static double cbrt(double x)
  {
    long inbits = Double.doubleToLongBits(x);
    int exponent = (int)(inbits >> 52 & 0x7FF) - 1023;
    boolean subnormal = false;
    
    if (exponent == 64513) {
      if (x == 0.0D) {
        return x;
      }
      

      subnormal = true;
      x *= 1.8014398509481984E16D;
      inbits = Double.doubleToLongBits(x);
      exponent = (int)(inbits >> 52 & 0x7FF) - 1023;
    }
    
    if (exponent == 1024)
    {
      return x;
    }
    

    int exp3 = exponent / 3;
    

    double p2 = Double.longBitsToDouble(inbits & 0x8000000000000000 | (exp3 + 1023 & 0x7FF) << 52);
    


    double mant = Double.longBitsToDouble(inbits & 0xFFFFFFFFFFFFF | 0x3FF0000000000000);
    

    double est = -0.010714690733195933D;
    est = est * mant + 0.0875862700108075D;
    est = est * mant + -0.3058015757857271D;
    est = est * mant + 0.7249995199969751D;
    est = est * mant + 0.5039018405998233D;
    
    est *= CBRTTWO[(exponent % 3 + 2)];
    



    double xs = x / (p2 * p2 * p2);
    est += (xs - est * est * est) / (3.0D * est * est);
    est += (xs - est * est * est) / (3.0D * est * est);
    

    double temp = est * 1.073741824E9D;
    double ya = est + temp - temp;
    double yb = est - ya;
    
    double za = ya * ya;
    double zb = ya * yb * 2.0D + yb * yb;
    temp = za * 1.073741824E9D;
    double temp2 = za + temp - temp;
    zb += za - temp2;
    za = temp2;
    
    zb = za * yb + ya * zb + zb * yb;
    za *= ya;
    
    double na = xs - za;
    double nb = -(na - xs + za);
    nb -= zb;
    
    est += (na + nb) / (3.0D * est * est);
    

    est *= p2;
    
    if (subnormal) {
      est *= 3.814697265625E-6D;
    }
    
    return est;
  }
  





  public static double toRadians(double x)
  {
    if ((Double.isInfinite(x)) || (x == 0.0D)) {
      return x;
    }
    

    double facta = 0.01745329052209854D;
    double factb = 1.997844754509471E-9D;
    
    double xa = doubleHighPart(x);
    double xb = x - xa;
    
    double result = xb * 1.997844754509471E-9D + xb * 0.01745329052209854D + xa * 1.997844754509471E-9D + xa * 0.01745329052209854D;
    if (result == 0.0D) {
      result *= x;
    }
    return result;
  }
  





  public static double toDegrees(double x)
  {
    if ((Double.isInfinite(x)) || (x == 0.0D)) {
      return x;
    }
    

    double facta = 57.2957763671875D;
    double factb = 3.145894820876798E-6D;
    
    double xa = doubleHighPart(x);
    double xb = x - xa;
    
    return xb * 3.145894820876798E-6D + xb * 57.2957763671875D + xa * 3.145894820876798E-6D + xa * 57.2957763671875D;
  }
  




  public static int abs(int x)
  {
    return x < 0 ? -x : x;
  }
  




  public static long abs(long x)
  {
    return x < 0L ? -x : x;
  }
  




  public static float abs(float x)
  {
    return x == 0.0F ? 0.0F : x < 0.0F ? -x : x;
  }
  




  public static double abs(double x)
  {
    return x == 0.0D ? 0.0D : x < 0.0D ? -x : x;
  }
  




  public static double ulp(double x)
  {
    if (Double.isInfinite(x)) {
      return Double.POSITIVE_INFINITY;
    }
    return abs(x - Double.longBitsToDouble(Double.doubleToLongBits(x) ^ 1L));
  }
  




  public static float ulp(float x)
  {
    if (Float.isInfinite(x)) {
      return Float.POSITIVE_INFINITY;
    }
    return abs(x - Float.intBitsToFloat(Float.floatToIntBits(x) ^ 0x1));
  }
  







  public static double scalb(double d, int n)
  {
    if ((n > 64513) && (n < 1024)) {
      return d * Double.longBitsToDouble(n + 1023 << 52);
    }
    

    if ((Double.isNaN(d)) || (Double.isInfinite(d)) || (d == 0.0D)) {
      return d;
    }
    if (n < 63438) {
      return d > 0.0D ? 0.0D : -0.0D;
    }
    if (n > 2097) {
      return d > 0.0D ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    

    long bits = Double.doubleToLongBits(d);
    long sign = bits & 0x8000000000000000;
    int exponent = (int)(bits >>> 52) & 0x7FF;
    long mantissa = bits & 0xFFFFFFFFFFFFF;
    

    int scaledExponent = exponent + n;
    
    if (n < 0)
    {
      if (scaledExponent > 0)
      {
        return Double.longBitsToDouble(sign | scaledExponent << 52 | mantissa); }
      if (scaledExponent > -53)
      {


        mantissa |= 0x10000000000000;
        

        long mostSignificantLostBit = mantissa & 1L << -scaledExponent;
        mantissa >>>= 1 - scaledExponent;
        if (mostSignificantLostBit != 0L)
        {
          mantissa += 1L;
        }
        return Double.longBitsToDouble(sign | mantissa);
      }
      

      return sign == 0L ? 0.0D : -0.0D;
    }
    

    if (exponent == 0)
    {

      while (mantissa >>> 52 != 1L) {
        mantissa <<= 1;
        scaledExponent--;
      }
      scaledExponent++;
      mantissa &= 0xFFFFFFFFFFFFF;
      
      if (scaledExponent < 2047) {
        return Double.longBitsToDouble(sign | scaledExponent << 52 | mantissa);
      }
      return sign == 0L ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    
    if (scaledExponent < 2047) {
      return Double.longBitsToDouble(sign | scaledExponent << 52 | mantissa);
    }
    return sign == 0L ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
  }
  










  public static float scalb(float f, int n)
  {
    if ((n > -127) && (n < 128)) {
      return f * Float.intBitsToFloat(n + 127 << 23);
    }
    

    if ((Float.isNaN(f)) || (Float.isInfinite(f)) || (f == 0.0F)) {
      return f;
    }
    if (n < 65259) {
      return f > 0.0F ? 0.0F : -0.0F;
    }
    if (n > 276) {
      return f > 0.0F ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
    }
    

    int bits = Float.floatToIntBits(f);
    int sign = bits & 0x80000000;
    int exponent = bits >>> 23 & 0xFF;
    int mantissa = bits & 0x7FFFFF;
    

    int scaledExponent = exponent + n;
    
    if (n < 0)
    {
      if (scaledExponent > 0)
      {
        return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa); }
      if (scaledExponent > -24)
      {


        mantissa |= 0x800000;
        

        int mostSignificantLostBit = mantissa & 1 << -scaledExponent;
        mantissa >>>= 1 - scaledExponent;
        if (mostSignificantLostBit != 0)
        {
          mantissa++;
        }
        return Float.intBitsToFloat(sign | mantissa);
      }
      

      return sign == 0 ? 0.0F : -0.0F;
    }
    

    if (exponent == 0)
    {

      while (mantissa >>> 23 != 1) {
        mantissa <<= 1;
        scaledExponent--;
      }
      scaledExponent++;
      mantissa &= 0x7FFFFF;
      
      if (scaledExponent < 255) {
        return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa);
      }
      return sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
    }
    
    if (scaledExponent < 255) {
      return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa);
    }
    return sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
  }
  



































  public static double nextAfter(double d, double direction)
  {
    if ((Double.isNaN(d)) || (Double.isNaN(direction)))
      return NaN.0D;
    if (d == direction)
      return direction;
    if (Double.isInfinite(d))
      return d < 0.0D ? -1.7976931348623157E308D : Double.MAX_VALUE;
    if (d == 0.0D) {
      return direction < 0.0D ? -4.9E-324D : Double.MIN_VALUE;
    }
    


    long bits = Double.doubleToLongBits(d);
    long sign = bits & 0x8000000000000000;
    if (((direction < d ? 1 : 0) ^ (sign == 0L ? 1 : 0)) != 0) {
      return Double.longBitsToDouble(sign | (bits & 0x7FFFFFFFFFFFFFFF) + 1L);
    }
    return Double.longBitsToDouble(sign | (bits & 0x7FFFFFFFFFFFFFFF) - 1L);
  }
  


































  public static float nextAfter(float f, double direction)
  {
    if ((Double.isNaN(f)) || (Double.isNaN(direction)))
      return NaN.0F;
    if (f == direction)
      return (float)direction;
    if (Float.isInfinite(f))
      return f < 0.0F ? -3.4028235E38F : Float.MAX_VALUE;
    if (f == 0.0F) {
      return direction < 0.0D ? -1.4E-45F : Float.MIN_VALUE;
    }
    


    int bits = Float.floatToIntBits(f);
    int sign = bits & 0x80000000;
    if (((direction < f ? 1 : 0) ^ (sign == 0 ? 1 : 0)) != 0) {
      return Float.intBitsToFloat(sign | (bits & 0x7FFFFFFF) + 1);
    }
    return Float.intBitsToFloat(sign | (bits & 0x7FFFFFFF) - 1);
  }
  







  public static double floor(double x)
  {
    if (x != x) {
      return x;
    }
    
    if ((x >= 4.503599627370496E15D) || (x <= -4.503599627370496E15D)) {
      return x;
    }
    
    long y = x;
    if ((x < 0.0D) && (y != x)) {
      y -= 1L;
    }
    
    if (y == 0L) {
      return x * y;
    }
    
    return y;
  }
  





  public static double ceil(double x)
  {
    if (x != x) {
      return x;
    }
    
    double y = floor(x);
    if (y == x) {
      return y;
    }
    
    y += 1.0D;
    
    if (y == 0.0D) {
      return x * y;
    }
    
    return y;
  }
  



  public static double rint(double x)
  {
    double y = floor(x);
    double d = x - y;
    
    if (d > 0.5D) {
      if (y == -1.0D) {
        return -0.0D;
      }
      return y + 1.0D;
    }
    if (d < 0.5D) {
      return y;
    }
    

    long z = y;
    return (z & 1L) == 0L ? y : y + 1.0D;
  }
  



  public static long round(double x)
  {
    return floor(x + 0.5D);
  }
  



  public static int round(float x)
  {
    return (int)floor(x + 0.5F);
  }
  




  public static int min(int a, int b)
  {
    return a <= b ? a : b;
  }
  




  public static long min(long a, long b)
  {
    return a <= b ? a : b;
  }
  




  public static float min(float a, float b)
  {
    if (a > b) {
      return b;
    }
    if (a < b) {
      return a;
    }
    
    if (a != b) {
      return NaN.0F;
    }
    

    int bits = Float.floatToRawIntBits(a);
    if (bits == Integer.MIN_VALUE) {
      return a;
    }
    return b;
  }
  




  public static double min(double a, double b)
  {
    if (a > b) {
      return b;
    }
    if (a < b) {
      return a;
    }
    
    if (a != b) {
      return NaN.0D;
    }
    

    long bits = Double.doubleToRawLongBits(a);
    if (bits == Long.MIN_VALUE) {
      return a;
    }
    return b;
  }
  




  public static int max(int a, int b)
  {
    return a <= b ? b : a;
  }
  




  public static long max(long a, long b)
  {
    return a <= b ? b : a;
  }
  




  public static float max(float a, float b)
  {
    if (a > b) {
      return a;
    }
    if (a < b) {
      return b;
    }
    
    if (a != b) {
      return NaN.0F;
    }
    

    int bits = Float.floatToRawIntBits(a);
    if (bits == Integer.MIN_VALUE) {
      return b;
    }
    return a;
  }
  




  public static double max(double a, double b)
  {
    if (a > b) {
      return a;
    }
    if (a < b) {
      return b;
    }
    
    if (a != b) {
      return NaN.0D;
    }
    

    long bits = Double.doubleToRawLongBits(a);
    if (bits == Long.MIN_VALUE) {
      return b;
    }
    return a;
  }
  













  public static double hypot(double x, double y)
  {
    if ((Double.isInfinite(x)) || (Double.isInfinite(y)))
      return Double.POSITIVE_INFINITY;
    if ((Double.isNaN(x)) || (Double.isNaN(y))) {
      return NaN.0D;
    }
    
    int expX = getExponent(x);
    int expY = getExponent(y);
    if (expX > expY + 27)
    {
      return abs(x); }
    if (expY > expX + 27)
    {
      return abs(y);
    }
    

    int middleExp = (expX + expY) / 2;
    

    double scaledX = scalb(x, -middleExp);
    double scaledY = scalb(y, -middleExp);
    

    double scaledH = sqrt(scaledX * scaledX + scaledY * scaledY);
    

    return scalb(scaledH, middleExp);
  }
  























  public static double IEEEremainder(double dividend, double divisor)
  {
    return StrictMath.IEEEremainder(dividend, divisor);
  }
  







  public static double copySign(double magnitude, double sign)
  {
    long m = Double.doubleToLongBits(magnitude);
    long s = Double.doubleToLongBits(sign);
    if (((m >= 0L) && (s >= 0L)) || ((m < 0L) && (s < 0L))) {
      return magnitude;
    }
    return -magnitude;
  }
  







  public static float copySign(float magnitude, float sign)
  {
    int m = Float.floatToIntBits(magnitude);
    int s = Float.floatToIntBits(sign);
    if (((m >= 0) && (s >= 0)) || ((m < 0) && (s < 0))) {
      return magnitude;
    }
    return -magnitude;
  }
  








  public static int getExponent(double d)
  {
    return (int)(Double.doubleToLongBits(d) >>> 52 & 0x7FF) - 1023;
  }
  








  public static int getExponent(float f)
  {
    return (Float.floatToIntBits(f) >>> 23 & 0xFF) - 127;
  }
  
  private FastMath() {}
}

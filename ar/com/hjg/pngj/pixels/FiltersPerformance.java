package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjExceptionInternal;
import java.util.Arrays;




public class FiltersPerformance
{
  private final ImageInfo iminfo;
  private double memoryA = 0.7D;
  private int lastrow = -1;
  private double[] absum = new double[5];
  
  private double[] entropy = new double[5];
  private double[] cost = new double[5];
  private int[] histog = new int['Ā'];
  private int lastprefered = -1;
  private boolean initdone = false;
  private double preferenceForNone = 1.0D;
  



  public static final double[] FILTER_WEIGHTS_DEFAULT = { 0.73D, 1.03D, 0.97D, 1.11D, 1.22D };
  

  private double[] filter_weights = { -1.0D, -1.0D, -1.0D, -1.0D, -1.0D };
  
  private static final double LOG2NI = -1.0D / Math.log(2.0D);
  
  public FiltersPerformance(ImageInfo imgInfo) {
    iminfo = imgInfo;
  }
  
  private void init() {
    if (filter_weights[0] < 0.0D) {
      System.arraycopy(FILTER_WEIGHTS_DEFAULT, 0, filter_weights, 0, 5);
      double wNone = filter_weights[0];
      if (iminfo.bitDepth == 16) {
        wNone = 1.2D;
      } else if (iminfo.alpha) {
        wNone = 0.8D;
      } else if ((iminfo.indexed) || (iminfo.bitDepth < 8))
        wNone = 0.4D;
      wNone /= preferenceForNone;
      filter_weights[0] = wNone;
    }
    Arrays.fill(cost, 1.0D);
    initdone = true;
  }
  
  public void updateFromFiltered(FilterType ftype, byte[] rowff, int rown) {
    updateFromRawOrFiltered(ftype, rowff, null, null, rown);
  }
  
  public void updateFromRaw(FilterType ftype, byte[] rowb, byte[] rowbprev, int rown)
  {
    updateFromRawOrFiltered(ftype, null, rowb, rowbprev, rown);
  }
  
  private void updateFromRawOrFiltered(FilterType ftype, byte[] rowff, byte[] rowb, byte[] rowbprev, int rown)
  {
    if (!initdone)
      init();
    if (rown != lastrow) {
      Arrays.fill(absum, NaN.0D);
      Arrays.fill(entropy, NaN.0D);
    }
    lastrow = rown;
    if (rowff != null) {
      computeHistogram(rowff);
    } else
      computeHistogramForFilter(ftype, rowb, rowbprev);
    if (ftype == FilterType.FILTER_NONE) {
      entropy[val] = computeEntropyFromHistogram();
    } else {
      absum[val] = computeAbsFromHistogram();
    }
  }
  
  public FilterType getPreferred() {
    int fi = 0;
    double vali = Double.MAX_VALUE;double val = 0.0D;
    for (int i = 0; i < 5; i++) {
      if (!Double.isNaN(absum[i])) {
        val = absum[i];
      } else { if (Double.isNaN(entropy[i])) continue;
        val = (Math.pow(2.0D, entropy[i]) - 1.0D) * 0.5D;
      }
      
      val *= filter_weights[i];
      val = cost[i] * memoryA + (1.0D - memoryA) * val;
      cost[i] = val;
      if (val < vali) {
        vali = val;
        fi = i;
      }
    }
    lastprefered = fi;
    return FilterType.getByVal(lastprefered);
  }
  
  public final void computeHistogramForFilter(FilterType filterType, byte[] rowb, byte[] rowbprev) {
    Arrays.fill(histog, 0);
    int imax = iminfo.bytesPerRow;
    int i; switch (1.$SwitchMap$ar$com$hjg$pngj$FilterType[filterType.ordinal()]) {
    case 1: 
      for (i = 1; i <= imax;) {
        histog[(rowb[i] & 0xFF)] += 1;i++; continue;
        

        for (i = 1; i <= imax; i++)
          histog[ar.com.hjg.pngj.PngHelperInternal.filterRowPaeth(rowb[i], 0, rowbprev[i] & 0xFF, 0)] += 1;
        j = 1; for (i = iminfo.bytesPixel + 1; i <= imax;) {
          histog[ar.com.hjg.pngj.PngHelperInternal.filterRowPaeth(rowb[i], rowb[j] & 0xFF, rowbprev[i] & 0xFF, rowbprev[j] & 0xFF)] += 1;i++;j++; continue;
          


          for (i = 1; i <= iminfo.bytesPixel; i++)
            histog[(rowb[i] & 0xFF)] += 1;
          j = 1; for (i = iminfo.bytesPixel + 1; i <= imax;) {
            histog[(rowb[i] - rowb[j] & 0xFF)] += 1;i++;j++; continue;
            

            for (i = 1; i <= iminfo.bytesPerRow;) {
              histog[(rowb[i] - rowbprev[i] & 0xFF)] += 1;i++; continue;
              

              for (i = 1; i <= iminfo.bytesPixel; i++)
                histog[((rowb[i] & 0xFF) - (rowbprev[i] & 0xFF) / 2 & 0xFF)] += 1;
              j = 1; for (i = iminfo.bytesPixel + 1; i <= imax;) {
                histog[((rowb[i] & 0xFF) - ((rowbprev[i] & 0xFF) + (rowb[j] & 0xFF)) / 2 & 0xFF)] += 1;i++;j++; continue;
                

                throw new PngjExceptionInternal("Bad filter:" + filterType);
              }
            } } } } }
    int j; }
  
  public void computeHistogram(byte[] rowff) { Arrays.fill(histog, 0);
    for (int i = 1; i < iminfo.bytesPerRow; i++)
      histog[(rowff[i] & 0xFF)] += 1;
  }
  
  public double computeAbsFromHistogram() {
    int s = 0;
    for (int i = 1; i < 128; i++)
      s += histog[i] * i;
    int i = 128; for (int j = 128; j > 0; j--) {
      s += histog[i] * j;i++; }
    return s / iminfo.bytesPerRow;
  }
  
  public final double computeEntropyFromHistogram() {
    double s = 1.0D / iminfo.bytesPerRow;
    double ls = Math.log(s);
    
    double h = 0.0D;
    for (int x : histog) {
      if (x > 0)
        h += (Math.log(x) + ls) * x;
    }
    h *= s * LOG2NI;
    if (h < 0.0D)
      h = 0.0D;
    return h;
  }
  




  public void setPreferenceForNone(double preferenceForNone)
  {
    this.preferenceForNone = preferenceForNone;
  }
  




  public void tuneMemory(double m)
  {
    if (m == 0.0D) {
      memoryA = 0.0D;
    } else {
      memoryA = Math.pow(memoryA, 1.0D / m);
    }
  }
  




  public void setFilterWeights(double[] weights)
  {
    System.arraycopy(weights, 0, filter_weights, 0, 5);
  }
}

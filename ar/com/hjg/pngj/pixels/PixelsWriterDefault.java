package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjOutputException;
import java.util.Arrays;












public class PixelsWriterDefault
  extends PixelsWriter
{
  protected byte[] rowb;
  protected byte[] rowbprev;
  protected byte[] rowbfilter;
  protected FiltersPerformance filtersPerformance;
  protected FilterType curfilterType;
  protected int adaptMaxSkip;
  protected int adaptSkipIncreaseSinceRow;
  protected double adaptSkipIncreaseFactor;
  protected int adaptNextRow = 0;
  
  public PixelsWriterDefault(ImageInfo imgInfo) {
    super(imgInfo);
    filtersPerformance = new FiltersPerformance(imgInfo);
  }
  
  protected void initParams()
  {
    super.initParams();
    
    if ((rowb == null) || (rowb.length < buflen))
      rowb = new byte[buflen];
    if ((rowbfilter == null) || (rowbfilter.length < buflen))
      rowbfilter = new byte[buflen];
    if ((rowbprev == null) || (rowbprev.length < buflen)) {
      rowbprev = new byte[buflen];
    } else {
      Arrays.fill(rowbprev, (byte)0);
    }
    
    if ((imgInfo.cols < 3) && (!FilterType.isValidStandard(filterType)))
      filterType = FilterType.FILTER_DEFAULT;
    if ((imgInfo.rows < 3) && (!FilterType.isValidStandard(filterType))) {
      filterType = FilterType.FILTER_DEFAULT;
    }
    if ((imgInfo.getTotalPixels() <= 1024L) && (!FilterType.isValidStandard(filterType))) {
      filterType = getDefaultFilter();
    }
    if (FilterType.isAdaptive(filterType))
    {
      adaptNextRow = 0;
      if (filterType == FilterType.FILTER_ADAPTIVE_FAST) {
        adaptMaxSkip = 200;
        adaptSkipIncreaseSinceRow = 3;
        adaptSkipIncreaseFactor = 0.25D;
      } else if (filterType == FilterType.FILTER_ADAPTIVE_MEDIUM) {
        adaptMaxSkip = 8;
        adaptSkipIncreaseSinceRow = 32;
        adaptSkipIncreaseFactor = 0.0125D;
      } else if (filterType == FilterType.FILTER_ADAPTIVE_FULL) {
        adaptMaxSkip = 0;
        adaptSkipIncreaseSinceRow = 128;
        adaptSkipIncreaseFactor = 0.008333333333333333D;
      } else {
        throw new PngjOutputException("bad filter " + filterType);
      }
    }
  }
  
  protected void filterAndWrite(byte[] rowb) {
    if (rowb != this.rowb)
      throw new RuntimeException("??");
    decideCurFilterType();
    byte[] filtered = filterRowWithFilterType(curfilterType, rowb, rowbprev, rowbfilter);
    sendToCompressedStream(filtered);
    
    byte[] aux = this.rowb;
    this.rowb = rowbprev;
    rowbprev = aux;
  }
  
  protected void decideCurFilterType()
  {
    if (FilterType.isValidStandard(getFilterType())) {
      curfilterType = getFilterType();
    } else if (getFilterType() == FilterType.FILTER_PRESERVE) {
      curfilterType = FilterType.getByVal(rowb[0]);
    } else if (getFilterType() == FilterType.FILTER_CYCLIC) {
      curfilterType = FilterType.getByVal(currentRow % 5);
    } else if (getFilterType() == FilterType.FILTER_DEFAULT) {
      setFilterType(getDefaultFilter());
      curfilterType = getFilterType();
    } else if (FilterType.isAdaptive(getFilterType())) {
      if (currentRow == adaptNextRow) {
        for (FilterType ftype : FilterType.getAllStandard())
          filtersPerformance.updateFromRaw(ftype, rowb, rowbprev, currentRow);
        curfilterType = filtersPerformance.getPreferred();
        int skip = currentRow >= adaptSkipIncreaseSinceRow ? (int)Math.round((currentRow - adaptSkipIncreaseSinceRow) * adaptSkipIncreaseFactor) : 0;
        

        if (skip > adaptMaxSkip)
          skip = adaptMaxSkip;
        if (currentRow == 0)
          skip = 0;
        adaptNextRow = (currentRow + 1 + skip);
      }
    } else {
      throw new PngjOutputException("not implemented filter: " + getFilterType());
    }
    if ((currentRow == 0) && (curfilterType != FilterType.FILTER_NONE) && (curfilterType != FilterType.FILTER_SUB))
    {
      curfilterType = FilterType.FILTER_SUB;
    }
  }
  
  public byte[] getRowb() {
    if (!initdone)
      init();
    return rowb;
  }
  
  public void close()
  {
    super.close();
  }
  


  public void setPreferenceForNone(double preferenceForNone)
  {
    filtersPerformance.setPreferenceForNone(preferenceForNone);
  }
  


  public void tuneMemory(double m)
  {
    filtersPerformance.tuneMemory(m);
  }
  


  public void setFilterWeights(double[] weights)
  {
    filtersPerformance.setFilterWeights(weights);
  }
}

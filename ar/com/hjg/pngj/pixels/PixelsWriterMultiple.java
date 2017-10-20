package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.ImageInfo;
import java.util.LinkedList;









public class PixelsWriterMultiple
  extends PixelsWriter
{
  protected LinkedList<byte[]> rows;
  protected CompressorStream[] filterBank = new CompressorStream[6];
  


  protected byte[][] filteredRows = new byte[5][];
  
  protected byte[] filteredRowTmp;
  protected FiltersPerformance filtersPerf;
  protected int rowsPerBand = 0;
  protected int rowsPerBandCurrent = 0;
  
  protected int rowInBand = -1;
  protected int bandNum = -1;
  protected int firstRowInThisBand;
  protected int lastRowInThisBand; private boolean tryAdaptive = true;
  
  protected static final int HINT_MEMORY_DEFAULT_KB = 100;
  
  protected int hintMemoryKb = 100;
  
  private int hintRowsPerBand = 1000;
  
  private boolean useLz4 = true;
  
  public PixelsWriterMultiple(ImageInfo imgInfo) {
    super(imgInfo);
    filtersPerf = new FiltersPerformance(imgInfo);
    rows = new LinkedList();
    for (int i = 0; i < 2; i++)
      rows.add(new byte[buflen]);
    filteredRowTmp = new byte[buflen];
  }
  
  protected void filterAndWrite(byte[] rowb)
  {
    if (!initdone)
      init();
    if (rowb != rows.get(0))
      throw new RuntimeException("?");
    setBandFromNewRown();
    byte[] rowbprev = (byte[])rows.get(1);
    for (FilterType ftype : FilterType.getAllStandardNoneLast())
    {

      if ((currentRow != 0) || (ftype == FilterType.FILTER_NONE) || (ftype == FilterType.FILTER_SUB))
      {
        byte[] filtered = filterRowWithFilterType(ftype, rowb, rowbprev, filteredRows[val]);
        filterBank[val].write(filtered);
        if ((currentRow == 0) && (ftype == FilterType.FILTER_SUB)) {
          filterBank[FILTER_PAETHval].write(filtered);
          filterBank[FILTER_AVERAGEval].write(filtered);
          filterBank[FILTER_UPval].write(filtered);
        }
        
        if (tryAdaptive)
          filtersPerf.updateFromFiltered(ftype, filtered, currentRow);
      }
    }
    filteredRows[0] = rowb;
    if (tryAdaptive) {
      FilterType preferredAdaptive = filtersPerf.getPreferred();
      filterBank[5].write(filteredRows[val]);
    }
    if (currentRow == lastRowInThisBand) {
      int best = getBestCompressor();
      


      byte[] filtersAdapt = filterBank[best].getFirstBytes();
      int r = firstRowInThisBand;int i = 0; for (int j = lastRowInThisBand - firstRowInThisBand; r <= lastRowInThisBand; i++) {
        int fti = filtersAdapt[i];
        byte[] filtered = null;
        if (r != lastRowInThisBand) {
          filtered = filterRowWithFilterType(FilterType.getByVal(fti), (byte[])rows.get(j), (byte[])rows.get(j + 1), filteredRowTmp);
        }
        else
        {
          filtered = filteredRows[fti];
        }
        sendToCompressedStream(filtered);r++;j--;
      }
    }
    
    if (rows.size() > rowsPerBandCurrent) {
      rows.addFirst(rows.removeLast());
    } else {
      rows.addFirst(new byte[buflen]);
    }
  }
  
  public byte[] getRowb() {
    return (byte[])rows.get(0);
  }
  
  private void setBandFromNewRown()
  {
    boolean newBand = (currentRow == 0) || (currentRow > lastRowInThisBand);
    if (currentRow == 0)
      bandNum = -1;
    if (newBand) {
      bandNum += 1;
      rowInBand = 0;
    } else {
      rowInBand += 1;
    }
    if (newBand) {
      firstRowInThisBand = currentRow;
      lastRowInThisBand = (firstRowInThisBand + rowsPerBand - 1);
      int lastRowInNextBand = firstRowInThisBand + 2 * rowsPerBand - 1;
      if (lastRowInNextBand >= imgInfo.rows)
      {
        lastRowInThisBand = (imgInfo.rows - 1); }
      rowsPerBandCurrent = (1 + lastRowInThisBand - firstRowInThisBand);
      tryAdaptive = ((rowsPerBandCurrent > 3) && ((rowsPerBandCurrent >= 10) || (imgInfo.bytesPerRow >= 64)));
      


      rebuildFiltersBank();
    }
  }
  
  private void rebuildFiltersBank() {
    long bytesPerBandCurrent = rowsPerBandCurrent * buflen;
    int DEFLATER_COMP_LEVEL = 4;
    for (int i = 0; i <= 5; i++) {
      CompressorStream cp = filterBank[i];
      if ((cp == null) || (totalbytes != bytesPerBandCurrent)) {
        if (cp != null)
          cp.close();
        if (useLz4) {
          cp = new CompressorStreamLz4(null, buflen, bytesPerBandCurrent);
        } else {
          cp = new CompressorStreamDeflater(null, buflen, bytesPerBandCurrent, 4, 0);
        }
        
        filterBank[i] = cp;
      } else {
        cp.reset();
      }
      cp.setStoreFirstByte(true, rowsPerBandCurrent);
    }
  }
  
  private int computeInitialRowsPerBand()
  {
    int r = (int)(hintMemoryKb * 1024.0D / (imgInfo.bytesPerRow + 1) - 5.0D);
    if (r < 1)
      r = 1;
    if ((hintRowsPerBand > 0) && (r > hintRowsPerBand))
      r = hintRowsPerBand;
    if (r > imgInfo.rows)
      r = imgInfo.rows;
    if ((r > 2) && (r > imgInfo.rows / 8)) {
      int k = (imgInfo.rows + (r - 1)) / r;
      r = (imgInfo.rows + k / 2) / k;
    }
    
    return r;
  }
  
  private int getBestCompressor() {
    double bestcr = Double.MAX_VALUE;
    int bestb = -1;
    for (int i = tryAdaptive ? 5 : 4; i >= 0; i--) {
      CompressorStream fb = filterBank[i];
      double cr = fb.getCompressionRatio();
      if (cr <= bestcr)
      {
        bestb = i;
        bestcr = cr;
      }
    }
    return bestb;
  }
  
  protected void initParams()
  {
    super.initParams();
    
    if ((imgInfo.cols < 3) && (!FilterType.isValidStandard(filterType)))
      filterType = FilterType.FILTER_DEFAULT;
    if ((imgInfo.rows < 3) && (!FilterType.isValidStandard(filterType)))
      filterType = FilterType.FILTER_DEFAULT;
    for (int i = 1; i <= 4; i++) {
      if ((filteredRows[i] == null) || (filteredRows[i].length < buflen))
        filteredRows[i] = new byte[buflen];
    }
    if (rowsPerBand == 0) {
      rowsPerBand = computeInitialRowsPerBand();
    }
  }
  
  public void close() {
    super.close();
    rows.clear();
    for (CompressorStream f : filterBank) {
      f.close();
    }
  }
  
  public void setHintMemoryKb(int hintMemoryKb) {
    this.hintMemoryKb = (hintMemoryKb > 10000 ? 10000 : hintMemoryKb <= 0 ? 100 : hintMemoryKb);
  }
  
  public void setHintRowsPerBand(int hintRowsPerBand)
  {
    this.hintRowsPerBand = hintRowsPerBand;
  }
  
  public void setUseLz4(boolean lz4) {
    useLz4 = lz4;
  }
  
  public FiltersPerformance getFiltersPerf()
  {
    return filtersPerf;
  }
  
  public void setTryAdaptive(boolean tryAdaptive) {
    this.tryAdaptive = tryAdaptive;
  }
}

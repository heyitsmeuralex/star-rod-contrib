package ar.com.hjg.pngj;

import java.util.Arrays;
import java.util.zip.Checksum;
import java.util.zip.Inflater;








public class IdatSet
  extends DeflatedChunksSet
{
  protected byte[] rowUnfiltered;
  protected byte[] rowUnfilteredPrev;
  protected final ImageInfo imgInfo;
  protected final Deinterlacer deinterlacer;
  final RowInfo rowinfo;
  protected int[] filterUseStat = new int[5];
  




  public IdatSet(String id, ImageInfo iminfo, Deinterlacer deinterlacer)
  {
    this(id, iminfo, deinterlacer, null, null);
  }
  






  public IdatSet(String id, ImageInfo iminfo, Deinterlacer deinterlacer, Inflater inf, byte[] buffer)
  {
    super(id, deinterlacer != null ? deinterlacer.getBytesToRead() + 1 : bytesPerRow + 1, bytesPerRow + 1, inf, buffer);
    
    imgInfo = iminfo;
    this.deinterlacer = deinterlacer;
    rowinfo = new RowInfo(iminfo, deinterlacer);
  }
  


  public void unfilterRow()
  {
    unfilterRow(rowinfo.bytesRow);
  }
  
  protected void unfilterRow(int nbytes)
  {
    if ((rowUnfiltered == null) || (rowUnfiltered.length < row.length)) {
      rowUnfiltered = new byte[row.length];
      rowUnfilteredPrev = new byte[row.length];
    }
    if (rowinfo.rowNsubImg == 0) {
      Arrays.fill(rowUnfiltered, (byte)0);
    }
    byte[] tmp = rowUnfiltered;
    rowUnfiltered = rowUnfilteredPrev;
    rowUnfilteredPrev = tmp;
    
    int ftn = row[0];
    if (!FilterType.isValidStandard(ftn))
      throw new PngjInputException("Filter type " + ftn + " invalid");
    FilterType ft = FilterType.getByVal(ftn);
    filterUseStat[ftn] += 1;
    rowUnfiltered[0] = row[0];
    switch (1.$SwitchMap$ar$com$hjg$pngj$FilterType[ft.ordinal()]) {
    case 1: 
      unfilterRowNone(nbytes);
      break;
    case 2: 
      unfilterRowSub(nbytes);
      break;
    case 3: 
      unfilterRowUp(nbytes);
      break;
    case 4: 
      unfilterRowAverage(nbytes);
      break;
    case 5: 
      unfilterRowPaeth(nbytes);
      break;
    default: 
      throw new PngjInputException("Filter type " + ftn + " not implemented");
    }
  }
  
  private void unfilterRowAverage(int nbytes)
  {
    int j = 1 - imgInfo.bytesPixel; for (int i = 1; i <= nbytes; j++) {
      int x = j > 0 ? rowUnfiltered[j] & 0xFF : 0;
      rowUnfiltered[i] = ((byte)(row[i] + (x + (rowUnfilteredPrev[i] & 0xFF)) / 2));i++;
    }
  }
  
  private void unfilterRowNone(int nbytes)
  {
    for (int i = 1; i <= nbytes; i++) {
      rowUnfiltered[i] = row[i];
    }
  }
  
  private void unfilterRowPaeth(int nbytes)
  {
    int j = 1 - imgInfo.bytesPixel; for (int i = 1; i <= nbytes; j++) {
      int x = j > 0 ? rowUnfiltered[j] & 0xFF : 0;
      int y = j > 0 ? rowUnfilteredPrev[j] & 0xFF : 0;
      rowUnfiltered[i] = ((byte)(row[i] + PngHelperInternal.filterPaethPredictor(x, rowUnfilteredPrev[i] & 0xFF, y)));i++;
    }
  }
  




  private void unfilterRowSub(int nbytes)
  {
    for (int i = 1; i <= imgInfo.bytesPixel; i++) {
      rowUnfiltered[i] = row[i];
    }
    int j = 1; for (i = imgInfo.bytesPixel + 1; i <= nbytes; j++) {
      rowUnfiltered[i] = ((byte)(row[i] + rowUnfiltered[j]));i++;
    }
  }
  
  private void unfilterRowUp(int nbytes) {
    for (int i = 1; i <= nbytes; i++) {
      rowUnfiltered[i] = ((byte)(row[i] + rowUnfilteredPrev[i]));
    }
  }
  



  protected void preProcessRow()
  {
    super.preProcessRow();
    rowinfo.update(getRown());
    unfilterRow();
    rowinfo.updateBuf(rowUnfiltered, rowinfo.bytesRow + 1);
  }
  














  protected int processRowCallback()
  {
    int bytesNextRow = advanceToNextRow();
    return bytesNextRow;
  }
  
  protected void processDoneCallback()
  {
    super.processDoneCallback();
  }
  



  public int advanceToNextRow()
  {
    int bytesNextRow;
    


    int bytesNextRow;
    

    if (deinterlacer == null) {
      bytesNextRow = getRown() >= imgInfo.rows - 1 ? 0 : imgInfo.bytesPerRow + 1;
    } else {
      boolean more = deinterlacer.nextRow();
      bytesNextRow = more ? deinterlacer.getBytesToRead() + 1 : 0;
    }
    if (!isCallbackMode()) {
      prepareForNextRow(bytesNextRow);
    }
    return bytesNextRow;
  }
  
  public boolean isRowReady() {
    return !isWaitingForMoreInput();
  }
  










  public byte[] getUnfilteredRow()
  {
    return rowUnfiltered;
  }
  
  public Deinterlacer getDeinterlacer() {
    return deinterlacer;
  }
  
  void updateCrcs(Checksum... idatCrcs) {
    for (Checksum idatCrca : idatCrcs) {
      if (idatCrca != null)
        idatCrca.update(getUnfilteredRow(), 1, getRowFilled() - 1);
    }
  }
  
  public void close() {
    super.close();
    rowUnfiltered = null;
    rowUnfilteredPrev = null;
  }
  




  public int[] getFilterUseStat()
  {
    return filterUseStat;
  }
}

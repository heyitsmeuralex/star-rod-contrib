package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.IDatChunkWriter;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjOutputException;
import java.io.OutputStream;


















public abstract class PixelsWriter
{
  private static final int IDAT_MAX_SIZE_DEFAULT = 32000;
  protected final ImageInfo imgInfo;
  protected final int buflen;
  protected final int bytesPixel;
  protected final int bytesRow;
  private CompressorStream compressorStream;
  protected int deflaterCompLevel = 6;
  protected int deflaterStrategy = 0;
  
  protected boolean initdone = false;
  


  protected FilterType filterType;
  


  private int[] filtersUsed = new int[5];
  

  private OutputStream os;
  
  private int idatMaxSize = 32000;
  

  protected int currentRow;
  


  public PixelsWriter(ImageInfo imgInfo)
  {
    this.imgInfo = imgInfo;
    bytesRow = bytesPerRow;
    buflen = (bytesRow + 1);
    bytesPixel = bytesPixel;
    currentRow = -1;
    filterType = FilterType.FILTER_DEFAULT;
  }
  



  public final void processRow(byte[] rowb)
  {
    if (!initdone)
      init();
    currentRow += 1;
    filterAndWrite(rowb);
  }
  
  protected void sendToCompressedStream(byte[] rowf) {
    compressorStream.write(rowf, 0, rowf.length);
    filtersUsed[rowf[0]] += 1;
  }
  










  protected abstract void filterAndWrite(byte[] paramArrayOfByte);
  










  protected final byte[] filterRowWithFilterType(FilterType _filterType, byte[] _rowb, byte[] _rowbprev, byte[] _rowf)
  {
    if (_filterType == FilterType.FILTER_NONE)
      _rowf = _rowb;
    _rowf[0] = ((byte)val);
    int i;
    int j; switch (1.$SwitchMap$ar$com$hjg$pngj$FilterType[_filterType.ordinal()])
    {
    case 1: 
      break;
    case 2: 
      for (i = 1; i <= bytesPixel; i++)
        _rowf[i] = ((byte)PngHelperInternal.filterRowPaeth(_rowb[i], 0, _rowbprev[i] & 0xFF, 0));
      j = 1; for (i = bytesPixel + 1; i <= bytesRow;) {
        _rowf[i] = ((byte)PngHelperInternal.filterRowPaeth(_rowb[i], _rowb[j] & 0xFF, _rowbprev[i] & 0xFF, _rowbprev[j] & 0xFF));i++;j++; continue;
        



        for (i = 1; i <= bytesPixel; i++)
          _rowf[i] = _rowb[i];
        j = 1; for (i = bytesPixel + 1; i <= bytesRow;) {
          _rowf[i] = ((byte)(_rowb[i] - _rowb[j]));i++;j++; continue;
          

          for (i = 1; i <= bytesPixel; i++)
            _rowf[i] = ((byte)(_rowb[i] - (_rowbprev[i] & 0xFF) / 2));
          j = 1; for (i = bytesPixel + 1; i <= bytesRow;) {
            _rowf[i] = ((byte)(_rowb[i] - ((_rowbprev[i] & 0xFF) + (_rowb[j] & 0xFF)) / 2));i++;j++; continue;
            

            for (i = 1; i <= bytesRow;) {
              _rowf[i] = ((byte)(_rowb[i] - _rowbprev[i]));i++; continue;
              

              throw new PngjOutputException("Filter type not recognized: " + _filterType);
            } } } } }
    return _rowf;
  }
  



  public abstract byte[] getRowb();
  



  protected final void init()
  {
    if (!initdone) {
      initParams();
      initdone = true;
    }
  }
  
  protected void initParams()
  {
    IDatChunkWriter idatWriter = new IDatChunkWriter(os, idatMaxSize);
    if (compressorStream == null) {
      compressorStream = new CompressorStreamDeflater(idatWriter, buflen, imgInfo.getTotalRawBytes(), deflaterCompLevel, deflaterStrategy);
    }
  }
  


  public void close()
  {
    if (compressorStream != null) {
      compressorStream.close();
    }
  }
  



  public void setDeflaterStrategy(Integer deflaterStrategy)
  {
    this.deflaterStrategy = deflaterStrategy.intValue();
  }
  


  public void setDeflaterCompLevel(Integer deflaterCompLevel)
  {
    this.deflaterCompLevel = deflaterCompLevel.intValue();
  }
  
  public Integer getDeflaterCompLevel() {
    return Integer.valueOf(deflaterCompLevel);
  }
  
  public final void setOs(OutputStream datStream)
  {
    os = datStream;
  }
  
  public OutputStream getOs() {
    return os;
  }
  
  public final FilterType getFilterType()
  {
    return filterType;
  }
  
  public final void setFilterType(FilterType filterType)
  {
    this.filterType = filterType;
  }
  
  public double getCompression()
  {
    return compressorStream.isDone() ? compressorStream.getCompressionRatio() : 1.0D;
  }
  
  public void setCompressorStream(CompressorStream compressorStream) {
    this.compressorStream = compressorStream;
  }
  
  public long getTotalBytesToWrite() {
    return imgInfo.getTotalRawBytes();
  }
  
  public boolean isDone() {
    return currentRow == imgInfo.rows - 1;
  }
  




  protected FilterType getDefaultFilter()
  {
    if ((imgInfo.indexed) || (imgInfo.bitDepth < 8))
      return FilterType.FILTER_NONE;
    if (imgInfo.getTotalPixels() < 1024L)
      return FilterType.FILTER_NONE;
    if (imgInfo.rows == 1)
      return FilterType.FILTER_SUB;
    if (imgInfo.cols == 1) {
      return FilterType.FILTER_UP;
    }
    return FilterType.FILTER_PAETH;
  }
  
  public final String getFiltersUsed()
  {
    return String.format("%d,%d,%d,%d,%d", new Object[] { Integer.valueOf((int)(filtersUsed[0] * 100.0D / imgInfo.rows + 0.5D)), Integer.valueOf((int)(filtersUsed[1] * 100.0D / imgInfo.rows + 0.5D)), Integer.valueOf((int)(filtersUsed[2] * 100.0D / imgInfo.rows + 0.5D)), Integer.valueOf((int)(filtersUsed[3] * 100.0D / imgInfo.rows + 0.5D)), Integer.valueOf((int)(filtersUsed[4] * 100.0D / imgInfo.rows + 0.5D)) });
  }
  


  public void setIdatMaxSize(int idatMaxSize)
  {
    this.idatMaxSize = idatMaxSize;
  }
}

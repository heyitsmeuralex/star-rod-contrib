package ar.com.hjg.pngj;

class RowInfo {
  public final ImageInfo imgInfo;
  public final Deinterlacer deinterlacer;
  public final boolean imode;
  int dY;
  int dX;
  int oY;
  int oX;
  int rowNseq;
  int rowNreal;
  int rowNsubImg;
  int rowsSubImg;
  int colsSubImg;
  int bytesRow;
  int pass;
  byte[] buf;
  int buflen;
  
  public RowInfo(ImageInfo imgInfo, Deinterlacer deinterlacer) {
    this.imgInfo = imgInfo;
    this.deinterlacer = deinterlacer;
    imode = (deinterlacer != null);
  }
  
  void update(int rowseq) {
    rowNseq = rowseq;
    if (imode) {
      pass = deinterlacer.getPass();
      dX = deinterlacer.dX;
      dY = deinterlacer.dY;
      oX = deinterlacer.oX;
      oY = deinterlacer.oY;
      rowNreal = deinterlacer.getCurrRowReal();
      rowNsubImg = deinterlacer.getCurrRowSubimg();
      rowsSubImg = deinterlacer.getRows();
      colsSubImg = deinterlacer.getCols();
      bytesRow = ((imgInfo.bitspPixel * colsSubImg + 7) / 8);
    } else {
      pass = 1;
      dX = (this.dY = 1);
      oX = (this.oY = 0);
      rowNreal = (this.rowNsubImg = rowseq);
      rowsSubImg = imgInfo.rows;
      colsSubImg = imgInfo.cols;
      bytesRow = imgInfo.bytesPerRow;
    }
  }
  
  void updateBuf(byte[] buf, int buflen) {
    this.buf = buf;
    this.buflen = buflen;
  }
}

package ar.com.hjg.pngj;

public class Deinterlacer { final ImageInfo imi;
  private int pass;
  private int rows;
  private int cols;
  int dY;
  int dX;
  int oY;
  int oX;
  int oXsamples;
  int dXsamples;
  private int currRowSubimg;
  private int currRowReal;
  private int currRowSeq;
  int totalRows;
  private boolean ended = false;
  
  public Deinterlacer(ImageInfo iminfo) {
    imi = iminfo;
    pass = 0;
    currRowSubimg = -1;
    currRowReal = -1;
    currRowSeq = 0;
    ended = false;
    totalRows = 0;
    setPass(1);
    setRow(0);
  }
  
  private void setRow(int n)
  {
    currRowSubimg = n;
    currRowReal = (n * dY + oY);
    if ((currRowReal < 0) || (currRowReal >= imi.rows)) {
      throw new PngjExceptionInternal("bad row - this should not happen");
    }
  }
  
  boolean nextRow() {
    currRowSeq += 1;
    if ((rows == 0) || (currRowSubimg >= rows - 1)) {
      if (pass == 7) {
        ended = true;
        return false;
      }
      setPass(pass + 1);
      if (rows == 0) {
        currRowSeq -= 1;
        return nextRow();
      }
      setRow(0);
    } else {
      setRow(currRowSubimg + 1);
    }
    return true;
  }
  
  boolean isEnded() {
    return ended;
  }
  
  void setPass(int p) {
    if (pass == p)
      return;
    pass = p;
    byte[] pp = paramsForPass(p);
    dX = pp[0];
    dY = pp[1];
    oX = pp[2];
    oY = pp[3];
    rows = (imi.rows > oY ? (imi.rows + dY - 1 - oY) / dY : 0);
    cols = (imi.cols > oX ? (imi.cols + dX - 1 - oX) / dX : 0);
    if (cols == 0)
      rows = 0;
    dXsamples = (dX * imi.channels);
    oXsamples = (oX * imi.channels);
  }
  
  static byte[] paramsForPass(int p) {
    switch (p) {
    case 1: 
      return new byte[] { 8, 8, 0, 0 };
    case 2: 
      return new byte[] { 8, 8, 4, 0 };
    case 3: 
      return new byte[] { 4, 8, 0, 4 };
    case 4: 
      return new byte[] { 4, 4, 2, 0 };
    case 5: 
      return new byte[] { 2, 4, 0, 2 };
    case 6: 
      return new byte[] { 2, 2, 1, 0 };
    case 7: 
      return new byte[] { 1, 2, 0, 1 };
    }
    throw new PngjExceptionInternal("bad interlace pass" + p);
  }
  



  int getCurrRowSubimg()
  {
    return currRowSubimg;
  }
  


  int getCurrRowReal()
  {
    return currRowReal;
  }
  


  int getPass()
  {
    return pass;
  }
  


  int getRows()
  {
    return rows;
  }
  


  int getCols()
  {
    return cols;
  }
  
  public int getPixelsToRead() {
    return getCols();
  }
  
  public int getBytesToRead() {
    return (imi.bitspPixel * getPixelsToRead() + 7) / 8;
  }
  
  public int getdY() {
    return dY;
  }
  


  public int getdX()
  {
    return dX;
  }
  
  public int getoY() {
    return oY;
  }
  


  public int getoX()
  {
    return oX;
  }
  
  public int getTotalRows() {
    if (totalRows == 0) {
      for (int p = 1; p <= 7; p++) {
        byte[] pp = paramsForPass(p);
        int rows = imi.rows > pp[3] ? (imi.rows + pp[1] - 1 - pp[3]) / pp[1] : 0;
        int cols = imi.cols > pp[2] ? (imi.cols + pp[0] - 1 - pp[2]) / pp[0] : 0;
        if ((rows > 0) && (cols > 0))
          totalRows += rows;
      }
    }
    return totalRows;
  }
  


  public long getTotalRawBytes()
  {
    long bytes = 0L;
    for (int p = 1; p <= 7; p++) {
      byte[] pp = paramsForPass(p);
      int rows = imi.rows > pp[3] ? (imi.rows + pp[1] - 1 - pp[3]) / pp[1] : 0;
      int cols = imi.cols > pp[2] ? (imi.cols + pp[0] - 1 - pp[2]) / pp[0] : 0;
      int bytesr = (imi.bitspPixel * cols + 7) / 8;
      if ((rows > 0) && (cols > 0))
        bytes += rows * (1L + bytesr);
    }
    return bytes;
  }
  
  public int getCurrRowSeq() {
    return currRowSeq;
  }
}

package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;














































































public class BlockRealMatrix
  extends AbstractRealMatrix
  implements Serializable
{
  public static final int BLOCK_SIZE = 52;
  private static final long serialVersionUID = 4991895511313664478L;
  private final double[][] blocks;
  private final int rows;
  private final int columns;
  private final int blockRows;
  private final int blockColumns;
  
  public BlockRealMatrix(int rows, int columns)
    throws IllegalArgumentException
  {
    super(rows, columns);
    this.rows = rows;
    this.columns = columns;
    

    blockRows = ((rows + 52 - 1) / 52);
    blockColumns = ((columns + 52 - 1) / 52);
    

    blocks = createBlocksLayout(rows, columns);
  }
  













  public BlockRealMatrix(double[][] rawData)
    throws IllegalArgumentException
  {
    this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
  }
  
















  public BlockRealMatrix(int rows, int columns, double[][] blockData, boolean copyArray)
    throws IllegalArgumentException
  {
    super(rows, columns);
    this.rows = rows;
    this.columns = columns;
    

    blockRows = ((rows + 52 - 1) / 52);
    blockColumns = ((columns + 52 - 1) / 52);
    
    if (copyArray)
    {
      blocks = new double[blockRows * blockColumns][];
    }
    else {
      blocks = blockData;
    }
    
    int index = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      for (int jBlock = 0; jBlock < blockColumns; index++) {
        if (blockData[index].length != iHeight * blockWidth(jBlock)) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.WRONG_BLOCK_LENGTH, new Object[] { Integer.valueOf(blockData[index].length), Integer.valueOf(iHeight * blockWidth(jBlock)) });
        }
        

        if (copyArray) {
          blocks[index] = ((double[])blockData[index].clone());
        }
        jBlock++;
      }
    }
  }
  































  public static double[][] toBlocksLayout(double[][] rawData)
    throws IllegalArgumentException
  {
    int rows = rawData.length;
    int columns = rawData[0].length;
    int blockRows = (rows + 52 - 1) / 52;
    int blockColumns = (columns + 52 - 1) / 52;
    

    for (int i = 0; i < rawData.length; i++) {
      int length = rawData[i].length;
      if (length != columns) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(columns), Integer.valueOf(length) });
      }
    }
    



    double[][] blocks = new double[blockRows * blockColumns][];
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      int iHeight = pEnd - pStart;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, columns);
        int jWidth = qEnd - qStart;
        

        double[] block = new double[iHeight * jWidth];
        blocks[blockIndex] = block;
        

        int index = 0;
        for (int p = pStart; p < pEnd; p++) {
          System.arraycopy(rawData[p], qStart, block, index, jWidth);
          index += jWidth;
        }
        
        blockIndex++;
      }
    }
    

    return blocks;
  }
  













  public static double[][] createBlocksLayout(int rows, int columns)
  {
    int blockRows = (rows + 52 - 1) / 52;
    int blockColumns = (columns + 52 - 1) / 52;
    
    double[][] blocks = new double[blockRows * blockColumns][];
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      int iHeight = pEnd - pStart;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, columns);
        int jWidth = qEnd - qStart;
        blocks[blockIndex] = new double[iHeight * jWidth];
        blockIndex++;
      }
    }
    
    return blocks;
  }
  


  public BlockRealMatrix createMatrix(int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    return new BlockRealMatrix(rowDimension, columnDimension);
  }
  



  public BlockRealMatrix copy()
  {
    BlockRealMatrix copied = new BlockRealMatrix(rows, columns);
    

    for (int i = 0; i < blocks.length; i++) {
      System.arraycopy(blocks[i], 0, blocks[i], 0, blocks[i].length);
    }
    
    return copied;
  }
  

  public BlockRealMatrix add(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return add((BlockRealMatrix)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkAdditionCompatible(this, m);
      
      BlockRealMatrix out = new BlockRealMatrix(rows, columns);
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {

          double[] outBlock = blocks[blockIndex];
          double[] tBlock = blocks[blockIndex];
          int pStart = iBlock * 52;
          int pEnd = FastMath.min(pStart + 52, rows);
          int qStart = jBlock * 52;
          int qEnd = FastMath.min(qStart + 52, columns);
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            for (int q = qStart; q < qEnd; q++) {
              tBlock[k] += m.getEntry(p, q);
              k++;
            }
          }
          

          blockIndex++;
        }
      }
      

      return out;
    }
  }
  









  public BlockRealMatrix add(BlockRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkAdditionCompatible(this, m);
    
    BlockRealMatrix out = new BlockRealMatrix(rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      double[] outBlock = blocks[blockIndex];
      double[] tBlock = blocks[blockIndex];
      double[] mBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        tBlock[k] += mBlock[k];
      }
    }
    
    return out;
  }
  

  public BlockRealMatrix subtract(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return subtract((BlockRealMatrix)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkSubtractionCompatible(this, m);
      
      BlockRealMatrix out = new BlockRealMatrix(rows, columns);
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {

          double[] outBlock = blocks[blockIndex];
          double[] tBlock = blocks[blockIndex];
          int pStart = iBlock * 52;
          int pEnd = FastMath.min(pStart + 52, rows);
          int qStart = jBlock * 52;
          int qEnd = FastMath.min(qStart + 52, columns);
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            for (int q = qStart; q < qEnd; q++) {
              tBlock[k] -= m.getEntry(p, q);
              k++;
            }
          }
          

          blockIndex++;
        }
      }
      

      return out;
    }
  }
  









  public BlockRealMatrix subtract(BlockRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkSubtractionCompatible(this, m);
    
    BlockRealMatrix out = new BlockRealMatrix(rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      double[] outBlock = blocks[blockIndex];
      double[] tBlock = blocks[blockIndex];
      double[] mBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        tBlock[k] -= mBlock[k];
      }
    }
    
    return out;
  }
  



  public BlockRealMatrix scalarAdd(double d)
    throws IllegalArgumentException
  {
    BlockRealMatrix out = new BlockRealMatrix(rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      double[] outBlock = blocks[blockIndex];
      double[] tBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        tBlock[k] += d;
      }
    }
    
    return out;
  }
  



  public RealMatrix scalarMultiply(double d)
    throws IllegalArgumentException
  {
    BlockRealMatrix out = new BlockRealMatrix(rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      double[] outBlock = blocks[blockIndex];
      double[] tBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        tBlock[k] *= d;
      }
    }
    
    return out;
  }
  

  public BlockRealMatrix multiply(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return multiply((BlockRealMatrix)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkMultiplicationCompatible(this, m);
      
      BlockRealMatrix out = new BlockRealMatrix(rows, m.getColumnDimension());
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++)
      {
        int pStart = iBlock * 52;
        int pEnd = FastMath.min(pStart + 52, rows);
        
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {
          int qStart = jBlock * 52;
          int qEnd = FastMath.min(qStart + 52, m.getColumnDimension());
          

          double[] outBlock = blocks[blockIndex];
          

          for (int kBlock = 0; kBlock < blockColumns; kBlock++) {
            int kWidth = blockWidth(kBlock);
            double[] tBlock = blocks[(iBlock * blockColumns + kBlock)];
            int rStart = kBlock * 52;
            int k = 0;
            for (int p = pStart; p < pEnd; p++) {
              int lStart = (p - pStart) * kWidth;
              int lEnd = lStart + kWidth;
              for (int q = qStart; q < qEnd; q++) {
                double sum = 0.0D;
                int r = rStart;
                for (int l = lStart; l < lEnd; l++) {
                  sum += tBlock[l] * m.getEntry(r, q);
                  r++;
                }
                outBlock[k] += sum;
                k++;
              }
            }
          }
          

          blockIndex++;
        }
      }
      

      return out;
    }
  }
  









  public BlockRealMatrix multiply(BlockRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkMultiplicationCompatible(this, m);
    
    BlockRealMatrix out = new BlockRealMatrix(rows, columns);
    

    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++)
    {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int jWidth = out.blockWidth(jBlock);
        int jWidth2 = jWidth + jWidth;
        int jWidth3 = jWidth2 + jWidth;
        int jWidth4 = jWidth3 + jWidth;
        

        double[] outBlock = blocks[blockIndex];
        

        for (int kBlock = 0; kBlock < blockColumns; kBlock++) {
          int kWidth = blockWidth(kBlock);
          double[] tBlock = blocks[(iBlock * blockColumns + kBlock)];
          double[] mBlock = blocks[(kBlock * blockColumns + jBlock)];
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            int lStart = (p - pStart) * kWidth;
            int lEnd = lStart + kWidth;
            for (int nStart = 0; nStart < jWidth; nStart++) {
              double sum = 0.0D;
              int l = lStart;
              int n = nStart;
              while (l < lEnd - 3) {
                sum += tBlock[l] * mBlock[n] + tBlock[(l + 1)] * mBlock[(n + jWidth)] + tBlock[(l + 2)] * mBlock[(n + jWidth2)] + tBlock[(l + 3)] * mBlock[(n + jWidth3)];
                


                l += 4;
                n += jWidth4;
              }
              while (l < lEnd) {
                sum += tBlock[(l++)] * mBlock[n];
                n += jWidth;
              }
              outBlock[k] += sum;
              k++;
            }
          }
        }
        

        blockIndex++;
      }
    }
    

    return out;
  }
  



  public double[][] getData()
  {
    double[][] data = new double[getRowDimension()][getColumnDimension()];
    int lastColumns = columns - (blockColumns - 1) * 52;
    
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      int regularPos = 0;
      int lastPos = 0;
      for (int p = pStart; p < pEnd; p++) {
        double[] dataP = data[p];
        int blockIndex = iBlock * blockColumns;
        int dataPos = 0;
        for (int jBlock = 0; jBlock < blockColumns - 1; jBlock++) {
          System.arraycopy(blocks[(blockIndex++)], regularPos, dataP, dataPos, 52);
          dataPos += 52;
        }
        System.arraycopy(blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
        regularPos += 52;
        lastPos += lastColumns;
      }
    }
    
    return data;
  }
  


  public double getNorm()
  {
    double[] colSums = new double[52];
    double maxColSum = 0.0D;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      Arrays.fill(colSums, 0, jWidth, 0.0D);
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        int iHeight = blockHeight(iBlock);
        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        for (int j = 0; j < jWidth; j++) {
          double sum = 0.0D;
          for (int i = 0; i < iHeight; i++) {
            sum += FastMath.abs(block[(i * jWidth + j)]);
          }
          colSums[j] += sum;
        }
      }
      for (int j = 0; j < jWidth; j++) {
        maxColSum = FastMath.max(maxColSum, colSums[j]);
      }
    }
    return maxColSum;
  }
  

  public double getFrobeniusNorm()
  {
    double sum2 = 0.0D;
    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      for (double entry : blocks[blockIndex]) {
        sum2 += entry * entry;
      }
    }
    return FastMath.sqrt(sum2);
  }
  




  public BlockRealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    

    BlockRealMatrix out = new BlockRealMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
    


    int blockStartRow = startRow / 52;
    int rowsShift = startRow % 52;
    int blockStartColumn = startColumn / 52;
    int columnsShift = startColumn % 52;
    

    int pBlock = blockStartRow;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = out.blockHeight(iBlock);
      int qBlock = blockStartColumn;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int jWidth = out.blockWidth(jBlock);
        

        int outIndex = iBlock * blockColumns + jBlock;
        double[] outBlock = blocks[outIndex];
        int index = pBlock * blockColumns + qBlock;
        int width = blockWidth(qBlock);
        
        int heightExcess = iHeight + rowsShift - 52;
        int widthExcess = jWidth + columnsShift - 52;
        if (heightExcess > 0)
        {
          if (widthExcess > 0)
          {
            int width2 = blockWidth(qBlock + 1);
            copyBlockPart(blocks[index], width, rowsShift, 52, columnsShift, 52, outBlock, jWidth, 0, 0);
            


            copyBlockPart(blocks[(index + 1)], width2, rowsShift, 52, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
            


            copyBlockPart(blocks[(index + blockColumns)], width, 0, heightExcess, columnsShift, 52, outBlock, jWidth, iHeight - heightExcess, 0);
            


            copyBlockPart(blocks[(index + blockColumns + 1)], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);

          }
          else
          {

            copyBlockPart(blocks[index], width, rowsShift, 52, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
            


            copyBlockPart(blocks[(index + blockColumns)], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);

          }
          


        }
        else if (widthExcess > 0)
        {
          int width2 = blockWidth(qBlock + 1);
          copyBlockPart(blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 52, outBlock, jWidth, 0, 0);
          


          copyBlockPart(blocks[(index + 1)], width2, rowsShift, iHeight + rowsShift, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);

        }
        else
        {

          copyBlockPart(blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
        }
        




        qBlock++;
      }
      

      pBlock++;
    }
    

    return out;
  }
  



















  private void copyBlockPart(double[] srcBlock, int srcWidth, int srcStartRow, int srcEndRow, int srcStartColumn, int srcEndColumn, double[] dstBlock, int dstWidth, int dstStartRow, int dstStartColumn)
  {
    int length = srcEndColumn - srcStartColumn;
    int srcPos = srcStartRow * srcWidth + srcStartColumn;
    int dstPos = dstStartRow * dstWidth + dstStartColumn;
    for (int srcRow = srcStartRow; srcRow < srcEndRow; srcRow++) {
      System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
      srcPos += srcWidth;
      dstPos += dstWidth;
    }
  }
  



  public void setSubMatrix(double[][] subMatrix, int row, int column)
    throws MatrixIndexException
  {
    int refLength = subMatrix[0].length;
    if (refLength < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    int endRow = row + subMatrix.length - 1;
    int endColumn = column + refLength - 1;
    MatrixUtils.checkSubMatrixIndex(this, row, endRow, column, endColumn);
    for (double[] subRow : subMatrix) {
      if (subRow.length != refLength) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(refLength), Integer.valueOf(subRow.length) });
      }
    }
    



    int blockStartRow = row / 52;
    int blockEndRow = (endRow + 52) / 52;
    int blockStartColumn = column / 52;
    int blockEndColumn = (endColumn + 52) / 52;
    

    for (int iBlock = blockStartRow; iBlock < blockEndRow; iBlock++) {
      int iHeight = blockHeight(iBlock);
      int firstRow = iBlock * 52;
      int iStart = FastMath.max(row, firstRow);
      int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
      
      for (int jBlock = blockStartColumn; jBlock < blockEndColumn; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int firstColumn = jBlock * 52;
        int jStart = FastMath.max(column, firstColumn);
        int jEnd = FastMath.min(endColumn + 1, firstColumn + jWidth);
        int jLength = jEnd - jStart;
        

        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        for (int i = iStart; i < iEnd; i++) {
          System.arraycopy(subMatrix[(i - row)], jStart - column, block, (i - firstRow) * jWidth + (jStart - firstColumn), jLength);
        }
      }
    }
  }
  





  public BlockRealMatrix getRowMatrix(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    BlockRealMatrix out = new BlockRealMatrix(1, columns);
    

    int iBlock = row / 52;
    int iRow = row - iBlock * 52;
    int outBlockIndex = 0;
    int outIndex = 0;
    double[] outBlock = blocks[outBlockIndex];
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      int available = outBlock.length - outIndex;
      if (jWidth > available) {
        System.arraycopy(block, iRow * jWidth, outBlock, outIndex, available);
        outBlock = blocks[(++outBlockIndex)];
        System.arraycopy(block, iRow * jWidth, outBlock, 0, jWidth - available);
        outIndex = jWidth - available;
      } else {
        System.arraycopy(block, iRow * jWidth, outBlock, outIndex, jWidth);
        outIndex += jWidth;
      }
    }
    
    return out;
  }
  

  public void setRowMatrix(int row, RealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setRowMatrix(row, (BlockRealMatrix)matrix);
    } catch (ClassCastException cce) {
      super.setRowMatrix(row, matrix);
    }
  }
  











  public void setRowMatrix(int row, BlockRealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    if ((matrix.getRowDimension() != 1) || (matrix.getColumnDimension() != nCols))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    




    int iBlock = row / 52;
    int iRow = row - iBlock * 52;
    int mBlockIndex = 0;
    int mIndex = 0;
    double[] mBlock = blocks[mBlockIndex];
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      int available = mBlock.length - mIndex;
      if (jWidth > available) {
        System.arraycopy(mBlock, mIndex, block, iRow * jWidth, available);
        mBlock = blocks[(++mBlockIndex)];
        System.arraycopy(mBlock, 0, block, iRow * jWidth, jWidth - available);
        mIndex = jWidth - available;
      } else {
        System.arraycopy(mBlock, mIndex, block, iRow * jWidth, jWidth);
        mIndex += jWidth;
      }
    }
  }
  



  public BlockRealMatrix getColumnMatrix(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    BlockRealMatrix out = new BlockRealMatrix(rows, 1);
    

    int jBlock = column / 52;
    int jColumn = column - jBlock * 52;
    int jWidth = blockWidth(jBlock);
    int outBlockIndex = 0;
    int outIndex = 0;
    double[] outBlock = blocks[outBlockIndex];
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        if (outIndex >= outBlock.length) {
          outBlock = blocks[(++outBlockIndex)];
          outIndex = 0;
        }
        outBlock[(outIndex++)] = block[(i * jWidth + jColumn)];
      }
    }
    
    return out;
  }
  

  public void setColumnMatrix(int column, RealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setColumnMatrix(column, (BlockRealMatrix)matrix);
    } catch (ClassCastException cce) {
      super.setColumnMatrix(column, matrix);
    }
  }
  











  void setColumnMatrix(int column, BlockRealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    if ((matrix.getRowDimension() != nRows) || (matrix.getColumnDimension() != 1))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    




    int jBlock = column / 52;
    int jColumn = column - jBlock * 52;
    int jWidth = blockWidth(jBlock);
    int mBlockIndex = 0;
    int mIndex = 0;
    double[] mBlock = blocks[mBlockIndex];
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        if (mIndex >= mBlock.length) {
          mBlock = blocks[(++mBlockIndex)];
          mIndex = 0;
        }
        block[(i * jWidth + jColumn)] = mBlock[(mIndex++)];
      }
    }
  }
  



  public RealVector getRowVector(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    double[] outData = new double[columns];
    

    int iBlock = row / 52;
    int iRow = row - iBlock * 52;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(block, iRow * jWidth, outData, outIndex, jWidth);
      outIndex += jWidth;
    }
    
    return new ArrayRealVector(outData, false);
  }
  

  public void setRowVector(int row, RealVector vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setRow(row, ((ArrayRealVector)vector).getDataRef());
    } catch (ClassCastException cce) {
      super.setRowVector(row, vector);
    }
  }
  


  public RealVector getColumnVector(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    double[] outData = new double[rows];
    

    int jBlock = column / 52;
    int jColumn = column - jBlock * 52;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        outData[(outIndex++)] = block[(i * jWidth + jColumn)];
      }
    }
    
    return new ArrayRealVector(outData, false);
  }
  

  public void setColumnVector(int column, RealVector vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setColumn(column, ((ArrayRealVector)vector).getDataRef());
    } catch (ClassCastException cce) {
      super.setColumnVector(column, vector);
    }
  }
  


  public double[] getRow(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    double[] out = new double[columns];
    

    int iBlock = row / 52;
    int iRow = row - iBlock * 52;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(block, iRow * jWidth, out, outIndex, jWidth);
      outIndex += jWidth;
    }
    
    return out;
  }
  



  public void setRow(int row, double[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    if (array.length != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    



    int iBlock = row / 52;
    int iRow = row - iBlock * 52;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(array, outIndex, block, iRow * jWidth, jWidth);
      outIndex += jWidth;
    }
  }
  



  public double[] getColumn(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    double[] out = new double[rows];
    

    int jBlock = column / 52;
    int jColumn = column - jBlock * 52;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        out[(outIndex++)] = block[(i * jWidth + jColumn)];
      }
    }
    
    return out;
  }
  



  public void setColumn(int column, double[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    if (array.length != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    



    int jBlock = column / 52;
    int jColumn = column - jBlock * 52;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      double[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        block[(i * jWidth + jColumn)] = array[(outIndex++)];
      }
    }
  }
  

  public double getEntry(int row, int column)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 52;
      int jBlock = column / 52;
      int k = (row - iBlock * 52) * blockWidth(jBlock) + (column - jBlock * 52);
      
      return blocks[(iBlock * blockColumns + jBlock)][k];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void setEntry(int row, int column, double value)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 52;
      int jBlock = column / 52;
      int k = (row - iBlock * 52) * blockWidth(jBlock) + (column - jBlock * 52);
      
      blocks[(iBlock * blockColumns + jBlock)][k] = value;
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void addToEntry(int row, int column, double increment)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 52;
      int jBlock = column / 52;
      int k = (row - iBlock * 52) * blockWidth(jBlock) + (column - jBlock * 52);
      
      blocks[(iBlock * blockColumns + jBlock)][k] += increment;
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void multiplyEntry(int row, int column, double factor)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 52;
      int jBlock = column / 52;
      int k = (row - iBlock * 52) * blockWidth(jBlock) + (column - jBlock * 52);
      
      blocks[(iBlock * blockColumns + jBlock)][k] *= factor;
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  




  public BlockRealMatrix transpose()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    BlockRealMatrix out = new BlockRealMatrix(nCols, nRows);
    

    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockColumns; iBlock++) {
      for (int jBlock = 0; jBlock < blockRows; jBlock++)
      {

        double[] outBlock = blocks[blockIndex];
        double[] tBlock = blocks[(jBlock * blockColumns + iBlock)];
        int pStart = iBlock * 52;
        int pEnd = FastMath.min(pStart + 52, columns);
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, rows);
        int k = 0;
        for (int p = pStart; p < pEnd; p++) {
          int lInc = pEnd - pStart;
          int l = p - pStart;
          for (int q = qStart; q < qEnd; q++) {
            outBlock[k] = tBlock[l];
            k++;
            l += lInc;
          }
        }
        

        blockIndex++;
      }
    }
    

    return out;
  }
  


  public int getRowDimension()
  {
    return rows;
  }
  

  public int getColumnDimension()
  {
    return columns;
  }
  


  public double[] operate(double[] v)
    throws IllegalArgumentException
  {
    if (v.length != columns) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(columns) });
    }
    

    double[] out = new double[rows];
    

    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, columns);
        int k = 0;
        for (int p = pStart; p < pEnd; p++) {
          double sum = 0.0D;
          int q = qStart;
          while (q < qEnd - 3) {
            sum += block[k] * v[q] + block[(k + 1)] * v[(q + 1)] + block[(k + 2)] * v[(q + 2)] + block[(k + 3)] * v[(q + 3)];
            


            k += 4;
            q += 4;
          }
          while (q < qEnd) {
            sum += block[(k++)] * v[(q++)];
          }
          out[p] += sum;
        }
      }
    }
    
    return out;
  }
  



  public double[] preMultiply(double[] v)
    throws IllegalArgumentException
  {
    if (v.length != rows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(rows) });
    }
    

    double[] out = new double[columns];
    

    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      int jWidth2 = jWidth + jWidth;
      int jWidth3 = jWidth2 + jWidth;
      int jWidth4 = jWidth3 + jWidth;
      int qStart = jBlock * 52;
      int qEnd = FastMath.min(qStart + 52, columns);
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        int pStart = iBlock * 52;
        int pEnd = FastMath.min(pStart + 52, rows);
        for (int q = qStart; q < qEnd; q++) {
          int k = q - qStart;
          double sum = 0.0D;
          int p = pStart;
          while (p < pEnd - 3) {
            sum += block[k] * v[p] + block[(k + jWidth)] * v[(p + 1)] + block[(k + jWidth2)] * v[(p + 2)] + block[(k + jWidth3)] * v[(p + 3)];
            


            k += jWidth4;
            p += 4;
          }
          while (p < pEnd) {
            sum += block[k] * v[(p++)];
            k += jWidth;
          }
          out[q] += sum;
        }
      }
    }
    
    return out;
  }
  


  public double walkInRowOrder(RealMatrixChangingVisitor visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int qStart = jBlock * 52;
          int qEnd = FastMath.min(qStart + 52, columns);
          double[] block = blocks[(iBlock * blockColumns + jBlock)];
          int k = (p - pStart) * jWidth;
          for (int q = qStart; q < qEnd; q++) {
            block[k] = visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  

  public double walkInRowOrder(RealMatrixPreservingVisitor visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int qStart = jBlock * 52;
          int qEnd = FastMath.min(qStart + 52, columns);
          double[] block = blocks[(iBlock * blockColumns + jBlock)];
          int k = (p - pStart) * jWidth;
          for (int q = qStart; q < qEnd; q++) {
            visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  



  public double walkInRowOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; iBlock++) {
      int p0 = iBlock * 52;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int q0 = jBlock * 52;
          int qStart = FastMath.max(startColumn, q0);
          int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
          double[] block = blocks[(iBlock * blockColumns + jBlock)];
          int k = (p - p0) * jWidth + qStart - q0;
          for (int q = qStart; q < qEnd; q++) {
            block[k] = visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  



  public double walkInRowOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; iBlock++) {
      int p0 = iBlock * 52;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int q0 = jBlock * 52;
          int qStart = FastMath.max(startColumn, q0);
          int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
          double[] block = blocks[(iBlock * blockColumns + jBlock)];
          int k = (p - p0) * jWidth + qStart - q0;
          for (int q = qStart; q < qEnd; q++) {
            visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  

  public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, columns);
        double[] block = blocks[blockIndex];
        int k = 0;
        for (int p = pStart; p < pEnd; p++) {
          for (int q = qStart; q < qEnd; q++) {
            block[k] = visitor.visit(p, q, block[k]);
            k++;
          }
        }
        blockIndex++;
      }
    }
    return visitor.end();
  }
  

  public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 52;
      int pEnd = FastMath.min(pStart + 52, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 52;
        int qEnd = FastMath.min(qStart + 52, columns);
        double[] block = blocks[blockIndex];
        int k = 0;
        for (int p = pStart; p < pEnd; p++) {
          for (int q = qStart; q < qEnd; q++) {
            visitor.visit(p, q, block[k]);
            k++;
          }
        }
        blockIndex++;
      }
    }
    return visitor.end();
  }
  



  public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; iBlock++) {
      int p0 = iBlock * 52;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
      for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int q0 = jBlock * 52;
        int qStart = FastMath.max(startColumn, q0);
        int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        for (int p = pStart; p < pEnd; p++) {
          int k = (p - p0) * jWidth + qStart - q0;
          for (int q = qStart; q < qEnd; q++) {
            block[k] = visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  



  public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; iBlock++) {
      int p0 = iBlock * 52;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
      for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int q0 = jBlock * 52;
        int qStart = FastMath.max(startColumn, q0);
        int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
        double[] block = blocks[(iBlock * blockColumns + jBlock)];
        for (int p = pStart; p < pEnd; p++) {
          int k = (p - p0) * jWidth + qStart - q0;
          for (int q = qStart; q < qEnd; q++) {
            visitor.visit(p, q, block[k]);
            k++;
          }
        }
      }
    }
    return visitor.end();
  }
  




  private int blockHeight(int blockRow)
  {
    return blockRow == blockRows - 1 ? rows - blockRow * 52 : 52;
  }
  




  private int blockWidth(int blockColumn)
  {
    return blockColumn == blockColumns - 1 ? columns - blockColumn * 52 : 52;
  }
}

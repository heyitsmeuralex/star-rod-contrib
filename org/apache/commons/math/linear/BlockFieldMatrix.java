package org.apache.commons.math.linear;

import java.io.Serializable;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
















































































public class BlockFieldMatrix<T extends FieldElement<T>>
  extends AbstractFieldMatrix<T>
  implements Serializable
{
  public static final int BLOCK_SIZE = 36;
  private static final long serialVersionUID = -4602336630143123183L;
  private final T[][] blocks;
  private final int rows;
  private final int columns;
  private final int blockRows;
  private final int blockColumns;
  
  public BlockFieldMatrix(Field<T> field, int rows, int columns)
    throws IllegalArgumentException
  {
    super(field, rows, columns);
    this.rows = rows;
    this.columns = columns;
    

    blockRows = ((rows + 36 - 1) / 36);
    blockColumns = ((columns + 36 - 1) / 36);
    

    blocks = createBlocksLayout(field, rows, columns);
  }
  













  public BlockFieldMatrix(T[][] rawData)
    throws IllegalArgumentException
  {
    this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
  }
  
















  public BlockFieldMatrix(int rows, int columns, T[][] blockData, boolean copyArray)
    throws IllegalArgumentException
  {
    super(extractField(blockData), rows, columns);
    this.rows = rows;
    this.columns = columns;
    

    blockRows = ((rows + 36 - 1) / 36);
    blockColumns = ((columns + 36 - 1) / 36);
    
    if (copyArray)
    {
      blocks = buildArray(getField(), blockRows * blockColumns, -1);
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
          blocks[index] = ((FieldElement[])blockData[index].clone());
        }
        jBlock++;
      }
    }
  }
  

































  public static <T extends FieldElement<T>> T[][] toBlocksLayout(T[][] rawData)
    throws IllegalArgumentException
  {
    int rows = rawData.length;
    int columns = rawData[0].length;
    int blockRows = (rows + 36 - 1) / 36;
    int blockColumns = (columns + 36 - 1) / 36;
    

    for (int i = 0; i < rawData.length; i++) {
      int length = rawData[i].length;
      if (length != columns) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(columns), Integer.valueOf(length) });
      }
    }
    



    Field<T> field = extractField(rawData);
    T[][] blocks = buildArray(field, blockRows * blockColumns, -1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      int iHeight = pEnd - pStart;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, columns);
        int jWidth = qEnd - qStart;
        

        T[] block = buildArray(field, iHeight * jWidth);
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
  

















  public static <T extends FieldElement<T>> T[][] createBlocksLayout(Field<T> field, int rows, int columns)
  {
    int blockRows = (rows + 36 - 1) / 36;
    int blockColumns = (columns + 36 - 1) / 36;
    
    T[][] blocks = buildArray(field, blockRows * blockColumns, -1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      int iHeight = pEnd - pStart;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, columns);
        int jWidth = qEnd - qStart;
        blocks[blockIndex] = buildArray(field, iHeight * jWidth);
        blockIndex++;
      }
    }
    
    return blocks;
  }
  


  public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    return new BlockFieldMatrix(getField(), rowDimension, columnDimension);
  }
  



  public FieldMatrix<T> copy()
  {
    BlockFieldMatrix<T> copied = new BlockFieldMatrix(getField(), rows, columns);
    

    for (int i = 0; i < blocks.length; i++) {
      System.arraycopy(blocks[i], 0, blocks[i], 0, blocks[i].length);
    }
    
    return copied;
  }
  

  public FieldMatrix<T> add(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    try
    {
      return add((BlockFieldMatrix)m);
    }
    catch (ClassCastException cce)
    {
      checkAdditionCompatible(m);
      
      BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {

          T[] outBlock = blocks[blockIndex];
          T[] tBlock = blocks[blockIndex];
          int pStart = iBlock * 36;
          int pEnd = FastMath.min(pStart + 36, rows);
          int qStart = jBlock * 36;
          int qEnd = FastMath.min(qStart + 36, columns);
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            for (int q = qStart; q < qEnd; q++) {
              outBlock[k] = ((FieldElement)tBlock[k].add(m.getEntry(p, q)));
              k++;
            }
          }
          

          blockIndex++;
        }
      }
      

      return out;
    }
  }
  









  public BlockFieldMatrix<T> add(BlockFieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkAdditionCompatible(m);
    
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      T[] outBlock = blocks[blockIndex];
      T[] tBlock = blocks[blockIndex];
      T[] mBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        outBlock[k] = ((FieldElement)tBlock[k].add(mBlock[k]));
      }
    }
    
    return out;
  }
  

  public FieldMatrix<T> subtract(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    try
    {
      return subtract((BlockFieldMatrix)m);
    }
    catch (ClassCastException cce)
    {
      checkSubtractionCompatible(m);
      
      BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {

          T[] outBlock = blocks[blockIndex];
          T[] tBlock = blocks[blockIndex];
          int pStart = iBlock * 36;
          int pEnd = FastMath.min(pStart + 36, rows);
          int qStart = jBlock * 36;
          int qEnd = FastMath.min(qStart + 36, columns);
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            for (int q = qStart; q < qEnd; q++) {
              outBlock[k] = ((FieldElement)tBlock[k].subtract(m.getEntry(p, q)));
              k++;
            }
          }
          

          blockIndex++;
        }
      }
      

      return out;
    }
  }
  









  public BlockFieldMatrix<T> subtract(BlockFieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkSubtractionCompatible(m);
    
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      T[] outBlock = blocks[blockIndex];
      T[] tBlock = blocks[blockIndex];
      T[] mBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        outBlock[k] = ((FieldElement)tBlock[k].subtract(mBlock[k]));
      }
    }
    
    return out;
  }
  



  public FieldMatrix<T> scalarAdd(T d)
    throws IllegalArgumentException
  {
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      T[] outBlock = blocks[blockIndex];
      T[] tBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        outBlock[k] = ((FieldElement)tBlock[k].add(d));
      }
    }
    
    return out;
  }
  



  public FieldMatrix<T> scalarMultiply(T d)
    throws IllegalArgumentException
  {
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
    

    for (int blockIndex = 0; blockIndex < blocks.length; blockIndex++) {
      T[] outBlock = blocks[blockIndex];
      T[] tBlock = blocks[blockIndex];
      for (int k = 0; k < outBlock.length; k++) {
        outBlock[k] = ((FieldElement)tBlock[k].multiply(d));
      }
    }
    
    return out;
  }
  

  public FieldMatrix<T> multiply(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    try
    {
      return multiply((BlockFieldMatrix)m);
    }
    catch (ClassCastException cce)
    {
      checkMultiplicationCompatible(m);
      
      BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, m.getColumnDimension());
      T zero = (FieldElement)getField().getZero();
      

      int blockIndex = 0;
      for (int iBlock = 0; iBlock < blockRows; iBlock++)
      {
        int pStart = iBlock * 36;
        int pEnd = FastMath.min(pStart + 36, rows);
        
        for (int jBlock = 0; jBlock < blockColumns; jBlock++)
        {
          int qStart = jBlock * 36;
          int qEnd = FastMath.min(qStart + 36, m.getColumnDimension());
          

          T[] outBlock = blocks[blockIndex];
          

          for (int kBlock = 0; kBlock < blockColumns; kBlock++) {
            int kWidth = blockWidth(kBlock);
            T[] tBlock = blocks[(iBlock * blockColumns + kBlock)];
            int rStart = kBlock * 36;
            int k = 0;
            for (int p = pStart; p < pEnd; p++) {
              int lStart = (p - pStart) * kWidth;
              int lEnd = lStart + kWidth;
              for (int q = qStart; q < qEnd; q++) {
                T sum = zero;
                int r = rStart;
                for (int l = lStart; l < lEnd; l++) {
                  sum = (FieldElement)sum.add(tBlock[l].multiply(m.getEntry(r, q)));
                  r++;
                }
                outBlock[k] = ((FieldElement)outBlock[k].add(sum));
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
  









  public BlockFieldMatrix<T> multiply(BlockFieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkMultiplicationCompatible(m);
    
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, columns);
    T zero = (FieldElement)getField().getZero();
    

    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++)
    {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int jWidth = out.blockWidth(jBlock);
        int jWidth2 = jWidth + jWidth;
        int jWidth3 = jWidth2 + jWidth;
        int jWidth4 = jWidth3 + jWidth;
        

        T[] outBlock = blocks[blockIndex];
        

        for (int kBlock = 0; kBlock < blockColumns; kBlock++) {
          int kWidth = blockWidth(kBlock);
          T[] tBlock = blocks[(iBlock * blockColumns + kBlock)];
          T[] mBlock = blocks[(kBlock * blockColumns + jBlock)];
          int k = 0;
          for (int p = pStart; p < pEnd; p++) {
            int lStart = (p - pStart) * kWidth;
            int lEnd = lStart + kWidth;
            for (int nStart = 0; nStart < jWidth; nStart++) {
              T sum = zero;
              int l = lStart;
              int n = nStart;
              while (l < lEnd - 3) {
                sum = (FieldElement)((FieldElement)((FieldElement)((FieldElement)sum.add(tBlock[l].multiply(mBlock[n]))).add(tBlock[(l + 1)].multiply(mBlock[(n + jWidth)]))).add(tBlock[(l + 2)].multiply(mBlock[(n + jWidth2)]))).add(tBlock[(l + 3)].multiply(mBlock[(n + jWidth3)]));
                



                l += 4;
                n += jWidth4;
              }
              while (l < lEnd) {
                sum = (FieldElement)sum.add(tBlock[(l++)].multiply(mBlock[n]));
                n += jWidth;
              }
              outBlock[k] = ((FieldElement)outBlock[k].add(sum));
              k++;
            }
          }
        }
        

        blockIndex++;
      }
    }
    

    return out;
  }
  



  public T[][] getData()
  {
    T[][] data = buildArray(getField(), getRowDimension(), getColumnDimension());
    int lastColumns = columns - (blockColumns - 1) * 36;
    
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      int regularPos = 0;
      int lastPos = 0;
      for (int p = pStart; p < pEnd; p++) {
        T[] dataP = data[p];
        int blockIndex = iBlock * blockColumns;
        int dataPos = 0;
        for (int jBlock = 0; jBlock < blockColumns - 1; jBlock++) {
          System.arraycopy(blocks[(blockIndex++)], regularPos, dataP, dataPos, 36);
          dataPos += 36;
        }
        System.arraycopy(blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
        regularPos += 36;
        lastPos += lastColumns;
      }
    }
    
    return data;
  }
  





  public FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    

    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), endRow - startRow + 1, endColumn - startColumn + 1);
    


    int blockStartRow = startRow / 36;
    int rowsShift = startRow % 36;
    int blockStartColumn = startColumn / 36;
    int columnsShift = startColumn % 36;
    

    int pBlock = blockStartRow;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = out.blockHeight(iBlock);
      int qBlock = blockStartColumn;
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int jWidth = out.blockWidth(jBlock);
        

        int outIndex = iBlock * blockColumns + jBlock;
        T[] outBlock = blocks[outIndex];
        int index = pBlock * blockColumns + qBlock;
        int width = blockWidth(qBlock);
        
        int heightExcess = iHeight + rowsShift - 36;
        int widthExcess = jWidth + columnsShift - 36;
        if (heightExcess > 0)
        {
          if (widthExcess > 0)
          {
            int width2 = blockWidth(qBlock + 1);
            copyBlockPart(blocks[index], width, rowsShift, 36, columnsShift, 36, outBlock, jWidth, 0, 0);
            


            copyBlockPart(blocks[(index + 1)], width2, rowsShift, 36, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
            


            copyBlockPart(blocks[(index + blockColumns)], width, 0, heightExcess, columnsShift, 36, outBlock, jWidth, iHeight - heightExcess, 0);
            


            copyBlockPart(blocks[(index + blockColumns + 1)], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);

          }
          else
          {

            copyBlockPart(blocks[index], width, rowsShift, 36, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
            


            copyBlockPart(blocks[(index + blockColumns)], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);

          }
          


        }
        else if (widthExcess > 0)
        {
          int width2 = blockWidth(qBlock + 1);
          copyBlockPart(blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 36, outBlock, jWidth, 0, 0);
          


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
  



















  private void copyBlockPart(T[] srcBlock, int srcWidth, int srcStartRow, int srcEndRow, int srcStartColumn, int srcEndColumn, T[] dstBlock, int dstWidth, int dstStartRow, int dstStartColumn)
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
  



  public void setSubMatrix(T[][] subMatrix, int row, int column)
    throws MatrixIndexException
  {
    int refLength = subMatrix[0].length;
    if (refLength < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    int endRow = row + subMatrix.length - 1;
    int endColumn = column + refLength - 1;
    checkSubMatrixIndex(row, endRow, column, endColumn);
    for (T[] subRow : subMatrix) {
      if (subRow.length != refLength) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(refLength), Integer.valueOf(subRow.length) });
      }
    }
    



    int blockStartRow = row / 36;
    int blockEndRow = (endRow + 36) / 36;
    int blockStartColumn = column / 36;
    int blockEndColumn = (endColumn + 36) / 36;
    

    for (int iBlock = blockStartRow; iBlock < blockEndRow; iBlock++) {
      int iHeight = blockHeight(iBlock);
      int firstRow = iBlock * 36;
      int iStart = FastMath.max(row, firstRow);
      int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
      
      for (int jBlock = blockStartColumn; jBlock < blockEndColumn; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int firstColumn = jBlock * 36;
        int jStart = FastMath.max(column, firstColumn);
        int jEnd = FastMath.min(endColumn + 1, firstColumn + jWidth);
        int jLength = jEnd - jStart;
        

        T[] block = blocks[(iBlock * blockColumns + jBlock)];
        for (int i = iStart; i < iEnd; i++) {
          System.arraycopy(subMatrix[(i - row)], jStart - column, block, (i - firstRow) * jWidth + (jStart - firstColumn), jLength);
        }
      }
    }
  }
  





  public FieldMatrix<T> getRowMatrix(int row)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), 1, columns);
    

    int iBlock = row / 36;
    int iRow = row - iBlock * 36;
    int outBlockIndex = 0;
    int outIndex = 0;
    T[] outBlock = blocks[outBlockIndex];
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  

  public void setRowMatrix(int row, FieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setRowMatrix(row, (BlockFieldMatrix)matrix);
    } catch (ClassCastException cce) {
      super.setRowMatrix(row, matrix);
    }
  }
  











  public void setRowMatrix(int row, BlockFieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    if ((matrix.getRowDimension() != 1) || (matrix.getColumnDimension() != nCols))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    




    int iBlock = row / 36;
    int iRow = row - iBlock * 36;
    int mBlockIndex = 0;
    int mIndex = 0;
    T[] mBlock = blocks[mBlockIndex];
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  



  public FieldMatrix<T> getColumnMatrix(int column)
    throws MatrixIndexException
  {
    checkColumnIndex(column);
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), rows, 1);
    

    int jBlock = column / 36;
    int jColumn = column - jBlock * 36;
    int jWidth = blockWidth(jBlock);
    int outBlockIndex = 0;
    int outIndex = 0;
    T[] outBlock = blocks[outBlockIndex];
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  

  public void setColumnMatrix(int column, FieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setColumnMatrix(column, (BlockFieldMatrix)matrix);
    } catch (ClassCastException cce) {
      super.setColumnMatrix(column, matrix);
    }
  }
  











  void setColumnMatrix(int column, BlockFieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    if ((matrix.getRowDimension() != nRows) || (matrix.getColumnDimension() != 1))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    




    int jBlock = column / 36;
    int jColumn = column - jBlock * 36;
    int jWidth = blockWidth(jBlock);
    int mBlockIndex = 0;
    int mIndex = 0;
    T[] mBlock = blocks[mBlockIndex];
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        if (mIndex >= mBlock.length) {
          mBlock = blocks[(++mBlockIndex)];
          mIndex = 0;
        }
        block[(i * jWidth + jColumn)] = mBlock[(mIndex++)];
      }
    }
  }
  



  public FieldVector<T> getRowVector(int row)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    T[] outData = buildArray(getField(), columns);
    

    int iBlock = row / 36;
    int iRow = row - iBlock * 36;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(block, iRow * jWidth, outData, outIndex, jWidth);
      outIndex += jWidth;
    }
    
    return new ArrayFieldVector(outData, false);
  }
  

  public void setRowVector(int row, FieldVector<T> vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setRow(row, ((ArrayFieldVector)vector).getDataRef());
    } catch (ClassCastException cce) {
      super.setRowVector(row, vector);
    }
  }
  


  public FieldVector<T> getColumnVector(int column)
    throws MatrixIndexException
  {
    checkColumnIndex(column);
    T[] outData = buildArray(getField(), rows);
    

    int jBlock = column / 36;
    int jColumn = column - jBlock * 36;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        outData[(outIndex++)] = block[(i * jWidth + jColumn)];
      }
    }
    
    return new ArrayFieldVector(outData, false);
  }
  

  public void setColumnVector(int column, FieldVector<T> vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    try
    {
      setColumn(column, ((ArrayFieldVector)vector).getDataRef());
    } catch (ClassCastException cce) {
      super.setColumnVector(column, vector);
    }
  }
  


  public T[] getRow(int row)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    T[] out = buildArray(getField(), columns);
    

    int iBlock = row / 36;
    int iRow = row - iBlock * 36;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(block, iRow * jWidth, out, outIndex, jWidth);
      outIndex += jWidth;
    }
    
    return out;
  }
  



  public void setRow(int row, T[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    if (array.length != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    



    int iBlock = row / 36;
    int iRow = row - iBlock * 36;
    int outIndex = 0;
    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      System.arraycopy(array, outIndex, block, iRow * jWidth, jWidth);
      outIndex += jWidth;
    }
  }
  



  public T[] getColumn(int column)
    throws MatrixIndexException
  {
    checkColumnIndex(column);
    T[] out = buildArray(getField(), rows);
    

    int jBlock = column / 36;
    int jColumn = column - jBlock * 36;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        out[(outIndex++)] = block[(i * jWidth + jColumn)];
      }
    }
    
    return out;
  }
  



  public void setColumn(int column, T[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    if (array.length != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    



    int jBlock = column / 36;
    int jColumn = column - jBlock * 36;
    int jWidth = blockWidth(jBlock);
    int outIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int iHeight = blockHeight(iBlock);
      T[] block = blocks[(iBlock * blockColumns + jBlock)];
      for (int i = 0; i < iHeight; i++) {
        block[(i * jWidth + jColumn)] = array[(outIndex++)];
      }
    }
  }
  

  public T getEntry(int row, int column)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 36;
      int jBlock = column / 36;
      int k = (row - iBlock * 36) * blockWidth(jBlock) + (column - jBlock * 36);
      
      return blocks[(iBlock * blockColumns + jBlock)][k];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void setEntry(int row, int column, T value)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 36;
      int jBlock = column / 36;
      int k = (row - iBlock * 36) * blockWidth(jBlock) + (column - jBlock * 36);
      
      blocks[(iBlock * blockColumns + jBlock)][k] = value;
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void addToEntry(int row, int column, T increment)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 36;
      int jBlock = column / 36;
      int k = (row - iBlock * 36) * blockWidth(jBlock) + (column - jBlock * 36);
      
      T[] blockIJ = blocks[(iBlock * blockColumns + jBlock)];
      blockIJ[k] = ((FieldElement)blockIJ[k].add(increment));
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  


  public void multiplyEntry(int row, int column, T factor)
    throws MatrixIndexException
  {
    try
    {
      int iBlock = row / 36;
      int jBlock = column / 36;
      int k = (row - iBlock * 36) * blockWidth(jBlock) + (column - jBlock * 36);
      
      T[] blockIJ = blocks[(iBlock * blockColumns + jBlock)];
      blockIJ[k] = ((FieldElement)blockIJ[k].multiply(factor));
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  




  public FieldMatrix<T> transpose()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    BlockFieldMatrix<T> out = new BlockFieldMatrix(getField(), nCols, nRows);
    

    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockColumns; iBlock++) {
      for (int jBlock = 0; jBlock < blockRows; jBlock++)
      {

        T[] outBlock = blocks[blockIndex];
        T[] tBlock = blocks[(jBlock * blockColumns + iBlock)];
        int pStart = iBlock * 36;
        int pEnd = FastMath.min(pStart + 36, columns);
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, rows);
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
  


  public T[] operate(T[] v)
    throws IllegalArgumentException
  {
    if (v.length != columns) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(columns) });
    }
    

    T[] out = buildArray(getField(), rows);
    T zero = (FieldElement)getField().getZero();
    

    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        T[] block = blocks[(iBlock * blockColumns + jBlock)];
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, columns);
        int k = 0;
        for (int p = pStart; p < pEnd; p++) {
          T sum = zero;
          int q = qStart;
          while (q < qEnd - 3) {
            sum = (FieldElement)((FieldElement)((FieldElement)((FieldElement)sum.add(block[k].multiply(v[q]))).add(block[(k + 1)].multiply(v[(q + 1)]))).add(block[(k + 2)].multiply(v[(q + 2)]))).add(block[(k + 3)].multiply(v[(q + 3)]));
            



            k += 4;
            q += 4;
          }
          while (q < qEnd) {
            sum = (FieldElement)sum.add(block[(k++)].multiply(v[(q++)]));
          }
          out[p] = ((FieldElement)out[p].add(sum));
        }
      }
    }
    
    return out;
  }
  



  public T[] preMultiply(T[] v)
    throws IllegalArgumentException
  {
    if (v.length != rows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(rows) });
    }
    

    T[] out = buildArray(getField(), columns);
    T zero = (FieldElement)getField().getZero();
    

    for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
      int jWidth = blockWidth(jBlock);
      int jWidth2 = jWidth + jWidth;
      int jWidth3 = jWidth2 + jWidth;
      int jWidth4 = jWidth3 + jWidth;
      int qStart = jBlock * 36;
      int qEnd = FastMath.min(qStart + 36, columns);
      for (int iBlock = 0; iBlock < blockRows; iBlock++) {
        T[] block = blocks[(iBlock * blockColumns + jBlock)];
        int pStart = iBlock * 36;
        int pEnd = FastMath.min(pStart + 36, rows);
        for (int q = qStart; q < qEnd; q++) {
          int k = q - qStart;
          T sum = zero;
          int p = pStart;
          while (p < pEnd - 3) {
            sum = (FieldElement)((FieldElement)((FieldElement)((FieldElement)sum.add(block[k].multiply(v[p]))).add(block[(k + jWidth)].multiply(v[(p + 1)]))).add(block[(k + jWidth2)].multiply(v[(p + 2)]))).add(block[(k + jWidth3)].multiply(v[(p + 3)]));
            



            k += jWidth4;
            p += 4;
          }
          while (p < pEnd) {
            sum = (FieldElement)sum.add(block[k].multiply(v[(p++)]));
            k += jWidth;
          }
          out[q] = ((FieldElement)out[q].add(sum));
        }
      }
    }
    
    return out;
  }
  


  public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int qStart = jBlock * 36;
          int qEnd = FastMath.min(qStart + 36, columns);
          T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  

  public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int qStart = jBlock * 36;
          int qEnd = FastMath.min(qStart + 36, columns);
          T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  



  public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; iBlock++) {
      int p0 = iBlock * 36;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int q0 = jBlock * 36;
          int qStart = FastMath.max(startColumn, q0);
          int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
          T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  



  public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; iBlock++) {
      int p0 = iBlock * 36;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
      for (int p = pStart; p < pEnd; p++) {
        for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; jBlock++) {
          int jWidth = blockWidth(jBlock);
          int q0 = jBlock * 36;
          int qStart = FastMath.max(startColumn, q0);
          int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
          T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  

  public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, columns);
        T[] block = blocks[blockIndex];
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
  

  public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    int blockIndex = 0;
    for (int iBlock = 0; iBlock < blockRows; iBlock++) {
      int pStart = iBlock * 36;
      int pEnd = FastMath.min(pStart + 36, rows);
      for (int jBlock = 0; jBlock < blockColumns; jBlock++) {
        int qStart = jBlock * 36;
        int qEnd = FastMath.min(qStart + 36, columns);
        T[] block = blocks[blockIndex];
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
  



  public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; iBlock++) {
      int p0 = iBlock * 36;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
      for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int q0 = jBlock * 36;
        int qStart = FastMath.max(startColumn, q0);
        int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
        T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
  



  public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(rows, columns, startRow, endRow, startColumn, endColumn);
    for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; iBlock++) {
      int p0 = iBlock * 36;
      int pStart = FastMath.max(startRow, p0);
      int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
      for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; jBlock++) {
        int jWidth = blockWidth(jBlock);
        int q0 = jBlock * 36;
        int qStart = FastMath.max(startColumn, q0);
        int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
        T[] block = blocks[(iBlock * blockColumns + jBlock)];
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
    return blockRow == blockRows - 1 ? rows - blockRow * 36 : 36;
  }
  




  private int blockWidth(int blockColumn)
  {
    return blockColumn == blockColumns - 1 ? columns - blockColumn * 36 : 36;
  }
}

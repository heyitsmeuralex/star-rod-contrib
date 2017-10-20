package data.shared.decoder;

import java.io.PrintWriter;
import java.nio.ByteBuffer;

public abstract interface IPrint<D extends DataDecoder>
{
  public abstract void print(D paramD, Pointer paramPointer, ByteBuffer paramByteBuffer, PrintWriter paramPrintWriter);
}

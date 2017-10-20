package data.shared.decoder;

import java.nio.ByteBuffer;

public abstract interface IScan<D extends DataDecoder>
{
  public abstract void scan(D paramD, Pointer paramPointer, ByteBuffer paramByteBuffer);
}

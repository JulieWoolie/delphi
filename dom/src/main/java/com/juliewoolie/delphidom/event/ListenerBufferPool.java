package com.juliewoolie.delphidom.event;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.BitSet;

public class ListenerBufferPool {
  public static final ListenerBufferPool POOL = new ListenerBufferPool();

  private ListenerBuffer[] buffers = new ListenerBuffer[3];
  private final BitSet inUse = new BitSet(3);

  public ListenerBuffer allocBuffer() {
    for (int i = 0; i < buffers.length; i++) {
      ListenerBuffer buffer = buffers[i];

      if (buffer == null) {
        buffer = new ListenerBuffer(i);
        buffers[i] = buffer;
        inUse.set(i);
        return buffer;
      }

      if (inUse.get(i)) {
        continue;
      }

      inUse.set(i);
      return buffer;
    }

    int len = buffers.length;
    buffers = ObjectArrays.grow(buffers, buffers.length + 3);

    ListenerBuffer buf = new ListenerBuffer(len);
    buffers[len] = buf;
    inUse.set(len);

    return buf;
  }

  public void freeBuffer(ListenerBuffer buffer) {
    inUse.clear(buffer.bufferId);
  }
}

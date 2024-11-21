package net.arcadiusmc.chimera;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScssList implements Iterable<Object> {

  private Object[] values = ObjectArrays.EMPTY_ARRAY;
  private int length = 0;

  public ScssList() {

  }

  public ScssList(int length) {
    this.values = new Object[length];
  }

  public Object get(int idx) {
    Objects.checkIndex(idx, length);
    return values[idx];
  }

  public void add(Object o) {
    values = ObjectArrays.ensureCapacity(values, length + 10, values.length);
    values[length++] = o;
  }

  public void remove(int idx) {
    Objects.checkIndex(idx, length);
    values[idx] = null;
    length--;
    if (idx != length) {
      System.arraycopy(values, idx + 1, values, idx, length - idx);
    }
  }

  @NotNull
  @Override
  public Iterator<Object> iterator() {
    return new Iter();
  }

  class Iter implements Iterator<Object> {

    int idx = 0;

    @Override
    public boolean hasNext() {
      return idx < length;
    }

    @Override
    public Object next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return values[idx++];
    }
  }
}

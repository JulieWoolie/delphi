package net.arcadiusmc.chimera.selector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.Getter;
import net.arcadiusmc.dom.Element;
import org.jetbrains.annotations.NotNull;

@Getter
public class SelectorGroup implements Iterable<Selector> {
  public static final SelectorGroup EMPTY = new SelectorGroup(new Selector[0]);

  private final Selector[] selectors;

  public SelectorGroup(Selector[] selectors) {
    this.selectors = selectors;
  }

  public int length() {
    return selectors.length;
  }

  public Selector get(int idx) {
    return selectors[idx];
  }

  public boolean test(Element root, Element element) {
    for (Selector selector : selectors) {
      if (selector.test(root, element)) {
        return true;
      }
    }

    return false;
  }

  @NotNull
  @Override
  public Iterator<Selector> iterator() {
    return new Iter();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    append(builder);
    return builder.toString();
  }

  void append(StringBuilder builder) {
    for (int i = 0; i < selectors.length; i++) {
      if (i != 0) {
        builder.append(", ");
      }

      selectors[i].append(builder);
    }
  }

  class Iter implements Iterator<Selector> {

    int idx = 0;

    @Override
    public boolean hasNext() {
      return idx < length();
    }

    @Override
    public Selector next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return selectors[idx++];
    }
  }
}

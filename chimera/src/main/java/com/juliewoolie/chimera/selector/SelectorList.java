package com.juliewoolie.chimera.selector;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.dom.Element;
import org.jetbrains.annotations.NotNull;

public class SelectorList implements Selector, Iterable<Selector> {

  static final int DEFAULT_SIZE = 10;

  private Selector[] selectors;

  @Getter
  private int size = 0;

  @Getter @Setter
  private ListStyle style = ListStyle.COMMA_LIST;

  @Getter @Setter
  private ListType type = ListType.OR;

  public SelectorList() {
    this(DEFAULT_SIZE);
  }

  public SelectorList(int size) {
    selectors = new Selector[size];
  }

  public void add(Selector selector) {
    if (size >= selectors.length) {
      selectors = ObjectArrays.ensureCapacity(selectors, selectors.length + 5);
    }

    selectors[size++] = selector;
  }

  public Selector get(int idx) {
    Objects.checkIndex(idx, size);
    return selectors[idx];
  }

  @Override
  public boolean test(Element element) {
    if (selectors.length < 1) {
      return true;
    }

    if (type == ListType.OR) {
      return testOr(element);
    } else {
      return testAnd(element);
    }
  }

  private boolean testAnd(Element element) {
    for (Selector selector : selectors) {
      if (selector.test(element)) {
        continue;
      }

      return false;
    }

    return true;
  }

  private boolean testOr(Element element) {
    for (Selector selector : selectors) {
      if (!selector.test(element)) {
        continue;
      }

      return true;
    }

    return false;
  }

  @Override
  public void append(StringBuilder builder) {
    for (int i = 0; i < selectors.length; i++) {
      if (i > 0 && style == ListStyle.COMMA_LIST) {
        builder.append(", ");
      }

      selectors[i].append(builder);
    }
  }

  @Override
  public void appendSpec(Spec spec) {
    for (Selector selector : selectors) {
      selector.appendSpec(spec);
    }
  }

  @NotNull
  @Override
  public Iterator<Selector> iterator() {
    return new Iter();
  }

  @Override
  public String toString() {
    return getCssString();
  }

  public void addAll(SelectorList list) {
    for (Selector selector : list) {
      add(selector);
    }
  }

  public enum ListStyle {
    // Separate entries with commas
    COMMA_LIST,

    // Do not delimit entries at all
    COMPACT
  }

  public enum ListType {
    // All must match
    AND,

    // One must match
    OR
  }

  private class Iter implements Iterator<Selector> {

    private int idx;

    @Override
    public boolean hasNext() {
      return idx < selectors.length;
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

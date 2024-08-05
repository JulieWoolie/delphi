package net.arcadiusmc.delphidom.selector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.parser.Parser;
import net.arcadiusmc.delphidom.parser.ParserErrors;
import org.jetbrains.annotations.NotNull;

public class SelectorGroup implements Iterable<Selector> {

  @Getter
  private final Selector[] selectors;

  public SelectorGroup(Selector[] selectors) {
    this.selectors = selectors;
  }

  public static SelectorGroup parse(String query) {
    StringBuffer buf = new StringBuffer(query);
    Parser parser = new Parser(buf);
    ParserErrors errors = parser.getErrors();

    SelectorGroup group = parser.selectorGroup();
    errors.orThrow();

    return group;
  }

  public int length() {
    return selectors.length;
  }

  public Selector get(int idx) {
    return selectors[idx];
  }

  public boolean test(DelphiElement root, DelphiElement element) {
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

  public String debugString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<selector-group src=")
        .append('"');

    append(builder);

    builder.append('"').append(" >");

    for (Selector selector : selectors) {
      builder.append("\n");

      String debug = selector.debugString();
      debug = debug.replace("\n", "\n  ");

      builder.append(debug);
    }

    builder.append("\n</selector-group>");

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

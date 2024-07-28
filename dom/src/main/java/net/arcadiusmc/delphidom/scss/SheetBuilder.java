package net.arcadiusmc.delphidom.scss;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.arcadiusmc.delphidom.selector.Selector;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import org.jetbrains.annotations.NotNull;

public class SheetBuilder implements StylesheetBuilder {

  List<Rule> rules = new ArrayList<>();

  public void add(Selector selector, PropertySet set) {
    Rule r = new Rule(selector, set);
    rules.add(r);
  }

  @Override
  public SheetBuilder addRule(@NotNull String selector, @NotNull Consumer<StyleProperties> consumer) {
    Objects.requireNonNull(selector, "Null selector");
    Objects.requireNonNull(consumer, "Null consumer");

    Selector s = Selector.parse(selector);

    PropertySet set = new PropertySet();
    PropertyMap map = new PropertyMap(set);
    consumer.accept(map);

    Rule r = new Rule(s, set);
    rules.add(r);

    return this;
  }

  @Override
  public Sheet build() {
    Rule[] array = rules.toArray(Rule[]::new);
    return new Sheet(array);
  }
}

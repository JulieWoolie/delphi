package com.juliewoolie.chimera;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.system.StyleObjectModel;
import com.juliewoolie.dom.ParserException;
import com.juliewoolie.dom.style.StyleProperties;
import com.juliewoolie.dom.style.StylesheetBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ChimeraSheetBuilder implements StylesheetBuilder {

  private final @Nullable StyleObjectModel system;
  private final List<Rule> rules = new ArrayList<>();

  public ChimeraSheetBuilder(@Nullable StyleObjectModel system) {
    this.system = system;
  }

  public ChimeraSheetBuilder() {
    this(null);
  }

  public ChimeraSheetBuilder addRule(Rule rule) {
    rules.add(rule);
    return this;
  }

  @Override
  public ChimeraSheetBuilder addRule(
      @NotNull String selector,
      @NotNull Consumer<StyleProperties> consumer
  ) throws ParserException {
    Objects.requireNonNull(selector, "Null selector");
    Objects.requireNonNull(consumer, "Null consumer");

    Selector selectorObj = Chimera.parseSelector(selector);
    PropertySet set = new PropertySet();
    PropertiesMap map = new PropertiesMap(set, system);

    consumer.accept(map);

    Rule rule = new Rule(selectorObj, set);
    rules.add(rule);

    return this;
  }

  @Override
  public ChimeraStylesheet build() {
    ChimeraStylesheet sheet = new ChimeraStylesheet(rules.toArray(Rule[]::new));

    if (system != null) {
      system.addStylesheet(sheet);
    }

    return sheet;
  }
}

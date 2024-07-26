package net.arcadiusmc.delphi.dom.scss;

import net.arcadiusmc.delphi.dom.selector.Selector;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import net.arcadiusmc.dom.style.StyleRule;
import org.jetbrains.annotations.NotNull;

public class Rule implements Comparable<Rule>, StyleRule {

  private final Selector selector;
  private final PropertySet properties;
  private final ReadonlyMap apiMap;

  public Rule(Selector selector, PropertySet properties) {
    this.selector = selector;
    this.properties = properties;
    this.apiMap = new ReadonlyMap(properties);
  }

  public Selector getSelectorObj() {
    return selector;
  }

  public PropertySet getPropertySet() {
    return properties;
  }

  @Override
  public int compareTo(@NotNull Rule o) {
    return selector.compareTo(o.selector);
  }

  @Override
  public String getSelector() {
    return selector.toString();
  }

  @Override
  public StylePropertiesReadonly getProperties() {
    return apiMap;
  }
}

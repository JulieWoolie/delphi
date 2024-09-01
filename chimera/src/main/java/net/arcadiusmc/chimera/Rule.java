package net.arcadiusmc.chimera;

import lombok.Getter;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.Spec;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import net.arcadiusmc.dom.style.StyleRule;
import org.jetbrains.annotations.NotNull;

public class Rule implements StyleRule, Comparable<Rule> {

  private final Selector selector;
  private final PropertySet properties;

  private final ReadonlyProperties apiProperties;

  @Getter
  private final Spec spec;

  public Rule(Selector selector, PropertySet properties) {
    this.selector = selector;
    this.properties = properties;
    this.apiProperties = new ReadonlyProperties(properties);

    this.spec = new Spec();
    selector.appendSpec(spec);
  }

  @Override
  public String getSelector() {
    return selector.getCssString();
  }

  @Override
  public StylePropertiesReadonly getProperties() {
    return apiProperties;
  }

  public Selector getSelectorObject() {
    return selector;
  }

  public PropertySet getPropertySet() {
    return properties;
  }

  @Override
  public int compareTo(@NotNull Rule o) {
    return spec.compareTo(o.spec);
  }
}

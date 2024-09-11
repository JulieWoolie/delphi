package net.arcadiusmc.chimera;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.PropertySet.PropertyIterator;
import net.arcadiusmc.dom.style.Stylesheet;

public class ChimeraStylesheet implements Stylesheet {

  public static final int FLAG_DEFAULT_STYLE = 0x1;

  private final Rule[] rules;

  @Setter @Getter
  private int flags = 0;

  public ChimeraStylesheet(Rule[] rules) {
    this.rules = rules;
  }

  @Override
  public int getLength() {
    return rules.length;
  }

  @Override
  public Rule getRule(int index) throws IndexOutOfBoundsException {
    return rules[index];
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    for (Rule rule : rules) {
      builder
          .append(rule.getSelector())
          .append("{");

      PropertyIterator it = rule.getPropertySet().iterator();
      while (it.hasNext()) {
        it.next();

        Property<Object> prop = it.property();
        Value<Object> val = it.value();

        builder
            .append(prop.getKey())
            .append(":")
            .append(val.getTextValue())
            .append(";");

        if (it.hasNext()) {
          builder.append(" ");
        }
      }

      builder.append("}");
    }

    return builder.toString();
  }
}

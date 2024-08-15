package net.arcadiusmc.delphidom.scss;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.dom.style.Stylesheet;

public class Sheet implements Stylesheet {

  public static final int FLAG_DEFAULT = 0x1;

  private final Rule[] rules;

  @Getter @Setter
  private int flags = 0;

  public Sheet(Rule[] rules) {
    this.rules = rules;
  }

  public void addFlag(int flag) {
    this.flags |= flag;
  }

  @Override
  public int getLength() {
    return rules.length;
  }

  @Override
  public Rule getRule(int index) {
    return rules[index];
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("/* rule-count: ").append(rules.length).append(" */");

    for (Rule rule : rules) {
      builder.append("\n");
      builder.append(rule.getSelector());
      builder.append(" {\n  ");

      String str = rule.getPropertySet().toParseString();
      builder.append(str.replace(";", ";\n  ").trim());

      builder.append("\n}");
    }

    return builder.toString();
  }
}

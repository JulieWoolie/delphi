package net.arcadiusmc.delphidom.scss;

import net.arcadiusmc.dom.style.Stylesheet;

public class Sheet implements Stylesheet {

  private final Rule[] rules;

  public Sheet(Rule[] rules) {
    this.rules = rules;
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

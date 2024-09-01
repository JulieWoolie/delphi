package net.arcadiusmc.chimera;

import net.arcadiusmc.dom.style.Stylesheet;

public class ChimeraStylesheet implements Stylesheet {

  private final Rule[] rules;

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
}

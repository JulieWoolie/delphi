package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.dom.style.Color;

@Getter @Setter
public class ColorLiteral extends Expression {

  private Color color;

  @Override
  public Color evaluate(ChimeraContext ctx) {
    return color;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.colorLiteral(this, context);
  }
}

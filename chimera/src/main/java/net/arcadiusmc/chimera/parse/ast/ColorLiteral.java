package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Color;

@Getter @Setter
public class ColorLiteral extends Expression {

  private Color color;

  @Override
  public Color evaluate(ChimeraContext ctx, Scope scope) {
    return color;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.colorLiteral(this, context);
  }
}

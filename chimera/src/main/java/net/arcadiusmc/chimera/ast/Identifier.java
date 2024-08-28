package net.arcadiusmc.chimera.ast;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;

@Getter @Setter
public class Identifier extends Expression {

  private String value;

  @Override
  public Object evaluate(ChimeraContext ctx) {
    if (Strings.isNullOrEmpty(value)) {
      return null;
    }

    Color c = NamedColor.named(value);
    if (c != null) {
      return c;
    }

    return value;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.identifier(this, context);
  }
}

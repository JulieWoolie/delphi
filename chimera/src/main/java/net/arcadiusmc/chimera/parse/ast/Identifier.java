package net.arcadiusmc.chimera.parse.ast;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;

@Getter @Setter
public class Identifier extends Expression {

  private String value;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
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
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.identifier(this);
  }
}

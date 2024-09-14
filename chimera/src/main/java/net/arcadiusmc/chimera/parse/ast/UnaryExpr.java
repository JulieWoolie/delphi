package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;

@Getter @Setter
public class UnaryExpr extends Expression {

  private Expression value;
  private UnaryOp op = UnaryOp.PLUS;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    if (value == null) {
      return null;
    }

    Object o = value.evaluate(ctx, scope);
    if (o == null) {
      return null;
    }

    if (op == UnaryOp.INVERT) {
      Boolean b = Chimera.coerceValue(Boolean.class, o);

      if (b == null) {
        return o;
      }

      return b;
    }

    Primitive prim = Chimera.coerceValue(Primitive.class, o);
    if (prim == null) {
      return null;
    }

    if (op == UnaryOp.PLUS) {
      return Primitive.create(+prim.getValue(), prim.getUnit());
    } else {
      return Primitive.create(-prim.getValue(), prim.getUnit());
    }
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.unary(this);
  }
}

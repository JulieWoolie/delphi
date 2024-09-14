package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.PrimitiveRect;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;

@Getter @Setter
public class RectExpr extends Expression {

  private Expression top;
  private Expression right;
  private Expression bottom;
  private Expression left;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    Primitive topRes = Chimera.coerceValue(Primitive.class, top.evaluate(ctx, scope));
    Primitive rightRes = Chimera.coerceValue(Primitive.class, right.evaluate(ctx, scope));
    Primitive bottomRes = Chimera.coerceValue(Primitive.class, bottom.evaluate(ctx, scope));
    Primitive leftRes = Chimera.coerceValue(Primitive.class, left.evaluate(ctx, scope));

    topRes = reportInvalidSide(topRes, "top", top.getStart(), ctx);
    rightRes = reportInvalidSide(rightRes, "right", right.getStart(), ctx);
    bottomRes = reportInvalidSide(bottomRes, "bottom", bottom.getStart(), ctx);
    leftRes = reportInvalidSide(leftRes, "left", left.getStart(), ctx);

    return PrimitiveRect.create(topRes, rightRes, bottomRes, leftRes);
  }

  Primitive reportInvalidSide(Primitive prim, String side, Location l, ChimeraContext ctx) {
    if (prim != null) {
      return prim;
    }

    ctx.getErrors().error(
        l,
        "Invalid expression for %s side. Must be a primitive, or a reference to a primitive",
        side
    );

    return Primitive.ZERO;
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.rectangle(this);
  }
}

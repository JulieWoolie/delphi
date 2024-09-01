package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.PrimitiveRect;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.dom.style.Primitive;

@Getter @Setter
public class RectExpr extends Expression {

  private Expression top;
  private Expression right;
  private Expression bottom;
  private Expression left;

  @Override
  public Object evaluate(ChimeraContext ctx) {
    Primitive topRes = Chimera.coerceValue(Primitive.class, top);
    Primitive rightRes = Chimera.coerceValue(Primitive.class, right);
    Primitive bottomRes = Chimera.coerceValue(Primitive.class, bottom);
    Primitive leftRes = Chimera.coerceValue(Primitive.class, left);

    topRes = reportInvalid(topRes, "top", top.getStart(), ctx);
    rightRes = reportInvalid(rightRes, "right", right.getStart(), ctx);
    bottomRes = reportInvalid(bottomRes, "bottom", bottom.getStart(), ctx);
    leftRes = reportInvalid(leftRes, "left", left.getStart(), ctx);

    return PrimitiveRect.create(topRes, rightRes, bottomRes, leftRes);
  }

  Primitive reportInvalid(Primitive prim, String side, Location l, ChimeraContext ctx) {
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
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.rectangle(this, context);
  }
}

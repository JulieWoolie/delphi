package net.arcadiusmc.chimera;

import lombok.Getter;
import net.arcadiusmc.dom.style.Primitive;

@Getter
public class PrimitiveRect {
  public static final PrimitiveRect ZERO = create(Primitive.ZERO);

  private final Primitive top;
  private final Primitive right;
  private final Primitive bottom;
  private final Primitive left;

  public PrimitiveRect(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public static PrimitiveRect create(Primitive prim) {
    return new PrimitiveRect(prim, prim, prim, prim);
  }

  public static PrimitiveRect create(Primitive x, Primitive y) {
    return new PrimitiveRect(y, x, y, x);
  }

  public static PrimitiveRect create(Primitive top, Primitive x, Primitive bottom) {
    return new PrimitiveRect(top, x, bottom, x);
  }

  public static PrimitiveRect create(
      Primitive top,
      Primitive right,
      Primitive bottom,
      Primitive left
  ) {
    return new PrimitiveRect(top, right, bottom, left);
  }

  @Override
  public String toString() {
    return top + " " + right + " " + bottom + " " + left;
  }
}

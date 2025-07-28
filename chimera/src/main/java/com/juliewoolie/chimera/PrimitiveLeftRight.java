package com.juliewoolie.chimera;

import lombok.Getter;
import com.juliewoolie.dom.style.Primitive;

@Getter
public class PrimitiveLeftRight {
  public static PrimitiveLeftRight ZERO = new PrimitiveLeftRight(Primitive.ZERO, Primitive.ZERO);

  private final Primitive left;
  private final Primitive right;

  public PrimitiveLeftRight(Primitive left, Primitive right) {
    this.left = left;
    this.right = right;
  }
}

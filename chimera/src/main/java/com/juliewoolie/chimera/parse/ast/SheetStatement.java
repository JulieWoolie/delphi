package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SheetStatement extends Block {

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.sheet(this);
  }
}

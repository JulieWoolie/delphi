package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IncludeStatement extends Statement {

  private Identifier name;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.include(this);
  }
}

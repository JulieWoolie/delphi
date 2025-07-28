package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExpressionStatement extends Statement {

  private Expression expr;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.exprStatement(this);
  }
}

package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BinaryExpr extends Expression {

  private BinaryOp op;
  private Expression lhs;
  private Expression rhs;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.binary(this);
  }
}

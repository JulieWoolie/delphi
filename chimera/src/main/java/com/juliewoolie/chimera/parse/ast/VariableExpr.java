package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VariableExpr extends Expression {

  private Identifier variableName;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.variableExpr(this);
  }
}

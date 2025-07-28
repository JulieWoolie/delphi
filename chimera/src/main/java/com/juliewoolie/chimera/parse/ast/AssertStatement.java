package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AssertStatement extends Statement {

  private Expression condition;
  private Expression message;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.assertStatement(this);
  }
}

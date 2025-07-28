package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IfStatement extends Statement {

  private Expression condition;
  private Statement body;
  private Statement elseBody;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.ifStatement(this);
  }
}

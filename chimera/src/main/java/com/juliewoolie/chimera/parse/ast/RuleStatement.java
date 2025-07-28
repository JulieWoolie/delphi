package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RuleStatement extends Statement {

  private SelectorExpression selector;
  private Block body;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.rule(this);
  }
}

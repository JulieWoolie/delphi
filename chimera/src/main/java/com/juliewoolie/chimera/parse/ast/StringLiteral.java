package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StringLiteral extends Expression {

  private String value;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.stringLiteral(this);
  }
}

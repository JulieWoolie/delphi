package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.Token;

@Getter @Setter
public class ErroneousExpr extends Expression {

  private Token token;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.error(this);
  }
}

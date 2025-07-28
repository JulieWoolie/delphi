package com.juliewoolie.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CallExpr extends Expression {

  private Identifier functionName;
  private final List<Expression> arguments = new ArrayList<>();

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.callExpr(this);
  }
}

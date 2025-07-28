package com.juliewoolie.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ListLiteral extends Expression {

  private final List<Expression> values = new ArrayList<>();

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.listLiteral(this);
  }
}

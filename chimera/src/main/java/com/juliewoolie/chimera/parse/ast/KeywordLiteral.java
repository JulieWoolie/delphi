package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KeywordLiteral extends Expression {

  private Keyword keyword;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.keywordLiteral(this);
  }
}

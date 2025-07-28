package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.dom.style.Color;

@Getter @Setter
public class ColorLiteral extends Expression {

  private Color color;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.colorLiteral(this);
  }
}

package com.juliewoolie.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InlineStyleStatement extends Statement {
  private final List<PropertyStatement> properties = new ArrayList<>();

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.inlineStyle(this);
  }
}

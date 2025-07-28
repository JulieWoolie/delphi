package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.ControlFlow;

@Getter @Setter
public class ControlFlowStatement extends Statement {

  private Expression returnValue;
  private ControlFlow flowType = ControlFlow.RETURN;

  private boolean invalid = false;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.returnStatement(this);
  }
}

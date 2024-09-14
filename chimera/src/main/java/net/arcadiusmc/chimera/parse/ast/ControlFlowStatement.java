package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ControlFlow;

@Getter @Setter
public class ControlFlowStatement extends Statement {

  private Expression returnValue;
  private ControlFlow flowType = ControlFlow.RETURN;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.returnStatement(this);
  }
}

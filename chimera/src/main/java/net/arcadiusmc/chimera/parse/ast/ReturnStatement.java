package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReturnStatement extends Statement {

  private Expression returnValue;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return null;
  }
}

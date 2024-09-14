package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImportStatement extends Statement {

  private StringLiteral importPath;
  private boolean invalid = false;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.importStatement(this);
  }
}

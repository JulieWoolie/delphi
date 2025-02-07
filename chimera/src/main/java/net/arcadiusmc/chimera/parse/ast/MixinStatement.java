package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MixinStatement extends Statement {

  private Identifier name;
  private Block body;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.mixin(this);
  }
}

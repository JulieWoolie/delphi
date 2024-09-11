package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Block extends Statement {

  private final List<Statement> statements = new ArrayList<>();
  private boolean propertyDeclAllowed = true;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return null;
  }
}

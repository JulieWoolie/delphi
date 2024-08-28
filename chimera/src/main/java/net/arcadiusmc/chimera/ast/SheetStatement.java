package net.arcadiusmc.chimera.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SheetStatement extends Node {

  private final List<VariableDecl> variableDeclarations = new ArrayList<>();
  private final List<RuleStatement> rules = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.sheet(this, context);
  }
}

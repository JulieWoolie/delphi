package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RuleStatement extends Statement {

  private SelectorExpression selector;

  private final List<RuleStatement> nestedRules = new ArrayList<>();
  private final List<PropertyStatement> properties = new ArrayList<>();

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.rule(this, context);
  }
}

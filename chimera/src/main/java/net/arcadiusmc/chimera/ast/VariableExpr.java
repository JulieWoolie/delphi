package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;

@Getter @Setter
public class VariableExpr extends Expression {

  private Identifier variableName;

  @Override
  public Object evaluate(ChimeraContext ctx) {
    if (variableName == null) {
      return null;
    }

    return ctx.getVariables().get(variableName.getValue());
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.variableExpr(this, context);
  }
}

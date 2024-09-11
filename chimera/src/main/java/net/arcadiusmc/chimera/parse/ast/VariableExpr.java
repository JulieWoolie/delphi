package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;

@Getter @Setter
public class VariableExpr extends Expression {

  private Identifier variableName;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    if (variableName == null) {
      return null;
    }

    Object o = scope.getVariable(variableName.getValue());

    if (o == null) {
      ctx.getErrors().error(getStart(), "Unknown variable %s", variableName.getValue());
    }

    return o;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.variableExpr(this, context);
  }
}

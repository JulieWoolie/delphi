package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;

@Getter @Setter
public class VariableDecl extends Statement {

  private Identifier name;
  private Expression value;

  public void execute(ChimeraContext ctx, Scope scope) {
    if (value == null) {
      return;
    }

    Object value = this.value.evaluate(ctx, scope);
    if (value == null) {
      return;
    }

    scope.putVariable(name.getValue(), value);
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.variableDecl(this);
  }
}

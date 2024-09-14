package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;

@Getter @Setter
public class NamespaceExpr extends Expression {

  private Identifier namespace;
  private Expression target;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    Scope module = scope.getNamespaced(namespace.getValue());
    if (module == null) {
      return null;
    }

    return target.evaluate(ctx, module);
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.namespaced(this);
  }
}

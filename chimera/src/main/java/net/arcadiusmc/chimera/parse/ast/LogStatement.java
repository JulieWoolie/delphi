package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import org.slf4j.event.Level;

@Getter @Setter
public class LogStatement extends Statement {

  private Expression expression;
  private Level level = Level.INFO;
  private String name = "info";

  public void evaluate(ChimeraContext ctx, Scope scope) {
    if (expression == null) {
      ctx.getErrors().log(level, "@%s:%s", name, getStart().line());
      return;
    }

    Object o = expression.evaluate(ctx, scope);
    ctx.getErrors().log(level, "@%s:%s %s", name, getStart().line(), o);
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.logStatement(this);
  }
}

package net.arcadiusmc.chimera.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;
import net.arcadiusmc.chimera.function.Argument;
import net.arcadiusmc.chimera.function.ScssFunction;
import net.arcadiusmc.chimera.function.ScssInvocationException;

@Getter @Setter
public class CallExpr extends Expression {

  private Identifier functionName;
  private final List<Expression> arguments = new ArrayList<>();

  @Override
  public Object evaluate(ChimeraContext ctx) {
    if (functionName == null) {
      return null;
    }

    String name = functionName.getValue();
    ScssFunction func = ctx.getFunctions().get(name);

    if (func == null) {
      ctx.getErrors().error(getStart(), "Unknown function %s", name);
      return null;
    }

    Argument[] arguments = new Argument[this.arguments.size()];
    for (int i = 0; i < this.arguments.size(); i++) {
      Expression argExpr = this.arguments.get(i);
      Object value = argExpr.evaluate(ctx);

      Argument arg = new Argument();
      arg.setArgumentIndex(i);
      arg.setValue(value);
      arg.setLocation(argExpr.getStart());
      arg.setErrors(ctx.getErrors());

      arguments[i] = arg;
    }

    try {
      return func.invoke(ctx, arguments);
    } catch (ScssInvocationException exc) {
      ctx.getErrors().error(exc.getLocation(), "Failed to invoke %s: %s", name, exc.getMessage());
      return null;
    }
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.callExpr(this, context);
  }
}

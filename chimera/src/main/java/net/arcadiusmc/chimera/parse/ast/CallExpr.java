package net.arcadiusmc.chimera.parse.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.function.Argument;
import net.arcadiusmc.chimera.function.ScssFunction;
import net.arcadiusmc.chimera.function.ScssInvocationException;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

@Getter @Setter
public class CallExpr extends Expression {

  private Identifier functionName;
  private final List<Expression> arguments = new ArrayList<>();

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    if (functionName == null) {
      return null;
    }

    String name = functionName.getValue();
    ScssFunction func = scope.getFunction(name);

    if (func == null) {
      ctx.getErrors().error(getStart(), "Unknown function %s", name);
      return null;
    }

    Argument[] arguments = new Argument[this.arguments.size()];
    for (int i = 0; i < this.arguments.size(); i++) {
      Expression argExpr = this.arguments.get(i);
      Object value = argExpr.evaluate(ctx, scope);

      Argument arg = new Argument();
      arg.setArgumentIndex(i);
      arg.setValue(value);
      arg.setLocation(argExpr.getStart());
      arg.setErrors(ctx.getErrors());

      arguments[i] = arg;
    }

    // Test argument counts
    Range<Integer> argCount = func.argumentCount();
    if (arguments.length < argCount.getMinimum()) {
      ctx.getErrors().error(getStart(),
          "Too few arguments! Expected at least %s arguments, found %s",
          argCount.getMinimum(), arguments.length
      );
      return null;
    }
    if (arguments.length > argCount.getMaximum()) {
      ctx.getErrors().error(getStart(),
          "Too many arguments! Expected at most %s arguments, found %s",
          argCount.getMaximum(), arguments.length
      );
      return null;
    }

    try {
      return func.invoke(ctx, arguments);
    } catch (ScssInvocationException exc) {
      ctx.getErrors().error(exc.getLocation(), "Failed to invoke %s: %s", name, exc.getMessage());
      return null;
    }
  }

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.callExpr(this);
  }
}

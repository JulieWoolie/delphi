package com.juliewoolie.chimera.function;

import com.juliewoolie.chimera.Properties;
import com.juliewoolie.chimera.Property;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.Scope;
import org.apache.commons.lang3.Range;

public class SetPropertyFunction implements ScssFunction {

  @Override
  public Range<Integer> argumentCount() {
    return Range.between(2, 3);
  }

  @Override
  public Object invoke(ChimeraContext ctx, Scope scope, Argument[] arguments)
      throws ScssInvocationException
  {
    String propertyName = arguments[0].string();
    PropertySet set = scope.getPropertyOutput();

    if (set == null) {
      throw new ScssInvocationException("Cannot access style properties in this context");
    }

    Property<Object> property = Properties.getByKey(propertyName);
    if (property == null) {
      throw new ScssInvocationException("Unknown property: " + propertyName);
    }

    boolean important = false;
    if (arguments.length > 2) {
      important = arguments[2].bool();
    }

    Argument arg = arguments[1];
    Object argVal = arg.getValue();
    String input = ctx.getInput(arg.getStart(), arg.getEnd());

    Value<Object> coerced = Chimera.coerceCssValue(
        input,
        important,
        property,
        argVal,
        ctx.getErrors(),
        arg.getStart()
    );

    if (coerced == null) {
      return null;
    }

    Value<Object> previous = set.orNull(property);
    set.setValue(property, coerced);

    if (previous == null) {
      return null;
    }
    return Chimera.valueToScript(previous);
  }
}

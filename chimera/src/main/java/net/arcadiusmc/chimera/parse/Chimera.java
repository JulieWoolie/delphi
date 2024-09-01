package net.arcadiusmc.chimera.parse;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.PrimitiveRect;
import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.chimera.Property;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.Value.ValueType;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.Identifier;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.Keyword;
import net.arcadiusmc.chimera.parse.ast.PropertyStatement;
import net.arcadiusmc.chimera.parse.ast.RuleStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.chimera.parse.ast.VariableDecl;
import net.arcadiusmc.chimera.selector.RegularSelector;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorNode;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import net.arcadiusmc.dom.style.Primitive;

public final class Chimera {
  private Chimera() {}

  public static Selector parseSelector(String selector) {
    ChimeraParser parser = new ChimeraParser(selector);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("<selector>");
    errors.setListener(error -> {
      throw new ParserException(error.getFormattedError());
    });

    SelectorExpression expr = parser.selector();

    if (parser.getStream().hasNext()) {
      errors.error(parser.peek().location(), "Invalid selector. whole input was not consumed");
    }

    Selector compiled = expr.compile(errors);

    return compiled;
  }

  public static void evaluateVariables(List<VariableDecl> list, ChimeraContext ctx) {
    for (VariableDecl variableDecl : list) {
      if (variableDecl.getValue() == null) {
        continue;
      }

      Object value = variableDecl.getValue().evaluate(ctx);
      if (value == null) {
        continue;
      }

      ctx.getVariables().put(variableDecl.getName().getValue(), value);
    }
  }

  public static ChimeraStylesheet compileSheet(SheetStatement stat, ChimeraContext ctx) {
    evaluateVariables(stat.getVariableDeclarations(), ctx);

    List<Rule> rules = new ArrayList<>(stat.getRules().size());

    for (int i = 0; i < stat.getRules().size(); i++) {
      compileRule(stat.getRules().get(i), ctx, null, rules);
    }

    return new ChimeraStylesheet(rules.toArray(Rule[]::new));
  }

  public static void compileInline(InlineStyleStatement stat, PropertySet out, ChimeraContext ctx) {
    for (PropertyStatement property : stat.getProperties()) {
      compileProperty(property, ctx, out);
    }
  }

  public static void compileRule(
      RuleStatement stat,
      ChimeraContext ctx,
      Selector prefix,
      List<Rule> out
  ) {
    Selector selector = stat.getSelector().compile(ctx.getErrors());
    PropertySet set = compileProperties(stat.getProperties(), ctx);

    if (prefix != null) {
      SelectorNode n1 = new SelectorNode();
      SelectorNode n2 = new SelectorNode();

      n1.setSelector(prefix);
      n2.setSelector(selector);

      selector = new RegularSelector(new SelectorNode[] {n1, n2});
    }

    out.add(new Rule(selector, set));

    for (RuleStatement nestedRule : stat.getNestedRules()) {
      compileRule(nestedRule, ctx, selector, out);
    }
  }

  public static PropertySet compileProperties(
      List<PropertyStatement> properties,
      ChimeraContext ctx
  ) {
    PropertySet set = new PropertySet();

    for (PropertyStatement propertyStat : properties) {
      compileProperty(propertyStat, ctx, set);
    }

    return set;
  }

  private static void compileProperty(
      PropertyStatement propertyStat,
      ChimeraContext ctx,
      PropertySet set
  ) {

    Identifier propertyName = propertyStat.getPropertyName();
    if (propertyName == null) {
      return;
    }

    String name = propertyName.getValue().toLowerCase();
    Property<Object> property = Properties.getByKey(name);

    if (property == null) {
      ctx.getErrors().error(propertyStat.getStart(), "Unknown/unsupported property %s", name);
      return;
    }

    Expression valExpr = propertyStat.getValue();

    if (valExpr == null) {
      return;
    }

    Object value = valExpr.evaluate(ctx);
    String input = ctx.getInput(valExpr.getStart(), propertyStat.getEnd());

    Value<Object> sval = coerceCssValue(
        input,
        propertyStat.getImportant() != null,
        property.getType(),
        value,
        ctx.getErrors(),
        valExpr.getStart()
    );

    if (sval == null) {
      return;
    }

    set.setValue(property, sval);
  }

  public static <T> Value<T> coerceCssValue(
      String input,
      boolean important,
      Class<T> type,
      Object value,
      CompilerErrors errors,
      Location l
  ) {
    value = tryCoerceValue(type, value);

    Value<T> sval = new Value<>();
    sval.setTextValue(input);
    sval.setImportant(important);

    if (type.isInstance(value)) {
      sval.setValue(type.cast(value));
      sval.setType(ValueType.EXPLICIT);
      return sval;
    }

    if (value instanceof Keyword keyw) {
      switch (keyw) {
        case UNSET -> {
          sval.setType(ValueType.UNSET);
        }
        case INITIAL -> {
          sval.setType(ValueType.INITIAL);
        }
        case INHERIT -> {
          sval.setType(ValueType.INHERIT);
        }
        case AUTO -> {
          sval.setType(ValueType.AUTO);
        }
      }
    }

    errors.error(l, "Invalid value for property");
    return null;
  }

  public static <T> T coerceValue(Class<T> type, Object o) {
    Object coerced = tryCoerceValue(type, o);

    if (!type.isInstance(coerced)) {
      return null;
    }

    return type.cast(coerced);
  }

  private static <T> Object tryCoerceValue(Class<T> type, Object object) {
    if (type.isInstance(object)) {
      return type.cast(object);
    }

    if (object instanceof Primitive prim) {
      if (type == Integer.class) {
        return (int) prim.getValue();
      }
      if (type == Float.class) {
        return prim.getValue();
      }
      if (type == PrimitiveRect.class) {
        return PrimitiveRect.create(prim);
      }

      return prim;
    }

    if (!(object instanceof Keyword key)) {
      return object;
    }

    return switch (key) {
      case TRUE -> true;
      case FALSE -> false;

      case NONE -> DisplayType.NONE;
      case INLINE -> DisplayType.INLINE;
      case INLINE_BLOCK -> DisplayType.INLINE_BLOCK;
      case FLEX -> DisplayType.FLEX;

      case ROW -> FlexDirection.ROW;
      case ROW_REVERSE -> FlexDirection.ROW_REVERSE;
      case COLUMN -> FlexDirection.COLUMN;
      case COLUMN_REVERSE -> FlexDirection.COLUMN_REVERSE;

      case NOWRAP -> FlexWrap.NOWRAP;
      case WRAP -> FlexWrap.WRAP;
      case WRAP_REVERSE -> FlexWrap.WRAP_REVERSE;

      case SPACE_AROUND -> JustifyContent.SPACE_AROUND;
      case SPACE_BETWEEN -> JustifyContent.SPACE_BETWEEN;
      case SPACE_EVENLY -> JustifyContent.SPACE_EVENLY;

      case FLEX_START -> {
        if (type == AlignItems.class) {
          yield AlignItems.FLEX_START;
        } else if (type == JustifyContent.class) {
          yield JustifyContent.FLEX_START;
        }

        yield key;
      }

      case FLEX_END -> {
        if (type == AlignItems.class) {
          yield AlignItems.FLEX_END;
        } else if (type == JustifyContent.class) {
          yield JustifyContent.FLEX_END;
        }

        yield key;
      }

      case CENTER -> {
        if (type == AlignItems.class) {
          yield AlignItems.CENTER;
        } else if (type == JustifyContent.class) {
          yield JustifyContent.CENTER;
        }

        yield key;
      }

      case STRETCH -> AlignItems.STRETCH;
      case BASELINE -> AlignItems.BASELINE;

      default -> key;
    };
  }
}

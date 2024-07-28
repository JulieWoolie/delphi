package net.arcadiusmc.delphi.dom.scss;

import static net.arcadiusmc.delphi.parser.Token.BRACKET_OPEN;
import static net.arcadiusmc.delphi.parser.Token.COLON;
import static net.arcadiusmc.delphi.parser.Token.COMMA;
import static net.arcadiusmc.delphi.parser.Token.DOLLAR_SIGN;
import static net.arcadiusmc.delphi.parser.Token.EOF;
import static net.arcadiusmc.delphi.parser.Token.HEX;
import static net.arcadiusmc.delphi.parser.Token.HEX_ALPHA;
import static net.arcadiusmc.delphi.parser.Token.HEX_SHORT;
import static net.arcadiusmc.delphi.parser.Token.ID;
import static net.arcadiusmc.delphi.parser.Token.NUMBER;
import static net.arcadiusmc.delphi.parser.Token.SEMICOLON;
import static net.arcadiusmc.delphi.parser.Token.SQUIG_CLOSE;
import static net.arcadiusmc.delphi.parser.Token.SQUIG_OPEN;
import static net.arcadiusmc.delphi.parser.Token.STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.StringUtil;
import net.arcadiusmc.delphi.dom.scss.func.ArgsParser;
import net.arcadiusmc.delphi.dom.scss.func.ScssFunction;
import net.arcadiusmc.delphi.dom.scss.func.StyleFunctions;
import net.arcadiusmc.delphi.dom.selector.Selector;
import net.arcadiusmc.delphi.parser.Location;
import net.arcadiusmc.delphi.parser.Parser;
import net.arcadiusmc.delphi.parser.Token;
import net.arcadiusmc.delphi.parser.TokenStream.ParseMode;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

public class ScssParser extends Parser {

  @Setter @Getter
  private Map<String, Object> variables = new HashMap<>();

  public ScssParser(StringBuffer in) {
    super(in);
  }

  private void expectStatementEnd(int endType) {
    Token peek = peek();

    if (peek.type() == SEMICOLON) {
      next();
      return;
    }

    if (peek.type() == endType) {
      return;
    }

    errors.err(peek.location(),
        "Expected %s or %s, found %s (Property values must end with a semicolon)",
        Token.typeToString(SEMICOLON),
        Token.typeToString(endType),
        peek.info()
    );
  }

  void semicolonError() {
    Token p = peek();

    errors.err(p.location(),
        "Expected ';', found %s (Property values and variable declarations must end with a semicolon)",
        p.info()
    );
  }

  public void inlineRules(PropertySet set) {
    stream.pushMode(ParseMode.VALUES);

    while (stream.hasNext()) {
      rule(set);
      expectStatementEnd(EOF);
    }

    stream.popMode();
  }

  public PropertySet inlineRules() {
    PropertySet set = new PropertySet();
    inlineRules(set);
    return set;
  }

  public Sheet stylesheet() {
    SheetBuilder stylesheet = new SheetBuilder();

    while (hasNext()) {
      Token peek = peek();

      if (peek.type() == DOLLAR_SIGN) {
        variableDefinition();
        continue;
      }

      style(stylesheet);
    }

    return stylesheet.build();
  }

  void style(SheetBuilder out) {
    List<Selector> selectorList = new ArrayList<>();
    selectorList.add(selector());

    while (peek().type() == COMMA) {
      next();
      selectorList.add(selector());
    }

    PropertySet set = ruleset();

    for (Selector selector : selectorList) {
      out.add(selector, set);
    }
  }

  PropertySet ruleset() {
    softExpect(SQUIG_OPEN);

    stream.pushMode(ParseMode.VALUES);
    PropertySet set = new PropertySet();

    while (peek().type() != SQUIG_CLOSE) {
      rule(set);
      expectStatementEnd(EOF);
    }

    softExpect(SQUIG_CLOSE);
    stream.popMode();

    return set;
  }

  void variableDefinition() {
    Token prefix = softExpect(DOLLAR_SIGN);
    Location l = prefix.location();

    Token name = expect(ID);
    softExpect(COLON);

    Object value = parseValue(String.class);

    if (peek().type() == SEMICOLON) {
      next();
    } else {
      semicolonError();
    }

    if (value == null) {
      return;
    }

    if (variables.containsKey(name.value())) {
      errors.warn(l, "Variable named '%s' already defined, overriding", name.value());
    }

    variables.put(name.value(), value);
  }

  @SuppressWarnings("unchecked")
  private void rule(PropertySet out) {
    Token id = expect(ID);
    expect(COLON);

    String ruleKey = id.value();
    Objects.requireNonNull(ruleKey);

    switch (ruleKey) {
      case "padding":
      case "margin":
      case "outline":
      case "border":
        rectangleShorthand(ruleKey, out);
        break;

      default:
        Property<Object> property = Properties.getByKey(ruleKey);
        Object value = parseValue(property == null ? Object.class : property.getType());

        if (property == null) {
          errors.err(id.location(), "Unknown/unsupported rule '%s'", ruleKey);
          return;
        }

        if (value == null) {
          return;
        }

        if (!property.getType().isInstance(value)) {
          errors.err(id.location(), "Invalid value for rule '%s': %s", ruleKey, value);
          return;
        }

        if (out.has(property)) {
          errors.warn(id.location(),
              "Property '%s' already set in rule... overriding",
              property.getKey()
          );
        }

        out.set(property, value);
        break;
    }

    if (peek().type() != Token.EXCLAMATION) {
      return;
    }

    next();
    Token important = expect(ID);

    if (important.value().equalsIgnoreCase("important")) {
      errors.err(important.location(), "'!important' is not supported by Delphi");
    } else {
      errors.err(important.location(), "Unexpected token");
    }
  }

  private void rectangleShorthand(String key, PropertySet out) {
    Property<Primitive> top;
    Property<Primitive> right;
    Property<Primitive> bottom;
    Property<Primitive> left;

    switch (key) {
      case "margin" -> {
        top = Properties.MARGIN_TOP;
        left = Properties.MARGIN_LEFT;
        bottom = Properties.MARGIN_BOTTOM;
        right = Properties.MARGIN_RIGHT;
      }

      case "padding" -> {
        top = Properties.PADDING_TOP;
        left = Properties.PADDING_LEFT;
        bottom = Properties.PADDING_BOTTOM;
        right = Properties.PADDING_RIGHT;
      }

      case "border" -> {
        top = Properties.BORDER_TOP;
        left = Properties.BORDER_LEFT;
        bottom = Properties.BORDER_BOTTOM;
        right = Properties.BORDER_RIGHT;
      }

      case "outline" -> {
        top = Properties.OUTLINE_TOP;
        left = Properties.OUTLINE_LEFT;
        bottom = Properties.OUTLINE_BOTTOM;
        right = Properties.OUTLINE_RIGHT;
      }

      default -> {
        return;
      }
    }

    Primitive[] values = new Primitive[4];
    int count = 0;
    boolean err = false;

    for (int i = 0; i < values.length; i++) {
      Token peek = peek();

      if (peek.type() != NUMBER && peek.type() != DOLLAR_SIGN) {
        break;
      }

      Primitive value = parseAs(Primitive.class);

      if (value == null) {
        err = true;
        continue;
      }

      values[count++] = value;
    }

    if (err) {
      return;
    }

    switch (count) {
      case 1:
        out.set(top, values[0]);
        out.set(right, values[0]);
        out.set(bottom, values[0]);
        out.set(left, values[0]);
        break;
      case 2:
        out.set(top, values[0]);
        out.set(right, values[1]);
        out.set(bottom, values[0]);
        out.set(left, values[1]);
        break;
      case 3:
        out.set(top, values[0]);
        out.set(right, values[1]);
        out.set(bottom, values[2]);
        out.set(left, values[1]);
        break;
      case 4:
        out.set(top, values[0]);
        out.set(right, values[1]);
        out.set(bottom, values[2]);
        out.set(left, values[3]);
        break;

      default:
        return;
    }
  }


  public <T> T parseAs(Class<T> type) {
    Object o = parseValue(type);

    if (!type.isInstance(o)) {
      return null;
    }

    return type.cast(o) ;
  }

  @SuppressWarnings("null")
  private Object parseValue(Class<?> hint) {
    Token next = next();
    String val = next.value();

    return switch (next.type()) {
      case STRING -> attemptCoercion(next.location(), val, hint);

      case ID -> {
        if (peek().type() == BRACKET_OPEN) {
          yield evaluateFunction(val);
        }

        yield attemptCoercion(next.location(), val, hint);
      }

      case DOLLAR_SIGN -> {
        Token var = expect(ID);
        Object varValue = variables.get(var.value());

        if (varValue == null) {
          errors.err(var.location(), "Unknown variable '%s'", var.value());
        } else if (varValue instanceof String str) {
          yield attemptCoercion(next.location(), str, hint);
        }

        yield varValue;
      }

      case HEX -> {
        int rgb = Integer.parseUnsignedInt(val, 16);
        yield Color.rgb(rgb);
      }

      case HEX_SHORT -> {
        StringBuilder buffer = new StringBuilder();

        for (char ch: val.toCharArray()) {
          buffer.append(ch);
          buffer.append(ch);
        }

        int rgb = Integer.parseUnsignedInt(buffer, 0, buffer.length(), 16);
        yield Color.rgb(rgb);
      }

      case HEX_ALPHA -> {
        int argb = Integer.parseUnsignedInt(val, 16);
        yield Color.argb(argb);
      }

      case NUMBER -> {
        float f = Float.parseFloat(val);

        Token peeked = peek();
        int ptype = peeked.type();

        Unit unit;

        if (hint == Integer.class) {
          yield (int) f;
        }
        if (hint == Long.class) {
          yield (long) f;
        }
        if (hint == Float.class) {
          yield f;
        }
        if (hint == Double.class) {
          yield (double) f;
        }

        if (ptype == ID) {
          String pvalue = peeked.value();
          assert pvalue != null;

          unit = switch (pvalue) {
            case "px" -> Unit.PX;
            case "vw" -> Unit.VW;
            case "vh" -> Unit.VH;
            case "ch" -> Unit.CH;
            default -> Unit.NONE;
          };
        } else {
          unit = Unit.NONE;
        }

        if (unit != Unit.NONE) {
          next();
        }

        yield Primitive.create(f, unit);
      }

      default -> {
        errors.err(next.location(), "Invalid value type");
        yield null;
      }
    };
  }

  private Object evaluateFunction(String functionName) {
    Token start = expect(BRACKET_OPEN);
    Location l = start.location();

    ScssFunction function;

    switch (functionName.toLowerCase()) {
      case "lighten":
        function = StyleFunctions.LIGHTEN;
        break;
      case "darken":
        function = StyleFunctions.DARKEN;
        break;
      case "rgb":
        function = StyleFunctions.RGB;
        break;
      case "rgba":
        function = StyleFunctions.RGBA;
        break;

      default:
        errors.err(l, "Unknown/unsupported function '%s'", functionName);
        return null;
    }

    ArgsParser parser = new ArgsParser(this);
    return function.evaluate(functionName, parser, errors);
  }

  private Object attemptCoercion(Location l, String val, Class<?> hint) {
    if (String.class.isAssignableFrom(hint)) {
      return val;
    }

    Boolean b = StringUtil.parseBoolean(val);

    if (b != null) {
      return b;
    }

    if (hint == Boolean.class || hint == Boolean.TYPE) {
      errors.err(l, "Invalid boolean value '%s'", val);
      return null;
    }

    if (Enum.class.isAssignableFrom(hint)) {
      Enum[] constants = (Enum[]) hint.getEnumConstants();
      String underscoredVal = val.replace("-", "_");

      for (Enum constant : constants) {
        String name = constant.name();

        if (name.equalsIgnoreCase(val) || name.equalsIgnoreCase(underscoredVal)) {
          return constant;
        }
      }

      errors.err(l, "Invalid %s value '%s'", hint.getSimpleName(), val);
      return null;
    }

    Color c = NamedColor.named(val);
    if (c == null) {
      errors.err(l, "Unknown color '%s'", val);
      return NamedColor.BLACK;
    }

    return c;
  }
}

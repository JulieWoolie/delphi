package com.juliewoolie.chimera.parse;

import com.google.common.base.Strings;
import java.util.Optional;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.PrimitiveLeftRight;
import com.juliewoolie.chimera.PrimitiveRect;
import com.juliewoolie.chimera.Property;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.ScssList;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.chimera.Value.ValueType;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.chimera.parse.ast.Keyword;
import com.juliewoolie.chimera.parse.ast.SelectorExpression;
import com.juliewoolie.chimera.parse.ast.SheetStatement;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.dom.ParserException;
import com.juliewoolie.dom.style.AlignItems;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.FlexWrap;
import com.juliewoolie.dom.style.JustifyContent;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Visibility;
import org.slf4j.LoggerFactory;

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

    return expr.compile(errors);
  }

  public static ChimeraStylesheet parseSheet(StringBuffer input, String sourceName) {
    return parseSheet(input, sourceName, error -> {
      LoggerFactory.getLogger("Document")
          .atLevel(error.getLevel())
          .setMessage(error.getFormattedError())
          .log();
    });
  }

  public static ChimeraStylesheet parseSheet(
      StringBuffer input,
      String name,
      CompilerErrorListener listener
  ) {
    ChimeraParser parser = new ChimeraParser(input);

    if (!Strings.isNullOrEmpty(name)) {
      parser.getErrors().setSourceName(name);
    }
    if (listener != null) {
      parser.getErrors().setListener(listener);
    }

    SheetStatement stat = parser.stylesheet();
    return compileSheet(stat, parser.createContext());
  }

  public static ChimeraStylesheet compileSheet(SheetStatement stat, ChimeraContext ctx) {
    Scope scope = Scope.createTopLevel();
    Interpreter inter = new Interpreter(ctx, scope);
    return inter.sheet(stat);
  }

  public static void compileInline(InlineStyleStatement stat, PropertySet out, ChimeraContext ctx) {
    Scope scope = Scope.createTopLevel();
    Interpreter inter = new Interpreter(ctx, scope);
    inter.inline(stat, out);
  }

  public static <T> Value<T> coerceCssValue(
      String input,
      boolean important,
      Property<T> property,
      Object value,
      CompilerErrors errors,
      Location l
  ) {
    Class<T> type = property.getType();
    value = tryCoerceValue(type, value);

    Value<T> sval = new Value<>();
    sval.setTextValue(input);
    sval.setImportant(important);

    if (type.isInstance(value)) {
      T tval = type.cast(value);

      Optional<String> errorOpt = property.validateValue(tval);
      if (errorOpt.isPresent()) {
        errors.error(l, "Invalid value for property %s: %s", property.getKey(), errorOpt.get());
        tval = property.getDefaultValue();
      }

      sval.setValue(tval);
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

      return sval;
    }

    System.out.println(value);
    if (value instanceof ScssList list) {
      System.out.println(list.get(0));
      System.out.println(list.get(0).getClass());
    }
    errors.error(l, "Invalid value for property %s. Expected %s, got %s",
        property.getKey(),
        type.getSimpleName(),
        value == null ? "null" : value.getClass().getSimpleName()
    );
    return null;
  }

  public static <T> T coerceValue(Class<T> type, Object o) {
    Object coerced = tryCoerceValue(type, o);

    if (!type.isInstance(coerced)) {
      return null;
    }

    return type.cast(coerced);
  }

  public static <T> Object tryCoerceValue(Class<T> type, Object object) {
    if (type.isInstance(object)) {
      return object;
    }

    if (type == PrimitiveRect.class) {
      return tryCoerceRectangle(object);
    }
    if (type == PrimitiveLeftRight.class) {
      return tryCoerceLeftRight(object);
    }

    if (object instanceof Primitive prim) {
      if (type == Integer.class) {
        return (int) prim.getValue();
      }
      if (type == Float.class) {
        return prim.getValue();
      }
      return prim;
    }

    if (!(object instanceof Keyword key)) {
      return object;
    }

    return fromKeyword(key, type);
  }

  private static Object tryCoerceLeftRight(Object object) {
    if (object instanceof Primitive prim) {
      return new PrimitiveLeftRight(prim, prim);
    }

    if (object instanceof ScssList list) {
      int len = list.getLength();
      if (len < 1) {
        return PrimitiveLeftRight.ZERO;
      }

      Primitive l;
      Primitive r;

      if (len == 1) {
        l = coerceValue(Primitive.class, list.get(0));
        r = l;
      } else {
        l = coerceValue(Primitive.class, list.get(0));
        r = coerceValue(Primitive.class, list.get(1));
      }

      if (l == null || r == null) {
        return object;
      }

      return new PrimitiveLeftRight(l, r);
    }

    return object;
  }

  private static Object tryCoerceRectangle(Object object) {
    if (object instanceof Primitive prim) {
      return PrimitiveRect.create(prim);
    }

    if (object instanceof ScssList list) {
      int len = list.getLength();

      if (len > 4) {
        return object;
      }
      if (len < 1) {
        return object;
      }

      Primitive top;
      Primitive right;
      Primitive bottom;
      Primitive left;

      switch (len) {
        case 1:
          top = coerceValue(Primitive.class, list.get(0));
          right = top;
          bottom = top;
          left = top;
          break;

        case 2:
          top = coerceValue(Primitive.class, list.get(0));
          right = coerceValue(Primitive.class, list.get(1));
          bottom = top;
          left = right;
          break;

        case 3:
          top = coerceValue(Primitive.class, list.get(0));
          right = coerceValue(Primitive.class, list.get(1));
          bottom = coerceValue(Primitive.class, list.get(2));
          left = right;
          break;

        default: // Guaranteed to be 4 here
          top = coerceValue(Primitive.class, list.get(0));
          right = coerceValue(Primitive.class, list.get(1));
          bottom = coerceValue(Primitive.class, list.get(2));
          left = coerceValue(Primitive.class, list.get(3));
          break;
      }

      if (top == null || bottom == null || left == null || right == null) {
        return object;
      }

      return PrimitiveRect.create(top, right, bottom, left);
    }

    return object;
  }

  private static Object fromKeyword(Keyword key, Class<?> type) {
    return switch (key) {
      case TRUE -> true;
      case FALSE -> false;

      case BLOCK -> DisplayType.BLOCK;
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

      case CONTENT_BOX -> BoxSizing.CONTENT_BOX;
      case BORDER_BOX -> BoxSizing.BORDER_BOX;

      case VISIBLE -> Visibility.VISIBLE;
      case HIDDEN -> Visibility.HIDDEN;
      case COLLAPSE -> Visibility.COLLAPSE;

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

  public static Object valueToScript(Value<?> v) {
    ValueType type = v.getType();
    switch (type) {
      case UNSET -> {
        return Keyword.UNSET;
      }
      case AUTO -> {
        return Keyword.AUTO;
      }
      case INHERIT -> {
        return Keyword.INHERIT;
      }
      case INITIAL -> {
        return Keyword.INITIAL;
      }
      default -> {
        return toScriptValue(v.getValue());
      }
    }
  }

  public static Object toScriptValue(Object o) {
    switch (o) {
      case PrimitiveRect rect -> {
        ScssList list = new ScssList(4);
        list.add(rect.getTop());
        list.add(rect.getRight());
        list.add(rect.getBottom());
        list.add(rect.getLeft());
        return list;
      }
      case PrimitiveLeftRight lr -> {
        ScssList list = new ScssList(2);
        list.add(lr.getLeft());
        list.add(lr.getRight());
        return list;
      }

      case DisplayType dt -> {
        return switch (dt) {
          case BLOCK -> Keyword.BLOCK;
          case FLEX -> Keyword.FLEX;
          case NONE -> Keyword.NONE;
          case INLINE -> Keyword.INLINE;
          case INLINE_BLOCK -> Keyword.INLINE_BLOCK;
        };
      }
      case FlexDirection dir -> {
        return switch (dir) {
          case ROW -> Keyword.ROW;
          case COLUMN -> Keyword.COLUMN;
          case ROW_REVERSE -> Keyword.ROW_REVERSE;
          case COLUMN_REVERSE -> Keyword.COLUMN_REVERSE;
        };
      }
      case FlexWrap wrap -> {
        return switch (wrap) {
          case WRAP -> Keyword.WRAP;
          case NOWRAP -> Keyword.NOWRAP;
          case WRAP_REVERSE -> Keyword.WRAP_REVERSE;
        };
      }
      case JustifyContent jc -> {
        return switch (jc) {
          case CENTER -> Keyword.CENTER;
          case FLEX_END -> Keyword.FLEX_END;
          case FLEX_START -> Keyword.FLEX_START;
          case SPACE_AROUND -> Keyword.SPACE_AROUND;
          case SPACE_BETWEEN -> Keyword.SPACE_BETWEEN;
          case SPACE_EVENLY -> Keyword.SPACE_EVENLY;
        };
      }
      case BoxSizing bs -> {
        return switch (bs) {
          case BORDER_BOX -> Keyword.BORDER_BOX;
          case CONTENT_BOX -> Keyword.CONTENT_BOX;
        };
      }
      case AlignItems ai -> {
        return switch (ai) {
          case FLEX_END -> Keyword.FLEX_END;
          case CENTER -> Keyword.CENTER;
          case STRETCH -> Keyword.STRETCH;
          case BASELINE -> Keyword.BASELINE;
          case FLEX_START -> Keyword.FLEX_START;
        };
      }
      case Visibility v -> {
        return switch (v) {
          case HIDDEN -> Keyword.HIDDEN;
          case VISIBLE -> Keyword.VISIBLE;
          case COLLAPSE -> Keyword.COLLAPSE;
        };
      }

      default -> {
        return o;
      }
    }
  }
}

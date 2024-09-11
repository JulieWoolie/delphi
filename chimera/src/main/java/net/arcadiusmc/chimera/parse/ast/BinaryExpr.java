package net.arcadiusmc.chimera.parse.ast;

import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;

@Getter @Setter
public class BinaryExpr extends Expression {

  static final int COMPARISON_FAILED = -2;
  static final int LESS = -1;
  static final int EQ = 0;
  static final int GREATER = 1;

  static final BinaryOperator<String> CONCAT = (l, r) -> l + r;
  static final BinaryOperator<String> CONCAT_DASH = (l, r) -> l + "-" + r;

  static final BooleanBinaryOperator AND = (x, y) -> x & y;
  static final BooleanBinaryOperator OR = (x, y) -> x | y;

  private BinaryOp op;
  private Expression lhs;
  private Expression rhs;

  @Override
  public Object evaluate(ChimeraContext ctx, Scope scope) {
    Object lv = lhs.evaluate(ctx, scope);
    Object rv = rhs.evaluate(ctx, scope);

    CompilerErrors err = ctx.getErrors();

    return switch (op) {
      case EQUAL -> Objects.equals(lv, rv);
      case NOT_EQUAL -> !Objects.equals(lv, rv);

      case OR -> booleanOp(lv, rv, OR);
      case AND -> booleanOp(lv, rv, AND);

      case DIV -> applyNumbers(err, lv, rv, NumberOp.DIVIDE);
      case MOD -> applyNumbers(err, lv, rv, NumberOp.MODULO);
      case MUL -> applyNumbers(err, lv, rv, NumberOp.MULTIPLY);

      case LT, GT, GTE, LTE -> runCompare(err, lv, rv);

      case PLUS -> stringConcatOrNumber(err, lv, rv, NumberOp.ADD, CONCAT);
      case MINUS -> stringConcatOrNumber(err, lv, rv, NumberOp.SUBTRACT, CONCAT_DASH);
    };
  }

  boolean booleanOp(Object l, Object r, BooleanBinaryOperator operator) {
    boolean b1 = Objects.requireNonNullElse(Chimera.coerceValue(Boolean.class, l), false);
    boolean b2 = Objects.requireNonNullElse(Chimera.coerceValue(Boolean.class, r), false);
    return operator.apply(b1, b2);
  }

  boolean runCompare(CompilerErrors errors, Object l, Object r) {
    int cmp = compareValues(errors, l, r);

    if (cmp == COMPARISON_FAILED) {
      return false;
    }

    return switch (op) {
      case LT -> cmp == LESS;
      case LTE -> cmp == LESS || cmp == EQ;
      case GT -> cmp == GREATER;
      case GTE -> cmp == GREATER || cmp == EQ;
      default -> false;
    };
  }

  int compareValues(CompilerErrors errors, Object l, Object r) {
    if (l == null && r == null) {
      return EQ;
    }
    if (l == null) {
      return LESS;
    }
    if (r == null) {
      return GREATER;
    }

    if (l instanceof Primitive pl && r instanceof Primitive pr) {
      if (!testCompatability(errors, getStart(), pl.getUnit(), pr.getUnit())) {
        return COMPARISON_FAILED;
      }

      float vl = pl.getValue();
      float vr = pr.getValue();

      if (pl.getUnit() == Unit.M) {
        vl *= 100.0f;
      }
      if (pr.getUnit() == Unit.M) {
        vr *= 100.0f;
      }

      return Float.compare(vl, vr);
    }

    String ls = String.valueOf(l);
    String lr = String.valueOf(r);

    int cmp = ls.compareTo(lr);
    return Integer.compare(cmp, 0);
  }

  Object stringConcatOrNumber(
      CompilerErrors err,
      Object l,
      Object r,
      FloatBinaryOperator op,
      BinaryOperator<String> stringConcat
  ) {
    if (l instanceof Primitive && r instanceof Primitive) {
      return applyNumbers(err, l, r, op);
    }

    String ls = String.valueOf(l);
    String rs = String.valueOf(r);

    return stringConcat.apply(ls, rs);
  }

  Primitive applyNumbers(
      CompilerErrors err,
      Object l,
      Object r,
      FloatBinaryOperator op
  ) {
    if (!(l instanceof Primitive pl) || !(r instanceof Primitive pr)) {
      err.error(getStart(), "Operator requires 2 numeric values");
      return Primitive.ZERO;
    }

    testCompatability(err, getStart(), pl.getUnit(), pr.getUnit());

    Unit left = pl.getUnit();
    Unit right = pr.getUnit();

    float fl = preEvalTranslate(pl);
    float fr = preEvalTranslate(pr);

    float result = op.apply(fl, fr);

    return postEval(result, left, right);
  }

  public static boolean testCompatability(CompilerErrors err, Location l, Unit left, Unit right) {
    if (areUnitsCompatible(left, right)) {
      return true;
    }

    err.error(
        l,
        "Incompatible units %s and %s",
        left.getUnit(),
        right.getUnit()
    );

    return false;
  }

  public static float preEvalTranslate(Primitive p) {
    if (p.getUnit() == Unit.M) {
      return p.getValue() * 100.0f;
    }
    if (isAngular(p.getUnit())) {
      return p.toDegrees();
    }
    return p.getValue();
  }

  public static Primitive postEval(float v, Unit left, Unit right) {
    if (isAngular(left) || isAngular(right)) {
      return Primitive.create(v, Unit.DEG);
    }
    if (left == Unit.M || right == Unit.M) {
      return Primitive.create(v, Unit.CM);
    }

    if (left == Unit.NONE) {
      return Primitive.create(v, right);
    } else {
      return Primitive.create(v, left);
    }
  }

  public static boolean areUnitsCompatible(Unit l, Unit r) {
    if (l == r || l == Unit.NONE || r == Unit.NONE) {
      return true;
    }
    if (l == Unit.CM || l == Unit.M) {
      return r == Unit.CM || l == Unit.M;
    }
    if (isAngular(l)) {
      return isAngular(r);
    }
    return false;
  }

  private static boolean isAngular(Unit u) {
    return switch (u) {
      case DEG, RAD, GRAD, TURN -> true;
      default -> false;
    };
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.binary(this, context);
  }

  enum NumberOp implements FloatBinaryOperator {
    ADD {
      @Override
      public float apply(float x, float y) {
        return x + y;
      }
    },
    SUBTRACT {
      @Override
      public float apply(float x, float y) {
        return x - y;
      }
    },
    MULTIPLY {
      @Override
      public float apply(float x, float y) {
        return x * y;
      }
    },
    DIVIDE {
      @Override
      public float apply(float x, float y) {
        return x / y;
      }
    },
    MODULO {
      @Override
      public float apply(float x, float y) {
        return x % y;
      }
    },
    ;
  }
}

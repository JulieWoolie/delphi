package net.arcadiusmc.chimera.parse;

import static net.arcadiusmc.chimera.parse.Chimera.coerceCssValue;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.booleans.BooleanBinaryOperator;
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.function.BinaryOperator;
import lombok.Getter;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.PrimitiveRect;
import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.chimera.Property;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.function.Argument;
import net.arcadiusmc.chimera.function.ScssFunction;
import net.arcadiusmc.chimera.function.ScssInvocationException;
import net.arcadiusmc.chimera.parse.ast.BinaryExpr;
import net.arcadiusmc.chimera.parse.ast.BinaryOp;
import net.arcadiusmc.chimera.parse.ast.Block;
import net.arcadiusmc.chimera.parse.ast.CallExpr;
import net.arcadiusmc.chimera.parse.ast.ColorLiteral;
import net.arcadiusmc.chimera.parse.ast.ControlFlowStatement;
import net.arcadiusmc.chimera.parse.ast.ErroneousExpr;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.Identifier;
import net.arcadiusmc.chimera.parse.ast.IfStatement;
import net.arcadiusmc.chimera.parse.ast.ImportStatement;
import net.arcadiusmc.chimera.parse.ast.ImportantMarker;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.KeywordLiteral;
import net.arcadiusmc.chimera.parse.ast.LogStatement;
import net.arcadiusmc.chimera.parse.ast.NamespaceExpr;
import net.arcadiusmc.chimera.parse.ast.NodeVisitor;
import net.arcadiusmc.chimera.parse.ast.NumberLiteral;
import net.arcadiusmc.chimera.parse.ast.PropertyStatement;
import net.arcadiusmc.chimera.parse.ast.RectExpr;
import net.arcadiusmc.chimera.parse.ast.RegularSelectorStatement;
import net.arcadiusmc.chimera.parse.ast.RuleStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.NestedSelector;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.TagNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorListStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.chimera.parse.ast.Statement;
import net.arcadiusmc.chimera.parse.ast.StringLiteral;
import net.arcadiusmc.chimera.parse.ast.UnaryExpr;
import net.arcadiusmc.chimera.parse.ast.UnaryOp;
import net.arcadiusmc.chimera.parse.ast.VariableDecl;
import net.arcadiusmc.chimera.parse.ast.VariableExpr;
import net.arcadiusmc.chimera.selector.Combinator;
import net.arcadiusmc.chimera.selector.RegularSelector;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorList;
import net.arcadiusmc.chimera.selector.SelectorList.ListStyle;
import net.arcadiusmc.chimera.selector.SelectorList.ListType;
import net.arcadiusmc.chimera.selector.SelectorNode;
import net.arcadiusmc.delphi.util.Nothing;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.apache.commons.lang3.Range;
import org.slf4j.event.Level;

@Getter
public class Interpreter implements NodeVisitor<Object> {

  static final int COMPARISON_FAILED = -2;
  static final int LESS = -1;
  static final int EQ = 0;
  static final int GREATER = 1;

  static final BinaryOperator<String> CONCAT = (l, r) -> l + r;
  static final BinaryOperator<String> CONCAT_DASH = (l, r) -> l + "-" + r;

  static final BooleanBinaryOperator AND = (x, y) -> x & y;
  static final BooleanBinaryOperator OR = (x, y) -> x | y;

  static final FloatBinaryOperator ADD      = Float::sum;
  static final FloatBinaryOperator SUBTRACT = (x, y) -> x - y;
  static final FloatBinaryOperator MULTIPLY = (x, y) -> x * y;
  static final FloatBinaryOperator DIVIDE   = (x, y) -> x / y;
  static final FloatBinaryOperator MODULO   = (x, y) -> x % y;

  private final ChimeraContext context;
  private final CompilerErrors errors;
  private final Scope topScope;

  private final Stack<Scope> scopeStack = new Stack<>();
  private final Stack<Selector> selectorStack = new Stack<>();

  private Scope scope;

  public Interpreter(ChimeraContext context, Scope topScope) {
    this.context = context;
    this.errors = context.getErrors();

    if (topScope == null) {
      topScope = Scope.createTopLevel();
    }

    scopeStack.push(topScope);

    this.topScope = topScope;
    this.scope = topScope;
  }

  public void warn(Location location, String format, Object... args) {
    errors.warn(location, format, args);
  }

  public void warn(String format, Object... args) {
    errors.warn(format, args);
  }

  public void error(Location location, String format, Object... args) {
    errors.error(location, format, args);
  }

  public void error(String format, Object... args) {
    errors.error(format, args);
  }

  public void log(Level level, Location location, String format, Object... args) {
    errors.log(level, location, format, args);
  }

  public void log(Level level, String format, Object... args) {
    errors.log(level, format, args);
  }

  private Scope pushScope(Scope scope) {
    scopeStack.push(scope);
    this.scope = scope;
    return scope;
  }

  private Scope pushScope() {
    Scope s = scope.pushFrame();
    return pushScope(s);
  }

  private void popScope() {
    scopeStack.pop();
    scope = scopeStack.peek();
  }

  @Override
  public Object variableExpr(VariableExpr expr) {
    Identifier variableName = expr.getVariableName();

    if (variableName == null) {
      return null;
    }

    Object o = scope.getVariable(variableName.getValue());

    if (o == null) {
      error(expr.getStart(), "Unknown variable %s", variableName.getValue());
    }

    return o;
  }

  @Override
  public Object stringLiteral(StringLiteral expr) {
    return expr.getValue();
  }

  @Override
  public Object numberLiteral(NumberLiteral expr) {
    Unit unit = Objects.requireNonNullElse(expr.getUnit(), Unit.NONE);
    return Primitive.create(expr.getValue().floatValue(), unit);
  }

  @Override
  public Object keywordLiteral(KeywordLiteral expr) {
    return expr.getKeyword();
  }

  @Override
  public Object identifier(Identifier expr) {
    String value = expr.getValue();

    if (Strings.isNullOrEmpty(value)) {
      return null;
    }

    Color c = NamedColor.named(value);
    if (c != null) {
      return c;
    }

    return value;
  }

  @Override
  public Object error(ErroneousExpr expr) {
    return null;
  }

  @Override
  public Object colorLiteral(ColorLiteral expr) {
    return expr.getColor();
  }

  @Override
  public Object callExpr(CallExpr expr) {
    List<Expression> arguments = expr.getArguments();
    Identifier functionName = expr.getFunctionName();

    String name = functionName.getValue();
    ScssFunction func = scope.getFunction(name);

    if (func == null) {
      error(expr.getStart(), "Unknown function %s", name);
      return null;
    }

    Argument[] argValues = new Argument[arguments.size()];
    for (int i = 0; i < arguments.size(); i++) {
      Expression argExpr = arguments.get(i);
      Object value = argExpr.visit(this);

      Argument arg = new Argument();
      arg.setArgumentIndex(i);
      arg.setValue(value);
      arg.setLocation(argExpr.getStart());
      arg.setErrors(errors);

      argValues[i] = arg;
    }

    // Test argument counts
    Range<Integer> argCount = func.argumentCount();
    if (argValues.length < argCount.getMinimum()) {
      error(expr.getStart(),
          "Too few arguments! Expected at least %s arguments, found %s",
          argCount.getMinimum(), argValues.length
      );
      return null;
    }
    if (argValues.length > argCount.getMaximum()) {
      error(expr.getStart(),
          "Too many arguments! Expected at most %s arguments, found %s",
          argCount.getMaximum(), argValues.length
      );
      return null;
    }

    try {
      return func.invoke(context, argValues);
    } catch (ScssInvocationException exc) {
      error(exc.getLocation(), "Failed to invoke %s: %s", name, exc.getMessage());
      return null;
    }
  }

  private IllegalStateException doNotCall() {
    return new IllegalStateException("This should not be called");
  }

  @Override
  public Object selector(RegularSelectorStatement selectorNode) {
    throw doNotCall();
  }

  @Override
  public Object selectorGroup(SelectorListStatement group) {
    throw doNotCall();
  }

  @Override
  public Object selectorMatchAll(MatchAllExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object anb(AnbExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object evenOdd(EvenOddKeyword expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorPseudoFunction(PseudoFunctionExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorPseudoClass(PseudoClassExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorAttribute(AttributeExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorId(IdExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorClassName(ClassNameExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorTagName(TagNameExpr expr) {
    throw doNotCall();
  }

  @Override
  public Object selectorNode(SelectorNodeStatement node) {
    throw doNotCall();
  }

  @Override
  public Object selectorNested(NestedSelector selector) {
    throw doNotCall();
  }

  @Override
  public Object rectangle(RectExpr expr) {
    var top = expr.getTop();
    var right = expr.getRight();
    var bottom = expr.getBottom();
    var left = expr.getLeft();

    Primitive topRes = Chimera.coerceValue(Primitive.class, top.visit(this));
    Primitive rightRes = Chimera.coerceValue(Primitive.class, right.visit(this));
    Primitive bottomRes = Chimera.coerceValue(Primitive.class, bottom.visit(this));
    Primitive leftRes = Chimera.coerceValue(Primitive.class, left.visit(this));

    topRes = reportInvalidSide(topRes, "top", top.getStart());
    rightRes = reportInvalidSide(rightRes, "right", right.getStart());
    bottomRes = reportInvalidSide(bottomRes, "bottom", bottom.getStart());
    leftRes = reportInvalidSide(leftRes, "left", left.getStart());

    return PrimitiveRect.create(topRes, rightRes, bottomRes, leftRes);
  }

  Primitive reportInvalidSide(Primitive prim, String side, Location l) {
    if (prim != null) {
      return prim;
    }

    error(
        l,
        "Invalid expression for %s side. Must be a primitive, or a reference to a primitive",
        side
    );

    return Primitive.ZERO;
  }

  @Override
  public Object important(ImportantMarker marker) {
    throw doNotCall();
  }

  @Override
  public Object unary(UnaryExpr expr) {
    Expression value = expr.getValue();
    UnaryOp op = expr.getOp();

    if (value == null) {
      return null;
    }

    Object o = value.visit(this);
    if (o == null) {
      return null;
    }

    if (op == UnaryOp.INVERT) {
      Boolean b = Chimera.coerceValue(Boolean.class, o);

      if (b == null) {
        return o;
      }

      return b;
    }

    Primitive prim = Chimera.coerceValue(Primitive.class, o);
    if (prim == null) {
      return null;
    }

    if (op == UnaryOp.PLUS) {
      return Primitive.create(+prim.getValue(), prim.getUnit());
    } else {
      return Primitive.create(-prim.getValue(), prim.getUnit());
    }
  }

  @Override
  public Object namespaced(NamespaceExpr expr) {
    String name = expr.getNamespace().getValue();
    Scope s = scope.getNamespaced(name);

    if (s == null) {
      error(expr.getStart(), "Unknown object %s", name);
    } else {
      pushScope(s);
    }

    Object o = expr.getTarget().visit(this);

    if (s != null) {
      popScope();
    }

    return o;
  }

  @Override
  public Object binary(BinaryExpr expr) {
    BinaryOp op = expr.getOp();
    Location start = expr.getStart();

    Object lv = expr.getLhs().visit(this);
    Object rv = expr.getRhs().visit(this);

    return switch (op) {
      case EQUAL -> Objects.equals(lv, rv);
      case NOT_EQUAL -> !Objects.equals(lv, rv);

      case OR -> booleanOp(lv, rv, OR);
      case AND -> booleanOp(lv, rv, AND);

      case DIV -> applyNumbers(start, lv, rv, DIVIDE);
      case MOD -> applyNumbers(start, lv, rv, MODULO);
      case MUL -> applyNumbers(start, lv, rv, MULTIPLY);

      case LT, GT, GTE, LTE -> runCompare(start, op, lv, rv);

      case PLUS -> stringConcatOrNumber(start, lv, rv, ADD, CONCAT);
      case MINUS -> stringConcatOrNumber(start, lv, rv, SUBTRACT, CONCAT_DASH);
    };
  }

  boolean booleanOp(Object l, Object r, BooleanBinaryOperator operator) {
    boolean b1 = Objects.requireNonNullElse(Chimera.coerceValue(Boolean.class, l), false);
    boolean b2 = Objects.requireNonNullElse(Chimera.coerceValue(Boolean.class, r), false);
    return operator.apply(b1, b2);
  }

  boolean runCompare(Location start, BinaryOp op, Object l, Object r) {
    int cmp = compareValues(start, l, r);

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

  int compareValues(Location start, Object l, Object r) {
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
      if (!testCompatability(start, pl.getUnit(), pr.getUnit())) {
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
      Location start,
      Object l,
      Object r,
      FloatBinaryOperator op,
      BinaryOperator<String> stringConcat
  ) {
    if (l instanceof Primitive && r instanceof Primitive) {
      return applyNumbers(start, l, r, op);
    }

    String ls = String.valueOf(l);
    String rs = String.valueOf(r);

    return stringConcat.apply(ls, rs);
  }

  Primitive applyNumbers(
      Location start,
      Object l,
      Object r,
      FloatBinaryOperator op
  ) {
    if (!(l instanceof Primitive pl) || !(r instanceof Primitive pr)) {
      error(start, "Operator requires 2 numeric values");
      return Primitive.ZERO;
    }

    testCompatability(start, pl.getUnit(), pr.getUnit());

    Unit left = pl.getUnit();
    Unit right = pr.getUnit();

    float fl = preEvalTranslate(pl);
    float fr = preEvalTranslate(pr);

    float result = op.apply(fl, fr);

    return postEval(result, left, right);
  }

  boolean testCompatability(Location l, Unit left, Unit right) {
    if (areUnitsCompatible(left, right)) {
      return true;
    }

    error(
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
  public Object variableDecl(VariableDecl decl) {
    Expression value = decl.getValue();

    if (value == null) {
      return null;
    }

    Object valueO = value.visit(this);
    if (valueO == null) {
      return null;
    }

    String name = decl.getName().getValue();
    scope.putVariable(name, valueO);

    return null;
  }

  @Override
  public PropertySet inlineStyle(InlineStyleStatement inline) {
    PropertySet set = new PropertySet();
    inline(inline, set);
    return set;
  }

  public void inline(InlineStyleStatement inline, PropertySet out) {
    Scope s = pushScope();
    s.setPropertyOutput(out);

    for (PropertyStatement property : inline.getProperties()) {
      property(property);
    }
  }

  @Override
  public ChimeraStylesheet sheet(SheetStatement sheet) {
    scope.setSheetBuilder(new ChimeraSheetBuilder(null));

    for (Statement statement : sheet.getStatements()) {
      statement.visit(this);
    }

    return scope.getSheetBuilder().build();
  }

  private Selector selectorFromStack() {
    if (selectorStack.isEmpty()) {
      return Selector.MATCH_ALL;
    }
    if (selectorStack.size() == 1) {
      return selectorStack.peek();
    }

    List<SelectorNode> nodes = new ArrayList<>();

    for (Selector selector : selectorStack) {
      if (selector instanceof SelectorNode node) {
        if (node.getCombinator() == Combinator.NEST) {
          SelectorNode prev;

          if (nodes.isEmpty()) {
            prev = new SelectorNode();
            nodes.add(prev);
          } else {
            prev = nodes.getLast();
          }

          Selector prevSelector = prev.getSelector();
          if (prevSelector == null) {
            prev.setSelector(selector);
          } else if (prevSelector instanceof SelectorList l) {
            SelectorList nlist = new SelectorList(l.getSize() + 1);
            nlist.addAll(l);
            nlist.add(selector);
            prev.setSelector(nlist);
          } else {
            SelectorList list = new SelectorList(2);
            list.setType(ListType.AND);
            list.setStyle(ListStyle.COMPACT);
            list.add(prevSelector);
            list.add(selector);
            prev.setSelector(list);
          }

          continue;
        }

        nodes.add(node.copy());
        continue;
      }

      SelectorNode n = new SelectorNode();
      n.setSelector(selector);
      n.setCombinator(Combinator.DESCENDANT);

      nodes.add(n);
    }

    return new RegularSelector(nodes.toArray(SelectorNode[]::new));
  }

  @Override
  public Void rule(RuleStatement rule) {
    if (scope.getSheetBuilder() == null) {
      return null;
    }

    SelectorExpression selectorExpr = rule.getSelector();
    Selector selector = selectorExpr.compile(errors);

    selectorStack.push(selector);

    selector = selectorFromStack();

    PropertySet properties = new PropertySet();

    Scope pre = scope;
    pushScope();

    scope.setPropertyOutput(properties);
    scope.setSheetBuilder(pre.getSheetBuilder());

    blockStatement(rule.getBody(), true);

    popScope();
    selectorStack.pop();

    Rule r = new Rule(selector, properties);
    scope.getSheetBuilder().addRule(r);

    return null;
  }

  @Override
  public Void property(PropertyStatement propertyStat) {
    Identifier propertyName = propertyStat.getPropertyName();
    if (propertyName == null) {
      return null;
    }

    String name = propertyName.getValue().toLowerCase();
    Property<Object> property = Properties.getByKey(name);

    if (property == null) {
      error(propertyStat.getStart(), "Unknown/unsupported property %s", name);
      return null;
    }

    Expression valExpr = propertyStat.getValue();

    if (valExpr == null) {
      return null;
    }

    Object value = valExpr.visit(this);
    String input = context.getInput(valExpr.getStart(), propertyStat.getEnd());

    Value<Object> sval = coerceCssValue(
        input,
        propertyStat.getImportant() != null,
        property,
        value,
        errors,
        valExpr.getStart()
    );

    if (sval == null) {
      return null;
    }

    PropertySet out = scope.getPropertyOutput();
    if (out != null) {
      out.setValue(property, sval);
    }

    return null;
  }

  @Override
  public Void returnStatement(ControlFlowStatement stat) {
    Expression expr = stat.getReturnValue();

    if (expr == null) {
      scope.setReturnValue(Nothing.INSTANCE);
      return null;
    }

    Object value = expr.visit(this);
    scope.setReturnValue(value);

    return null;
  }

  @Override
  public Void logStatement(LogStatement statement) {
    Expression expression = statement.getExpression();
    String name = statement.getName();
    Level level = statement.getLevel();

    int line = statement.getStart().line();

    if (expression == null) {
      log(level, "@%s:%s", name, line);
      return null;
    }

    Object o = expression.visit(this);
    log(level, "@%s:%s %s", name, line, o);

    return null;
  }

  @Override
  public Object importStatement(ImportStatement statement) {
    //error(statement.getStart(), "Not implemented");
    return null;
  }

  @Override
  public Object ifStatement(IfStatement statement) {
    Expression condition = statement.getCondition();
    Object evaluated = condition.visit(this);

    Boolean b = Chimera.coerceValue(Boolean.class, evaluated);
    if (b == null) {
      b = false;
    }

    if (b) {
      statement.getBody().visit(this);
    } else if (statement.getElseBody() != null) {
      statement.getElseBody().visit(this);
    }

    return null;
  }

  @Override
  public Void blockStatement(Block block) {
    return blockStatement(block, false);
  }

  private Void blockStatement(Block block, boolean ignoreControlFlow) {
    for (Statement statement : block.getStatements()) {
      statement.visit(this);

      if (scope.controlFlowBroken() && !ignoreControlFlow) {
        return null;
      }
    }

    return null;
  }
}

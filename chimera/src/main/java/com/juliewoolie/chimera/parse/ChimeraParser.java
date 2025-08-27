package com.juliewoolie.chimera.parse;

import static com.juliewoolie.chimera.parse.Token.AMPERSAND;
import static com.juliewoolie.chimera.parse.Token.ANGLE_LEFT;
import static com.juliewoolie.chimera.parse.Token.ANGLE_RIGHT;
import static com.juliewoolie.chimera.parse.Token.AT_ASSERT;
import static com.juliewoolie.chimera.parse.Token.AT_BREAK;
import static com.juliewoolie.chimera.parse.Token.AT_CONTINUE;
import static com.juliewoolie.chimera.parse.Token.AT_DEBUG;
import static com.juliewoolie.chimera.parse.Token.AT_ELSE;
import static com.juliewoolie.chimera.parse.Token.AT_ERROR;
import static com.juliewoolie.chimera.parse.Token.AT_FUNCTION;
import static com.juliewoolie.chimera.parse.Token.AT_IF;
import static com.juliewoolie.chimera.parse.Token.AT_IMPORT;
import static com.juliewoolie.chimera.parse.Token.AT_INCLUDE;
import static com.juliewoolie.chimera.parse.Token.AT_MIXIN;
import static com.juliewoolie.chimera.parse.Token.AT_PRINT;
import static com.juliewoolie.chimera.parse.Token.AT_RETURN;
import static com.juliewoolie.chimera.parse.Token.AT_WARN;
import static com.juliewoolie.chimera.parse.Token.BRACKET_CLOSE;
import static com.juliewoolie.chimera.parse.Token.BRACKET_OPEN;
import static com.juliewoolie.chimera.parse.Token.CARET_EQ;
import static com.juliewoolie.chimera.parse.Token.COLON;
import static com.juliewoolie.chimera.parse.Token.COMMA;
import static com.juliewoolie.chimera.parse.Token.DOLLAR_EQ;
import static com.juliewoolie.chimera.parse.Token.DOLLAR_SIGN;
import static com.juliewoolie.chimera.parse.Token.DOT;
import static com.juliewoolie.chimera.parse.Token.DOUBLECOLON;
import static com.juliewoolie.chimera.parse.Token.ELLIPSES;
import static com.juliewoolie.chimera.parse.Token.EQUALS;
import static com.juliewoolie.chimera.parse.Token.EQUAL_TO;
import static com.juliewoolie.chimera.parse.Token.EXCLAMATION;
import static com.juliewoolie.chimera.parse.Token.GTE;
import static com.juliewoolie.chimera.parse.Token.HASHTAG;
import static com.juliewoolie.chimera.parse.Token.HEX;
import static com.juliewoolie.chimera.parse.Token.HEX_ALPHA;
import static com.juliewoolie.chimera.parse.Token.HEX_SHORT;
import static com.juliewoolie.chimera.parse.Token.ID;
import static com.juliewoolie.chimera.parse.Token.INT;
import static com.juliewoolie.chimera.parse.Token.LTE;
import static com.juliewoolie.chimera.parse.Token.MINUS;
import static com.juliewoolie.chimera.parse.Token.NOT_EQUAL_TO;
import static com.juliewoolie.chimera.parse.Token.NUMBER;
import static com.juliewoolie.chimera.parse.Token.PERCENT;
import static com.juliewoolie.chimera.parse.Token.PLUS;
import static com.juliewoolie.chimera.parse.Token.SEMICOLON;
import static com.juliewoolie.chimera.parse.Token.SLASH;
import static com.juliewoolie.chimera.parse.Token.SQUARE_CLOSE;
import static com.juliewoolie.chimera.parse.Token.SQUARE_OPEN;
import static com.juliewoolie.chimera.parse.Token.SQUIGLY;
import static com.juliewoolie.chimera.parse.Token.SQUIG_CLOSE;
import static com.juliewoolie.chimera.parse.Token.SQUIG_EQ;
import static com.juliewoolie.chimera.parse.Token.SQUIG_OPEN;
import static com.juliewoolie.chimera.parse.Token.STAR;
import static com.juliewoolie.chimera.parse.Token.STAR_EQ;
import static com.juliewoolie.chimera.parse.Token.STRING;
import static com.juliewoolie.chimera.parse.Token.WALL_EQ;
import static com.juliewoolie.chimera.parse.Token.WHITESPACE;

import java.util.Objects;
import java.util.Stack;
import lombok.Getter;
import com.juliewoolie.chimera.parse.TokenStream.ParseMode;
import com.juliewoolie.chimera.parse.ast.AssertStatement;
import com.juliewoolie.chimera.parse.ast.BinaryExpr;
import com.juliewoolie.chimera.parse.ast.BinaryOp;
import com.juliewoolie.chimera.parse.ast.Block;
import com.juliewoolie.chimera.parse.ast.CallExpr;
import com.juliewoolie.chimera.parse.ast.ColorLiteral;
import com.juliewoolie.chimera.parse.ast.ControlFlowStatement;
import com.juliewoolie.chimera.parse.ast.ErroneousExpr;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.chimera.parse.ast.ExpressionStatement;
import com.juliewoolie.chimera.parse.ast.FunctionStatement;
import com.juliewoolie.chimera.parse.ast.FunctionStatement.FuncParameterStatement;
import com.juliewoolie.chimera.parse.ast.Identifier;
import com.juliewoolie.chimera.parse.ast.IfStatement;
import com.juliewoolie.chimera.parse.ast.ImportStatement;
import com.juliewoolie.chimera.parse.ast.ImportantMarker;
import com.juliewoolie.chimera.parse.ast.IncludeStatement;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.chimera.parse.ast.Keyword;
import com.juliewoolie.chimera.parse.ast.KeywordLiteral;
import com.juliewoolie.chimera.parse.ast.ListLiteral;
import com.juliewoolie.chimera.parse.ast.LogStatement;
import com.juliewoolie.chimera.parse.ast.MixinStatement;
import com.juliewoolie.chimera.parse.ast.NamespaceExpr;
import com.juliewoolie.chimera.parse.ast.NumberLiteral;
import com.juliewoolie.chimera.parse.ast.PropertyStatement;
import com.juliewoolie.chimera.parse.ast.RegularSelectorStatement;
import com.juliewoolie.chimera.parse.ast.RuleStatement;
import com.juliewoolie.chimera.parse.ast.SelectorExpression;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.AnbExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.AttributeExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.EvenOdd;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.EvenOddKeyword;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.IdExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.MatchAllExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.NestedSelector;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.PseudoClassExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.PseudoElementExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.PseudoFunctionExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.TagNameExpr;
import com.juliewoolie.chimera.parse.ast.SelectorListStatement;
import com.juliewoolie.chimera.parse.ast.SelectorNodeStatement;
import com.juliewoolie.chimera.parse.ast.SheetStatement;
import com.juliewoolie.chimera.parse.ast.Statement;
import com.juliewoolie.chimera.parse.ast.StringLiteral;
import com.juliewoolie.chimera.parse.ast.UnaryExpr;
import com.juliewoolie.chimera.parse.ast.UnaryOp;
import com.juliewoolie.chimera.parse.ast.VariableDecl;
import com.juliewoolie.chimera.parse.ast.VariableExpr;
import com.juliewoolie.chimera.selector.AttributeOperation;
import com.juliewoolie.chimera.selector.Combinator;
import com.juliewoolie.dom.style.Color;
import com.juliewoolie.dom.style.NamedColor;
import com.juliewoolie.dom.style.Primitive.Unit;
import org.slf4j.event.Level;

public class ChimeraParser {

  @Getter
  private final TokenStream stream;

  @Getter
  private final CompilerErrors errors;

  private final Stack<ParserScope> scopeStack = new Stack<>();

  public ChimeraParser(String buffer) {
    this(new StringBuffer(buffer));
  }

  public ChimeraParser(StringBuffer buffer) {
    this.errors = new CompilerErrors(buffer);
    this.stream = new TokenStream(buffer, errors);
  }

  public ChimeraContext createContext() {
    ChimeraContext ctx = new ChimeraContext(stream.getInput());
    ctx.setErrors(errors);
    return ctx;
  }

  public ParserScope scope() {
    if (scopeStack.isEmpty()) {
      return ParserScope.TOP_LEVEL;
    }

    return scopeStack.peek();
  }

  public void pushScope(ParserScope scope) {
    scopeStack.push(scope);
  }

  public void popScope() {
    scopeStack.pop();
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

  public void skipUnexpectedWhitespace() {
    if (!matches(WHITESPACE)) {
      return;
    }

    unexpectedWhitespace(peek().location());
    next();
  }

  public void unexpectedWhitespace(Location location) {
    error(location, "Unexpected whitespace");
  }

  public Token expectId(String value) {
    Token t = expect(ID);
    if (t == null || !t.value().equalsIgnoreCase(value)) {
      return null;
    }

    return t;
  }

  public Token expect(int tokenType) {
    Token next = next();

    if (next.type() != tokenType) {
      expectedToken(next.location(), tokenType, next.type());
      return null;
    }

    return next;
  }

  private void expectedToken(Location l, int expected, int found) {
    error(l, "Expected %s, but found %s", Token.typeToString(expected), Token.typeToString(found));
  }

  public Token peek() {
    return stream.peek();
  }

  public Token next() {
    return stream.next();
  }

  public boolean matchesId(String value) {
    if (!matches(ID)) {
      return false;
    }

    Token p = peek();
    return p.value().equalsIgnoreCase(value);
  }

  public boolean matches(int... tokenTypes) {
    Token p = peek();

    for (int tokenType : tokenTypes) {
      if (tokenType == p.type()) {
        return true;
      }
    }

    return false;
  }

  public boolean hasNext() {
    return stream.hasNext();
  }

  public Location location() {
    return stream.location();
  }

  public ParseMode popMode() {
    return stream.popMode();
  }

  public void pushMode(ParseMode parseMode) {
    stream.pushMode(parseMode);
  }

  private void skipWhitespace() {
    while (matches(WHITESPACE)) {
      next();
    }
  }

  /* --------------------------- Selectors ---------------------------- */

  boolean isSelectorNext() {
    // .className
    // #id
    // [attr=value]
    // > <selector>
    // + <selector>
    // & <selector>
    // ~ <selector>
    // *
    // :pseudo-class
    if (matches(DOT, HASHTAG, SQUARE_OPEN, ANGLE_RIGHT, PLUS, AMPERSAND, SQUIGLY, STAR, COLON)) {
      return true;
    }
    if (!matches(ID)) {
      return false;
    }

    // An ident followed by anything other than a ':' or '('
    // token is also considered a nested selector.
    try (StreamState state = stream.saveState()) {
      next();

      if (!hasNext()) {
        return false;
      }
      if (!matches(COLON, BRACKET_OPEN, EQUALS)) {
        return true;
      }

      var l = next().location();
      if (!matches(ID)) {
        return false;
      }

      next();
      if (matches(SEMICOLON)) {
        return false;
      }

      return matches(
          SQUIG_OPEN,
          DOT,
          HASHTAG,
          SQUARE_OPEN,
          ANGLE_RIGHT,
          PLUS,
          SQUIGLY,
          STAR,
          COLON
      );
    }
  }

  SelectorExpression selector() {
    pushMode(ParseMode.SELECTOR);
    ParserScope scope = scope();

    pushScope(ParserScope.SELECTOR);

    SelectorExpression expr;
    skipWhitespace();

    if (matches(AMPERSAND)) {
      Token start = next();
      SelectorListStatement list = selectorList();

      NestedSelector nested = new NestedSelector();
      nested.setStart(start.location());
      nested.setEnd(list.getEnd());
      nested.setSelector(list);

      if (scope != ParserScope.RULE) {
        error(start.location(), "Nesting operator not allowed here");
      }

      expr = nested;
    } else {
      expr = selectorList();
    }

    popMode();
    popScope();

    return expr;
  }

  SelectorListStatement selectorList() {
    SelectorListStatement group = new SelectorListStatement();
    RegularSelectorStatement selector = regularSelector();

    group.setStart(selector.getStart());
    group.getSelectors().add(selector);

    while (matches(COMMA)) {
      next();
      skipWhitespace();
      selector = regularSelector();
      skipWhitespace();

      group.getSelectors().add(selector);
      group.setEnd(selector.getEnd());
    }

    return group;
  }

  RegularSelectorStatement regularSelector() {
    RegularSelectorStatement stat = new RegularSelectorStatement();

    while (true) {
      SelectorNodeStatement node = selectorNode();
      if (node == null) {
        break;
      }

      stat.getNodes().add(node);
    }

    return stat;
  }

  SelectorNodeStatement selectorNode() {
    SelectorNodeStatement stat = new SelectorNodeStatement();
    stat.setStart(peek().location());

    skipWhitespace();
    Combinator combinator = combinator();
    skipWhitespace();
    stat.setCombinator(combinator);

    int count = 0;

    while (true) {
      SelectorExpression expr = primarySelector();
      if (expr == null) {
        break;
      }

      stat.setEnd(expr.getEnd());
      stat.getExpressions().add(expr);

      count++;
    }

    if (count < 1) {
      return null;
    }

    return stat;
  }

  Combinator combinator() {
    return switch (peek().type()) {
      case PLUS -> {
        next();
        yield Combinator.DIRECT_SIBLING;
      }
      case SQUIGLY -> {
        next();
        yield Combinator.SIBLING;
      }
      case ANGLE_RIGHT -> {
        next();
        yield Combinator.PARENT;
      }

      default -> Combinator.DESCENDANT;
    };
  }

  SelectorExpression primarySelector() {
    Token t = peek();

    switch (t.type()) {
      case STAR -> {
        next();
        MatchAllExpr expr = new MatchAllExpr();
        expr.setStart(t.end());
        expr.setEnd(t.end());
        return expr;
      }
      case DOT -> {
        next();
        skipUnexpectedWhitespace();

        ClassNameExpr className = new ClassNameExpr();
        Identifier id = id();

        className.setClassName(id);
        className.setStart(t.location());
        className.setEnd(id.getEnd());

        return className;
      }
      case HASHTAG -> {
        next();
        skipUnexpectedWhitespace();

        IdExpr id = new IdExpr();
        Identifier idVal = id();

        id.setStart(t.location());
        id.setId(idVal);
        id.setEnd(idVal.getEnd());

        return id;
      }
      case ID -> {
        TagNameExpr tagName = new TagNameExpr();
        Identifier id = id();

        tagName.setTagName(id);
        tagName.setStart(t.location());
        tagName.setEnd(id.getEnd());

        return tagName;
      }
      case SQUARE_OPEN -> {
        return attributeExpr();
      }
      case COLON -> {
        return pseudoClass();
      }
      case DOUBLECOLON -> {
        return pseudoElement();
      }
      default -> {
        return null;
      }
    }
  }

  PseudoElementExpr pseudoElement() {
    Token start = expect(DOUBLECOLON);
    PseudoElementExpr expr = new PseudoElementExpr();
    expr.setStart(start.location());

    Identifier name = id();
    expr.setName(name);
    expr.setEnd(name.getEnd());

    return expr;
  }

  AttributeExpr attributeExpr() {
    Token start = next();

    skipUnexpectedWhitespace();

    AttributeExpr expr = new AttributeExpr();
    expr.setAttributeName(id());
    expr.setStart(start.location());

    skipUnexpectedWhitespace();

    if (matches(SQUARE_CLOSE)) {
      Token end = next();
      expr.setOperation(AttributeOperation.HAS);
      expr.setEnd(end.end());
      return expr;
    }

    Token peek = next();
    AttributeOperation op = switch (peek.type()) {
      case EQUALS -> AttributeOperation.EQUALS;
      case WALL_EQ -> AttributeOperation.DASH_PREFIXED;
      case SQUIG_EQ -> AttributeOperation.CONTAINS_WORD;
      case CARET_EQ -> AttributeOperation.STARTS_WITH;
      case DOLLAR_EQ -> AttributeOperation.ENDS_WITH;
      case STAR_EQ -> AttributeOperation.CONTAINS_SUBSTRING;

      default -> {
        error(peek.location(), "Unexpected comparison operator... falling back to '=' operator");
        yield AttributeOperation.EQUALS;
      }
    };

    skipUnexpectedWhitespace();

    StringLiteral literal = stringLiteral();

    expr.setOperation(op);
    expr.setValue(literal);

    skipUnexpectedWhitespace();

    Token endToken = expect(SQUARE_CLOSE);
    expr.setEnd(endToken.end());

    return expr;
  }

  SelectorExpression pseudoClass() {
    Token start = expect(COLON);

    skipUnexpectedWhitespace();
    Identifier className = id();

    if (!matches(BRACKET_OPEN)) {
      PseudoClassExpr classExpr = new PseudoClassExpr();
      classExpr.setPseudoClass(className);
      classExpr.setEnd(className.getEnd());
      classExpr.setStart(start.location());
      return classExpr;
    }

    next();
    skipWhitespace();

    PseudoFunctionExpr expr = new PseudoFunctionExpr();
    expr.setFunctionName(className);
    expr.setStart(start.location());

    String classNameString = className.getValue();
    if (classNameString.equalsIgnoreCase("is") || classNameString.equalsIgnoreCase("not")) {
      SelectorListStatement group = selectorList();
      expr.setSelectorGroup(group);
    } else {
      anbExpr(expr);
    }

    skipWhitespace();
    Token endToken = expect(BRACKET_CLOSE);

    expr.setEnd(endToken.end());

    return expr;
  }

  void anbExpr(PseudoFunctionExpr expr) {
    if (matches(ID)) {
      Token p = peek();
      String val = p.value();

      assert val != null;

      if (val.equalsIgnoreCase("even") || val.equalsIgnoreCase("odd")) {
        EvenOddKeyword e = new EvenOddKeyword();
        e.setEvenOdd(val.equalsIgnoreCase("even") ? EvenOdd.EVEN : EvenOdd.ODD);
        e.setStart(p.location());
        e.setEnd(p.end());
        next();

        expr.setIndex(e);
        return;
      } else if (val.equalsIgnoreCase("n")) {
        anbIndexSelector(expr);
        return;
      } else {
        error(p.location(), "Unexpected token");
        return;
      }
    }

    anbIndexSelector(expr);
  }

  void anbIndexSelector(PseudoFunctionExpr expr) {
    anb(expr);
    skipWhitespace();

    if (matches(ID) && peek().value().equalsIgnoreCase("of")) {
      next();
      skipWhitespace();

      SelectorListStatement group = selectorList();
      expr.setSelectorGroup(group);
    }
  }

  void anb(PseudoFunctionExpr expr) {
    Token peek = peek();

    NumberLiteral a;
    NumberLiteral b;

    if (matches(MINUS)) {
      next();
      Token t = expectId("n");

      a = new NumberLiteral();
      a.setStart(t.location());
      a.setValue(-1);

      if (matches(PLUS)) {
        next();
        b = numberLiteral();
      } else {
        b = null;
      }
    } else if (matches(NUMBER) || matches(INT)) {
      NumberLiteral num = numberLiteral();

      if (matchesId("n")) {
        a = num;
        next();

        if (matches(PLUS)) {
          next();
          b = numberLiteral();
        } else {
          b = null;
        }
      } else {
        a = null;
        b = num;
      }
    } else if (matchesId("n")) {
      Token t = next();

      a = new NumberLiteral();
      a.setValue(1);
      a.setStart(t.location());

      expect(PLUS);
      b = numberLiteral();
    } else {
      error(peek.location(), "Invalid An+B expression");
      a = null;
      b = null;
    }

    AnbExpr anbExpr = new AnbExpr();
    anbExpr.setStart(peek.location());
    anbExpr.setEnd(peek().location());
    anbExpr.setA(a);
    anbExpr.setB(b);

    expr.setIndex(anbExpr);
  }

  /* --------------------------- Style sheets ---------------------------- */

  public SheetStatement stylesheet() {
    SheetStatement stat = new SheetStatement();
    stat.setStart(Location.START);

    while (hasNext()) {
      Statement statement = statement();
      stat.getStatements().add(statement);
    }

    stat.setEnd(peek().location());
    return stat;
  }

  /* --------------------------- Inline style ---------------------------- */

  public InlineStyleStatement inlineStyle() {
    InlineStyleStatement stat = new InlineStyleStatement();
    stat.setStart(peek().location());

    pushScope(ParserScope.INLINE);

    while (hasNext()) {
      PropertyStatement property = propertyStatement();
      stat.getProperties().add(property);
    }

    popScope();

    stat.setEnd(peek().location());
    return stat;
  }

  /* --------------------------- Statements ---------------------------- */

  void expectEndOfStatement() {
    if (matches(SEMICOLON)) {
      next();
      return;
    }
    
    Token p = peek();
    warn(p.location(), "Expected ';' to end statement, found %s", p.info());
  }

  IfStatement ifStatement() {
    Token start = expect(AT_IF);

    IfStatement stat = new IfStatement();
    stat.setStart(start.location());

    Expression expr = expr();
    stat.setCondition(expr);

    if (matches(SQUIG_OPEN)) {
      Block block = blockStatement();
      stat.setBody(block);
    }

    if (matches(AT_ELSE)) {
      next();
      Block elseBody = blockStatement();
      stat.setElseBody(elseBody);
    }

    return stat;
  }

  Block blockStatement() {
    Block block = new Block();
    Token start = expect(SQUIG_OPEN);

    block.setStart(start.location());

    while (hasNext() && !matches(SQUIG_CLOSE)) {
      Statement statement = statement();
      block.getStatements().add(statement);
    }

    Token end = expect(SQUIG_CLOSE);
    block.setEnd(end.end());

    return block;
  }

  LogStatement logStatement() {
    Token t = peek();

    LogStatement stat = new LogStatement();
    stat.setStart(t.location());

    switch (t.type()) {
      case AT_PRINT:
        stat.setName("print");
        stat.setLevel(Level.INFO);
        next();
        break;
      case AT_WARN:
        stat.setName("warn");
        stat.setLevel(Level.WARN);
        next();
        break;
      case AT_ERROR:
        stat.setName("error");
        stat.setLevel(Level.ERROR);
        next();
        break;
      case AT_DEBUG:
        stat.setName("debug");
        stat.setLevel(Level.DEBUG);
        next();
        break;

      default:
        expectedToken(t.location(), AT_PRINT, t.type());
        stat.setEnd(t.end());
        return stat;
    }

    if (matches(SEMICOLON)) {
      next();
      stat.setEnd(t.end());
      return stat;
    }

    Expression expr = expr();
    stat.setExpression(expr);

    expectEndOfStatement();

    return stat;
  }

  ControlFlowStatement controlFlowStatement() {
    Token t = peek();
    ControlFlow flow;

    switch (t.type()) {
      case AT_RETURN:
        next();
        flow = ControlFlow.RETURN;
        break;
      case AT_BREAK:
        next();
        flow = ControlFlow.BREAK;
        break;
      case AT_CONTINUE:
        next();
        flow = ControlFlow.CONTINUE;
        break;

      default:
        expectedToken(t.location(), AT_RETURN, t.type());
        return null;
    }

    ControlFlowStatement stat = new ControlFlowStatement();
    stat.setStart(t.location());
    stat.setFlowType(flow);

    if (matches(SEMICOLON)) {
      stat.setEnd(t.end());
    } else if (peek().location().line() == t.location().line()) {
      Expression expr = expr();
      stat.setReturnValue(expr);
    }

    expectEndOfStatement();

    if (flow == ControlFlow.RETURN) {
      if (scope() != ParserScope.FUNCTION) {
        error(t.location(), "@return not allowed here");
        stat.setInvalid(true);
      }
    } else if (scope() != ParserScope.LOOP) {
      stat.setInvalid(true);

      if (flow == ControlFlow.CONTINUE) {
        error(t.location(), "@continue not allowed here");
      } else {
        error(t.location(), "@break not allowed here");
      }
    }

    return stat;
  }

  ImportStatement importStatement() {
    Token start = expect(AT_IMPORT);

    ImportStatement stat = new ImportStatement();
    stat.setStart(start.location());

    StringLiteral path = stringLiteral();
    stat.setImportPath(path);
    stat.setEnd(path.getEnd());

    if (scope() != ParserScope.TOP_LEVEL) {
      stat.setInvalid(true);
      error(start.location(), "@import not allowed here");
    }

    return stat;
  }

  VariableDecl variableDecl() {
    Token start = expect(DOLLAR_SIGN);
    Identifier name = id();

    VariableDecl decl = new VariableDecl();
    decl.setStart(start.location());
    decl.setName(name);

    if (!matches(COLON)) {
      expectedToken(peek().location(), COLON, peek().type());
      return decl;
    }

    next();

    Expression value = expr();
    decl.setValue(value);
    decl.setEnd(value.getEnd());

    expectEndOfStatement();
    return decl;
  }

  AssertStatement assertStatement() {
    Token start = expect(AT_ASSERT);
    AssertStatement stat = new AssertStatement();
    stat.setStart(start.location());

    Expression condition = expr();
    stat.setCondition(condition);
    stat.setEnd(condition.getEnd());

    if (matches(COLON)) {
      next();
      Expression message = expr();
      stat.setMessage(message);
      stat.setEnd(message.getEnd());
    }

    expectEndOfStatement();

    return stat;
  }

  FunctionStatement functionStatement() {
    Token start = expect(AT_FUNCTION);
    FunctionStatement statement = new FunctionStatement();
    statement.setStart(start.location());
    statement.setFunctionName(id());

    expect(BRACKET_OPEN);
    boolean varargSet = false;

    while (hasNext() && !matches(BRACKET_CLOSE)) {
      while (matches(COMMA)) {
        next();
      }

      FuncParameterStatement param = functionParameter();
      statement.getParameters().add(param);

      if (param.isVarargs()) {
        if (varargSet) {
          error(param.getStart(), "No more arguments allowed, varargs parameter declared");
        }

        varargSet = true;
      }
    }

    expect(BRACKET_CLOSE);

    Block block = blockStatement();
    statement.setBody(block);
    statement.setEnd(block.getEnd());

    return statement;
  }

  FuncParameterStatement functionParameter() {
    Token start = expect(DOLLAR_SIGN);
    Identifier id = id();

    FuncParameterStatement param = new FuncParameterStatement();
    param.setStart(start.location());
    param.setName(id);

    if (matches(ELLIPSES)) {
      next();
      param.setVarargs(true);
    } else if (matches(COLON)) {
      Expression value = expr();
      param.setDefaultValue(value);
    }

    return param;
  }

  RuleStatement rule() {
    RuleStatement stat = new RuleStatement();
    SelectorExpression selector = selector();

    stat.setStart(selector.getStart());
    stat.setSelector(selector);

    pushScope(ParserScope.RULE);
    Block block = blockStatement();
    popScope();

    stat.setBody(block);

    ParserScope scope = scope();
    if (scope != ParserScope.RULE && scope != ParserScope.TOP_LEVEL) {
      error(stat.getStart(), "Style rule not allowed here");
    }

    return stat;
  }

  PropertyStatement propertyStatement() {
    Identifier propertyName = id();

    if (matches(COLON)) {
      next();
    } else if (matches(EQUALS)) {
      // idk, maybe they confused the two, just accept and issue error
      Token t = next();
      error(t.location(), "Invalid assignment operator '='");
    } else {
      // Assume ':' is just missing and next is value
      error(propertyName.getEnd(), "Expected ':' assignment operator");
    }

    Expression value = expr();

    PropertyStatement stat = new PropertyStatement();

    stat.setStart(propertyName.getStart());
    stat.setEnd(value.getEnd());

    stat.setValue(value);
    stat.setPropertyName(propertyName);

    ImportantMarker marker = importantMarker();
    stat.setImportant(marker);

    ParserScope scope = scope();

    if (marker != null && scope == ParserScope.INLINE) {
      error(marker.getStart(), "'!important' not allowed here");
    }
    if (scope != ParserScope.RULE && scope != ParserScope.INLINE) {
      error(propertyName.getStart(), "Property declaration not allowed here");
    }

    expectEndOfStatement();

    return stat;
  }

  public ImportantMarker importantMarker() {
    if (!matches(EXCLAMATION)) {
      return null;
    }

    Token exclaim = next();
    Token idToken = expect(ID);

    if (idToken != null && idToken.value().equalsIgnoreCase("important")) {
      ImportantMarker stat = new ImportantMarker();
      stat.setStart(exclaim.location());
      stat.setEnd(idToken.end());

      return stat;
    }

    return null;
  }

  MixinStatement mixinStatement() {
    Token start = expect(AT_MIXIN);
    MixinStatement stat = new MixinStatement();
    stat.setStart(start.location());

    Identifier id = id();
    Block body = blockStatement();

    stat.setName(id);
    stat.setBody(body);
    stat.setEnd(body.getEnd());

    return stat;
  }

  IncludeStatement includeStatement() {
    Token start = expect(AT_INCLUDE);
    IncludeStatement stat = new IncludeStatement();

    Identifier id = id();

    stat.setStart(start.location());
    stat.setName(id);
    stat.setEnd(id.getEnd());

    expectEndOfStatement();

    return stat;
  }

  Statement statement() {
    if (isSelectorNext()) {
      return rule();
    }

    Token peek = peek();

    return switch (peek.type()) {
      case DOLLAR_SIGN -> variableDecl();
      case AT_IF -> ifStatement();
      case AT_PRINT, AT_DEBUG, AT_WARN, AT_ERROR -> logStatement();
      case AT_RETURN, AT_BREAK, AT_CONTINUE -> controlFlowStatement();
      case AT_IMPORT -> importStatement();
      case AT_FUNCTION -> functionStatement();
      case AT_ASSERT -> assertStatement();
      case AT_MIXIN -> mixinStatement();
      case AT_INCLUDE -> includeStatement();

      case ID -> {
        if (scope() == ParserScope.TOP_LEVEL) {
          yield rule();
        }

        boolean property;

        try (StreamState stat = stream.saveState()) {
          next();
          property = matches(COLON);
        }

        if (property) {
          yield propertyStatement();
        }

        Expression expr = expr();

        ExpressionStatement stat = new ExpressionStatement();
        stat.setStart(expr.getStart());
        stat.setEnd(expr.getEnd());
        stat.setExpr(expr);

        expectEndOfStatement();

        yield stat;
      }

      default -> {
        error(peek.location(), "Invalid/unsupported statement");
        yield null;
      }
    };
  }

  /* --------------------------- Expressions ---------------------------- */

  private boolean isNextExpression() {
    return matches(
        ID,
        PLUS,
        MINUS,
        BRACKET_OPEN,
        NUMBER,
        INT,
        STRING,
        DOLLAR_SIGN,
        HEX,
        HEX_SHORT,
        HEX_ALPHA,
        SQUARE_OPEN
    );
  }

  private boolean isSkippableListComma() {
    return matches(COMMA) && scope() != ParserScope.CALL_EXPR;
  }

  private boolean isArrayLiteralPart(Expression expr) {
    if (expr.getStart().line() != peek().location().line()) {
      return false;
    }
    if (matches(SEMICOLON, BRACKET_CLOSE)) {
      return false;
    }
    if (!hasNext()) {
      return false;
    }
    if (!isNextExpression()) {
      return false;
    }

    return scope() != ParserScope.CALL_EXPR;
  }

  public Expression expr() {
    try {
      pushMode(ParseMode.VALUES);
      return listExpr();
    } finally {
      popMode();
    }
  }

  Expression listExpr() {
    Expression expr = logicOr();

    if (!isArrayLiteralPart(expr)) {
      return expr;
    }

    ListLiteral literal = new ListLiteral();
    literal.setStart(expr.getStart());
    literal.getValues().add(expr);

    if (isSkippableListComma()) {
      next();
    }

    while (isArrayLiteralPart(expr)) {
      expr = logicOr();
      literal.setEnd(expr.getEnd());
      literal.getValues().add(expr);

      if (isSkippableListComma()) {
        next();
      }
    }

    return literal;
  }

  Expression logicOr() {
    Expression e = logicAnd();
    Location start = e.getStart();

    while (matchesId("or")) {
      next();

      Expression rhs = logicAnd();

      BinaryExpr expr = new BinaryExpr();
      expr.setOp(BinaryOp.OR);
      expr.setLhs(e);
      expr.setRhs(rhs);
      expr.setStart(start);
      expr.setEnd(rhs.getEnd());

      e = expr;
    }

    return e;
  }

  Expression logicAnd() {
    Expression e = equalityExpr();
    Location start = e.getStart();

    while (matchesId("and")) {
      next();

      Expression rhs = equalityExpr();

      BinaryExpr expr = new BinaryExpr();
      expr.setOp(BinaryOp.AND);
      expr.setLhs(e);
      expr.setRhs(rhs);
      expr.setStart(start);
      expr.setEnd(rhs.getEnd());

      e = expr;
    }

    return e;
  }

  Expression equalityExpr() {
    Expression e = comparisonExpr();
    Location start = e.getStart();

    while (matches(EQUAL_TO, NOT_EQUAL_TO)) {
      BinaryOp op = next().type() == EQUAL_TO
          ? BinaryOp.EQUAL
          : BinaryOp.NOT_EQUAL;

      Expression rhs = comparisonExpr();
      BinaryExpr expr = new BinaryExpr();
      expr.setRhs(rhs);
      expr.setLhs(e);
      expr.setOp(op);
      expr.setStart(start);
      expr.setEnd(rhs.getEnd());

      e = expr;
    }

    return e;
  }

  Expression comparisonExpr() {
    Expression e = additiveExpr();
    Location start = e.getStart();

    while (matches(ANGLE_LEFT, ANGLE_RIGHT, GTE, LTE)) {
      BinaryOp op = switch (next().type()) {
        case ANGLE_LEFT -> BinaryOp.LT;
        case ANGLE_RIGHT -> BinaryOp.GT;
        case LTE -> BinaryOp.LTE;
        default -> BinaryOp.GTE;
      };

      Expression rhs = additiveExpr();
      BinaryExpr expr = new BinaryExpr();
      expr.setStart(start);
      expr.setLhs(e);
      expr.setRhs(rhs);
      expr.setOp(op);
      expr.setEnd(rhs.getEnd());

      e = expr;
    }

    return e;
  }

  Expression additiveExpr() {
    Expression e = multiplicativeExpr();
    Location start = e.getStart();

    while (matches(PLUS, MINUS)) {
      BinaryOp binOp = next().type() == PLUS
          ? BinaryOp.PLUS
          : BinaryOp.MINUS;

      BinaryExpr bin = new BinaryExpr();
      Expression rhs = multiplicativeExpr();

      bin.setStart(start);
      bin.setRhs(rhs);
      bin.setLhs(e);
      bin.setOp(binOp);
      bin.setEnd(rhs.getEnd());

      e = bin;
    }

    return e;
  }

  Expression multiplicativeExpr() {
    Expression e = unaryExpr();
    var start = e.getStart();

    while (matches(SLASH, STAR, PERCENT)) {
      BinaryOp binOp = switch (next().type()) {
        case SLASH -> BinaryOp.DIV;
        case STAR -> BinaryOp.MUL;
        default -> BinaryOp.MOD;
      };

      BinaryExpr bin = new BinaryExpr();
      Expression rhs = unaryExpr();

      bin.setStart(start);
      bin.setLhs(e);
      bin.setRhs(rhs);
      bin.setEnd(rhs.getEnd());
      bin.setOp(binOp);

      e = bin;
    }

    return e;
  }

  Expression unaryExpr() {
    if (!matches(PLUS, MINUS) && !matchesId("not")) {
      Expression expr = namespacedExpr();

      if (matches(ELLIPSES)) {
        UnaryExpr unary = new UnaryExpr();
        unary.setStart(expr.getStart());
        unary.setOp(UnaryOp.SPREAD);
        unary.setValue(expr);

        Location end = next().end();
        unary.setEnd(end);

        return unary;
      }

      return expr;
    }

    UnaryOp op = switch (peek().type()) {
      case ID -> UnaryOp.INVERT;
      case MINUS -> UnaryOp.MINUS;
      case PLUS -> UnaryOp.PLUS;
      default -> throw new IllegalStateException();
    };
    Location start = next().location();

    Expression expr = namespacedExpr();

    UnaryExpr unary = new UnaryExpr();
    unary.setStart(start);
    unary.setEnd(expr.getEnd());
    unary.setValue(expr);
    unary.setOp(op);

    return unary;
  }

  Expression namespacedExpr() {
    Expression prim = primaryExpr();

    if (!(prim instanceof Identifier id)) {
      return prim;
    }

    if (!matches(DOT)) {
      return prim;
    }

    NamespaceExpr expr = new NamespaceExpr();
    expr.setStart(prim.getStart());
    expr.setNamespace(id);

    next();

    Expression rhsExpr = primaryExpr();
    expr.setTarget(rhsExpr);
    expr.setEnd(rhsExpr.getEnd());

    return expr;
  }

  Expression primaryExpr() {
    Token peek = peek();

    switch (peek.type()) {
      case ID:
        return fromId(peek);

      case STRING:
        return stringLiteral();

      case DOLLAR_SIGN:
        return variableExpr();

      case INT:
      case NUMBER:
        return numberLiteral();

      case HEX:
      case HEX_ALPHA:
      case HEX_SHORT:
        return hexExpr();

      case BRACKET_OPEN:
        next();
        Expression expr = logicOr();
        expect(BRACKET_CLOSE);
        return expr;

      case SQUARE_OPEN:
        return bracedListLiteral();

      default:
        ErroneousExpr err = new ErroneousExpr();
        err.setToken(next());
        err.setStart(peek.location());
        err.setEnd(peek.end());
        return err;
    }
  }

  Expression bracedListLiteral() {
    ListLiteral literal = new ListLiteral();
    Token start = expect(SQUARE_OPEN);

    literal.setStart(start.location());

    while (hasNext() && !matches(SQUARE_CLOSE)) {
      Expression expr = logicOr();
      literal.getValues().add(expr);

      while (matches(COMMA)) {
        next();
      }
    }

    Token end = expect(SQUARE_CLOSE);
    literal.setEnd(end.end());

    return literal;
  }

  Expression hexExpr() {
    Token t = peek();

    ColorLiteral color = new ColorLiteral();
    color.setStart(t.location());

    String hexLiteral;

    switch (t.type()) {
      case HEX_SHORT:
        StringBuilder builder = new StringBuilder();
        for (char c : t.value().toCharArray()) {
          builder.append(c);
          builder.append(c);
        }
        hexLiteral = "ff" + builder;
        next();
        break;

      case HEX:
        hexLiteral = "ff" + t.value();
        next();
        break;

      case HEX_ALPHA:
        // Color constructor expects ARGB, input is expected to be RGBA,
        // Need to flip some bits

        String rgba = t.value();
        assert rgba != null;

        String alpha = rgba.substring(rgba.length() - 2);
        hexLiteral = alpha + (rgba.substring(0, rgba.length() - 2));

        next();
        break;

      default:
        color.setColor(NamedColor.TRANSPARENT);
        expectedToken(t.location(), HEX, t.type());
        return color;
    }

    int argb = Integer.parseUnsignedInt(hexLiteral, 16);
    Color c = Color.argb(argb);

    color.setColor(c);
    color.setEnd(t.end());

    return color;
  }

  NumberLiteral numberLiteral() {
    Token numberToken = peek();
    Number number;

    if (numberToken.type() == NUMBER || numberToken.type() == INT) {
      next();

      if (numberToken.type() == INT) {
        number = Integer.parseInt(numberToken.value());
      } else {
        number = Float.parseFloat(numberToken.value());
      }
    } else {
      number = 0.0f;
      expectedToken(numberToken.location(), NUMBER, numberToken.type());
    }

    NumberLiteral num = new NumberLiteral();
    num.setStart(numberToken.location());
    num.setEnd(numberToken.end());
    num.setValue(number);
    num.setUnit(Unit.NONE);

    Unit unit;
    Token peek = peek();

    // Ensure no whitespace between number and next token
    if (peek.location().cursor() != numberToken.end().cursor() || scope() == ParserScope.SELECTOR) {
      return num;
    }

    if (peek.type() == PERCENT) {
      unit = Unit.PERCENT;
      num.setEnd(peek.end());
      next();
    } else if (peek.type() == ID) {
      switch (peek.value()) {
        case "px":
          unit = Unit.PX;
          next();
          break;
        case "ch":
          unit = Unit.CH;
          next();
          break;
        case "vh":
          unit = Unit.VH;
          next();
          break;
        case "vw":
          unit = Unit.VW;
          next();
          break;
        case "cm":
          unit = Unit.CM;
          next();
          break;
        case "m":
          unit = Unit.M;
          next();
          break;
        case "deg":
          unit = Unit.DEG;
          next();
          break;
        case "rad":
          unit = Unit.RAD;
          next();
          break;
        case "grad":
          unit = Unit.GRAD;
          next();
          break;
        case "turn":
          unit = Unit.TURN;
          next();
          break;

        default:
          errors.error(peek.location(), "Unknown/unsupported measurement %s", peek.value());
          return num;
      }

      num.setEnd(peek.end());
    } else {
      return num;
    }

    num.setUnit(unit);
    return num;
  }

  VariableExpr variableExpr() {
    Token prefix = expect(DOLLAR_SIGN);
    Identifier id = id();

    VariableExpr expr = new VariableExpr();
    expr.setStart(prefix.location());
    expr.setVariableName(id);
    expr.setEnd(id.getEnd());

    return expr;
  }

  StringLiteral stringLiteral() {
    Token t = peek();
    String val;

    if (t.type() != STRING) {
      val = "";
      expectedToken(t.location(), STRING, t.type());
    } else {
      next();
      val = t.value();
      assert val != null;
    }

    StringLiteral literal = new StringLiteral();
    literal.setStart(t.location());
    literal.setEnd(t.end());
    literal.setValue(val);

    return literal;
  }

  Expression fromId(Token peekedId) {
    String value = Objects.requireNonNull(peekedId.value());
    Keyword keyword;

    switch (value.toLowerCase()) {
      case "inherit":
        keyword = Keyword.INHERIT;
        break;
      case "initial":
        keyword = Keyword.INITIAL;
        break;
      case "auto":
        keyword = Keyword.AUTO;
        break;
      case "unset":
        keyword = Keyword.UNSET;
        break;
      case "flex-start":
        keyword = Keyword.FLEX_START;
        break;
      case "flex-end":
        keyword = Keyword.FLEX_END;
        break;
      case "center":
        keyword = Keyword.CENTER;
        break;
      case "stretch":
        keyword = Keyword.STRETCH;
        break;
      case "baseline":
        keyword = Keyword.BASELINE;
        break;
      case "none":
        keyword = Keyword.NONE;
        break;
      case "inline":
        keyword = Keyword.INLINE;
        break;
      case "block":
        keyword = Keyword.BLOCK;
        break;
      case "inline-block":
        keyword = Keyword.INLINE_BLOCK;
        break;
      case "flex":
        keyword = Keyword.FLEX;
        break;
      case "row":
        keyword = Keyword.ROW;
        break;
      case "row-reverse":
        keyword = Keyword.ROW_REVERSE;
        break;
      case "column":
        keyword = Keyword.COLUMN;
        break;
      case "column-reverse":
        keyword = Keyword.COLUMN_REVERSE;
        break;
      case "nowrap":
        keyword = Keyword.NOWRAP;
        break;
      case "wrap":
        keyword = Keyword.WRAP;
        break;
      case "wrap-reverse":
        keyword = Keyword.WRAP_REVERSE;
        break;
      case "space-between":
        keyword = Keyword.SPACE_BETWEEN;
        break;
      case "space-around":
        keyword = Keyword.SPACE_AROUND;
        break;
      case "space-evenly":
        keyword = Keyword.SPACE_EVENLY;
        break;
      case "content-box":
        keyword = Keyword.CONTENT_BOX;
        break;
      case "border-box":
        keyword = Keyword.BORDER_BOX;
        break;
      case "visible":
        keyword = Keyword.VISIBLE;
        break;
      case "hidden":
        keyword = Keyword.HIDDEN;
        break;
      case "collapse":
        keyword = Keyword.COLLAPSE;
        break;
      case "sub":
        keyword = Keyword.SUB;
        break;
      case "super":
        keyword = Keyword.SUPER;
        break;
      case "top":
        keyword = Keyword.TOP;
        break;
      case "middle":
        keyword = Keyword.MIDDLE;
        break;
      case "bottom":
        keyword = Keyword.BOTTOM;
        break;
      case "true":
        keyword = Keyword.TRUE;
        break;
      case "false":
        keyword = Keyword.FALSE;
        break;

      default:
        Identifier idExpr = id();
        if (peek().type() == BRACKET_OPEN) {
          return callExpr(idExpr);
        }
        return idExpr;
    }

    KeywordLiteral literal = new KeywordLiteral();
    literal.setKeyword(keyword);
    literal.setStart(peekedId.location());
    literal.setEnd(peekedId.end());

    next();

    return literal;
  }

  Identifier id() {
    Token id = peek();
    String value;

    if (id.type() == ID) {
      value = id.value();
      next();
    } else {
      value = "";
      expectedToken(id.location(), ID, id.type());
    }

    Identifier idExpr = new Identifier();
    idExpr.setStart(id.location());
    idExpr.setEnd(id.end());
    idExpr.setValue(value);

    return idExpr;
  }

  CallExpr callExpr(Identifier funcName) {
    CallExpr expr = new CallExpr();

    expr.setFunctionName(funcName);
    expr.setStart(funcName.getStart());

    expect(BRACKET_OPEN);

    while (matches(COMMA)) {
      next();
    }

    pushScope(ParserScope.CALL_EXPR);

    while (!matches(BRACKET_CLOSE) && hasNext()) {
      Expression argExpr = expr();

      if (argExpr != null) {
        expr.getArguments().add(argExpr);
      }

      while (matches(COMMA)) {
        next();
      }
    }

    Location l = expect(BRACKET_CLOSE).end();
    expr.setEnd(l);

    popScope();

    return expr;
  }

  enum ParserScope {
    TOP_LEVEL,
    RULE,
    INLINE,
    FUNCTION,
    LOOP,
    CALL_EXPR,
    SELECTOR,
    ;
  }
}

package net.arcadiusmc.chimera;

import static net.arcadiusmc.chimera.Token.ANGLE_RIGHT;
import static net.arcadiusmc.chimera.Token.BRACKET_CLOSE;
import static net.arcadiusmc.chimera.Token.BRACKET_OPEN;
import static net.arcadiusmc.chimera.Token.CARET_EQ;
import static net.arcadiusmc.chimera.Token.COLON;
import static net.arcadiusmc.chimera.Token.COMMA;
import static net.arcadiusmc.chimera.Token.DOLLAR_EQ;
import static net.arcadiusmc.chimera.Token.DOLLAR_SIGN;
import static net.arcadiusmc.chimera.Token.DOT;
import static net.arcadiusmc.chimera.Token.EQUALS;
import static net.arcadiusmc.chimera.Token.EXCLAMATION;
import static net.arcadiusmc.chimera.Token.HASHTAG;
import static net.arcadiusmc.chimera.Token.HEX;
import static net.arcadiusmc.chimera.Token.HEX_ALPHA;
import static net.arcadiusmc.chimera.Token.HEX_SHORT;
import static net.arcadiusmc.chimera.Token.ID;
import static net.arcadiusmc.chimera.Token.INT;
import static net.arcadiusmc.chimera.Token.MINUS;
import static net.arcadiusmc.chimera.Token.NUMBER;
import static net.arcadiusmc.chimera.Token.PERCENT;
import static net.arcadiusmc.chimera.Token.PLUS;
import static net.arcadiusmc.chimera.Token.SEMICOLON;
import static net.arcadiusmc.chimera.Token.SQUARE_CLOSE;
import static net.arcadiusmc.chimera.Token.SQUARE_OPEN;
import static net.arcadiusmc.chimera.Token.SQUIGLY;
import static net.arcadiusmc.chimera.Token.SQUIG_CLOSE;
import static net.arcadiusmc.chimera.Token.SQUIG_EQ;
import static net.arcadiusmc.chimera.Token.SQUIG_OPEN;
import static net.arcadiusmc.chimera.Token.STAR;
import static net.arcadiusmc.chimera.Token.STAR_EQ;
import static net.arcadiusmc.chimera.Token.STRING;
import static net.arcadiusmc.chimera.Token.WALL_EQ;
import static net.arcadiusmc.chimera.Token.WHITESPACE;

import java.util.Objects;
import lombok.Getter;
import net.arcadiusmc.chimera.TokenStream.ParseMode;
import net.arcadiusmc.chimera.ast.CallExpr;
import net.arcadiusmc.chimera.ast.ColorLiteral;
import net.arcadiusmc.chimera.ast.ErroneousExpr;
import net.arcadiusmc.chimera.ast.Expression;
import net.arcadiusmc.chimera.ast.Identifier;
import net.arcadiusmc.chimera.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.ast.Keyword;
import net.arcadiusmc.chimera.ast.KeywordLiteral;
import net.arcadiusmc.chimera.ast.NumberLiteral;
import net.arcadiusmc.chimera.ast.NumberUnitLiteral;
import net.arcadiusmc.chimera.ast.PropertyStatement;
import net.arcadiusmc.chimera.ast.RuleStatement;
import net.arcadiusmc.chimera.ast.SelectorExpression;
import net.arcadiusmc.chimera.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.EvenOdd;
import net.arcadiusmc.chimera.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.TagNameExpr;
import net.arcadiusmc.chimera.ast.SelectorGroupStatement;
import net.arcadiusmc.chimera.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.ast.SelectorStatement;
import net.arcadiusmc.chimera.ast.SheetStatement;
import net.arcadiusmc.chimera.ast.StringLiteral;
import net.arcadiusmc.chimera.ast.VariableDecl;
import net.arcadiusmc.chimera.ast.VariableExpr;
import net.arcadiusmc.chimera.selector.AttributeOperation;
import net.arcadiusmc.chimera.selector.Combinator;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive.Unit;

public class ChimeraParser {

  @Getter
  private final TokenStream stream;

  @Getter
  private final CompilerErrors errors;

  public ChimeraParser(String buffer) {
    this(new StringBuffer(buffer));
  }

  public ChimeraParser(StringBuffer buffer) {
    this.errors = new CompilerErrors(buffer);
    this.stream = new TokenStream(buffer, errors);
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

  public Token lastReadToken() {
    return stream.lastReadToken();
  }

  private void skipWhitespace() {
    while (matches(WHITESPACE)) {
      next();
    }
  }

  /* --------------------------- Selectors ---------------------------- */

  SelectorGroupStatement selectorGroup() {
    SelectorGroupStatement group = new SelectorGroupStatement();
    SelectorStatement selector = selector();

    group.setStart(selector.getStart());
    group.getSelectors().add(selector);

    while (matches(COMMA)) {
      next();
      skipWhitespace();
      selector = selector();
      group.getSelectors().add(selector);
    }

    return group;
  }

  SelectorStatement selector() {
    pushMode(ParseMode.SELECTOR);

    SelectorStatement stat = new SelectorStatement();

    SelectorNodeStatement nodeStat = new SelectorNodeStatement();
    nodeStat.setCombinator(Combinator.DESCENDANT);

    outer: while (true) {
      Token p = peek();

      if (stat.getStart() == null) {
        stat.setStart(p.location());
      }
      if (nodeStat.getStart() == null) {
        nodeStat.setStart(p.location());
      }

      SelectorExpression expr;

      switch (p.type()) {
        case STAR -> {
          next();
          expr = new MatchAllExpr();
        }
        case DOT -> {
          next();
          skipUnexpectedWhitespace();

          ClassNameExpr className = new ClassNameExpr();
          className.setClassName(id());
          expr = className;
        }
        case HASHTAG -> {
          next();
          skipUnexpectedWhitespace();

          IdExpr id = new IdExpr();
          id.setId(id());
          expr = id;
        }
        case ID -> {
          TagNameExpr tagName = new TagNameExpr();
          tagName.setTagName(id());
          expr = tagName;
        }
        case SQUARE_OPEN -> {
          expr = attributeExpr();
        }
        case COLON -> {
          expr = pseudoClass();
        }

        default -> {
          break outer;
        }
      }

      expr.setStart(p.location());
      nodeStat.getExpressions().add(expr);

      if (matches(WHITESPACE, PLUS, SQUIGLY, ANGLE_RIGHT)) {
        skipWhitespace();

        Combinator combinator = combinator();
        nodeStat.setCombinator(combinator);
        stat.getNodes().add(nodeStat);

        nodeStat = new SelectorNodeStatement();

        skipWhitespace();
      }
    }

    if (!nodeStat.getExpressions().isEmpty()){
      nodeStat.setCombinator(Combinator.DESCENDANT);
      stat.getNodes().add(nodeStat);
    }

    popMode();
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

  //
  // Only call from selector():
  //  1. This expects the first token to be '['
  //  2. This expects the caller to set the returned node's start location
  //
  AttributeExpr attributeExpr() {
    next();

    skipUnexpectedWhitespace();

    AttributeExpr expr = new AttributeExpr();
    expr.setAttributeName(id());

    skipUnexpectedWhitespace();

    if (matches(SQUARE_CLOSE)) {
      next();
      expr.setOperation(AttributeOperation.HAS);
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

    expect(SQUARE_CLOSE);

    return expr;
  }

  //
  // Like the above method, expects to be called from within the selector() function
  //
  SelectorExpression pseudoClass() {
    next();
    skipUnexpectedWhitespace();
    Identifier className = id();

    if (!matches(BRACKET_OPEN)) {
      PseudoClassExpr classExpr = new PseudoClassExpr();
      classExpr.setPseudoClass(className);
      return classExpr;
    }

    next();
    skipWhitespace();

    PseudoFunctionExpr expr = new PseudoFunctionExpr();
    expr.setFunctionName(className);

    String classNameString = className.getValue();
    if (classNameString.equalsIgnoreCase("is") || classNameString.equalsIgnoreCase("not")) {
      SelectorGroupStatement group = selectorGroup();
      expr.setSelectorGroup(group);
    } else {
      anbExpr(expr);
    }

    skipWhitespace();
    expect(BRACKET_CLOSE);

    return expr;
  }

  void anbExpr(PseudoFunctionExpr expr) {
    if (matches(ID)) {
      Token p = peek();

      switch (p.value().toLowerCase()) {
        case "even" -> {
          EvenOddKeyword e = new EvenOddKeyword();
          e.setEvenOdd(EvenOdd.EVEN);
          e.setStart(p.location());
          next();

          expr.setIndex(e);
          return;
        }
        case "odd" -> {
          EvenOddKeyword e = new EvenOddKeyword();
          e.setEvenOdd(EvenOdd.ODD);
          e.setStart(p.location());
          next();

          expr.setIndex(e);
          return;
        }
        case "n" -> {
          anbIndexSelector(expr);
          return;
        }

        default -> {
          error(p.location(), "Unexpected token");
          return;
        }
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

      SelectorGroupStatement group = selectorGroup();
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
    anbExpr.setA(a);
    anbExpr.setB(b);

    expr.setIndex(anbExpr);
  }

  /* --------------------------- Style sheets ---------------------------- */

  public SheetStatement stylesheet() {
    SheetStatement stat = new SheetStatement();
    stat.setStart(Location.START);

    while (hasNext()) {
      if (matches(DOLLAR_SIGN)) {
        VariableDecl decl = variableDecl();
        stat.getVariableDeclarations().add(decl);
        continue;
      }

      RuleStatement rule = rule();
      stat.getRules().add(rule);
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

    expect(SEMICOLON);

    return decl;
  }

  RuleStatement rule() {
    RuleStatement stat = new RuleStatement();
    SelectorGroupStatement group = selectorGroup();

    stat.setStart(group.getStart());
    stat.setSelector(group);

    expect(SQUIG_OPEN);

    while (!matches(SQUIG_CLOSE)) {
      PropertyStatement prop = propertyStatement(true);
      stat.getProperties().add(prop);

      if (matches(SEMICOLON)) {
        next();
      } else if (!matches(SQUIG_CLOSE)) {
        expectedToken(peek().location(), SEMICOLON, peek().type());
      }
    }

    expect(SQUIG_CLOSE);
    return stat;
  }

  /* --------------------------- Inline style ---------------------------- */

  public InlineStyleStatement inlineStyle() {
    InlineStyleStatement stat = new InlineStyleStatement();
    stat.setStart(peek().location());

    while (hasNext()) {
      PropertyStatement property = propertyStatement(false);
      stat.getProperties().add(property);

      if (hasNext()) {
        expect(SEMICOLON);
      }
    }

    return stat;
  }

  /* --------------------------- Expressions ---------------------------- */

  PropertyStatement propertyStatement(boolean importantAllowed) {
    Identifier propertyName = id();
    expect(COLON);

    Expression value = expr();

    PropertyStatement stat = new PropertyStatement();
    stat.setStart(propertyName.getStart());
    stat.setValue(value);
    stat.setPropertyName(propertyName);

    if (importantAllowed && peek().type() == EXCLAMATION) {
      Token exclaim = next();
      Token idToken = expect(ID);

      if (idToken != null && idToken.value().equalsIgnoreCase("important")) {
        next();

        stat.setImportant(true);
        stat.setImportantStart(exclaim.location());
      }
    }

    return stat;
  }

  Expression expr() {
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
        return numericLiteral();

      case HEX:
      case HEX_ALPHA:
      case HEX_SHORT:
        return hexExpr();

      default:
        ErroneousExpr expr = new ErroneousExpr();
        expr.setToken(next());
        return expr;
    }
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
        hexLiteral = t.value();
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
    num.setValue(number);

    return num;
  }

  Expression numericLiteral() {
    Token numberToken = peek();
    NumberLiteral literal = numberLiteral();
    Token peek = peek();

    if (peek.location().cursor() != (numberToken.length() + numberToken.location().cursor())) {
      return literal;
    }

    Unit unit;
    if (peek.type() == PERCENT) {
      unit = Unit.PERCENT;
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

        default:
          return literal;
      }
    } else {
      return literal;
    }

    NumberUnitLiteral unitLiteral = new NumberUnitLiteral();
    unitLiteral.setStart(literal.getStart());
    unitLiteral.setNumber(literal);
    unitLiteral.setUnit(unit);

    return unitLiteral;
  }

  VariableExpr variableExpr() {
    Token prefix = expect(DOLLAR_SIGN);
    Identifier id = id();

    VariableExpr expr = new VariableExpr();
    expr.setStart(prefix.location());
    expr.setVariableName(id);
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

    while (!matches(BRACKET_CLOSE)) {
      Expression argExpr = expr();

      if (argExpr != null) {
        expr.getArguments().add(argExpr);
      }

      while (matches(COMMA)) {
        next();
      }
    }

    expect(BRACKET_CLOSE);
    return expr;
  }
}

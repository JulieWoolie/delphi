package com.juliewoolie.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.base.Strings;
import java.util.function.Consumer;
import com.juliewoolie.chimera.parse.ChimeraParser.ParserScope;
import com.juliewoolie.chimera.parse.ast.BinaryExpr;
import com.juliewoolie.chimera.parse.ast.BinaryOp;
import com.juliewoolie.chimera.parse.ast.CallExpr;
import com.juliewoolie.chimera.parse.ast.ColorLiteral;
import com.juliewoolie.chimera.parse.ast.ControlFlowStatement;
import com.juliewoolie.chimera.parse.ast.ErroneousExpr;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.chimera.parse.ast.IfStatement;
import com.juliewoolie.chimera.parse.ast.ImportStatement;
import com.juliewoolie.chimera.parse.ast.Keyword;
import com.juliewoolie.chimera.parse.ast.KeywordLiteral;
import com.juliewoolie.chimera.parse.ast.ListLiteral;
import com.juliewoolie.chimera.parse.ast.NamespaceExpr;
import com.juliewoolie.chimera.parse.ast.NumberLiteral;
import com.juliewoolie.chimera.parse.ast.RegularSelectorStatement;
import com.juliewoolie.chimera.parse.ast.SelectorExpression;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.AttributeExpr;
import com.juliewoolie.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import com.juliewoolie.chimera.parse.ast.SelectorNodeStatement;
import com.juliewoolie.chimera.parse.ast.SheetStatement;
import com.juliewoolie.chimera.parse.ast.Statement;
import com.juliewoolie.chimera.parse.ast.UnaryExpr;
import com.juliewoolie.chimera.parse.ast.UnaryOp;
import com.juliewoolie.chimera.parse.ast.VariableDecl;
import com.juliewoolie.chimera.parse.ast.VariableExpr;
import com.juliewoolie.chimera.selector.AttributeOperation;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import org.junit.jupiter.api.Test;

class ChimeraParserTest {

  static final String DEFAULT_STYLE_TEST = """
      /* Variables */
            
      $button-color: white;
      $button-outline: black;
      $button-bg: darken(gray, 20%);
      $button-bg-hover: lighten($button-bg, 20%);
      $button-bg-active: darken($button-bg, 25%);
      $button-padding: 1px;
      $body-bg: darken(white, 45%);
            
      /* Background */
            
      :root {
        background-color: $body-bg;
        min-width: 100vw;
        min-height: 100vh;
        max-width: 100vw;
        max-height: 100vh;
            
        outline: 1px;
        padding: 1.5px;
        outline-color: darken($body-bg);
      }
            
      /* Text elements */
            
      h1 {
        scale: 2;
        display: block;
      }
            
      h2 {
        scale: 1.75;
        display: block;
      }
            
      h3 {
        scale: 1.5;
        display: block;
      }
            
      h3 {
        scale: 1.25;
        display: block;
      }
            
      b, bold {
        bold: true;
        display: inline;
      }
            
      i, italic, em {
        italic: true;
        display: inline;
      }
            
      u, underlined {
        underlined: true;
        display: inline;
      }
      st, strikethrough {
        display: inline;
        strikethrough: true;
      }
      obf, obfuscated {
        display: inline;
        obfuscated: true;
      }
            
      /* HTML tags */
            
      div {
        display: block;
      }
      span {
        display: inline;
      }
      p {
        display: block;
      }
            
      /* New line tag */
            
      br, nl, newline {
        display: inline-block;
      }
            
      /* Item */
            
      item {
        scale: 0.5;
        display: inline;
      }
            
      /* Item tooltip stuff */
            
      item-tooltip-name {
        margin-bottom: 1px;
        display: block;
      }
            
      item-tooltip {
        scale: 0.5;
        z-index: 10;
        display: block;
            
        padding: 1px;
        background-color: #170817;
        border: 1px;
        border-color: #270558;
        outline: 1px;
        outline-color: #170817;
      }
            
      /* Regular Tooltips */
            
      tooltip {
        scale: 0.5;
        z-index: 10;
        display: block;
            
        padding: 1px;
        background-color: #170817;
        border: 1px;
        border-color: #270558;
        outline: 1px;
        outline-color: #170817;
      }
            
      /* Buttons */
            
      button {
        display: inline;
        color: $button-color;
        background-color: $button-bg;
        border-color: $button-outline;
        padding: $button-padding;
        border: $button-padding;
      }
            
      button:hover {
        background-color: $button-bg-hover;
      }
            
      button:active {
        background-color: $button-bg-active;
      }
            
      /*
       * Color tags
       *
       * These don't use CSS colors because these colors are meant to be
       * minimessage tags, as such, they use minecraft colors. Both tags
       * with underscores and dashes are supported. Underscores are for
       * minimessage, dashes are prettier.
       *
       * :3
       */
            
      black {
        display: inline;
        color: black;
      }
      dark_blue, dark-blue {
        display: inline;
        color: #0000AA;
      }
      dark_green, dark-green {
        display: inline;
        color: #00AA00;
      }
      dark_aqua, dark-aqua {
        display: inline;
        color: #00AAAA;
      }
      dark_red, dark-red {
        display: inline;
        color: #AA0000;
      }
      dark_purple, dark-purple {
        display: inline;
        color: #AA00AA;
      }
      gold {
        display: inline;
        color: #FFAA00;
      }
      gray {
        display: inline;
        color: #AAAAAA;
      }
      dark_gray, dark_grey, dark-gray, dark-grey {
        display: inline;
        color: #555555;
      }
      blue {
        display: inline;
        color: #5555FF;
      }
      green {
        display: inline;
        color: #55FF55;
      }
      aqua {
        display: inline;
        color: #55FFFF;
      }
      red {
        display: inline;
        color: #FF5555;
      }
      light_purple, light-purple, purple {
        display: inline;
        color: #FF55FF;
      }
      yellow {
        display: inline;
        color: #FFFF55;
      }
      white {
        display: inline;
        color: white;
      }
      """;

  @Test
  void testClassSelectors() {
    RegularSelectorStatement selector = selector(".class-name");

    assertEquals(1, selector.getNodes().size());
    SelectorNodeStatement node = selector.getNodes().getFirst();

    assertEquals(1, node.getExpressions().size());

    ClassNameExpr expr = assertInstanceOf(ClassNameExpr.class, node.getExpressions().getFirst());
    assertEquals(expr.getClassName().getValue(), "class-name");
  }

  @Test
  void testAttributeOperators() {
    testAttributeOp("[attr=\"value\"]", AttributeOperation.EQUALS, "value");
    testAttributeOp("[attr~=\"value\"]", AttributeOperation.CONTAINS_WORD, "value");
    testAttributeOp("[attr|=\"value\"]", AttributeOperation.DASH_PREFIXED, "value");
    testAttributeOp("[attr^=\"value\"]", AttributeOperation.STARTS_WITH, "value");
    testAttributeOp("[attr*=\"value\"]", AttributeOperation.CONTAINS_SUBSTRING, "value");
    testAttributeOp("[attr$=\"value\"]", AttributeOperation.ENDS_WITH, "value");
    testAttributeOp("[attr]", AttributeOperation.HAS, null);
  }

  static void testAttributeOp(String str, AttributeOperation op, String value) {
    RegularSelectorStatement stat = selector(str);
    assertEquals(1, stat.getNodes().size());

    SelectorNodeStatement node = stat.getNodes().getFirst();
    SelectorExpression first = node.getExpressions().getFirst();

    AttributeExpr attributeExpr = assertInstanceOf(AttributeExpr.class, first);
    assertEquals(op, attributeExpr.getOperation());

    if (value != null) {
      assertNotNull(attributeExpr.getValue());
      assertEquals(value, attributeExpr.getValue().getValue());
    }
  }

  @Test
  void testNumericValues() {
    NumberLiteral num = parseExpr("14", NumberLiteral.class);
    assertEquals(14, num.getValue());

    num = parseExpr("14px", NumberLiteral.class);
    assertEquals(14, num.getValue());

    NumberLiteral unit = parseExpr("14px", NumberLiteral.class);
    assertEquals(Primitive.Unit.PX, unit.getUnit());
    assertEquals(14, unit.getValue());

    unit = parseExpr("14%", NumberLiteral.class);
    assertEquals(Unit.PERCENT, unit.getUnit());
    assertEquals(14, unit.getValue());

    for (Unit value : Unit.values()) {
      unit = parseExpr("14" + value.getUnit(), NumberLiteral.class);
      assertEquals(value, unit.getUnit());
    }
  }

  @Test
  void testKeywords() {
    assertKeyword("inherit", Keyword.INHERIT);
    assertKeyword("initial", Keyword.INITIAL);
    assertKeyword("auto", Keyword.AUTO);
    assertKeyword("unset", Keyword.UNSET);
    assertKeyword("flex-start", Keyword.FLEX_START);
    assertKeyword("flex-end", Keyword.FLEX_END);
    assertKeyword("center", Keyword.CENTER);
    assertKeyword("stretch", Keyword.STRETCH);
    assertKeyword("baseline", Keyword.BASELINE);
    assertKeyword("none", Keyword.NONE);
    assertKeyword("inline", Keyword.INLINE);
    assertKeyword("block", Keyword.BLOCK);
    assertKeyword("inline-block", Keyword.INLINE_BLOCK);
    assertKeyword("flex", Keyword.FLEX);
    assertKeyword("row", Keyword.ROW);
    assertKeyword("row-reverse", Keyword.ROW_REVERSE);
    assertKeyword("column", Keyword.COLUMN);
    assertKeyword("column-reverse", Keyword.COLUMN_REVERSE);
    assertKeyword("nowrap", Keyword.NOWRAP);
    assertKeyword("wrap", Keyword.WRAP);
    assertKeyword("wrap-reverse", Keyword.WRAP_REVERSE);
    assertKeyword("space-between", Keyword.SPACE_BETWEEN);
    assertKeyword("space-around", Keyword.SPACE_AROUND);
    assertKeyword("space-evenly", Keyword.SPACE_EVENLY);
  }

  @Test
  void testHex() {
    ColorLiteral lit = parseExpr("#fab", ColorLiteral.class);
    assertEquals(0xffffaabb, lit.getColor().argb());

    lit = parseExpr("#ffaabb", ColorLiteral.class);
    assertEquals(0xffffaabb, lit.getColor().argb());

    lit = parseExpr("#c0aaffcc", ColorLiteral.class);
    System.out.println(lit.getColor().hexString());
    assertEquals(0xccc0aaff, lit.getColor().argb());
  }

  @Test
  void testVariableRef() {
    VariableExpr expr = parseExpr("$var", VariableExpr.class);
    assertEquals("var", expr.getVariableName().getValue());
  }

  @Test
  void testVariableDef() {
    ChimeraParser parser = Tests.parser("$variable: 1;");
    VariableDecl decl = parser.variableDecl();

    assertEquals(decl.getName().getValue(), "variable");
    assertEquals(1, assertInstanceOf(NumberLiteral.class, decl.getValue()).getValue());

    assertInvalidSyntax(
        "$variable",
        ChimeraParser::variableDecl,
        "Expected ':', but found end-of-input"
    );

    assertInvalidSyntax(
        "$variable:",
        ChimeraParser::variableDecl,
        "Expected ';' to end statement, found end-of-input"
    );

    assertInvalidSyntax(
        "$variable: 14",
        ChimeraParser::variableDecl,
        "Expected ';' to end statement, found end-of-input"
    );
  }

  @Test
  void testImplicitList() {
    ChimeraParser parser = Tests.parser("14px 10ch 6vw 2vh");
    Expression expr = parser.expr();

    ListLiteral rect = assertInstanceOf(ListLiteral.class, expr);

    assertEquals(4, rect.getValues().size());
    assertNumberValue(14, Unit.PX, rect.getValues().get(0));
    assertNumberValue(10, Unit.CH, rect.getValues().get(1));
    assertNumberValue(6, Unit.VW, rect.getValues().get(2));
    assertNumberValue(2, Unit.VH, rect.getValues().get(3));

    rect = parseExpr("14px 10px", ListLiteral.class);
    assertEquals(2, rect.getValues().size());
    assertNumberValue(14, Unit.PX, rect.getValues().get(0));
    assertNumberValue(10, Unit.PX, rect.getValues().get(1));

    rect = parseExpr("14px 10px 6px", ListLiteral.class);
    assertEquals(3, rect.getValues().size());
    assertNumberValue(14, Unit.PX, rect.getValues().get(0));
    assertNumberValue(10, Unit.PX, rect.getValues().get(1));
    assertNumberValue( 6, Unit.PX, rect.getValues().get(2));

    rect = parseExpr("14px 10px; 5px", ListLiteral.class);
    assertEquals(2, rect.getValues().size());
    assertNumberValue(14, Unit.PX, rect.getValues().get(0));
    assertNumberValue(10, Unit.PX, rect.getValues().get(1));
  }

  @Test
  void testExplicitList() {
    ListLiteral literal = parseExpr("[1px, 2px 4px]", ListLiteral.class);
    assertEquals(3, literal.getValues().size());

    assertNumberValue(1, Unit.PX, literal.getValues().get(0));
    assertNumberValue(2, Unit.PX, literal.getValues().get(1));
    assertNumberValue(4, Unit.PX, literal.getValues().get(2));
  }

  @Test
  void testNamespaced() {
    NamespaceExpr expr = parseExpr("color.red(#fff)", NamespaceExpr.class);
    assertEquals("color", expr.getNamespace().getValue());

    CallExpr call = assertInstanceOf(CallExpr.class, expr.getTarget());
    assertEquals("red", call.getFunctionName().getValue());
    assertEquals(1, call.getArguments().size());
  }

  @Test
  void testUnary() {
    UnaryExpr unaryExpr = parseExpr("not true", UnaryExpr.class);
    assertEquals(UnaryOp.INVERT, unaryExpr.getOp());

    KeywordLiteral literal = assertInstanceOf(KeywordLiteral.class, unaryExpr.getValue());
    assertEquals(Keyword.TRUE, literal.getKeyword());

    unaryExpr = parseExpr("+id", UnaryExpr.class);
    assertEquals(UnaryOp.PLUS, unaryExpr.getOp());

    unaryExpr = parseExpr("-$variable", UnaryExpr.class);
    assertEquals(UnaryOp.MINUS, unaryExpr.getOp());
  }

  @Test
  void testParenExpr() {
    Expression expr = parseExpr("-($variable)", Expression.class);
    System.out.println(expr);
  }

  @Test
  void testBinary() {
    BinaryExpr expr = parseExpr("4px + 2px", BinaryExpr.class);
    assertEquals(BinaryOp.PLUS, expr.getOp());

    NumberLiteral lhs = assertInstanceOf(NumberLiteral.class, expr.getLhs());
    NumberLiteral rhs = assertInstanceOf(NumberLiteral.class, expr.getRhs());

    assertEquals(4, lhs.getValue());
    assertEquals(2, rhs.getValue());

    expr = parseExpr("4px + -$var", BinaryExpr.class);
    assertEquals(BinaryOp.PLUS, expr.getOp());

    lhs = assertInstanceOf(NumberLiteral.class, expr.getLhs());
    assertEquals(4, lhs.getValue());
    assertEquals(Unit.PX, lhs.getUnit());

    UnaryExpr unary = assertInstanceOf(UnaryExpr.class, expr.getRhs());
    assertEquals(UnaryOp.MINUS, unary.getOp());

    VariableExpr varExpr = assertInstanceOf(VariableExpr.class, unary.getValue());
    assertEquals("var", varExpr.getVariableName().getValue());

    expr = parseExpr("true and false", BinaryExpr.class);
    assertEquals(BinaryOp.AND, expr.getOp());

    expr = parseExpr("true or false", BinaryExpr.class);
    assertEquals(BinaryOp.OR, expr.getOp());
  }

  @Test
  void testIf() {
    IfStatement stat = parseStat(ParserScope.TOP_LEVEL, "@if true {}", IfStatement.class);
    assertInstanceOf(KeywordLiteral.class, stat.getCondition());
    assertNotNull(stat.getBody());
  }

  @Test
  void testReturn() {
    ControlFlowStatement stat = parseStat(
        ParserScope.FUNCTION,
        "@return;",
        ControlFlowStatement.class
    );

    assertNull(stat.getReturnValue());

    stat = parseStat(ParserScope.FUNCTION, "@return 14px;", ControlFlowStatement.class);
    NumberLiteral ret = assertInstanceOf(NumberLiteral.class, stat.getReturnValue());

    assertEquals(14, ret.getValue());
    assertEquals(Unit.PX, ret.getUnit());

    assertInvalidSyntax(
        "@return;",
        ChimeraParser::controlFlowStatement,
        "@return not allowed here"
    );

    assertInvalidSyntax(
        "@return",
        p -> {
          p.pushScope(ParserScope.FUNCTION);
          p.controlFlowStatement();
        },
        "Expected ';' to end statement, found end-of-input"
    );

    assertInvalidSyntax(
        "@return\n1px;",
        p -> {
          p.pushScope(ParserScope.FUNCTION);
          p.controlFlowStatement();
        },
        "Expected ';' to end statement, found integer(1)"
    );

    assertDoesNotThrow(() -> parseStat(ParserScope.LOOP, "@break;", ControlFlowStatement.class));
    assertDoesNotThrow(() -> parseStat(ParserScope.LOOP, "@continue;", ControlFlowStatement.class));
  }

  @Test
  void testImport() {
    ImportStatement stat = parseStat(
        ParserScope.TOP_LEVEL,
        "@import \"scss:color\";",
        ImportStatement.class
    );

    assertEquals("scss:color", stat.getImportPath().getValue());
  }

  @Test
  void testNesting() {
    var parser = Tests.parser(
        """
        .class1 {
          color: red;
          background-color: green;
          
          .class2 {
            color: green;
          }
          
          &img {
            color: blue;
          }
          
          tagName {
            color: cyan;
          }
        }
        """
    );

    SheetStatement statement = parser.stylesheet();
    System.out.println(statement);
  }

  void assertNumberValue(Number num, Unit unit, Expression expr) {
    if (expr instanceof ErroneousExpr e) {
      fail("Erroneous expression result: token=" + e.getToken().info());
    }

    NumberLiteral lit = assertInstanceOf(NumberLiteral.class, expr);
    assertEquals(num.floatValue(), lit.getValue().floatValue());

    if (unit != null) {
      assertEquals(unit, lit.getUnit());
    }
  }

  void assertInvalidSyntax(String str, Consumer<ChimeraParser> consumer, String error) {
    ChimeraParser parser = Tests.parser(str);
    ChimeraException exc = assertThrows(ChimeraException.class, () -> consumer.accept(parser));

    if (Strings.isNullOrEmpty(error)) {
      fail(exc);
    }

    assertEquals(error, exc.getError().getMessage());
  }

  void assertKeyword(String string, Keyword keyword) {
    ChimeraParser parser = Tests.parser(string);
    Expression expr = parser.expr();

    KeywordLiteral literal = assertInstanceOf(KeywordLiteral.class, expr);
    assertEquals(keyword, literal.getKeyword());
  }

  static <T> T parseStat(ParserScope scope, String str, Class<T> t) {
    ChimeraParser parser = Tests.parser(str);
    parser.pushScope(scope);
    Statement expr = parser.statement();
    parser.popScope();

    return assertInstanceOf(t, expr);
  }

  static <T> T parseExpr(String str, Class<T> t) {
    ChimeraParser parser = Tests.parser(str);
    Expression expr = parser.expr();

    if (expr instanceof ErroneousExpr err) {
      fail("Erroneous expression: token=" + err.getToken().info());
    }

    return assertInstanceOf(t, expr);
  }

  static RegularSelectorStatement selector(String str) {
    return Tests.parser(str).regularSelector();
  }
}
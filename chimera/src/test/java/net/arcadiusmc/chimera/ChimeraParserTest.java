package net.arcadiusmc.chimera;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.base.Strings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.arcadiusmc.chimera.ast.ColorLiteral;
import net.arcadiusmc.chimera.ast.Expression;
import net.arcadiusmc.chimera.ast.KeywordLiteral;
import net.arcadiusmc.chimera.ast.KeywordLiteral.Keyword;
import net.arcadiusmc.chimera.ast.NumberLiteral;
import net.arcadiusmc.chimera.ast.NumberUnitLiteral;
import net.arcadiusmc.chimera.ast.SelectorExpression;
import net.arcadiusmc.chimera.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.ast.SelectorStatement;
import net.arcadiusmc.chimera.ast.SheetStatement;
import net.arcadiusmc.chimera.ast.VariableDecl;
import net.arcadiusmc.chimera.ast.VariableExpr;
import net.arcadiusmc.chimera.selector.AttributeOperation;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
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
    SelectorStatement selector = selector(".class-name");

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
    SelectorStatement stat = selector(str);
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

    num = parseExpr("14 px", NumberLiteral.class);
    assertEquals(14, num.getValue());

    NumberUnitLiteral unit = parseExpr("14px", NumberUnitLiteral.class);
    assertEquals(Primitive.Unit.PX, unit.getUnit());
    assertEquals(14, unit.getNumber().getValue());

    unit = parseExpr("14%", NumberUnitLiteral.class);
    assertEquals(Unit.PERCENT, unit.getUnit());
    assertEquals(14, unit.getNumber().getValue());
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
    assertEquals(0xc0aaffcc, lit.getColor().argb());
  }

  @Test
  void testVariableRef() {
    VariableExpr expr = parseExpr("$var", VariableExpr.class);
    assertEquals("var", expr.getVariableName().getValue());
  }

  @Test
  void testVariableDef() {
    ChimeraParser parser = parser("$variable: 1;");
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
        "Expected ';', but found end-of-input"
    );

    assertInvalidSyntax(
        "$variable: 14",
        ChimeraParser::variableDecl,
        "Expected ';', but found end-of-input"
    );
  }

  @Test
  void testSelector() {
    SelectorStatement selector = parser(".class").selector();
    Chimera.print(selector);

    assertInvalidSyntax(". class", ChimeraParser::selector, "Unexpected whitespace");
  }

  @Test
  void printAst() {
    ChimeraParser parser = parser(DEFAULT_STYLE_TEST);
    SheetStatement stylesheet = parser.stylesheet();

    String str = stylesheet.toString();
    Path out = Path.of("ast.xml").toAbsolutePath();

    assertDoesNotThrow(() -> Files.writeString(out, str, StandardCharsets.UTF_8));
  }

  void assertInvalidSyntax(String str, Consumer<ChimeraParser> consumer, String error) {
    ChimeraParser parser = parser(str);
    ChimeraException exc = assertThrows(ChimeraException.class, () -> consumer.accept(parser));

    if (Strings.isNullOrEmpty(error)) {
      fail(exc);
    }

    assertEquals(error, exc.getError().getMessage());
  }

  void assertKeyword(String string, Keyword keyword) {
    ChimeraParser parser = parser(string);
    Expression expr = parser.expr();

    KeywordLiteral literal = assertInstanceOf(KeywordLiteral.class, expr);
    assertEquals(keyword, literal.getKeyword());
  }

  static <T> T parseExpr(String str, Class<T> t) {
    ChimeraParser parser = parser(str);
    Expression expr = parser.expr();
    return assertInstanceOf(t, expr);
  }

  static ChimeraParser parser(String str) {
    ChimeraParser parser = new ChimeraParser(str);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("test-src.scss");
    errors.setListener(error -> {
      throw new ChimeraException(error);
    });

    return parser;
  }

  static SelectorStatement selector(String str) {
    return parser(str).selector();
  }
}
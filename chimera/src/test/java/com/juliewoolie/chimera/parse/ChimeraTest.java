package com.juliewoolie.chimera.parse;

import static com.juliewoolie.chimera.parse.Tests.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.PrimitiveRect;
import com.juliewoolie.chimera.Properties;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.chimera.Value.ValueType;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.chimera.parse.ast.SheetStatement;
import com.juliewoolie.dom.style.Color;
import com.juliewoolie.dom.style.NamedColor;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import org.junit.jupiter.api.Test;

class ChimeraTest {

  static final String TEST_SHEET_1 = """
      $variable1: red;
      
      .rule1 {
        color: $variable1;
      }
      """;

  @Test
  void inlineTest() {
    ChimeraParser parser = parser("background-color: red; color: green;");

    ChimeraContext context = new ChimeraContext(parser.getStream().getInput());
    context.setErrors(parser.getErrors());

    InlineStyleStatement inline = parser.inlineStyle();
    PropertySet set = new PropertySet();

    Chimera.compileInline(inline, set, context);

    Value<Color> bgColor = set.get(Properties.BACKGROUND_COLOR);
    assertNotNull(bgColor);
    assertEquals(ValueType.EXPLICIT, bgColor.getType());
    assertEquals("red", bgColor.getTextValue());
    assertEquals(NamedColor.RED, bgColor.getValue());

    Value<Color> txtColor = set.get(Properties.COLOR);
    assertNotNull(txtColor);
    assertEquals(ValueType.EXPLICIT, txtColor.getType());
    assertEquals("green", txtColor.getTextValue());
    assertEquals(NamedColor.GREEN, txtColor.getValue());
  }

  @Test
  void testSheet() {
    ChimeraParser parser = parser(TEST_SHEET_1);

    ChimeraContext context = new ChimeraContext(parser.getStream().getInput());
    context.setErrors(parser.getErrors());

    SheetStatement sheet = parser.stylesheet();
    ChimeraStylesheet compiled = Chimera.compileSheet(sheet, context);

    assertEquals(1, compiled.getLength());
    Rule r1 = compiled.getRule(0);

    assertEquals(".rule1", r1.getSelector());

    PropertySet set = r1.getPropertySet();
    Value<Color> color = set.get(Properties.COLOR);

    assertNotNull(color);
    assertEquals(ValueType.EXPLICIT, color.getType());
    assertEquals("$variable1", color.getTextValue());
    assertEquals(NamedColor.RED, color.getValue());
  }

  @Test
  void testBinary() {
    Object o = evaluateStr("2 + 3");
    Primitive primitive = assertInstanceOf(Primitive.class, o);

    assertEquals(5, primitive.getValue());
    assertEquals(Unit.NONE, primitive.getUnit());

    assertEquals(6, evaluate("2 * 3", Primitive.class).getValue());
    assertEquals(3, evaluate("6 / 2", Primitive.class).getValue());
    assertEquals(1, evaluate("5 % 2", Primitive.class).getValue());

    assertFalse(evaluate("5 < 2", Boolean.class));
    assertTrue(evaluate("5 > 2", Boolean.class));
    assertTrue(evaluate("5 >= 5", Boolean.class));
    assertTrue(evaluate("5 <= 5", Boolean.class));
    assertFalse(evaluate("5 > 5", Boolean.class));
    assertFalse(evaluate("5 < 5", Boolean.class));

    assertEquals(strcmp("a", "b") < 0, evaluate("a < b", Boolean.class));
    assertEquals(strcmp("a", "b") > 0, evaluate("a > b", Boolean.class));

    assertEquals("ab", evaluate("a + b", String.class));
    assertEquals("a-b", evaluate("a - b", String.class));
  }

  @Test
  void testRectangle() {
    ChimeraStylesheet sheet = evaluateSheet(
        """
        .test1 {
          padding: 1px;
        }
        .test2 {
          padding: 1px 2px;
        }
        .test3 {
          padding: 1px 2px 3px;
        }
        .test4 {
          padding: 1px 2px 3px 4px;
        }
        """
    );

    assertEquals(4, sheet.getLength());
    PropertySet test1Set = sheet.getRule(0).getPropertySet();
    PropertySet test2Set = sheet.getRule(1).getPropertySet();
    PropertySet test3Set = sheet.getRule(2).getPropertySet();
    PropertySet test4Set = sheet.getRule(3).getPropertySet();

    PrimitiveRect rect1 = test1Set.getValue(Properties.PADDING);
    PrimitiveRect rect2 = test2Set.getValue(Properties.PADDING);
    PrimitiveRect rect3 = test3Set.getValue(Properties.PADDING);
    PrimitiveRect rect4 = test4Set.getValue(Properties.PADDING);

    assertNotNull(rect1);
    assertNotNull(rect2);
    assertNotNull(rect3);
    assertNotNull(rect4);

    Primitive px1 = Primitive.create(1, Unit.PX);
    Primitive px2 = Primitive.create(2, Unit.PX);
    Primitive px3 = Primitive.create(3, Unit.PX);
    Primitive px4 = Primitive.create(4, Unit.PX);

    // padding: 1px;
    assertEquals(px1, rect1.getTop());
    assertEquals(px1, rect1.getRight());
    assertEquals(px1, rect1.getBottom());
    assertEquals(px1, rect1.getLeft());

    // padding: 1px 2px;
    assertEquals(px1, rect2.getTop());
    assertEquals(px2, rect2.getRight());
    assertEquals(px1, rect2.getBottom());
    assertEquals(px2, rect2.getLeft());

    // padding: 1px 2px 3px;
    assertEquals(px1, rect3.getTop());
    assertEquals(px2, rect3.getRight());
    assertEquals(px3, rect3.getBottom());
    assertEquals(px2, rect3.getLeft());

    // padding: 1px 2px 3px 4px;
    assertEquals(px1, rect4.getTop());
    assertEquals(px2, rect4.getRight());
    assertEquals(px3, rect4.getBottom());
    assertEquals(px4, rect4.getLeft());
  }

  @Test
  void testIf() {
    var sheet = evaluateSheet("""
        $variable: true;
        
        @if $variable {
          .className {
            color: red;
            padding: 2px;
          }
        } @else {
          .otherClass {
            background-color: green;
            margin: 4px;
          }
        }
        """);

    assertEquals(1, sheet.getLength());
    var rule = sheet.getRule(0);

    assertEquals("red", rule.getProperties().getColor());
    assertEquals("2px", rule.getProperties().getPadding());
  }

  @Test
  void testNesting() {
    ChimeraStylesheet sheet = evaluateSheet(
        """
        .class1 {
          &:hover {
            div {}
          }
          :active {}
          
          &+:disabled {}
        }
        """
    );

    assertEquals(5, sheet.getLength());

    Rule r1 = sheet.getRule(0);
    Rule r2 = sheet.getRule(1);
    Rule r3 = sheet.getRule(2);
    Rule r4 = sheet.getRule(3);
    Rule r5 = sheet.getRule(4);

    assertEquals(".class1:hover div", r1.getSelector());
    assertEquals(".class1:hover", r2.getSelector());
    assertEquals(".class1 :active", r3.getSelector());
    assertEquals(".class1 + :disabled", r4.getSelector());
    assertEquals(".class1", r5.getSelector());
  }

  @Test
  void testListNesting() {
    ChimeraStylesheet sheet = evaluateSheet(
        """
        .class1, .class2 {
          &div {}
          
          .colored {}
        }
        """
    );
  }

  @Test
  void testPrintStatements() {
    ChimeraParser parser = new ChimeraParser(
        """
        @print 4px 23px;
        @debug HelloDebug;
        @error HelloError;
        @warn HelloWarn;
        """
    );

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("test-src.scss");
    errors.setListener(error -> {
      System.out.println(error.getFormattedError());
    });

    var sheet = parser.stylesheet();
    Chimera.compileSheet(sheet, parser.createContext());
  }

  int strcmp(String s1, String s2) {
    int cmp = s1.compareTo(s2);
    return Integer.compare(cmp, 0);
  }

  ChimeraStylesheet evaluateSheet(String str) {
    ChimeraParser parser = parser(str);
    SheetStatement sheet = parser.stylesheet();
    return Chimera.compileSheet(sheet, parser.createContext());
  }

  <T> T evaluate(String str, Class<T> type) {
    return assertInstanceOf(type, Chimera.coerceValue(type, evaluateStr(str)));
  }

  Object evaluateStr(String expr) {
    ChimeraParser parser = parser(expr);
    Expression expr1 = parser.expr();

    ChimeraContext ctx = parser.createContext();
    Scope scope = Scope.createTopLevel();

    Interpreter inter = new Interpreter(ctx, scope);
    return expr1.visit(inter);
  }
}
package net.arcadiusmc.chimera.parse;

import static net.arcadiusmc.chimera.parse.Tests.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.Value.ValueType;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
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

  int strcmp(String s1, String s2) {
    int cmp = s1.compareTo(s2);
    return Integer.compare(cmp, 0);
  }

  <T> T evaluate(String str, Class<T> type) {
    return assertInstanceOf(type, Chimera.coerceValue(type, evaluateStr(str)));
  }

  Object evaluateStr(String expr) {
    ChimeraParser parser = parser(expr);
    Expression expr1 = parser.expr();

    ChimeraContext ctx = parser.createContext();
    Scope scope = Scope.createTopLevel();

    return expr1.evaluate(ctx, scope);
  }
}
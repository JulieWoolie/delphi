package net.arcadiusmc.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import org.junit.jupiter.api.Test;

public class ScssFunctionsTest {

  @Test
  void testRgb() {
    Color c = parseColor("rgb(255 255 255)");
    assertEquals(255, c.getAlpha());
    assertEquals(255, c.getRed());
    assertEquals(255, c.getGreen());
    assertEquals(255, c.getBlue());

    c = parseColor("rgba(100 100 100 .5)");
    assertEquals(Color.MAX_VALUE / 2, c.getAlpha());
    assertEquals(100, c.getRed());
    assertEquals(100, c.getGreen());
    assertEquals(100, c.getBlue());

    assertThrows(ChimeraException.class, () -> parseColor("rgb(255)"));
    assertThrows(ChimeraException.class, () -> parseColor("rgb(255 255)"));
    assertThrows(ChimeraException.class, () -> parseColor("rgb(255 255 300)"));
    assertThrows(ChimeraException.class, () -> parseColor("rgb(255 255 255 255)"));
  }

  @Test
  void testBrighten() {
    Color c = parseColor("lighten(green 0.25)");
    Color bg = NamedColor.GREEN.brighten(0.25f);

    assertEquals(bg, c);

    c = parseColor("lighten(green 25%)");
    assertEquals(bg, c);

    c = parseColor("lighten(green)");
    bg = NamedColor.GREEN.brighten();
    assertEquals(bg, c);
  }

  @Test
  void testDarken() {
    Color c = parseColor("darken(green 0.25)");
    Color dg = NamedColor.GREEN.darken(0.25f);

    assertEquals(dg, c);

    c = parseColor("darken(green 25%)");
    assertEquals(dg, c);
  }

  @Test
  void testHsl() {
    Color c = parseColor("hsl(360deg 50% 20%)");
    Color control = Color.hsv(1f, .5f, .2f);
    assertEquals(control, c);

    c = parseColor("hsl(1turn 100% 100%)");
    control = Color.hsv(1f, 1f, 1f);
    assertEquals(control, c);
  }

  @Test
  void testHsla() {
    Color c = parseColor("hsla(1turn 100% 100% 0.5)");
    Color control = Color.hsva(1f, 1f, 1f, 0.5f);
    assertEquals(control, c);
  }

  @Test
  void testSqrt() {
    assertEvaluatesFloat("sqrt(1654)", (float) Math.sqrt(1654));
    assertEvaluatesFloat("sqrt(7 * 7)", 7);
  }

  @Test
  void testMaxMin() {
    assertEvaluatesFloat("max(1, 2)", 2);
    assertEvaluatesFloat("max(2, 1)", 2);

    assertEvaluatesFloat("min(1, 2)", 1);
    assertEvaluatesFloat("min(2, 1)", 1);
  }

  @Test
  void testClamp() {
    assertEvaluatesFloat("clamp(10, 20, 100)", 20);
    assertEvaluatesFloat("clamp(10, 5, 100)", 10);
    assertEvaluatesFloat("clamp(10, 500, 100)", 100);
  }

  void assertEvaluatesFloat(String str, float expected) {
    Primitive prim = parsePrim(str);
    assertEquals(expected, prim.getValue());
  }

  Primitive parsePrim(String str) {
    ChimeraParser parser = Tests.parser(str);
    Expression expr = parser.expr();

    ChimeraContext ctx = parser.createContext();
    Scope scope = Scope.createTopLevel();

    Object o = expr.evaluate(ctx, scope);

    return assertInstanceOf(Primitive.class, o);
  }

  Color parseColor(String str) {
    ChimeraParser parser = Tests.parser(str);
    Expression expr = parser.expr();

    ChimeraContext ctx = parser.createContext();

    Object o = expr.evaluate(ctx, Scope.createTopLevel());
    return assertInstanceOf(Color.class, o);
  }
}

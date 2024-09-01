package net.arcadiusmc.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
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

    assertThrows(ParserException.class, () -> parseColor("rgb(255)"));
    assertThrows(ParserException.class, () -> parseColor("rgb(255 255)"));
    assertThrows(ParserException.class, () -> parseColor("rgb(255 255 300)"));
    assertThrows(ParserException.class, () -> parseColor("rgb(255 255 255 255)"));
  }

  @Test
  void testBrighten() {
    Color c = parseColor("brighten(green 0.25)");
    Color bg = NamedColor.GREEN.brighten(0.25f);

    assertEquals(bg, c);
  }

  @Test
  void testDarken() {
    Color c = parseColor("darken(green 0.25)");
    Color dg = NamedColor.GREEN.darken(0.25f);

    assertEquals(dg, c);
  }

  Color parseColor(String str) {
    ChimeraParser parser = Tests.parser(str);
    Expression expr = parser.expr();

    ChimeraContext ctx = new ChimeraContext(parser.getStream().getInput());
    ctx.setErrors(parser.getErrors());

    Object o = expr.evaluate(ctx);
    return assertInstanceOf(Color.class, o);
  }
}

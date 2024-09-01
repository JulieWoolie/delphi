package net.arcadiusmc.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.Value.ValueType;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
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
    ChimeraParser parser = Tests.parser("background-color: red; color: green;");

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
    ChimeraParser parser = Tests.parser(TEST_SHEET_1);

    ChimeraContext context = new ChimeraContext(parser.getStream().getInput());
    context.setErrors(parser.getErrors());

    SheetStatement sheet = parser.stylesheet();
    ChimeraStylesheet compiled = Chimera.compileSheet(sheet, context);

    assertEquals(1, compiled.getLength());
    Rule r1 = (Rule) compiled.getRule(0);

    assertEquals(".rule1", r1.getSelector());

    PropertySet set = r1.getPropertySet();
    Value<Color> color = set.get(Properties.COLOR);

    assertNotNull(color);
    assertEquals(ValueType.EXPLICIT, color.getType());
    assertEquals("$variable1", color.getTextValue());
    assertEquals(NamedColor.RED, color.getValue());
  }
}
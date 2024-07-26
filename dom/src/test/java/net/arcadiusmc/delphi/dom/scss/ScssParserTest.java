package net.arcadiusmc.delphi.dom.scss;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.arcadiusmc.delphi.Loggers;
import net.arcadiusmc.delphi.parser.ErrorListener;
import net.arcadiusmc.delphi.parser.ParserErrors;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.junit.jupiter.api.Test;

class ScssParserTest {

  static final RectType[] RECT_TYPES = {
      new RectType(
          "padding",
          Properties.PADDING_TOP,
          Properties.PADDING_RIGHT,
          Properties.PADDING_BOTTOM,
          Properties.PADDING_LEFT
      ),
      new RectType(
          "border",
          Properties.BORDER_TOP,
          Properties.BORDER_RIGHT,
          Properties.BORDER_BOTTOM,
          Properties.BORDER_LEFT
      ),
      new RectType(
          "outline",
          Properties.OUTLINE_TOP,
          Properties.OUTLINE_RIGHT,
          Properties.OUTLINE_BOTTOM,
          Properties.OUTLINE_LEFT
      ),
      new RectType(
          "margin",
          Properties.MARGIN_TOP,
          Properties.MARGIN_RIGHT,
          Properties.MARGIN_BOTTOM,
          Properties.MARGIN_LEFT
      ),
  };

  record RectType(
      String type,
      Property<Primitive> top,
      Property<Primitive> right,
      Property<Primitive> bottom,
      Property<Primitive> left
  ) {

  }

  @Test
  void recognizePrimitive() {
    for (Unit value : Unit.values()) {
      String str = "padding-left: 1" + value.getUnit() + ";";
      PropertySet set = assertDoesNotThrow(() -> tryParseInline(str));

      Primitive prim = set.get(Properties.PADDING_LEFT);
      assertEquals(value, prim.getUnit());
    }
  }

  @Test
  void rectangle1() {
    for (RectType rectType : RECT_TYPES) {
      PropertySet set = assertDoesNotThrow(() -> tryParseInline(rectType.type + ": 1px"));
      Primitive val = Primitive.create(1, Unit.PX);

      assertEquals(set.get(rectType.top), val);
      assertEquals(set.get(rectType.right), val);
      assertEquals(set.get(rectType.bottom), val);
      assertEquals(set.get(rectType.left), val);
    }
  }

  @Test
  void rectangle2() {
    for (RectType rectType : RECT_TYPES) {
      PropertySet set = assertDoesNotThrow(() -> tryParseInline(rectType.type + ": 1px 2px"));
      Primitive y = Primitive.create(1, Unit.PX);
      Primitive x = Primitive.create(2, Unit.PX);

      assertEquals(set.get(rectType.top), y);
      assertEquals(set.get(rectType.right), x);
      assertEquals(set.get(rectType.bottom), y);
      assertEquals(set.get(rectType.left), x);
    }
  }

  @Test
  void rectangle3() {
    for (RectType rectType : RECT_TYPES) {
      PropertySet set = assertDoesNotThrow(() -> tryParseInline(rectType.type + ": 1px 2px 3px"));
      Primitive top = Primitive.create(1, Unit.PX);
      Primitive x = Primitive.create(2, Unit.PX);
      Primitive bottom = Primitive.create(3, Unit.PX);

      assertEquals(set.get(rectType.top), top);
      assertEquals(set.get(rectType.right), x);
      assertEquals(set.get(rectType.bottom), bottom);
      assertEquals(set.get(rectType.left), x);
    }
  }

  @Test
  void rectangle4() {
    for (RectType rectType : RECT_TYPES) {
      PropertySet set = assertDoesNotThrow(() -> tryParseInline(rectType.type + ": 1px 2px 3px 4px"));
      Primitive top = Primitive.create(1, Unit.PX);
      Primitive right = Primitive.create(2, Unit.PX);
      Primitive bottom = Primitive.create(3, Unit.PX);
      Primitive left = Primitive.create(4, Unit.PX);

      assertEquals(set.get(rectType.top), top);
      assertEquals(set.get(rectType.right), right);
      assertEquals(set.get(rectType.bottom), bottom);
      assertEquals(set.get(rectType.left), left);
    }
  }

  @Test
  void rgbFunction() {
    // Without commas
    PropertySet set = assertDoesNotThrow(() -> tryParseInline("color: rgb(255 255 255)"));
    assertEquals(set.get(Properties.TEXT_COLOR), NamedColor.WHITE);

    // With commas
    set = assertDoesNotThrow(() -> tryParseInline("color: rgb(255, 255, 255)"));
    assertEquals(set.get(Properties.TEXT_COLOR), NamedColor.WHITE);

    // No arguments
    assertThrows(ParserException.class, () -> tryParseInline("color: rgb()"));

    // Only 1 argument
    assertThrows(ParserException.class, () -> tryParseInline("color: rgb(255)"));

    // Only 2 arguments
    assertThrows(ParserException.class, () -> tryParseInline("color: rgb(255 255)"));
  }

  PropertySet tryParseInline(String in) {
    StringBuffer buf = new StringBuffer(in);
    ScssParser parser = new ScssParser(buf);
    ParserErrors errors = parser.getErrors();

    errors.setListener(ErrorListener.logging(Loggers.getLogger("parser")));

    return parser.inlineRules();
  }

  Sheet tryParse(String in) {
    StringBuffer buf = new StringBuffer(in);
    ScssParser parser = new ScssParser(buf);
    ParserErrors errors = parser.getErrors();

    Sheet sheet = parser.stylesheet();
    errors.orThrow();

    return sheet;
  }
}
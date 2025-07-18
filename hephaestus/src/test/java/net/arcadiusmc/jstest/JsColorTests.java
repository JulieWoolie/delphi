package net.arcadiusmc.jstest;

import static net.arcadiusmc.hephaestus.Scripting.JS_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.NamedColor;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsColorTests {

  static Value bindings;
  static Context context;

  @BeforeAll
  static void setup() {
    Scripting.scriptingInit();
    context = Scripting.setupContext();
    bindings = context.getBindings(JS_LANGUAGE);
  }

  @AfterAll
  static void teardown() {
    context.close();
    context = null;
    bindings = null;
  }

  @BeforeEach
  void beforeEach() {
    bindings.putMember("color", Scripting.wrapReturn(Color.class, NamedColor.INDIGO));
  }

  @Test
  void should_asdasdasd() {
    Value v = bindings.getMember("color");

    assertNotNull(v);
    assertFalse(v.isNull());

    Value r = v.getMember("red");
    assertNotNull(r);
    assertTrue(r.fitsInInt());
    assertEquals(NamedColor.INDIGO.getRed(), r.asInt());
  }

  @Test
  void should_returnAllRgbValuesCorrectly_when_accessed() {
    Value r = context.eval(JS_LANGUAGE, "color.red");
    Value g = context.eval(JS_LANGUAGE, "color.green");
    Value b = context.eval(JS_LANGUAGE, "color.blue");

    assertTrue(r.fitsInInt());
    assertTrue(g.fitsInInt());
    assertTrue(b.fitsInInt());

    assertEquals(NamedColor.INDIGO.getRed(), r.asInt());
    assertEquals(NamedColor.INDIGO.getGreen(), g.asInt());
    assertEquals(NamedColor.INDIGO.getBlue(), b.asInt());
  }

  @Test
  void should_returnAllRgbValuesCorrectly_when_accessedWithIndexes() {
    Value r = context.eval(JS_LANGUAGE, "color[0]");
    Value g = context.eval(JS_LANGUAGE, "color[1]");
    Value b = context.eval(JS_LANGUAGE, "color[2]");

    assertTrue(r.fitsInInt());
    assertTrue(g.fitsInInt());
    assertTrue(b.fitsInInt());

    assertEquals(NamedColor.INDIGO.getRed(), r.asInt());
    assertEquals(NamedColor.INDIGO.getGreen(), g.asInt());
    assertEquals(NamedColor.INDIGO.getBlue(), b.asInt());
  }
}

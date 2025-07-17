package net.arcadiusmc.jstest;

import static net.arcadiusmc.hephaestus.Scripting.JS_LANGUAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import net.arcadiusmc.hephaestus.Scripting;
import net.arcadiusmc.hephaestus.interop.GetProperty;
import net.arcadiusmc.hephaestus.interop.ScriptFunction;
import net.arcadiusmc.hephaestus.interop.SetProperty;
import net.kyori.adventure.text.Component;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeRegistryTests {

  public static class TestObject {
    public int a;
    public int b;
  }

  public static class TestObjectMethods {
    @ScriptFunction
    public static int addTogether(TestObject object) {
      return object.a + object.b;
    }

    @ScriptFunction
    public static void doubleValues(TestObject o) {
      o.a *= 2;
      o.b *= 2;
    }

    @ScriptFunction
    public static void componentInput(TestObject o, Component inp) {
      System.out.println(inp);
    }

    @ScriptFunction
    public static Path randomPathThing(TestObject o) {
      return Path.of("asdas");
    }

    @GetProperty
    public static int a(TestObject o) {
      return o.a;
    }

    @GetProperty("b")
    public static int getB(TestObject o) {
      return o.b;
    }

    @SetProperty("a")
    public static void setA(TestObject o, int v) {
      o.a = v;
    }

    @SetProperty("b")
    public static void setB(TestObject o, int v) {
      o.b = v;
    }
  }

  static Context context;
  static Value jsBindings;
  static TestObject tobj;

  @BeforeAll
  static void setup() {
    Scripting.scriptingInit();

    context = Scripting.setupContext();
    jsBindings = context.getBindings(JS_LANGUAGE);
    Scripting.typeRegistry.register(TestObject.class, TestObjectMethods.class);

    tobj = new TestObject();
    tobj.a = 1;
    tobj.b = 2;

    Object o = Scripting.wrapReturn(tobj);
    jsBindings.putMember("testobj", o);
  }

  @AfterAll
  static void teardown() {
    context.close();
    context = null;
    jsBindings = null;
  }

  @BeforeEach
  void beforeEach() {
    tobj.a = 1;
    tobj.b = 2;
  }

  @Test
  void component_bs() {
    context.eval(JS_LANGUAGE, "testobj.componentInput({text: 'hello'})");
  }

  @Test
  void should_beOkayWithJavaObect_when_idk() {
    Value v = context.eval(JS_LANGUAGE, "testobj.randomPathThing()");
    assertNotNull(v);
    assertFalse(v.isNull());
  }

  @Test
  void should_callAddTogether_when_called() {
    Value v = context.eval(JS_LANGUAGE, "testobj.addTogether()");

    assertTrue(v.fitsInInt());
    assertEquals(3, v.asInt());
  }

  @Test
  void should_callVoidMethod_when_called() {
    Value v = context.eval(JS_LANGUAGE, "testobj.doubleValues()");
    assertTrue(v.isNull());
    assertEquals(2, tobj.a);
    assertEquals(4, tobj.b);
  }

  @Test
  void should_returnA_when_aPropertyAccessed() {
    Value v = context.eval(JS_LANGUAGE, "testobj.a");

    assertTrue(v.fitsInInt());
    assertEquals(1, v.asInt());
  }

  @Test
  void should_changeA_when_aPropertyModified() {
    Value v = context.eval(JS_LANGUAGE, "testobj.a = 20");

    assertTrue(v.fitsInInt());
    assertEquals(20, v.asInt());
    assertEquals(20, tobj.a);
  }

  @Test
  void should_not_returnA_when_bPropertyAccessed() {
    Value v = context.eval(JS_LANGUAGE, "testobj.b");

    assertTrue(v.fitsInInt());
    assertNotEquals(1, v.asInt());
  }

  @Test
  void should_returnUndefined_when_unknownPropertyAccessed() {
    tobj.a = 1;
    tobj.b = 2;

    Value v = context.eval(JS_LANGUAGE, "testobj.ab");

    assertTrue(v.isNull());
  }
}

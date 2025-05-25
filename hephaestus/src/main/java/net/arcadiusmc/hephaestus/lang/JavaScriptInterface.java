package net.arcadiusmc.hephaestus.lang;

import static net.arcadiusmc.hephaestus.ScriptElementSystem.JS_LANGUAGE;

import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class JavaScriptInterface implements LanguageInterface {

  Context ctx;
  Value arrayCtor;
  Value objectCtor;
  Value toJson;
  Value fromJson;

  @Override
  public void initLanguage() {
    ctx = Scripting.setupContext();

    try {
      ctx.eval(JS_LANGUAGE, "'Hello, world!'");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    arrayCtor = ctx.parse(JS_LANGUAGE, "[]");
    objectCtor = ctx.parse(JS_LANGUAGE, "{}");
    toJson = ctx.eval(JS_LANGUAGE, "(x) => JSON.stringify(s)");
    fromJson = ctx.eval(JS_LANGUAGE, "(s) => JSON.parse(s)");
  }

  @Override
  public void shutdown() {
    if (ctx == null) {
      return;
    }

    ctx.close(true);
    arrayCtor = null;
    objectCtor = null;
    toJson = null;
    fromJson = null;
  }

  @Override
  public Value newArray() {
    return arrayCtor.execute();
  }

  @Override
  public void appendToArray(Value array, Object value) {
    array.invokeMember("push", value);
  }

  @Override
  public Value newObject() {
    return objectCtor.execute();
  }

  @Override
  public Value fromJson(String json) {
    return fromJson.execute(json);
  }

  @Override
  public String toJson(Object value) {
    return toJson.execute(value).asString();
  }
}

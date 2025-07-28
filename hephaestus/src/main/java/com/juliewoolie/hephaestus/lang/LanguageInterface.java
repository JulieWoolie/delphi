package com.juliewoolie.hephaestus.lang;

import org.graalvm.polyglot.Value;

public interface LanguageInterface {

  void initLanguage();

  Value newArray();

  Value newObject();

  void appendToArray(Value array, Object value);

  String toJson(Object value);

  Value fromJson(String json);

  void shutdown();
}

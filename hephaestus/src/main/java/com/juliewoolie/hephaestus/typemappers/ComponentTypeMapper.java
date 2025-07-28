package com.juliewoolie.hephaestus.typemappers;

import com.juliewoolie.hephaestus.Scripting;
import net.kyori.adventure.text.Component;
import org.graalvm.polyglot.Value;

public class ComponentTypeMapper implements TypeMapper<Value, Component> {

  @Override
  public Component apply(Value value) {
    return Scripting.toComponent(value, null);
  }

  @Override
  public boolean test(Value value) {
    return true;
  }
}

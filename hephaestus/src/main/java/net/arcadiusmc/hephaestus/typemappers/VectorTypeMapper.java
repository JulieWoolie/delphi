package net.arcadiusmc.hephaestus.typemappers;

import org.bukkit.util.Vector;
import org.graalvm.polyglot.Value;

public class VectorTypeMapper implements TypeMapper<Value, Vector> {

  @Override
  public Vector apply(Value value) {
    if (value.isNumber()) {
      double d = value.asDouble();
      return new Vector(d, d, d);
    }

    if (value.hasArrayElements()) {
      long size = value.getArraySize();
      double x, y, z;

      if (size == 0) {
        x = y = z = 0;
      } else if (size == 1L) {
        x = y = z = getDouble(value.getArrayElement(0));
      } else if (size == 2L) {
        x = getDouble(value.getArrayElement(0));
        y = getDouble(value.getArrayElement(1));
        z = 0.0d;
      } else {
        x = getDouble(value.getArrayElement(0));
        y = getDouble(value.getArrayElement(1));
        z = getDouble(value.getArrayElement(2));
      }

      return new Vector(x, y, z);
    }

    Value xVal = value.getMember("x");
    Value yVal = value.getMember("y");
    Value zVal = value.getMember("z");

    return new Vector(getDouble(xVal), getDouble(yVal), getDouble(zVal));
  }

  private double getDouble(Value value) {
    if (value == null || !value.isNumber()) {
      return 0.0d;
    }
    return value.asDouble();
  }

  @Override
  public boolean test(Value value) {
    return true;
  }
}

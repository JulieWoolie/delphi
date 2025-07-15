package net.arcadiusmc.hephaestus.stdlib;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public enum HsvFunction implements ProxyExecutable {
  HSV,
  ;

  @Override
  public Object execute(Value... arguments) {
    float hue = 1.0f;
    float sat = 1.0f;
    float val = 1.0f;
    float alpha = 1.0f;

    switch (arguments.length) {
      case 4:
        alpha = Scripting.toFloat(arguments[3], 1.0f);
      case 3:
        val = Scripting.toFloat(arguments[2], 1.0f);
      case 2:
        sat = Scripting.toFloat(arguments[1], 1.0f);
      case 1:
        hue = Scripting.toFloat(arguments[0], 1.0f);
      default:
        break;
    }

    return Color.hsva(hue, sat, val, alpha);
  }
}

package com.juliewoolie.hephaestus.stdlib;

import com.juliewoolie.dom.style.Color;
import com.juliewoolie.hephaestus.Scripting;
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

    switch (Math.min(arguments.length, 4)) {
      case 4:
        alpha = Scripting.toFloat(arguments[3], 1.0f);
      case 3:
        val = Scripting.toFloat(arguments[2], 1.0f);
      case 2:
        sat = Scripting.toFloat(arguments[1], 1.0f);
      case 1:
        hue = Scripting.toFloat(arguments[0], 1.0f);
        break;

      default:
        hue = 0.0f;
        sat = 0.0f;
        val = 0.0f;
        break;
    }

    return Color.hsva(hue, sat, val, alpha);
  }
}

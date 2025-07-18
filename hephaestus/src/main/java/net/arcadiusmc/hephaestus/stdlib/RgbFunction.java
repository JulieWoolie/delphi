package net.arcadiusmc.hephaestus.stdlib;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public enum RgbFunction implements ProxyExecutable {
  RGB;

  @Override
  public Object execute(Value... arguments) {
    int r = 255;
    int g = 255;
    int b = 255;
    int a = 255;

    switch (Math.min(arguments.length, 4)) {
      case 4:
        a = Scripting.toInt(arguments[3], a);
      case 3:
        b = Scripting.toInt(arguments[2], b);
      case 2:
        g = Scripting.toInt(arguments[2], g);
      case 1:
        r = Scripting.toInt(arguments[2], r);
        break;

      default:
        r = 0;
        g = 0;
        b = 0;
        break;
    }

    return Scripting.wrapReturn(Color.class, Color.argb(a, r, g, b));
  }
}

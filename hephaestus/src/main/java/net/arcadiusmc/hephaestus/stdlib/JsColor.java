package net.arcadiusmc.hephaestus.stdlib;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.hephaestus.interop.GetProperty;

public class JsColor {

  @GetProperty("red")
  public static int getRed(Color color) {
    return color.getRed();
  }

  @GetProperty("green")
  public static int getGreen(Color c) {
    return c.getGreen();
  }

  @GetProperty("blue")
  public static int getBlue(Color c) {
    return c.getBlue();
  }

  @GetProperty("alpha")
  public static int getAlpha(Color c) {
    return c.getAlpha();
  }
}

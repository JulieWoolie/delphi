package net.arcadiusmc.hephaestus.stdlib;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.hephaestus.interop.GetProperty;
import net.arcadiusmc.hephaestus.interop.IndexRead;
import net.arcadiusmc.hephaestus.interop.ScriptGetLength;

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

  @ScriptGetLength
  public static int length(Color c) {
    return 4;
  }

  @IndexRead
  public static int getComponent(Color c, int idx) {
    return switch (idx) {
      case 0 -> c.getRed();
      case 1 -> c.getGreen();
      case 2 -> c.getBlue();
      case 3 -> c.getAlpha();
      default -> throw new IndexOutOfBoundsException();
    };
  }
}

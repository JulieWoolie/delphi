package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public sealed interface Color permits DelphiColor {

  int MASK = 0xff;
  int MIN_VALUE = 0;
  int MAX_VALUE = 255;

  static Color rgb(int red, int green, int blue) {
    return argb(MAX_VALUE, red, green, blue);
  }

  static Color rgb(int colorData) {
    int r = (colorData >> 16) & MASK;
    int g = (colorData >>  8) & MASK;
    int b = (colorData >>  0) & MASK;
    return rgb(r, g, b);
  }

  static Color argb(int alpha, int red, int green, int blue) {
    return new DelphiColor(alpha, red, green, blue);
  }

  static Color argb(int colorData) {
    int a = (colorData >> 24) & MASK;
    int r = (colorData >> 16) & MASK;
    int g = (colorData >>  8) & MASK;
    int b = (colorData >>  0) & MASK;
    return argb(a, r, g, b);
  }

  static @Nullable Color named(String name) {
    return NamedColor.named(name);
  }

  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getAlpha();

  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getRed();

  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getGreen();

  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getBlue();
}

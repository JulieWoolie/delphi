package net.arcadiusmc.dom.style;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

final class DelphiColor implements Color {

  final byte alpha;
  final byte red;
  final byte green;
  final byte blue;

  public DelphiColor(int alpha, int red, int green, int blue) {
    validate(alpha, "Alpha");
    validate(red, "Red");
    validate(green, "Green");
    validate(blue, "Blue");

    this.alpha = (byte) alpha;
    this.red = (byte) red;
    this.green = (byte) green;
    this.blue = (byte) blue;
  }

  static Color blend(boolean hsv, float progress, Color... colors) {
    if (colors.length == 0) {
      return null;
    }
    if (colors.length == 1) {
      return colors[0];
    }
    if (progress <= 0) {
      return colors[0];
    }
    if (progress >= 1) {
      return colors[colors.length - 1];
    }

    final int max = colors.length - 1;

    int startIdx = (int) (progress * max);
    float firstStep = ((float) startIdx) / max;
    float localStep = (progress - firstStep) * max;

    Color c1 = colors[startIdx];
    Color c2 = colors[startIdx + 1];

    if (hsv) {
      return c1.blendHsv(localStep, c2);
    } else {
      return c1.blendRgb(localStep, c2);
    }
  }

  public static Color hsvaColor(float hue, float saturation, float value, float alpha) {
    int alphaInt = (int) (Math.clamp(alpha, 0f, 1f) * MAX_VALUE);
    int argb = java.awt.Color.HSBtoRGB(hue, saturation, value);
    return Color.argb(argb | (alphaInt << 24));
  }

  private void validate(int v, String channel) {
    if (v >= MIN_VALUE && v <= MAX_VALUE) {
      return;
    }

    throw new IllegalArgumentException(
        channel + " channel is out of bounds [" + MIN_VALUE + ".." + MAX_VALUE + "]: " + v
    );
  }

  @Override
  public int getAlpha() {
    return alpha & MASK;
  }

  @Override
  public int getRed() {
    return red & MASK;
  }

  @Override
  public int getGreen() {
    return green & MASK;
  }

  @Override
  public int getBlue() {
    return blue & MASK;
  }

  @Override
  public float @NotNull [] hsv() {
    return java.awt.Color.RGBtoHSB(getRed(), getGreen(), getBlue(), null);
  }

  @Override
  public float @NotNull [] hsva() {
    float[] arr = new float[4];
    java.awt.Color.RGBtoHSB(getRed(), getGreen(), getBlue(), arr);
    arr[3] = ((float) getAlpha()) / MAX_VALUE;
    return arr;
  }

  @Override
  public int argb() {
    return ((alpha & MASK) << 24)
        | ((red & MASK) << 16)
        | ((green & MASK) << 8)
        | (blue & MASK);
  }

  @Override
  public int rgb() {
    return ((red & MASK) << 16)
        | ((green & MASK) << 8)
        | (blue & MASK);
  }

  @Override
  public String toString() {
    int argb = argb();

    String foundName = NamedColor.VALUE_TO_NAME.get(argb);
    if (foundName != null) {
      return foundName;
    }

    return hexString();
  }

  @Override
  public String hexString() {
    String hex;

    if (getAlpha() == MAX_VALUE) {
      hex = toHex(rgb(), 6);
    } else {
      hex = toHex(argb(), 8);
    }

    return "#" + hex;
  }

  private String toHex(int v, int chars) {
    String hex = Integer.toUnsignedString(v, 16);

    if (hex.length() < chars) {
      return "0".repeat(chars - hex.length()) + hex;
    }

    return hex;
  }

  @Override
  public @NotNull Color brighten() {
    return brighten(0.25f);
  }

  @Override
  public @NotNull Color brighten(@Range(from = 0, to = 1) float amount) {
    amount = Math.clamp(amount, 0f, 1f);
    return mulAdd(amount);
  }

  private static int mulAddChannel(byte v, float mod) {
    int b32 = v & MASK;
    int mul = (int) (mod * MAX_VALUE);
    return Math.clamp(mul + b32, MIN_VALUE, MAX_VALUE);
  }

  @Override
  public @NotNull Color darken() {
    return darken(0.25f);
  }

  @Override
  public @NotNull Color darken(@Range(from = 0, to = 1) float amount) {
    amount = -Math.clamp(amount, 0f, 1f);
    return mulAdd(amount);
  }

  private Color mulAdd(float amount) {
    int r = mulAddChannel(red, amount);
    int g = mulAddChannel(green, amount);
    int b = mulAddChannel(blue, amount);

    return Color.argb(getAlpha(), r, g, b);
  }

  @Override
  public @NotNull Color multiplyRgb(float multiplier) {
    return Color.argb(
        getAlpha(),
        multiplyChannel(red, multiplier),
        multiplyChannel(green, multiplier),
        multiplyChannel(blue, multiplier)
    );
  }

  @Override
  public @NotNull Color multiplyArgb(float multiplier) {
    return Color.argb(
        multiplyChannel(alpha, multiplier),
        multiplyChannel(red, multiplier),
        multiplyChannel(green, multiplier),
        multiplyChannel(blue, multiplier)
    );
  }

  @Override
  public @NotNull Color multiplyHsv(float multiplier) {
    float[] hsv = hsv();
    for (int i = 0; i < hsv.length; i++) {
      hsv[i] = Math.clamp(hsv[i] * multiplier, 0f, 1f);
    }

    int rgb = java.awt.Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
    return Color.argb(rgb & ((alpha & MASK) << 24));
  }

  @Override
  public @NotNull Color multiplyHsv(float hue, float sat, float val) {
    float[] hsv = hsv();

    hsv[0] *= hue;
    hsv[1] *= sat;
    hsv[2] *= val;

    return hsvaColor(hsv[0], hsv[1], hsv[2], ((float) getAlpha()) / MAX_VALUE);
  }

  private static int multiplyChannel(byte c, float amount) {
    int b32 = c & MASK;
    int multiplied = (int) (b32 * amount);
    return Math.clamp(multiplied, MIN_VALUE, MAX_VALUE);
  }

  @Override
  public @NotNull Color blendRgb(float progress, @NotNull Color other) {
    if (progress <= 0) {
      return this;
    } else if (progress >= 1) {
      return other;
    }

    DelphiColor o = (DelphiColor) other;

    return Color.argb(
        getAlpha(),
        lerpChannel(red, o.red, progress),
        lerpChannel(green, o.green, progress),
        lerpChannel(blue, o.blue, progress)
    );
  }

  private static int lerpChannel(byte sv, byte ov, float prog) {
    int s = sv & MASK;
    int o = ov & MASK;
    return (int) (s + ((o - s) * prog));
  }

  @Override
  public @NotNull Color blendHsv(float progress, @NotNull Color other) {
    if (progress <= 0) {
      return this;
    } else if (progress >= 1) {
      return other;
    }

    float[] hsvThis = this.hsv();
    float[] hsvOther = other.hsv();

    float[] values = new float[hsvThis.length];
    for (int i = 0; i < hsvThis.length; i++) {
      float s = hsvThis[i];
      float o = hsvOther[i];
      values[i] = s + ((o - s) * progress);
    }

    int rgb = java.awt.Color.HSBtoRGB(values[0], values[1], values[2]);

    // AND the alpha in so its retained
    return Color.argb(rgb & ((alpha & MASK) << 24));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DelphiColor that)) {
      return false;
    }

    return alpha == that.alpha
        && red == that.red
        && green == that.green
        && blue == that.blue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alpha, red, green, blue);
  }
}

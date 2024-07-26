package net.arcadiusmc.dom.style;

import java.util.Objects;

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
  public String toString() {
    float a = ((float) getAlpha()) / MAX_VALUE;
    return String.format("rgba(%s %s %s %s)", getRed(), getGreen(), getBlue(), a);
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

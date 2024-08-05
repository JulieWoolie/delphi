package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Range;

/**
 * ARGB Color
 * @see NamedColor Named colors
 */
public sealed interface Color permits DelphiColor {

  /**
   * 8 bit, color channel mask
   */
  int MASK = 0xff;

  /**
   * Minimum color value
   */
  int MIN_VALUE = 0;

  /**
   * Maximum color value
   */
  int MAX_VALUE = 255;

  /**
   * Creates a color object from the specified rgb components
   *
   * @param red Red component
   * @param green Green component
   * @param blue Blue component
   *
   * @return Created color
   *
   * @throws IllegalArgumentException If any color component is less than {@link #MIN_VALUE} or
   *                                  greater than {@link #MAX_VALUE}.
   */
  static Color rgb(int red, int green, int blue) throws IllegalArgumentException {
    return argb(MAX_VALUE, red, green, blue);
  }

  /**
   * Creates a color from the specified {@code rgb} value.
   * @param rgb RGB color data
   * @return Created color
   */
  static Color rgb(int rgb) {
    int r = (rgb >> 16) & MASK;
    int g = (rgb >>  8) & MASK;
    int b = (rgb >>  0) & MASK;
    return rgb(r, g, b);
  }

  /**
   * Creates a color object from the specified rgb components
   *
   * @param alpha Alpha component
   * @param red Red component
   * @param green Green component
   * @param blue Blue component
   *
   * @return Created color
   *
   * @throws IllegalArgumentException If any color component is less than {@link #MIN_VALUE} or
   *                                  greater than {@link #MAX_VALUE}.
   */
  static Color argb(int alpha, int red, int green, int blue) throws IllegalArgumentException {
    return new DelphiColor(alpha, red, green, blue);
  }

  /**
   * Creates a color from the specified {@code argb} value.
   * @param argb ARGB color data
   * @return Created color
   */
  static Color argb(int argb) {
    int a = (argb >> 24) & MASK;
    int r = (argb >> 16) & MASK;
    int g = (argb >>  8) & MASK;
    int b = (argb >>  0) & MASK;
    return argb(a, r, g, b);
  }

  /**
   * Get the alpha component.
   * @return Alpha component
   */
  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getAlpha();

  /**
   * Get the red component.
   * @return Red component
   */
  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getRed();

  /**
   * Get the green component.
   * @return Green component
   */
  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getGreen();

  /**
   * Get the blue component.
   * @return Blue component
   */
  @Range(from = MIN_VALUE, to = MAX_VALUE)
  int getBlue();
}

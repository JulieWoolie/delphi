package com.juliewoolie.dom.style;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
   * Creates a color from the specified hue, saturation and value parameters
   *
   * @param hue Hue component (In range {@code 0..1})
   * @param saturation Saturation component (In range {@code 0..1})
   * @param value Value component (In range {@code 0..1})
   *
   * @return Created color
   */
  static Color hsv(float hue, float saturation, float value) {
    return hsva(hue, saturation, value, 1.0f);
  }

  /**
   * Creates a color from the specified hue, saturation, value and alpha parameters
   *
   * @param hue Hue component (In range {@code 0..1})
   * @param saturation Saturation component (In range {@code 0..1})
   * @param value Value component (In range {@code 0..1})
   * @param alpha Alpha component (In range {@code 0..1})
   *
   * @return Created color
   */
  static Color hsva(float hue, float saturation, float value, float alpha) {
    return DelphiColor.hsvaColor(hue, saturation, value, alpha);
  }

  /**
   * Interpolates between multiple colors.
   * <p>
   * If the {@code colors} array is empty or {@code null}, then {@code null} is returned. If only 1
   * color is specified, then that color is always returned. Otherwise, an interpolated color is
   * returned.
   *
   * @param progress Interpolation value (In range {@code 0..1})
   * @param colors Array of colors to interpolate between
   *
   * @return Interpolated colors.
   */
  @Contract("_, null -> null")
  static Color lerpRgb(float progress, Color... colors) {
    return DelphiColor.blend(false, progress, colors);
  }

  /**
   * Interpolates between multiple colors in HSV color space.
   * <p>
   * If the {@code colors} array is empty or {@code null}, then {@code null} is returned. If only 1
   * color is specified, then that color is always returned. Otherwise, an interpolated color is
   * returned.
   *
   * @param progress Interpolation value (In range {@code 0..1})
   * @param colors Array of colors to interpolate between
   *
   * @return Interpolated colors.
   */
  static Color lerpHsv(float progress, Color... colors) {
    return DelphiColor.blend(true, progress, colors);
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

  /**
   * Get the HSV (Hue Saturation Value) color components
   * @return HSV Color components
   */
  float @NotNull [] hsv();

  /**
   * Get the HSVA (Hue Saturation Value Alpha) components.
   * @return HSVA Color components
   */
  float @NotNull [] hsva();

  /**
   * Get the ARGB value of this color.
   * <p>
   * Returned color bits: {@code 0xAARRGGBB}
   *
   * @return ARGB value
   */
  int argb();

  /**
   * Get the RGB value of this color.
   * <p>
   * Returned color bits: {@code 0x00RRGGBB}
   *
   * @return RGB value
   */
  int rgb();

  /**
   * Brighten the color by 25%.
   * <p>
   * Multiplies all channels by 25% and then adds that to the current value of the channel.
   *
   * @return Brightened color
   *
   * @see #brighten(float)
   */
  @NotNull
  Color brighten();

  /**
   * Brighten the color.
   * <p>
   * Multiplies all channels by the specified {@code amount} and then adds that to the current
   * value of the channel.
   *
   * @param amount Amount to brighten (In range {@code 0..1})
   *
   * @return Brightened color
   */
  @NotNull
  Color brighten(@Range(from = 0, to = 1) float amount);

  /**
   * Darken the color by 25%.
   * <p>
   * Multiplies all channels by 25% and then subtracts that from the current value of the channel.
   *
   * @return Darkened color.
   * @see #darken(float)
   */
  @NotNull
  Color darken();

  /**
   * Darken the color.
   * <p>
   * Multiplies all channels by the specified {@code amount} and then subtracts that from the
   * current value of the channel.
   *
   * @return Darkened color.
   */
  @NotNull
  Color darken(@Range(from = 0, to = 1) float amount);

  /**
   * Multiply the RGB values by the specified {@code multiplier}
   * @param multiplier Multiplier
   * @return Multiplied color
   */
  @NotNull
  Color multiplyRgb(float multiplier);

  /**
   * Multiply the ARGB values by the specified {@code multiplier}
   * @param multiplier Multiplier
   * @return Multiplied color
   */
  @NotNull
  Color multiplyArgb(float multiplier);

  /**
   * Convert the color to HSV and then multiply each component by the specified {@code multiplier}
   *
   * @param multiplier Multiplier
   * @return Multiplied color
   */
  @NotNull
  Color multiplyHsv(float multiplier);

  /**
   * Convert the color to HSV and then multiply each component by the specified multiplier values
   *
   * @param hue Hue multiplier
   * @param sat Saturation multiplier
   * @param val Value multiplier
   *
   * @return Multiplied color
   */
  @NotNull
  Color multiplyHsv(float hue, float sat, float val);

  /**
   * Interpolate between {@code this} color and the specified {@code other} color.
   * <p>
   * Interpolation is performed in RGB color space and is simply: <pre><code>
   * self + ((other - self) * progress)</code></pre>
   *
   * @param progress Interpolation value (In range {@code 0..1})
   * @param other Other color
   *
   * @return Interpolated color
   */
  @NotNull
  Color blendRgb(@Range(from = 0, to = 1) float progress, @NotNull Color other);

  /**
   * Interpolate between {@code this} color and the specified {@code other} color in HSV space.
   *
   * @param progress Interpolation value (In range {@code 0..1})
   * @param other Other color
   *
   * @return Interpolated color
   */
  @NotNull
  Color blendHsv(@Range(from = 0, to = 1) float progress, @NotNull Color other);

  /**
   * Get the SCSS string that can be used to represent this string.
   * <p>
   * If the string's value matches a named color (Named colors defined in {@link NamedColor}),
   * then the name is returned. Otherwise, a hex string representing this color is returned.
   *
   * @return SCSS Color representation
   */
  String toString();

  /**
   * Get the hex code representation of this color.
   * @return Hex code
   */
  String hexString();
}

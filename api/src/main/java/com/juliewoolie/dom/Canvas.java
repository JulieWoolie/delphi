package com.juliewoolie.dom;

import com.juliewoolie.dom.style.Color;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

/**
 * A {@link CanvasElement}'s image data.
 */
public interface Canvas {

  /**
   * The width of the canvas.
   * @return Canvas width
   * @see CanvasElement#getWidth()
   */
  int getWidth();

  /**
   * The height of the canvas.
   * @return Canvas height
   * @see CanvasElement#getHeight()
   */
  int getHeight();

  /**
   * Get the element this canvas belongs to.
   * @return Canvas element
   */
  @NotNull
  CanvasElement getElement();

  /**
   * Get a pixel's color value in range [0..1]
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param out Vector to store color value in.
   *
   * @return The {@code out} argument
   *
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   * @throws NullPointerException If {@code out} is {@code null}
   */
  Vector3f getColorf(int x, int y, @NotNull Vector3f out)
      throws NullPointerException, IllegalArgumentException;

  /**
   * Get a pixel's color value in range [0..255]
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param out Vector to store color value in.
   *
   * @return The {@code out} argument
   *
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   * @throws NullPointerException If {@code out} is {@code null}
   */
  Vector3i getColori(int x, int y, @NotNull Vector3i out);

  /**
   * Get a pixel's color value in range [0..1]
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   *
   * @return Color value
   *
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  @NotNull
  Vector3f getColorf(int x, int y) throws IllegalArgumentException;

  /**
   * Get a pixel's color value in range [0..255]
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   *
   * @return Color value
   *
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  @NotNull
  Vector3i getColori(int x, int y) throws IllegalArgumentException;

  /**
   * Get a pixel's color.
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   *
   * @return Color value
   *
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  Color getColor(int x, int y) throws IllegalArgumentException;

  /**
   * Set a pixel's color value.
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param color Color values.
   *
   * @throws NullPointerException If {@code color} is {@code null}
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  void setColor(int x, int y, Color color) throws NullPointerException, IllegalArgumentException;

  /**
   * Set a pixel's color value in range [0..1]
   *
   * <table>
   *   <caption>Color components</caption>
   *   <thead>
   *     <tr>
   *       <th>Color Channel</th>
   *       <th>Vector Component</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>Red</td>
   *       <td>{@code x}</td>
   *     </tr>
   *     <tr>
   *       <td>Green</td>
   *       <td>{@code y}</td>
   *     </tr>
   *     <tr>
   *       <td>Blue</td>
   *       <td>{@code z}</td>
   *     </tr>
   *     <tr>
   *       <td>Alpha</td>
   *       <td>{@code w}</td>
   *     </tr>
   *   </tbody>
   * </table>
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param color Color values.
   *
   * @throws NullPointerException If {@code color} is {@code null}
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  void setColorf(int x, int y, @NotNull Vector4fc color)
      throws IllegalArgumentException, NullPointerException;

  /**
   * Set a pixel's color value in range [0..1]
   *
   * <table>
   *   <caption>Color components</caption>
   *   <thead>
   *     <tr>
   *       <th>Color Channel</th>
   *       <th>Vector Component</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>Red</td>
   *       <td>{@code x}</td>
   *     </tr>
   *     <tr>
   *       <td>Green</td>
   *       <td>{@code y}</td>
   *     </tr>
   *     <tr>
   *       <td>Blue</td>
   *       <td>{@code z}</td>
   *     </tr>
   *   </tbody>
   * </table>
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param color Color values.
   *
   * @throws NullPointerException If {@code color} is {@code null}
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  void setColorf(int x, int y, @NotNull Vector3fc color)
      throws IllegalArgumentException, NullPointerException;

  /**
   * Set a pixel's color value in range [0..255]
   *
   * <table>
   *   <caption>Color components</caption>
   *   <thead>
   *     <tr>
   *       <th>Color Channel</th>
   *       <th>Vector Component</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>Red</td>
   *       <td>{@code x}</td>
   *     </tr>
   *     <tr>
   *       <td>Green</td>
   *       <td>{@code y}</td>
   *     </tr>
   *     <tr>
   *       <td>Blue</td>
   *       <td>{@code z}</td>
   *     </tr>
   *     <tr>
   *       <td>Alpha</td>
   *       <td>{@code w}</td>
   *     </tr>
   *   </tbody>
   * </table>
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param color Color values.
   *
   * @throws NullPointerException If {@code color} is {@code null}
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  void setColori(int x, int y, @NotNull Vector4ic color)
      throws IllegalArgumentException, NullPointerException;

  /**
   * Set a pixel's color value in range [0..255]
   *
   * <table>
   *   <caption>Color components</caption>
   *   <thead>
   *     <tr>
   *       <th>Color Channel</th>
   *       <th>Vector Component</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>Red</td>
   *       <td>{@code x}</td>
   *     </tr>
   *     <tr>
   *       <td>Green</td>
   *       <td>{@code y}</td>
   *     </tr>
   *     <tr>
   *       <td>Blue</td>
   *       <td>{@code z}</td>
   *     </tr>
   *   </tbody>
   * </table>
   *
   * @param x Pixel X coordinate
   * @param y Pixel Y coordinate
   * @param color Color values.
   *
   * @throws NullPointerException If {@code color} is {@code null}
   * @throws IllegalArgumentException If either {@code x} or {@code y} is below 0 or above the
   *         width or height, respectively.
   */
  void setColori(int x, int y, @NotNull Vector3ic color)
      throws IllegalArgumentException, NullPointerException;
}

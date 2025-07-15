package net.arcadiusmc.dom;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Interface that represents a canvas made up of pixels.
 * <p>
 * Canvases are rendered by displaying each pixel as its own {@link org.bukkit.entity.TextDisplay}
 * entity. Thus, when using a canvas element, keep in mind the amount of entities that will be used
 * to render the canvas.
 */
public interface CanvasElement extends Element {

  /**
   * Minimum allowed size for a canvas
   */
  int MIN_SIZE = 0;

  /**
   * Maximum allowed size for a canvas.
   */
  int MAX_SIZE = 1024;

  /**
   * Default size of a canvas element
   */
  int DEFAULT_SIZE = 16;

  /**
   * Canvas elements cannot have any child nodes.
   * @return {@code false}
   */
  @Override @Contract("-> false")
  boolean canHaveChildren();

  /**
   * Get the value of the {@link Attributes#WIDTH} attribute.
   * <p>
   * If the attribute has no value, or is not a valid number, {@link #DEFAULT_SIZE} is returned.
   * <p>
   * <b>Note:</b> There is a brief window of time when the value returned by this method differs
   * from {@link #getCanvas()}'s {@link Canvas#getWidth()}. This happens when the attribute is
   * changed, as canvas is updated after all attribute change listeners have been called.
   *
   * @return Width
   */
  int getWidth();

  /**
   * Set the value of {@link Attributes#WIDTH} attribute.
   * <p>
   * When called, the {@link #getCanvas()}'s {@link Canvas#getWidth()} will not be updated
   * instantly. All attribute update listeners will be called first, then the canvas' size
   * will be updated.
   * <p>
   * When a canvas' size is changed, the canvas is resized, and any existing image data is retained,
   * but the image will be cropped if the size is changed to smaller value than it was before.
   *
   * @param width New width
   *
   * @throws IllegalArgumentException If {@code width} is less than {@link #MIN_SIZE} or greater
   *                                  than {@link #MAX_SIZE}.
   */
  void setWidth(int width) throws IllegalArgumentException;

  /**
   * Get the value of the {@link Attributes#HEIGHT} attribute.
   * <p>
   * If the attribute has no value, or is not a valid number, {@link #DEFAULT_SIZE} is returned.
   * <p>
   * <b>Note:</b> There is a brief window of time when the value returned by this method differs
   * from {@link #getCanvas()}'s {@link Canvas#getHeight()}. This happens when the attribute is
   * changed, as canvas is updated after all attribute change listeners have been called.
   *
   * @return Height
   */
  int getHeight();

  /**
   * Set the value of {@link Attributes#HEIGHT} attribute.
   * <p>
   * When called, the {@link #getCanvas()}'s {@link Canvas#getHeight()} will not be updated
   * instantly. All attribute update listeners will be called first, then the canvas' size
   * will be updated.
   * <p>
   * When a canvas' size is changed, the canvas is resized, and any existing image data is retained,
   * but the image will be cropped if the size is changed to smaller value than it was before.
   *
   * @param height New height
   *
   * @throws IllegalArgumentException If {@code height} is less than {@link #MIN_SIZE} or greater
   *                                  than {@link #MAX_SIZE}.
   */
  void setHeight(int height) throws IllegalArgumentException;

  /**
   * Get the canvas of this element.
   * @return Canvas
   */
  @NotNull
  Canvas getCanvas();
}

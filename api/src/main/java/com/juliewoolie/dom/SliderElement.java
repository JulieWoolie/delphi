package com.juliewoolie.dom;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a slider element
 */
public interface SliderElement extends Element, Disableable {

  /** Default minimum slider value */
  double DEFAULT_MIN = 0.0d;

  /** Default maximum slider value */
  double DEFAULT_MAX = 100.0d;

  /**
   * Slider elements cannot have child nodes.
   * @return {@code false}
   */
  @Override
  boolean canHaveChildren();

  /**
   * Get the slider value.
   * <p>
   * If not set, {@link #getMin()} is returned.
   * <p>
   * The unparsed string value can be accessed by getting the {@link Attributes#VALUE} attribute.
   *
   * @return Slider value
   */
  double getValue();

  /**
   * Set the slider value.
   * @param value Slider value, or {@code null} to unset the value
   * @see #setValue(Double, Player)
   */
  void setValue(@Nullable Double value);

  /**
   * Set the slider value with a specified player.
   * <p>
   * Triggers a {@link com.juliewoolie.dom.event.EventTypes#SLIDER} event to trigger, after which,
   * the {@link Attributes#VALUE} attribute will be set to the value specified.
   *
   * @param value Slider value, or {@code null} to unset the value
   * @param player Source of the change, or {@code null}, if there is no source.
   */
  void setValue(@Nullable Double value, @Nullable Player player);

  /**
   * Get the minimum value of the slider.
   * <p>
   * Minimum slider value is gotten by parsing the {@link Attributes#MIN} value if it's set.
   * If the attribute is not set, then {@link #DEFAULT_MIN} is returned.
   *
   * @return Minimum slider value, or {@link #DEFAULT_MIN} if not explicitly set
   */
  double getMin();

  /**
   * Set the minimum value of the slider.
   * <p>
   * Shorthand for setting the {@link Attributes#MIN} attribute's value.
   *
   * @param min Minimum slider value, or {@code null}, to unset
   */
  void setMin(@Nullable Double min);

  /**
   * Get the maximum value of the slider.
   * <p>
   * Maximum slider value is gotten by parsing the {@link Attributes#MAX} value if it's set.
   * If the attribute is not set, then {@link #DEFAULT_MAX} is returned.
   *
   * @return Maximum slider value, or {@link #DEFAULT_MAX} if not explicitly set
   */
  double getMax();

  /**
   * Set the maximum value of the slider.
   * <p>
   * Shorthand for setting the {@link Attributes#MAX} attribute's value.
   *
   * @param max Maximum slider value, or {@code null}, to unset
   */
  void setMax(@Nullable Double max);

  /**
   * Get the step value.
   * <p>
   * The step value determines the increments in which the slider value is changed by the player. If
   * no step is set, then the slider's value can be set to any value between {@link #getMin()} and
   * {@link #getMax()}.
   * <p>
   * Shorthand for accessing the {@link Attributes#STEP} attribute's value.
   *
   * @return Step value, or {@code null}, if not set.
   */
  @Nullable Double getStep();

  /**
   * Set the step value.
   * <p>
   * Shorthand for setting the {@link Attributes#STEP} attribute's value.
   *
   * @param step Step value, or {@code null}, to unset
   */
  void setStep(@Nullable Double step);

  /**
   * Get the slider's orientation.
   * <p>
   * Shorthand for accessing the {@link Attributes#ORIENT} attribute's value.
   * <p>
   * If the orient attribute is not set, then {@link SliderOrient#DEFAULT} is returned.
   *
   * @return Slider orientation
   */
  @NotNull SliderOrient getOrient();

  /**
   * Set the slider's orientation.
   * <p>
   * Shorthand for setting the {@link Attributes#ORIENT} attribute value.
   *
   * @param orient Orient value, or {@code null}, to unset.
   */
  void setOrient(@Nullable SliderOrient orient);

  /**
   * Get the prompt that will be shown to players if the slider element is opened in a
   * {@link FieldSetElement} dialog.
   * <p>
   * Input element dialogs use minimessage rendering to support color codes and formatting codes.
   *
   * @return Dialog prompt.
   */
  @Nullable String getPrompt();

  /**
   * Set the dialog prompt that will be shown to players if the slider element is opened in a
   * {@link FieldSetElement} dialog.
   * <p>
   * Input element dialogs use minimessage rendering to support color codes and formatting codes.
   *
   * @param prompt Dialog prompt.
   */
  void setPrompt(@Nullable String prompt);

  /**
   * Represents a slider's orientation
   */
  enum SliderOrient {
    /** Horizontal, left to right slider */
    HORIZONTAL ("horizontal"),

    /** Vertical, up and down slider */
    VERTICAL ("vertical"),
    ;

    /**
     * Default orient value
     * @see #HORIZONTAL
     */
    public static final SliderOrient DEFAULT = HORIZONTAL;

    private final String value;

    SliderOrient(String value) {
      this.value = value;
    }

    /**
     * Get the attribute value of the slider orientation.
     * @return Attribute value
     */
    public String getValue() {
      return value;
    }
  }

}

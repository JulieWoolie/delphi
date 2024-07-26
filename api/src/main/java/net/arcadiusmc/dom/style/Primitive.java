package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.NotNull;

/**
 * A primitive style value
 */
public sealed interface Primitive permits PrimitiveImpl {

  /** Zero value constant */
  Primitive ZERO = new PrimitiveImpl(0, Unit.NONE);

  /**
   * Creates a new primitive value.
   * @param base Base value
   * @param unit Value unit
   * @return Created value
   */
  static Primitive create(float base, Unit unit) {
    if (base == 0.0f && unit == Unit.NONE) {
      return ZERO;
    }

    return new PrimitiveImpl(base, unit);
  }

  /**
   * Creates a new primitive value with {@link Unit#NONE}.
   * @param base Base value.
   * @return Created value
   */
  static Primitive create(float base) {
    return create(base, Unit.NONE);
  }

  /**
   * Gets the base value of the primitive
   * @return Base value
   */
  float getValue();

  /**
   * Gets the unit of the value
   * @return Value unit.
   */
  @NotNull Unit getUnit();

  /**
   * Tests if this is a zero value. Zero values have a {@link #getValue()} of 0.
   * @return {@code true}, if this is a 0 value, {@code false}, otherwise.
   */
  boolean isZero();

  /**
   * Style units
   */
  enum Unit {
    /**
     * No modification of the base value is done
     */
    NONE (""),

    /**
     * Base value is multiplied by the size of a pixel
     */
    PX ("px"),

    /**
     * Base value is multiplied by the width of the '0' character
     */
    CH ("ch"),

    /**
     * Base value is a percentage of the screen's width
     */
    VW ("vw"),

    /**
     * Base value is a percentage of the screen's height
     */
    VH ("vh"),
    ;

    private final String unit;

    Unit(String unit) {
      this.unit = unit;
    }

    public String getUnit() {
      return unit;
    }
  }

}

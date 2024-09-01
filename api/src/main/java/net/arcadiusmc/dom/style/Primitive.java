package net.arcadiusmc.dom.style;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * A primitive style value
 */
public sealed interface Primitive permits PrimitiveImpl {

  /** Zero value constant */
  Primitive ZERO = new PrimitiveImpl(0, Unit.NONE);

  /** One value constant */
  Primitive ONE = new PrimitiveImpl(1, Unit.NONE);

  /**
   * Creates a new primitive value.
   *
   * @param base Base value
   * @param unit Value unit
   *
   * @return Created value
   *
   * @throws NullPointerException If {@code unit} is {@code null}
   */
  static Primitive create(float base, @NotNull Unit unit) {
    Objects.requireNonNull(unit, "Null unit");

    if (unit == Unit.NONE) {
      if (base == 0.0f) {
        return ZERO;
      }
      if (base == 1.0f) {
        return ONE;
      }
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

    /**
     * Base value is 1x the size of a block in the game
     */
    M ("m"),

    /**
     * Base value is 1/100th the size of a block in the game
     */
    CM ("cm"),

    /**
     * Base value is 1/100th of the size of the parent element
     */
    PERCENT ("%"),
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

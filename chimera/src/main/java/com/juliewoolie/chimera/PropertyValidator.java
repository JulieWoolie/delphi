package com.juliewoolie.chimera;

import java.util.Optional;
import com.juliewoolie.dom.style.Primitive;

public interface PropertyValidator<T> {

  PropertyValidator<Primitive> NON_ANGLE = value -> {
    return switch (value.getUnit()) {
      case DEG, RAD, GRAD, TURN -> Optional.of("Angular measurement not allowed here");
      default -> Optional.empty();
    };
  };

  PropertyValidator<Primitive> SCALAR = value -> {
    return switch (value.getUnit()) {
      case PERCENT, NONE -> Optional.empty();
      default -> Optional.of("The 'scale' property only allows % measurements");
    };
  };

  PropertyValidator<PrimitiveRect> NON_ANGLE_RECT = rect -> {
    return NON_ANGLE.validate(rect.getTop())
        .or(() -> NON_ANGLE.validate(rect.getRight()))
        .or(() -> NON_ANGLE.validate(rect.getBottom()))
        .or(() -> NON_ANGLE.validate(rect.getLeft()));
  };

  PropertyValidator<PrimitiveLeftRight> NON_ANGLE_LR = value -> {
    return NON_ANGLE.validate(value.getLeft())
        .or(() -> NON_ANGLE.validate(value.getRight()));
  };

  Optional<String> validate(T value);
}

package net.arcadiusmc.chimera;

import java.util.Optional;
import net.arcadiusmc.dom.style.Primitive;

public interface PropertyValidator<T> {

  PropertyValidator<Primitive> NON_ANGLE = value -> {
    return switch (value.getUnit()) {
      case DEG, RAD, GRAD, TURN -> Optional.of("Angular measurement not allowed here");
      default -> Optional.empty();
    };
  };

  PropertyValidator<PrimitiveRect> NON_ANGLE_RECT = rect -> {
    return NON_ANGLE.validate(rect.getTop())
        .or(() -> NON_ANGLE.validate(rect.getRight()))
        .or(() -> NON_ANGLE.validate(rect.getBottom()))
        .or(() -> NON_ANGLE.validate(rect.getLeft()));
  };

  Optional<String> validate(T value);
}

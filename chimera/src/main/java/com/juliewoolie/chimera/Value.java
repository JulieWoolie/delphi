package com.juliewoolie.chimera;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.dom.style.KeywordRepresentable;

@Getter @Setter
public class Value<T> {

  private String textValue = "initial";
  private ValueType type = ValueType.INITIAL;
  private T value;
  private boolean important;
  private boolean enabled = true;

  public static <T> Value<T> create(T value) {
    Value<T> v = new Value<>();
    v.setValue(value);
    v.setType(ValueType.EXPLICIT);

    if (value instanceof KeywordRepresentable rep) {
      v.setTextValue(rep.getKeyword());
    } else if (value instanceof Enum<?> e) {
      v.setTextValue(e.name().toLowerCase().replace("_", "-"));
    } else {
      v.setTextValue(String.valueOf(value));
    }

    return v;
  }

  public enum ValueType {
    INHERIT,
    INITIAL,
    UNSET,
    AUTO,
    EXPLICIT,
    ;
  }
}

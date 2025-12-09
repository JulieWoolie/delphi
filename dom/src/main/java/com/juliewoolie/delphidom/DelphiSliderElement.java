package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.SliderElement;
import com.juliewoolie.dom.TagNames;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class DelphiSliderElement extends DisableableElement implements SliderElement {

  public DelphiSliderElement(DelphiDocument document) {
    super(document, TagNames.SLIDER);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  private double doubleAttr(String attr, double fb) {
    String val = getAttribute(attr);
    if (Strings.isNullOrEmpty(val)) {
      return fb;
    }
    try {
      return Double.parseDouble(val);
    } catch (NumberFormatException exc) {
      return fb;
    }
  }

  private void setDoubleAttr(String attr, Double val) {
    if (val == null) {
      removeAttribute(attr);
    } else {
      setAttribute(attr, String.valueOf(val));
    }
  }

  @Override
  public double getValue() {
    return doubleAttr(Attributes.VALUE, getMin());
  }

  @Override
  public void setValue(@Nullable Double value) {
    setValue(value, null);
  }

  @Override
  public void setValue(@Nullable Double value, @Nullable Player player) {

  }

  @Override
  public double getMin() {
    return doubleAttr(Attributes.MIN, DEFAULT_MIN);
  }

  @Override
  public void setMin(@Nullable Double min) {
    setDoubleAttr(Attributes.MIN, min);
  }

  @Override
  public double getMax() {
    return doubleAttr(Attributes.MAX, DEFAULT_MAX);
  }

  @Override
  public void setMax(@Nullable Double max) {
    setDoubleAttr(Attributes.MAX, max);
  }

  @Override
  public @Nullable Double getStep() {
    String val = getAttribute(Attributes.STEP);
    if (Strings.isNullOrEmpty(val)) {
      return null;
    }
    try {
      return Double.parseDouble(val);
    } catch (NumberFormatException exc) {
      return null;
    }
  }

  @Override
  public void setStep(@Nullable Double step) {
    setDoubleAttr(Attributes.STEP, step);
  }
}

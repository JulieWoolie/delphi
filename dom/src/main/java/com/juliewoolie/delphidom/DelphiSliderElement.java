package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import com.juliewoolie.delphidom.event.SliderEventImpl;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.SliderElement;
import com.juliewoolie.dom.TagNames;
import com.juliewoolie.dom.event.EventTypes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiSliderElement extends DisableableElement implements SliderElement {

  public DelphiSliderElement(DelphiDocument document) {
    super(document, TagNames.SLIDER);
  }

  public double getRatio() {
    double min = getMin();
    double max = getMax();
    double val = getValue();

    if (max < min) {
      double d = max;
      max = min;
      min = d;
    }

    if (val < min) {
      return 0.0d;
    }
    if (val > max) {
      return 1.0d;
    }

    double dif = max - min;
    double r = val - min;

    return r / dif;
  }

  public void setRatio(double ratio, Player player) {
    double min = getMin();
    double max = getMax();
    double val = ((max - min) * ratio) + min;

    setValue(val, player);
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
    Double oldVal = getValue();

    SliderEventImpl event = new SliderEventImpl(EventTypes.SLIDER, document);
    event.initEvent(this, true, true, oldVal, value, player);
    dispatchEvent(event);

    if (event.isCancelled()) {
      return;
    }

    setDoubleAttr(Attributes.VALUE, value);
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

  @Override
  public @NotNull SliderOrient getOrient() {
    String value = getAttribute(Attributes.ORIENT);

    if (Strings.isNullOrEmpty(value)) {
      return SliderOrient.HORIZONTAL;
    }

    return switch (value.toLowerCase()) {
      case "vertical" -> SliderOrient.VERTICAL;
      default -> SliderOrient.HORIZONTAL;
    };
  }

  @Override
  public void setOrient(@Nullable SliderOrient orient) {
    if (orient == null) {
      removeAttribute(Attributes.ORIENT);
      return;
    }

    setAttribute(Attributes.ORIENT, orient.getValue());
  }

  @Override
  public @Nullable String getPrompt() {
    return getAttribute(Attributes.PROMPT);
  }

  @Override
  public void setPrompt(@Nullable String prompt) {
    setAttribute(Attributes.PROMPT, prompt);
  }
}

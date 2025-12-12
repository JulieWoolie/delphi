package com.juliewoolie.delphidom.event;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiSliderElement;
import com.juliewoolie.dom.event.SliderEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class SliderEventImpl extends EventImpl implements SliderEvent {

  Double previousValue;
  Double newValue;
  Player player;

  public SliderEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  @Override
  public DelphiSliderElement getTarget() {
    return (DelphiSliderElement) super.getTarget();
  }

  public void initEvent(
      DelphiSliderElement target,
      boolean bubbles,
      boolean cancellable,
      Double newVal,
      Double oldVal,
      Player player
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.previousValue = oldVal;
    this.newValue = newVal;
    this.player = player;
  }
}

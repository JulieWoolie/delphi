package com.juliewoolie.delphidom.event;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.event.TooltipEvent;
import lombok.Getter;

@Getter
public class TooltipEventImpl extends EventImpl implements TooltipEvent {

  private DelphiElement oldTooltip;
  private DelphiElement newTooltip;

  public TooltipEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      DelphiElement oldTooltip,
      DelphiElement newTooltip
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.oldTooltip = oldTooltip;
    this.newTooltip = newTooltip;
  }
}

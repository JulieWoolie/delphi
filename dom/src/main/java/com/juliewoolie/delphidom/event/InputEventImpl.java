package com.juliewoolie.delphidom.event;

import lombok.Getter;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiInputElement;
import com.juliewoolie.dom.event.InputEvent;
import org.bukkit.entity.Player;

@Getter
public class InputEventImpl extends EventImpl implements InputEvent {

  String newValue;
  String previousValue;
  Player player;

  public InputEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  @Override
  public DelphiInputElement getTarget() {
    return (DelphiInputElement) super.getTarget();
  }

  public void initEvent(
      DelphiInputElement target,
      boolean bubbles,
      boolean cancellable,
      String nval,
      String pval,
      Player player
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.newValue = nval;
    this.previousValue = pval;
    this.player = player;
  }
}

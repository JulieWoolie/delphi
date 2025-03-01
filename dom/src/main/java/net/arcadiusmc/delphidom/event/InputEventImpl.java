package net.arcadiusmc.delphidom.event;

import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiInputElement;
import net.arcadiusmc.dom.event.InputEvent;
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

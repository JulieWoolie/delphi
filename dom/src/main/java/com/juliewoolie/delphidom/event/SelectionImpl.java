package com.juliewoolie.delphidom.event;

import com.juliewoolie.delphidom.DelphiChoiceElement;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.event.SelectionEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class SelectionImpl extends EventImpl implements SelectionEvent {

  private DelphiChoiceElement choice;
  private Player player;

  public SelectionImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      Player player,
      DelphiChoiceElement choice
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.choice = choice;
    this.player = player;
  }
}

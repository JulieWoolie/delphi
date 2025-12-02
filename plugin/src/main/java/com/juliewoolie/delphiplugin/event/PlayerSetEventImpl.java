package com.juliewoolie.delphiplugin.event;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.event.EventImpl;
import com.juliewoolie.dom.event.PlayerSetEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

public class PlayerSetEventImpl extends EventImpl implements PlayerSetEvent {

  @Getter
  private Player player;

  public PlayerSetEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(DelphiElement target, boolean bubbles, boolean cancellable, Player player) {
    super.initEvent(target, bubbles, cancellable);
    this.player = player;
  }
}

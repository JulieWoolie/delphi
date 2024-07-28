package net.arcadiusmc.delphidom.event;

import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.dom.event.MutationEvent;

@Getter
public class Mutation extends EventImpl implements MutationEvent {

  DelphiNode node;

  public Mutation(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      DelphiNode node
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.node = node;
  }
}

package com.juliewoolie.delphidom.event;

import lombok.Getter;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.dom.event.MutationEvent;

@Getter
public class Mutation extends EventImpl implements MutationEvent {

  DelphiNode node;
  int mutationIndex;

  public Mutation(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      DelphiNode node,
      int mutationIndex
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.node = node;
    this.mutationIndex = mutationIndex;
  }
}

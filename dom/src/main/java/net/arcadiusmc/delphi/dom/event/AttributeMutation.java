package net.arcadiusmc.delphi.dom.event;

import lombok.Getter;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.AttributeAction;

@Getter
public class AttributeMutation extends EventImpl implements AttributeMutateEvent {

  private String key;
  private String previousValue;
  private String newValue;

  AttributeAction action;

  public AttributeMutation(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      String key,
      String prevValue,
      String newValue,
      AttributeAction action
  ) {
    super.initEvent(target, bubbles, cancellable);

    this.key = key;
    this.previousValue = prevValue;
    this.newValue = newValue;
    this.action = action;
  }
}

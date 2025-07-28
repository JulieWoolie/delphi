package com.juliewoolie.delphidom.event;

import lombok.Getter;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.AttributeAction;

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

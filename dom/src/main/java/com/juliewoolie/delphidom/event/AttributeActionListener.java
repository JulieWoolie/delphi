package com.juliewoolie.delphidom.event;

import com.juliewoolie.dom.event.AttributeAction;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.EventListener;

public record AttributeActionListener(
    EventListener.Typed<AttributeMutateEvent> listener,
    AttributeAction action
) implements EventListener.Typed<AttributeMutateEvent> {

  @Override
  public void handleEvent(AttributeMutateEvent event) {
    if (event.getAction() != action) {
      return;
    }

    listener.handleEvent(event);
  }
}

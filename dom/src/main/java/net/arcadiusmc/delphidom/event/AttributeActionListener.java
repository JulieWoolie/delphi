package net.arcadiusmc.delphidom.event;

import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;

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

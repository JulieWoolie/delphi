package net.arcadiusmc.delphidom.event;

import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;

public record MouseButtonListener(EventListener.Typed<MouseEvent> listener, MouseButton button)
    implements EventListener.Typed<MouseEvent>
{

  @Override
  public void handleEvent(MouseEvent event) {
    if (event.getButton() != button) {
      return;
    }

    listener.handleEvent(event);
  }
}

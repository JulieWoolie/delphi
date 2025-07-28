package com.juliewoolie.delphidom.event;

import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;

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

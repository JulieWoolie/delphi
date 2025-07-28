package com.juliewoolie.delphidom.event;

import java.util.List;
import com.juliewoolie.dom.event.EventListener;

public class ListenerBuffer {

  EventListener[] listeners;
  int len;
  final int bufferId;

  public ListenerBuffer(int bufferId) {
    this.listeners = new EventListener[5];
    this.bufferId = bufferId;
  }

  public void copy(List<EventListener> listeners) {
    if (this.listeners.length < listeners.size()) {
      this.listeners = new EventListener[listeners.size()];
    }

    for (int i = 0; i < listeners.size(); i++) {
      this.listeners[i] = listeners.get(i);
    }

    this.len = listeners.size();
  }
}

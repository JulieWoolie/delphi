package net.arcadiusmc.delphi.dom.event;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventPhase;
import net.arcadiusmc.dom.event.EventTarget;


@Getter
public class EventImpl implements Event {

  static final int FLAG_CANCELLED = 0x1;
  static final int FLAG_CANCELLABLE = 0x2;
  static final int FLAG_BUBBLING = 0x4;
  static final int FLAG_PROPAGATION_STOPPED = 0x8;
  static final int FLAG_COMPOSED = 0x10;

  private final String type;
  private final DelphiDocument document;

  DelphiElement target;

  @Setter
  EventTarget currentTarget;

  @Getter @Setter
  EventPhase phase;

  int flags = 0;

  public EventImpl(String type, DelphiDocument document) {
    this.type = type;
    this.document = document;
  }

  public final void initEvent(DelphiElement target, boolean bubbles, boolean cancellable) {
    this.target = target;

    if (bubbles) {
      flags |= FLAG_BUBBLING;
    }

    if (cancellable) {
      flags |= FLAG_CANCELLABLE;
    }

    flags |= FLAG_COMPOSED;
  }

  private boolean hasFlag(int mask) {
    return (flags & mask) == mask;
  }

  @Override
  public boolean isCancelled() {
    return hasFlag(FLAG_CANCELLED);
  }

  @Override
  public boolean isBubbling() {
    return hasFlag(FLAG_BUBBLING);
  }

  @Override
  public boolean isPropagationStopped() {
    return hasFlag(FLAG_PROPAGATION_STOPPED);
  }

  @Override
  public boolean isCancellable() {
    return hasFlag(FLAG_CANCELLABLE);
  }

  @Override
  public boolean isComposed() {
    return hasFlag(FLAG_COMPOSED);
  }

  @Override
  public void stopPropagation() {
    flags |= FLAG_PROPAGATION_STOPPED;
  }

  @Override
  public void preventDefault() {
    if (!isCancellable()) {
      return;
    }

    flags |= FLAG_CANCELLABLE;
  }
}

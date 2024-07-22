package net.arcadiusmc.delphi.dom.event;

import lombok.Getter;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.ScrollDirection;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class MouseEventImpl extends EventImpl implements MouseEvent {

  boolean shiftPressed;
  MouseButton button;
  ScrollDirection scrollDirection;

  float screenX;
  float screenY;

  Vector3f worldPosition;

  public MouseEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      boolean shiftPressed,
      MouseButton button,
      ScrollDirection direction,
      float screenX,
      float screenY,
      Vector3f worldPosition
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.shiftPressed = shiftPressed;
    this.button = button;
    this.scrollDirection = direction;
    this.screenX = screenX;
    this.screenY = screenY;
    this.worldPosition = worldPosition;
  }

  @Override
  public Vector2f getScreenPosition() {
    return new Vector2f(screenX, screenY);
  }

  @Override
  public Vector3f getWorldPosition() {
    return new Vector3f(worldPosition);
  }
}

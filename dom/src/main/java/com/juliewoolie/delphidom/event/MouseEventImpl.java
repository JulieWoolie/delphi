package com.juliewoolie.delphidom.event;

import lombok.Getter;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
import com.juliewoolie.dom.event.ScrollDirection;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class MouseEventImpl extends EventImpl implements MouseEvent {

  boolean shiftPressed;
  MouseButton button;
  ScrollDirection scrollDirection;

  Vector2f screenPosition;
  Vector3f worldPosition;

  Player player;

  public MouseEventImpl(String type, DelphiDocument document) {
    super(type, document);
  }

  public void initEvent(
      DelphiElement target,
      boolean bubbles,
      boolean cancellable,
      Player player,
      boolean shiftPressed,
      MouseButton button,
      ScrollDirection direction,
      Vector2f screenPosition,
      Vector3f worldPosition
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.player = player;
    this.shiftPressed = shiftPressed;
    this.button = button;
    this.scrollDirection = direction;
    this.screenPosition = screenPosition;
    this.worldPosition = worldPosition;
  }

  @Override
  public Vector2f getScreenPosition() {
    return new Vector2f(screenPosition);
  }

  @Override
  public Vector3f getWorldPosition() {
    return new Vector3f(worldPosition);
  }
}

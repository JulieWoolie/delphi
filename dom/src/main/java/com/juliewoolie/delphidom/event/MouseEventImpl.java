package com.juliewoolie.delphidom.event;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3d;

@Getter
public class MouseEventImpl extends EventImpl implements MouseEvent {

  boolean shiftPressed;
  MouseButton button;

  Vector2f screenPosition;
  Vector3d worldPosition;

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
      Vector2f screenPosition,
      Vector3d worldPosition
  ) {
    super.initEvent(target, bubbles, cancellable);
    this.player = player;
    this.shiftPressed = shiftPressed;
    this.button = button;
    this.screenPosition = screenPosition;
    this.worldPosition = worldPosition;
  }

  @Override
  public Vector2f getScreenPosition() {
    return new Vector2f(screenPosition);
  }

  @Override
  public Vector3d getWorldPosition() {
    return new Vector3d(worldPosition);
  }
}

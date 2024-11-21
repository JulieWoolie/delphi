package net.arcadiusmc.delphidom.event;

import lombok.Getter;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.ScrollDirection;
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

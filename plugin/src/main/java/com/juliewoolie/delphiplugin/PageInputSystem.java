package com.juliewoolie.delphiplugin;

import java.util.Objects;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphidom.event.MouseEventImpl;
import com.juliewoolie.dom.ButtonElement;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
import com.juliewoolie.dom.event.ScrollDirection;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class PageInputSystem {

  public static final Sound CLICK_SOUND = Sound.sound()
      .type(org.bukkit.Sound.UI_BUTTON_CLICK)
      .build();

  public static final Sound DISABLED_BUTTON_SOUND = Sound.sound()
      .type(org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR)
      .build();

  private final PageView view;

  DelphiElement hoveredNode = null;
  DelphiElement clickedNode = null;
  MouseButton clickedButton = MouseButton.NONE;
  int clickedNodeTicks = 0;
  Player clickedNodePlayer = null;
  Player hoveredNodePlayer = null;

  public PageInputSystem(PageView view) {
    this.view = view;
  }

  void tick() {
    if (clickedNodeTicks <= 0) {
      return;
    }

    clickedNodeTicks--;

    if (clickedNodeTicks > 0) {
      return;
    }

    unselectClickedNode();
  }

  private MouseEventImpl fireMouseEvent(
      String type,
      Player player,
      boolean shift,
      MouseButton button,
      DelphiElement target,
      boolean bubbles,
      boolean cancellable
  ) {
    MouseEventImpl event = new MouseEventImpl(type, view.getDocument());
    event.initEvent(
        target,
        bubbles,
        cancellable,
        player,
        shift,
        button,
        ScrollDirection.NONE,
        view.cursorScreen,
        view.cursorWorld
    );

    target.dispatchEvent(event);
    return event;
  }

  void triggerClickEvent(Player player, MouseButton button, boolean shift) {
    if (hoveredNode == null) {
      return;
    }

    if (clickedNode != null && !Objects.equals(clickedNode, hoveredNode)) {
      unselectClickedNode();
    }

    this.clickedButton = button;
    this.clickedNodeTicks = Document.ACTIVE_TICKS;
    this.clickedNode = hoveredNode;
    this.clickedNodePlayer = player;

    view.getDocument().clicked = this.clickedNode;

    propagateFlagState(true, NodeFlag.CLICKED, clickedNode);

    MouseEvent event = fireMouseEvent(
        EventTypes.CLICK,
        player,
        shift,
        button,
        clickedNode,
        true,
        true
    );

    if (event.isCancelled()) {
      return;
    }

    if (clickedNode instanceof ButtonElement btn) {
      if (btn.isEnabled()) {
        player.playSound(CLICK_SOUND);
      } else {
        player.playSound(DISABLED_BUTTON_SOUND);
      }
    }
  }

  private void unselectClickedNode() {
    if (clickedNode == null) {
      return;
    }

    propagateFlagState(false, NodeFlag.CLICKED, clickedNode);

    fireMouseEvent(
        EventTypes.CLICK_EXPIRE,
        clickedNodePlayer,
        false,
        clickedButton,
        clickedNode,
        false,
        false
    );

    clickedNode = null;
    clickedNodeTicks = 0;
    clickedButton = MouseButton.NONE;
    clickedNodePlayer = null;

    view.getDocument().clicked = null;
  }

  void unselectHovered() {
    if (this.hoveredNode == null) {
      return;
    }

    propagateFlagState(false, NodeFlag.HOVERED, hoveredNode);
    fireMouseEvent(
        EventTypes.MOUSE_LEAVE,
        hoveredNodePlayer,
        false,
        MouseButton.NONE,
        this.hoveredNode,
        true,
        false
    );

    this.hoveredNode = null;
    this.hoveredNodePlayer = null;

    view.getDocument().hovered = null;
  }

  private void propagateFlagState(boolean state, NodeFlag flag, DelphiNode node) {
    DelphiNode p = node;

    while (p != null) {
      if (state) {
        p.addFlag(flag);
      } else {
        p.removeFlag(flag);
      }

      DelphiElement parent = p.getParent();

      if (parent == null) {
        view.getDocument().getStyles().updateDomStyle(p);
      }

      p = parent;
    }
  }

  void updateSelectedNode(Player player) {
    DelphiElement contained = view.renderer.findCursorContainingNode(view.cursorScreen);

    if (contained == null) {
      if (this.hoveredNode == null) {
        return;
      }

      unselectHovered();
      return;
    }

    if (Objects.equals(contained, hoveredNode)) {
      fireMouseEvent(EventTypes.MOUSE_MOVE,
          player,
          false,
          MouseButton.NONE,
          this.hoveredNode,
          false,
          false
      );

      return;
    }

    unselectHovered();

    this.hoveredNode = contained;
    this.hoveredNodePlayer = player;

    view.getDocument().hovered = hoveredNode;
    propagateFlagState(true, NodeFlag.HOVERED, hoveredNode);

    fireMouseEvent(EventTypes.MOUSE_ENTER, player, false, MouseButton.NONE, contained, true, false);
  }
}

package com.juliewoolie.delphirender;

import static com.juliewoolie.delphirender.FloatingWindow.FWT_TOOLTIP;
import static com.juliewoolie.delphirender.RenderSystem.TOOLTIP_DEPTH_SCALE;

import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.TooltipBehaviour;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

public class FloatingManager {

  private final List<FloatingWindow> floatingWindows = new ArrayList<>();
  private final List<FloatingWindow> toClear = new ArrayList<>();
  private final RenderSystem system;

  public FloatingManager(RenderSystem system) {
    this.system = system;
  }

  static RenderObject setupTooltipRenderObject(
      RenderSystem system,
      DelphiElement tooltip,
      Vector2f cursor,
      float depthScaleMod
  ) {
    TooltipBehaviour behaviour = tooltip.getTooltipBehaviour();

    RenderObject obj = system.getRenderElement(tooltip);
    if (obj == null) {
      obj = system.initRenderTree(tooltip, TOOLTIP_DEPTH_SCALE * depthScaleMod);
    }

    tooltip.getDocument().getStyles().updateDomStyle(tooltip);

    if (obj instanceof ElementRenderObject eObj) {
      LayoutCall.nlayout(eObj, system.screen.getDimensions());
    }

    ElementRenderObject parent = (ElementRenderObject) system.getRenderElement(tooltip.getParent());
    assert parent != null;

    switch (behaviour) {
      case ABOVE:
        obj.moveTo(parent.position.x, parent.position.y + obj.size.y);
        break;
      case RIGHT:
        obj.moveTo(parent.position.x + parent.size.x, parent.position.y);
        break;
      case BELOW:
        obj.moveTo(parent.position.x, parent.position.y - parent.size.y);
        break;
      case LEFT:
        obj.moveTo(parent.position.x - obj.size.x, parent.position.y);
        break;

      default:
        obj.moveTo(cursor);
        break;
    }

    return obj;
  }

  void onTooltipSwapped(DelphiElement el, DelphiNode old) {
    FloatingWindow win = getFromDomNode(old);
    if (win == null) {
      return;
    }

    recursiveKill(win);
  }

  FloatingWindow getFromDomNode(DelphiNode node) {
    for (int i = 0; i < floatingWindows.size(); i++) {
      FloatingWindow win = floatingWindows.get(i);
      if (win.domNode == node) {
        return win;
      }
    }
    return null;
  }

  void tick() {
    tickDelays();
    tickPositions();
    checkForHovered();
    tickWindows();
    killQueued();
  }

  int findWindowDepth(Element hovered) {
    if (!hovered.hasFlag(NodeFlag.TOOLTIP)) {
      return 1;
    }

    DelphiNode ttRoot = system.getTooltipRootElement((DelphiNode) hovered);
    FloatingWindow w = getFromDomNode(ttRoot);

    int i = 1;
    while (w != null) {
      i++;
      w = w.parent;
    }

    return i;
  }

  void checkForHovered() {
    Element hovered = system.getView().getDocument().getHoveredElement();
    if (hovered == null) {
      return;
    }

    DelphiElement tt = (DelphiElement) hovered.getTooltip();
    if (tt == null) {
      return;
    }

    FloatingWindow win = getFromDomNode(tt);
    if (win != null) {
      return;
    }

    Vector2f cursor = system.getView().getCursorScreen();
    int d = findWindowDepth(hovered);

    win = new FloatingWindow();
    win.type = FWT_TOOLTIP;
    win.domNode = tt;
    win.appearDelay = tt.getTooltipDelay();
    win.toolTipbehaviour = tt.getTooltipBehaviour();
    win.ro = setupTooltipRenderObject(system, tt, cursor, d);

    if (hovered.hasFlag(NodeFlag.TOOLTIP)) {
      DelphiNode ttRoot = system.getTooltipRootElement((DelphiNode) hovered);
      FloatingWindow rootWin = getFromDomNode(ttRoot);

      if (rootWin != null) {
        rootWin.child = win;
        win.parent = rootWin;
      }
    }

    floatingWindows.addLast(win);

    if (win.appearDelay < 0) {
      win.ro.spawnRecursive();
    }
  }

  void tickPositions() {
    Vector2f cursor = system.getView().getCursorScreen();

    for (int i = 0; i < floatingWindows.size(); i++) {
      FloatingWindow win = floatingWindows.get(i);

      if (!win.held
          || win.type != FWT_TOOLTIP
          || win.toolTipbehaviour != TooltipBehaviour.CURSOR_STICKY
          || win.ro == null
      ) {
        continue;
      }

      if (win.ro.position.equals(cursor.x, cursor.y)) {
        continue;
      }

      win.ro.moveTo(cursor);
    }
  }

  void tickWindows() {
    Vector2f cursor = system.getView().getCursorScreen();

    for (int i = 0; i < floatingWindows.size(); i++) {
      FloatingWindow win = floatingWindows.get(i);
      if (win.type != FWT_TOOLTIP) {
        continue;
      }
      if (win.ro == null) {
        win.held = false;
        continue;
      }

      if (win.toolTipbehaviour == TooltipBehaviour.CURSOR_STICKY) {
        win.held = false;
      } else {
        win.held = system.containsPoint(win.ro, cursor);
      }

      if (win.held) {
        continue;
      }

      DelphiElement parent = win.domNode.getParent();
      if (parent == null) {
        continue;
      }

      RenderObject ro = system.getRenderElement(parent);
      if (ro == null) {
        continue;
      }

      win.held = system.containsPoint(ro, cursor);
    }

    for (int i = 0; i < floatingWindows.size(); i++) {
      FloatingWindow win = floatingWindows.get(i);

      if (win.type != FWT_TOOLTIP || win.held) {
        continue;
      }

      win.held = childrenHeld(win);

      if (!win.held) {
        toClear.addLast(win);
      }
    }
  }

  boolean childrenHeld(FloatingWindow win) {
    if (win.child == null) {
      return false;
    }
    if (win.child.held) {
      return true;
    }
    return childrenHeld(win.child);
  }

  void killQueued() {
    for (int i = 0; i < toClear.size(); i++) {
      FloatingWindow win = toClear.get(i);
      recursiveKill(win);
    }
    toClear.clear();
  }

  void recursiveKill(FloatingWindow win) {
    if (win.ro != null) {
      win.ro.killRecursive();
    }

    if (win.parent != null) {
      win.parent.child = null;
    }

    floatingWindows.remove(win);

    if (win.child == null) {
      return;
    }

    recursiveKill(win.child);
  }

  void tickDelays() {
    for (int i = 0; i < floatingWindows.size(); i++) {
      FloatingWindow value = floatingWindows.get(i);

      if (value.type != FWT_TOOLTIP || value.appearDelay < 0) {
        continue;
      }

      value.appearDelay--;

      if (value.appearDelay >= 1) {
        return;
      }

      RenderObject activeTooltip = value.ro;

      if (value.toolTipbehaviour == TooltipBehaviour.CURSOR) {
        activeTooltip.moveTo(system.view.getCursorScreen());
      }

      activeTooltip.spawnRecursive();
      value.appearDelay = -1;
    }
  }
}

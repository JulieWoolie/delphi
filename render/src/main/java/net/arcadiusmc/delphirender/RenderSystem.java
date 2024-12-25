package net.arcadiusmc.delphirender;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.DirtyBit;
import net.arcadiusmc.chimera.StyleUpdateCallbacks;
import net.arcadiusmc.chimera.system.StyleNode;
import net.arcadiusmc.chimera.system.StyleObjectModel;
import net.arcadiusmc.delphidom.ChatNode;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiItemElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Text;
import net.arcadiusmc.delphirender.content.ComponentContent;
import net.arcadiusmc.delphirender.content.ItemContent;
import net.arcadiusmc.delphirender.content.StringContent;
import net.arcadiusmc.delphirender.dom.ContentRenderObject;
import net.arcadiusmc.delphirender.dom.ElementRenderObject;
import net.arcadiusmc.delphirender.dom.RenderObject;
import net.arcadiusmc.delphirender.layout.NLayout;
import net.arcadiusmc.delphirender.math.Rectangle;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.NodeFlag;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.MutationEvent;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.joml.Vector2f;

@Getter @Setter
public class RenderSystem implements StyleUpdateCallbacks {

  final ExtendedView view;
  final RenderScreen screen;

  private World world;
  private boolean active;
  private FontMeasureCallback fontMetrics;

  private final List<Entity> entities = new ObjectArrayList<>();

  private final Map<DelphiNode, RenderObject> renderObjects = new Object2ObjectOpenHashMap<>();
  private ElementRenderObject renderRoot = null;

  public RenderSystem(ExtendedView view, RenderScreen screen) {
    this.view = view;
    this.screen = screen;
  }

  public void init() {
    active = true;

    DelphiElement body = (DelphiElement) view.getDocument().getBody();
    if (body != null) {
      renderRoot = (ElementRenderObject) initRenderTree(body);
    }

    EventTarget g = view.getDocument().getGlobalTarget();
    MutationListener listener = new MutationListener();
    TooltipListener tooltipListener = new TooltipListener();

    g.addEventListener(EventTypes.APPEND_CHILD, listener);
    g.addEventListener(EventTypes.REMOVE_CHILD, listener);

    g.addEventListener(EventTypes.MOUSE_ENTER, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_LEAVE, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_MOVE, tooltipListener);
  }

  public void close() {
    kill();

    renderObjects.clear();
    renderRoot = null;
    active = false;
  }

  public void spawn() {
    if (renderRoot == null || !active) {
      return;
    }

    renderRoot.moveTo(new Vector2f(0, view.getScreen().getHeight()));
    NLayout.reflow(renderRoot);

    renderRoot.killRecursive();
    renderRoot.spawnRecursive();
  }

  public void kill() {
    for (Entity entity : entities) {
      entity.remove();
    }

    entities.clear();
  }

  private void triggerRealign() {
    if (renderRoot == null || !active) {
      return;
    }

    NLayout.reflow(renderRoot);
  }

  private void triggerUpdate() {
    if (renderRoot == null || !active) {
      return;
    }

    renderRoot.spawnRecursive();
  }

  public RenderObject getRenderObject(Node node) {
    return renderObjects.get(node);
  }

  public void removeRenderElement(DelphiElement element) {
    RenderObject obj = renderObjects.remove(element);

    if (obj == null) {
      return;
    }

    obj.kill();
  }

  public RenderObject initRenderTree(DelphiNode node) {
    RenderObject obj;

    StyleObjectModel styles = node.getDocument().getStyles();
    StyleNode styleNode = styles.getStyleNode(node);

    if (styleNode == null) {
      styleNode = styles.createNode(node);
    }

    ComputedStyleSet styleSet = styleNode.getComputedSet();

    switch (node) {
      case Text text -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet);
        StringContent content = new StringContent(text.getTextContent());
        content.metrics = fontMetrics;

        o.setContent(content);
        obj = o;
      }
      case ChatNode chat -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet);
        ComponentContent content = new ComponentContent(chat.getContent());
        content.metrics = fontMetrics;

        o.setContent(content);
        obj = o;
      }
      case DelphiItemElement item -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet);
        o.setContent(new ItemContent(item.getItemStack()));
        obj = o;
      }
      default -> {
        ElementRenderObject o = new ElementRenderObject(this, styleSet);
        DelphiElement el = (DelphiElement) node;

        for (DelphiNode delphiNode : el.childList()) {
          o.addChild(initRenderTree(delphiNode));
        }

        obj = o;
      }
    }

    if (node.getParent() == null) {
      obj.setSourceIndex(0);
    } else {
      obj.setSourceIndex(node.getSiblingIndex());
    }

    obj.setDepth(node.getDepth());

    renderObjects.put(node, obj);
    return obj;
  }

  public void screenMoved() {
    if (renderRoot != null && renderRoot.isSpawned()) {
      renderRoot.spawnRecursive();
    }
  }

  private static boolean changed(int changes, DirtyBit bit) {
    return (changes & bit.mask) == bit.mask;
  }

  @Override
  public void styleUpdated(StyleNode styleNode, int changes) {
    if (changes == 0 || !active) {
      return;
    }

    RenderObject obj = getRenderObject(styleNode.getDomNode());
    if (obj == null) {
      return;
    }

    boolean respawn;

    if (changed(changes, DirtyBit.CONTENT)) {
      if (obj instanceof ContentRenderObject co) {
        co.setContentDirty(true);
        respawn = true;
      } else {
        respawn = false;
      }
    } else {
      respawn = changed(changes, DirtyBit.VISUAL);
    }

    if (changed(changes, DirtyBit.LAYOUT)) {
      if (obj instanceof ElementRenderObject el) {
        el.sortChildren();
      }
      triggerRealign();

      if (renderRoot != null) {
        renderRoot.spawnRecursive();
      }
    } else if (respawn) {
      NLayout.applyBasicStyle(obj.getStyle(), styleNode.getComputedSet());
      obj.spawn();
    }
  }

  public void contentChanged(DelphiNode node) {
    if (!active) {
      return;
    }

    ContentRenderObject obj = (ContentRenderObject) getRenderObject(node);
    if (obj == null) {
      return;
    }

    if (node instanceof Text text) {
      StringContent content = new StringContent(text.getTextContent());
      obj.setContent(content);
    } else if (node instanceof ChatNode chat) {
      ComponentContent content = new ComponentContent(chat.getContent());
      obj.setContent(content);
    } else if (node instanceof DelphiItemElement itemEl) {
      ItemContent content = new ItemContent(itemEl.getItemStack());
      obj.setContent(content);
    }

    obj.spawn();
    triggerRealign();
  }

  public void tooltipChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode) {
    if (!active) {
      return;
    }

    if (old != null) {
      RenderObject oldRender = getRenderObject(old);
      if (oldRender != null) {
        oldRender.killRecursive();
      }
    }

    if (element == null || !element.hasFlag(NodeFlag.HOVERED)) {
      return;
    }

    RenderObject obj = getRenderObject(titleNode);
    if (obj == null) {
      obj = initRenderTree(titleNode);
    }

    obj.moveTo(view.getCursorScreen());
    obj.spawnRecursive();
  }

  public void screenSizeChanged(float newHeight) {
    if (renderRoot != null) {
      renderRoot.moveTo(new Vector2f(0, newHeight));
    }
  }

  public void removeEntity(Display entity) {
    entities.remove(entity);
  }

  public void addEntity(Display display) {
    entities.add(display);
    display.setPersistent(false);
    view.handleEntityVisibility(display);
  }

  public DelphiElement findCursorContainingNode(Vector2f cursorScreen) {
    DelphiElement p = (DelphiElement) view.getDocument().getBody();

    if (p == null) {
      return null;
    }

    Rectangle rectangle = new Rectangle();

    outer: while (true) {
      if (p.getChildren().isEmpty()) {
        return p;
      }

      for (DelphiNode child : p.childList()) {
        if (!(child instanceof DelphiElement el)) {
          continue;
        }

        RenderObject obj = getRenderObject(el);
        if (obj == null) {
          continue;
        }

        obj.getBounds(rectangle);

        if (!rectangle.contains(cursorScreen)) {
          continue;
        }

        p = el;
        continue outer;
      }

      return p;
    }
  }

  class TooltipListener implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      final DelphiElement el = (DelphiElement) event.getTarget();
      DelphiNode tooltip = el.getTooltip();

      if (tooltip == null) {
        return;
      }

      switch (event.getType()) {
        case EventTypes.MOUSE_ENTER -> {
          RenderObject obj = getRenderObject(tooltip);
          if (obj == null) {
            obj = initRenderTree(tooltip);
          }

          el.getDocument().getStyles().updateDomStyle(tooltip);
          obj.moveTo(event.getScreenPosition());

          if (obj instanceof ElementRenderObject eObj) {
            NLayout.reflow(eObj);
          }

          obj.spawnRecursive();
        }

        case EventTypes.MOUSE_LEAVE -> {
          RenderObject obj = getRenderObject(tooltip);

          if (obj == null) {
            return;
          }

          obj.killRecursive();
        }

        case EventTypes.MOUSE_MOVE -> {
          RenderObject obj = getRenderObject(tooltip);
          if (obj == null) {
            return;
          }

          obj.moveTo(event.getScreenPosition());
        }
      }
    }
  }

  class MutationListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Element parent = event.getTarget();
      ElementRenderObject parentObj = (ElementRenderObject) getRenderObject(parent);

      if (parentObj == null || parent == null) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        RenderObject tree = initRenderTree((DelphiNode) event.getNode());
        parentObj.addChild(tree, event.getMutationIndex());

        if (parentObj.isSpawned()) {
          tree.spawnRecursive();
        }
      } else {
        RenderObject nodeObj = getRenderObject(event.getNode());
        if (nodeObj == null) {
          return;
        }

        // Remove node and kill
        parentObj.removeChild(nodeObj);
        nodeObj.killRecursive();

        // Update source indexes
        for (Node child : parent.getChildren()) {
          RenderObject childObj = getRenderObject(child);
          if (childObj == null) {
            continue;
          }
          childObj.setSourceIndex(child.getSiblingIndex());
        }
      }

      triggerUpdate();
      triggerRealign();
    }
  }
}

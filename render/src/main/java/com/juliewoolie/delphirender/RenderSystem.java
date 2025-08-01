package com.juliewoolie.delphirender;

import static com.juliewoolie.delphirender.Consts.MACRO_LAYER_DEPTH;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.DirtyBit;
import com.juliewoolie.chimera.StyleUpdateCallbacks;
import com.juliewoolie.chimera.system.StyleNode;
import com.juliewoolie.chimera.system.StyleObjectModel;
import com.juliewoolie.delphidom.ChatElement;
import com.juliewoolie.delphidom.DelphiCanvasElement;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiInputElement;
import com.juliewoolie.delphidom.DelphiItemElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Text;
import com.juliewoolie.delphirender.layout.NLayout;
import com.juliewoolie.delphirender.math.Rectangle;
import com.juliewoolie.delphirender.object.CanvasRenderObject;
import com.juliewoolie.delphirender.object.ComponentRenderObject;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.ItemRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.delphirender.object.StringRenderObject;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTarget;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.InputEvent;
import com.juliewoolie.dom.event.MouseEvent;
import com.juliewoolie.dom.event.MutationEvent;
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

  private final Map<DelphiNode, RenderObject> renderElements = new Object2ObjectOpenHashMap<>();
  private ElementRenderObject renderRoot = null;

  private boolean layoutTriggered = false;
  private boolean updateTriggered = false;

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
    InputListener inputListener = new InputListener();

    g.addEventListener(EventTypes.APPEND_CHILD, listener);
    g.addEventListener(EventTypes.REMOVE_CHILD, listener);

    g.addEventListener(EventTypes.MOUSE_ENTER, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_LEAVE, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_MOVE, tooltipListener);

    g.addEventListener(EventTypes.INPUT, inputListener);
  }

  public void close() {
    kill();

    renderElements.clear();
    renderRoot = null;
    active = false;
  }

  public void spawn() {
    if (renderRoot == null || !active) {
      return;
    }

    renderRoot.moveTo(new Vector2f(0, view.getScreen().getHeight()));
    NLayout.layout(renderRoot);

    renderRoot.killRecursive();
    renderRoot.spawnRecursive();
  }

  public void kill() {
    for (Entity entity : entities) {
      entity.remove();
    }

    entities.clear();
  }

  public void tick() {
    if (renderRoot == null || !active) {
      return;
    }

    if (layoutTriggered) {
      layoutTriggered = false;
      NLayout.layout(renderRoot);
    }

    if (updateTriggered) {
      updateTriggered = false;
      renderRoot.spawnRecursive();
    }
  }

  //
  // Layout updates and render object entity updates should only occur a
  // maximum of once per tick. This means, all changes made to the DOM tree
  // in that time are all executed and laid out in a single go.
  //

  public void triggerRealign() {
    layoutTriggered = true;
  }

  public void triggerUpdate() {
    updateTriggered = true;
  }

  public RenderObject getRenderElement(Node node) {
    return renderElements.get(node);
  }

  public void removeRenderElement(Node element) {
    RenderObject obj = renderElements.remove(element);

    if (obj == null) {
      return;
    }

    obj.kill();

    ElementRenderObject parent = obj.parent;
    if (parent == null) {
      return;
    }

    parent.removeChild(obj);
  }

  public RenderObject initRenderTree(DelphiNode node) {
    RenderObject obj;

    StyleObjectModel styles = node.getDocument().getStyles();
    StyleNode styleNode = styles.getStyleNode(node);

    if (styleNode == null) {
      styleNode = styles.createNode(node);
    }

    ComputedStyleSet styleSet = styleNode.getComputedSet();
    float depth = ((float) node.getDepth()) * MACRO_LAYER_DEPTH;

    switch (node) {
      case Text text -> {
        StringRenderObject stringObj = new StringRenderObject(this);
        stringObj.content = text.getTextContent();
        obj = stringObj;
      }
      case ChatElement chat -> {
        ElementRenderObject el = new ElementRenderObject(this, styleSet);
        ComponentRenderObject comp = new ComponentRenderObject(this);

        comp.text = chat.getContent();
        comp.depth = depth;
        el.addChild(0, comp);

        obj = el;
      }
      case DelphiItemElement item -> {
        ElementRenderObject el = new ElementRenderObject(this, styleSet);
        ItemRenderObject itemObj = new ItemRenderObject(this);

        itemObj.item = item.getItemStack();
        itemObj.depth = depth;
        el.addChild(0, itemObj);

        obj = el;
      }
      case DelphiInputElement input -> {
        ElementRenderObject el = new ElementRenderObject(this, styleSet);
        StringRenderObject sro = new StringRenderObject(this);

        sro.content = input.getDisplayText();
        sro.depth = depth + MACRO_LAYER_DEPTH;
        el.addChild(0, sro);

        obj = el;
      }
      case DelphiCanvasElement canvas -> {
        ElementRenderObject el = new ElementRenderObject(this, styleSet);
        CanvasRenderObject cro = new CanvasRenderObject(this);

        cro.canvas = canvas.canvas;
        cro.depth = depth + MACRO_LAYER_DEPTH;
        el.addChild(0, cro);

        obj = el;
      }
      default -> {
        ElementRenderObject o = new ElementRenderObject(this, styleSet);
        DelphiElement el = (DelphiElement) node;

        for (DelphiNode delphiNode : el.childList()) {
          o.addChild(o.getChildObjects().size(), initRenderTree(delphiNode));
        }

        obj = o;
      }
    }

    obj.depth = depth;
    obj.domIndex = node.getSiblingIndex();

    renderElements.put(node, obj);

    return obj;
  }

  public void screenMoved() {
    if (renderRoot != null && renderRoot.spawned) {
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

    RenderObject obj = getRenderElement(styleNode.getDomNode());
    if (obj == null) {
      return;
    }

    boolean respawn;

    if (changed(changes, DirtyBit.CONTENT)) {
      respawn = true;
    } else {
      respawn = changed(changes, DirtyBit.VISUAL);
    }

    if (changed(changes, DirtyBit.LAYOUT)) {
      if (obj instanceof ElementRenderObject el) {
        el.sortChildren();
      }

      triggerRealign();
      triggerUpdate();
    } else if (respawn) {
      if (obj instanceof ElementRenderObject er) {
        NLayout.applyBasicStyle(er.style, styleNode.getComputedSet());
      }

      triggerUpdate();
    }
  }

  public void canvasSizeChanged(DelphiCanvasElement el) {
    ElementRenderObject obj = (ElementRenderObject) getRenderElement(el);
    if (obj == null) {
      return;
    }

    triggerUpdate();
  }

  public void contentChanged(DelphiNode node) {
    if (!active) {
      return;
    }

    RenderObject obj = getRenderElement(node);

    if (obj == null) {
      return;
    }

    boolean reflow = true;

    if (node instanceof Text text) {
      StringRenderObject stringObj = (StringRenderObject) obj;
      stringObj.content = text.getTextContent();
    } else if (node instanceof ChatElement chat) {
      ElementRenderObject el = (ElementRenderObject) obj;
      ComponentRenderObject comp = el.onlyChild();
      comp.text = chat.getContent();
    } else if (node instanceof DelphiItemElement itemEl) {
      ElementRenderObject el = (ElementRenderObject) obj;
      ItemRenderObject item = el.onlyChild();
      item.item = itemEl.getItemStack();
      reflow = false;
    } else if (node instanceof DelphiCanvasElement) {
      reflow = false;
    }

    if (reflow) {
      triggerRealign();
    }

    triggerUpdate();
  }

  public void tooltipChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode) {
    if (!active) {
      return;
    }

    if (old != null) {
      RenderObject oldRender = getRenderElement(old);
      if (oldRender != null) {
        oldRender.killRecursive();
      }
    }

    if (element == null || !element.hasFlag(NodeFlag.HOVERED)) {
      return;
    }

    RenderObject obj = getRenderElement(titleNode);
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

        RenderObject obj = getRenderElement(el);
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
          RenderObject obj = getRenderElement(tooltip);
          if (obj == null) {
            obj = initRenderTree(tooltip);
          }

          el.getDocument().getStyles().updateDomStyle(tooltip);
          obj.moveTo(event.getScreenPosition());

          if (obj instanceof ElementRenderObject eObj) {
            NLayout.layout(eObj);
          }

          obj.spawnRecursive();
        }

        case EventTypes.MOUSE_LEAVE -> {
          RenderObject obj = getRenderElement(tooltip);

          if (obj == null) {
            return;
          }

          obj.killRecursive();
        }

        case EventTypes.MOUSE_MOVE -> {
          RenderObject obj = getRenderElement(tooltip);
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
      ElementRenderObject parentObj = (ElementRenderObject) getRenderElement(parent);

      if (parentObj == null || parent == null) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        RenderObject tree = initRenderTree((DelphiNode) event.getNode());
        parentObj.addChild(event.getMutationIndex(), tree);

        if (parentObj.spawned) {
          tree.spawnRecursive();
        }
      } else {
        RenderObject nodeObj = getRenderElement(event.getNode());
        if (nodeObj == null) {
          return;
        }

        // Remove node and kill
        parentObj.removeChild(nodeObj);
        nodeObj.killRecursive();

        // Update source indexes
        for (Node child : parent.getChildren()) {
          RenderObject childObj = getRenderElement(child);
          if (childObj == null) {
            continue;
          }
          childObj.domIndex = child.getSiblingIndex();
        }
      }

      triggerRealign();
      triggerUpdate();
    }
  }

  class InputListener implements EventListener.Typed<InputEvent> {

    @Override
    public void handleEvent(InputEvent event) {
      DelphiInputElement target = (DelphiInputElement) event.getTarget();
      ElementRenderObject ero = (ElementRenderObject) getRenderElement(target);

      // Null if ero was removed
      if (ero == null) {
        return;
      }

      StringRenderObject sro = ero.onlyChild();
      String ncontent = target.getDisplayText();

      if (Objects.equals(sro.content, ncontent)) {
        return;
      }

      sro.content = ncontent;

      triggerRealign();
      triggerUpdate();
    }
  }
}

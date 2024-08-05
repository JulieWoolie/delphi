package net.arcadiusmc.delphiplugin;

import static net.arcadiusmc.delphidom.Consts.MAX_SCREEN_SIZE;
import static net.arcadiusmc.delphidom.Consts.MIN_SCREEN_SIZE;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphidom.ChatNode;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiItemElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.NodeFlag;
import net.arcadiusmc.delphidom.Text;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.event.MouseEventImpl;
import net.arcadiusmc.delphidom.scss.DirtyBit;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.delphiplugin.render.ComponentContent;
import net.arcadiusmc.delphiplugin.render.ItemContent;
import net.arcadiusmc.delphiplugin.render.RenderObject;
import net.arcadiusmc.delphiplugin.render.StringContent;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Options;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import net.arcadiusmc.dom.event.MutationEvent;
import net.arcadiusmc.dom.event.ScrollDirection;
import net.kyori.adventure.sound.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PageView implements ExtendedView {

  public static boolean debugOutlines = false;

  private static final Sound CLICK_SOUND = Sound.sound()
      .type(org.bukkit.Sound.UI_BUTTON_CLICK)
      .build();

  @Getter
  private final Screen screen = new Screen();

  public final Vector2f cursorScreen = new Vector2f();
  public final Vector3f cursorWorld = new Vector3f();

  @Getter
  private final Player player;
  @Setter @Getter
  private PlayerSession session;

  @Getter @Setter
  private World world;

  @Getter
  private final ResourcePath path;

  @Getter @Setter
  private PageResources resources;

  @Getter
  private DelphiDocument document;

  private DelphiElement hoveredNode = null;
  private DelphiElement clickedNode = null;
  private MouseButton clickedButton = MouseButton.NONE;
  private int clickedNodeTicks = 0;

  @Getter
  private final Map<DelphiNode, RenderObject> renderObjects = new Object2ObjectOpenHashMap<>();
  private RenderObject renderRoot;

  private final List<Display> entities = new ArrayList<>();

  public PageView(Player player, ResourcePath path) {
    Objects.requireNonNull(player, "Null player");
    Objects.requireNonNull(path, "Null path");

    this.player = player;
    this.path = path;
    this.world = player.getWorld();
  }

  public void spawn() {
    if (renderRoot == null) {
      return;
    }

    renderRoot.getPosition().set(0, screen.getHeight());

    renderRoot.spawnRecursive();
    renderRoot.align();
  }

  public void kill() {
    for (Display entity : entities) {
      entity.remove();
    }

    entities.clear();
  }

  public void initializeDocument(DelphiDocument document) {
    this.document = document;

    if (document == null) {
      return;
    }

    document.setView(this);
    parseScreenDimensions();

    EventListenerList g = document.getGlobalTarget();
    MutationListener listener = new MutationListener();

    g.addEventListener(EventTypes.APPEND_CHILD, listener);
    g.addEventListener(EventTypes.REMOVE_CHILD, listener);

    g.addEventListener(EventTypes.MODIFY_OPTION, new ScreenDimensionListener());

    if (document.getBody() != null) {
      renderRoot = initRenderTree(document.getBody());
    }
  }

  private void parseScreenDimensions() {
    String screenWidth = document.getOption(Options.SCREEN_WIDTH);
    String screenHeight = document.getOption(Options.SCREEN_WIDTH);

    float width = parseScreenDimension(screenWidth, Screen.DEFAULT_WIDTH);
    float height = parseScreenDimension(screenHeight, Screen.DEFAULT_HEIGHT);

    screen.setDimensions(width, height);
  }

  static float parseScreenDimension(String value, float def) {
    if (Strings.isNullOrEmpty(value)) {
      return def;
    }

    return Attributes.floatAttribute(value, MIN_SCREEN_SIZE, MAX_SCREEN_SIZE).orElse(def);
  }

  private RenderObject initRenderTree(DelphiNode node) {
    RenderObject obj = new RenderObject(this, node, screen);
    obj.setDepth(node.getDepth());

    switch (node) {
      case Text text -> obj.setContent(new StringContent(text.getTextContent()));
      case ChatNode chat -> obj.setContent(new ComponentContent(chat.getContent()));
      case DelphiItemElement item -> {
        if (ItemContent.isEmpty(item.getItemStack())) {
          obj.setContent(null);
        } else {
          obj.setContent(new ItemContent(item.getItemStack()));
        }
      }
      default -> {
        DelphiElement el = (DelphiElement) node;
        for (DelphiNode delphiNode : el.childList()) {
          obj.addChild(initRenderTree(delphiNode));
        }
      }
    }

    renderObjects.put(node, obj);
    return obj;
  }

  public RenderObject getRenderObject(DelphiNode node) {
    return renderObjects.get(node);
  }

  private void triggerRealign() {
    if (renderRoot == null) {
      return;
    }

    renderRoot.align();
  }

  private void triggerUpdate() {
    if (renderRoot == null) {
      return;
    }

    renderRoot.spawnRecursive();
  }

  private static boolean changed(int changes, DirtyBit bit) {
    return (changes & bit.mask) == bit.mask;
  }

  @Override
  public void styleUpdated(DelphiNode node, int changes) {
    if (changes == 0) {
      return;
    }

    RenderObject obj = getRenderObject(node);
    if (obj == null) {
      return;
    }

    boolean respawn;

    if (changed(changes, DirtyBit.CONTENT)) {
      obj.setContentDirty(true);
      respawn = true;
    } else {
      respawn = changed(changes, DirtyBit.VISUAL);
    }

    if (respawn) {
      obj.spawn();
    }

    if (changed(changes, DirtyBit.LAYOUT)) {
      triggerRealign();
    }
  }

  @Override
  public void contentChanged(DelphiNode node) {
    RenderObject obj = getRenderObject(node);
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
      ItemStack stack = itemEl.getItemStack();

      if (ItemContent.isEmpty(stack)) {
        obj.setContent(null);
      } else {
        ItemContent content = new ItemContent(itemEl.getItemStack());
        obj.setContent(content);
      }
    }

    obj.spawn();
    triggerRealign();
  }

  @Override
  public Vector2f getCursorScreenPosition() {
    return new Vector2f(cursorScreen);
  }

  @Override
  public Vector3f getCursorWorldPosition() {
    return new Vector3f(cursorWorld);
  }

  @Override
  public Map<String, Object> getStyleVariables() {
    if (resources == null) {
      return new HashMap<>();
    }

    return resources.getStyleVariables();
  }

  @Override
  public void killElement(DelphiElement element) {
    RenderObject obj = getRenderObject(element);
    if (obj == null) {
      return;
    }

    obj.kill();
  }

  @Override
  public void close() {
    if (session != null) {
      session.closeView(this);
    } else {
      onClose();
    }
  }

  public void onClose() {
    if (document != null) {
      EventImpl event = new EventImpl(EventTypes.DOM_CLOSING, document);
      event.initEvent(null, false, false);
      document.dispatchEvent(event);
    }

    kill();
    setSession(null);
  }

  public void removeEntity(Display entity) {
    entities.remove(entity);
  }

  public void addEntity(Display display) {
    entities.add(display);
    display.setPersistent(false);
  }

  public void tick() {
    drawSelected();

    if (clickedNodeTicks <= 0) {
      return;
    }

    clickedNodeTicks--;

    if (clickedNodeTicks > 0) {
      return;
    }

    unselectClickedNode();
  }

  private void drawSelected() {
    if (hoveredNode == null || !debugOutlines) {
      return;
    }

    RenderObject obj = getRenderObject(hoveredNode);
    if (obj == null) {
      return;
    }

    Rectangle rectangle = new Rectangle();
    obj.getBounds(rectangle);

    Debug.drawSelectionOutline(rectangle, this);
  }

  /* --------------------------- Selection and input ---------------------------- */

  public void onInteract(MouseButton button, boolean shift) {
    triggerClickEvent(button, shift);
  }

  public void cursorMoveTo(Vector2f screenPos, Vector3f targetPos) {
    if (screenPos.equals(this.cursorScreen)) {
      return;
    }

    cursorScreen.set(screenPos);
    cursorWorld.set(targetPos);

    updateSelectedNode();
  }

  public void onUnselect() {
    cursorScreen.set(-1);
    cursorWorld.set(-1);
    unselectHovered();
  }

  public void onSelect(Vector2f screenPos, Vector3f targetPos) {
    cursorMoveTo(screenPos, targetPos);
  }

  private MouseEventImpl fireMouseEvent(
      String type,
      boolean shift,
      MouseButton button,
      DelphiElement target,
      boolean bubbles,
      boolean cancellable
  ) {
    MouseEventImpl event = new MouseEventImpl(type, document);
    event.initEvent(
        target,
        bubbles,
        cancellable,
        shift,
        button,
        ScrollDirection.NONE,
        cursorScreen,
        cursorWorld
    );

    target.dispatchEvent(event);
    return event;
  }

  private void triggerClickEvent(MouseButton button, boolean shift) {
    if (hoveredNode == null) {
      return;
    }

    if (clickedNode != null && !Objects.equals(clickedNode, hoveredNode)) {
      unselectClickedNode();
    }

    this.clickedButton = button;
    this.clickedNodeTicks = Document.ACTIVE_TICKS;
    this.clickedNode = hoveredNode;

    document.clicked = this.clickedNode;

    hoveredNode.addFlag(NodeFlag.CLICKED);

    MouseEvent event = fireMouseEvent(
        EventTypes.MOUSE_DOWN,
        shift,
        button,
        clickedNode,
        true,
        true
    );

    if (event.isCancelled()) {
      return;
    }

    if (clickedNode.getTagName().equals(TagNames.BUTTON)) {
      player.playSound(CLICK_SOUND);
    }
  }

  private void unselectClickedNode() {
    if (clickedNode == null) {
      return;
    }

    clickedNode.removeFlag(NodeFlag.CLICKED);

    fireMouseEvent(EventTypes.CLICK_EXPIRE, false, clickedButton, clickedNode, false, false);

    clickedNode = null;
    clickedNodeTicks = 0;
    clickedButton = MouseButton.NONE;

    document.clicked = null;
  }

  private void unselectHovered() {
    if (this.hoveredNode == null) {
      return;
    }

    this.hoveredNode.removeFlag(NodeFlag.HOVERED);
    fireMouseEvent(EventTypes.MOUSE_LEAVE, false, MouseButton.NONE, this.hoveredNode, false, false);
    this.hoveredNode = null;
    document.hovered = null;
  }

  private void updateSelectedNode() {
    DelphiElement contained = findContainingNode();

    if (contained == null) {
      if (this.hoveredNode == null) {
        return;
      }

      unselectHovered();
      return;
    }

    if (Objects.equals(contained, hoveredNode)) {
      fireMouseEvent(EventTypes.MOUSE_MOVE, false, MouseButton.NONE, this.hoveredNode, false, false);
      return;
    }

    unselectHovered();

    this.hoveredNode = contained;
    document.hovered = hoveredNode;
    contained.addFlag(NodeFlag.HOVERED);

    fireMouseEvent(EventTypes.MOUSE_ENTER, false, MouseButton.NONE, contained, false, false);
  }

  private DelphiElement findContainingNode() {
    DelphiElement p = document.getBody();

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
  /* --------------------------- sub classes ---------------------------- */

  class MutationListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      RenderObject parentObj = getRenderObject((DelphiNode) event.getTarget());
      if (parentObj == null) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        RenderObject tree = initRenderTree((DelphiNode) event.getNode());
        parentObj.addChild(tree, event.getMutationIndex());
      } else {
        RenderObject removed = parentObj.removeChild((DelphiNode) event.getNode());

        if (removed != null) {
          removed.killRecursive();
        }
      }

      triggerUpdate();
      triggerRealign();
    }
  }

  class ScreenDimensionListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      String key = event.getKey();

      if (!key.equals(Options.SCREEN_HEIGHT) && !key.equals(Options.SCREEN_WIDTH)) {
        return;
      }

      boolean isWidth = key.equals(Options.SCREEN_WIDTH);
      float defaultValue = isWidth ? Screen.DEFAULT_WIDTH : Screen.DEFAULT_HEIGHT;
      float newDimension;

      if (event.getAction() == AttributeAction.REMOVE) {
        newDimension = defaultValue;
      } else {
        newDimension = parseScreenDimension(event.getNewValue(), defaultValue);
      }

      float w;
      float h;

      if (isWidth) {
        w = newDimension;
        h = screen.getHeight();
      } else {
        w = screen.getWidth();
        h = newDimension;
      }

      screen.setDimensions(w, h);
    }
  }
}

package net.arcadiusmc.delphiplugin;

import static net.arcadiusmc.delphidom.Consts.MAX_SCREEN_SIZE;
import static net.arcadiusmc.delphidom.Consts.MIN_SCREEN_SIZE;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.DirtyBit;
import net.arcadiusmc.chimera.StyleUpdateCallbacks;
import net.arcadiusmc.chimera.system.StyleNode;
import net.arcadiusmc.chimera.system.StyleObjectModel;
import net.arcadiusmc.delphi.event.DocumentCloseEvent;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphidom.ChatNode;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiItemElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.Text;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.event.MouseEventImpl;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.delphiplugin.render.ComponentContent;
import net.arcadiusmc.delphiplugin.render.ContentRenderObject;
import net.arcadiusmc.delphiplugin.render.ElementRenderObject;
import net.arcadiusmc.delphiplugin.render.ItemContent;
import net.arcadiusmc.delphiplugin.render.LayoutKt;
import net.arcadiusmc.delphiplugin.render.RenderObject;
import net.arcadiusmc.delphiplugin.render.StringContent;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.NodeFlag;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class PageView implements ExtendedView, StyleUpdateCallbacks {

  private static final Logger LOGGER = Loggers.getLogger("DocumentView");

  private static final Sound CLICK_SOUND = Sound.sound()
      .type(org.bukkit.Sound.UI_BUTTON_CLICK)
      .build();

  @Getter
  private final Screen screen = new Screen();

  private final DelphiPlugin plugin;

  public final Vector2f cursorScreen = new Vector2f();
  public final Vector3f cursorWorld = new Vector3f();

  @Getter
  private final Player player;
  @Setter @Getter
  private PlayerSession session;

  @Getter
  private boolean selected = false;

  @Getter
  private boolean closed = false;

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

  @Getter
  private ElementRenderObject renderRoot;

  private final List<Display> entities = new ArrayList<>();
  private Interaction interaction;

  public PageView(DelphiPlugin plugin, Player player, ResourcePath path) {
    Objects.requireNonNull(player, "Null player");
    Objects.requireNonNull(path, "Null path");
    Objects.requireNonNull(plugin, "Null plugin");

    this.player = player;
    this.path = path;
    this.world = player.getWorld();
    this.plugin = plugin;
  }

  public void spawn() {
    if (renderRoot == null) {
      return;
    }

    renderRoot.moveTo(new Vector2f(0, screen.getHeight()));
    LayoutKt.layout(renderRoot);

    renderRoot.killRecursive();
    renderRoot.spawnRecursive();

    closed = false;

    spawnScreenInteraction();
  }

  private void spawnScreenInteraction() {
    killScreenInteraction();

    Location loc = getSpawnInteractionLocation();

    interaction = world.spawn(loc, Interaction.class);
    interaction.setPersistent(false);
    handleEntityVisibility(interaction);

    configureInteractionSize();
  }

  private void configureInteractionSize() {
    if (interaction == null) {
      return;
    }

    float height = screen.boundingBoxSize.y;
    float width = Math.max(screen.boundingBoxSize.x, screen.boundingBoxSize.z);

    interaction.setInteractionWidth(width);
    interaction.setInteractionHeight(height);
  }

  private Location getSpawnInteractionLocation() {
    Vector3f center = screen.center();
    return new Location(world, center.x, screen.boundingBoxMin.y, center.z);
  }

  private void killScreenInteraction() {
    if (interaction == null || interaction.isDead()) {
      return;
    }

    interaction.remove();
    interaction = null;
  }

  public void kill() {
    for (Display entity : entities) {
      entity.remove();
    }

    entities.clear();
    killScreenInteraction();
  }

  @Override
  public void transform(@NotNull Transformation transformation) {
    Objects.requireNonNull(transformation, "Null transformation");

    kill();
    screen.apply(transformation);
    spawn();
  }

  @Override
  public void moveTo(@NotNull Vector3f position) {
    Objects.requireNonNull(position, "Null position");
    moveTo(world, position.x, position.y, position.z, null);
  }

  @Override
  public void moveTo(@NotNull Location location) {
    Objects.requireNonNull(location, "Null location");
    Objects.requireNonNull(location.getWorld(), "Null location world");

    moveTo(
        location.getWorld(),
        (float) location.getX(),
        (float) location.getY(),
        (float) location.getZ(),
        location
    );
  }

  @Override
  public void moveTo(@NotNull Location location, boolean changeRotation) {
    Objects.requireNonNull(location, "Null location");
    Objects.requireNonNull(location.getWorld(), "Null location world");

    moveTo(
        location.getWorld(),
        (float) location.getX(),
        (float) location.getY(),
        (float) location.getZ(),
        changeRotation ? location : null
    );
  }

  @Override
  public void moveTo(@NotNull World world, @NotNull Vector3f position) {
    Objects.requireNonNull(world, "Null world");
    Objects.requireNonNull(position, "Null position");

    moveTo(world, position.x, position.y, position.z, null);
  }

  private void moveTo(World world, float x, float y, float z, Location l) {
    float h = screen.getHeight() * 0.5f;
    Vector3f off = new Vector3f(x, y + h, z);
    off.sub(screen.center());

    if (off.lengthSquared() <= 0) {
      return;
    }

    screen.translate(off);

    if (l != null) {
      Quaternionf quaternion = new Quaternionf();

      Vector direction = l.getDirection();
      Vector3f dir = new Vector3f(
          (float) direction.getX(),
          (float) direction.getY(),
          (float) direction.getZ()
      );

      Screen.lookInDirection(quaternion, dir);

      screen.leftRotation.set(quaternion);

      screen.recalculate();
    }

    if (!Objects.equals(world, this.world)) {
      setWorld(world);
    }

    renderRoot.spawnRecursive();

    if (interaction != null && !interaction.isDead()) {
      Location loc = getSpawnInteractionLocation();
      interaction.teleport(loc);
      configureInteractionSize();
    }
  }

  public void initializeDocument(DelphiDocument document) {
    this.document = document;

    if (document == null) {
      return;
    }

    document.setView(this);
    parseScreenDimensions();
    configureScreen();

    EventListenerList g = document.getGlobalTarget();
    MutationListener listener = new MutationListener();
    TooltipListener tooltipListener = new TooltipListener();

    g.addEventListener(EventTypes.APPEND_CHILD, listener);
    g.addEventListener(EventTypes.REMOVE_CHILD, listener);

    g.addEventListener(EventTypes.MODIFY_OPTION, new ScreenDimensionListener());

    g.addEventListener(EventTypes.MOUSE_ENTER, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_LEAVE, tooltipListener);
    g.addEventListener(EventTypes.MOUSE_MOVE, tooltipListener);

    g.addEventListener(EventTypes.CLICK, new ButtonClickListener());

    if (document.getBody() != null) {
      renderRoot = (ElementRenderObject) initRenderTree(document.getBody());
    }

    document.getStyles().setUpdateCallbacks(this);
  }

  private void configureScreen() {
    Location location = player.getEyeLocation();
    Vector direction = location.getDirection();

    Vector3f pos = new Vector3f();
    Vector3f dir = new Vector3f();

    pos.x = (float) location.getX();
    pos.y = (float) location.getY();
    pos.z = (float) location.getZ();

    dir.x = (float) direction.getX();
    dir.y = (float) direction.getY();
    dir.z = (float) direction.getZ();

    boolean ignoreYDir = Attributes.boolAttribute(
        document.getOption(Options.IGNORE_PLAYER_PITCH),
        true
    );

    if (ignoreYDir) {
      dir.y = 0;
      dir.normalize();
    }

    final float width = screen.getWidth();
    final float height = screen.getHeight();
    final float distanceFromPlayer = width * 0.5f;

    pos.x += (dir.x * distanceFromPlayer);
    pos.y += (dir.y * distanceFromPlayer);
    pos.z += (dir.z * distanceFromPlayer);

    Quaternionf lrot = new Quaternionf();
    Screen.lookInDirection(lrot, dir);

    screen.set(pos, width, height);
    screen.leftRotation.set(lrot);
    screen.recalculate();
  }

  private void parseScreenDimensions() {
    String screenWidth = document.getOption(Options.SCREEN_WIDTH);
    String screenHeight = document.getOption(Options.SCREEN_HEIGHT);

    float width = parseScreenDimension(screenWidth, Screen.DEFAULT_WIDTH);
    float height = parseScreenDimension(screenHeight, Screen.DEFAULT_HEIGHT);

    screen.setDimensions(width, height);
  }

  static float parseScreenDimension(String value, float def) {
    if (Strings.isNullOrEmpty(value)) {
      return def;
    }
    if ("default".equalsIgnoreCase(value)) {
      return def;
    }

    return Attributes.floatAttribute(value, MIN_SCREEN_SIZE, MAX_SCREEN_SIZE).orElse(def);
  }

  private RenderObject initRenderTree(DelphiNode node) {
    RenderObject obj;

    StyleObjectModel styles = node.getDocument().getStyles();
    StyleNode styleNode = styles.getStyleNode(node);

    if (styleNode == null) {
      styleNode = styles.createNode(node);
    }

    ComputedStyleSet styleSet = styleNode.getComputedSet();

    switch (node) {
      case Text text -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet, screen);
        o.setContent(new StringContent(text.getTextContent()));
        obj = o;
      }
      case ChatNode chat -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet, screen);
        o.setContent(new ComponentContent(chat.getContent()));
        obj = o;
      }
      case DelphiItemElement item -> {
        ContentRenderObject o = new ContentRenderObject(this, styleSet, screen);
        o.setContent(new ItemContent(item.getItemStack()));
        obj = o;
      }
      default -> {
        ElementRenderObject o = new ElementRenderObject(this, styleSet, screen);
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

  public RenderObject getRenderObject(Node node) {
    return renderObjects.get(node);
  }

  private void triggerRealign() {
    if (renderRoot == null || closed) {
      return;
    }

    LayoutKt.layout(renderRoot);
  }

  private void triggerUpdate() {
    if (renderRoot == null || closed) {
      return;
    }

    renderRoot.spawnRecursive();
  }

  private static boolean changed(int changes, DirtyBit bit) {
    return (changes & bit.mask) == bit.mask;
  }

  @Override
  public void styleUpdated(StyleNode styleNode, int changes) {
    if (changes == 0 || closed) {
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
      LayoutKt.applyStandardProperties(obj.getStyle(), styleNode.getComputedSet());
      obj.spawn();
    }
  }

  @Override
  public void contentChanged(DelphiNode node) {
    if (closed) {
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

  @Override
  public Vector2f getCursorScreenPosition() {
    if (!selected) {
      return null;
    }

    return new Vector2f(cursorScreen);
  }

  @Override
  public Vector3f getCursorWorldPosition() {
    if (!selected) {
      return null;
    }
    return new Vector3f(cursorWorld);
  }

  @Override
  public void removeRenderElement(DelphiElement element) {
    RenderObject obj = renderObjects.remove(element);

    if (obj == null) {
      return;
    }

    obj.kill();
  }

  @Override
  public void titleChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode) {
    if (closed) {
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

    obj.moveTo(cursorScreen);
    obj.spawnRecursive();
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

      DocumentCloseEvent bukkitEvent = new DocumentCloseEvent(player, this);
      bukkitEvent.callEvent();
    }

    kill();
    setSession(null);

    renderObjects.clear();
    renderRoot = null;

    closed = true;
  }

  public void removeEntity(Display entity) {
    entities.remove(entity);
  }

  public void addEntity(Display display) {
    entities.add(display);
    display.setPersistent(false);
    handleEntityVisibility(display);
  }

  private void handleEntityVisibility(Entity entity) {
    entity.setVisibleByDefault(false);
    player.showEntity(plugin, entity);
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
    if (!Debug.debugOutlines) {
      return;
    }

    Debug.drawScreen(screen, world);

    if (hoveredNode == null) {
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
    selected = true;
  }

  public void cursorMoveTo(Vector2f screenPos, Vector3f targetPos) {
    selected = true;

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
    selected = false;
    unselectHovered();
  }

  public void onSelect(Vector2f screenPos, Vector3f targetPos) {
    cursorMoveTo(screenPos, targetPos);
    selected = true;
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
        EventTypes.CLICK,
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

    propagateHoverState(false, hoveredNode);
    fireMouseEvent(EventTypes.MOUSE_LEAVE, false, MouseButton.NONE, this.hoveredNode, true, false);
    this.hoveredNode = null;
    document.hovered = null;
  }

  private void propagateHoverState(boolean state, DelphiNode node) {
    DelphiNode p = node;

    while (p != null) {
      if (state) {
        p.addFlag(NodeFlag.HOVERED);
      } else {
        p.removeFlag(NodeFlag.HOVERED);
      }

      DelphiElement parent = p.getParent();

      if (parent == null) {
        document.getStyles().updateDomStyle(p);
      }

      p = parent;
    }
  }

  private void updateSelectedNode() {
    DelphiElement contained = findCursorContainingNode();

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
    propagateHoverState(true, contained);

    fireMouseEvent(EventTypes.MOUSE_ENTER, false, MouseButton.NONE, contained, true, false);
  }

  private DelphiElement findCursorContainingNode() {
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

  class ButtonClickListener implements EventListener.Typed<MouseEvent> {

    static final String CLOSE = "close";
    static final String CMD = "cmd:";
    static final String PLAYER_CMD = "player-cmd:";

    @Override
    public void handleEvent(MouseEvent event) {
      Element target = event.getTarget();
      if (!target.getTagName().equals(TagNames.BUTTON)) {
        return;
      }

      String action = target.getAttribute(Attributes.BUTTON_ACTION);
      if (Strings.isNullOrEmpty(action)) {
        return;
      }

      if (action.equalsIgnoreCase(CLOSE)) {
        event.stopPropagation();
        event.preventDefault();

        close();

        return;
      }

      if (action.startsWith(CMD)) {
        runCommand(Bukkit.getConsoleSender(), CMD, action);
        return;
      }
      if (action.startsWith(PLAYER_CMD)) {
        runCommand(player, PLAYER_CMD, action);
      }
    }

    private void runCommand(CommandSender sender, String prefix, String cmd) {
      String formatted = cmd.substring(prefix.length()).trim()
          .replace("%player%", player.getName());

      Bukkit.dispatchCommand(sender, formatted);
    }
  }

  class TooltipListener implements EventListener.Typed<MouseEvent> {

    @Override
    public void handleEvent(MouseEvent event) {
      final Element el = event.getTarget();

      DelphiNode tooltip = (DelphiNode) el.getTooltip();

      if (tooltip == null) {
        return;
      }

      switch (event.getType()) {
        case EventTypes.MOUSE_ENTER -> {
          RenderObject obj = getRenderObject(tooltip);
          if (obj == null) {
            obj = initRenderTree(tooltip);
          }

          document.getStyles().updateDomStyle(tooltip);

          obj.moveTo(event.getScreenPosition());

          if (obj instanceof ElementRenderObject eObj) {
            LayoutKt.layout(eObj);
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
      ElementRenderObject parentObj
          = (ElementRenderObject) getRenderObject((DelphiNode) event.getTarget());

      if (parentObj == null) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        RenderObject tree = initRenderTree((DelphiNode) event.getNode());
        parentObj.addChild(tree, event.getMutationIndex());
      } else {
        RenderObject nodeObj = getRenderObject((DelphiNode) event.getNode());
        RenderObject removed = parentObj.removeChild(nodeObj);

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

      if (renderRoot != null) {
        renderRoot.moveTo(new Vector2f(0, h));
      }
    }
  }
}

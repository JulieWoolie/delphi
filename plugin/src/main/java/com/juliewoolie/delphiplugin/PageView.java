package com.juliewoolie.delphiplugin;

import static com.juliewoolie.delphi.Screen.MAX_SCREEN_SIZE;
import static com.juliewoolie.delphi.Screen.MIN_SCREEN_SIZE;

import com.google.common.base.Strings;
import com.juliewoolie.delphi.PlayerSet;
import com.juliewoolie.delphi.event.DocumentCloseEvent;
import com.juliewoolie.delphi.event.DocumentEvent;
import com.juliewoolie.delphi.event.DocumentViewMoveEvent;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphidom.DelphiCanvasElement;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiDocumentElement;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.delphidom.event.EventImpl;
import com.juliewoolie.delphidom.event.EventListenerList;
import com.juliewoolie.delphiplugin.event.PlayerSetEventImpl;
import com.juliewoolie.delphiplugin.math.Screen;
import com.juliewoolie.delphiplugin.resource.FontMetrics;
import com.juliewoolie.delphiplugin.resource.PageResources;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.delphirender.math.Rectangle;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Options;
import com.juliewoolie.dom.RenderBounds;
import com.juliewoolie.dom.event.AttributeAction;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.nlayout.LayoutBox;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class PageView implements ExtendedView {

  public static final int MAX_NO_PLAYER_TICKS = 1;

  @Getter
  private final Screen screen = new Screen();

  private final DelphiPlugin plugin;

  @Getter
  private final String instanceName;

  @Getter @Setter
  private FontMetrics fontMetrics;

  @Getter
  public final Vector2f cursorScreen = new Vector2f();
  @Getter
  public final Vector3d cursorWorld = new Vector3d();

  @Getter
  private final PlayerSet players;

  @Getter
  private Player selectedPlayer = null;

  @Getter @Setter
  private ViewState state = ViewState.UNLOADED;

  @Getter @Setter
  private World world;

  @Getter
  private final ResourcePath path;

  @Getter @Setter
  private PageResources resources;

  @Getter
  private DelphiDocument document;

  private int noPlayerTicks = 0;

  @Getter
  final RenderSystem renderer;

  @Getter
  final PageInputSystem input;

  final ViewScheduler scheduler;

  private Interaction interaction;

  public PageView(
      DelphiPlugin plugin,
      String instanceName,
      World world,
      PlayerSet players,
      ResourcePath path
  ) {
    Objects.requireNonNull(players, "Null player");
    Objects.requireNonNull(world, "Null world");
    Objects.requireNonNull(path, "Null path");
    Objects.requireNonNull(plugin, "Null plugin");
    Objects.requireNonNull(instanceName, "Null instance name");

    this.players = players;
    this.instanceName = instanceName;
    this.path = path;
    this.world = world;
    this.plugin = plugin;

    this.renderer = new RenderSystem(this, screen);
    this.renderer.setWorld(world);
    this.renderer.setFontMetrics(fontMetrics);

    this.input = new PageInputSystem(this);

    this.scheduler = new ViewScheduler();
  }

  public void spawn() {
    renderer.spawn();
    state = ViewState.SPAWNED;
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

    double height = screen.boundingBoxSize.y;
    double width = Math.max(screen.boundingBoxSize.x, screen.boundingBoxSize.z);

    interaction.setInteractionWidth((float) width);
    interaction.setInteractionHeight((float) height);
  }

  private Location getSpawnInteractionLocation() {
    Vector3d center = screen.center();
    return new Location(world, center.x, screen.boundingBoxMin.y, center.z);
  }

  private void killScreenInteraction() {
    if (interaction == null || interaction.isDead()) {
      return;
    }

    interaction.remove();
    interaction = null;
  }

  public void killIfSpawned() {
    if (state != ViewState.SPAWNED) {
      return;
    }

    kill();
  }

  public void spawnIfSpawned() {
    if (state != ViewState.SPAWNED) {
      return;
    }

    spawn();
  }

  public void kill() {
    renderer.kill();
    killScreenInteraction();
  }

  @Override
  public boolean isClosed() {
    return state == ViewState.CLOSED;
  }

  @Override
  public void transform(@NotNull Transformation transformation) {
    Objects.requireNonNull(transformation, "Null transformation");

    if (state == ViewState.CLOSED) {
      return;
    }

    killIfSpawned();
    screen.apply(transformation);
    spawnIfSpawned();

    if (transformation.getTranslation().lengthSquared() != 0) {
      callMoveEvents();
    }
  }

  @Override
  public void setScreenTransform(@NotNull Transformation transformation) {
    if (state == ViewState.CLOSED) {
      return;
    }

    killIfSpawned();

    boolean callMoveEvent = !screen.center.equals(transformation.getTranslation());

    screen.center.set(transformation.getTranslation());
    screen.scale.set(transformation.getScale());
    screen.leftRotation.set(transformation.getLeftRotation());
    screen.rightRotation.set(transformation.getRightRotation());
    screen.recalculate();

    spawnIfSpawned();

    if (callMoveEvent) {
      callMoveEvents();
    }
  }

  @Override
  public @NotNull Transformation getScreenTransform() {
    return new Transformation(
        new Vector3f(),
        new Quaternionf(screen.leftRotation),
        new Vector3f(screen.scale),
        new Quaternionf(screen.rightRotation)
    );
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
        location.getX(),
        location.getY(),
        location.getZ(),
        location
    );
  }

  @Override
  public void moveTo(@NotNull Location location, boolean changeRotation) {
    Objects.requireNonNull(location, "Null location");
    Objects.requireNonNull(location.getWorld(), "Null location world");

    moveTo(
        location.getWorld(),
        location.getX(),
        location.getY(),
        location.getZ(),
        changeRotation ? location : null
    );
  }

  @Override
  public void moveTo(@NotNull World world, @NotNull Vector3f position) {
    Objects.requireNonNull(world, "Null world");
    Objects.requireNonNull(position, "Null position");

    moveTo(world, position.x, position.y, position.z, null);
  }

  private void moveTo(World world, double x, double y, double z, Location l) {
    if (state == ViewState.CLOSED) {
      return;
    }

    double h = screen.getHeight() * 0.5f;
    Vector3d off = new Vector3d(x, y + h, z);
    off.sub(screen.center());

    if (off.lengthSquared() <= 0) {
      return;
    }

    screen.translate(off);

    if (l != null) {
      Quaternionf quaternion = new Quaternionf();

      Vector direction = l.getDirection();
      Vector3f dir = new Vector3f(
          (float) -direction.getX(),
          (float) -direction.getY(),
          (float) -direction.getZ()
      );

      Screen.lookInDirection(quaternion, dir);

      screen.leftRotation.set(quaternion);

      screen.recalculate();
    }

    if (!Objects.equals(world, this.world)) {
      setWorld(world);
      renderer.setWorld(world);
    }

    renderer.screenMoved();

    if (interaction != null && !interaction.isDead()) {
      Location loc = getSpawnInteractionLocation();
      interaction.teleport(loc);
      configureInteractionSize();
    }

    callMoveEvents();
  }

  private void callMoveEvents() {
    if (state != ViewState.SPAWNED) {
      return;
    }

    // Call Bukkit Event
    Vector3d vec = new Vector3d(screen.center.x, screen.boundingBoxMin.y, screen.center.z);
    DocumentViewMoveEvent moveEvent = new DocumentViewMoveEvent(this, this.world, vec);
    moveEvent.callEvent();

    // Call DOM event
    DelphiDocumentElement element = document.getDocumentElement();
    if (element != null) {
      EventImpl domEvent = new EventImpl(EventTypes.VIEW_MOVED, document);
      domEvent.initEvent(element, false, false);
      element.dispatchEvent(domEvent);
    }
  }

  public void initializeDocument(DelphiDocument document) {
    this.document = document;

    if (document == null) {
      return;
    }

    document.setView(this);
    parseScreenDimensions();

    EventListenerList g = document.getGlobalTarget();
    g.addEventListener(EventTypes.MODIFY_OPTION, new ScreenDimensionListener());
    g.addEventListener(EventTypes.CLICK, new ButtonClickListener());

    g.addEventListener(EventTypes.CLICK, new InputConversationListener());
    g.addEventListener(EventTypes.CLICK, new FieldSetDialogListener());

    renderer.init();

    document.getStyles().setUpdateCallbacks(renderer);
    state = ViewState.LOADED;

    g.setPostRunListener(event -> {
      DocumentEvent bukkitEvent = new DocumentEvent(event);
      bukkitEvent.callEvent();
    });
  }

  public void configureScreen() {
    if (players.size() != 1) {
      throw new IllegalStateException(
          "Cannot automatically position the screen with more than 1 player"
      );
    }

    Player player = players.iterator().next();
    Location location = player.getEyeLocation();
    Vector direction = location.getDirection();

    Vector3d pos = new Vector3d();
    Vector3f dir = new Vector3f();

    pos.x = location.getX();
    pos.y = location.getY();
    pos.z = location.getZ();

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

    final float width = (float) screen.getWidth();
    final float height = (float) screen.getHeight();
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

  @Override
  public Vector2f getCursorScreenPosition() {
    if (selectedPlayer == null) {
      return null;
    }

    return new Vector2f(cursorScreen);
  }

  @Override
  public Vector3f getCursorWorldPosition() {
    if (selectedPlayer == null) {
      return null;
    }
    return new Vector3f(cursorWorld);
  }

  @Override
  public void removeRenderElement(DelphiElement element) {
    renderer.removeRenderElement(element);
  }

  @Override
  public void tooltipChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode) {
    renderer.tooltipChanged(element, old, titleNode);
  }


  @Override
  public void canvasSizeChanged(DelphiCanvasElement element) {
    renderer.canvasSizeChanged(element);
  }

  @Override
  public void contentChanged(DelphiNode node) {
    renderer.contentChanged(node);
  }

  @Override
  public void close() {
    if (state == ViewState.CLOSED || state == ViewState.CLOSING) {
      return;
    }

    onClose();
    plugin.getViewManager().removeView(this);
  }

  public void onClose() {
    state = ViewState.CLOSING;
    scheduler.stopped = true;

    if (document != null) {
      EventListenerList g = document.getGlobalTarget();
      g.setPostRunListener(null);

      EventImpl event = new EventImpl(EventTypes.DOM_CLOSING, document);
      event.initEvent(null, false, false);
      document.dispatchEvent(event);

      DocumentCloseEvent bukkitEvent = new DocumentCloseEvent(this);
      bukkitEvent.callEvent();

      document.shutdownSystems();
    }

    killScreenInteraction();
    renderer.close();

    state = ViewState.CLOSED;
  }

  public void handleEntityVisibility(Entity entity) {
    if (players.isServerPlayerSet()) {
      return;
    }

    entity.setVisibleByDefault(false);

    for (Player player : players) {
      player.showEntity(plugin, entity);
    }
  }

  public void tick() {
    if (!players.isServerPlayerSet()) {
      if (players.isEmpty()) {
        noPlayerTicks++;

        if (noPlayerTicks > MAX_NO_PLAYER_TICKS) {
          close();
          return;
        }
      } else {
        noPlayerTicks = 0;
      }
    }

    drawSelected();

    input.tick();
    renderer.tick();
    scheduler.tick();
  }

  @Override
  public RenderBounds renderBounds(DelphiNode delphiNode) {
    RenderObject obj = renderer.getRenderElement(delphiNode);
    if (obj == null) {
      return null;
    }

    return new RenderBoundsImpl(obj.position, obj.size);
  }

  @Override
  public RenderBounds innerRenderBounds(DelphiElement delphiElement) {
    RenderObject ro = renderer.getRenderElement(delphiElement);
    if (!(ro instanceof ElementRenderObject ero)) {
      return null;
    }

    FullStyle style = ero.style;

    Vector2f pos = new Vector2f(ero.position);
    Vector2f size = new Vector2f(ero.size);

    LayoutBox.subtractExtraSpace(size, style);
    LayoutBox.getContentStart(pos, ero.position, style);

    return new RenderBoundsImpl(pos, size);
  }

  @Override
  public boolean isSelected() {
    return selectedPlayer != null;
  }

  @Override
  public int runLater(long tickDelay, @NotNull Runnable task) throws IllegalArgumentException {
    return scheduler.scheduleLater(tickDelay, task);
  }

  @Override
  public int runRepeating(long tickDelay, long tickInterval, @NotNull Runnable task)
      throws NullPointerException
  {
    return scheduler.scheduleRepeating(tickDelay, tickInterval, task);
  }

  @Override
  public boolean cancelTask(int taskId) {
    return scheduler.cancelTask(taskId);
  }

  private void drawSelected() {
    if (!Debug.debugOutlines) {
      return;
    }

    Debug.drawScreen(screen, world);

    if (input.hoveredNode == null) {
      return;
    }

    RenderObject obj = renderer.getRenderElement(input.hoveredNode);
    if (obj == null) {
      return;
    }

    ElementRenderObject root = renderer.getRenderRoot();
    Rectangle rectangle = new Rectangle();

    drawDebug(rectangle, root, obj);
  }

  private void drawDebug(Rectangle rectangle, RenderObject obj, RenderObject hovered) {
    obj.getBounds(rectangle);

    Color elColor = obj == hovered ? Color.RED : Color.GRAY;
    Color marginColor = obj == hovered ? Color.FUCHSIA : Color.OLIVE;

    Debug.drawOutline(rectangle, this, elColor);

    Rect margin;
    if (obj instanceof ElementRenderObject el) {
      margin = el.style.margin;
    } else {
      margin = new Rect(0f);
    }

    boolean hasMargin = margin.left > 0 || margin.right > 0 || margin.top > 0 || margin.bottom > 0;

    if (hasMargin) {
      rectangle.position.x -= margin.left;
      rectangle.position.y += margin.top;
      rectangle.size.x += margin.left + margin.right;
      rectangle.size.y += margin.bottom + margin.top;

      Debug.drawOutline(rectangle, this, marginColor);
    }

    if (obj instanceof ElementRenderObject el) {
      for (RenderObject child : el.getChildObjects()) {
        drawDebug(rectangle, child, hovered);
      }
    }
  }

  /* --------------------------- Selection and input ---------------------------- */

  public void onInteract(Player player, MouseButton button, boolean shift) {
    selectedPlayer = player;
    input.triggerClickEvent(player, button, shift);
  }

  public void cursorMoveTo(Player player, Vector2f screenPos, Vector3d targetPos) {
    selectedPlayer = player;

    if (screenPos.equals(this.cursorScreen)) {
      return;
    }

    cursorScreen.set(screenPos);
    cursorWorld.set(targetPos);

    input.updateSelectedNode(player);
  }

  public void onUnselect() {
    cursorScreen.set(-1);
    cursorWorld.set(-1);
    selectedPlayer = null;
    input.unselectHovered();
  }

  public void onSelect(Player player, Vector2f screenPos, Vector3d targetPos) {
    cursorMoveTo(player, screenPos, targetPos);
  }

  void onPlayerRemoved(Player player) {
    for (Entity entity : renderer.getEntities()) {
      player.hideEntity(plugin, entity);
    }
    if (interaction != null) {
      player.hideEntity(plugin, interaction);
    }

    plugin.getViewManager().playerRemoved(this, player);
    firePlayerChangeEvent(EventTypes.PLAYER_REMOVED, player);
  }

  void onPlayerAdded(Player player) {
    for (Entity entity : renderer.getEntities()) {
      player.showEntity(plugin, entity);
    }
    if (interaction != null) {
      player.showEntity(plugin, interaction);
    }

    plugin.getViewManager().playerAdded(this, player);
    firePlayerChangeEvent(EventTypes.PLAYER_ADDED, player);
  }

  void firePlayerChangeEvent(String type, Player player) {
    if (document == null) {
      return;
    }

    DelphiDocumentElement docElem = document.getDocumentElement();
    if (docElem == null) {
      return;
    }

    PlayerSetEventImpl event = new PlayerSetEventImpl(type, document);
    event.initEvent(docElem, false, false, player);
    docElem.dispatchEvent(event);
  }

  /* --------------------------- sub classes ---------------------------- */

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
        h = (float) screen.getHeight();
      } else {
        w = (float) screen.getWidth();
        h = newDimension;
      }

      screen.setDimensions(w, h);
      renderer.screenSizeChanged(h);
    }
  }
}

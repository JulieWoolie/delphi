package net.arcadiusmc.delphiplugin.render;

import static net.arcadiusmc.delphidom.Consts.EMPTY_TD_BLOCK_SIZE;
import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;
import static net.arcadiusmc.delphiplugin.render.RenderLayer.LAYER_COUNT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphidom.scss.ComputedStyle;
import net.arcadiusmc.delphiplugin.HideUtil;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public class RenderObject {

  static final Brightness BRIGHTNESS = new Brightness(15, 15);
  public static final boolean SEE_THROUGH = false;
  
  // Macro layer = A single element
  // Micro layer = A single layer of an element (eg: content, background, outline)
  public static final float MICRO_LAYER_DEPTH = 0.001f;
  public static final float MACRO_LAYER_DEPTH = MICRO_LAYER_DEPTH * LAYER_COUNT;
  public static final float RAD90 = (float) Math.toRadians(90);

  public static final org.bukkit.Color NIL_COLOR = org.bukkit.Color.fromARGB(0, 0, 0, 0);

  private final Screen screen;
  private final PageView view;

  private final ComputedStyle style;

  private final Vector2f position = new Vector2f(0);
  private final Vector2f contentExtension = new Vector2f(0);
  private boolean spawned;

  @Setter
  private float depth;
  
  private RenderObject parent;
  private final List<RenderObject> childObjects = new ArrayList<>();

  private ElementContent content;

  @Setter
  private boolean contentDirty = false;

  private final Layer[] layers = new Layer[LAYER_COUNT];

  private final Align align = Align.Y;

  public RenderObject(PageView view, ComputedStyle style, Screen screen) {
    this.view = view;
    this.screen = screen;
    this.style = style;
  }

  public static boolean isNotSpawned(Layer layer) {
    return layer == null || !layer.isSpawned();
  }

  public boolean hasSpawnedLayer(RenderLayer layer) {
    return !isNotSpawned(layers[layer.ordinal()]);
  }

  public boolean isContentEmpty() {
    return content == null || content.isEmpty();
  }

  private static boolean isNotZero(Rect v) {
    return v.left > 0 || v.bottom > 0 || v.top > 0 || v.right > 0;
  }

  public void killRecursive() {
    for (RenderObject childObject : childObjects) {
      childObject.killRecursive();
    }

    kill();
  }

  public void kill() {
    for (Layer layer : layers) {
      if (isNotSpawned(layer)) {
        continue;
      }

      view.removeEntity(layer.entity);
      layer.killEntity();
    }

    spawned = false;
  }

  public void moveTo(Vector2f screenPos) {
    Vector2f currentPos = new Vector2f(this.position);

    this.position.set(screenPos);
    Location loc = getSpawnLocation();

    forEachSpawedLayer(LayerDirection.FORWARD, (layer, iteratedCount) -> {
      layer.entity.teleport(loc);
    });

    if (childObjects.isEmpty()) {
      return;
    }

    final Vector2f newChildPos = new Vector2f();
    final Vector2f currentChildPos = new Vector2f();
    final Vector2f dif = new Vector2f();

    for (RenderObject child : childObjects) {
      currentChildPos.set(child.position);
      dif.set(currentChildPos).sub(currentPos);

      newChildPos.set(screenPos).add(dif);
      child.moveTo(newChildPos);
    }
  }

  public void getContentStart(Vector2f out) {
    float topDif = style.padding.top + style.outline.top + style.border.top;
    float leftDif = style.padding.left + style.outline.left + style.border.left;

    out.set(position);
    out.add(leftDif * GLOBAL_SCALAR, -topDif * GLOBAL_SCALAR);
  }

  private Vector2f getContentSize() {
    if (content == null) {
      return new Vector2f();
    }

    Vector2f size = new Vector2f();
    content.measureContent(size, style);

    size.mul(GLOBAL_SCALAR).mul(style.scale);
    return size;
  }

  public void getContentEnd(Vector2f out) {
    getContentStart(out);

    if (content == null) {
      return;
    }

    out.add(getContentSize());
  }

  public void getElementSize(Vector2f out) {
    //
    // let raw_size = (content_size * content_scale) + content_extension
    // let capped_size = clamp(raw_size, min_size, max_size)
    // let result = capped_size + padding_size + outline_size
    //              =========================================
    //

    if (content != null) {
      content.measureContent(out, style);
    } else {
      out.set(0);
    }

    out.mul(GLOBAL_SCALAR);
    out.mul(style.scale);
    out.add(contentExtension);
    out.max(style.minSize).min(style.maxSize);

    float xAdd
        = style.padding.left + style.padding.right
        + style.outline.left + style.outline.right
        + style.border.left + style.border.right;

    float yAdd
        = style.padding.top + style.padding.bottom
        + style.outline.top + style.outline.bottom
        + style.border.top + style.border.bottom;

    out.x += xAdd * GLOBAL_SCALAR;
    out.y += yAdd * GLOBAL_SCALAR;
  }

  public void getBounds(Rectangle rectangle) {
    Vector2f pos = rectangle.getPosition();
    Vector2f size = rectangle.getSize();

    getElementSize(size);

    pos.x = position.x;
    pos.y = position.y - size.y;
  }

  /* --------------------------- Children ---------------------------- */

  public void addChild(RenderObject renderObject) {
    addChild(renderObject, childObjects.size());
  }

  public void addChild(RenderObject renderObject, int index) {
    childObjects.add(index, renderObject);
    renderObject.parent = this;
  }

  public RenderObject removeChild(RenderObject element) {
    if (!childObjects.remove(element)) {
      return null;
    }

    element.parent = null;
    return element;
  }

  /* --------------------------- Alignment ---------------------------- */

  public void getAlignmentPosition(Vector2f out) {
    getContentStart(out);

    if (content == null) {
      return;
    }

    Vector2f size = getContentSize();

    if (align == Align.X) {
      out.x += size.x;
    } else {
      out.y -= size.y;
    }
  }

  public void align() {
    if (childObjects.isEmpty()) {
      return;
    }

    for (RenderObject childObject : childObjects) {
      childObject.align();
    }

    boolean aligningOnX = align == Align.X;

    Vector2f alignPos = new Vector2f();
    getAlignmentPosition(alignPos);

    Vector2f pos = new Vector2f(alignPos);
    Vector2f tempMargin = new Vector2f(0);
    Vector2f elemSize = new Vector2f();

    for (RenderObject child : childObjects) {
      if (child.style.display == DisplayType.NONE) {
        continue;
      }

      Rect margin = child.style.margin;

      if (aligningOnX) {
        pos.x += margin.left;

        tempMargin.set(0, -margin.top);
        pos.y -= margin.top;
      } else {
        pos.y -= margin.top;

        tempMargin.set(margin.left, 0);
        pos.x += margin.left;
      }

      child.moveTo(pos);
      pos.sub(tempMargin);

      child.getElementSize(elemSize);

      if (aligningOnX) {
        pos.x += margin.right + elemSize.x;
      } else {
        pos.y -= margin.bottom + elemSize.y;
      }
    }

    postAlign();
  }

  private void postAlign() {
    if (childObjects.isEmpty()) {
      return;
    }

    Vector2f bottomRight = new Vector2f(Float.MIN_VALUE, Float.MAX_VALUE);
    Vector2f childMax = new Vector2f();

    Rectangle rectangle = new Rectangle();

    for (RenderObject child : childObjects) {
      child.getBounds(rectangle);

      childMax.x = rectangle.getPosition().x + rectangle.getSize().x;
      childMax.y = rectangle.getPosition().y;

      bottomRight.x = Math.max(childMax.x, bottomRight.x);
      bottomRight.y = Math.min(childMax.y, bottomRight.y);
    }

    Vector2f contentBottomRight = new Vector2f();
    getContentEnd(contentBottomRight);

    float difX = Math.max(bottomRight.x - contentBottomRight.x, 0);
    float difY = Math.max(contentBottomRight.y - bottomRight.y, 0);

    contentExtension.set(difX, difY);
    spawn();
  }

  /* --------------------------- Spawning ---------------------------- */
  
  private Location getSpawnLocation() {
    Vector3f pos = new Vector3f();
    Vector2f rot = view.getScreen().getRotation();

    view.getScreen().screenToWorld(position, pos);
    return new Location(view.getWorld(), pos.x, pos.y, pos.z, rot.x, rot.y);
  }

  public void spawnRecursive() {
    for (RenderObject childObject : childObjects) {
      childObject.spawnRecursive();
    }

    spawn();
  }

  public void spawn() {
    if (style.display == DisplayType.NONE) {
      kill();
      return;
    }

    Location location = getSpawnLocation();
    Layer content = getLayer(RenderLayer.CONTENT);
    content.nullify();

    // Step 1 - Spawn content
    if (isContentEmpty()) {
      killLayerEntity(content);
    } else {
      Display display = getOrCreateContentEntity(content, location);

      if (display instanceof TextDisplay td) {
        td.setShadowed(style.textShadowed);
      }

      content.size.mul(GLOBAL_SCALAR);
      content.size.mul(style.scale);

      content.scale.x = GLOBAL_SCALAR;
      content.scale.y = GLOBAL_SCALAR;
      content.scale.x *= style.scale.x;
      content.scale.y *= style.scale.y;

      // Early Step 6 - Offset content layer by half it's length
      content.translate.x += (content.size.x * 0.5f);
    }

    if (style.backgroundColor.getAlpha() > 0) {
      createLayerEntity(RenderLayer.BACKGROUND, location, style.backgroundColor, style.padding);
    } else {
      killLayerEntity(getLayer(RenderLayer.BACKGROUND));
    }

    if (isNotZero(style.border) && style.borderColor.getAlpha() > 0) {
      createLayerEntity(RenderLayer.BORDER, location, style.borderColor, style.border);
    } else {
      killLayerEntity(getLayer(RenderLayer.BORDER));
    }

    if (isNotZero(style.outline) && style.outlineColor.getAlpha() > 0) {
      createLayerEntity(RenderLayer.OUTLINE, location, style.outlineColor, style.outline);
    } else {
      killLayerEntity(getLayer(RenderLayer.OUTLINE));
    }

    // Step 4 - Set layer sizes
    calculateLayerSizes();

    // Step 5 - X and Y layer offsets
    applyBorderOffsets();

    // Step 7 - Apply layer specific screen normal offset
    applyScreenNormalOffsets();

    // Step 8 - Apply screen rotation and offset by height
    applyScreenRotation(location.getYaw(), location.getPitch());

    // Step 9 - Apply transformations to entities
    forEachSpawedLayer(LayerDirection.FORWARD, (layer, iteratedCount) -> {
      layer.updateTransform();
    });

    this.spawned = true;
  }

  private Display getOrCreateContentEntity(Layer content, Location location) {
    boolean requiresRespawn;
    ElementContent ec = this.content;

    if (!content.isSpawned()) {
      requiresRespawn = true;
    } else if (ec != null && !ec.getEntityClass().isInstance(content.entity)) {
      requiresRespawn = true;
    } else {
      requiresRespawn = false;
    }

    Display display;

    if (requiresRespawn) {
      killLayerEntity(content);

      display = ec.createEntity(location.getWorld(), location);
      ec.applyContentTo(display, style);

      content.entity = display;
      view.addEntity(display);
    } else {
      display = content.entity;

      if (contentDirty && ec != null) {
        ec.applyContentTo(display, style);
      }
    }

    if (ec != null) {
      ec.measureContent(content.size, style);
      ec.configureInitial(content, this);
    }

    configureDisplay(display);

    return display;
  }
  
  private void applyScreenRotation(float yaw, float pitch) {
    //Quaternionf lrot = new Quaternionf();
    //lrot.rotateY((float) Math.toRadians(yaw));
    //lrot.rotateX((float) Math.toRadians(pitch));

    Vector3f normal = screen.normal();
    Vector3f left = new Vector3f(normal).rotateY(RAD90).normalize();
    Vector3f up = new Vector3f(normal).rotateX(RAD90).normalize();

    Vector3f rX = new Vector3f();
    Vector3f rY = new Vector3f();
    Vector3f rZ = new Vector3f();

    forEachSpawedLayer(LayerDirection.FORWARD, (layer, iteratedCount) -> {
      // Add calculated values
      layer.translate.z -= layer.depth;

      // Perform rotation
      //layer.translate.rotate(lrot, layer.rotatedTranslate);

      layer.rotatedTranslate.set(0);

      left.mul(layer.translate.x, rX);
      up.mul(layer.translate.y, rY);
      normal.mul(layer.translate.z, rZ);

      layer.rotatedTranslate.add(rX).add(rY).add(rZ);
    });
  }

  private void calculateLayerSizes() {
    LayerIterator it = layerIterator(LayerDirection.FORWARD);
    boolean extensionApplied = false;

    while (it.hasNext()) {
      Layer layer = it.next();

      if (layer.layer != RenderLayer.CONTENT) {
        Rect increase = layer.borderSize;

        layer.size.x += increase.left + increase.right;
        layer.size.y += increase.top + increase.bottom;

        if (!extensionApplied) {
          layer.size.x += contentExtension.x;
          layer.size.y += contentExtension.y;

          layer.size.max(style.minSize);
          layer.size.min(style.maxSize);

          extensionApplied = true;
        }

        layer.scale.x = EMPTY_TD_BLOCK_SIZE * layer.size.x;
        layer.scale.y = EMPTY_TD_BLOCK_SIZE * layer.size.y;
      }

      layer.translate.y -= layer.size.y;
      Layer next = nextSpawned(layer);

      if (next == null) {
        continue;
      }

      next.size.add(layer.size);
    }
  }

  private void applyScreenNormalOffsets() {
    forEachSpawedLayer(LayerDirection.BACKWARD, (layer, iteratedCount) -> {
      float micro = iteratedCount * MICRO_LAYER_DEPTH;
      float macro = this.depth * MACRO_LAYER_DEPTH;

      layer.depth = micro + macro + (style.zindex * MACRO_LAYER_DEPTH);
    });
  }
  
  private void applyBorderOffsets() {
    Vector2f offsetStack = new Vector2f(0);

    forEachSpawedLayer(LayerDirection.BACKWARD, (layer, count) -> {
      Layer next = nextSpawned(layer);

      if (next == null) {
        return;
      }

      layer.translate.x += offsetStack.x += next.borderSize.left;
      layer.translate.y -= offsetStack.y += next.borderSize.bottom;
    });
  }

  /* --------------------------- Setters ---------------------------- */

  public void setContent(ElementContent content) {
    this.content = content;
    this.contentDirty = true;
  }

  /* --------------------------- Layers ---------------------------- */

  public final Layer getLayer(RenderLayer layer) {
    Layer l = layers[layer.ordinal()];

    if (l == null) {
      l = new Layer(layer);
      layers[layer.ordinal()] = l;
    }

    return l;
  }

  private void killLayerEntity(Layer layer) {
    if (layer.entity == null) {
      return;
    }

    view.removeEntity(layer.entity);
    layer.killEntity();
  }

  private void createLayerEntity(
      RenderLayer rLayer,
      Location location,
      Color color,
      Rect borderSize
  ) {
    Layer layer = getLayer(rLayer);
    layer.nullify();
    layer.borderSize.set(borderSize);
    layer.borderSize.mul(GLOBAL_SCALAR);

    TextDisplay display;

    if (layer.entity != null && !layer.entity.isDead()) {
      display = (TextDisplay) layer.entity;
    } else {
      killLayerEntity(layer);

      display = location.getWorld().spawn(location, TextDisplay.class);

      layer.entity = display;
      view.addEntity(display);
    }

    if (color == null) {
      display.setBackgroundColor(NIL_COLOR);
    } else {
      display.setBackgroundColor(toBukkit(color));
    }

    configureDisplay(display);
  }

  private org.bukkit.Color toBukkit(Color c) {
    return org.bukkit.Color.fromARGB(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
  }

  private void configureDisplay(Display display) {
    display.setBrightness(BRIGHTNESS);

    if (display instanceof TextDisplay td) {
      td.setSeeThrough(SEE_THROUGH);
    }

    if (style.display == DisplayType.NONE) {
      HideUtil.hide(display);
    }
  }

  private Layer nextSpawned(Layer layer) {
    int ord = layer.layer.ordinal();

    while (true) {
      ord++;

      if (ord >= LAYER_COUNT) {
        return null;
      }

      Layer l = layers[ord];

      if (isNotSpawned(l)) {
        continue;
      }

      return l;
    }
  }

  public void forEachSpawedLayer(LayerDirection dir, LayerOp op) {
    int start = dir.start;
    int iterated = 0;

    for (int i = start; i < LAYER_COUNT && i >= 0; i += dir.modifier) {
      Layer layer = layers[i];

      if (isNotSpawned(layer)) {
        continue;
      }

      op.accept(layer, iterated);
      iterated++;
    }
  }

  private <S extends Display> void applyLayerAs(RenderLayer layer, Class<S> type, Consumer<S> op) {
    applyLayer(layer, display -> {
      if (!type.isInstance(display)) {
        return;
      }

      op.accept((S) display);
    });
  }

  private void applyLayer(RenderLayer layer, Consumer<Display> consumer) {
    Layer l = layers[layer.ordinal()];

    if (isNotSpawned(l)) {
      return;
    }

    consumer.accept(l.entity);
  }

  LayerIterator layerIterator(LayerDirection direction) {
    return new LayerIterator(direction.modifier, direction.start);
  }

  public interface LayerOp {
    void accept(Layer layer, int iteratedCount);
  }

  class LayerIterator implements Iterator<Layer> {

    private int dir;
    private int index;

    @Getter
    private int count;

    public LayerIterator(int dir, int index) {
      this.dir = dir;
      this.index = index;
    }

    private boolean inBounds(int idx) {
      return idx >= 0 && idx < LAYER_COUNT;
    }

    @Override
    public boolean hasNext() {
      if (!inBounds(index)) {
        return false;
      }

      while (inBounds(index)) {
        Layer layer = layers[index];

        if (isNotSpawned(layer)) {
          index += dir;
          continue;
        }

        return true;
      }

      return false;
    }

    @Override
    public Layer next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      Layer l = layers[index];
      index += dir;
      count++;

      return l;
    }
  }

  enum LayerDirection {
    /** Starts from {@link RenderLayer#CONTENT}, moves towards {@link RenderLayer#OUTLINE} */
    FORWARD (0, 1),

    /** Starts from {@link RenderLayer#OUTLINE}, moves towards {@link RenderLayer#CONTENT} */
    BACKWARD (LAYER_COUNT - 1, -1);

    final int start;
    final int modifier;

    LayerDirection(int start, int modifier) {
      this.start = start;
      this.modifier = modifier;
    }
  }
}

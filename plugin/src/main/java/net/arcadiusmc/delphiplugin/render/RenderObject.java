package net.arcadiusmc.delphiplugin.render;

import static net.arcadiusmc.delphidom.Consts.EMPTY_TD_BLOCK_SIZE;
import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;
import static net.arcadiusmc.delphiplugin.render.RenderLayer.LAYER_COUNT;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.dom.style.DisplayType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public abstract class RenderObject {

  static final Brightness BRIGHTNESS = new Brightness(15, 15);
  public static final boolean SEE_THROUGH = false;
  
  // Macro layer = A single element
  // Micro layer = A single layer of an element (eg: content, background, outline)
  public static final float MICRO_LAYER_DEPTH = 0.001f;
  public static final float MACRO_LAYER_DEPTH = MICRO_LAYER_DEPTH * LAYER_COUNT;
  public static final float RAD90 = (float) Math.toRadians(90);

  public static final org.bukkit.Color NIL_COLOR = org.bukkit.Color.fromARGB(0, 0, 0, 0);

  protected final RenderScreen screen;
  protected final RenderSystem system;

  protected final ComputedStyleSet styleSet;
  protected final FullStyle style = new FullStyle();

  protected final Vector2f position = new Vector2f(0);
  public final Vector2f size = new Vector2f(0);

  private boolean spawned;

  @Setter
  private float depth;

  protected ElementRenderObject parent;

  private final Layer[] layers = new Layer[LAYER_COUNT];

  @Setter
  private int sourceIndex = 0;

  public RenderObject(RenderSystem system, ComputedStyleSet style) {
    this.system = system;
    this.screen = system.getScreen();
    this.styleSet = style;
  }

  public static boolean isNotSpawned(Layer layer) {
    return layer == null || !layer.isSpawned();
  }

  public void killRecursive() {
    kill();
  }

  public void kill() {
    for (Layer layer : layers) {
      if (isNotSpawned(layer)) {
        continue;
      }

      system.removeEntity(layer.entity);
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

    postMove(screenPos, currentPos);
  }

  protected void postMove(Vector2f screenPos, Vector2f currentPos) {

  }

  public void getContentStart(Vector2f out) {
    float topDif = style.padding.top + style.outline.top + style.border.top;
    float leftDif = style.padding.left + style.outline.left + style.border.left;

    out.set(position);

    out.x += leftDif * GLOBAL_SCALAR;
    out.y -= topDif * GLOBAL_SCALAR;
  }

  protected abstract void measureContent(Vector2f out);

  protected void clamp(Vector2f vec) {
    float minX = clampFallback(style.minSize.x, Float.MIN_VALUE);
    float minY = clampFallback(style.minSize.y, Float.MIN_VALUE);
    float maxX = clampFallback(style.maxSize.x, Float.MAX_VALUE);
    float maxY = clampFallback(style.maxSize.y, Float.MAX_VALUE);

    vec.x = Math.clamp(vec.x, minX, maxX);
    vec.y = Math.clamp(vec.y, minY, maxY);
  }

  protected float clampFallback(float f, float def) {
    if (f <= 0) {
      return def;
    }
    return f;
  }

  public void getElementSize(Vector2f out) {
    //
    // let raw_size = (content_size * content_scale) + content_extension
    // let capped_size = clamp(raw_size, min_size, max_size)
    // let result = capped_size + padding_size + outline_size
    //              =========================================
    //

    measureContent(out);
    clamp(out);

    out.x += boxWidthIncrease();
    out.y += boxHeightIncrease();
  }

  public float boxHeightIncrease() {
    float base
        = style.padding.top + style.padding.bottom
        + style.outline.top + style.outline.bottom
        + style.border.top + style.border.bottom;

    return base * GLOBAL_SCALAR;
  }

  public float boxWidthIncrease() {
    float base
        = style.padding.left + style.padding.right
        + style.outline.left + style.outline.right
        + style.border.left + style.border.right;

    return base * GLOBAL_SCALAR;
  }

  public void getBounds(Rectangle rectangle) {
    Vector2f pos = rectangle.getPosition();
    Vector2f size = rectangle.getSize();

    getElementSize(size);

    pos.x = position.x;
    pos.y = position.y - size.y;
  }

  /* --------------------------- Spawning ---------------------------- */
  
  private Location getSpawnLocation() {
    Vector3f pos = new Vector3f();
    screen.screenToWorld(position, pos);
    return new Location(system.getWorld(), pos.x, pos.y, pos.z);
  }

  public void spawnRecursive() {
    spawn();
  }

  protected abstract void spawnContent(Location location);

  protected boolean isHidden() {
    return style.display == DisplayType.NONE;
  }

  public void spawn() {
    if (isHidden()) {
      killRecursive();
      return;
    }

    Location location = getSpawnLocation();
    spawnContent(location);

    if (style.backgroundColor.getAlpha() > 0) {
      createLayerEntity(RenderLayer.BACKGROUND, location, style.backgroundColor, style.padding);
    } else {
      killLayerEntity(getLayer(RenderLayer.BACKGROUND));
    }

    if (style.border.isNotZero() && style.borderColor.getAlpha() > 0) {
      createLayerEntity(RenderLayer.BORDER, location, style.borderColor, style.border);
    } else {
      killLayerEntity(getLayer(RenderLayer.BORDER));
    }

    if (style.outline.isNotZero() && style.outlineColor.getAlpha() > 0) {
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
    applyScreenRotationAndScale();

    // Step 9 - Apply transformations to entities
    forEachSpawedLayer(LayerDirection.FORWARD, (layer, iteratedCount) -> {
      layer.updateTransform();
    });

    this.spawned = true;
  }
  
  private void applyScreenRotationAndScale() {
    Quaternionf lrot = screen.getLeftRotation();
    Quaternionf rrot = screen.getRightRotation();

    forEachSpawedLayer(LayerDirection.FORWARD, (layer, iteratedCount) -> {
      // Add calculated values
      layer.translate.z += layer.depth;

      layer.size.mul(screen.getScreenScale());
      layer.scale.mul(screen.getScale());
      layer.translate.x *= screen.getScreenScale().x;
      layer.translate.y *= screen.getScreenScale().y;

      // Perform rotation
      lrot.transform(layer.translate, layer.rotatedTranslate);
      rrot.transform(layer.rotatedTranslate);

      layer.leftRotation.mul(lrot);
      layer.rightRotation.mul(rrot);
    });
  }

  protected void applyContentExtension(Vector2f out) {

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
          applyContentExtension(layer.size);
          clamp(layer.size);
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

  /* --------------------------- Layers ---------------------------- */

  public final Layer getLayer(RenderLayer layer) {
    Layer l = layers[layer.ordinal()];

    if (l == null) {
      l = new Layer(layer);
      layers[layer.ordinal()] = l;
    }

    return l;
  }

  protected void killLayerEntity(Layer layer) {
    if (layer.entity == null) {
      return;
    }

    system.removeEntity(layer.entity);
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
      display.teleport(location);
    } else {
      killLayerEntity(layer);

      display = location.getWorld().spawn(location, TextDisplay.class);

      layer.entity = display;
      system.addEntity(display);
    }

    if (color == null) {
      display.setBackgroundColor(NIL_COLOR);
    } else {
      display.setBackgroundColor(color);
    }

    configureDisplay(display);
  }

  protected void configureDisplay(Display display) {
    display.setBrightness(BRIGHTNESS);

    if (display instanceof TextDisplay td) {
      td.setSeeThrough(SEE_THROUGH);
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

  LayerIterator layerIterator(LayerDirection direction) {
    return new LayerIterator(this.layers, direction.modifier, direction.start);
  }

  public boolean ignoreDisplay() {
    return false;
  }
}
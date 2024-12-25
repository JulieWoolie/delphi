package net.arcadiusmc.delphirender.tree;

import static net.arcadiusmc.delphidom.Consts.EMPTY_TD_BLOCK_SIZE;
import static net.arcadiusmc.delphirender.RenderLayer.BACKGROUND;
import static net.arcadiusmc.delphirender.RenderLayer.BORDER;
import static net.arcadiusmc.delphirender.RenderLayer.LAYER_COUNT;
import static net.arcadiusmc.delphirender.RenderLayer.OUTLINE;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.Layer;
import net.arcadiusmc.delphirender.RenderLayer;
import net.arcadiusmc.delphirender.RenderScreen;
import net.arcadiusmc.delphirender.RenderSystem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
public abstract class RenderElement {

  static final Brightness BRIGHTNESS = new Brightness(15, 15);
  public static final boolean SEE_THROUGH = false;

  // Macro layer = A single element
  // Micro layer = A single layer of an element (eg: content, background, outline)
  public static final float MICRO_LAYER_DEPTH = 0.001f;
  public static final float MACRO_LAYER_DEPTH = MICRO_LAYER_DEPTH * LAYER_COUNT;
  public static final float RAD90 = (float) Math.toRadians(90);

  protected final RenderScreen screen;
  protected final RenderSystem system;

  public final Vector2f position = new Vector2f(0);
  public final Vector2f size = new Vector2f(0);

  public final Layer[] layers;

  protected final ComputedStyleSet styleSet;
  protected final FullStyle style = new FullStyle();

  private boolean spawned;
  public int domIndex = 0;
  public ElementRenderElement parent;

  @Setter
  private float depth;

  public RenderElement(RenderSystem system, ComputedStyleSet styleSet) {
    this.screen = system.getScreen();
    this.system = system;
    this.styleSet = styleSet;
    this.layers = createLayers();
  }

  /* ----------------- LAYERS ----------------- */

  private Layer[] createLayers() {
    Layer[] layers = new Layer[3];
    for (int i = 0; i < layers.length; i++) {
      layers[i] = new Layer();
    }
    return layers;
  }

  private float layerDepth(int layerIdx) {
    return ((layerIdx + style.zindex) * MICRO_LAYER_DEPTH) + (this.depth * MACRO_LAYER_DEPTH);
  }

  private void set(RenderLayer layer, Rect borderSize, boolean alwaysSpawn, Color color) {
    Layer l = layers[layer.ordinal()];
    l.borderSize.set(borderSize);
    l.alwaysSpawn = alwaysSpawn;
    l.color = color;
  }

  public void configure() {
    set(OUTLINE, style.outline, false, style.outlineColor);
    set(BORDER, style.border, false, style.borderColor);
    set(BACKGROUND, style.padding, true, style.backgroundColor);

    for (int i = 0; i < layers.length; i++) {
      Layer layer = layers[i];
      layer.translate.z = layerDepth(i);

      if (i == 0) {
        layer.size.set(this.size);
        layer.borderSize.set(style.outline);
        layer.translate.x = 0;
        layer.translate.y = 0;

        continue;
      }

      Layer prev = layers[i - 1];
      layer.size.set(prev.size);

      layer.translate.x = prev.translate.x;
      layer.translate.y = prev.translate.y;

      layer.translate.x += prev.borderSize.left;
      layer.translate.y -= prev.borderSize.top;

      layer.size.x -= prev.borderSize.x();
      layer.size.y -= prev.borderSize.y();
    }
  }

  public void project() {
    Quaternionf lrot = screen.getLeftRotation();
    Quaternionf rrot = screen.getRightRotation();

    Vector2f screenScale = screen.getScreenScale();
    Vector3f scale = screen.getScale();

    for (Layer layer : layers) {
      layer.translate.y -= layer.size.y;

      layer.scale.x = EMPTY_TD_BLOCK_SIZE * layer.size.x;
      layer.scale.y = EMPTY_TD_BLOCK_SIZE * layer.size.y;

      // Add calculated values
      layer.size.mul(screenScale);
      layer.scale.mul(scale);
      layer.translate.x *= screenScale.x;
      layer.translate.y *= screenScale.y;

      // Perform rotation
      lrot.transform(layer.translate, layer.rotatedTranslate);
      rrot.transform(layer.rotatedTranslate);

      layer.leftRotation.mul(lrot);
      layer.rightRotation.mul(rrot);
    }
  }

  /* ----------------- ENTITY MANAGEMENT ----------------- */

  public void invalidateEntities() {
    for (Layer layer : layers) {
      layer.entity = null;
    }
    spawned = false;
  }

  public void moveTo(Vector2f newPos) {
    moveTo(newPos.x, newPos.y);
  }

  public void moveTo(float x, float y) {
    position.x = x;
    position.y = y;
  }

  public void spawnRecursive() {
    spawn();
  }

  public void killRecursive() {
    kill();
  }

  public void spawn() {
    configure();
    project();

    Vector3f pos = new Vector3f();
    screen.screenToWorld(this.position, pos);
    Location spawnLocation = new Location(system.getWorld(), pos.x, pos.y, pos.z);

    spawnContent(spawnLocation);
    int spawnCount = 0;

    for (Layer layer : layers) {
      if (!layer.shouldSpawn()) {
        if (layer.isSpawned()) {
          system.removeEntity(layer.entity);
        }

        layer.killEntity();
        continue;
      }

      if (layer.isSpawned()) {
        layer.updateEntity();
        layer.updateTransform();
        spawnCount++;

        continue;
      }

      layer.spawn(spawnLocation, spawnLocation.getWorld());

      if (layer.entity == null) {
        continue;
      }

      configureEntity(layer.entity);
      layer.updateTransform();
      system.addEntity(layer.entity);

      spawnCount++;
    }

    spawned = spawnCount > 0;
  }

  protected void spawnContent(Location location) {

  }

  protected void configureEntity(Display display) {
    display.setBrightness(BRIGHTNESS);

    if (display instanceof TextDisplay text) {
      text.setSeeThrough(SEE_THROUGH);
    }
  }

  public boolean kill() {
    if (!spawned) {
      return false;
    }

    for (Layer layer : layers) {
      if (!layer.isSpawned()) {
        continue;
      }

      Display entity = layer.entity;
      system.removeEntity(entity);
      layer.killEntity();
    }

    return true;
  }
}

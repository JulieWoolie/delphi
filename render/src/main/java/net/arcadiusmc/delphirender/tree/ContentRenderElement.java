package net.arcadiusmc.delphirender.tree;

import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphirender.Layer;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.delphirender.content.ElementContent;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

@Getter @Setter
public class ContentRenderElement extends RenderElement {

  static final int LAYER_COUNT = 4;

  private ElementContent content = null;
  private boolean contentDirty = false;

  public ContentRenderElement(RenderSystem system, ComputedStyleSet styleSet) {
    super(system, styleSet);
  }

  public boolean isContentEmpty() {
    return content == null || content.isEmpty();
  }

  @Override
  protected Layer[] createLayers() {
    Layer[] layers = new Layer[LAYER_COUNT];
    for (int i = 0; i < layers.length; i++) {
      layers[i] = new Layer();
    }
    layers[CONTENT_LAYER].alwaysSpawn = true;
    return layers;
  }

  public void setContent(ElementContent content) {
    this.content = content;
    this.contentDirty = true;
  }

  @Override
  protected boolean spawnContent(Location location) {
    Layer content = layers[CONTENT_LAYER];
    content.alwaysSpawn = true;

    if (isContentEmpty()) {
      content.killEntity();
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

    return true;
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
      content.killEntity();

      display = ec.createEntity(location.getWorld(), location);
      ec.applyContentTo(display, style);

      content.entity = display;
      system.addEntity(display);
    } else {
      display = content.entity;
      display.teleport(location);

      if (contentDirty && ec != null) {
        ec.applyContentTo(display, style);
      }
    }

    if (ec != null) {
      ec.measureContent(content.size, style);
      ec.configureInitial(content, this);
    }

    contentDirty = false;
    return display;
  }
}

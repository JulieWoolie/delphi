package net.arcadiusmc.delphiplugin.render;

import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector2f;

@Getter
public class ContentRenderObject extends RenderObject {

  private ElementContent content;

  @Setter
  private boolean contentDirty = false;

  public ContentRenderObject(RenderSystem system, ComputedStyleSet style) {
    super(system, style);
  }

  public boolean isContentEmpty() {
    return content == null || content.isEmpty();
  }

  @Override
  protected boolean isHidden() {
    return super.isHidden() || isContentEmpty();
  }

  @Override
  public boolean ignoreDisplay() {
    return content instanceof StringContent;
  }

  @Override
  protected void measureContent(Vector2f out) {
    if (isContentEmpty()) {
      out.set(0);
      return;
    }

    content.measureContent(out, style);
    out.mul(GLOBAL_SCALAR).mul(style.scale);
  }

  @Override
  protected void spawnContent(Location location) {
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

    configureDisplay(display);
    contentDirty = false;

    return display;
  }

  /* --------------------------- Setters ---------------------------- */

  public void setContent(ElementContent content) {
    this.content = content;
    this.contentDirty = true;
  }
}

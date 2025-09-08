package com.juliewoolie.delphiplugin.devtools;

import static com.juliewoolie.delphirender.Consts.EMPTY_CONTENT;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_X;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_Y;
import static com.juliewoolie.delphirender.Consts.EMPTY_TEXT_OPACITY;
import static com.juliewoolie.delphirender.object.BoxRenderObject.visualCenterOffset;

import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphiplugin.math.Screen;
import com.juliewoolie.delphirender.object.RenderObject;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ElementHighlight {

  private final Screen screen;
  private final PageView devtoolsView;
  public World world;

  public final Vector2f position = new Vector2f(0);
  public final Vector2f size = new Vector2f(0);

  public final Rect rect = new Rect();

  public Color color = Color.WHITE;
  public TextDisplay entity;

  public float depth = 0.0f;

  public ElementHighlight(Screen screen, PageView devtoolsView) {
    this.screen = screen;
    this.devtoolsView = devtoolsView;
  }

  private Location getSpawnLocation() {
    Vector3f out = new Vector3f();
    screen.screenToWorld(position, out);
    return new Location(world, out.x, out.y, out.z);
  }

  public void spawn() {
    if (size.x < 0 && size.y < 0 || !rect.isNotZero()) {
      kill();
      return;
    }

    if (entity == null || entity.isDead()) {
      if (world == null) {
        return;
      }

      entity = world.spawn(getSpawnLocation(), TextDisplay.class);
      entity.setBackgroundColor(color);
      entity.text(EMPTY_CONTENT);
      entity.setBrightness(RenderObject.BRIGHTNESS);
      entity.setTextOpacity(EMPTY_TEXT_OPACITY);

      devtoolsView.handleEntityVisibility(entity);
    } else {
      entity.teleport(getSpawnLocation());
    }

    Transformation transform = RenderObject.newTransform();
    Vector3f scale = transform.getScale();
    Vector3f offset = transform.getTranslation();

    scale.x = EMPTY_TD_BLOCK_SIZE_X * size.x;
    scale.y = EMPTY_TD_BLOCK_SIZE_Y * size.y;
    scale.z = 1.0f;

    offset.x = (size.x * 0.5f) - visualCenterOffset(scale.x);
    offset.y -= size.y;
    offset.z = depth;

    screen.project(transform);
    entity.setTransformation(transform);
  }

  public void kill() {
    if (entity == null) {
      return;
    }
    if (entity.isDead()) {
      entity = null;
      return;
    }

    entity.remove();
    entity = null;
  }
}

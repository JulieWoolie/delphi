package net.arcadiusmc.delphirender.object;

import static net.arcadiusmc.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_X;
import static net.arcadiusmc.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_Y;
import static net.arcadiusmc.delphirender.object.BoxRenderObject.visualCenterOffset;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.arcadiusmc.delphidom.DelphiCanvas;
import net.arcadiusmc.delphirender.Consts;
import net.arcadiusmc.delphirender.RenderSystem;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class CanvasRenderObject extends RenderObject {

  public DelphiCanvas canvas;
  public final List<TextDisplay> entities = new ObjectArrayList<>();

  public CanvasRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  public void spawn() {
    int h = canvas.getHeight();
    int w = canvas.getWidth();

    Vector2f pixelSize = new Vector2f(size).div(w, h);
    Vector2f pos = new Vector2f();
    Vector3i color = new Vector3i();

    World world = system.getWorld();
    Location location = new Location(world, 0, 0, 0);

    int idx = 0;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        canvas.sample(idx, color);

        pos.set(this.position);
        pos.x += x * pixelSize.x;
        pos.y -= y * pixelSize.y;

        screenLocation(pos, location);

        TextDisplay display;

        if (idx >= entities.size()) {
          display = world.spawn(location, TextDisplay.class);
          display.text(Consts.EMPTY_CONTENT);
          display.setTextOpacity(Consts.EMPTY_TEXT_OPACITY);
          configureEntity(display);
          system.addEntity(display);
          entities.addLast(display);
        } else {
          display = entities.get(idx);
          display.teleport(location);
        }

        Color bukkitColor = Color.fromRGB(color.x, color.y, color.z);
        display.setBackgroundColor(bukkitColor);

        Transformation trans = newTransform();

        Vector3f scale = trans.getScale();
        scale.x = EMPTY_TD_BLOCK_SIZE_X * pixelSize.x;
        scale.y = EMPTY_TD_BLOCK_SIZE_Y * pixelSize.y;

        Vector3f offset = trans.getTranslation();
        offset.x += (pixelSize.x * 0.5f) + visualCenterOffset(scale.x);
        offset.y -= pixelSize.y;
        offset.z = depth + getZIndexDepth();

        screen.project(trans);
        display.setTransformation(trans);

        idx++;
      }
    }

    if (idx < entities.size()) {
      List<TextDisplay> unused = entities.subList(idx, entities.size());
      for (TextDisplay display : unused) {
        system.removeEntity(display);
        display.remove();
      }
      unused.clear();
    }
  }

  @Override
  public void kill() {
    for (TextDisplay entity : entities) {
      system.removeEntity(entity);
      entity.remove();
    }
    entities.clear();
  }
}

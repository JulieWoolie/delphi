package com.juliewoolie.delphirender.object;

import static com.juliewoolie.delphirender.Consts.BOX_OVERPRINT;
import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_X;
import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_Y;
import static com.juliewoolie.delphirender.Consts.EMPTY_CONTENT;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_X;
import static com.juliewoolie.delphirender.Consts.EMPTY_TD_BLOCK_SIZE_Y;
import static com.juliewoolie.delphirender.Consts.EMPTY_TEXT_OPACITY;
import static com.juliewoolie.delphirender.object.BoxRenderObject.visualCenterOffset;

import com.juliewoolie.delphidom.DelphiCanvas;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.nlayout.MeasureFunc;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;
import org.slf4j.Logger;

public class CanvasRenderObject extends RenderObject implements MeasureFunc {

  private static final Logger LOGGER = Loggers.getLogger();

  public DelphiCanvas canvas;

  public final List<TextDisplay> entities = new ObjectArrayList<>();
  public int entityIdx = 0;

  public CanvasRenderObject(RenderSystem system) {
    super(system);
  }

  private TextDisplay allocEntity(Location location) {
    if (entityIdx < entities.size()) {
      TextDisplay display = entities.get(entityIdx++);
      display.teleport(location);
      return display;
    }

    TextDisplay display = system.getWorld().spawn(location, TextDisplay.class);
    display.text(EMPTY_CONTENT);
    display.setTextOpacity(EMPTY_TEXT_OPACITY);

    configureEntity(display);

    system.addEntity(display);
    entities.addLast(display);

    entityIdx = entities.size();

    return display;
  }

  @Override
  public void spawn() {
    int h = canvas.getHeight();
    int w = canvas.getWidth();

    Vector2f size;
    if (parent != null) {
      size = new Vector2f();
      parent.getContentSize(size);
    } else {
      size = this.size;
    }

    Vector2f pixelSize = new Vector2f(size).div(w, h);
    Vector2f pos = new Vector2f();
    Vector4i color = new Vector4i();

    World world = system.getWorld();
    Location location = new Location(world, 0, 0, 0);

    int idx = 0;
    this.entityIdx = 0;

    List<PixelMesh> meshes = greedyMesh();

    for (int i = 0; i < meshes.size(); i++) {
      PixelMesh mesh = meshes.get(i);

      pos.set(this.position);
      pos.x += mesh.x * pixelSize.x;
      pos.y -= mesh.y * pixelSize.y;

      pos.x -= BOX_OVERPRINT;
      pos.y += BOX_OVERPRINT;

      screenLocation(pos, location);
      TextDisplay td = allocEntity(location);

      Color bcolor = Color.fromARGB(mesh.alpha, mesh.red, mesh.green, mesh.blue);
      td.setBackgroundColor(bcolor);

      Transformation trans = newTransform();

      float mw = (pixelSize.x * mesh.width) + (BOX_OVERPRINT * 2.0f);
      float mh = (pixelSize.y * mesh.height) + (BOX_OVERPRINT * 2.0f);

      Vector3f scale = trans.getScale();
      scale.x = EMPTY_TD_BLOCK_SIZE_X * mw;
      scale.y = EMPTY_TD_BLOCK_SIZE_Y * mh;

      Vector3f offset = trans.getTranslation();
      offset.x += (mw * 0.5f) - visualCenterOffset(scale.x);
      offset.y -= mh;
      offset.z = depth + getZIndexDepth();

      screen.project(trans);
      td.setTransformation(trans);
    }

    Vector2f pixelOverprintSize = new Vector2f(pixelSize);
    pixelOverprintSize.x += (BOX_OVERPRINT * 2.0f);
    pixelOverprintSize.y += (BOX_OVERPRINT * 2.0f);

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (containsAny(meshes, x, y)) {
          idx++;
          continue;
        }

        canvas.sample(idx, color);

        if (color.w <= 0) {
          idx++;
          continue;
        }

        pos.set(this.position);
        pos.x += x * pixelSize.x;
        pos.y -= y * pixelSize.y;

        pos.x -= BOX_OVERPRINT;
        pos.y += BOX_OVERPRINT;

        screenLocation(pos, location);
        TextDisplay td = allocEntity(location);

        Color bukkitColor = Color.fromARGB(color.w, color.x, color.y, color.z);
        td.setBackgroundColor(bukkitColor);

        Transformation trans = newTransform();

        Vector3f scale = trans.getScale();
        scale.x = EMPTY_TD_BLOCK_SIZE_X * (pixelOverprintSize.x);
        scale.y = EMPTY_TD_BLOCK_SIZE_Y * (pixelOverprintSize.y);

        Vector3f offset = trans.getTranslation();
        offset.x += (pixelOverprintSize.x * 0.5f) - visualCenterOffset(scale.x);
        offset.y -= pixelSize.y;
        offset.z = depth + getZIndexDepth();

        screen.project(trans);
        td.setTransformation(trans);

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

  List<PixelMesh> greedyMesh() {
    List<PixelMesh> meshes = new ArrayList<>();

    int h = canvas.getHeight();
    int w = canvas.getWidth();

    Vector4i color = new Vector4i();
    Vector4i dcolor = new Vector4i();

    yloop: for (int y = 0; y < h; y++) {
      xloop: for (int x = 0; x < w; x++) {
        if (containsAny(meshes, x, y)) {
          continue xloop;
        }

        canvas.sample(x, y, color);
        if (color.w <= 0) {
          continue xloop;
        }

        int mw = 1;
        int mh = 1;

        canvas.sample(x + 1, y, dcolor);

        while (dcolor.equals(color)) {
          mw++;
          canvas.sample(x + mw, y, dcolor);
        }

        while (sampleLineEquals(x, y + mh, mw, color, dcolor)) {
          mh++;
        }

        if (mw < 2 && mh < 2) {
          continue;
        }

        PixelMesh mesh = new PixelMesh();
        mesh.x = x;
        mesh.y = y;
        mesh.width = mw;
        mesh.height = mh;
        mesh.red = color.x;
        mesh.green = color.y;
        mesh.blue = color.z;
        mesh.alpha = color.w;

        meshes.add(mesh);

        // If canvas is literally just one color
        if (x == 0 && y == 0 && mw == w && mh == h) {
          break yloop;
        }
      }
    }

    return meshes;
  }

  private boolean sampleLineEquals(int x, int y, int w, Vector4i color, Vector4i dcolor) {
    for (int i = 0; i < w; i++) {
      canvas.sample(x + i, y, dcolor);
      if (!dcolor.equals(color)) {
        return false;
      }
    }
    return true;
  }

  boolean containsAny(List<PixelMesh> meshes, int x, int y) {
    for (int i = 0; i < meshes.size(); i++) {
      if (!meshes.get(i).contains(x, y)) {
        continue;
      }
      return true;
    }
    return false;
  }

  @Override
  public void measure(Vector2f out) {
    out.x = canvas.getWidth() * CHAR_PX_SIZE_X;
    out.y = canvas.getHeight() * CHAR_PX_SIZE_Y;
  }

  static class PixelMesh {
    int x;
    int y;

    int width;
    int height;

    int red;
    int green;
    int blue;
    int alpha;

    boolean contains(int x, int y) {
      return (x >= this.x && (x < (this.x + width)))
          && (y >= this.y && (y < (this.y + height)));
    }
  }
}

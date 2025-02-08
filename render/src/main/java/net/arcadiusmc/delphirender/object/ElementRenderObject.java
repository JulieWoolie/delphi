package net.arcadiusmc.delphirender.object;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.Visibility;
import org.joml.Vector2f;

public class ElementRenderObject extends RenderObject {

  static final Comparator<RenderObject> BY_INDEX
      = Comparator.comparingInt(value -> value.domIndex);

  static final Comparator<RenderObject> BY_ORDER
      = Comparator.<RenderObject>comparingInt(value -> {
        if (value instanceof ElementRenderObject el) {
          return el.style.order;
        }
        return 0;
      })
      .thenComparing(BY_INDEX);

  public static final int BOXES = 3;
  public static final int OUTLINE = 0;
  public static final int BORDER = 1;
  public static final int BACKGROUND = 2;

  public final FullStyle style = new FullStyle();

  public final ComputedStyleSet computedStyleSet;
  public boolean spawned = false;

  public final BoxRenderObject[] boxes = new BoxRenderObject[BOXES];

  @Getter
  private final List<RenderObject> childObjects = new ArrayList<>();

  public ElementRenderObject(RenderSystem system, ComputedStyleSet set) {
    super(system);

    this.computedStyleSet = set;

    for (int i = 0; i < boxes.length; i++) {
      BoxRenderObject ro = new BoxRenderObject(system);
      ro.parent = this;

      boxes[i] = ro;
    }
  }

  @Override
  public void moveTo(float x, float y) {
    float offx = x - position.x;
    float offy = y - position.y;

    for (RenderObject childObject : childObjects) {
      float cx = childObject.position.x + offx;
      float cy = childObject.position.y + offy;
      childObject.moveTo(cx, cy);
    }

    for (BoxRenderObject box : boxes) {
      float cx = box.position.x + offx;
      float cy = box.position.y + offy;
      box.moveTo(cx, cy);
    }

    super.moveTo(x, y);
  }

  public boolean isHidden() {
    return style.display == DisplayType.NONE || style.visibility != Visibility.VISIBLE;
  }

  public void spawnRecursive() {
    if (isHidden()) {
      killRecursive();
      return;
    }

    spawn();

    for (RenderObject childObject : childObjects) {
      childObject.spawnRecursive();
    }
  }

  public void getContentStart(Vector2f out) {
    float leftOff = style.padding.left + style.border.left + style.outline.left;
    float topOff = style.padding.top + style.border.top + style.outline.top;

    out.x = position.x + leftOff;
    out.y = position.y - topOff;
  }

  private Rect scaledRect(Rect r) {
    return new Rect(r);
  }

  public void configureBoxes() {
    BoxRenderObject outline = boxes[OUTLINE];
    BoxRenderObject border = boxes[BORDER];
    BoxRenderObject bg = boxes[BACKGROUND];

    Rect outlineSize = scaledRect(style.outline);
    Rect borderSize = scaledRect(style.border);

    Vector2f pos = new Vector2f(position);

    // Configure layer colors
    outline.color = style.outlineColor;
    border.color = style.borderColor;
    bg.color = style.backgroundColor;

    // box.size = previousBox.size - previousBox.borderSizes
    // box.pos = previousBox.pos + previousBox.borderSizes.topLeft

    // Configure outline
    outline.moveTo(pos);
    outline.size.set(this.size);

    // Configure border
    pos.x += outlineSize.left;
    pos.y -= outlineSize.top;
    border.moveTo(pos);
    border.size.set(outline.size);
    border.size.x -= outlineSize.x();
    border.size.y -= outlineSize.y();

    // Configure padding/background
    pos.x += borderSize.left;
    pos.y -= borderSize.top;
    bg.moveTo(pos);
    bg.size.set(border.size);
    bg.size.x -= borderSize.x();
    bg.size.y -= borderSize.y();

    // Configure depth values
    for (int i = 0; i < boxes.length; i++) {
      BoxRenderObject box = boxes[i];
      box.depth = this.depth + (i * MICRO_LAYER_DEPTH);
    }
  }

  @Override
  public void spawn() {
    if (isHidden()) {
      kill();
      return;
    }

    configureBoxes();

    Rect outline = style.outline;
    Rect border = style.border;

    if (outline.isNotZero()) {
      boxes[OUTLINE].spawn();
    }
    if (border.isNotZero()) {
      boxes[BORDER].spawn();
    }

    boxes[BACKGROUND].spawn();

    spawned = true;
  }

  @Override
  public void kill() {
    for (BoxRenderObject box : boxes) {
      box.kill();
    }

    spawned = false;
  }

  public void addChild(int i, RenderObject obj) {
    childObjects.add(i, obj);
    obj.parent = this;
  }

  public boolean removeChild(RenderObject obj) {
    int idx = childObjects.indexOf(obj);
    if (idx == -1) {
      return false;
    }

    removeChild(idx);
    return true;
  }

  public void removeChild(int idx) {
    RenderObject obj = childObjects.remove(idx);
    obj.parent = null;
  }

  public void killRecursive() {
    kill();

    for (RenderObject childObject : childObjects) {
      childObject.killRecursive();
    }
  }

  public <T extends RenderObject> T onlyChild() {
    return (T) childObjects.getFirst();
  }

  public void sortChildren() {
    if (style.display == DisplayType.FLEX) {
      childObjects.sort(BY_ORDER);
      return;
    }

    childObjects.sort(BY_INDEX);
  }

  public void getContentSize(Vector2f out) {
    out.set(size);

    Rect outline = scaledRect(style.outline);
    Rect border = scaledRect(style.border);
    Rect padding = scaledRect(style.padding);

    out.x -= outline.x() + border.x() + padding.x();
    out.y -= outline.y() + border.y() + padding.y();
  }
}

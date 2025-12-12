package com.juliewoolie.delphirender.object;

import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.dom.SliderElement.SliderOrient;
import com.juliewoolie.nlayout.LayoutBox;
import com.juliewoolie.nlayout.MeasureFunc;
import org.joml.Vector2f;

public class SliderRenderObject extends BoxRenderObject implements MeasureFunc {

  public double ratio = 0.0d;
  public SliderOrient orient = SliderOrient.HORIZONTAL;

  public SliderRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  public void spawn() {
    this.color = FullStyle.toBukkitColor(parent.computedStyleSet.color);
    super.spawn();
  }

  @Override
  public void measure(Vector2f out) {
    out.set(0);
  }

  public void updateFromParentSize() {
    if (parent == null) {
      return;
    }

    size.set(parent.size);
    LayoutBox.subtractExtraSpace(size, getParentStyle());

    if (orient == SliderOrient.HORIZONTAL) {
      size.x *= ratio;
    } else {
      size.y *= ratio;
    }
  }
}

package net.arcadiusmc.delphirender.layout;

import net.arcadiusmc.delphirender.object.ElementRenderObject;
import org.joml.Vector2f;

public interface LayoutAlgorithm {

  boolean measure(ElementRenderObject ro, MeasureContext ctx, Vector2f out);

  void layout(ElementRenderObject ro);
}

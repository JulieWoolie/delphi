package net.arcadiusmc.delphirender.layout;

import net.arcadiusmc.delphirender.tree.ElementRenderElement;
import net.arcadiusmc.delphirender.tree.RenderElement;
import org.joml.Vector2f;

public interface LayoutStyle {

  void firstLayoutPass(RenderElement el);

  default void secondLayoutPass(RenderElement el) {

  }

  void measure(ElementRenderElement el, Vector2f out);
}

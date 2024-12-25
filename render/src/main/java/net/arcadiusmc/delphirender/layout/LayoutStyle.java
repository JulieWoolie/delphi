package net.arcadiusmc.delphirender.layout;

import net.arcadiusmc.delphirender.tree.RenderElement;

public interface LayoutStyle {

  void firstLayoutPass(RenderElement el, Layout layout);

  default void secondLayoutPass(RenderElement el, Layout layout) {

  }
}

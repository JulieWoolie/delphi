package net.arcadiusmc.delphiplugin.render;

public enum RenderLayer {
  CONTENT,
  BACKGROUND,
  BORDER,
  OUTLINE,
  ;

  public static final RenderLayer[] LAYERS = values();
  public static final int LAYER_COUNT = LAYERS.length;
}

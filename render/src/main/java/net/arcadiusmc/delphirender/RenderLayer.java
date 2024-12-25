package net.arcadiusmc.delphirender;

public enum RenderLayer {
  OUTLINE,
  BORDER,
  BACKGROUND,
  CONTENT,
  ;

  public static final RenderLayer[] LAYERS = values();
  public static final int LAYER_COUNT = LAYERS.length;
}

package net.arcadiusmc.delphirender;

import static net.arcadiusmc.delphirender.RenderLayer.LAYER_COUNT;

public enum LayerDirection {
  /**
   * Starts from {@link RenderLayer#CONTENT}, moves towards {@link RenderLayer#OUTLINE}
   */
  FORWARD(0, 1),

  /**
   * Starts from {@link RenderLayer#OUTLINE}, moves towards {@link RenderLayer#CONTENT}
   */
  BACKWARD(LAYER_COUNT - 1, -1);

  public final int start;
  public final int modifier;

  LayerDirection(int start, int modifier) {
    this.start = start;
    this.modifier = modifier;
  }
}

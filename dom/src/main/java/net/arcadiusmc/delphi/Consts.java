package net.arcadiusmc.delphi;

public interface Consts {

  /**
   * Scale value of a text display with no text to make it take up 1 block of space
   */
  float EMPTY_TD_BLOCK_SIZE = 40.0f;
  float CHAR_PX_SIZE = 1.0f / EMPTY_TD_BLOCK_SIZE;

  /** Global element scale */
  float GLOBAL_SCALAR = .5f;

  float ITEM_PX_TO_CH_PX = 2.5f;
  float ITEM_Z_WIDTH = 0.001f;
  float ITEM_SPRITE_SIZE = ITEM_PX_TO_CH_PX * CHAR_PX_SIZE * 16;
  boolean SEE_THROUGH = false;

  float LEN0 = 5;
  float LEN0_PX = LEN0 * CHAR_PX_SIZE;
}

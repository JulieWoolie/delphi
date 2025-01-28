package net.arcadiusmc.delphidom;

import net.kyori.adventure.text.Component;

public interface Consts {

  String EMPTY_BLOCK_CONTENT = "0";
  Component EMPTY_CONTENT = Component.text(EMPTY_BLOCK_CONTENT);
  byte EMPTY_TEXT_OPACITY = 24;

  float CHAR_PX_SIZE_X = 0.025f;
  float CHAR_PX_SIZE_Y = 0.025f;
  float EMPTY_TD_BLOCK_SIZE_X = 1.0f / (CHAR_PX_SIZE_X * 7.0f);
  float EMPTY_TD_BLOCK_SIZE_Y = 1.0f / (CHAR_PX_SIZE_Y * 10.0f);

  /** Global element scale */
  float GLOBAL_SCALAR = .5f;

  float ITEM_PX_TO_CH_PX = 2.5f;
  float ITEM_Z_WIDTH = 0.001f;
  float ITEM_SPRITE_SIZE = ITEM_PX_TO_CH_PX * CHAR_PX_SIZE_X * 16;
  boolean SEE_THROUGH = false;

  float LEN0 = 5;
  float LEN0_PX = LEN0 * CHAR_PX_SIZE_X;

  float MIN_SCREEN_SIZE = 1;
  float MAX_SCREEN_SIZE = 10;
}

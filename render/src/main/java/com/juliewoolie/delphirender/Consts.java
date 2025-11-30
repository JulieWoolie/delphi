package com.juliewoolie.delphirender;

import net.kyori.adventure.text.Component;

public interface Consts {

  /**
   * String content of text displays that are intended to exist as
   * background, outline and border elements.
   * <p>
   * Mojang fixed the bug where a text display with no content was displayed
   * as a single pixel, sized {@link #CHAR_PX_SIZE_X}x{@link #CHAR_PX_SIZE_Y}.
   * So now a content of '0' has to be used with an opacity value low enough
   * to not show it.
   */
  String EMPTY_BLOCK_CONTENT = "0";

  /**
   * Component with content {@link #EMPTY_BLOCK_CONTENT}
   */
  Component EMPTY_CONTENT = Component.text(EMPTY_BLOCK_CONTENT);

  /**
   * Opacity value of text displays that are intended to exist as background,
   * outline and border elements.
   *
   * @see #EMPTY_BLOCK_CONTENT
   */
  byte EMPTY_TEXT_OPACITY = 24;

  /**
   * The size of a single pixel on an unscaled text display
   */
  float CHAR_PX_SIZE_X = 0.025f;

  /**
   * The size of a single pixel on an unscaled text display
   */
  float CHAR_PX_SIZE_Y = 0.025f;

  /**
   * The width (in pixels) of a text display with the content "0". This
   * constant includes both the character pixels and the 1 pixel of padding
   * the display entity has on both the left and right sides.
   */
  float CH_0_SIZE_X = 7.0f;

  /**
   * The height (in pixels) of a text display with the content "0". This
   * constant includes both the character pixels and the 1 pixel of padding
   * the display entity has on both the top and bottom.
   */
  float CH_0_SIZE_Y = 10.0f;

  /**
   * The X scale of a text display with the content "0" stretched to fill a
   * whole block.
   */
  float EMPTY_TD_BLOCK_SIZE_X = 1.0f / (CHAR_PX_SIZE_X * CH_0_SIZE_X);

  /**
   * The Y scale of a text display with the content "0" stretched to fill a whole block.
   */
  float EMPTY_TD_BLOCK_SIZE_Y = 1.0f / (CHAR_PX_SIZE_Y * CH_0_SIZE_Y);

  //
  // The '0' char is offset from the center by this amount,
  // IF the scale X = EMPTY_TD_BLOCK_SIZE_X and the
  //        scale Y = EMPTY_TD_BLOCK_SIZE_Y
  //
  // To find the appropriate X offset, consider f(x) where X is the X scale
  // of the display entity, the function would look like so:
  //
  //  f(x) = (x / EMPTY_TD_BLOCK_SIZE_X) * BLOCK_OFFSET_X
  //
  // If you want to know where this number is from: My ass
  // No, I measured it using Axiom's display entity editor tools and a
  // bit of math
  //
  /**
   * The distance between a text display entity's visual center and its
   * entity origin point when it's been scaled to {@link #EMPTY_TD_BLOCK_SIZE_X} and
   * {@link #EMPTY_TD_BLOCK_SIZE_Y}.
   */
  float BLOCK_OFFSET_X = 0.0717f;

  /**
   * The difference between the size of an item sprite's pixels and a
   * text display's pixels
   */
  float ITEM_PX_TO_CH_PX = 2.5f;

  /**
   * Unscaled size of a regular item display entity
   */
  float ITEM_SPRITE_SIZE = ITEM_PX_TO_CH_PX * CHAR_PX_SIZE_X * 16;

  /**
   * The width of the '0' character in pixels
   */
  float LEN0 = 5;

  /**
   * The width of the '0' character in unscaled entity pixels
   */
  float LEN0_PX = LEN0 * CHAR_PX_SIZE_X;

  /**
   * The distance between each box inside an element render object on the Z
   * axis.
   * <p>
   * <b>Note</b>: This is the smallest I could make this value before Z
   * fighting became a major issue. Luckily, this value is also small enough,
   * so it doesn't look like elements are floating above other elements too
   * obviously.
   */
  float MICRO_LAYER_DEPTH = 0.000125f;

  /**
   * The distance between each render object on the Z axis.
   */
  float MACRO_LAYER_DEPTH = MICRO_LAYER_DEPTH * 3;

  /**
   * The amount a box render object's or canvas pixel's size is increased
   * to prevent some weird artifacting.
   */
  float BOX_OVERPRINT = 0.000125f;
}

package com.juliewoolie.dom;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Field set representation.
 * <p>
 * A field set allows for multiple input elements to be grouped together so their shown in one
 * dialog to a player.
 */
public interface FieldSetElement extends Element {

  /**
   * Get a list of input elements that belong to the field set
   * @return Field set elements
   */
  List<Element> getFieldSetElements();

  /**
   * Get the dialog title which will be shown when a player opens the field set's dialog.
   * <p>
   * The returned title supports rendering using minimessage rendering.
   *
   * @return Dialog title
   */
  @Nullable String getDialogTitle();

  /**
   * Set the dialog title which will be shown when a player opens the field set's dialog.
   * <p>
   * The specified {@code title} supports rendering using minimessage rendering.
   *
   * @param title Dialog title
   */
  void setDialogTitle(@Nullable String title);
}

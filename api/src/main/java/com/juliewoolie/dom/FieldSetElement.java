package com.juliewoolie.dom;

import java.util.List;

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
}

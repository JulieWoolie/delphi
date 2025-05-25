package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.TextNode;

/**
 * Event fired when a text node's content is changed. Fired on the text node's parent element.
 */
public interface TextChangeEvent extends Event {

  /**
   * Get the modified text node
   * @return Modified text node
   */
  TextNode getTextNode();
}

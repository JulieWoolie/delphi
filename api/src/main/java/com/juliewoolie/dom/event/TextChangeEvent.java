package com.juliewoolie.dom.event;

import com.juliewoolie.dom.TextNode;

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

package com.juliewoolie.dom.style;

/**
 * CSS object that can be represented by a CSS keyword
 */
public interface KeywordRepresentable {

  /**
   * Get the CSS keyword used to represent this object
   * @return CSS keyword
   */
  String getKeyword();
}

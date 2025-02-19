package net.arcadiusmc.delphidom;

public enum ContentSource {
  /**
   * No value
   */
  NONE,

  /**
   * Value loaded from a file pointed to by the "src" attribute
   */
  SRC_ATTR,

  /**
   * Value derived from element text content (text content is treated SCSS, JSON, etc)
   */
  TEXT_CONTENT,
  ;
}

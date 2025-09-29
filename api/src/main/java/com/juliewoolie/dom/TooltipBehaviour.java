package com.juliewoolie.dom;

/**
 * Represents the behaviour of a tooltip
 */
public enum TooltipBehaviour {

  /** Tooltip follows cursor */
  CURSOR_STICKY("cursor-sticky"),

  /** Tooltip spawns where cursor is, but doesn't move */
  CURSOR("cursor"),

  /** Tooltip appears on the left side of the element */
  LEFT ("left"),

  /** Tooltip appears on the right side of the element */
  RIGHT ("right"),

  /** Tooltip appears on the above the element */
  ABOVE ("above"),

  /** Tooltip appears on the below the element */
  BELOW ("below"),
  ;

  private final String attributeValue;

  TooltipBehaviour(String attributeValue) {
    this.attributeValue = attributeValue;
  }

  /**
   * Get the attribute value of the behaviour.
   * @return Attribute value
   */
  public String getAttributeValue() {
    return attributeValue;
  }
}

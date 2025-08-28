package com.juliewoolie.dom.style;

/**
 * CSS rule inside of a style sheet
 */
public interface StyleRule {

  /**
   * Get the rule selector
   * @return Selector
   */
  String getSelector();

  /**
   * Get the properties specified by the rule.
   * @return Rule properties
   */
  StylePropertiesReadonly getProperties();

  /**
   * Get the stylesheet the rule belongs to
   * @return Stylesheet
   */
  Stylesheet getStylesheet();
}

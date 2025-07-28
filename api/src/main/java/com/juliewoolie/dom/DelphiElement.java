package com.juliewoolie.dom;

import org.jetbrains.annotations.Nullable;

/**
 * The root element of a Delphi menu
 */
public interface DelphiElement extends Element {

  /**
   * Get the head element directly under this element. May be null if the head hasn't been loaded
   * yet during parsing or simply hasn't been set.
   *
   * @return Head element
   */
  HeadElement getHeadElement();

  /**
   * Get the body element directly under this element. May be null if the body hasn't been loaded
   * yet during parsing or simply hasn't been set.
   *
   * @return Body element
   */
  @Nullable
  BodyElement getBodyElement();
}

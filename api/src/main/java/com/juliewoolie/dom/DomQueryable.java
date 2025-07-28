package com.juliewoolie.dom;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an object that supports querying its descendant elements.
 */
public interface DomQueryable {

  /**
   * Gets an array list of elements by their tag name
   *
   * @param tagName Element tag name
   * @return An array list of elements with the specified tag name
   *
   * @throws NullPointerException if {@code tagName} is {@code null}
   */
  @NotNull List<Element> getElementsByTagName(@NotNull String tagName);

  /**
   * Gets an array list of elements with the specified class name
   *
   * @param className Class name to search for
   * @return An array list of elements with the specified class.
   */
  @NotNull List<Element> getElementsByClassName(@NotNull String className);

  /**
   * Gets all the elements that match the specified {@code query} CSS selector, or group of
   * selectors.
   *
   * @param query CSS selector
   * @return Array list of all matching elements, may be empty
   *
   * @throws ParserException If the {@code query} could not be parsed into a CSS selector
   */
  @NotNull List<Element> querySelectorAll(@NotNull String query) throws ParserException;

  /**
   * Gets the first element that matches the specified {@code query} CSS selector or group of
   * selectors.
   * <p>
   * Matching is done in a depth-first traversal of nodes.
   *
   * @param query CSS Selector
   * @return Found element, or {@code null}, if no matching node was found
   * @throws ParserException If the {@code query} could not be parsed into a CSS selector
   */
  @Nullable Element querySelector(@NotNull String query) throws ParserException;
}

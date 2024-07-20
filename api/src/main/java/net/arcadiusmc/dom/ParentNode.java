package net.arcadiusmc.dom;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ParentNode {

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

  @NotNull List<Element> querySelectorAll(@NotNull String query);

  @Nullable Element querySelector(@NotNull String query);
}

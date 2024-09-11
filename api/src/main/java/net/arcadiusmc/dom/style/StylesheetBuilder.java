package net.arcadiusmc.dom.style;

import java.util.function.Consumer;
import net.arcadiusmc.dom.ParserException;
import org.jetbrains.annotations.NotNull;

/**
 * CSS Style sheet builder
 */
public interface StylesheetBuilder {

  /**
   * Adds a rule.
   *
   * @param selector Rule selector
   * @param consumer Rule builder
   *
   * @return {@code this}
   *
   * @throws ParserException If the selector is invalid.
   */
  StylesheetBuilder addRule(@NotNull String selector, @NotNull Consumer<StyleProperties> consumer)
      throws ParserException;

  /**
   * Builds the stylesheet.
   * @return Built sheet
   */
  Stylesheet build();
}

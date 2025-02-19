package net.arcadiusmc.dom;

import net.arcadiusmc.dom.style.Stylesheet;
import org.jetbrains.annotations.Nullable;

/**
 * Style element used to link SCSS stylesheets to a document.
 * <p>
 * If the {@link Attributes#SOURCE} is set, then the element will use
 * {@link net.arcadiusmc.delphi.resource.ViewResources#loadStylesheet(String)} to load it. Any
 * changes to the source attribute cause the stylesheet to be loaded again.
 * <p>
 * If the {@link Attributes#SOURCE} is not set, then the element's text content will be parsed
 * as a stylesheet, and changes to the element's content will cause the stylesheet to be parsed
 * again.
 *
 * @see Stylesheet
 * @see TagNames#STYLE
 */
public interface StyleElement extends Element {

  /**
   * Get the stylesheet that was created by this element.
   * <p>
   * Note that the stylesheet itself will only be loaded or parsed after the
   * style element has been added to a document's header. Before that, this
   * method will return {@code null}.
   *
   * @return Stylesheet
   */
  @Nullable Stylesheet getStylesheet();

  /**
   * Get the value of the {@link Attributes#SOURCE} attribute
   * @return Source attribute value
   */
  @Nullable String getSource();
}

package net.arcadiusmc.dom;

public interface Attr {
  /**
   * ID attribute.
   * @see Document#getElementById(String)
   */
  String ID = "id";

  /**
   * Class list attribute.
   */
  String CLASS = "class";

  /**
   * Inline style attribute.
   */
  String STYLE = "style";

  /**
   * Source attribute, used by {@code <script>}, {@code <style>} and {@code <item>} elements.
   */
  String SOURCE = "src";

  /**
   * Key attribute, used during parsing by the {@code <option>} element.
   */
  String KEY = "key";

  /**
   * Key attribute, used during parsing by the {@code <option>} element.
   */
  String VALUE = "value";

  /**
   * Enabled/disabled state attribute, used by {@code <button>} elements.
   * @see ButtonElement
   */
  String ENABLED = "enabled";

  /**
   * Item tooltip hide state attribute, used by {@code <item>} elements to determine
   * whether the item tooltip should be hidden or not.
   *
   * @see ItemElement
   */
  String ITEM_TOOLTIP_HIDE = "hide-item-tooltip";
}

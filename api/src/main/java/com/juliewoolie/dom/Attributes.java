package com.juliewoolie.dom;

import com.google.common.base.Strings;
import com.juliewoolie.delphi.util.Result;
import org.jetbrains.annotations.Nullable;

/**
 * Attribute name constants
 */
public interface Attributes {
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
   * Option name attribute, used during parsing by the {@code <option>} element.
   */
  String NAME = "name";

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

  /**
   * If item tooltips use advanced tooltips. (Including the {@code F3+H} debug information).
   * <p>
   * This attribute overrides the {@link Options#ADVANCED_ITEM_TOOLTIPS} option, if it is set.
   *
   * @see Options#ADVANCED_ITEM_TOOLTIPS
   */
  String ADVANCED_ITEM_TOOLTIPS = Options.ADVANCED_ITEM_TOOLTIPS;

  /**
   * Button action, executed when a {@link ButtonElement} button element is clicked. Only
   * works on button elements.
   * <br>
   * <table>
   *   <caption>Valid value patterns</caption>
   *   <tr>
   *     <th>Pattern</th>
   *     <th>Description</th>
   *     <th>Example</th>
   *   </tr>
   *   <tr>
   *     <td>{@code close}</td>
   *     <td>Closes the page</td>
   *     <td>{@code "close"}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code cmd: <command>}</td>
   *     <td>Runs a command as console. You can use {@code %player%} as a placeholder for the player's name</td>
   *     <td>{@code "cmd: msg %player% Hello, world!}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code player-cmd: <command>}</td>
   *     <td>Runs a command as the player. You can use {@code %player%} as a placeholder for the player's name</td>
   *     <td>{@code "player-cmd: msg %player% Hello, me!}</td>
   *   </tr>
   * </table>
   */
  String BUTTON_ACTION = "action";

  /**
   * Trigger for {@link #BUTTON_ACTION}.
   * <table>
   *   <caption>Supported values</caption>
   *   <thead>
   *     <tr>
   *       <th>Value</th>
   *       <th>Triggers</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td><code>left</code></td>
   *       <td>When button is left-clicked</td>
   *     </tr>
   *     <tr>
   *       <td><code>right</code></td>
   *       <td>When button is right-clicked</td>
   *     </tr>
   *   </tbody>
   * </table>
   */
  String ACTION_TRIGGER = "action-trigger";

  /**
   * Name of the linked java class in a {@link JavaObjectElement}
   */
  String CLASS_NAME = "class-name";

  /**
   * Type attribute.
   * <p>
   * Used in multiple places:
   * <ul>
   *   <li>On {@link ComponentElement}s to specify what syntax to use when loading component data.</li>
   *   <li>On {@link InputElement}s to specify what type of input they accept</li>
   * </ul>
   */
  String TYPE = "type";

  /**
   * Used to specify the placeholder on an {@link InputElement}
   */
  String PLACEHOLDER = "placeholder";

  /**
   * Defer attribute for delaying script execution until the DOM has finished loading
   * @see ScriptElement
   */
  String DEFER = "defer";

  /**
   * Attribute for defining the width in pixels of a canvas element.
   * @see CanvasElement
   */
  String WIDTH = "width";

  /**
   * Attribute for defining the height in pixels of a canvas element.
   * @see CanvasElement
   */
  String HEIGHT = "height";

  /**
   * Parses a float attribute value.
   * <p>
   * If the specified {@code value} cannot be parsed into a float, an erroneous result with the
   * error {@code "Invalid number"} is returned.
   * <p>
   * If the number is parsed successfully, then it is clamped according to the {@code min} and
   * {@code max} parameters.
   *
   * @param value Value to parse from
   * @param min Minimum value
   * @param max Maximum value
   *
   * @return Parsed float result
   */
  static Result<Float, String> floatAttribute(String value, float min, float max) {
    float f;

    try {
      f = Float.parseFloat(value);
    } catch (NumberFormatException exc) {
      return Result.err("Invalid number");
    }

    if (f < min) {
      return Result.ok(min);
    }
    if (f > max) {
      return Result.ok(max);
    }

    return Result.ok(f);
  }

  /**
   * Parses an attribute's string value into an integer.
   *
   * @param value Attribute value
   * @param min Minimum parsed value
   * @param max Maximum parsed value
   * @param fb Fallback value if attribute value is empty or null
   *
   * @return Clamped parsed value, or fallback
   */
  static int intAttribute(String value, int min, int max, int fb) {
    if (Strings.isNullOrEmpty(value)) {
      return fb;
    }

    try {
      return Math.clamp(Integer.parseInt(value), min, max);
    } catch (NumberFormatException exc) {
      return fb;
    }
  }

  static boolean boolAttribute(@Nullable String value, boolean fallback) {
    if (Strings.isNullOrEmpty(value)) {
      return fallback;
    }

    return switch (value.toLowerCase()) {
      case "true" -> true;
      case "false" -> false;
      default -> fallback;
    };
  }
}

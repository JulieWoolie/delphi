package com.juliewoolie.dom;

/**
 * Tag name constants
 */
public interface TagNames {

  /**
   * Body tag
   * @see BodyElement
   */
  String BODY = "body";

  /**
   * Button tag
   * @see ButtonElement
   */
  String BUTTON = "button";

  /**
   * Item tag
   * @see ItemElement
   */
  String ITEM = "item";

  /**
   * Item tooltip container tag.
   * <p>
   * Tag is created when a {@link ItemElement} generates a tooltip.
   */
  String ITEM_TOOLTIP = "item-tooltip";

  /**
   * Item tooltip name.
   * <p>
   * Used by {@link ItemElement} to display an item's name when creating
   * an item tooltip.
   */
  String ITEM_TOOLTIP_NAME = "item-tooltip-name";

  /**
   * Item tooltip line
   * <p>
   * Used by {@link ItemElement} to display each individual line of an item's
   * tooltip.
   */
  String ITEM_TOOLTIP_LINE = "item-tooltip-line";

  /**
   * Chat Component
   * @see ComponentElement
   */
  String COMPONENT = "chat-component";

  /**
   * Header element
   * @see HeadElement
   */
  String HEAD = "head";

  /**
   * Root document element
   */
  String ROOT = "delphi";

  /**
   * Style element
   * @see StyleElement
   */
  String STYLE = "style";

  /**
   * Option element
   * @see OptionElement
   */
  String OPTION = "option";

  /**
   * Java object element.
   * @see JavaObjectElement
   */
  String JAVA_OBJECT = "java-object";

  /**
   * Input element that can take in user input
   * @see InputElement
   */
  String INPUT = "input";

  /**
   * Script element
   * @see ScriptElement
   */
  String SCRIPT = "script";

  /**
   * Canvas element
   * @see CanvasElement
   */
  String CANVAS = "canvas";

  /**
   * Tooltip element.
   */
  String TOOLTIP = "tooltip";

  /**
   * Field set element
   * @see FieldSetElement
   */
  String FIELDSET = "fieldset";

  /**
   * Slider element
   * @see SliderElement
   */
  String SLIDER = "slider";
}

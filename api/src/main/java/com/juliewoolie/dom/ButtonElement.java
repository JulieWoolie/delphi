package com.juliewoolie.dom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Button elements play a sound when clicked
 */
public interface ButtonElement extends Element, Disableable {

  /**
   * Get the button trigger, used for {@link #getAction()}.
   * <p>
   * If the {@link Attributes#ACTION_TRIGGER} attribute is not set, this
   * will default to {@link ButtonTrigger#DEFAULT}.
   *
   * @return Button trigger
   */
  @NotNull
  ButtonTrigger getTrigger();

  /**
   * Set the button trigger, used for {@link #getAction()}.
   * <p>
   * Shorthand for setting the {@link Attributes#ACTION_TRIGGER} attribute
   * value.
   *
   * @param trigger Trigger value
   */
  void setTrigger(@Nullable ButtonTrigger trigger);

  /**
   * Get the button action, shorthand for accessing the {@link Attributes#BUTTON_ACTION}
   * attribute's value.
   * <p>
   * The returned action is executed when the button is clicked with the same
   * mouse button as {@link #getTrigger()}.
   *
   * @return Button action, or {@code null}, if the underlying attribute is
   *         not set.
   */
  @Nullable
  ButtonAction getAction();

  /**
   * Set the button action, shorthand for setting the {@link Attributes#BUTTON_ACTION}
   * attribute's value.
   * <p>
   * The set action is executed when the button is clicked with the same
   * mouse button as {@link #getTrigger()}.
   *
   * @param action Button action
   */
  void setAction(@Nullable ButtonAction action);

  /**
   * Button action trigger
   */
  enum ButtonTrigger {
    /** Execute when button is left-clicked */
    LEFT_CLICK ("left"),
    /** Execute when button is right-clicked */
    RIGHT_CLICK ("right"),
    ;

    public static ButtonTrigger DEFAULT = LEFT_CLICK;

    private final String attributeValue;

    ButtonTrigger(String attributeValue) {
      this.attributeValue = attributeValue;
    }

    public String getAttributeValue() {
      return attributeValue;
    }
  }

  /**
   * Button action type
   */
  enum ButtonActionType {
    /** Close the page */
    CLOSE ("close"),

    /** Execute a command as the player that clicked the button */
    PLAYER_COMMAND ("player-cmd"),

    /** Execute a command as console */
    CONSOLE_COMMAND ("cmd"),
    ;

    private final String attributeValue;

    ButtonActionType(String attributeValue) {
      this.attributeValue = attributeValue;
    }

    public String getAttributeValue() {
      return attributeValue;
    }
  }

  /**
   * Button action.
   * <p>
   * Note that in the case of a {@link ButtonActionType#CLOSE}, any
   * provided command will be ignored.
   * <table>
   *   <caption>Placeholders that can be used in the {@link #command} string</caption>
   *   <thead>
   *     <tr>
   *       <th>Placeholder</th>
   *       <th>Description</th>
   *       <th>Example</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>{@code %player%}</td>
   *       <td>Player name</td>
   *       <td>{@code JulieWoolie}</td>
   *     </tr>
   *   </tbody>
   * </table>
   * @param type Action type
   * @param command Action command
   */
  record ButtonAction(ButtonActionType type, String command) {

  }
}

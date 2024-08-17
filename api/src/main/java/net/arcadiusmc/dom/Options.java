package net.arcadiusmc.dom;

import net.arcadiusmc.delphi.Screen;

/**
 * Constants for options supported/reserved by the DOM
 */
public interface Options {

  /**
   * A space-separated list of plugins that must be enabled for a page to be successfully opened.
   * <p>
   * This option is validated during document parsing, and if any of the plugins are missing or not
   * enabled, the parsing stops and loading fails.
   * <p>
   * Example: {@code <option name="required-plugins" value="Essentials WorldEdit WorldGuard"/>}
   */
  String REQUIRED_PLUGINS = "required-plugins";

  /**
   * Screen width option. By default, the value of this option is {@link Screen#DEFAULT_WIDTH}
   * <p>
   * Modifying this option when a page is already open, will cause the screen's size to change
   * <p>
   * Example: {@code <option name="screen-width" value="3"/>}
   */
  String SCREEN_WIDTH = "screen-width";

  /**
   * Screen height option. By default, the value of this option is {@link Screen#DEFAULT_HEIGHT}
   * <p>
   * Modifying this option when a page is already open, will cause the screen's size to change
   * <p>
   * Example: {@code <option name="screen-height" value="2"/>}
   */
  String SCREEN_HEIGHT = "screen-height";

  /**
   * If item tooltips use advanced tooltips. (Including the {@code F3+H} debug information).
   * This option can also be set as an attribute on each {@code <item>} element.
   * <p>
   * By default, the value of this option is {@code "false"}
   * <p>
   * Example: {@code <option name="advanced-item-tooltips" value="true"/>}
   */
  String ADVANCED_ITEM_TOOLTIPS = "advanced-item-tooltips";

  /**
   * Ignore the pitch of the player when opening the screen. Setting this to {@code false} means
   * the screen will spawn in front of the player regardless of what direction they're looking in.
   * <p>
   * If the value is {@code true}, then the screen will be spawned in front of the player at their
   * eye level without any X-axis rotation.
   * <p>
   * By default, the value of the option is {@code true}
   * <p>
   * Example: {@code <option name="ignore-player-pitch" value="false" />}
   */
  String IGNORE_PLAYER_PITCH = "ignore-player-pitch";
}

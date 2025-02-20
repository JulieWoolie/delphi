package net.arcadiusmc.delphiplugin;

import com.google.common.base.Strings;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.ButtonElement;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public
class ButtonClickListener implements EventListener.Typed<MouseEvent> {

  static final String CLOSE = "close";
  static final String CMD = "cmd:";
  static final String PLAYER_CMD = "player-cmd:";

  boolean matchesTrigger(String trigger, MouseEvent event) {
    if (Strings.isNullOrEmpty(trigger)) {
      return event.getButton() == MouseButton.LEFT;
    }

    return switch (trigger) {
      case "left" -> event.getButton() == MouseButton.LEFT;
      case "right" -> event.getButton() == MouseButton.RIGHT;
      default -> false;
    };
  }

  @Override
  public void handleEvent(MouseEvent event) {
    Element p = event.getTarget();

    if (!event.isBubbling()) {
      if (!(p instanceof ButtonElement el)) {
        return;
      }

      tryRunButton(el, event);
      return;
    }

    while (p != null) {
      if (p instanceof ButtonElement element) {
        tryRunButton(element, event);
      }

      p = p.getParent();
    }
  }

  private void tryRunButton(ButtonElement target, MouseEvent event) {
    String action = target.getAttribute(Attributes.BUTTON_ACTION);
    if (Strings.isNullOrEmpty(action)) {
      return;
    }

    String trigger = target.getAttribute(Attributes.ACTION_TRIGGER);
    if (!matchesTrigger(trigger, event)) {
      return;
    }

    if (action.equalsIgnoreCase(CLOSE)) {
      event.stopPropagation();
      event.preventDefault();
      event.getDocument().getView().close();
      return;
    }

    Player player = event.getPlayer();

    if (action.startsWith(CMD)) {
      runCommand(player, Bukkit.getConsoleSender(), CMD, action);
      return;
    }
    if (action.startsWith(PLAYER_CMD)) {
      runCommand(player, player, PLAYER_CMD, action);
    }
  }

  private void runCommand(Player player, CommandSender sender, String prefix, String cmd) {
    String formatted = cmd.substring(prefix.length())
        .trim()
        .replace("%player%", player.getName());

    Bukkit.dispatchCommand(sender, formatted);
  }
}

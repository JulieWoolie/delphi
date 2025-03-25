package net.arcadiusmc.delphiplugin;

import com.google.common.base.Strings;
import net.arcadiusmc.dom.ButtonElement;
import net.arcadiusmc.dom.ButtonElement.ButtonAction;
import net.arcadiusmc.dom.ButtonElement.ButtonTrigger;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public
class ButtonClickListener implements EventListener.Typed<MouseEvent> {

  boolean matchesTrigger(@NotNull ButtonTrigger trigger, MouseEvent event) {
    return switch (trigger) {
      case LEFT_CLICK -> event.getButton() == MouseButton.LEFT;
      case RIGHT_CLICK -> event.getButton() == MouseButton.RIGHT;
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
    if (!target.isEnabled()) {
      return;
    }

    ButtonTrigger trigger = target.getTrigger();
    if (!matchesTrigger(trigger, event)) {
      return;
    }

    ButtonAction action = target.getAction();
    if (action == null) {
      return;
    }

    Player player = event.getPlayer();

    switch (action.type()) {
      case CLOSE -> {
        event.stopPropagation();
        event.preventDefault();
        event.getDocument().getView().close();
      }

      case PLAYER_COMMAND -> {
        runCommand(player, player, action.command());
      }
      case CONSOLE_COMMAND -> {
        runCommand(player, Bukkit.getConsoleSender(), action.command());
      }
    }
  }

  private void runCommand(Player player, CommandSender sender, String cmd) {
    if (Strings.isNullOrEmpty(cmd)) {
      return;
    }

    String formatted = cmd.replace("%player%", player.getName());
    Bukkit.dispatchCommand(sender, formatted);
  }
}

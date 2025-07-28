package com.juliewoolie.hephaestus.stdlib;

import com.juliewoolie.hephaestus.Scripting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public enum SendMessageFunction implements ProxyExecutable {
  CHAT,
  ACTIONBAR,
  ;

  @Override
  public Object execute(Value... arguments) {
    if (arguments.length < 2) {
      return false;
    }

    Player player = arguments[0].as(Player.class);
    if (player == null) {
      return false;
    }

    Component message;

    if (arguments.length == 2) {
      message = Scripting.toComponent(arguments[1], player);
    } else {
      Builder builder = Component.text();
      for (int i = 1; i < arguments.length; i++) {
        Component component = Scripting.toComponent(arguments[i], player);
        builder.append(component);
      }
      message = builder.build();
    }

    if (this == ACTIONBAR) {
      player.sendActionBar(message);
    } else {
      player.sendMessage(message);
    }

    return true;
  }
}

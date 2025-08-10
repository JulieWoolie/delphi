package com.juliewoolie.delphiplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;

public class TextUtil {

  public static Component translate(Player aud, String transKey) {
    return GlobalTranslator.render(Component.translatable(transKey), aud.locale());
  }
}

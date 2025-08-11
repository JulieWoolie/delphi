package com.juliewoolie.delphiplugin;

import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;

public class TextUtil {

  public static Component translate(Player aud, String transKey) {
    return translate(aud.locale(), transKey);
  }

  public static String translateToString(Player aud, String transKey) {
    return translateToString(aud.locale(), transKey);
  }

  public static Component translate(Locale l, String transKey) {
    return GlobalTranslator.render(Component.translatable(transKey), l);
  }

  public static Component translate(Locale l, String transKey, Component... args) {
    return GlobalTranslator.render(Component.translatable(transKey, args), l);
  }

  public static String translateToString(Locale l, String transKey) {
    return PlainTextComponentSerializer.plainText().serialize(translate(l, transKey));
  }

  public static String translateToString(Locale l, String transKey, Component... args) {
    return PlainTextComponentSerializer.plainText().serialize(translate(l, transKey, args));
  }
}

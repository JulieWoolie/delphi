package net.arcadiusmc.delphirender;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class TextUtil {
  private TextUtil() {}

  public static final ComponentFlattener FLATTENER = Bukkit.getUnsafe().componentFlattener();

  public static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.builder()
      .flattener(FLATTENER)
      .build();

  public static boolean isEmpty(ComponentLike like) {
    return like == null
        || Objects.equals(like.asComponent(), Component.empty())
        || plain(like).isEmpty();
  }

  /**
   * Renders the given component to a plain string
   *
   * @param text The text to render
   * @return The plain string version of the given text
   */
  @Contract("null -> null")
  public static String plain(@Nullable ComponentLike text) {
    if (text == null) {
      return null;
    }
    return PLAIN.serialize(text.asComponent());
  }
}

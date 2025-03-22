package net.arcadiusmc.delphirender;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

public class SimpleTextMeasure extends TextMeasure {

  private static final MinecraftFont FONT = MinecraftFont.Font;

  float getWidth(char ch) {
    CharacterSprite sprite = FONT.getChar(ch);

    if (sprite != null) {
      return sprite.getWidth();
    }

    return switch (ch) {
      case '!', '\'', ',', '.', ':', ';', 'i', '|' -> 1;
      case '`', 'l' -> 2;
      case '"', '(', ')', '*', 'I', '[', ']', 't', '{', '}', ' ' -> 3;
      case '<', '>', 'f', 'k' -> 4;
      case '@', '~', '✔' -> 6;
      case '✖' -> 7;
      default -> 5;
    };
  }

  @Override
  public void component(@NotNull String text) {
    text = removeColorCodes(text);

    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);

      lineChars++;

      if (ch == '\n') {
        onLineBreak();
        continue;
      }

      if (lineChars > 0) {
        incWidth(1);
      }

      float w = getWidth(ch);
      incWidth(w);
      charHeight(8f, 2f);

      if (style.hasDecoration(TextDecoration.BOLD)) {
        incWidth(1);
      }
    }
  }
}

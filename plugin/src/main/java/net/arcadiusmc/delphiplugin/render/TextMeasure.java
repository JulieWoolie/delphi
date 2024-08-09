package net.arcadiusmc.delphiplugin.render;

import java.util.Stack;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

public class TextMeasure implements FlattenerListener {

  private static final MinecraftFont FONT = MinecraftFont.Font;

  int lineBreaks = 0;
  int longestLine = 0;
  int lineWidth = 0;
  int lineChars = 0 ;
  int totalChars = 0;

  final Stack<Style> styleStack = new Stack<>();
  protected Style style = Style.empty();

  int getWidth(char ch) {
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
  public void pushStyle(@NotNull Style style) {
    styleStack.push(style);
    reconfigureStyle();
  }

  @Override
  public void component(@NotNull String text) {
    for (int i = 0; i < text.length(); i++) {
      char ch = text.charAt(i);

      totalChars++;
      lineChars++;

      if (ch == '\n') {
        longestLine = Math.max(lineWidth, longestLine);
        lineWidth = 0;
        lineChars = 0;
        lineBreaks++;

        continue;
      }

      if (lineChars > 0) {
        incWidth(1);
      }

      int w = getWidth(ch);
      incWidth(w);

      if (style.hasDecoration(TextDecoration.BOLD)) {
        incWidth(1);
      }
    }
  }

  void incWidth(float inc) {
    lineWidth += inc;
    longestLine = Math.max(lineWidth, longestLine);
  }

  @Override
  public void popStyle(@NotNull Style style) {
    styleStack.pop();
    reconfigureStyle();
  }

  private void reconfigureStyle() {
    if (styleStack.isEmpty()) {
      style = Style.empty();
    }

    Style.Builder builder = Style.style();

    for (var s : styleStack) {
      builder.merge(s, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
    }

    style = builder.build();
  }
}

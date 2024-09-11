package net.arcadiusmc.delphiplugin.render;

import static net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE;
import static net.arcadiusmc.delphiplugin.render.RenderObject.NIL_COLOR;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector2f;

public abstract class TextContent implements ElementContent {

  // 8 for character + 2 for descender space
  public static final float LINE_HEIGHT = CHAR_PX_SIZE * (8 + 2);

  @Override
  public Display createEntity(World world, Location location) {
    return world.spawn(location, TextDisplay.class, d -> d.setBackgroundColor(NIL_COLOR));
  }

  protected abstract Component getBaseText();

  protected boolean overrideStyle() {
    return true;
  }

  private Component resolve(FullStyle set) {
    Component base = getBaseText();

    if (TextUtil.isEmpty(base)) {
      return Component.empty();
    }

    Component result;
    boolean override = overrideStyle();
    TextColor textColor = set.textColor == null
        ? NamedTextColor.BLACK
        : TextUtil.toTextColor(set.textColor);

    if (override) {
      result = base.color(textColor);
    } else {
      result = base.colorIfAbsent(textColor);
    }

    result = applyDecoration(result, TextDecoration.BOLD, override, set.bold);
    result = applyDecoration(result, TextDecoration.ITALIC, override, set.italic);
    result = applyDecoration(result, TextDecoration.UNDERLINED, override, set.underlined);
    result = applyDecoration(result, TextDecoration.STRIKETHROUGH, override, set.strikethrough);
    result = applyDecoration(result, TextDecoration.OBFUSCATED, override, set.obfuscated);

    return result;
  }

  private Component applyDecoration(
      Component text,
      TextDecoration deco,
      boolean override,
      boolean state
  ) {
    if (override) {
      return text.decoration(deco, State.byBoolean(state));
    }

    return text.decorationIfAbsent(deco, State.byBoolean(state));
  }

  @Override
  public void applyContentTo(Display entity, FullStyle set) {
    TextDisplay display = (TextDisplay) entity;

    Component text = resolve(set);
    display.text(text);
  }

  @Override
  public Class<? extends Display> getEntityClass() {
    return TextDisplay.class;
  }

  @Override
  public void measureContent(Vector2f out, FullStyle set) {
    if (isEmpty()) {
      out.set(0);
      return;
    }

    Component text = resolve(set);
    TextMeasure measure = new TextMeasure();
    TextUtil.FLATTENER.flatten(text, measure);

    int lines = measure.lineBreaks;
    if (measure.totalChars > 0) {
      lines++;
    }

    out.x = (measure.longestLine) * CHAR_PX_SIZE;
    out.y = lines * LINE_HEIGHT;
  }

  @Override
  public boolean isEmpty() {
    return TextUtil.isEmpty(getBaseText());
  }

  @Override
  public void configureInitial(Layer layer, RenderObject element) {

  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + TextUtil.plain(getBaseText()).replace("\n", "\\n") + "]";
  }
}

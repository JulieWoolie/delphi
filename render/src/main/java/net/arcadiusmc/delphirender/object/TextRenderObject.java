package net.arcadiusmc.delphirender.object;

import static net.arcadiusmc.delphirender.object.BoxRenderObject.NIL_COLOR;

import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.delphirender.layout.NLayout;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class TextRenderObject extends SingleEntityRenderObject<TextDisplay> {

  public TextRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected TextDisplay spawnEntity(World w, Location l) {
    return w.spawn(l, TextDisplay.class);
  }

  @Override
  protected void configure(TextDisplay entity, Transformation trans) {
    Component text = text();
    entity.text(text);
    entity.setBackgroundColor(NIL_COLOR);

    FullStyle style = getParentStyle();
    if (style != null) {
      entity.setShadowed(style.textShadowed);
    }

    configureTextSize(this, text, trans.getScale());
  }

  static void configureTextSize(TextRenderObject holder, Component text, Vector3f scale) {
    Vector2f textSize = new Vector2f();
    NLayout.measureText(holder, text, textSize);

    Vector2f objectSize = holder.size;

    scale.x = objectSize.x / textSize.x;
    scale.y = objectSize.y / textSize.y;
  }

  protected abstract Component baseText();

  public Component text() {
    Component text = this.baseText();
    FullStyle style = getParentStyle();

    if (style != null) {
      if (style.textColor != null) {
        text = text.colorIfAbsent(style.textColor);
      }
      if (style.bold) {
        text = text.decorationIfAbsent(TextDecoration.BOLD, State.TRUE);
      }
      if (style.italic) {
        text = text.decorationIfAbsent(TextDecoration.ITALIC, State.TRUE);
      }
      if (style.underlined) {
        text = text.decorationIfAbsent(TextDecoration.UNDERLINED, State.TRUE);
      }
      if (style.strikethrough) {
        text = text.decorationIfAbsent(TextDecoration.STRIKETHROUGH, State.TRUE);
      }
      if (style.obfuscated) {
        text = text.decorationIfAbsent(TextDecoration.OBFUSCATED, State.TRUE);
      }
    }

    return text;
  }
}

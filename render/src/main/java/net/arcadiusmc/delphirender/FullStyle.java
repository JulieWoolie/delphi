package net.arcadiusmc.delphirender;

import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.joml.Vector2f;

public class FullStyle {

  public static final float UNSET = -1.0f;

  public final Rect padding = new Rect();
  public final Rect border = new Rect();
  public final Rect outline = new Rect();
  public final Rect margin = new Rect();

  public TextColor textColor = toTextColor(Properties.COLOR.getDefaultValue());
  public Color backgroundColor = toBukkitColor(Properties.BACKGROUND_COLOR.getDefaultValue());
  public Color borderColor = toBukkitColor(Properties.BORDER_COLOR.getDefaultValue());
  public Color outlineColor = toBukkitColor(Properties.OUTLINE_COLOR.getDefaultValue());

  public boolean textShadowed;
  public boolean bold;
  public boolean italic;
  public boolean underlined;
  public boolean strikethrough;
  public boolean obfuscated;

  public DisplayType display = DisplayType.DEFAULT;

  public final Vector2f scale = new Vector2f(UNSET);
  public final Vector2f size = new Vector2f(UNSET);
  public final Vector2f minSize = new Vector2f(UNSET);
  public final Vector2f maxSize = new Vector2f(UNSET);

  public int zindex = 0;
  public AlignItems alignItems = AlignItems.DEFAULT;
  public FlexDirection flexDirection = FlexDirection.DEFAULT;
  public FlexWrap flexWrap = FlexWrap.DEFAULT;
  public JustifyContent justify = JustifyContent.DEFAULT;
  public int order = 0;

  public static Color toBukkitColor(net.arcadiusmc.dom.style.Color c) {
    return Color.fromARGB(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
  }

  public static TextColor toTextColor(net.arcadiusmc.dom.style.Color color) {
    return TextColor.color(color.rgb());
  }
}

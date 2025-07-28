package com.juliewoolie.delphirender;

import com.juliewoolie.chimera.Properties;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.dom.style.AlignItems;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.FlexWrap;
import com.juliewoolie.dom.style.JustifyContent;
import com.juliewoolie.dom.style.Visibility;
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

  public final Vector2f size = new Vector2f(UNSET);
  public final Vector2f minSize = new Vector2f(UNSET);
  public final Vector2f maxSize = new Vector2f(UNSET);

  public float marginInlineStart = 0.0f;
  public float marginInlineEnd = 0.0f;
  public float fontSize = 1.0f;

  public int zindex = 0;
  public AlignItems alignItems = AlignItems.DEFAULT;
  public FlexDirection flexDirection = FlexDirection.DEFAULT;
  public FlexWrap flexWrap = FlexWrap.DEFAULT;
  public JustifyContent justify = JustifyContent.DEFAULT;
  public int order = 0;
  public BoxSizing boxSizing = BoxSizing.DEFAULT;
  public Visibility visibility = Visibility.DEFAULT;

  public static Color toBukkitColor(com.juliewoolie.dom.style.Color c) {
    return Color.fromARGB(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
  }

  public static TextColor toTextColor(com.juliewoolie.dom.style.Color color) {
    return TextColor.color(color.rgb());
  }
}

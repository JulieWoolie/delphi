package com.juliewoolie.delphirender;

import com.juliewoolie.chimera.Properties;
import com.juliewoolie.nlayout.LayoutStyle;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.block.data.BlockData;

public class FullStyle extends LayoutStyle {

  public static final float UNSET = -1.0f;

  public TextColor textColor = toTextColor(Properties.COLOR.getDefaultValue());
  public Color backgroundColor = toBukkitColor(Properties.BACKGROUND_COLOR.getDefaultValue());
  public Color borderColor = toBukkitColor(Properties.BORDER_COLOR.getDefaultValue());
  public Color outlineColor = toBukkitColor(Properties.OUTLINE_COLOR.getDefaultValue());

  public BlockData backgroundBlock = null;
  public BlockData borderBlock = null;
  public BlockData outlineBlock = null;

  public boolean textShadowed;
  public boolean bold;
  public boolean italic;
  public boolean underlined;
  public boolean strikethrough;
  public boolean obfuscated;

  public int zindex = 0;

  public static Color toBukkitColor(com.juliewoolie.dom.style.Color c) {
    return Color.fromARGB(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
  }

  public static TextColor toTextColor(com.juliewoolie.dom.style.Color color) {
    return TextColor.color(color.rgb());
  }
}

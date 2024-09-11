package net.arcadiusmc.delphiplugin.render;

import net.arcadiusmc.chimera.Properties;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import org.joml.Vector2f;

public class FullStyle {

  public final Rect padding = new Rect();
  public final Rect border = new Rect();
  public final Rect outline = new Rect();
  public final Rect margin = new Rect();

  public Color textColor = Properties.COLOR.getDefaultValue();
  public Color backgroundColor = Properties.BACKGROUND_COLOR.getDefaultValue();
  public Color borderColor = Properties.BORDER_COLOR.getDefaultValue();
  public Color outlineColor = Properties.OUTLINE_COLOR.getDefaultValue();

  public boolean textShadowed;
  public boolean bold;
  public boolean italic;
  public boolean underlined;
  public boolean strikethrough;
  public boolean obfuscated;

  public DisplayType display = DisplayType.DEFAULT;

  public final Vector2f scale = new Vector2f();
  public final Vector2f setSize = new Vector2f();
  public final Vector2f minSize = new Vector2f();
  public final Vector2f maxSize = new Vector2f();

  public int zindex = 0;
  public AlignItems alignItems = AlignItems.DEFAULT;
  public FlexDirection flexDirection = FlexDirection.DEFAULT;
  public FlexWrap flexWrap = FlexWrap.DEFAULT;
  public JustifyContent justify = JustifyContent.DEFAULT;
  public int order = 0;
}

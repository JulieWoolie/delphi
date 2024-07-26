package net.arcadiusmc.delphi.dom.scss;

import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphi.dom.Rect;
import net.arcadiusmc.delphi.dom.scss.Property.StyleFunction;
import net.arcadiusmc.delphi.dom.scss.PropertySet.RuleIterator;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import org.joml.Vector2f;

public class ComputedStyle {
  public final Rect padding = new Rect();
  public final Rect border = new Rect();
  public final Rect outline = new Rect();
  public final Rect margin = new Rect();

  public Color textColor;
  public Color backgroundColor;
  public Color borderColor;
  public Color outlineColor;

  public boolean textShadowed;
  public boolean bold;
  public boolean italic;
  public boolean underlined;
  public boolean strikethrough;
  public boolean obfuscated;

  public DisplayType display;

  public final Vector2f scale = new Vector2f();
  public final Vector2f minSize = new Vector2f();
  public final Vector2f maxSize = new Vector2f();

  public int zindex;

  public ComputedStyle() {
    clear(null);
  }

  public void clear(Screen screen) {
    for (int i = 0; i < Properties.count(); i++) {
      Property<Object> property = Properties.getById(i);
      StyleFunction<Object> func = property.getApplicator();

      if (func == null) {
        continue;
      }

      func.apply(this, screen, property.getDefaultValue());
    }
  }

  public void putAll(PropertySet set, Screen screen) {
    RuleIterator it = set.iterator();

    while (it.hasNext()) {
      it.next();

      Property<Object> prop = it.property();
      Object v = it.value();

      StyleFunction<Object> function = prop.getApplicator();

      if (function != null) {
        function.apply(this, screen, v);
      }
    }
  }
}

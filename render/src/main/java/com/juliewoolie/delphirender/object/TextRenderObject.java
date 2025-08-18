package com.juliewoolie.delphirender.object;

import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_X;
import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_Y;
import static com.juliewoolie.delphirender.object.BoxRenderObject.NIL_COLOR;

import com.juliewoolie.delphirender.FontMeasureCallback;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.MetricTextMeasure;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.delphirender.SimpleTextMeasure;
import com.juliewoolie.delphirender.TextMeasure;
import com.juliewoolie.delphirender.TextUtil;
import com.juliewoolie.nlayout.MeasureFunc;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class TextRenderObject
    extends SingleEntityRenderObject<TextDisplay>
    implements MeasureFunc
{
  public static final float GLOBAL_FONT_SIZE = 0.5f;

  public TextRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected TextDisplay spawnEntity(World w, Location l) {
    return w.spawn(l, TextDisplay.class, txt -> {
      txt.setBackgroundColor(NIL_COLOR);
      txt.setLineWidth(Integer.MAX_VALUE);
    });
  }

  @Override
  protected void configure(TextDisplay entity, Transformation trans) {
    Component text = text();
    entity.text(text);
    entity.setLineWidth(Integer.MAX_VALUE);

    FullStyle style = getParentStyle();
    if (style != null) {
      entity.setShadowed(style.textShadowed);
    }

    configureTextSize(this, text, trans.getScale());

    trans.getTranslation().x -= BoxRenderObject.visualCenterOffset(trans.getScale().x);
  }

  @Override
  public void measure(Vector2f out) {
    measureText(this, text(), out);
    out.mul(GLOBAL_FONT_SIZE);

    if (parent != null) {
      out.mul(parent.style.fontSize);
    }
  }

  static void measureText(TextRenderObject obj, Component text, Vector2f out) {
    TextMeasure measure;
    FontMeasureCallback metrics = obj.system.getFontMetrics();

    if (metrics == null) {
      measure = new SimpleTextMeasure();
    } else {
      measure = new MetricTextMeasure(metrics);
    }

    TextUtil.FLATTENER.flatten(text, measure);

    measure.outputSize(out);

    out.x *= CHAR_PX_SIZE_X;
    out.y *= CHAR_PX_SIZE_Y;
  }

  static void configureTextSize(TextRenderObject holder, Component text, Vector3f scale) {
    Vector2f textSize = new Vector2f();
    measureText(holder, text, textSize);

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

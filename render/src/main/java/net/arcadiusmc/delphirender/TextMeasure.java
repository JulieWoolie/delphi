package net.arcadiusmc.delphirender;

import it.unimi.dsi.fastutil.floats.FloatArrays;
import java.util.Stack;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public abstract class TextMeasure implements FlattenerListener {

  protected Style style = Style.empty();
  private Stack<Style> styleStack = new Stack<>();

  protected float currentLineWidth = 0.0f;
  protected float currentLineHeight = 0.0f;
  protected float currentLineDescender = 0.0f;
  protected int lineChars = 0;
  private int lineCount = 0;

  protected float[] lineWidths = new float[1];
  protected float[] lineHeights = new float[1];
  protected float[] lineDescenders = new float[1];

  public void outputSize(Vector2f out) {
    if (lineChars > 0) {
      pushLine();
    }

    out.x = 0;
    out.y = 0;

    for (float lineWidth : lineWidths) {
      out.x = Math.max(lineWidth, out.x);
    }

    for (float height : lineHeights) {
      out.y += height + 1;
    }
    for (float lineDescender : lineDescenders) {
      out.y += lineDescender;
    }
  }

  protected void onLineBreak() {
    pushLine();

    lineCount++;

    lineChars = 0;
    currentLineWidth = 0;
    currentLineHeight = 0;
    currentLineDescender = 0;
  }

  protected void pushLine() {
    lineHeights = FloatArrays.ensureCapacity(lineHeights, lineCount + 1);
    lineWidths = FloatArrays.ensureCapacity(lineWidths, lineCount + 1);
    lineDescenders = FloatArrays.ensureCapacity(lineDescenders, lineCount + 1);

    lineHeights[lineCount] = currentLineHeight;
    lineDescenders[lineCount] = currentLineDescender;
    lineWidths[lineCount] = currentLineWidth;
  }

  protected final void charHeight(float h, float desc) {
    if (desc > currentLineDescender) {
      currentLineDescender = desc;
    }

    if (h > currentLineHeight) {
      currentLineHeight = h;
    }
  }

  protected final void incWidth(float w) {
    currentLineWidth += w;
  }

  @Override
  public void popStyle(@NotNull Style style) {
    styleStack.pop();
    reconfigureStyle();
  }

  @Override
  public void pushStyle(@NotNull Style style) {
    styleStack.push(style);
    reconfigureStyle();
  }

  private void reconfigureStyle() {
    if (styleStack.isEmpty()) {
      style = Style.empty();
    }

    Style.Builder builder = Style.style();

    for (Style s : styleStack) {
      builder.merge(s, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
    }

    style = builder.build();
  }
}

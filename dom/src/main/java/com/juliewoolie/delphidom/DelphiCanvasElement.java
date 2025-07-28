package com.juliewoolie.delphidom;

import static com.juliewoolie.dom.Attributes.intAttribute;

import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Canvas;
import com.juliewoolie.dom.CanvasElement;
import com.juliewoolie.dom.TagNames;
import org.jetbrains.annotations.NotNull;

public class DelphiCanvasElement extends DelphiElement implements CanvasElement {

  public final DelphiCanvas canvas;

  public DelphiCanvasElement(DelphiDocument document) {
    super(document, TagNames.CANVAS);
    this.canvas = new DelphiCanvas(this);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public int getWidth() {
    return intAttribute(getAttribute(Attributes.WIDTH), MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
  }

  @Override
  public void setWidth(int width) {
    if (width < 0 || width > MAX_SIZE) {
      throw new IllegalArgumentException("Width cannot be less than 0 or greater than " + MAX_SIZE);
    }
    setAttribute(Attributes.WIDTH, String.valueOf(width));
  }

  @Override
  public int getHeight() {
    return intAttribute(getAttribute(Attributes.HEIGHT), MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
  }

  @Override
  public void setHeight(int height) {
    if (height < 0 || height > MAX_SIZE) {
      throw new IllegalArgumentException("Height cannot be less than 0 or greater than " + MAX_SIZE);
    }
    setAttribute(Attributes.HEIGHT, String.valueOf(height));
  }

  @Override
  public @NotNull Canvas getCanvas() {
    return canvas;
  }
}

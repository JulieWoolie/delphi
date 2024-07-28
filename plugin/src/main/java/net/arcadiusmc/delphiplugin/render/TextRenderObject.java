package net.arcadiusmc.delphiplugin.render;

import lombok.Getter;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphi.dom.Text;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import org.joml.Vector2f;

public class TextRenderObject extends RenderObject {

  @Getter
  private ElementContent content;
  private boolean contentDirty = false;

  public TextRenderObject(Text node, Screen screen) {
    super(node, screen);
  }

  public void setContent(ElementContent content) {
    this.content = content;
    this.contentDirty = true;
  }

  @Override
  public void moveTo(Vector2f screenPosition) {

  }

  @Override
  public void getSize(Vector2f out) {

  }

  @Override
  public void getBounds(Rectangle rectangle) {

  }

  @Override
  public void spawn() {

  }
}

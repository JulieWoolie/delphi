package net.arcadiusmc.delphiplugin.render;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import org.joml.Vector2f;

public class ElementRenderObject extends RenderObject {

  @Getter
  private final List<RenderObject> children = new ArrayList<>();

  private ElementContent content;

  public ElementRenderObject(DelphiNode node, Screen screen) {
    super(node, screen);
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

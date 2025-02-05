package net.arcadiusmc.delphirender.object;

import net.arcadiusmc.delphirender.RenderSystem;
import net.kyori.adventure.text.Component;

public class ComponentRenderObject extends TextRenderObject  {

  public Component text;

  public ComponentRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected Component baseText() {
    return text;
  }
}

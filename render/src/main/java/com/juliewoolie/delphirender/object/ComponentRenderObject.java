package com.juliewoolie.delphirender.object;

import com.juliewoolie.delphirender.RenderSystem;
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

package com.juliewoolie.delphirender.object;

import com.google.common.base.Strings;
import com.juliewoolie.delphirender.RenderSystem;
import net.kyori.adventure.text.Component;

public class StringRenderObject extends TextRenderObject {

  public String content;

  public StringRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected Component baseText() {
    if (Strings.isNullOrEmpty(content)) {
      return Component.empty();
    }

    return Component.text(content);
  }
}

package net.arcadiusmc.delphirender.content;

import java.util.Objects;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class ComponentContent extends TextContent {

  private final Component baseText;

  public ComponentContent(Component baseText) {
    Objects.requireNonNull(baseText, "null base text");
    this.baseText = baseText;
  }

  @Override
  protected boolean overrideStyle() {
    return false;
  }
}

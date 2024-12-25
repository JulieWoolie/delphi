package net.arcadiusmc.delphirender.tree;

import lombok.Getter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.delphirender.content.ElementContent;
import org.bukkit.Location;

@Getter
public class ContentRenderElement extends RenderElement {

  private ElementContent content = null;
  private boolean contentDirty = false;

  public ContentRenderElement(RenderSystem system, ComputedStyleSet styleSet) {
    super(system, styleSet);
  }

  public void setContent(ElementContent content) {
    this.content = content;
    this.contentDirty = true;
  }

  @Override
  protected void spawnContent(Location location) {

  }
}

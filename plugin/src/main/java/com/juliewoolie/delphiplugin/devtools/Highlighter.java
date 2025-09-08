package com.juliewoolie.delphiplugin.devtools;

import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphiplugin.math.Screen;
import com.juliewoolie.delphirender.Consts;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.dom.Node;
import org.bukkit.Color;
import org.bukkit.World;

public class Highlighter {

  static final float DEPTH_OFFSET = Consts.MACRO_LAYER_DEPTH * 35.0f;

  private final ElementHighlight margin;
  private final ElementHighlight outline;
  private final ElementHighlight border;
  private final ElementHighlight padding;
  private final ElementHighlight background;

  private final RenderSystem system;

  private Node current = null;

  Highlighter(World world, Screen screen, RenderSystem system, PageView devtoolsView) {
    this.margin = new ElementHighlight(screen, devtoolsView);
    this.outline = new ElementHighlight(screen, devtoolsView);
    this.border = new ElementHighlight(screen, devtoolsView);
    this.padding = new ElementHighlight(screen, devtoolsView);
    this.background = new ElementHighlight(screen, devtoolsView);

    this.system = system;

    this.margin.color = Color.ORANGE;
    this.outline.color = Color.YELLOW;
    this.border.color = Color.GREEN;
    this.padding.color = Color.AQUA;
    this.background.color = Color.BLUE;

    updateWorld(world);
  }

  public void updateWorld(World world) {
    this.margin.world = world;
    this.outline.world = world;
    this.border.world = world;
    this.padding.world = world;
    this.background.world = world;
  }

  public void update() {
    margin.spawn();
    outline.spawn();
    border.spawn();
    padding.spawn();
    background.spawn();
  }

  public void highlight(Node node) {
    if (current == node) {
      return;
    }

    if (node == null) {
      current = null;
      kill();
      return;
    }

    RenderObject ro = system.getRenderElement(node);
    if (ro == null) {
      return;
    }

    current = node;

    if (!(ro instanceof ElementRenderObject ero)) {
      margin.size.set(0);
      outline.size.set(0);
      border.size.set(0);
      padding.size.set(0);

      background.size.set(ro.size);
      background.position.set(ro.position);
      background.rect.set(1.0f);

      update();
      return;
    }

    FullStyle style = ero.style;

    // Margin
    margin.position.set(ero.position);
    margin.size.set(ero.size);
    margin.position.x -= style.margin.left;
    margin.position.y += style.margin.top;
    margin.size.x += style.margin.x();
    margin.size.y += style.margin.y();
    margin.depth = ero.depth + ero.getZIndexDepth() + DEPTH_OFFSET;
    margin.rect.set(style.margin);

    // Outline
    outline.position.set(ero.position);
    outline.size.set(ero.size);
    outline.depth = margin.depth + Consts.MICRO_LAYER_DEPTH;
    outline.rect.set(style.outline);

    // Border
    border.position.set(outline.position);
    border.size.set(outline.size);
    border.position.x += style.outline.left;
    border.position.y -= style.outline.top;
    border.size.x -= style.outline.x();
    border.size.y -= style.outline.y();
    border.depth = outline.depth + Consts.MICRO_LAYER_DEPTH;
    border.rect.set(style.border);

    // Padding
    padding.position.set(border.position);
    padding.size.set(border.size);
    padding.position.x += style.border.left;
    padding.position.y -= style.border.top;
    padding.size.x -= style.border.x();
    padding.size.y -= style.border.y();
    padding.depth = border.depth + Consts.MICRO_LAYER_DEPTH;
    padding.rect.set(style.padding);

    // Background
    background.size.set(padding.size);
    background.position.set(padding.position);
    background.position.x += style.padding.left;
    background.position.y -= style.padding.top;
    background.size.x -= style.padding.x();
    background.size.y -= style.padding.y();
    background.depth = padding.depth + Consts.MICRO_LAYER_DEPTH;
    background.rect.set(1.0f); // Always 1.0 to force it to spawn

    update();
  }

  public void kill() {
    margin.kill();
    outline.kill();
    border.kill();
    padding.kill();
    background.kill();
  }
}

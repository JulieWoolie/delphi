package net.arcadiusmc.delphiplugin.render;

import java.util.Set;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.XmlPrintVisitor;
import net.arcadiusmc.delphidom.scss.Property;
import net.arcadiusmc.delphidom.scss.PropertySet;
import net.arcadiusmc.delphidom.scss.PropertySet.RuleIterator;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TextNode;
import org.joml.Vector2f;

public class RenderTreePrint extends XmlPrintVisitor {

  static final String COMMENT_START = "<!--";
  static final String COMMENT_END = "-->";

  private final PageView view;

  public RenderTreePrint(PageView view) {
    this.view = view;
  }

  public void appendDocumentInfo() {
    nlIndent().append(COMMENT_START);
    indent++;

    nlIndent().append("player-name: ").append(view.getPlayer().getName());
    nlIndent().append("world: ").append(view.getWorld().getName());
    nlIndent().append("render-object-count: ").append(view.getRenderObjects().size());
    nlIndent().append("module-name: ").append(view.getResources().getModuleName());
    nlIndent().append("resource-path: ").append(view.getPath());

    Screen screen = view.getScreen();
    nlIndent().append("screen:");
    indent++;
    nlIndent().append("center: ").append(screen.center());
    nlIndent().append("normal: ").append(screen.normal());
    nlIndent().append("rotation: ").append(screen.getRotation());
    nlIndent().append("width: ").append(screen.getWidth());
    nlIndent().append("height: ").append(screen.getHeight());
    indent--;

    DelphiDocument doc = view.getDocument();
    final Set<String> optionKeys = doc.getOptionKeys();
    if (!optionKeys.isEmpty()) {
      nlIndent().append("document-options:");
      indent++;

      for (String optionKey : optionKeys) {
        String value = doc.getOption(optionKey);
        nlIndent().append(optionKey).append(": ")
            .append('"')
            .append(value)
            .append('"');
      }

      indent--;
    }

    indent--;
    nlIndent().append(COMMENT_END);
  }

  private void appendRenderObjectComment(DelphiNode node, RenderObject re) {
    nlIndent().append("render-element:");
    indent++;

    var s = re.getStyle();
    nlIndent().append("content-scale: ").append(s.scale);
    nlIndent().append("content: ").append(re.getContent());


    Vector2f vector = new Vector2f();

    re.getElementSize(vector);
    nlIndent().append("size: ").append(vector);

    re.getContentStart(vector);
    nlIndent().append("content-start: ").append(vector);

    re.getContentEnd(vector);
    nlIndent().append("content-end: ").append(vector);

    nlIndent().append("content-dirty: ").append(re.isContentDirty());
    nlIndent().append("position: ").append(re.getPosition());
    nlIndent().append("max-size: ").append(s.maxSize);
    nlIndent().append("min-size: ").append(s.minSize);
    nlIndent().append("padding: ").append(s.padding);
    nlIndent().append("outline-size: ").append(s.outline);
    nlIndent().append("content-ext: ").append(re.getContentExtension());
    nlIndent().append("depth: ").append(re.getDepth());

    indent--;

    builder.append("\n");
    nlIndent().append("layers:");

    indent++;

    for (Layer layer : re.getLayers()) {
      if (RenderObject.isNotSpawned(layer)) {
        continue;
      }

      nlIndent().append("layer[").append(layer.layer).append("]:");
      indent++;

      nlIndent().append("size: ").append(layer.size);
      nlIndent().append("border-size: ").append(layer.borderSize);
      nlIndent().append("depth: ").append(layer.depth);
      nlIndent().append("scale: ").append(layer.scale);
      nlIndent().append("translate: ").append(layer.translate);

      indent--;
    }

    indent--;

    PropertySet set = node.styleSet;
    RuleIterator it = set.iterator();

    if (it.hasNext()) {
      builder.append('\n');
      nlIndent().append("style-properties:");
      indent++;

      while (it.hasNext()) {
        it.next();

        Property<Object> rule = it.property();
        Object value = it.value();

        nlIndent()
            .append(rule.getKey())
            .append(": ")
            .append(value)
            .append(';');
      }

      indent--;
    }
  }

  private void appendInfo(DelphiNode node) {
    nlIndent().append(COMMENT_START);
    indent++;

    RenderObject obj = view.getRenderObject(node);
    if (obj == null) {
      nlIndent().append("Render object missing");
    } else {
      appendRenderObjectComment(node, obj);
    }

    indent--;
    nlIndent().append(COMMENT_END);
  }

  @Override
  public void enterElement(Element element) {
    super.enterElement(element);
    appendInfo((DelphiNode) element);
  }

  @Override
  public void enterText(TextNode text) {
    super.enterText(text);
    appendInfo((DelphiNode) text);
  }

  @Override
  public void enterComponent(ComponentNode node) {
    super.enterComponent(node);
    appendInfo((DelphiNode) node);
  }
}

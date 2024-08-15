package net.arcadiusmc.delphiplugin.render;

import java.util.List;
import java.util.Map;
import net.arcadiusmc.delphi.resource.ApiModule;
import net.arcadiusmc.delphi.resource.DirectoryModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ZipModule;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.XmlPrintVisitor;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.scss.ComputedStyle;
import net.arcadiusmc.delphidom.scss.DocumentStyles;
import net.arcadiusmc.delphidom.scss.Property;
import net.arcadiusmc.delphidom.scss.PropertySet;
import net.arcadiusmc.delphidom.scss.PropertySet.RuleIterator;
import net.arcadiusmc.delphidom.scss.Rule;
import net.arcadiusmc.delphidom.scss.Sheet;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.event.EventListener;
import org.joml.Vector2f;

public class RenderTreePrint extends XmlPrintVisitor {

  static final String COMMENT_START = "<!--";
  static final String COMMENT_END = "-->";

  private final PageView view;

  public RenderTreePrint(PageView view) {
    this.view = view;
  }

  public void appendHeader() {
    DelphiDocument doc = view.getDocument();
    DocumentStyles styles = doc.getStyles();

    nlIndent().append("<header>");
    indent++;

    for (String optionKey : doc.getOptionKeys()) {
      nlIndent().append("<option name=")
          .append('"')
          .append(optionKey)
          .append('"')
          .append(" value=")
          .append('"')
          .append(doc.getOption(optionKey))
          .append('"')
          .append(" />");
    }

    for (Sheet stylesheet : styles.stylesheets) {
      if ((stylesheet.getFlags() & Sheet.FLAG_DEFAULT) == Sheet.FLAG_DEFAULT) {
        continue;
      }

      nlIndent().append("<style>");
      indent++;

      for (int i = 0; i < stylesheet.getLength(); i++) {
        Rule rule = stylesheet.getRule(i);

        nlIndent().append(rule.getSelector()).append(" {");
        indent++;

        RuleIterator it = rule.getPropertySet().iterator();
        while (it.hasNext()) {
          it.next();

          nlIndent().append(it.property().getKey()).append(": ");

          Object val = it.value();

          if (val instanceof Enum<?> e) {
            builder.append(e.name().toLowerCase().replace("_", "-"));
          } else {
            builder.append(val);
          }
        }

        indent--;
        nlIndent().append("}");
      }

      indent--;
      nlIndent().append("</style>");
    }

    nlIndent().append(COMMENT_START);
    indent++;

    nlIndent().append("player-name: ").append(view.getPlayer().getName());
    nlIndent().append("world: ").append(view.getWorld().getName());
    nlIndent().append("render-object-count: ").append(view.getRenderObjects().size());
    nlIndent().append("module-name: ").append(view.getResources().getModuleName());

    String moduleType;
    ResourceModule module = view.getResources().getModule();

    moduleType = switch (module) {
      case ApiModule apiModule -> "api-module";
      case ZipModule zipModule -> "zip";
      case DirectoryModule directoryModule -> "directory";
      case null, default -> "unknown";
    };

    nlIndent().append("module-type: ").append(moduleType);
    nlIndent().append("resource-path: ").append(view.getPath());

    Screen screen = view.getScreen();
    nlIndent().append("screen:");
    indent++;
    screen.appendInfo(builder, indent);
    indent--;

    appendListeners("document-", doc.getDocumentListeners());
    appendListeners("global-", doc.getGlobalTarget());

    indent--;
    nlIndent().append(COMMENT_END);

    indent--;
    nlIndent().append("</header>");
  }

  private void appendRenderObjectComment(DelphiNode node, RenderObject re) {
    nlIndent().append("render-element:");
    indent++;

    ComputedStyle s = re.getStyle();
    nlIndent().append("content-scale: ").append(s.scale);
    nlIndent().append("parent-set: ").append(re.parent != null);

    if (re instanceof ContentRenderObject co) {
      nlIndent().append("content: ").append(co.getContent());
      nlIndent().append("content-dirty: ").append(co.isContentDirty());
    } else if (re instanceof ElementRenderObject er) {
      nlIndent().append("element-object-size: ").append(er.contentSize);
      nlIndent().append("child-count: ").append(er.childObjects.size());
    }

    Vector2f vector = new Vector2f();

    re.getElementSize(vector);
    nlIndent().append("size: ").append(vector);

    re.getContentStart(vector);
    nlIndent().append("content-start: ").append(vector);

    nlIndent().append("position: ").append(re.getPosition());
    nlIndent().append("max-size: ").append(s.maxSize);
    nlIndent().append("min-size: ").append(s.minSize);
    nlIndent().append("padding: ").append(s.padding);
    nlIndent().append("outline-size: ").append(s.outline);
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

      nlIndent().append("entity-position: ")
          .append('(')
          .append(layer.entity.getX())
          .append(' ')
          .append(layer.entity.getY())
          .append(' ')
          .append(layer.entity.getZ())
          .append(')');

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

    if (node instanceof DelphiElement el) {
      appendListeners("", el.getListenerList());
    }
  }

  private void appendListeners(String name, EventListenerList listenerList) {
    Map<String, List<EventListener>> listenerMap = listenerList.getListenerMap();

    if (listenerMap.isEmpty()) {
      return;
    }

    nlIndent().append(name).append("event-listeners:");
    indent++;

    listenerMap.forEach((eventType, eventListeners) -> {
      nlIndent().append("event-type[").append(eventType).append("]: ");
      indent++;

      for (int i = 0; i < eventListeners.size(); i++) {
        EventListener l = eventListeners.get(i);
        nlIndent().append("- ").append(i).append(": ").append(l);
      }

      indent--;
    });

    indent--;
  }

  private void appendInfo(DelphiNode node) {
    RenderObject obj = view.getRenderObject(node);

    if (obj == null) {
      return;
    }

    nlIndent().append(COMMENT_START);
    indent++;

    appendRenderObjectComment(node, obj);

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

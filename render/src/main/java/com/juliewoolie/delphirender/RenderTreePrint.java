package com.juliewoolie.delphirender;

import static com.juliewoolie.delphirender.object.ElementRenderObject.BORDER;
import static com.juliewoolie.delphirender.object.ElementRenderObject.OUTLINE;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.system.StyleObjectModel;
import com.juliewoolie.delphi.PlayerSet;
import com.juliewoolie.delphi.resource.ApiModule;
import com.juliewoolie.delphi.resource.DirectoryModule;
import com.juliewoolie.delphi.resource.IoModule;
import com.juliewoolie.delphi.resource.JarResourceModule;
import com.juliewoolie.delphi.resource.ResourceModule;
import com.juliewoolie.delphi.resource.ZipModule;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.delphidom.XmlPrintVisitor;
import com.juliewoolie.delphidom.event.EventListenerList;
import com.juliewoolie.delphirender.object.BoxRenderObject;
import com.juliewoolie.delphirender.object.CanvasRenderObject;
import com.juliewoolie.delphirender.object.ComponentRenderObject;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.ItemRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.delphirender.object.SingleEntityRenderObject;
import com.juliewoolie.delphirender.object.StringRenderObject;
import com.juliewoolie.dom.CanvasElement;
import com.juliewoolie.dom.ComponentElement;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.ItemElement;
import com.juliewoolie.dom.TextNode;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.style.StyleProperties;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public class RenderTreePrint extends XmlPrintVisitor {

  static final String COMMENT_START = "<!--";
  static final String COMMENT_END = "-->";

  private final ExtendedView view;
  private final RenderSystem system;

  public RenderTreePrint(ExtendedView view, RenderSystem system) {
    this.view = view;
    this.system = system;
  }

  public void appendDocumentInfo() {
    DelphiDocument doc = (DelphiDocument) view.getDocument();

    nlIndent().append(COMMENT_START);
    indent++;

    PlayerSet players = view.getPlayers();
    if (players.isServerPlayerSet()) {
      nlIndent().append("players: ").append("all");
    } else if (players.isEmpty()) {
      nlIndent().append("players: ").append("none");
    } else {
      nlIndent().append("players:");
      indent++;

      for (Player player : players) {
        nlIndent().append("- ").append(player.getName());
      }

      indent--;
    }

    nlIndent().append("world: ").append(view.getWorld().getName());
    nlIndent().append("render-object-count: ").append(system.getRenderElements().size());
    nlIndent().append("module-name: ").append(view.getResources().getModuleName());
    nlIndent().append("instance-name: ").append(view.getInstanceName());

    ResourceModule module = view.getResources().getModule();
    String moduleType = switch (module) {
      case ApiModule apiModule -> "api-module";
      case ZipModule zip -> "zip(" + zip.getZipFile() + ")";
      case DirectoryModule dir -> "directory(" + dir.getDirectory() + ")";
      case JarResourceModule jarRes -> "jar-resource";
      case IoModule io -> "io-module";
      case null -> "unknown";
    };

    nlIndent().append("module-type: ").append(moduleType);
    nlIndent().append("resource-path: ").append(view.getPath());

    RenderScreen screen = system.getScreen();
    nlIndent().append("screen:");
    indent++;
    screen.appendInfo(builder, indent);
    indent--;

    nlIndent();

    appendListeners("document-", doc.getDocumentListeners());
    appendListeners("global-", doc.getGlobalTarget());

    indent--;
    nlIndent().append(COMMENT_END);
  }

  private void appendFullStyle(FullStyle style)  {
    nlIndent().append("padding:");
    appendRect(style.padding);

    nlIndent().append("border: ");
    appendRect(style.border);

    nlIndent().append("outline: ");
    appendRect(style.outline);

    nlIndent().append("margin: ");
    appendRect(style.margin);
    
    nlIndent().append("margin-inline-start: ").append(style.marginInlineStart);
    nlIndent().append("margin-inline-end: ").append(style.marginInlineEnd);

    nlIndent().append("text-color: ").append(style.textColor);
    nlIndent().append("background-color: ").append(style.backgroundColor);
    nlIndent().append("border-color: ").append(style.borderColor);
    nlIndent().append("outline-color: ").append(style.outlineColor);

    nlIndent().append("text-shadowed: ").append(style.textShadowed);
    nlIndent().append("bold: ").append(style.bold);
    nlIndent().append("italic: ").append(style.italic);
    nlIndent().append("underlined: ").append(style.underlined);
    nlIndent().append("strikethrough: ").append(style.strikethrough);
    nlIndent().append("obfuscated: ").append(style.obfuscated);

    nlIndent().append("display: ").append(style.display);

    nlIndent().append("font-size: ").append(style.fontSize);
    nlIndent().append("set-size: ").append(style.size);
    nlIndent().append("min-size: ").append(style.minSize);
    nlIndent().append("max-size: ").append(style.maxSize);

    nlIndent().append("flex-basis: ").append(style.flexBasis);
    nlIndent().append("row-gap: ").append(style.rowGap);
    nlIndent().append("column-gap: ").append(style.columnGap);

    nlIndent().append("z-index: ").append(style.zindex);
    nlIndent().append("align-items: ").append(style.alignItems);
    nlIndent().append("align-self: ").append(style.alignSelf);
    nlIndent().append("flex-direction: ").append(style.flexDirection);
    nlIndent().append("flex-wrap: ").append(style.flexWrap);
    nlIndent().append("justify-content: ").append(style.justify);
    nlIndent().append("order: ").append(style.order);
    nlIndent().append("flex-grow: ").append(style.grow);
    nlIndent().append("flex-shrink: ").append(style.shrink);
    nlIndent().append("box-sizing: ").append(style.boxSizing);
    nlIndent().append("visibility: ").append(style.visibility);
    nlIndent().append("vertical-align: ").append(style.verticalAlign);
  }

  private void appendRect(Rect rect) {
    builder.append(rect.toString());
  }

  private List<Rule> findApplicableRules(DelphiNode node) {
    if (!(node instanceof DelphiElement el)) {
      return List.of();
    }

    StyleObjectModel styles = node.getDocument().getStyles();
    List<Rule> rules = new ArrayList<>();

    for (Rule rule : styles.getRules()) {
      if (!rule.getSelectorObject().test(el)) {
        continue;
      }

      rules.add(rule);
    }

    return rules;
  }

  private void appendProperties(boolean nl, String title, StylePropertiesReadonly readonly) {
    Set<String> properties = readonly.getProperties();
    if (properties.isEmpty()) {
      return;
    }

    if (nl) {
      nlIndent();
    }

    nlIndent()
        .append(title)
        .append("(property-count=")
        .append(properties.size())
        .append("): ");

    indent++;

    for (String property : properties) {
      String value = readonly.getPropertyValue(property);

      if (Strings.isNullOrEmpty(property)) {
        continue;
      }

      nlIndent().append(property).append(": ").append(value).append(";");
    }

    indent--;
  }

  private void appendRenderElementComment(DelphiNode node, RenderObject re) {
    nlIndent().append("render-object:");
    indent++;

    boolean appendChildren
         = node instanceof ComponentElement
        || node instanceof ItemElement
        || node instanceof CanvasElement;

    appendRenderObject(re, appendChildren);

    indent--;

    List<Rule> applicable = findApplicableRules(node);
    if (!applicable.isEmpty()) {
      nlIndent();

      nlIndent()
          .append("applicable-rules(")
          .append(applicable.size())
          .append(" / ")
          .append(node.getDocument().getStyles().getRules().size())
          .append("):");

      indent++;

      for (Rule rule : applicable) {
        appendProperties(false, "rule[" + rule.getSelector() + "]", rule.getProperties());
      }

      indent--;
    }

    if (node instanceof DelphiElement el) {
      StyleProperties inline = el.getInlineStyle();
      appendProperties(true, "inline-properties", inline);

      List<String> classList = el.getClassList();
      if (!classList.isEmpty()) {
        nlIndent().append("class-list:");
        indent++;
        for (String string : classList) {
          nlIndent().append(" - ").append('"').append(string).append('"');
        }
        indent--;
      }
    }

    StylePropertiesReadonly styleSet = node.getDocument().getCurrentStyle(node);
    appendProperties(true, "current-style-properties", styleSet);

    if (node instanceof DelphiElement el) {
      appendListeners("", el.getListenerList());
    }
  }

  private void appendRenderObject(RenderObject object, boolean printChildren) {
    nlIndent().append("object-type: ").append(object.getClass().getSimpleName());
    nlIndent().append("position: ").append(object.position);
    nlIndent().append("size: ").append(object.size);
    nlIndent().append("depth: ").append(object.depth);
    nlIndent().append("dom-index: ").append(object.domIndex);

    if (object instanceof SingleEntityRenderObject<?> single) {
      Display entity = single.entity;
      if (entity != null && !entity.isDead()) {
        nlIndent().append("entity-state: alive");

        Vector3f pos = new Vector3f();
        pos.x = (float) entity.getX();
        pos.y = (float) entity.getY();
        pos.z = (float) entity.getZ();

        nlIndent().append("entity-type: ").append(entity.getType().key());
        nlIndent().append("entity-position: ").append(pos);

        Transformation trans = entity.getTransformation();

        nlIndent().append("entity-transformation:");
        indent++;
        nlIndent().append("translation: ").append(trans.getTranslation());
        nlIndent().append("left-rotation: ").append(trans.getLeftRotation());
        nlIndent().append("scale: ").append(trans.getScale());
        nlIndent().append("right-rotation: ").append(trans.getRightRotation());
        indent--;

        if (entity instanceof TextDisplay txt) {
          nlIndent().append("entity-background: ").append(txt.getBackgroundColor());
          nlIndent().append("entity-text-opacity: ").append(txt.getTextOpacity());
          nlIndent().append("entity-text-shadowed: ").append(txt.isShadowed());
        }
      } else {
        nlIndent().append("entity-state: not-spawned");
      }
    }

    switch (object) {
      case CanvasRenderObject canvas -> {
        nlIndent().append("canvas-width: ").append(canvas.canvas.getWidth());
        nlIndent().append("canvas-height: ").append(canvas.canvas.getHeight());
        nlIndent().append("entities: ").append(canvas.entities.size());
      }
      case ItemRenderObject item -> {
        nlIndent().append("itemstack: ").append(item.item);
      }
      case StringRenderObject string -> {
        nlIndent().append("string: ").append(string.content);
      }
      case ComponentRenderObject comp -> {
        nlIndent().append("component: ")
            .append(GsonComponentSerializer.gson().serialize(comp.text));
      }
      case BoxRenderObject box -> {
        nlIndent().append("box-color: ").append(box.color);
      }
      case ElementRenderObject el -> {
        nlIndent().append("spawned: ").append(el.spawned);

        nlIndent();
        nlIndent().append("full-style:");
        indent++;
        appendFullStyle(el.style);
        indent--;

        nlIndent();
        nlIndent().append("box-objects:");
        indent++;

        BoxRenderObject[] boxes = el.boxes;
        for (int i = 0; i < boxes.length; i++) {
          String label = switch (i) {
            case OUTLINE -> "OUTLINE";
            case BORDER -> "BORDER";
            default -> "BACKGROUND";
          };

          nlIndent().append("box[").append(i).append("=").append(label).append("]:");
          indent++;

          BoxRenderObject box = boxes[i];
          appendRenderObject(box, false);

          indent--;
        }

        indent--;

        if (printChildren) {
          nlIndent();
          nlIndent()
              .append("child-objects(")
              .append(el.getChildObjects().size())
              .append("): ");

          indent++;

          for (RenderObject childObject : el.getChildObjects()) {
            appendRenderObject(childObject, true);
          }

          indent--;
        }
      }
      default -> {}
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
    RenderObject obj = system.getRenderElement(node);

    if (obj == null) {
      return;
    }

    nlIndent().append(COMMENT_START);
    indent++;

    appendRenderElementComment(node, obj);

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
  public void enterComponent(ComponentElement node) {
    super.enterComponent(node);
    appendInfo((DelphiNode) node);
  }
}

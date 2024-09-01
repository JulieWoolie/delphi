package net.arcadiusmc.chimera.system;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.InlineStyle;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.StyleLoggers;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.ChimeraParser;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.dom.Element;

@Getter
public class ElementStyleNode extends StyleNode {

  private final List<StyleNode> children = new ArrayList<>();
  private final PropertySet inlineStyleSet = new PropertySet();

  private final InlineStyle inlineApi;

  @Setter
  private boolean suppressingInlineUpdates = false;

  public ElementStyleNode(Element domNode, StyleSystem system) {
    super(domNode, system);

    this.inlineApi = new InlineStyle(inlineStyleSet, this);
  }

  @Override
  public Element getDomNode() {
    return (Element) super.getDomNode();
  }

  public void setInline(String inline) {
    if (suppressingInlineUpdates) {
      return;
    }
    if (Strings.isNullOrEmpty(inline)) {
      inlineStyleSet.clear();
    }

    ChimeraParser parser = new ChimeraParser(inline);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("<inline-style>");
    errors.setListener(error -> {
      StyleLoggers.getLogger()
          .atLevel(error.getLevel())
          .setMessage(error.getFormattedError())
          .log();
    });

    InlineStyleStatement statement = parser.inlineStyle();

    ChimeraContext context = new ChimeraContext(parser.getStream().getInput());
    context.setErrors(errors);
    context.setVariables(getSystem().getVariables());

    Chimera.compileInline(statement, inlineStyleSet, context);
  }

  public void addChild(StyleNode node, int idx) {
    if (node.parent == this) {
      return;
    }
    if (node.parent != null) {
      throw new IllegalStateException("Style node already has parent");
    }

    children.add(idx, node);
    node.parent = this;
  }

  public void removeChild(int idx) {
    StyleNode removed = children.remove(idx);
    removed.parent = null;
  }
}

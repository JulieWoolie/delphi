package net.arcadiusmc.delphiplugin.devtools;

import com.google.common.base.Strings;
import java.util.Map.Entry;
import java.util.Set;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class ElementTreeTab implements DevToolTab {

  private static final Logger LOGGER = Loggers.getLogger();

  static final String SPAN = "span";

  @Override
  public void onOpen(Devtools devtools) {
    Element content = devtools.getContentEl();
    Document document = devtools.getDocument();
    Element targetRoot = devtools.getTarget().getDocument().getDocumentElement();

    DomBuilder builder = new DomBuilder(content);
    createElementLines(targetRoot, document, builder);
    builder.linebreak();
  }

  @Override
  public void onClose(Devtools devtools) {

  }

  void createElementLines(Node node, Document document, DomBuilder builder) {
    if (node instanceof TextNode txt) {
      String noWhiteSpace = StringUtils.deleteWhitespace(txt.getTextContent());
      if (Strings.isNullOrEmpty(noWhiteSpace)) {
        return;
      }

      String[] contentArr = txt.getTextContent().split("\\n+");

      for (int i = 0; i < contentArr.length; i++) {
        String content = contentArr[i];
        Element txtNode = document.createElement(SPAN);
        txtNode.setTextContent(content);
        txtNode.setClassName("xml-text");
        builder.append(txtNode);

        if (i != contentArr.length - 1) {
          builder.linebreak();
        }
      }

      return;
    }

    Element element = (Element) node;

    String prefix = "<" + element.getTagName();
    Element prefixEl = document.createElement(SPAN);
    prefixEl.setTextContent(prefix);
    prefixEl.setClassName("xml-tag");
    builder.append(prefixEl);

    Set<Entry<String, String>> attrs = element.getAttributeEntries();

    for (Entry<String, String> attr : attrs) {
      Element attrName = document.createElement(SPAN);
      Element attrSep = document.createElement(SPAN);
      Element attrValue = document.createElement(SPAN);
      Element attrSuffix = document.createElement(SPAN);

      attrName.setTextContent(attr.getKey());
      attrSep.setTextContent("=\"");
      attrValue.setTextContent(attr.getValue());
      attrSuffix.setTextContent("\"");

      attrName.setClassName("xml-attr-name");
      attrSep.setClassName("xml-attr-sep");
      attrValue.setClassName("xml-attr-value");
      attrSuffix.setClassName("xml-attr-sep");

      builder.append(attrName);
      builder.append(attrSep);
      builder.append(attrValue);
      builder.append(attrSuffix);
    }

    Element addAttrBtn = document.createElement(SPAN);
    addAttrBtn.setTextContent("+");
    addAttrBtn.setClassName("add-attr");
    builder.append(addAttrBtn);

    if (!element.canHaveChildren() || !element.hasChildren()) {
      Element suffix = document.createElement(SPAN);
      suffix.setTextContent("/>");
      suffix.setClassName("xml-end-tag");
      builder.append(suffix);
      return;
    }

    Element suffix = document.createElement(SPAN);
    suffix.setTextContent(">");
    suffix.setClassName("xml-end-tag");
    builder.append(suffix);

    boolean inlineChildren = inlineChildren(element);

    if (!inlineChildren) {
      builder.indent++;
      builder.linebreak();
    }

    for (Node child : element.getChildren()) {
      createElementLines(child, document, builder);

      if (!inlineChildren) {
        builder.linebreak();
      }
    }

    if (!inlineChildren) {
      builder.indent--;
    }

    Element endTag = document.createElement(SPAN);
    endTag.setTextContent("</" + element.getTagName() + ">");
    endTag.setClassName("xml-end-tag");

    builder.append(endTag);
  }

  boolean inlineChildren(Element el) {
    int descendantCount = countDescendants(el);
    if (descendantCount > 1) {
      return false;
    }

    if (!el.hasChildren()) {
      return true;
    }

    Node child = el.firstChild();
    if (!(child instanceof TextNode txt)) {
      return false;
    }

    String content = txt.getTextContent();
    if (Strings.isNullOrEmpty(content)) {
      return true;
    }

    return !(content.contains("\n") || content.contains("\r"));
  }

  int countDescendants(Element el) {
    int desc = el.getChildCount();

    for (Node child : el.getChildren()) {
      if (!(child instanceof Element e)) {
        continue;
      }

      desc += countDescendants(e);
    }

    return desc;
  }

  class DomBuilder {

    static final int MAX_LINE_NUM = 24;

    private final Element lineContainer;
    private int lineCount = 1;
    private int indent = 0;
    private Element line;

    public DomBuilder(Element lineContainer) {
      this.lineContainer = lineContainer;
    }

    public void append(Node n) {
      if (line == null) {
        Document document = lineContainer.getOwningDocument();

        line = document.createElement("div");
        line.getClassList().add("xml-line");
        line.setAttribute("line-num", String.valueOf(lineCount));

        if (indent > 0) {
          line.getClassList().add("indent-" + indent);
        }

        Element lineEl = document.createElement("line");
        lineEl.setClassName("lineno");
        lineEl.setTextContent(String.valueOf(lineCount));

        line.appendChild(lineEl);
      }

      line.appendChild(n);
    }

    public void linebreak() {
      if (line == null) {
        return;
      }
      if (lineCount > MAX_LINE_NUM) {
        return;
      }

      lineCount++;
      lineContainer.appendChild(line);

      line = null;
    }
  }
}

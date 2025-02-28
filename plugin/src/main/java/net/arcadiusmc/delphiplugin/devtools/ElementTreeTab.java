package net.arcadiusmc.delphiplugin.devtools;

import java.util.Map.Entry;
import java.util.Set;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.TextNode;

public class ElementTreeTab implements DevToolTab {

  static final String SPAN = "span";

  @Override
  public void onOpen(Devtools devtools) {
    Element content = devtools.getContentEl();
    Document document = devtools.getDocument();
    Element targetRoot = devtools.getTarget().getDocument().getDocumentElement();

    DomBuilder builder = new DomBuilder(content);
    createElementLines(targetRoot, document, builder);


  }

  @Override
  public void onClose(Devtools devtools) {

  }

  void createElementLines(Node node, Document document, DomBuilder builder) {
    if (node instanceof TextNode txt) {
      TextNode copy = document.createText(txt.getTextContent());
      builder.append(copy);
      return;
    }

    Element element = (Element) node;

    String prefix = "<" + element.getTagName();
    Element prefixEl = document.createElement(SPAN);
    prefixEl.setTextContent(prefix);
    prefixEl.setClassName("xml-tag");

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

    if (!element.canHaveChildren()) {
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

    builder.indent++;
    builder.linebreak();

    for (Node child : element.getChildren()) {
      createElementLines(child, document, builder);
      builder.linebreak();
    }

    builder.indent--;

    Element endTag = document.createElement(SPAN);
    suffix.setTextContent("</" + element.getTagName() + ">");
    suffix.setClassName("xml-end-tag");

    builder.append(endTag);
  }

  class DomBuilder {

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

      lineCount++;
      lineContainer.appendChild(line);

      line = null;
    }
  }
}

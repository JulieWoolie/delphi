package net.arcadiusmc.delphidom;

import java.util.Map.Entry;
import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.Visitor;

public class XmlPrintVisitor implements Visitor {

  protected int indent = 0;
  protected StringBuilder builder = new StringBuilder();

  protected StringBuilder nlIndent() {
    return builder.append("\n")
        .append("  ".repeat(indent));
  }

  @Override
  public void enterElement(Element element) {
    nlIndent().append("<").append(element.getTagName());

    for (Entry<String, String> entry : element.getAttributeEntries()) {
      builder.append(" ")
          .append(entry.getKey())
          .append('=')
          .append('"')
          .append(entry.getValue())
          .append('"');
    }

    builder.append('>');

    indent++;
  }

  @Override
  public void exitElement(Element element) {
    indent--;
    nlIndent().append("</").append(element.getTagName()).append(">");
  }

  @Override
  public void enterText(TextNode text) {
    nlIndent().append(text.getTextContent());
  }

  @Override
  public void exitText(TextNode text) {

  }

  @Override
  public void enterComponent(ComponentNode node) {
    nlIndent().append("<chat-component text=\"unknown\"/>");
  }

  @Override
  public void exitComponent(ComponentNode node) {

  }

  @Override
  public String toString() {
    return builder.toString();
  }
}

package net.arcadiusmc.delphi;

import java.util.Map.Entry;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.Visitor;

public class XmlPrintVisitor implements Visitor {

  private int indent = 0;
  private StringBuilder builder = new StringBuilder();

  private StringBuilder nlIndent() {
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
  public String toString() {
    return builder.toString();
  }
}

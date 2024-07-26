package net.arcadiusmc.dom;

public interface Visitor {

  static void visit(Node node, Visitor visitor) {
    node.enterVisitor(visitor);

    if (node instanceof Element e) {
      for (Node child : e.getChildren()) {
        visit(child, visitor);
      }
    }

    node.exitVisitor(visitor);
  }

  void enterElement(Element element);

  void exitElement(Element element);

  void enterText(TextNode text);

  void exitText(TextNode text);
}

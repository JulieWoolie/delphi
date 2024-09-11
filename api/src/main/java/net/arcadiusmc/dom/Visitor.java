package net.arcadiusmc.dom;

/**
 * DOM tree visitor
 */
public interface Visitor {

  /**
   * Recursively visit the specified {@code node} and it's children, if it has any.
   * @param node Node to visit
   * @param visitor Visitor
   */
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

  void enterComponent(ComponentNode node);

  void exitComponent(ComponentNode node);
}

package net.arcadiusmc.chimera;

import net.arcadiusmc.chimera.ast.Node;

public final class Chimera {
  private Chimera() {}

  public static void print(Node node) {
    XmlPrintVisitor visitor = new XmlPrintVisitor();
    node.visit(visitor, null);
    String out = visitor.toString();
    System.out.println(out);
  }
}

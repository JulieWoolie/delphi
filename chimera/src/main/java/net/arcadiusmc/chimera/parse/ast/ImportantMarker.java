package net.arcadiusmc.chimera.parse.ast;

public class ImportantMarker extends Node {

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.important(this, context);
  }
}

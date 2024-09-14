package net.arcadiusmc.chimera.parse.ast;

public class ImportantMarker extends Node {

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.important(this);
  }
}

package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.Location;
import net.arcadiusmc.chimera.parse.XmlPrintVisitor;

@Setter
@Getter
public abstract class Node {

  private Location start;
  private Location end;

  public abstract <R> R visit(NodeVisitor<R> visitor);

  @Override
  public String toString() {
    XmlPrintVisitor visitor = new XmlPrintVisitor();
    visitor.noComments = true;
    visit(visitor);
    return visitor.toString();
  }
}

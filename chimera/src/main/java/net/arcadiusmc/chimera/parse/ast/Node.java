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

  public abstract <R, C> R visit(NodeVisitor<R, C> visitor, C context);

  @Override
  public String toString() {
    XmlPrintVisitor visitor = new XmlPrintVisitor();
    visit(visitor, null);
    return visitor.toString();
  }
}

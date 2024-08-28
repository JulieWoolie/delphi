package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.Location;
import net.arcadiusmc.chimera.XmlPrintVisitor;

@Setter
@Getter
public abstract class Node {

  private Location start;

  public abstract <R, C> R visit(NodeVisitor<R, C> visitor, C context);

  @Override
  public String toString() {
    XmlPrintVisitor visitor = new XmlPrintVisitor();
    visit(visitor, null);
    return visitor.toString();
  }
}

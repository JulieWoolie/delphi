package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.Location;
import com.juliewoolie.chimera.parse.XmlPrintVisitor;

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

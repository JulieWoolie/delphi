package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;

@Getter @Setter
public class LogStatement extends Statement {

  private Expression expression;
  private Level level = Level.INFO;
  private String name = "info";

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.logStatement(this);
  }
}

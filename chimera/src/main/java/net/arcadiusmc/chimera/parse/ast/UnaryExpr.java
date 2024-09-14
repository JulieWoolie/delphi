package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UnaryExpr extends Expression {

  private Expression value;
  private UnaryOp op = UnaryOp.PLUS;

  @Override
  public <R> R visit(NodeVisitor<R> visitor) {
    return visitor.unary(this);
  }
}

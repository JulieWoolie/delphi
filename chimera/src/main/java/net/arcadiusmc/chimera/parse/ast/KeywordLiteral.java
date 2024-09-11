package net.arcadiusmc.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.Scope;

@Getter @Setter
public class KeywordLiteral extends Expression {

  private Keyword keyword;

  @Override
  public Keyword evaluate(ChimeraContext ctx, Scope scope) {
    return keyword;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.keywordLiteral(this, context);
  }
}

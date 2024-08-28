package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraContext;

@Getter @Setter
public class KeywordLiteral extends Expression {

  private Keyword keyword;

  @Override
  public Keyword evaluate(ChimeraContext ctx) {
    return keyword;
  }

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.keywordLiteral(this, context);
  }

  public enum Keyword {
    INHERIT,
    INITIAL,
    AUTO,
    UNSET,

    // Align items
    FLEX_START,
    FLEX_END,
    CENTER,
    STRETCH,
    BASELINE,

    // Display options
    NONE,
    INLINE,
    BLOCK,
    INLINE_BLOCK,
    FLEX,

    // Flex types
    ROW,
    ROW_REVERSE,
    COLUMN,
    COLUMN_REVERSE,

    // Flex wrap
    NOWRAP,
    WRAP,
    WRAP_REVERSE,

    // Justify content
    SPACE_BETWEEN,
    SPACE_AROUND,
    SPACE_EVENLY
  }
}

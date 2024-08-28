package net.arcadiusmc.chimera.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.CompilerErrors;
import net.arcadiusmc.chimera.selector.Combinator;
import net.arcadiusmc.chimera.selector.SelectorFunction;
import net.arcadiusmc.chimera.selector.SelectorNode;

@Getter @Setter
public class SelectorNodeStatement extends Node {

  private final List<SelectorExpression> expressions = new ArrayList<>();
  private Combinator combinator;

  @Override
  public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
    return visitor.selectorNode(this, context);
  }

  public SelectorNode compile(CompilerErrors errors) {
    List<SelectorFunction> functions = new ArrayList<>();

    for (int i = 0; i < expressions.size(); i++) {
      SelectorFunction func = expressions.get(i).compile(errors);

      if (func == null) {
        continue;
      }

      functions.add(func);
    }

    return new SelectorNode(combinator, functions.toArray(SelectorFunction[]::new));
  }
}

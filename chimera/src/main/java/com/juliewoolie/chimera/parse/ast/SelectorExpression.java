package com.juliewoolie.chimera.parse.ast;

import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.selector.AnB;
import com.juliewoolie.chimera.selector.AttributeOperation;
import com.juliewoolie.chimera.selector.AttributeSelector;
import com.juliewoolie.chimera.selector.ClassNameSelector;
import com.juliewoolie.chimera.selector.Combinator;
import com.juliewoolie.chimera.selector.GroupedIndexSelector;
import com.juliewoolie.chimera.selector.IdSelector;
import com.juliewoolie.chimera.selector.IndexSelector;
import com.juliewoolie.chimera.selector.PseudoClass;
import com.juliewoolie.chimera.selector.PseudoClassSelector;
import com.juliewoolie.chimera.selector.PseudoElement;
import com.juliewoolie.chimera.selector.PseudoElementSelector;
import com.juliewoolie.chimera.selector.PseudoFuncSelector;
import com.juliewoolie.chimera.selector.PseudoFunctions;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.selector.SelectorNode;
import com.juliewoolie.chimera.selector.SimpleIndexSelector;
import com.juliewoolie.chimera.selector.TagNameSelector;

public abstract class SelectorExpression extends Node {

  public abstract Selector compile(CompilerErrors errors);

  @Getter @Setter
  public static class TagNameExpr extends SelectorExpression {
    private Identifier tagName;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorTagName(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      return new TagNameSelector(tagName.getValue());
    }
  }

  @Getter @Setter
  public static class ClassNameExpr extends SelectorExpression {
    private Identifier className;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorClassName(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      return new ClassNameSelector(className.getValue());
    }
  }

  @Getter @Setter
  public static class IdExpr extends SelectorExpression {
    private Identifier id;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorId(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      return new IdSelector(id.getValue());
    }
  }

  @Getter @Setter
  public static class AttributeExpr extends SelectorExpression {
    private Identifier attributeName;
    private AttributeOperation operation;
    private StringLiteral value;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorAttribute(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      return new AttributeSelector(
          attributeName.getValue(),
          operation,
          value == null ? null : value.getValue()
      );
    }
  }

  @Getter @Setter
  public static class PseudoClassExpr extends SelectorExpression {
    private Identifier pseudoClass;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorPseudoClass(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      PseudoClass pseudo = switch (pseudoClass.getValue().toLowerCase()) {
        case "active" -> PseudoClass.ACTIVE;
        case "hover" -> PseudoClass.HOVER;
        case "disabled" -> PseudoClass.DISABLED;
        case "enabled" -> PseudoClass.ENABLED;
        case "root" -> PseudoClass.ROOT;
        case "first-child" -> PseudoClass.FIRST_CHILD;
        case "last-child" -> PseudoClass.LAST_CHILD;
        case "last-of-type" -> PseudoClass.LAST_OF_TYPE;
        case "first-of-type" -> PseudoClass.FIRST_OF_TYPE;
        case "only-of-type" -> PseudoClass.ONLY_OF_TYPE;
        case "only-child" -> PseudoClass.ONLY_CHILD;

        default -> {
          errors.error(getStart(), "Unknown/unsupported pseudo class %s", pseudoClass.getValue());
          yield null;
        }
      };

      if (pseudo == null) {
        return null;
      }

      return new PseudoClassSelector(pseudo);
    }
  }

  @Getter @Setter
  public static class PseudoFunctionExpr extends SelectorExpression {
    private Identifier functionName;
    private IndexExpr index;
    private SelectorListStatement selectorGroup;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorPseudoFunction(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      String name = functionName.getValue().toLowerCase();

      if (name.equals("is") || name.equals("not")) {
        Selector group;

        if (selectorGroup == null) {
          errors.error(getStart(), ":%s() pseudo class requires a selector to be defined", name);
          group = Selector.MATCH_ALL;
        } else {
          group = selectorGroup.compile(errors);
        }

        return new PseudoFuncSelector<>(
            name.equalsIgnoreCase("not")
                ? PseudoFunctions.NOT
                : PseudoFunctions.IS,
            group
        );
      }

      // Validate state
      switch (name) {
        case "nth-of-type":
        case "nth-last-of-type":
          if (selectorGroup != null) {
            errors.error(getStart(), "'of selector' cannot be set on %s pseudo classes", name);
          }

        case "nth-child":
        case "nth-last-child":
          if (index == null) {
            errors.error(getStart(), "No index expression specified");
          }
          break;

        default:
          errors.error(getStart(), "Unknown/unsupported pseudo class");
          return null;
      }

      AnB anb = index.compile(errors);
      IndexSelector selector;

      if (selectorGroup == null) {
        selector = new SimpleIndexSelector(anb);
      } else {
        Selector group = selectorGroup.compile(errors);
        selector = new GroupedIndexSelector(anb, group);
      }

      return switch (name) {
        case "nth-child" -> new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, selector);
        case "nth-last-child" -> new PseudoFuncSelector<>(PseudoFunctions.NTH_LAST_CHILD, selector);
        case "nth-of-type" -> new PseudoFuncSelector<>(PseudoFunctions.NTH_OF_TYPE, anb);
        case "nth-last-of-type" -> new PseudoFuncSelector<>(PseudoFunctions.NTH_LAST_OF_TYPE, anb);
        default -> null;
      };
    }
  }

  public abstract static class IndexExpr extends Node {

    public abstract AnB compile(CompilerErrors errors);
  }

  @Getter @Setter
  public static class EvenOddKeyword extends IndexExpr {
    private EvenOdd evenOdd;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.evenOdd(this);
    }

    @Override
    public AnB compile(CompilerErrors errors) {
      return evenOdd == EvenOdd.EVEN ? AnB.EVEN : AnB.ODD;
    }
  }

  public enum EvenOdd {
    EVEN,
    ODD,
    ;
  }

  @Getter @Setter
  public static class AnbExpr extends IndexExpr {

    private NumberLiteral a;
    private NumberLiteral b;

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.anb(this);
    }

    @Override
    public AnB compile(CompilerErrors errors) {
      int aVal = a == null ? 0 : a.getValue().intValue();
      int bVal = b == null ? 0 : b.getValue().intValue();

      if (aVal == 0 && bVal < 0) {
        errors.error(getStart(), "n value must be greater than 0");
      }
      if (aVal != 0 && bVal < 0) {
        errors.error(getStart(), "An+B B value bust be greater than 0");
      }

      return new AnB(aVal, bVal);
    }
  }

  public static class MatchAllExpr extends SelectorExpression {

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorMatchAll(this);
    }

    @Override
    public Selector compile(CompilerErrors errors) {
      return Selector.MATCH_ALL;
    }
  }

  @Getter @Setter
  public static class NestedSelector extends SelectorExpression {

    private SelectorExpression selector;

    @Override
    public Selector compile(CompilerErrors errors) {
      Selector selector = this.selector.compile(errors);
      if (selector == null) {
        return null;
      }

      SelectorNode node = new SelectorNode();
      node.setCombinator(Combinator.NEST);
      node.setSelector(selector);
      return node;
    }

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorNested(this);
    }
  }

  @Getter @Setter
  public static class PseudoElementExpr extends SelectorExpression {

    private Identifier name;

    @Override
    public Selector compile(CompilerErrors errors) {
      PseudoElement element;
      switch (name.getValue()) {
        case "placeholder" -> {
          element = PseudoElement.PLACEHOLDER;
        }

        default -> {
          errors.error(getStart(), "Unknown/unsupported pseudo element");
          element = PseudoElement.PLACEHOLDER;
        }
      }

      return new PseudoElementSelector(element);
    }

    @Override
    public <R> R visit(NodeVisitor<R> visitor) {
      return visitor.selectorPseudoElement(this);
    }
  }
}

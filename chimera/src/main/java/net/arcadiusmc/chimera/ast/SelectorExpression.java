package net.arcadiusmc.chimera.ast;

import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.CompilerErrors;
import net.arcadiusmc.chimera.selector.AnB;
import net.arcadiusmc.chimera.selector.AttributeFunction;
import net.arcadiusmc.chimera.selector.AttributeOperation;
import net.arcadiusmc.chimera.selector.ClassNameFunction;
import net.arcadiusmc.chimera.selector.GroupedIndexSelector;
import net.arcadiusmc.chimera.selector.IdFunction;
import net.arcadiusmc.chimera.selector.IndexSelector;
import net.arcadiusmc.chimera.selector.PseudoClass;
import net.arcadiusmc.chimera.selector.PseudoClassFunction;
import net.arcadiusmc.chimera.selector.PseudoFuncFunction;
import net.arcadiusmc.chimera.selector.PseudoFunctions;
import net.arcadiusmc.chimera.selector.SelectorFunction;
import net.arcadiusmc.chimera.selector.SelectorGroup;
import net.arcadiusmc.chimera.selector.SimpleIndexSelector;
import net.arcadiusmc.chimera.selector.TagNameFunction;

public abstract class SelectorExpression extends Node {

  public abstract SelectorFunction compile(CompilerErrors errors);

  @Getter @Setter
  public static class TagNameExpr extends SelectorExpression {
    private Identifier tagName;

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorTagName(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      return new TagNameFunction(tagName.getValue());
    }
  }

  @Getter @Setter
  public static class ClassNameExpr extends SelectorExpression {
    private Identifier className;

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorClassName(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      return new ClassNameFunction(className.getValue());
    }
  }

  @Getter @Setter
  public static class IdExpr extends SelectorExpression {
    private Identifier id;

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorId(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      return new IdFunction(id.getValue());
    }
  }

  @Getter @Setter
  public static class AttributeExpr extends SelectorExpression {
    private Identifier attributeName;
    private AttributeOperation operation;
    private StringLiteral value;

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorAttribute(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      return new AttributeFunction(
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
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorPseudoClass(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
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

      return new PseudoClassFunction(pseudo);
    }
  }

  @Getter @Setter
  public static class PseudoFunctionExpr extends SelectorExpression {
    private Identifier functionName;
    private IndexExpr index;
    private SelectorGroupStatement selectorGroup;

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorPseudoFunction(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      String name = functionName.getValue().toLowerCase();

      if (name.equals("is") || name.equals("not")) {
        SelectorGroup group;

        if (selectorGroup == null) {
          errors.error(getStart(), ":%s() pseudo class requires a selector to be defined", name);
          group = SelectorGroup.EMPTY;
        } else {
          group = selectorGroup.compile(errors);
        }

        return new PseudoFuncFunction<>(
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
        SelectorGroup group = selectorGroup.compile(errors);
        selector = new GroupedIndexSelector(anb, group);
      }

      return switch (name) {
        case "nth-child" -> new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, selector);
        case "nth-last-child" -> new PseudoFuncFunction<>(PseudoFunctions.NTH_LAST_CHILD, selector);
        case "nth-of-type" -> new PseudoFuncFunction<>(PseudoFunctions.NTH_OF_TYPE, anb);
        case "nth-last-of-type" -> new PseudoFuncFunction<>(PseudoFunctions.NTH_LAST_OF_TYPE, anb);
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
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.evenOdd(this, context);
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
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.anb(this, context);
    }

    @Override
    public AnB compile(CompilerErrors errors) {
      int aVal = a == null ? 1 : a.getValue().intValue();
      int bVal = b == null ? 0 : b.getValue().intValue();

      if (aVal != -1 && bVal < 1) {
        if (aVal == 0) {
          errors.error(getStart(), "n value must be greater than 0");
        } else {
          errors.error(getStart(), "An+B B value bust be greater than 0");
        }
      }

      return new AnB(aVal, bVal);
    }
  }

  public static class MatchAllExpr extends SelectorExpression {

    @Override
    public <R, C> R visit(NodeVisitor<R, C> visitor, C context) {
      return visitor.selectorMatchAll(this, context);
    }

    @Override
    public SelectorFunction compile(CompilerErrors errors) {
      return SelectorFunction.MATCH_ALL;
    }
  }

}

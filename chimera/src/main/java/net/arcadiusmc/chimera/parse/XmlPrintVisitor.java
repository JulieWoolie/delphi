package net.arcadiusmc.chimera.parse;

import java.util.List;
import java.util.Map;
import net.arcadiusmc.chimera.parse.ast.BinaryExpr;
import net.arcadiusmc.chimera.parse.ast.CallExpr;
import net.arcadiusmc.chimera.parse.ast.ColorLiteral;
import net.arcadiusmc.chimera.parse.ast.ErroneousExpr;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.Identifier;
import net.arcadiusmc.chimera.parse.ast.ImportantMarker;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.KeywordLiteral;
import net.arcadiusmc.chimera.parse.ast.NamespaceExpr;
import net.arcadiusmc.chimera.parse.ast.Node;
import net.arcadiusmc.chimera.parse.ast.NodeVisitor;
import net.arcadiusmc.chimera.parse.ast.NumberLiteral;
import net.arcadiusmc.chimera.parse.ast.PropertyStatement;
import net.arcadiusmc.chimera.parse.ast.RectExpr;
import net.arcadiusmc.chimera.parse.ast.RegularSelectorStatement;
import net.arcadiusmc.chimera.parse.ast.RuleStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.TagNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorListStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.chimera.parse.ast.StringLiteral;
import net.arcadiusmc.chimera.parse.ast.UnaryExpr;
import net.arcadiusmc.chimera.parse.ast.VariableDecl;
import net.arcadiusmc.chimera.parse.ast.VariableExpr;

public class XmlPrintVisitor implements NodeVisitor<Void, Void> {

  private final StringBuilder builder = new StringBuilder();
  private int indent = 0;

  private StringBuilder nlIndent() {
    if (builder.isEmpty()) {
      return builder;
    }

    return builder.append("\n").append("  ".repeat(indent));
  }

  private StringBuilder comment(String comment) {
    return nlIndent().append("<!-- ").append(comment).append(" -->");
  }

  private StringBuilder enterTag(String tag, Node node) {
    return enterTag(tag, node, null);
  }

  private StringBuilder enterTag(String tag, Node node, Map<String, Object> attrs) {
    startTag(tag, node, attrs).append('>');
    indent++;
    return builder;
  }

  private StringBuilder exitTag(String tag) {
    indent--;
    return endTag(tag);
  }

  private StringBuilder voidTag(String tag, Node node) {
    return voidTag(tag, node, null);
  }

  private StringBuilder voidTag(String tag, Node node, Map<String, Object> attrs) {
    return startTag(tag, node, attrs).append(" />");
  }

  private StringBuilder startTag(String tag, Node node) {
    return startTag(tag, node, null);
  }

  private StringBuilder startTag(String tag, Node node, Map<String, Object> attrs) {
    nlIndent().append("<").append(tag);

    if (node.getStart() != null) {
      builder
          .append(" start=")
          .append('"')
          .append(node.getStart())
          .append('"');
    }

    if (node.getEnd() != null) {
      builder
          .append(" end=")
          .append('"')
          .append(node.getEnd())
          .append('"');
    }

    if (attrs != null && !attrs.isEmpty()) {
      attrs.forEach((s, object) -> {
        String valueString;

        if (object instanceof Enum<?> e) {
          valueString = e.name().toLowerCase();
        } else {
          valueString = String.valueOf(object);
        }

        builder
            .append(' ')
            .append(s)
            .append('=')
            .append('"')
            .append(valueString)
            .append('"');
      });
    }

    return builder;
  }

  private StringBuilder endTag(String tag) {
    return nlIndent().append("</").append(tag).append(">");
  }

  @Override
  public Void variableExpr(VariableExpr expr, Void unused) {
    enterTag("variable-expr", expr);
    expr.getVariableName().visit(this, unused);
    exitTag("variable-expr");
    return null;
  }

  @Override
  public Void variableDecl(VariableDecl decl, Void unused) {
    enterTag("variable-decl", decl);

    if (decl.getName() != null) {
      comment("variable name");
      decl.getName().visit(this, unused);
    }
    if (decl.getValue() != null) {
      comment("value");
      decl.getValue().visit(this, unused);
    }

    exitTag("variable-decl");
    return null;
  }

  @Override
  public Void stringLiteral(StringLiteral expr, Void unused) {
    voidTag("string-literal", expr, Map.of("value", expr.getValue()));
    return null;
  }

  @Override
  public Void sheet(SheetStatement sheet, Void unused) {
    enterTag("stylesheet", sheet);

    List<VariableDecl> vars = sheet.getVariableDeclarations();
    if (!vars.isEmpty()) {
      comment("variables");
      for (VariableDecl var : vars) {
        var.visit(this, unused);
      }
    }

    List<RuleStatement> rules = sheet.getRules();
    if (!rules.isEmpty()) {
      comment("rules");
      for (RuleStatement rule : rules) {
        rule.visit(this, unused);
      }
    }

    exitTag("stylesheet");
    return null;
  }

  @Override
  public Void rule(RuleStatement rule, Void unused) {
    enterTag("rule", rule);

    if (rule.getSelector() != null) {
      rule.getSelector().visit(this, unused);
    }

    List<RuleStatement> nest = rule.getNestedRules();
    if (!nest.isEmpty()) {
      comment("nested rules");

      for (RuleStatement ruleStatement : nest) {
        ruleStatement.visit(this, unused);
      }
    }

    List<PropertyStatement> properties = rule.getProperties();
    if (!properties.isEmpty()) {
      comment("properties");

      for (PropertyStatement property : properties) {
        property(property, unused);
      }
    }

    exitTag("rule");
    return null;
  }

  @Override
  public Void property(PropertyStatement prop, Void unused) {
    enterTag("property", prop);

    comment("property name");
    prop.getPropertyName().visit(this, unused);

    if (prop.getValue() != null) {
      comment("property value");
      prop.getValue().visit(this, unused);
    }

    if (prop.getImportant() != null) {
      comment("important");
      important(prop.getImportant(), unused);
    }

    exitTag("property");
    return null;
  }

  @Override
  public Void numberLiteral(NumberLiteral expr, Void unused) {
    voidTag("number", expr, Map.of("value", expr.getValue(), "unit", expr.getUnit()));
    return null;
  }

  @Override
  public Void keywordLiteral(KeywordLiteral expr, Void unused) {
    voidTag("keyword", expr, Map.of("keyword", expr.getKeyword()));
    return null;
  }

  @Override
  public Void inlineStyle(InlineStyleStatement inline, Void unused) {
    enterTag("inline-style", inline);

    List<PropertyStatement> properties = inline.getProperties();
    for (PropertyStatement property : properties) {
      property.visit(this, unused);
    }

    exitTag("inline-style");
    return null;
  }

  @Override
  public Void identifier(Identifier expr, Void unused) {
    voidTag("identifier", expr, Map.of("value", expr.getValue()));
    return null;
  }

  @Override
  public Void error(ErroneousExpr expr, Void unused) {
    voidTag("error", expr, Map.of("error-token", expr.getToken().info()));
    return null;
  }

  @Override
  public Void colorLiteral(ColorLiteral expr, Void unused) {
    voidTag("color", expr, Map.of("value", expr.getColor()));
    return null;
  }

  @Override
  public Void callExpr(CallExpr expr, Void unused) {
    enterTag("call", expr);
    expr.getFunctionName().visit(this, unused);

    if (!expr.getArguments().isEmpty()) {
      comment("function arguments");

      for (Expression argument : expr.getArguments()) {
        argument.visit(this, unused);
      }
    }

    exitTag("call");
    return null;
  }

  @Override
  public Void selector(RegularSelectorStatement selector, Void unused) {
    enterTag("selector", selector);

    for (SelectorNodeStatement node : selector.getNodes()) {
      node.visit(this, unused);
    }

    exitTag("selector");
    return null;
  }

  @Override
  public Void selectorGroup(SelectorListStatement group, Void unused) {
    enterTag("selector-group", group);

    for (RegularSelectorStatement selector : group.getSelectors()) {
      selector(selector, unused);
    }

    exitTag("selector-group");
    return null;
  }

  @Override
  public Void selectorMatchAll(MatchAllExpr expr, Void unused) {
    voidTag("select-all", expr);
    return null;
  }

  @Override
  public Void anb(AnbExpr expr, Void unused) {
    enterTag("anb", expr);

    if (expr.getA() != null) {
      comment("A");
      expr.getA().visit(this, unused);
    }

    if (expr.getB() != null) {
      comment("B");
      expr.getB().visit(this, unused);
    }

    exitTag("anb");
    return null;
  }

  @Override
  public Void evenOdd(EvenOddKeyword expr, Void unused) {
    voidTag("even-odd", expr, Map.of("value", expr.getEvenOdd()));

    return null;
  }

  @Override
  public Void selectorPseudoFunction(PseudoFunctionExpr expr, Void unused) {
    enterTag("pseudo-function", expr);

    if (expr.getFunctionName() != null) {
      comment("function name");
      expr.getFunctionName().visit(this, unused);
    }

    if (expr.getSelectorGroup() != null) {
      comment("selector group");
      expr.getSelectorGroup().visit(this, unused);
    }

    if (expr.getIndex() != null) {
      comment("index expr");
      expr.getIndex().visit(this, unused);
    }

    exitTag("pseudo-function");
    return null;
  }

  @Override
  public Void selectorPseudoClass(PseudoClassExpr expr, Void unused) {
    enterTag("pseudo-class", expr);

    expr.getPseudoClass().visit(this, unused);

    exitTag("pseudo-class");
    return null;
  }

  @Override
  public Void selectorAttribute(AttributeExpr expr, Void unused) {
    enterTag("attribute", expr, Map.of("operation", expr.getOperation()));

    expr.getAttributeName().visit(this, unused);

    if (expr.getValue() != null) {
      comment("value");
      expr.getValue().visit(this, unused);
    }

    exitTag("attribute");
    return null;
  }

  @Override
  public Void selectorId(IdExpr expr, Void unused) {
    enterTag("id-matcher", expr);
    expr.getId().visit(this, unused);
    exitTag("id-matcher");
    return null;
  }

  @Override
  public Void selectorClassName(ClassNameExpr expr, Void unused) {
    enterTag("class-name", expr);
    expr.getClassName().visit(this, unused);
    exitTag("class-name");
    return null;
  }

  @Override
  public Void selectorTagName(TagNameExpr expr, Void unused) {
    enterTag("tag-name", expr);
    expr.getTagName().visit(this, unused);
    exitTag("tag-name");
    return null;
  }

  @Override
  public Void selectorNode(SelectorNodeStatement node, Void unused) {
    enterTag("selector-node", node, Map.of("combinator", node.getCombinator()));

    for (SelectorExpression expression : node.getExpressions()) {
      expression.visit(this, unused);
    }

    exitTag("selector-node");
    return null;
  }

  @Override
  public Void rectangle(RectExpr expr, Void unused) {
    enterTag("rectangle", expr);

    if (expr.getTop() != null) {
      comment("top");
      expr.getTop().visit(this, unused);
    }
    if (expr.getRight() != null) {
      comment("right");
      expr.getRight().visit(this, unused);
    }
    if (expr.getBottom() != null) {
      comment("bottom");
      expr.getBottom().visit(this, unused);
    }
    if (expr.getLeft() != null) {
      comment("left");
      expr.getLeft().visit(this, unused);
    }

    exitTag("rectangle");
    return null;
  }

  @Override
  public Void important(ImportantMarker marker, Void unused) {
    voidTag("important", marker);
    return null;
  }

  @Override
  public Void unary(UnaryExpr expr, Void unused) {
    enterTag("unary", expr, Map.of("operation", expr.getOp()));

    if (expr.getValue() != null) {
      expr.getValue().visit(this, unused);
    }

    exitTag("unary");
    return null;
  }

  @Override
  public Void namespaced(NamespaceExpr expr, Void unused) {
    enterTag("namespaced", expr);

    if (expr.getNamespace() != null) {
      identifier(expr.getNamespace(), unused);
    }

    if (expr.getTarget() != null) {
      expr.getTarget().visit(this, unused);
    }

    exitTag("namespaced");
    return null;
  }

  @Override
  public Void binary(BinaryExpr expr, Void unused) {
    enterTag("binary", expr, Map.of("operation", expr.getOp()));

    if (expr.getLhs() != null) {
      expr.getLhs().visit(this, unused);
    }

    if (expr.getRhs() != null) {
      expr.getRhs().visit(this, unused);
    }

    exitTag("binary");
    return null;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}

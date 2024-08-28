package net.arcadiusmc.chimera;

import java.util.List;
import net.arcadiusmc.chimera.ast.CallExpr;
import net.arcadiusmc.chimera.ast.ColorLiteral;
import net.arcadiusmc.chimera.ast.ErroneousExpr;
import net.arcadiusmc.chimera.ast.Expression;
import net.arcadiusmc.chimera.ast.Identifier;
import net.arcadiusmc.chimera.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.ast.KeywordLiteral;
import net.arcadiusmc.chimera.ast.Node;
import net.arcadiusmc.chimera.ast.NodeVisitor;
import net.arcadiusmc.chimera.ast.NumberLiteral;
import net.arcadiusmc.chimera.ast.NumberUnitLiteral;
import net.arcadiusmc.chimera.ast.PropertyStatement;
import net.arcadiusmc.chimera.ast.RuleStatement;
import net.arcadiusmc.chimera.ast.SelectorExpression;
import net.arcadiusmc.chimera.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.ast.SelectorExpression.TagNameExpr;
import net.arcadiusmc.chimera.ast.SelectorGroupStatement;
import net.arcadiusmc.chimera.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.ast.SelectorStatement;
import net.arcadiusmc.chimera.ast.SheetStatement;
import net.arcadiusmc.chimera.ast.StringLiteral;
import net.arcadiusmc.chimera.ast.VariableDecl;
import net.arcadiusmc.chimera.ast.VariableExpr;

public class XmlPrintVisitor implements NodeVisitor<Void, Void> {

  private final StringBuilder builder = new StringBuilder();
  private int indent = 0;

  private StringBuilder nlIndent() {
    return builder.append("\n").append("  ".repeat(indent));
  }

  private StringBuilder comment(String comment) {
    return nlIndent().append("<!-- ").append(comment).append(" -->");
  }

  private StringBuilder enterTag(String tag, Node node) {
    startTag(tag, node).append('>');
    indent++;
    return builder;
  }

  private StringBuilder exitTag(String tag) {
    indent--;
    return endTag(tag);
  }

  private StringBuilder voidTag(String tag, Node node) {
    return startTag(tag, node).append(" />");
  }

  private StringBuilder startTag(String tag, Node node) {
    nlIndent().append("<").append(tag).append(' ');

    if (node.getStart() != null) {
      builder
          .append("start=")
          .append('"')
          .append(node.getStart())
          .append('"');
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
    enterTag("string-literal", expr).append(expr.getValue()).append("</string-literal>");
    indent--;
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
        property.visit(this, unused);
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

    exitTag("property");
    return null;
  }

  @Override
  public Void numberUnitLiteral(NumberUnitLiteral expr, Void unused) {
    startTag("number-unit", expr)
        .append(" unit=")
        .append('"')
        .append(expr.getUnit().name().toLowerCase())
        .append('"')
        .append(">");
    indent++;

    expr.getNumber().visit(this, unused);

    exitTag("number-unit");
    return null;
  }

  @Override
  public Void numberLiteral(NumberLiteral expr, Void unused) {
    startTag("number", expr)
        .append(" value=")
        .append('"')
        .append(expr.getValue())
        .append('"')
        .append(" />");
    return null;
  }

  @Override
  public Void keywordLiteral(KeywordLiteral expr, Void unused) {
    startTag("keyword", expr)
        .append(" keyword=").append('"')
        .append(expr.getKeyword().name().toLowerCase())
        .append('"')
        .append(" />");

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
    startTag("identifier", expr)
        .append(" value=")
        .append('"')
        .append(expr.getValue())
        .append('"')
        .append(" />");

    return null;
  }

  @Override
  public Void error(ErroneousExpr expr, Void unused) {
    startTag("error", expr).append("/>");
    return null;
  }

  @Override
  public Void colorLiteral(ColorLiteral expr, Void unused) {
    startTag("color", expr)
        .append(" value=")
        .append('"')
        .append(expr.getColor().toString())
        .append('"')
        .append(" />");

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
  public Void selector(SelectorStatement selector, Void unused) {
    enterTag("selector", selector);

    for (SelectorNodeStatement node : selector.getNodes()) {
      node.visit(this, unused);
    }

    exitTag("selector");
    return null;
  }

  @Override
  public Void selectorGroup(SelectorGroupStatement group, Void unused) {
    enterTag("selector-group", group);

    for (SelectorStatement selector : group.getSelectors()) {
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
    startTag("even-odd", expr)
        .append(" value=")
        .append('"')
        .append(expr.getEvenOdd().name().toLowerCase())
        .append('"')
        .append(" />");

    return null;
  }

  @Override
  public Void selectorPseudoFunction(PseudoFunctionExpr expr, Void unused) {
    startTag("pseudo-function", expr);

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
    startTag("attribute", expr)
        .append(" operation=")
        .append('"')
        .append(expr.getOperation().name().toLowerCase())
        .append('"')
        .append(" >");

    indent++;

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
    startTag("selector-node", node)
        .append(" combinator=")
        .append('"')
        .append(node.getCombinator().name().toLowerCase())
        .append('"')
        .append(">");
    indent++;

    for (SelectorExpression expression : node.getExpressions()) {
      expression.visit(this, unused);
    }

    exitTag("selector-node");
    return null;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}

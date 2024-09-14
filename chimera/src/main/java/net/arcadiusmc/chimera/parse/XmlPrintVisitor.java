package net.arcadiusmc.chimera.parse;

import java.util.List;
import java.util.Map;
import net.arcadiusmc.chimera.parse.ast.BinaryExpr;
import net.arcadiusmc.chimera.parse.ast.Block;
import net.arcadiusmc.chimera.parse.ast.CallExpr;
import net.arcadiusmc.chimera.parse.ast.ColorLiteral;
import net.arcadiusmc.chimera.parse.ast.ControlFlowStatement;
import net.arcadiusmc.chimera.parse.ast.ErroneousExpr;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.Identifier;
import net.arcadiusmc.chimera.parse.ast.IfStatement;
import net.arcadiusmc.chimera.parse.ast.ImportStatement;
import net.arcadiusmc.chimera.parse.ast.ImportantMarker;
import net.arcadiusmc.chimera.parse.ast.InlineStyleStatement;
import net.arcadiusmc.chimera.parse.ast.KeywordLiteral;
import net.arcadiusmc.chimera.parse.ast.ListLiteral;
import net.arcadiusmc.chimera.parse.ast.LogStatement;
import net.arcadiusmc.chimera.parse.ast.NamespaceExpr;
import net.arcadiusmc.chimera.parse.ast.Node;
import net.arcadiusmc.chimera.parse.ast.NodeVisitor;
import net.arcadiusmc.chimera.parse.ast.NumberLiteral;
import net.arcadiusmc.chimera.parse.ast.PropertyStatement;
import net.arcadiusmc.chimera.parse.ast.RegularSelectorStatement;
import net.arcadiusmc.chimera.parse.ast.RuleStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.NestedSelector;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.TagNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorListStatement;
import net.arcadiusmc.chimera.parse.ast.SelectorNodeStatement;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.chimera.parse.ast.Statement;
import net.arcadiusmc.chimera.parse.ast.StringLiteral;
import net.arcadiusmc.chimera.parse.ast.UnaryExpr;
import net.arcadiusmc.chimera.parse.ast.VariableDecl;
import net.arcadiusmc.chimera.parse.ast.VariableExpr;

public class XmlPrintVisitor implements NodeVisitor<Void> {

  private final StringBuilder builder = new StringBuilder();
  private int indent = 0;

  public boolean noComments = false;

  private StringBuilder nlIndent() {
    if (builder.isEmpty()) {
      return builder;
    }

    return builder.append("\n").append("  ".repeat(indent));
  }

  private StringBuilder comment(String comment) {
    if (noComments) {
      return builder;
    }

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
  public Void variableExpr(VariableExpr expr) {
    enterTag("variable-expr", expr);
    expr.getVariableName().visit(this);
    exitTag("variable-expr");
    return null;
  }

  @Override
  public Void variableDecl(VariableDecl decl) {
    enterTag("variable-decl", decl);

    if (decl.getName() != null) {
      comment("variable name");
      decl.getName().visit(this);
    }
    if (decl.getValue() != null) {
      comment("value");
      decl.getValue().visit(this);
    }

    exitTag("variable-decl");
    return null;
  }

  @Override
  public Void stringLiteral(StringLiteral expr) {
    voidTag("string-literal", expr, Map.of("value", expr.getValue()));
    return null;
  }

  @Override
  public Void sheet(SheetStatement sheet) {
    enterTag("stylesheet", sheet);

    for (Statement statement : sheet.getStatements()) {
      statement.visit(this);
    }

    exitTag("stylesheet");
    return null;
  }

  @Override
  public Void rule(RuleStatement rule) {
    enterTag("rule", rule);

    if (rule.getSelector() != null) {
      rule.getSelector().visit(this);
    }

    rule.getBody().visit(this);

    exitTag("rule");
    return null;
  }

  @Override
  public Void property(PropertyStatement prop) {
    enterTag("property", prop);

    comment("property name");
    prop.getPropertyName().visit(this);

    if (prop.getValue() != null) {
      comment("property value");
      prop.getValue().visit(this);
    }

    if (prop.getImportant() != null) {
      comment("important");
      important(prop.getImportant());
    }

    exitTag("property");
    return null;
  }

  @Override
  public Void numberLiteral(NumberLiteral expr) {
    voidTag("number", expr, Map.of("value", expr.getValue(), "unit", expr.getUnit()));
    return null;
  }

  @Override
  public Void keywordLiteral(KeywordLiteral expr) {
    voidTag("keyword", expr, Map.of("keyword", expr.getKeyword()));
    return null;
  }

  @Override
  public Void inlineStyle(InlineStyleStatement inline) {
    enterTag("inline-style", inline);

    List<PropertyStatement> properties = inline.getProperties();
    for (PropertyStatement property : properties) {
      property.visit(this);
    }

    exitTag("inline-style");
    return null;
  }

  @Override
  public Void identifier(Identifier expr) {
    voidTag("identifier", expr, Map.of("value", expr.getValue()));
    return null;
  }

  @Override
  public Void error(ErroneousExpr expr) {
    voidTag("error", expr, Map.of("error-token", expr.getToken().info()));
    return null;
  }

  @Override
  public Void colorLiteral(ColorLiteral expr) {
    voidTag("color", expr, Map.of("value", expr.getColor()));
    return null;
  }

  @Override
  public Void callExpr(CallExpr expr) {
    enterTag("call", expr);
    expr.getFunctionName().visit(this);

    if (!expr.getArguments().isEmpty()) {
      comment("function arguments");

      for (Expression argument : expr.getArguments()) {
        argument.visit(this);
      }
    }

    exitTag("call");
    return null;
  }

  @Override
  public Void selector(RegularSelectorStatement selector) {
    enterTag("selector", selector);

    for (SelectorNodeStatement node : selector.getNodes()) {
      node.visit(this);
    }

    exitTag("selector");
    return null;
  }

  @Override
  public Void selectorGroup(SelectorListStatement group) {
    enterTag("selector-group", group);

    for (RegularSelectorStatement selector : group.getSelectors()) {
      selector(selector);
    }

    exitTag("selector-group");
    return null;
  }

  @Override
  public Void selectorMatchAll(MatchAllExpr expr) {
    voidTag("select-all", expr);
    return null;
  }

  @Override
  public Void anb(AnbExpr expr) {
    enterTag("anb", expr);

    if (expr.getA() != null) {
      comment("A");
      expr.getA().visit(this);
    }

    if (expr.getB() != null) {
      comment("B");
      expr.getB().visit(this);
    }

    exitTag("anb");
    return null;
  }

  @Override
  public Void evenOdd(EvenOddKeyword expr) {
    voidTag("even-odd", expr, Map.of("value", expr.getEvenOdd()));

    return null;
  }

  @Override
  public Void selectorPseudoFunction(PseudoFunctionExpr expr) {
    enterTag("pseudo-function", expr);

    if (expr.getFunctionName() != null) {
      comment("function name");
      expr.getFunctionName().visit(this);
    }

    if (expr.getSelectorGroup() != null) {
      comment("selector group");
      expr.getSelectorGroup().visit(this);
    }

    if (expr.getIndex() != null) {
      comment("index expr");
      expr.getIndex().visit(this);
    }

    exitTag("pseudo-function");
    return null;
  }

  @Override
  public Void selectorPseudoClass(PseudoClassExpr expr) {
    enterTag("pseudo-class", expr);

    expr.getPseudoClass().visit(this);

    exitTag("pseudo-class");
    return null;
  }

  @Override
  public Void selectorAttribute(AttributeExpr expr) {
    enterTag("attribute", expr, Map.of("operation", expr.getOperation()));

    expr.getAttributeName().visit(this);

    if (expr.getValue() != null) {
      comment("value");
      expr.getValue().visit(this);
    }

    exitTag("attribute");
    return null;
  }

  @Override
  public Void selectorId(IdExpr expr) {
    enterTag("id-matcher", expr);
    expr.getId().visit(this);
    exitTag("id-matcher");
    return null;
  }

  @Override
  public Void selectorClassName(ClassNameExpr expr) {
    enterTag("class-name", expr);
    expr.getClassName().visit(this);
    exitTag("class-name");
    return null;
  }

  @Override
  public Void selectorTagName(TagNameExpr expr) {
    enterTag("tag-name", expr);
    expr.getTagName().visit(this);
    exitTag("tag-name");
    return null;
  }

  @Override
  public Void selectorNode(SelectorNodeStatement node) {
    enterTag("selector-node", node, Map.of("combinator", node.getCombinator()));

    for (SelectorExpression expression : node.getExpressions()) {
      expression.visit(this);
    }

    exitTag("selector-node");
    return null;
  }

  @Override
  public Void selectorNested(NestedSelector selector) {
    enterTag("selector-nested", selector);
    selector.getSelector().visit(this);
    exitTag("selector-nested");
    return null;
  }

  @Override
  public Void listLiteral(ListLiteral expr) {
    enterTag("list-literal", expr);

    for (Expression value : expr.getValues()) {
      value.visit(this);
    }

    exitTag("list-literal");
    return null;
  }

  @Override
  public Void important(ImportantMarker marker) {
    voidTag("important", marker);
    return null;
  }

  @Override
  public Void unary(UnaryExpr expr) {
    enterTag("unary", expr, Map.of("operation", expr.getOp()));

    if (expr.getValue() != null) {
      expr.getValue().visit(this);
    }

    exitTag("unary");
    return null;
  }

  @Override
  public Void namespaced(NamespaceExpr expr) {
    enterTag("namespaced", expr);

    if (expr.getNamespace() != null) {
      identifier(expr.getNamespace());
    }

    if (expr.getTarget() != null) {
      expr.getTarget().visit(this);
    }

    exitTag("namespaced");
    return null;
  }

  @Override
  public Void binary(BinaryExpr expr) {
    enterTag("binary", expr, Map.of("operation", expr.getOp()));

    if (expr.getLhs() != null) {
      expr.getLhs().visit(this);
    }

    if (expr.getRhs() != null) {
      expr.getRhs().visit(this);
    }

    exitTag("binary");
    return null;
  }

  @Override
  public Void returnStatement(ControlFlowStatement stat) {
    if (stat.getReturnValue() == null) {
      voidTag("return", stat);
      return null;
    }

    enterTag("return", stat);
    stat.getReturnValue().visit(this);
    exitTag("return");

    return null;
  }

  @Override
  public Void logStatement(LogStatement statement) {
    if (statement.getExpression() == null) {
      voidTag("log", statement, Map.of("level", statement.getLevel(), "name", statement.getName()));
      return null;
    }

    enterTag("log", statement, Map.of("level", statement.getLevel(), "name", statement.getName()));
    statement.getExpression().visit(this);
    exitTag("log");

    return null;
  }

  @Override
  public Void importStatement(ImportStatement statement) {
    voidTag("import", statement, Map.of("path", statement.getImportPath()));
    return null;
  }

  @Override
  public Void ifStatement(IfStatement statement) {
    enterTag("if", statement);

    statement.getCondition().visit(this);
    statement.getBody().visit(this);

    if (statement.getElseBody() != null) {
      statement.getElseBody().visit(this);
    }

    exitTag("if");
    return null;
  }

  @Override
  public Void blockStatement(Block block) {
    enterTag("block", block);
    for (Statement statement : block.getStatements()) {
      statement.visit(this);
    }
    exitTag("block");
    return null;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}

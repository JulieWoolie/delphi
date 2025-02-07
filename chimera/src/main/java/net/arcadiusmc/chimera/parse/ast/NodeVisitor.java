package net.arcadiusmc.chimera.parse.ast;

import net.arcadiusmc.chimera.parse.ast.FunctionStatement.FuncParameterStatement;
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

public interface NodeVisitor<R> {

  /* --------------------------- Expressions ---------------------------- */

  R variableExpr(VariableExpr expr);

  R stringLiteral(StringLiteral expr);

  R numberLiteral(NumberLiteral expr);

  R keywordLiteral(KeywordLiteral expr);

  R inlineStyle(InlineStyleStatement inline);

  R identifier(Identifier expr);

  R error(ErroneousExpr expr);

  R colorLiteral(ColorLiteral expr);

  R callExpr(CallExpr expr);

  R listLiteral(ListLiteral expr);

  R important(ImportantMarker marker);

  R unary(UnaryExpr expr);

  R namespaced(NamespaceExpr expr);

  R binary(BinaryExpr expr);

  /* --------------------------- Selectors ---------------------------- */

  R selector(RegularSelectorStatement selector);

  R selectorGroup(SelectorListStatement group);

  R selectorMatchAll(MatchAllExpr expr);

  R anb(AnbExpr expr);

  R evenOdd(EvenOddKeyword expr);

  R selectorPseudoFunction(PseudoFunctionExpr expr);

  R selectorPseudoClass(PseudoClassExpr expr);

  R selectorAttribute(AttributeExpr expr);

  R selectorId(IdExpr expr);

  R selectorClassName(ClassNameExpr expr);

  R selectorTagName(TagNameExpr expr);

  R selectorNode(SelectorNodeStatement node);

  R selectorNested(NestedSelector selector);

  /* --------------------------- Statements ---------------------------- */

  R variableDecl(VariableDecl decl);

  R sheet(SheetStatement sheet);

  R rule(RuleStatement rule);

  R property(PropertyStatement prop);

  R returnStatement(ControlFlowStatement stat);

  R logStatement(LogStatement statement);

  R importStatement(ImportStatement statement);

  R ifStatement(IfStatement statement);

  R blockStatement(Block block);

  R function(FunctionStatement statement);

  R functionParameter(FuncParameterStatement parameter);

  R assertStatement(AssertStatement statement);

  R exprStatement(ExpressionStatement statement);

  R mixin(MixinStatement statement);

  R include(IncludeStatement statement);

}

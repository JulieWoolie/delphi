package net.arcadiusmc.chimera.parse.ast;

import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AnbExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.AttributeExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.ClassNameExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.EvenOddKeyword;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.IdExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.MatchAllExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoClassExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.PseudoFunctionExpr;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression.TagNameExpr;

public interface NodeVisitor<R, C> {

  R variableExpr(VariableExpr expr, C c);

  R variableDecl(VariableDecl decl, C c);

  R stringLiteral(StringLiteral expr, C c);

  R sheet(SheetStatement sheet, C c);

  R rule(RuleStatement rule, C c);

  R property(PropertyStatement prop, C c);

  R numberLiteral(NumberLiteral expr, C c);

  R keywordLiteral(KeywordLiteral expr, C c);

  R inlineStyle(InlineStyleStatement inline, C c);

  R identifier(Identifier expr, C c);

  R error(ErroneousExpr expr, C c);

  R colorLiteral(ColorLiteral expr, C c);

  R callExpr(CallExpr expr, C c);

  R selector(RegularSelectorStatement selector, C c);

  R selectorGroup(SelectorListStatement group, C c);

  R selectorMatchAll(MatchAllExpr expr, C c);

  R anb(AnbExpr expr, C c);

  R evenOdd(EvenOddKeyword expr, C c);

  R selectorPseudoFunction(PseudoFunctionExpr expr, C c);

  R selectorPseudoClass(PseudoClassExpr expr, C c);

  R selectorAttribute(AttributeExpr expr, C c);

  R selectorId(IdExpr expr, C c);

  R selectorClassName(ClassNameExpr expr, C c);

  R selectorTagName(TagNameExpr expr, C c);

  R selectorNode(SelectorNodeStatement node, C c);

  R rectangle(RectExpr expr, C c);

  R important(ImportantMarker marker, C c);

  R unary(UnaryExpr expr, C c);

  R namespaced(NamespaceExpr expr, C c);

  R binary(BinaryExpr expr, C c);
}

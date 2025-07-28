package com.juliewoolie.chimera.system;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.InlineStyle;
import com.juliewoolie.chimera.Property;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.PropertySet.PropertyIterator;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.StyleLoggers;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.chimera.Value.ValueType;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraContext;
import com.juliewoolie.chimera.parse.ChimeraParser;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.dom.Element;

@Getter
public class ElementStyleNode extends StyleNode {

  private final List<StyleNode> children = new ArrayList<>();
  private final PropertySet inlineStyleSet = new PropertySet();

  private final InlineStyle inlineApi;

  @Setter
  private boolean suppressingInlineUpdates = false;

  public ElementStyleNode(Element domNode, StyleObjectModel system) {
    super(domNode, system);

    this.inlineApi = new InlineStyle(inlineStyleSet, this);
  }

  @Override
  public void updateStyle() {
    PropertySet newSet = new PropertySet();
    applyCascading(newSet);

    Element domElement = getDomNode();

    for (Rule rule : system.rules) {
      if (!rule.getSelectorObject().test(domElement)) {
        continue;
      }

      resolveSetTo(rule.getPropertySet(), newSet);
    }

    resolveSetTo(inlineStyleSet, newSet);

    int changes = styleSet.setAll(newSet);

    if (changes != 0) {
      computedSet.putAll(styleSet);
      triggerCallback(changes);
    }

    for (StyleNode child : children) {
      child.updateStyle();
    }
  }

  <T> void trySet(Property<T> property, Value<T> value, PropertySet target) {
    if (!target.has(property)) {
      target.setValue(property, value);
      return;
    }

    Value<T> existing = target.get(property);
    if (!existing.isImportant()) {
      target.setValue(property, value);
      return;
    }

    if (!value.isImportant()) {
      return;
    }

    target.setValue(property, value);
  }

  protected void resolveSetTo(PropertySet source, PropertySet target) {
    PropertyIterator it = source.iterator();

    while (it.hasNext()) {
      it.next();

      Property<Object> property = it.property();
      Value<Object> value = resolveValue(property, it.value());

      trySet(property, value, target);
    }
  }

  protected <T> Value<T> resolveValue(Property<T> property, Value<T> value) {
    if (value == null) {
      return null;
    }

    ValueType type = value.getType();
    if (type == ValueType.EXPLICIT || type == ValueType.AUTO) {
      return value;
    }

    if (type == ValueType.INHERIT) {
      if (parent == null) {
        return null;
      }

      return parent.getStyleSet().get(property);
    }
    if (type == ValueType.UNSET) {
      return Value.create(property.getDefaultValue());
    }

    // type = INITIAL
    if (system.defaultStyleSheet == null) {
      return null;
    }

    Element el = getDomNode();

    for (int i = 0; i < system.defaultStyleSheet.getLength(); i++) {
      Rule r = system.defaultStyleSheet.getRule(i);
      if (!r.getSelectorObject().test(el)) {
        continue;
      }

      Value<T> val = r.getPropertySet().orNull(property);
      if (val == null) {
        continue;
      }

      return val;
    }

    return null;
  }

  @Override
  public Element getDomNode() {
    return (Element) super.getDomNode();
  }

  public void setInline(String inline) {
    if (suppressingInlineUpdates) {
      return;
    }
    if (Strings.isNullOrEmpty(inline)) {
      inlineStyleSet.clear();
      return;
    }

    ChimeraParser parser = new ChimeraParser(inline);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName(getDomNode().toString());
    errors.setListener(error -> {
      StyleLoggers.getLogger()
          .atLevel(error.getLevel())
          .setMessage(error.getFormattedError())
          .log();
    });

    InlineStyleStatement statement = parser.inlineStyle();

    ChimeraContext context = new ChimeraContext(parser.getStream().getInput());
    context.setErrors(errors);

    Scope scope = Scope.createTopLevel();
    scope.setVariableMap(getSystem().getVariables());

    Chimera.compileInline(statement, inlineStyleSet, context);
  }

  public void addChild(StyleNode node, int idx) {
    if (node.parent == this) {
      return;
    }
    if (node.parent != null) {
      throw new IllegalStateException("Style node already has parent");
    }

    children.add(idx, node);
    node.parent = this;
  }

  public void removeChild(int idx) {
    StyleNode removed = children.remove(idx);
    removed.parent = null;
  }

  public void removeChild(StyleNode remove) {
    if (!children.remove(remove)) {
      return;
    }

    remove.parent = null;
  }
}

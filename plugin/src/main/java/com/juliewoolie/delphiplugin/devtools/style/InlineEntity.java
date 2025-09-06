package com.juliewoolie.delphiplugin.devtools.style;

import static com.juliewoolie.delphiplugin.TextUtil.translateToString;

import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.system.ElementStyleNode;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public record InlineEntity(DelphiElement target) implements StyleEntity {

  @Override
  public boolean isModifiable() {
    return true;
  }

  @Override
  public @Nullable String getSelector() {
    return null;
  }

  @Override
  public String getSource(Locale l) {
    return translateToString(l, "delphi.devtools.styles.inlineName");
  }

  @Override
  public StylePropertiesReadonly getProperties() {
    return target.getInlineStyle();
  }

  @Override
  public PropertySet getPropertySet() {
    ElementStyleNode styleNode = (ElementStyleNode) target.getDocument()
        .getStyles()
        .getStyleNode(target);

    return styleNode.getInlineApi().getSet();
  }

  @Override
  public void postUpdateCallback() {
    ElementStyleNode styleNode = (ElementStyleNode) target.getDocument()
        .getStyles()
        .getStyleNode(target);
    styleNode.getInlineApi().triggerChange();
  }

  @Override
  public @Nullable String getSpec() {
    return null;
  }
}

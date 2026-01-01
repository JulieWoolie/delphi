package com.juliewoolie.delphidom;

import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.ChoiceElement;
import com.juliewoolie.dom.SelectElement;
import com.juliewoolie.dom.TagNames;
import org.jetbrains.annotations.Nullable;

public class DelphiChoiceElement extends DisableableElement implements ChoiceElement {

  public DelphiChoiceElement(DelphiDocument document) {
    super(document, TagNames.CHOICE);
  }

  @Override
  public SelectElement getSelectElement() {
    if (parent instanceof SelectElement select) {
      return select;
    }
    return null;
  }

  @Override
  public @Nullable String getValue() {
    return getAttribute(Attributes.VALUE);
  }

  @Override
  public void setValue(@Nullable String value) {
    setAttribute(Attributes.VALUE, value);
  }

  @Override
  public boolean isSelected() {
    return hasAttribute(Attributes.SELECTED);
  }

  @Override
  public void setSelected(boolean selected) {
    if (selected) {
      setAttribute(Attributes.SELECTED, Attributes.SELECTED);
      return;
    }

    removeAttribute(Attributes.SELECTED);
  }
}

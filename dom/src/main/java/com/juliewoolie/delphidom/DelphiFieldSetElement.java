package com.juliewoolie.delphidom;

import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.FieldSetElement;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.TagNames;
import java.util.ArrayList;
import java.util.List;

public class DelphiFieldSetElement extends DelphiElement implements FieldSetElement {

  public DelphiFieldSetElement(DelphiDocument document) {
    super(document, TagNames.FIELDSET);
  }

  @Override
  public List<Element> getFieldSetElements() {
    List<Element> elements = new ArrayList<>();
    recursivelySearch(elements, this);
    return elements;
  }

  private void recursivelySearch(List<Element> out, DelphiElement el) {
    for (int i = 0; i < el.getChildCount(); i++) {
      Node child = el.getChild(i);
      if (!(child instanceof DelphiElement element)) {
        continue;
      }
      if (element instanceof FieldSetElement) {
        continue;
      }

      if (isInputElement(element)) {
        out.addLast(element);
      }

      recursivelySearch(out, element);
    }
  }

  public static boolean isInputElement(Element element) {
    return element instanceof InputElement;
  }

  public static FieldSetElement getFieldSetParent(Element element) {
    Element p = element.getParent();

    while (p != null) {
      if (p instanceof FieldSetElement fs) {
        return fs;
      }
      p = p.getParent();
    }

    return null;
  }
}

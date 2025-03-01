package net.arcadiusmc.chimera.selector;

import com.google.common.base.Strings;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.InputElement;

public record PseudoElementSelector(PseudoElement element) implements Selector {

  @Override
  public boolean test(Element element) {
    switch (this.element) {
      case PLACEHOLDER -> {
        if (!(element instanceof InputElement input)) {
          return false;
        }

        String val = input.getValue();
        return Strings.isNullOrEmpty(val);
      }
    }

    return false;
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append("::");

    switch (element) {
      case PLACEHOLDER -> builder.append("placeholder");
    }
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.typeColumn++;
  }
}

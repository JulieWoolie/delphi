package net.arcadiusmc.chimera.selector;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import net.arcadiusmc.chimera.StringUtil;
import net.arcadiusmc.dom.Element;

public record AttributeSelector(String attributeKey, AttributeOperation op, String value)
    implements Selector
{

  @Override
  public boolean test(Element root, Element element) {
    String attrValue = element.getAttribute(attributeKey);

    if (Strings.isNullOrEmpty(attrValue)) {
      return false;
    }

    return switch (op) {
      case HAS -> true;
      case EQUALS -> Objects.equal(value, attrValue);
      case ENDS_WITH -> attrValue.endsWith(value);
      case STARTS_WITH -> attrValue.startsWith(value);
      case DASH_PREFIXED -> attrValue.startsWith(value) || attrValue.startsWith(value + "-");
      case CONTAINS_SUBSTRING -> attrValue.contains(value);
      case CONTAINS_WORD -> StringUtil.containsWord(attrValue, value);
    };
  }

  @Override
  public void append(StringBuilder builder) {
    builder.append("[");
    builder.append(attributeKey);

    switch (op) {
      case DASH_PREFIXED -> builder.append("|=");
      case CONTAINS_WORD -> builder.append("~=");
      case STARTS_WITH -> builder.append("^=");
      case ENDS_WITH -> builder.append("$=");
      case EQUALS -> builder.append("=");
      case CONTAINS_SUBSTRING -> builder.append("*=");
      default -> {}
    }

    if (!Strings.isNullOrEmpty(value)) {
      builder.append('"').append(value).append('"');
    }

    builder.append(']');
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn++;
  }

  @Override
  public String getCssString() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    append(builder);
    return builder.toString();
  }
}

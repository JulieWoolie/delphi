package net.arcadiusmc.delphi.dom.selector;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.util.List;
import net.arcadiusmc.delphi.StringUtil;
import net.arcadiusmc.delphi.dom.DelphiElement;

public class AttributedNode implements FilteringFunction {

  private final List<AttributeTest> attributeTests;

  public AttributedNode(List<AttributeTest> attributeTests) {
    this.attributeTests = attributeTests;
  }

  @Override
  public void appendSpec(Spec spec) {
    spec.classColumn += attributeTests.size();
  }

  @Override
  public boolean test(DelphiElement element) {
    for (AttributeTest attributeTest : attributeTests) {
      if (!attributeTest.test(element)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void append(StringBuilder builder) {
    for (AttributeTest attributeTest : attributeTests) {
      attributeTest.append(builder);
    }
  }

  public record AttributeTest(String attrName, Operation op, String value) {

    public void append(StringBuilder builder) {
      builder.append("[");
      builder.append(attrName);

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

    public boolean test(DelphiElement element) {
      String attrValue = element.getAttribute(attrName);

      if (Strings.isNullOrEmpty(attrValue)) {
        return op == Operation.HAS;
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
  }

  public enum Operation {
    HAS,
    EQUALS,
    ENDS_WITH,
    STARTS_WITH,
    DASH_PREFIXED,
    CONTAINS_SUBSTRING,
    CONTAINS_WORD;
  }
}

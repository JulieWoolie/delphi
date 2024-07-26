package net.arcadiusmc.dom.style;

public interface Stylesheet {

  /**
   * Gets the amount of rules in this stylesheet
   * @return Rule count
   */
  int getLength();

  /**
   * Gets the rule at a specific index
   * @param index Rule index
   * @return Rule
   * @throws IndexOutOfBoundsException If the {@code index} is less than 0, or greater/equal to
   *                                   {@link #getLength()}.
   */
  StyleRule getRule(int index) throws IndexOutOfBoundsException;
}

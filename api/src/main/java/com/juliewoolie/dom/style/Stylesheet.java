package com.juliewoolie.dom.style;

/**
 * CSS style sheet
 */
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

  /**
   * Get the stylesheet's source.
   *
   * <table>
   *   <caption>Return values</caption>
   *   <thead>
   *     <tr>
   *       <th>Stylesheet type</th>
   *       <th>Return value</th>
   *     </tr>
   *   </thead>
   *   <tbody>
   *     <tr>
   *       <td>Inline stylesheet</td>
   *       <td>{@code inline}</td>
   *     </tr>
   *     <tr>
   *       <td>Programmatically created stylesheet</td>
   *       <td>{@code programmatic}</td>
   *     </tr>
   *     <tr>
   *       <td>Default styles sheet</td>
   *       <td>{@code default-stylesheet}</td>
   *     </tr>
   *     <tr>
   *       <td>Stylesheet loaded from a file</td>
   *       <td>(URI the stylesheet was loaded from)</td>
   *     </tr>
   *   </tbody>
   * </table>
   *
   * @return Stylesheet source
   */
  String getSource();
}

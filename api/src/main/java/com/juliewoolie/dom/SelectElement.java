package com.juliewoolie.dom;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectElement extends Element, Disableable {

  /**
   * Get a list of choices that belong to the select element
   * @return List of choices
   */
  @NotNull
  List<ChoiceElement> getChoices();

  /**
   * Get the first choice element marked as selected
   * @return First selected choice
   */
  @Nullable ChoiceElement getSelectedChoice();
}

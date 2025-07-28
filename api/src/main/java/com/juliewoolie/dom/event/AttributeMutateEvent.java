package com.juliewoolie.dom.event;

/**
 * Event fired when either an attribute on an element is modified or when a document option is
 * modified
 */
public interface AttributeMutateEvent extends Event {

  /**
   * Get the name of the attribute/option that was mutated
   * @return Mutated attribute/option key
   */
  String getKey();

  /**
   * Get the previous value of the mutated attribute
   * @return Previous value
   */
  String getPreviousValue();

  /**
   * Get the new value of the mutated attribute
   * @return New value
   */
  String getNewValue();

  /**
   * Get the mutation action
   * @return Attribute action
   */
  AttributeAction getAction();
}

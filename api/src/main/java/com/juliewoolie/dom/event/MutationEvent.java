package com.juliewoolie.dom.event;

import com.juliewoolie.dom.Node;

/**
 * Provides contextual information about modifications to the DOM tree
 */
public interface MutationEvent extends Event {

  /**
   * Gets the node being removed/appended
   * @return Affected node
   */
  Node getNode();

  /**
   * Gets the index of the node that was removed/added
   * @return Mutation index
   */
  int getMutationIndex();
}

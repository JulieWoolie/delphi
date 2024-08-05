package net.arcadiusmc.dom.event;

public enum AttributeAction {
  /** Attribute did not exist before */
  ADD,

  /** Attribute was present, but is being removed */
  REMOVE,

  /** Attribute was present before, but value is being changed */
  SET,
  ;
}

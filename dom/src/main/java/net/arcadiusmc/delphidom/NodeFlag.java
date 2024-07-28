package net.arcadiusmc.delphidom;

public enum NodeFlag {
  HOVERED,
  CLICKED,
  ADDED,
  ROOT;

  final int mask;

  NodeFlag() {
    this.mask = 1 << ordinal();
  }
}

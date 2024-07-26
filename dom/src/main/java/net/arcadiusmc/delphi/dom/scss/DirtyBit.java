package net.arcadiusmc.delphi.dom.scss;

public enum DirtyBit {
  LAYOUT,
  VISUAL,
  CONTENT;

  public final int mask;

  DirtyBit() {
    this.mask = 1 << ordinal();
  }
}

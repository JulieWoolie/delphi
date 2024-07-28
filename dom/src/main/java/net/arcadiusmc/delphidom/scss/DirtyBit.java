package net.arcadiusmc.delphidom.scss;

public enum DirtyBit {
  LAYOUT,
  VISUAL,
  CONTENT;

  public final int mask;

  DirtyBit() {
    this.mask = 1 << ordinal();
  }
}

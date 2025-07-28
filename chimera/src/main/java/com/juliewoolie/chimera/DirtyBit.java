package com.juliewoolie.chimera;

public enum DirtyBit {
  LAYOUT,
  VISUAL,
  CONTENT;

  public final int mask;

  DirtyBit() {
    this.mask = 1 << ordinal();
  }
}

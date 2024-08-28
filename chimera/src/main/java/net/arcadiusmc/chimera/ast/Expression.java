package net.arcadiusmc.chimera.ast;

import net.arcadiusmc.chimera.ChimeraContext;

public abstract class Expression extends Node{

  public abstract Object evaluate(ChimeraContext ctx);
}

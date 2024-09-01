package net.arcadiusmc.chimera.parse.ast;

import net.arcadiusmc.chimera.parse.ChimeraContext;

public abstract class Expression extends Node{

  public abstract Object evaluate(ChimeraContext ctx);
}

package net.arcadiusmc.chimera.parse;

import org.jetbrains.annotations.NotNull;

public interface CompilerErrorListener {

  void handle(@NotNull ChimeraError error);
}

package net.arcadiusmc.chimera;

import org.jetbrains.annotations.NotNull;

public interface CompilerErrorListener {

  void handle(@NotNull ChimeraError error);
}

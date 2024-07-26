package net.arcadiusmc.delphi.resource;

import java.util.Optional;
import net.arcadiusmc.dom.Document;
import org.jetbrains.annotations.NotNull;

public non-sealed interface ApiModule extends ResourceModule {

  Optional<Document> loadDocument(@NotNull PagePath path, @NotNull DocumentFactory factory);
}

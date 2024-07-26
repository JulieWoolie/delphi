package net.arcadiusmc.delphi.resource;

import java.util.Optional;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.style.Stylesheet;

public interface ViewResources {

  ResourceModule getModule();

  String getModuleName();

  Optional<Document> loadDocument(String uri);

  Optional<Stylesheet> loadStylesheet(String uri);
}

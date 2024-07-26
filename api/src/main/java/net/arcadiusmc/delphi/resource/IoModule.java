package net.arcadiusmc.delphi.resource;

import java.io.IOException;

public non-sealed interface IoModule extends ResourceModule {

  StringBuffer loadString(PagePath path) throws IOException;
}
